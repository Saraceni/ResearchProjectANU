package br.saraceni.research.opengl.objects;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;
import android.view.KeyEvent;
import br.saraceni.research.opengl.data.Constants;
import br.saraceni.research.opengl.data.VertexArray;
import br.saraceni.research.opengl.programs.ColorShaderProgram;
import br.saraceni.research.opengl.programs.TextureShaderProgram;


public class ImageDisplay {
	
	private static final String TAG = "ImageDisplay";
	
	private static final int POSITION_COMPONENT_COUNT = 3;
	private static final int COLOR_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + 
			COLOR_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;
	
	
	/*private static final float[] VERTEX_DATA = 
		{
			// Order of coordinates: X, Y, S, T
			
			// Triangle Fan
			0f,		0f,		0.5f,	0.5f,
		 -0.5f,	 -0.8f,       0f,   0.9f,
		  0.5f,  -0.8f,       1f,   0.9f,
		  0.5f,   0.8f,       1f,   0.1f, 
		 -0.5f,   0.8f,       0f,   0.1f,
		 -0.5f,  -0.8f,       0f,   0.9f
		 
		};*/
	
	/*private static final float[] VERTEX_DATA = 
		{
			// Order of coordinates: X, Y, S, T
			
			// Triangle Fan
			0f,		0f,		1.0f,	  0f,    0f,
		 -0.5f,	 -0.8f,     1.0f,     0f,    0f,
		  0.5f,  -0.8f,     1.0f,     0f,    0f,
		  0.5f,   0.8f,     1.0f,     0f,    0f, 
		 -0.5f,   0.8f,     1.0f,     0f,    0f,
		 -0.5f,  -0.8f,     1.0f,     0f,    0f
		 
		};*/
		
	
	private float[] VERTEX_DATA = new float[30];
	
    private float bitmapLeft;
	private float bitmapRight;
	private float bitmapBottom;
	private float bitmapTop;
	private float bitmapZ;
	
	private final Bitmap bitmap;
	
	private VertexArray vertexArray;
	
	private final float scale = 1.03f;
	
	
	/* ------------------------------- Class Constructor ------------------------------ */
	
	public ImageDisplay(Bitmap bitmap)
	{
		this.bitmap = bitmap;
		final float aspectRatio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
		if(aspectRatio > 1)
		{
			bitmapRight = 0.5f;
			bitmapLeft = -0.5f;
			bitmapTop = 0.5f / aspectRatio;
			bitmapBottom = -0.5f / aspectRatio;
		}
		else
		{
			bitmapTop = 0.5f;
			bitmapBottom = -0.5f;
			bitmapRight = 0.5f * aspectRatio;
			bitmapLeft = -0.5f * aspectRatio;
		}
		
		bitmapZ = -1.5f;
		setVertexArray();
	}
	
	/* ---------------------- Bind Data to Texture Program ----------------------- */
	
	public void bindData(TextureShaderProgram textureProgram)
	{
		vertexArray.setVertexAttribPointer(
				0,
				textureProgram.getPositionAttributeLocation(), 
				POSITION_COMPONENT_COUNT, 
				STRIDE);
		
		vertexArray.setVertexAttribPointer(
				POSITION_COMPONENT_COUNT, 
				textureProgram.getTextureCoordinatesLocation(), 
				COLOR_COMPONENT_COUNT, 
				STRIDE);
	}
	
	/* ---------------------- Method for Drawing Bitmap on Screen -------------------- */
	
	public void draw()
	{
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
	}
	
	/* ------------------------------- Set Vertex Array -------------------------------- */
	
	private void setVertexArray()
	{
		VERTEX_DATA[0] = 0f; 
		VERTEX_DATA[1] = 0f;
		VERTEX_DATA[2] = bitmapZ;
		VERTEX_DATA[3] = 0.5f; 
		VERTEX_DATA[4] = 0.5f;
		
		VERTEX_DATA[5] = bitmapLeft; 
		VERTEX_DATA[6] = bitmapBottom;
		VERTEX_DATA[7] = bitmapZ;
		VERTEX_DATA[8] = 0f;
		VERTEX_DATA[9] = 1f;
		
		VERTEX_DATA[10] = bitmapRight;
		VERTEX_DATA[11] = bitmapBottom;
		VERTEX_DATA[12] = bitmapZ;
		VERTEX_DATA[13] = 1f;
		VERTEX_DATA[14] = 1f;
		
		VERTEX_DATA[15] = bitmapRight;
		VERTEX_DATA[16] = bitmapTop;
		VERTEX_DATA[17] = bitmapZ;
		VERTEX_DATA[18] = 1f;
		VERTEX_DATA[19] = 0f;
		
		VERTEX_DATA[20] = bitmapLeft;
		VERTEX_DATA[21] = bitmapTop;
		VERTEX_DATA[22] = bitmapZ;
		VERTEX_DATA[23] = 0f;
		VERTEX_DATA[24] = 0f;
		
		VERTEX_DATA[25] = bitmapLeft;
		VERTEX_DATA[26] = bitmapBottom;
		VERTEX_DATA[27] = bitmapZ;
		VERTEX_DATA[28] = 0f;
		VERTEX_DATA[29] = 1f;
		
		vertexArray = new VertexArray(VERTEX_DATA);
	}
	
	public void scaleUp()
	{
		bitmapZ *= scale;
		bitmapLeft *= scale;
		bitmapRight *= scale;
		bitmapTop *= scale;
		bitmapBottom *= scale;
		setVertexArray();
		Log.i(TAG, "New scale: " + scale);
	}
	
	public void scaleDown()
	{
		bitmapZ /= scale;
		bitmapLeft /= scale;
		bitmapRight /= scale;
		bitmapTop /= scale;
		bitmapBottom /= scale;
		setVertexArray();
		Log.i(TAG, "New scale: " + scale);
	}
	

}
