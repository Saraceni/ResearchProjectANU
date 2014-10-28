package br.saraceni.research.opengl.objects;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import br.saraceni.research.opengl.data.Constants;
import br.saraceni.research.opengl.data.VertexArray;
import br.saraceni.research.opengl.programs.ColorShaderProgram;
import br.saraceni.research.opengl.programs.TextureShaderProgram;


public class ImageDisplay {
	
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
		
	
	private final float[] VERTEX_DATA = new float[30];
	
    private final float bitmapLeft;
	private final float bitmapRight;
	private final float bitmapBottom;
	private final float bitmapTop;
	
	private final Bitmap bitmap;
	
	private final VertexArray vertexArray;
	
	
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
		
		VERTEX_DATA[0] = 0f; 
		VERTEX_DATA[1] = 0f;
		VERTEX_DATA[2] = -1f;
		VERTEX_DATA[3] = 0.5f; 
		VERTEX_DATA[4] = 0.5f;
		
		VERTEX_DATA[5] = bitmapLeft; 
		VERTEX_DATA[6] = bitmapBottom;
		VERTEX_DATA[7] = -1f;
		VERTEX_DATA[8] = 0f;
		VERTEX_DATA[9] = 1f;
		
		VERTEX_DATA[10] = bitmapRight;
		VERTEX_DATA[11] = bitmapBottom;
		VERTEX_DATA[12] = -1f;
		VERTEX_DATA[13] = 1f;
		VERTEX_DATA[14] = 1f;
		
		VERTEX_DATA[15] = bitmapRight;
		VERTEX_DATA[16] = bitmapTop;
		VERTEX_DATA[17] = -1f;
		VERTEX_DATA[18] = 1f;
		VERTEX_DATA[19] = 0f;
		
		VERTEX_DATA[20] = bitmapLeft;
		VERTEX_DATA[21] = bitmapTop;
		VERTEX_DATA[22] = -1f;
		VERTEX_DATA[23] = 0f;
		VERTEX_DATA[24] = 0f;
		
		VERTEX_DATA[25] = bitmapLeft;
		VERTEX_DATA[26] = bitmapBottom;
		VERTEX_DATA[27] = -1f;
		VERTEX_DATA[28] = 0f;
		VERTEX_DATA[29] = 1f;
		
		vertexArray = new VertexArray(VERTEX_DATA);
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
	

}
