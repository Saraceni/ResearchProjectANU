package br.saraceni.research.opengl.programs;

import br.saraceni.research.R;
import android.content.Context;
import android.opengl.GLES20;

/*
 * This class was taken from the book
 * Open GL ES 2 for Android A Quick-Start Guide
 * by Kevin Brothaler
 */

public class SkyboxShaderProgram extends ShaderProgram {
	
	// Matrix Variables Location in Shader Progam
	private final int uMatrixLocation;
	private final int uTextureUnitLocation;
	private final int aPositionLocation;
	
	// Constructor
	public SkyboxShaderProgram(Context context)
	{
		super(context, R.raw.skybox_vertex_shader, R.raw.skybox_fragment_shader);
		
		uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
		uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
		aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
	}
	
	// Set Uniform Variables in Code
	public void setUniforms(float[] matrix, int textureId)
	{
		GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId);
		GLES20.glUniform1i(uTextureUnitLocation, 0);
	}
	
	// Gets position attribute
	public int getPositionAttributeLocation()
	{
		return aPositionLocation;
	}

}











