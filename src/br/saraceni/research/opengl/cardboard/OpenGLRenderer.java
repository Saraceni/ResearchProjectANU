package br.saraceni.research.opengl.cardboard;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import br.saraceni.research.R;
import br.saraceni.research.opengl.objects.ImageDisplay;
import br.saraceni.research.opengl.programs.ColorShaderProgram;
import br.saraceni.research.opengl.programs.TextureShaderProgram;
import br.saraceni.research.opengl.util.MatrixHelper;
import br.saraceni.research.opengl.util.TextureHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;

public class OpenGLRenderer implements Renderer {
	
	private final Context context;
	private final Bitmap bitmap;
	
	private final float[] projectionMatrix = new float[16];
	private final float[] modelMatrix = new float[16];
	
	private ImageDisplay imageDisplay;
	
	private TextureShaderProgram textureProgram;
	private ColorShaderProgram colorProgram;
	
	private int texture;
	
	/* -------------------------------- Constructor ------------------------------- */
	
	public OpenGLRenderer(Context context, Bitmap bitmap)
	{
		this.context = context;
		this.bitmap = bitmap;
	}
	
	/* --------------------- OpenGL Renderer Interface Methods --------------------- */

	@Override
	public void onDrawFrame(GL10 arg0) {
		// Clear the rendering surface
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		// Draw Bitmap
		textureProgram.useProgram();
		textureProgram.setUniforms(projectionMatrix, texture);
		imageDisplay.bindData(textureProgram);
		imageDisplay.draw();
		
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / 
				(float) height, 1f, 10f);
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
		final float[] temp = new float[16];
		Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
		System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
		
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		
		imageDisplay = new ImageDisplay(bitmap);
		
		textureProgram = new TextureShaderProgram(context);
		colorProgram = new ColorShaderProgram(context);
		texture = TextureHelper.loadTexture(bitmap);
		
	}

}
