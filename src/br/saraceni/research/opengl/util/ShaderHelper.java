package br.saraceni.research.opengl.util;

import static android.opengl.GLES20.*; 
import static android.opengl.GLUtils.*; 
import static android.opengl.Matrix.*;
import android.util.Log;

/*
 * This class was taken from the book
 * Open GL ES 2 for Android A Quick-Start Guide
 * by Kevin Brothaler
 */

public class ShaderHelper {
	
	// Debugging tag for this class
	private static final String TAG = "ShaderHelper";
	
	/* ------------------------- Auxiliary Compile Methods ------------------------- */
	
	public static final int compileVertexShader(String shaderCode)
	{
		return compileShader(GL_VERTEX_SHADER, shaderCode);
	}
	
	public static int compileFragmentShader(String shaderCode)
	{
		return compileShader(GL_FRAGMENT_SHADER, shaderCode);
	}
	
	/* ---------------------------- Shader Compiler Method --------------------------- */
	
	private static int compileShader(int type, String shaderCode)
	{
		final int shaderObjectId = glCreateShader(type);
		
		// If shaderObject == 0 OpenGL faled to create the shader
		if(shaderObjectId == 0)
		{
			if(LoggerConfig.ON)
			{
				Log.w(TAG, "Could not create new shader.");
			}
			return 0;
		}
		
		// Bind the shader code to the shader Id, compiles and verify compilation status
		glShaderSource(shaderObjectId, shaderCode);
		glCompileShader(shaderObjectId);
		final int[] compileStatus = new int[1];
		glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
		
		if(LoggerConfig.ON)
		{
			// Print the shader info log to the Android output
			Log.v(TAG, "Results of compiling resource:\n" + shaderCode + "\n" +
			glGetShaderInfoLog(shaderObjectId));
		}
		
		if(compileStatus[0] == 0)
		{
			// If it failed delete the shader object
			glDeleteShader(shaderObjectId);
			
			if(LoggerConfig.ON)
			{
				Log.w(TAG, "Compilation of shader failed.");
			}
			
			return 0;
		}
		
		return shaderObjectId;
	}
	
	/* -------- Create a Program linking a Vertex Shader and a Fragment Shader --------- */
	
	public static int linkProgram(int vertexShaderId, int fragmentShaderId)
	{
		final int programObjectId = glCreateProgram();
		
		// If programObjectId == 0 OpenGL failed to create the program
		if(programObjectId == 0)
		{
			if(LoggerConfig.ON)
			{
				Log.w(TAG, "Could not create new program.");
			}
			
			return 0;
		}
		
		// Attach shaders and link program
		glAttachShader(programObjectId, vertexShaderId);
		glAttachShader(programObjectId, fragmentShaderId);
		glLinkProgram(programObjectId);
		
		// Verify linking status
		final int[] linkStatus = new int[1];
		glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
		
		if(LoggerConfig.ON)
		{
			// Print the program info log to the Android output
			Log.v(TAG, "Results of linking program:\n" + 
					glGetProgramInfoLog(programObjectId));
		}
		
		if(linkStatus[0] == 0)
		{
			// If it failed delete the program object
			glDeleteProgram(programObjectId);
			if(LoggerConfig.ON)
			{
				Log.w(TAG, "Linking of program failed.");
			}
			return 0;
		}
		
		return programObjectId;
	}
	
	/* ------------------------- Method for Validate Program ------------------------- */
	
	public static boolean validateProgram(int programObjectId)
	{
		glValidateProgram(programObjectId);
		
		// Get validation status
		final int[] validateStatus = new int[1];
		glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
		
		// Show verbose about validation 
		Log.v(TAG, "Results of validating program: " + validateStatus[0] +
				"\nLog: " + glGetProgramInfoLog(programObjectId));
		
		return validateStatus[0] != 0;
	}
	
	/* ------------------------ Method for Buiding the Program ----------------------- */
	
	public static int buildProgram(String vertexShaderSource,
			String fragmentShaderSource)
	{
		int program;
		
		// Compile the shaders
		int vertexShader = compileVertexShader(vertexShaderSource);
		int fragmentShader = compileFragmentShader(fragmentShaderSource);
		
		// Link them into a shader program
		program = linkProgram(vertexShader, fragmentShader);
		
		if(LoggerConfig.ON)
		{
			validateProgram(program);
		}
		
		return program;
	}

}













