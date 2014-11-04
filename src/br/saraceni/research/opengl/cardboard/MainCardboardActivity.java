package br.saraceni.research.opengl.cardboard;

import javax.microedition.khronos.egl.EGLConfig;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import br.saraceni.research.R;
import br.saraceni.research.ResearchMainActivity;
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
import br.saraceni.research.utils.BitmapFileHandler;
import br.saraceni.research.utils.MatBitmapHelper;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.EyeTransform;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

public class MainCardboardActivity extends CardboardActivity implements CardboardView.StereoRenderer {

	private static final float CAMERA_Z = 0.01f;
	private static final String TAG = "MainCardboardActivity";
	
	private float[] mCamera; // set in onNewFrame
	private float[] mView; // transform.getEyeView() * mCamera
	private float[] mHeadView; // set in onNewFrame
	private float[] mModelViewProjection; // transform.getPerspective() * mModelView in onDrawEye
	private float[] mModelView; // mView * mModelImgDisplay
	private float[] mModelImgDisplay; // translation of ImgDisplay
	private float[] mEyeTransformPerspective;
	
	private Bitmap[] bitmaps;
	private ImageDisplay[] imageDisplays;
    private Skybox skybox;
	
	private TextureShaderProgram textureProgram;
	private SkyboxShaderProgram skyboxProgram;
	
	private int particleTexture;
	private int skyboxTexture;
	private int[] textures;
	
	private long globalStartTime;
	
	private ParticleShaderProgram particleProgram;
	private ParticleSystem particleSystem;
	private ParticleShooter redParticleShooter;
	private ParticleShooter blueParticleShooter;
	
	/* --------------------------- Activity Lifecycle Methods --------------------------- */
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
		
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
		
		bitmaps = retrieveBitmaps();
	}
	
	@Override
	public void onDestroy()
	{
		BitmapFileHandler.deleteAllFiles(this);
		super.onDestroy();
	}
	
    /* ---------------------- Retrieve Bitmap From Last Activity ----------------------- */
	
	private Bitmap[] retrieveBitmaps()
	{
		Bitmap[] result = new Bitmap[BitmapFileHandler.getBitmapCount()];
		for(int i = 0; i < BitmapFileHandler.getBitmapCount(); i++)
		{
			result[i] = BitmapFileHandler.readBitmap(this, i);
			result[i] = MatBitmapHelper.convertPowerOfTwo(result[i]);
		}
		return result;
	}
	
	/* ------------------------- Stereo Renderer Interface Methods ------------------------ */
	
	@Override
	public void onDrawEye(EyeTransform transform) {
		// Equivalent do OpenGL onDrawFrame
		
		// Clear the rendering surface
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		// Apply the eye transformation to the camera.
		mEyeTransformPerspective = transform.getPerspective();
        Matrix.multiplyMM(mView, 0, transform.getEyeView(), 0, mCamera, 0);
        Matrix.setIdentityM(mModelImgDisplay, 0);
        Matrix.multiplyMM(mModelView, 0, mView, 0, mModelImgDisplay, 0);
        Matrix.multiplyMM(mModelViewProjection, 0, mEyeTransformPerspective, 0, mModelView, 0);
        
        // Draw Skybox
        drawSkybox();
		
		// Draw particle shooter
		drawParticles();
		
		// Draw Bitmap
		float angle = 0f;
	    for(int i = 0; i < BitmapFileHandler.getBitmapCount(); i++)
	    {
	    	drawImageDisplay(imageDisplays[i], textures[i], angle);
	    	angle += 45f;
	    }
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
		final int bitCount = BitmapFileHandler.getBitmapCount();
		imageDisplays = new ImageDisplay[bitCount];
		for(int i = 0; i < bitCount; i++)
		{
			imageDisplays[i] = new ImageDisplay(bitmaps[i]);
		}
		
		textureProgram = new TextureShaderProgram(this);
		textures = new int[bitCount];
		for(int i = 0; i < bitCount; i++)
		{
			textures[i] = TextureHelper.loadTexture(bitmaps[i]);
		}
		
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
	
	private void drawImageDisplay(ImageDisplay imgDisp, int txtr, float angle)
	{
		textureProgram.useProgram();
		setImageDisplayPosition(angle);
		textureProgram.setUniforms(mModelViewProjection, txtr);
		imgDisp.bindData(textureProgram);
		imgDisp.draw();
		
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
	
	/* ------------------ Position Image Displays Around Scenario --------------------- */
	
	private void setImageDisplayPosition(float angle)
	{
		Matrix.setIdentityM(mModelImgDisplay, 0);
		Matrix.rotateM(mModelImgDisplay, 0, angle, 0f, 1f, 0f);
		Matrix.multiplyMM(mModelView, 0, mView, 0, mModelImgDisplay, 0);
        Matrix.multiplyMM(mModelViewProjection, 0, mEyeTransformPerspective, 0, mModelView, 0);
	}
	
    /* ------------------------ Volume Button Listener Method ------------------------- */
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            imageDisplays[0].scaleDown();
            Log.i(TAG, "Key Volume DOWN");
        }
        else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
        	imageDisplays[0].scaleUp();
        	Log.i(TAG, "Key Volume UP");
        }
        return super.onKeyDown(keyCode, event);
    }
	
	/* ----------------------------- When Back Key is Pressed -------------------------- */
	
	@Override
	public void onBackPressed()
	{
		Log.i(TAG, "onBackPressed()");
		super.onBackPressed();
		this.finish();
	}

}
