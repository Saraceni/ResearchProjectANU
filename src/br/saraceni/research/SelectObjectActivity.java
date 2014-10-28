package br.saraceni.research;

import java.io.ByteArrayOutputStream;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import br.saraceni.research.opengl.cardboard.MainCardboardActivity;
import br.saraceni.research.utils.MatBitmapHelper;
import br.saraceni.research.views.DrawableImageView;

public class SelectObjectActivity extends Activity {
	
	public static final String OBJECT_BITMAP_EXTRA = "OBJECT_BITMAP_EXTRA";
	
	private Bitmap bitmapCameraFrame;
	private Bitmap bitmapObject;
	private DrawableImageView drawableImageView;
	
	/* ------------------------------ Life Cycle Methods ---------------------------- */
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_obj_activity);
		drawableImageView = (DrawableImageView) findViewById(R.id.image_display);
		if(retrieveBitmap())
		{
			drawableImageView.setImageBitmap(bitmapCameraFrame);
		}
	}
	
	/* --------------------------- Intent Bitmap Retrieval --------------------------- */ 
	
	private boolean retrieveBitmap()
	{
		Intent intent = getIntent();
		String sUri = intent.getStringExtra(ResearchMainActivity.FRAME_URI_EXTRA);
		if(sUri != null)
		{
			bitmapCameraFrame = MatBitmapHelper.getBitmapFromUri(this, sUri);
			if(bitmapCameraFrame != null)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	/* ---------------------------------- Menu Methods ------------------------------*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sel_obj_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_finish) 
		{
			selectObjectFromImg();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/* ------------------------- Select Object From Image ------------------------------*/
	
	private void selectObjectFromImg()
	{
		int[] boundaries = drawableImageView.getRectangle();
		Rect rect = new Rect(boundaries[0], boundaries[1], boundaries[2], boundaries[3]);
		drawableImageView.eraseRectangle();
		Mat img = MatBitmapHelper.bitmapToMat(bitmapCameraFrame);
		Mat matObjct = MatBitmapHelper.grabCut(img, rect);
		bitmapObject = MatBitmapHelper.MatToBitmap(matObjct);
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		bitmapObject.compress(Bitmap.CompressFormat.PNG, 50, bs);
		//Intent intent = new Intent(SelectObjectActivity.this, OpenGLRenderingActivity.class);
		Intent intent = new Intent(SelectObjectActivity.this, MainCardboardActivity.class);
		intent.putExtra(OBJECT_BITMAP_EXTRA, bs.toByteArray());
		startActivity(intent);
	}

}
