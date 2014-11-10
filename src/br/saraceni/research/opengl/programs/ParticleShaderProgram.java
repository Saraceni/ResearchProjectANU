package br.saraceni.research.opengl.programs;

import br.saraceni.research.R;
import android.content.Context;
import android.opengl.GLES20;

/*
 * This class was taken from the book
 * Open GL ES 2 for Android A Quick-Start Guide
 * by Kevin Brothaler
 */

public class ParticleShaderProgram extends ShaderProgram {
	
	// Uniform Locations
	private final int uMatrixLocation;
	private final int uTimeLocation;
	private final int uTextureUnitLocation;
	
	// Attribute Locations
	private final int aPositionLocation;
	private final int aColorLocation;
	private final int aDirectionVectorLocation;
	private final int aParticleStartTimeLocation;
	
	
	/* ----------------------------- Constructor --------------------------- */
	
	public ParticleShaderProgram(Context context)
	{
		super(context, R.raw.particle_vertex_shader, R.raw.particle_fragment_shader);
		
		// Retrieve uniform locations for the shader program
		uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
		uTimeLocation = GLES20.glGetUniformLocation(program, U_TIME);
		uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
		
		// Retrieve attribute locations for the shader program
		aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
		aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
		aDirectionVectorLocation = GLES20.glGetAttribLocation(program, A_DIRECTION_VECTOR);
		aParticleStartTimeLocation = GLES20.glGetAttribLocation(program, A_PARTICLE_START_TIME);
	}
	
	// Set uniforn data for this program
	public void setUniforms(float[] matrix, float elapsedTime, int textureId)
	{
		GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		GLES20.glUniform1f(uTimeLocation, elapsedTime);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glUniform1i(uTextureUnitLocation, 0);
	}
	
	/* ---------- Get Methods for Position, Color, DirectionVector and StartTime --------- */
	public int getPositionAttributeLocation()
	{
		return aPositionLocation;
	}
	
	public int getColorAttributeLocation()
	{
		return aColorLocation;
	}
	
	public int getDirectionVectorAttributeLocation()
	{
		return aDirectionVectorLocation;
	}
	
	public int getParticleStartTimeAttributeLocation()
	{
		return aParticleStartTimeLocation;
	}

}










