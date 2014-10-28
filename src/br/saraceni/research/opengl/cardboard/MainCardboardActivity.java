package br.saraceni.research.opengl.cardboard;

import javax.microedition.khronos.egl.EGLConfig;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import br.saraceni.research.R;
import br.saraceni.research.SelectObjectActivity;
import br.saraceni.research.opengl.objects.ImageDisplay;
import br.saraceni.research.opengl.objects.ParticleShooter;
import br.saraceni.research.opengl.objects.ParticleSystem;
import br.saraceni.research.opengl.objects.Skybox;
import br.saraceni.research.opengl.objects.Table;
import br.saraceni.research.opengl.programs.ColorShaderProgram;
import br.saraceni.research.opengl.programs.ParticleShaderProgram;
import br.saraceni.research.opengl.programs.SkyboxShaderProgram;
import br.saraceni.research.opengl.programs.TextureShaderProgram;
import br.saraceni.research.opengl.util.Geometry.Point;
import br.saraceni.research.opengl.util.Geometry.Vector;
import br.saraceni.research.opengl.util.TextureHelper;
import br.saraceni.research.utils.MatBitmapHelper;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.EyeTransform;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

public class MainCardboardActivity extends CardboardActivity implements CardboardView.StereoRenderer {

	private static final float CAMERA_Z = 0.01f;
	
	private float[] mCamera; // set in onNewFrame
	private float[] mView; // transform.getEyeView() * mCamera
	private float[] mHeadView; // set in onNewFrame
	private float[] mModelViewProjection; // transform.getPerspective() * mModelView in onDrawEye
	private float[] mModelView; // mView * mModelImgDisplay
	private float[] mModelImgDisplay; // translation of ImgDisplay
	
	private Bitmap bitmap;
	
    private ImageDisplay imageDisplay;
    private Skybox skybox;
	
	private TextureShaderProgram textureProgram;
	private SkyboxShaderProgram skyboxProgram;
	
	private int particleTexture;
	private int skyboxTexture;
	private int texture;
	
	private long globalStartTime;
	
	private ParticleShaderProgram particleProgram;
	private ParticleSystem particleSystem;
	private ParticleShooter redParticleShooter;
	private ParticleShooter blueParticleShooter;
	
	/* --------------------------- Activity Lifecycle Methods --------------------------- */
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.cardboard_activity);
		CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
		cardboardView.setRenderer(this);
		setCardboardView(cardboardView);
		
		mCamera = new float[16];
		mView = new float[16];
		mHeadView = new float[16];
		mModelViewProjection = new float[16];
		mModelView = new float[16];
		mModelImgDisplay = new float[16];
		
		bitmap = retrieveBitmap();
		bitmap = MatBitmapHelper.convertPowerOfTwo(bitmap);
	}
	
    /* ---------------------- Retrieve Bitmap From Last Activity ----------------------- */
	
	private Bitmap retrieveBitmap()
	{
		Intent intent = getIntent();
		byte[] bytes = intent.getByteArrayExtra(SelectObjectActivity.OBJECT_BITMAP_EXTRA);
		Bitmap result = BitmapFactory.decodeByteArray(
		        bytes, 0 , bytes.length);
		return result;
	}
	
	/* ------------------------- Stereo Renderer Interface Methods ------------------------ */
	
	@Override
	public void onDrawEye(EyeTransform transform) {
		// Equivalent do OpenGL onDrawFrame
		
		// Clear the rendering surface
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		// Apply the eye transformation to the camera.
        Matrix.multiplyMM(mView, 0, transform.getEyeView(), 0, mCamera, 0);
        Matrix.setIdentityM(mModelImgDisplay, 0);
        Matrix.multiplyMM(mModelView, 0, mView, 0, mModelImgDisplay, 0);
        Matrix.multiplyMM(mModelViewProjection, 0, transform.getPerspective(), 0, mModelView, 0);
        
        // Draw Skybox
        drawSkybox();
		
		// Draw Bitmap
		drawImageDisplay();
		
		// Draw particle shooter
		drawParticles();
		
	}

	@Override
	public void onFinishFrame(Viewport arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewFrame(HeadTransform headTransform) {
		
		// Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(mCamera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        headTransform.getHeadView(mHeadView, 0);
		
	}

	@Override
	public void onRendererShutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSurfaceChanged(int arg0, int arg1) {
		
	}

	@Override
	public void onSurfaceCreated(EGLConfig arg0) {
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f);
		
		imageDisplay = new ImageDisplay(bitmap);
		//table = new Table();
		
		textureProgram = new TextureShaderProgram(this);
		texture = TextureHelper.loadTexture(bitmap);
		
		skyboxProgram = new SkyboxShaderProgram(this);
		skybox = new Skybox();
		skyboxTexture = TextureHelper.loadCubeMap(this, new int[]{
				R.drawable.left, R.drawable.right, R.drawable.bottom,
				R.drawable.top, R.drawable.front, R.drawable.back
		});
		
		particleProgram = new ParticleShaderProgram(this);
		particleSystem = new ParticleSystem(10000);
		globalStartTime = System.nanoTime();
		
        final Vector particleDirection = new Vector(0f, 0.5f, 0.5f);
		
		final float angleVarianceInDegrees = 5f;
		final float speedVariance = 1f;
		
		redParticleShooter = new ParticleShooter(
				new Point(-1f, 0f, 0f),
				particleDirection,
				Color.rgb(255, 50, 5),
				angleVarianceInDegrees,
				speedVariance);
		
		blueParticleShooter = new ParticleShooter(
				new Point(1f, 0f, 0f),
				particleDirection,
				Color.rgb(5, 50, 255),
				angleVarianceInDegrees,
				speedVariance);
		
		particleTexture = TextureHelper.loadTexture(this, R.drawable.particle_texture);
		
	}
	
	private void drawSkybox()
	{
		skyboxProgram.useProgram();
		skyboxProgram.setUniforms(mModelViewProjection, skyboxTexture);
		skybox.bindData(skyboxProgram);
		skybox.draw();
	}
	
	private void drawImageDisplay()
	{
		textureProgram.useProgram();
		textureProgram.setUniforms(mModelViewProjection, texture);
		imageDisplay.bindData(textureProgram);
		imageDisplay.draw();
	}
	
	private void drawParticles()
	{
		float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
		
		redParticleShooter.addParticles(particleSystem, currentTime, 5);
		blueParticleShooter.addParticles(particleSystem, currentTime, 5);
		
		particleProgram.useProgram();
		particleProgram.setUniforms(mModelViewProjection, currentTime, particleTexture);
		particleSystem.bind(particleProgram);
		particleSystem.draw();
		
	}

}
