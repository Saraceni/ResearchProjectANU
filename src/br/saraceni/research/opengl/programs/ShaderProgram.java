package br.saraceni.research.opengl.programs;

import br.saraceni.research.opengl.util.ShaderHelper;
import br.saraceni.research.utils.TextResourceReader;
import android.content.Context;
import android.opengl.GLES20;

/*
 * This class was taken from the book
 * Open GL ES 2 for Android A Quick-Start Guide
 * by Kevin Brothaler
 */

public class ShaderProgram {
	
	// Uniform constants
	protected static final String U_MATRIX = "u_Matrix";
	protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
	protected static final String U_COLOR = "u_Color";
	protected static final String U_TIME = "u_Time";
	
	// Attribute Constants
	protected static final String A_POSITION = "a_Position";
	protected static final String A_COLOR = "a_Color";
	protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
	protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
	protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";
	
	// Shader Program
	protected final int program;
	
	protected ShaderProgram(Context context, int vertexShaderResourceId,
			int fragmentShaderResourceId)
	{
		// Compile the shaders and link the program
		program = ShaderHelper.buildProgram(
				TextResourceReader.readTextFileFromResource(
						context, vertexShaderResourceId), 
				TextResourceReader.readTextFileFromResource(
						context, fragmentShaderResourceId));
	}
	
	public void useProgram()
	{
		// Set the current OpenGL program to this program
		GLES20.glUseProgram(program);
	}

}











