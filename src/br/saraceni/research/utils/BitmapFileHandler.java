package br.saraceni.research.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/*
 * Class created to handle bitmap files saving and retrieval
 */

public class BitmapFileHandler {
	
	// Debugging tag for this class
	public static final String TAG = "BitmapFileHandler";
	
	// File name and extension.
	private static final String FILE_NAME = "bitmaps";
	private static final String FILE_EXTENSION = ".dat";
	
	// Number of files saved 
	private static int numFiles = 0;
	
	public BitmapFileHandler()
	{
		
	}
	
	/* -------------------------- Write a Bitmap Data as a File --------------------- */
	
	public static synchronized boolean writeBitmap(Context context, Bitmap bitmap)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput(FILE_NAME + numFiles + FILE_EXTENSION, Context.MODE_PRIVATE);
			bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
			fos.close();
		}
		catch(IOException exc)
		{
			exc.printStackTrace();
			return false;
		}
		numFiles++;
		return true;
	}
	
	/* -------------------------- Read Bitmap Data From File  --------------------- */
	
	public static synchronized Bitmap readBitmap(Context context, int num)
	{
		FileInputStream fis = null;
		File file = new File(FILE_NAME + num + FILE_EXTENSION);
		Bitmap result = null;
		try
		{
			fis = context.openFileInput(FILE_NAME + num + FILE_EXTENSION);
		    result = BitmapFactory.decodeStream(fis);
		    fis.close();
		}
		catch(IOException exc)
		{
			exc.printStackTrace();
			return null;
		}
		if(result == null)
		{
			Log.i(TAG, "Bitmap.Factory.decodeStream returned null");
		}
		return result;
		
	}
	
	/* -------------------------- Delete All Bitmap Files  --------------------- */
	
	public static synchronized void deleteAllFiles(Context context)
	{
		for(int i = 0; i < numFiles; i++)
		{
			context.deleteFile(FILE_NAME + i + FILE_EXTENSION);
		}
		numFiles = 0;
	}
	
	/* -------------------------- Get Number of Bitmap Files Saved  --------------------- */
	
	public static synchronized int getBitmapCount()
	{
		return numFiles;
	}

}
