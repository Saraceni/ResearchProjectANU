package br.saraceni.research.opengl.programs;

import br.saraceni.research.R;
import android.content.Context;
import android.opengl.GLES20;

/*
 * This class was taken from the book
 * Open GL ES 2 for Android A Quick-Start Guide
 * by Kevin Brothaler
 */

public class TextureShaderProgram extends ShaderProgram {
	
	// Uniform locations
	private final int uMatrixLocation;
	private final int uTextureUnitLocation;
	
	// Attribute locations
	private final int aPositionLocation;
	private final int aTextureCoordinatesLocation;
	
	public TextureShaderProgram(Context context)
	{
		super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
		
		// Retrieve uniform locations for the shader program
		uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
		uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
		
		// Retrieve attribute locations for the shader program
		aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
		aTextureCoordinatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
	}
	
	public void setUniforms(float[] matrix, int textureId)
	{
		// Pass the matrix into the shader program
		GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		
		// Set the active texture unit to texture unit 0
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		// Bind the texture to this unit
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		
		// Tell the texture uniform sampler to use this texture in the shader by
		// telling it to read from texture unit 0.
		GLES20.glUniform1i(uTextureUnitLocation, 0);
	}
	
	/* ------------- Get Methods for Position and TextureCoordinates Location ----------- */
	public int getPositionAttributeLocation()
	{
		return aPositionLocation;
	}
	
	public int getTextureCoordinatesLocation()
	{
		return aTextureCoordinatesLocation;
	}

}











