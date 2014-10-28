package br.saraceni.research.opengl.cardboard;

import br.saraceni.research.SelectObjectActivity;
import br.saraceni.research.utils.MatBitmapHelper;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class OpenGLRenderingActivity extends Activity {
	
    private static final String TAG = "CardboardRenderingActivity";
	
	private GLSurfaceView mGLSurfaceView;
	
	private boolean rendererSet = false;
	
	private Bitmap bitmap;
	
/*--------------- Detecting OpenGL ES 2.0 Support and Initializing App --------------- */
	
	private boolean supportsGLES20()
	{
		ActivityManager am = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		return info.reqGlEsVersion >= 0x20000;
	}
	
	private void initializeGLSurfaceView()
	{
		mGLSurfaceView = new GLSurfaceView(this);
		mGLSurfaceView.setEGLContextClientVersion(2);
		mGLSurfaceView.setPreserveEGLContextOnPause(true);
		mGLSurfaceView.setRenderer(new OpenGLRenderer(this, bitmap));
		rendererSet = true;
	}
	
	/* ------------------------ Activity Lifecycle Methods ----------------------- */
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		bitmap = retrieveBitmap();
		bitmap = MatBitmapHelper.convertPowerOfTwo(bitmap);
		if(supportsGLES20())
		{
			initializeGLSurfaceView();
		}
		else
		{
			Toast.makeText(this, "This device does not support OpenGL ES 2.0.", 
					Toast.LENGTH_LONG).show();
			return;
		}
		setContentView(mGLSurfaceView);
		/*Log.i(TAG, "Original Bitmap Width = " + bitmap.getWidth());
		Log.i(TAG, "Original Bitmap Height = " + bitmap.getHeight());
		bitmap = MatBitmapHelper.convertPowerOfTwo(bitmap);
		Log.i(TAG, "Converted Bitmap Width " + bitmap.getWidth());
		Log.i(TAG, "Converted Bitmap Height " + bitmap.getHeight());
		ImageView imgView = new ImageView(this);
		imgView.setImageBitmap(bitmap);
		setContentView(imgView);*/
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		if(rendererSet)
		{
			mGLSurfaceView.onPause();
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if(rendererSet)
		{
			mGLSurfaceView.onResume();
		}
	}
	
	/* ---------------------- Retrieve Bitmap From Last Activity ----------------------- */
	
	private Bitmap retrieveBitmap()
	{
		Intent intent = getIntent();
		byte[] bytes = intent.getByteArrayExtra(SelectObjectActivity.OBJECT_BITMAP_EXTRA);
		Bitmap result = BitmapFactory.decodeByteArray(
		        bytes, 0 , bytes.length);
		return result;
	}
	
}







