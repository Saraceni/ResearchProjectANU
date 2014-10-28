package br.saraceni.research.opengl.objects;

import br.saraceni.research.opengl.data.Constants;
import br.saraceni.research.opengl.data.VertexArray;
import br.saraceni.research.opengl.programs.TextureShaderProgram;
import android.opengl.GLES20;


public class Table {
	
	private static final int POSITION_COMPONENT_COUNT = 3;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + 
			TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;
	
	private static final float[] VERTEX_DATA = 
		{
			// Order of coordinates: X, Y, S, T
			
			// Triangle Fan
			0f,		0f,	   2f, 	   0.5f,	0.5f,
		 -0.5f,	 -0.8f,    2f,       0f,   0.9f,
		  0.5f,  -0.8f,    2f,       1f,   0.9f,
		  0.5f,   0.8f,    2f,       1f,   0.1f, 
		 -0.5f,   0.8f,    2f,       0f,   0.1f,
		 -0.5f,  -0.8f,    2f,       0f,   0.9f
		 
		};
	
	private final VertexArray vertexArray;
	
	public Table()
	{
		vertexArray = new VertexArray(VERTEX_DATA);
	}
	
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
				TEXTURE_COORDINATES_COMPONENT_COUNT, 
				STRIDE);
	}
	
	public void draw()
	{
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
	}

}














