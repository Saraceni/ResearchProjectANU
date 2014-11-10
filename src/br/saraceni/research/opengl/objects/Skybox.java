package br.saraceni.research.opengl.objects;

import java.nio.ByteBuffer;

import br.saraceni.research.opengl.data.VertexArray;
import br.saraceni.research.opengl.programs.SkyboxShaderProgram;
import android.opengl.GLES20;

/*
 * This class was taken from the book
 * Open GL ES 2 for Android A Quick-Start Guide
 * by Kevin Brothaler
 */

public class Skybox {
	
	private static final int POSITION_COMPONENT_COUNT = 3;
	private final VertexArray vertexArray;
	private final ByteBuffer indexArray;
	
	public Skybox()
	{
		// Create a unit cube
		vertexArray = new VertexArray(new float[] {
				-1,   1,   1,		// (0) Top-left near
				 1,   1,   1,		// (1) Top-right near
				-1,  -1,   1,       // (2) Bottom-left near
				 1,  -1,   1,       // (3) Bottom-right near
				-1,   1,  -1,       // (4) Top-left far
				 1,   1,  -1,		// (5) Top-right far
				-1,  -1,  -1,       // (6) Bottom-left far
				 1,  -1,  -1        // (7) Bottom-right far
		});
		
		indexArray = ByteBuffer.allocateDirect(6 * 6).put(new byte[]
				{
				// Front 
				1, 3, 0,
				0, 3, 2,
				
				// Back
				4, 6, 5,
				5, 6, 7,
				
				// Left
				0, 2, 4,
				4, 2, 6,
				
				// Right
				5, 7, 1,
				1, 7, 3,
				
				// Top
				5, 1, 4,
				4, 1, 0,
				
				// Bottom
				6, 2, 7,
				7, 2, 3
				
				});
		indexArray.position(0);
	}
	
	// bind this object data to a variable in the shader program
	public void bindData(SkyboxShaderProgram skyboxShaderProgram)
	{
		vertexArray.setVertexAttribPointer(0, 
				skyboxShaderProgram.getPositionAttributeLocation(), 
				POSITION_COMPONENT_COUNT, 0);
	}
	
	// Draw this object in the screen
	public void draw()
	{
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_BYTE, indexArray);
	}

}









