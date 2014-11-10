package br.saraceni.research.opengl.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/*
 * This class was taken from the book
 * Open GL ES 2 for Android A Quick-Start Guide
 * by Kevin Brothaler
 */

public class TextureHelper {
	
	// Debugging tag for this class
	private final static String TAG = "TextureHelper";
	
	/* -------------------------- Load a Bitmap into OpenGL ----------------------- */
	
	public static int loadTexture(Bitmap bitmap)
	{
		// Create ID for bitmap
		final int[] textureObjectIds = new int[1];
		GLES20.glGenTextures(1, textureObjectIds, 0);
		
		// If textureObjecIds[0] == 0 OpenGL failed to create the ID
		if(textureObjectIds[0] == 0)
		{
			if(LoggerConfig.ON)
			{
				Log.w(TAG, "Could not generate a new OpenGL texture object.");
			}
			return 0;
		}
		
		// Cannot load a null bitmap
		if(bitmap == null)
		{
			if(LoggerConfig.ON)
			{
				Log.w(TAG, "Bitmap could not be decoded.");
			}
			
			GLES20.glDeleteTextures(1, textureObjectIds, 0);
			return 0;
		}
		
		// Bind bitmap data as a 2D Texture and create linear filters for the Image
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, 
				GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, 
				GLES20.GL_LINEAR);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		
		return textureObjectIds[0];
	}
	
	/* ------------------------ Load Resource File into OpenGL ----------------------- */
	
	public static int loadTexture(Context context, int resourceId)
	{
		// Create ID for the object
		final int[] textureObjectIds = new int[1];
		GLES20.glGenTextures(1, textureObjectIds, 0);
		
		// If textureObjecIds[0] == 0 OpenGL failed to create the ID
		if(textureObjectIds[0] == 0)
		{
			if(LoggerConfig.ON)
			{
				Log.w(TAG, "Could not generate a new OpenGL texture object.");
			}
			return 0;
		}
		
		// Auxiliar Class for converting the resource into a bitmap without scaling
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
		final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId,
				options);
		
		// Cannot load a null bitmap
		if(bitmap == null)
		{
			if(LoggerConfig.ON)
			{
				Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
			}
			
			GLES20.glDeleteTextures(1, textureObjectIds, 0);
			return 0;
		}
		
		Log.i(TAG, "bitmap config = " + bitmap.getConfig().toString());
		
		// Bind bitmap data as a 2D Texture and create linear filters for the Image
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, 
				GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, 
				GLES20.GL_LINEAR);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		
		return textureObjectIds[0];
	} 
	
	/* ------------------------ Load Cube Map into OpenGL ----------------------- */
	
	public static int loadCubeMap(Context context, int[] cubeResources)
	{
		// Create a location in open gl for the cube map
		final int[] textureObjectIds = new int[1];
		GLES20.glGenTextures(1, textureObjectIds, 0);
		
		// Verify if the location was successfully created
		if(textureObjectIds[0] == 0)
		{
			if(LoggerConfig.ON)
			{
				Log.i(TAG, "Could not generate a new OpenGL texture object.");
			}
			return 0;
		}
		
		// Retrieve the bitmap of all 6 faces of the cube and verify if they are not null 
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		final Bitmap[] cubeBitmaps = new Bitmap[6];
		
		for(int i = 0; i < 6; i++)
		{
			cubeBitmaps[i] = 
					BitmapFactory.decodeResource(context.getResources(), 
							cubeResources[i], options);
			if(cubeBitmaps[i] == null)
			{
				if(LoggerConfig.ON)
				{
					Log.w(TAG, "Resource ID " + cubeResources[i] + " could not be decoded.");
				}
				GLES20.glDeleteTextures(1, textureObjectIds, 0);
				return 0;
			}
		}
		
		// Bind the cube to OpenGL and apply linear filter
		GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);
		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, 
				GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER,
				GLES20.GL_LINEAR);
		
		// Load each face of the cube in OpenGL
		GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);
		
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		
		// Recycle bitmaps to save memory
		for(Bitmap bitmap : cubeBitmaps)
		{
			bitmap.recycle();
		}
		
		return textureObjectIds[0];
	}

}











