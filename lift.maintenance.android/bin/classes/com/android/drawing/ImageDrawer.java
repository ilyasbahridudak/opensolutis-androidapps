package com.android.drawing;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class ImageDrawer extends ImageView implements OnTouchListener{

	private Bitmap bitmap;
	private Canvas canvas;
	private Paint paint;
	
	private float downx = 0,downy = 0,upx = 0,upy = 0;
	
	public ImageDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public ImageDrawer(Context context) {
		super(context);
		init();
	}	
	public ImageDrawer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
		for(int x=0;x<bitmap.getWidth();x++){
	    	for(int y=0;y<bitmap.getHeight();y++){
	    		bitmap.setPixel(x, y, Color.WHITE);
	    	}
	    }
		
		createListener();
	}

	private void createListener() {
		canvas = new Canvas(bitmap);
	    paint = new Paint();
	    paint.setColor(Color.BLACK);
	    setImageBitmap(bitmap);
	    setOnTouchListener(this);
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		
	    switch (action) {
	    case MotionEvent.ACTION_DOWN:
	      downx = event.getX();
	      downy = event.getY();
	      break;
	    case MotionEvent.ACTION_MOVE:
	      upx = event.getX();
	      upy = event.getY();
	      canvas.drawLine(downx, downy, upx, upy, paint);
	      invalidate();
	      downx = upx;
	      downy = upy;
	      break;
	    case MotionEvent.ACTION_UP:
	      upx = event.getX();
	      upy = event.getY();
	      canvas.drawLine(downx, downy, upx, upy, paint);
	      invalidate();
	      break;
	    case MotionEvent.ACTION_CANCEL:
	      break;
	    default:
	      break;
	    }
	    return true;
	}
	
	/**
	 * Get the content of image in a byte array.
	 * 
	 * @return byte[] with the content of image
	 */
	public byte[] getByteArray(){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
		
		return bos.toByteArray();
	}
	
	/**
	 * Convert a byte[] to bitmap and insert it in the image drawer
	 * @param blob content of the image
	 */
	public void setByteArray(byte[] blob){
		Bitmap baBitmap = BitmapFactory.decodeByteArray(blob,0,blob.length);
		if(baBitmap != null)
			bitmap = Bitmap.createScaledBitmap(baBitmap, baBitmap.getWidth(), baBitmap.getHeight(), true);
		
		createListener();
	}
	
	/**
	 * resize the image with width and height
	 * @param width in pixels
	 * @param height in pixels
	 */
	public void setSize(int width, int height){
		Bitmap oldBitmap = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
		bitmap = Bitmap.createScaledBitmap(oldBitmap, width, height, true);//(width, width, Bitmap.Config.ARGB_8888);
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				if(x>oldBitmap.getWidth() ||  y<oldBitmap.getHeight())
					bitmap.setPixel(x, y, Color.WHITE);
			}
		}

		createListener();
	}
	
	/**
	 * Erase the content of the image and make it white
	 * 
	 */
	public void emptyImage(){
		bitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		for(int x=0;x<bitmap.getWidth();x++){
			for(int y=0;y<bitmap.getHeight();y++){
				bitmap.setPixel(x, y, Color.WHITE);
			}
		}

		createListener();
	}
}
