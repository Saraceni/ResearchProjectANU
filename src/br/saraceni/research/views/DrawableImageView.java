package br.saraceni.research.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/*
 * View created to handle user interaction for 
 * selecting element for grab cut
 */

public class DrawableImageView extends ImageView {
	
	// Debugging tag for this class
	public static final String TAG = "DrawableImageView";
	
	// User touch coordinates variables
	private float x_touch = 0;
	private float y_touch = 0;
	private float y_origin = 0;
	private float x_origin = 0;
	
	// Size selection rectangle drawn by the user.
	private int cropWidth;
	private int cropHeight;
	
	// Paint for drawing rectangle
	private Paint rectanglePaint;
	
	/* -------------------------------- Constructors ------------------------------ */

	public DrawableImageView(Context context) {
		super(context);
		init();
	}
	
	public DrawableImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    public DrawableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    /* --------------------------- Initialize Rectangle Paint ------------------------ */
	
    private void init()
    {
    	rectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rectanglePaint.setColor(Color.RED);
		rectanglePaint.setStyle(Paint.Style.STROKE);
    }
    /* ------------------- Remove Rectangle Lines from The Screen --------------------- */
    
    public void eraseRectangle()
    {
    	x_touch = 0;
    	y_touch = 0;
    	x_origin = 0;
    	y_origin = 0;
    }
    
    /* -------------------- Retrieve Rectangle Size and Coordinates --------------------- */
    
    public int[] getRectangle()
    {
    	Log.i(TAG, "x = " + x_origin + "\ny = " + y_origin + "\nwidth = " + cropWidth+ "\nheight = " + cropHeight);
    	int result[] = {(int) x_origin,(int) y_origin, cropWidth, cropHeight };
    	return result;
    }
    
    /* --------------- Method Called when the screen needs to be drawn ---------------- */
    
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		// Draw Rectangle
		canvas.drawRect(x_origin, y_origin, x_touch, y_touch, rectanglePaint);
	}
	
	/* ----------------------- Callback for handling touchscreen events -------------- */
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// Retrieve touch coordinates
		x_touch = event.getX();
		y_touch = event.getY();
		int i_action = event.getAction();
		switch(i_action)
		{
		// User pressed the screen
		case MotionEvent.ACTION_DOWN:
			// Updtae origin coordinates
			y_origin = y_touch;
			x_origin = x_touch;
			break;
		// User is moving through screen
		case MotionEvent.ACTION_MOVE:
			// Redraw the view
			super.invalidate();
			break;
		// User removed finger from screen
		case MotionEvent.ACTION_UP:
			// Retrieve width and height of rectangle.
			Log.i(TAG, "Rect Width = " + cropWidth);
			Log.i(TAG, "Rect Height = " + cropHeight);
			cropWidth = (int) Math.abs(x_origin - x_touch);
			cropHeight = (int) Math.abs(y_origin - y_touch);
			break;
		}
		return true;
	}
	
	// Set bitmap as view background
	@Override
	public void setImageBitmap(Bitmap bitmap)
	{
		super.setImageBitmap(bitmap);
	}

}
