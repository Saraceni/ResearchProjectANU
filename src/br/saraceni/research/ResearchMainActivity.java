package br.saraceni.research;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
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
	
	public static final String TAG = "ResearchMainActivity";
	public static final String FRAME_URI_EXTRA = "FRAME_URI_EXTRA";
	
	private CameraBridgeViewBase mOpenCvCameraView;
	private Mat mCameraFrame;
	private Mat lastCameraFrame;
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
	
	@Override
	public void onPause()
	{
		Log.i(TAG, "onPause");
		super.onPause();
		if(mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}
	
	public void onDestroy() {
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
		startActivity(intent);
		this.finish();
	}
	
	/* --------------------- CvCameraViewListener2 Interface Methods ------------------- */

	@Override
	public void onCameraViewStarted(int width, int height) {
		frameSize = new Size(width, height);
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
}
