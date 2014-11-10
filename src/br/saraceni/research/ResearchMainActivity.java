package br.saraceni.research;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;
import br.saraceni.research.utils.BitmapFileHandler;
import br.saraceni.research.utils.MatBitmapHelper;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;
import org.opencv.core.Size;

public class ResearchMainActivity extends Activity implements CvCameraViewListener2 {
	
	// Tag for debugging
	public static final String TAG = "ResearchMainActivity";
	
	// String to identify this Frame
	public static final String FRAME_URI_EXTRA = "FRAME_URI_EXTRA";
	
	// Int to identify activities results
	private static final int ACTIVITY_RESULT = 67;
	private static final int REQUEST_USR_IMG = 32;
	
	// View responsible for receiving camera frames
	private CameraBridgeViewBase mOpenCvCameraView;
	
	// OpenCV Mat object will holding camera frames
	private Mat mCameraFrame;
	private Mat lastCameraFrame;
	
	// Object for holding the frame size
	private Size frameSize;
	
    /* --------------------- Method for Downloading OpenCvLibrary --------------------- */
	
	/* Open CV is required to be installed in the mobile phone in order to use it's
	 * features. The following BaseLoaderCallback is necessary for searching if the
	 * phone has the OpenCV library app installed and if it hasn't, prompt the user to
	 * download the app.
	 */
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    /* Now enable camera view to start receiving frames */
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    
    /* -----------------------Activity Life Cycle Methods ---------------------------- */

    /*
     * This method is called when the activity is created. It retrieve an instance
     * from OpenCvCameraView defined in the activity_main.xml layout. It also sets
     * this layout as this activity GUI. This activity is defined as the OpenCvCameraView
     * CvCameraViewListener.
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.research_activity_main);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.OpenCvView);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
		mOpenCvCameraView.disableFpsMeter();
		
	}

	
	@Override
	public void onResume()
	{
		Log.i(TAG, "onResume");
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}
	
	// Disable frame capturing when this activity is paused
	@Override
	public void onPause()
	{
		Log.i(TAG, "onPause");
		super.onPause();
		if(mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}
	
	// Disable frame capturing when this activity is destroyed
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
	
	/* ---------------------------------- Menu Methods ------------------------------*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_capture) 
		{
			handleCapturedFrame();
			return true;
		}
		else if(id == R.id.action_select)
		{
			promptUserSelectImg();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/* ----------------------- Method for Sending Captured Frame ---------------------- */
	
	private void handleCapturedFrame()
	{
		lastCameraFrame = mCameraFrame.clone();
		Bitmap bitmapCameraFrame = MatBitmapHelper.MatToBitmap(lastCameraFrame);
		String sUri = MatBitmapHelper.getUriFromBitmap(this, bitmapCameraFrame, 
				"Frame", "Camera Frame");
		Intent intent = new Intent(ResearchMainActivity.this, SelectObjectActivity.class);
		intent.putExtra(FRAME_URI_EXTRA, sUri);
		startActivityForResult(intent, ACTIVITY_RESULT);
	}
	
	/* -------------------------- Result From Started Activity ------------------------- */
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode)
		{
		case ACTIVITY_RESULT:
			if(resultCode == Activity.RESULT_OK)
			{
				// The user has chosen all the elements to add to the 3D VR environment
				// Now this activity is finished
				Log.i(TAG, "resultCode = RESULT_OK");
				this.finish();
			}
			break;
		case REQUEST_USR_IMG:
			if(resultCode == Activity.RESULT_OK)
			{
				// The user have selected an Image. This image must be retrieved
				// To be passed to the next activity
				Uri uri = data.getData();
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
	            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
	            cursor.moveToFirst();
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String filePath = cursor.getString(columnIndex);
	            cursor.close();
	            // First decode with inJustDecodeBounds=true to check dimensions
	    	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    	    options.inJustDecodeBounds = false;
	    	    Bitmap img = BitmapFactory.decodeFile(filePath, options);
	    	    final float scale = calculateImageScale(options.outWidth, options.outHeight);
	    	    img = MatBitmapHelper.scaleBitmap(img, scale);
	    	    String sUri = MatBitmapHelper.getUriFromBitmap(this, img, 
	    				"Frame", "Camera Frame");
	    		Intent intent = new Intent(ResearchMainActivity.this, SelectObjectActivity.class);
	    		intent.putExtra(FRAME_URI_EXTRA, sUri);
	    		startActivityForResult(intent, ACTIVITY_RESULT);
			}
			break;
		}
	}
	
	/* --------------------- CvCameraViewListener2 Interface Methods ------------------- */

	@Override
	public void onCameraViewStarted(int width, int height) {
		frameSize = new Size(width, height);
		Log.i(TAG, "Frame width = " + width + " Frame Height = " + height); 
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mCameraFrame = inputFrame.rgba();
		return mCameraFrame;
	}
	
    /* ----------------------------- Select Image From Storage --------------------------- */
	
	private void promptUserSelectImg()
	{
		Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_USR_IMG);
		
	}
	
	/* ---------------------- Calculate Scale to fit Image in Frame ------------------- */
	
	private float calculateImageScale(int width, int height)
	{
		float scale = 1f;
		if(width > frameSize.width || height > frameSize.height)
		{
			float widthAspectRatio = ((float) frameSize.width / (float) width);
			float heightAspectRatio = ((float) frameSize.height / (float) height);
			scale = widthAspectRatio < heightAspectRatio ? widthAspectRatio : heightAspectRatio;
		}
		return scale;
	}
	
}
