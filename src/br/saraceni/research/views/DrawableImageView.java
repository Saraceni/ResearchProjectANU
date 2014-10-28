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

public class DrawableImageView extends ImageView {
	
	public static final String TAG = "DrawableImageView";
	
	private float x_touch = 0;
	private float y_touch = 0;
	private float y_origin = 0;
	private float x_origin = 0;
	
	private int cropWidth;
	private int cropHeight;
	
	private Paint rectanglePaint;

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
	
    private void init()
    {
    	rectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rectanglePaint.setColor(Color.RED);
		rectanglePaint.setStyle(Paint.Style.STROKE);
    }
    
    public void eraseRectangle()
    {
    	x_touch = 0;
    	y_touch = 0;
    	x_origin = 0;
    	y_origin = 0;
    }
    
    public int[] getRectangle()
    {
    	Log.i(TAG, "x = " + x_origin + "\ny = " + y_origin + "\nwidth = " + cropWidth+ "\nheight = " + cropHeight);
    	int result[] = {(int) x_origin,(int) y_origin, cropWidth, cropHeight };
    	return result;
    }
    
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawRect(x_origin, y_origin, x_touch, y_touch, rectanglePaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		x_touch = event.getX();
		y_touch = event.getY();
		int i_action = event.getAction();
		switch(i_action)
		{
		case MotionEvent.ACTION_DOWN:
			y_origin = y_touch;
			x_origin = x_touch;
			break;
		case MotionEvent.ACTION_MOVE:
			super.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			cropWidth = (int) Math.abs(x_origin - x_touch);
			cropHeight = (int) Math.abs(y_origin - y_touch);
			Log.i(TAG, "Rect Width = " + cropWidth);
			Log.i(TAG, "Rect Height = " + cropHeight);
			cropWidth = (int) Math.abs(x_origin - x_touch);
			cropHeight = (int) Math.abs(y_origin - y_touch);
			break;
		}
		return true;
	}
	
	@Override
	public void setImageBitmap(Bitmap bitmap)
	{
		super.setImageBitmap(bitmap);
	}

}
