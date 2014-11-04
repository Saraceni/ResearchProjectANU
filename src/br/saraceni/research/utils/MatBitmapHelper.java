package br.saraceni.research.utils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

public class MatBitmapHelper {
	
	public static final String TAG = "MatBitmapHelper";
	
	
    /* ---------------------------- Mat and Bitmap Conversion ------------------------ */
	
	public static Bitmap MatToBitmap(Mat frame)
	{
		Bitmap bmp = Bitmap.createBitmap(frame.cols(), frame.rows(), Bitmap.Config.ARGB_8888);
		try
		{
			Utils.matToBitmap(frame, bmp);
		}
		catch(Exception exc)
		{
			Log.i(TAG, exc.toString());
			if(exc.getMessage() != null)
				Log.i(TAG, exc.getMessage());
			bmp = null;
		}
		return bmp;
	}
	
	public static Mat bitmapToMat(Bitmap bitmap)
	{
		Mat mat = new Mat();
		Log.i(TAG, "Bitmap width = " + bitmap.getWidth());
		Log.i(TAG, "Bitmap height = " + bitmap.getHeight());
		try
		{
			Utils.bitmapToMat(bitmap, mat);
		}
		catch(Exception exc)
		{
			Log.i(TAG, exc.toString());
			if(exc.getMessage() != null)
				Log.i(TAG, exc.getMessage());
			return null;
		}
		return mat;
	}
	
	/* ----------------------------- Bitmap Url Retrieval --------------------------- */
	
	public static String getUriFromBitmap(Context context, Bitmap bitmap, String title, String description)
	{
		String sUrl = MediaStore.Images.Media.insertImage(context.getContentResolver(), 
				bitmap, title, description);
		return sUrl;
	}
	
	public static Bitmap getBitmapFromUri(Context context, String sUri)
	{
		Bitmap bitmap;
		try
		{
			bitmap = Media.getBitmap(context.getContentResolver(), Uri.parse(sUri));
		}
		catch(Exception exc)
		{
			bitmap = null;
			Log.i(TAG, "Error retrieving bitmap: " + exc.toString());
			if(exc.getMessage() != null)
			{
				Log.i(TAG, exc.getMessage());
			}
		}
		return bitmap;
	}
	
	/* ----------------------------- Grab Cut Methods --------------------------- */
	
	public static Mat grabCut(Mat img, Rect rect)
	{
		Mat fgdModel = new Mat();
		Mat bgdModel = new Mat();
		Mat mask = new Mat();
		Mat imgC3 = new Mat();  
	    Imgproc.cvtColor(img, imgC3, Imgproc.COLOR_RGBA2RGB);
		Imgproc.grabCut(imgC3, mask, rect, bgdModel, fgdModel, 2, Imgproc.GC_INIT_WITH_RECT);
		Core.convertScaleAbs(mask, mask, 100, 0);
		Mat result = createCroppedImageMat(img, mask).submat(rect);
		return result;
	}
	
	private static Mat createCroppedImageMat(Mat img, Mat mask)
	{
		Mat result = new Mat(img.rows(), img.cols(), img.type());
		for(int r = 0; r < mask.rows(); r++)
		{
			for(int c = 0; c < mask.cols(); c++)
			{
				double[] pixels = mask.get(r, c);
				if(pixels[0] == 255)
				{
					result.put(r, c, img.get(r, c));
				}
			}
		}
		return result;
	}
	
	/* ---------------- Convert Bitmap Width and Height for Power of Two ---------------- */
	
	public static Bitmap convertPowerOfTwo(Bitmap bitmap)
	{
		PowerOfTwo width = getPowerOfTwo(bitmap.getWidth());
		PowerOfTwo height = getPowerOfTwo(bitmap.getHeight());
		return convertPowerOfTwo(bitmap, width, height);
	}
	
	public static Bitmap convertPowerOfTwo(Bitmap bitmap, PowerOfTwo width, PowerOfTwo height)
	{
		Bitmap result = Bitmap.createBitmap(width.value, height.value, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.ANTI_ALIAS_FLAG));
		return result;
	}
	
	private static PowerOfTwo getPowerOfTwo(int num)
	{
		if(num <= 64)
		{
			return PowerOfTwo.powOfTwo_64;
		}
		else if(num <= 128)
		{
			return PowerOfTwo.powOfTwo_128;
		}
		else if(num <= 256)
		{
			return PowerOfTwo.powOfTwo_256;
		}
		else if(num <= 512)
		{
			return PowerOfTwo.powOfTwo_512;
		}
		else if(num <= 1024)
		{
			return PowerOfTwo.powOfTwo_1024;
		}
		else
		{
			return PowerOfTwo.powOfTwo_2048;
		}
	}
	
	public enum PowerOfTwo
	{
		powOfTwo_64(64), powOfTwo_128(128), powOfTwo_256(256), powOfTwo_512(512),
		powOfTwo_1024(1024), powOfTwo_2048(2048);
		
		private int value;
		
		private PowerOfTwo(int value)
		{
			this.value = value;
		}
	};
	
	/* -------------------------------- Resize Bitmap -------------------------------- */
	
	public static Bitmap resizeBitmap(Bitmap bitmap, int maxSize)
	{
		int bigger = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight();
		if(bigger > maxSize)
		{
			float scale = bigger/maxSize;
			return scaleBitmap(bitmap, scale);
		}
		else
		{
			return bitmap;
		}
	}
	
	public static Bitmap scaleBitmap(Bitmap bitmap, float scale)
	{
		return resizeBitmap(bitmap, (int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale));
	}
	
	public static Bitmap resizeBitmap(Bitmap original, int width, int height)
	{
		return Bitmap.createScaledBitmap(original, width, height, false);
	}

}














