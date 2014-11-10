package br.saraceni.research.opengl.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


import android.opengl.GLES20;

/*
 * This class was taken from the book
 * Open GL ES 2 for Android A Quick-Start Guide
 * by Kevin Brothaler
 */


public class VertexArray {
	
	// Buffer for data
	private final FloatBuffer floatBuffer;
	
	// Constructor
	public VertexArray(float[] vertexData)
	{
		floatBuffer = ByteBuffer
				.allocateDirect(vertexData.length * Constants.BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(vertexData);
	}
	
	// Send the buffer data to the corresponding variable in the shader program
	public void setVertexAttribPointer(int dataOffset, int attributeLocation,
			int componentCount, int stride)
	{
		floatBuffer.position(dataOffset);
		GLES20.glVertexAttribPointer(attributeLocation, componentCount, GLES20.GL_FLOAT,
				false, stride, floatBuffer);
		GLES20.glEnableVertexAttribArray(attributeLocation);
		
		floatBuffer.position(0);
	}
	
	// Update the data on buffer
	public void updateBuffer(float[] vertexData, int start, int count)
	{
		floatBuffer.position(start);
		floatBuffer.put(vertexData, start, count);
		floatBuffer.position(0);
	}

}
