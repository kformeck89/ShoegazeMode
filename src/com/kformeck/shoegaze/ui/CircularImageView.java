package com.kformeck.shoegaze.ui;

import com.example.onthego.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircularImageView extends ImageView {
	private int borderWidth;
	private int viewWidth;
	private Bitmap image;
	private Paint paint;
	private Paint paintBorder;
	private BitmapShader shader;
	private Context context;
	
	public CircularImageView(Context context) {
		super(context);
		this.context = context;
		setup();
	}
	public CircularImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setup();
	}
	public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		setup();
	}
	
	private void setup() {
		paint = new Paint();
		paint.setAntiAlias(true);
		
		borderWidth = context.getResources().getDimensionPixelSize(
				R.dimen.about_user_image_border_thickness);
		paintBorder = new Paint();
		
		if (paintBorder != null) {
			paintBorder.setColor(context.getResources().getColor(R.color.card_details_color));
		}
		this.invalidate();
		paintBorder.setAntiAlias(true);
		paintBorder.setShadowLayer(0.4f, 0.0f, 2.0f, Color.BLACK);
		this.setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
	}
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = viewWidth;
		}
		return result;
	}
	private int measureHeight(int measureSpecHeight, int measureSpectWidth) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpecHeight);
		int specSize = MeasureSpec.getSize(measureSpecHeight);
		
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = viewWidth;
		}
		return (result + 2);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		BitmapDrawable bitmapDrawable = (BitmapDrawable)this.getDrawable();
		if (bitmapDrawable != null) {
			image = bitmapDrawable.getBitmap();
		}
		if (image != null) {
			shader = new BitmapShader(
					Bitmap.createScaledBitmap(
							image, canvas.getWidth(), canvas.getHeight(), false), 
					Shader.TileMode.CLAMP, 
					Shader.TileMode.CLAMP);
			paint.setShader(shader);
			int circleCenter = viewWidth / 2;
			
			canvas.drawCircle(
					circleCenter + borderWidth, 
					circleCenter + borderWidth, 
					circleCenter + borderWidth - 4.0f, paintBorder);
			canvas.drawCircle(
					circleCenter + borderWidth, 
					circleCenter + borderWidth, 
					circleCenter - 4.0f, paint);
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec, widthMeasureSpec);
		
		viewWidth = width - (borderWidth * 2);
		
		setMeasuredDimension(width, height);
		
	}
}
