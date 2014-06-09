package iit.android.swarachakra;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class SwaraChakra extends View {
	private float mOuterRadius;
	private float mInnerRadius;
	private float mArcTextRadius;
	private float centerX, centerY;
	private Paint mBlackPaint;
	private Paint mWhitePaint;
	private Paint mInnerPaint;
	private Paint mInnerTextPaint;
	private Paint mArcPaint;
	private Paint mArcDividerPaint;
	private Paint mArcPrevPaint;
	private Paint mArcTextPaint;
	private float screen_width;
	private float screen_height;
	private RectF bound;
	private static String[] defaultChakra;
	private static int nArcs;
	private int arc;
	private static boolean halantExists;
	private KeyAttr currentKey;
	private String keyLabel;
	

	public SwaraChakra(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}
	
	public SwaraChakra(Context context, AttributeSet attrs){
		super(context,attrs);
		init(context);
	}
	

	@SuppressLint("NewApi")
	private void init(Context context){
		mBlackPaint = new Paint();
		mBlackPaint.setColor(Color.BLACK);
		mBlackPaint.setAntiAlias(true);
		mWhitePaint = new Paint();
		mWhitePaint.setColor(Color.WHITE);
		mWhitePaint.setAntiAlias(true);
		mInnerPaint = new Paint();
		mInnerPaint.setColor(Color.rgb(255,255,255));
		mInnerPaint.setAntiAlias(true);
		mInnerTextPaint = new Paint();
		mInnerTextPaint.setColor(Color.BLACK);
		mInnerTextPaint.setAntiAlias(true);;
		mArcPaint = new Paint();
		mArcPaint.setColor(Color.rgb(48, 48, 48));
		mArcPaint.setAntiAlias(true);
		mArcDividerPaint = new Paint();
		mArcDividerPaint.setColor(Color.rgb(200, 200, 200));
		mArcDividerPaint.setAntiAlias(true);
		mArcPrevPaint = new Paint();
		mArcPrevPaint.setColor(Color.rgb(108, 108, 108));
		mArcPrevPaint.setAntiAlias(true);
		mArcTextPaint = new Paint();
		mArcTextPaint.setColor(Color.rgb(255, 255, 255));
		mArcTextPaint.setAntiAlias(true);
		
		setLayerType(View.LAYER_TYPE_HARDWARE, null);
		arc = -1;
		
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        screen_width = displayMetrics.widthPixels;
        screen_height = displayMetrics.heightPixels;
	}
	
	
	public void setMetrics(int mode){
		switch(mode){
		case 0:
			mOuterRadius = (float) (0.25*Math.min(screen_width, screen_height));
			mInnerRadius = (float) (0.3*mOuterRadius);
			mArcTextPaint.setTextSize((float) 0.25*mOuterRadius);
			mInnerTextPaint.setTextSize((float) 0.25*mOuterRadius);
			mArcTextRadius = (float) (0.75*mOuterRadius);
			break;
		default:
			break;
		}
		bound = new RectF(mOuterRadius,mOuterRadius,3*mOuterRadius, 3*mOuterRadius);
		centerX = bound.centerX();
		centerY = bound.centerY();
	}
	
	public float getOuterRadius(){
		return mOuterRadius;
	}
	
	public float getInnerRadius(){
		return mInnerRadius;
	}
	
	public float getScreenHeight(){
		return screen_height;
	}

	
	public static void setDefaultChakra(String[] swaras){
		defaultChakra = swaras;
		nArcs = defaultChakra.length;
	}
	
	public static int getNArcs(){
		return nArcs;
	}
	
	public void setArc(int region){
		if(region != arc){
			arc = region;
			invalidate();
		}
	}
	
	public void setCurrentKey(KeyAttr currentKey){
		this.currentKey = currentKey;
	}
	
	public void setKeyLabel(String label){
		keyLabel = label;
	}
	
	public void desetArc(){
		arc = -1;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		canvas.drawCircle(centerX, centerY, mOuterRadius, mBlackPaint);
		
		float anglePerArc = (float) (360.0/nArcs);
		Paint arcPaint;
		for(int i =0; i< nArcs; i++){
			arcPaint = mArcPaint;
			if(i == arc){arcPaint = mArcPrevPaint;}
			//arcs
			canvas.drawArc(bound, getMidAngle(i) - anglePerArc/2, anglePerArc-1, true, arcPaint);
			
			//arc seperators
			canvas.drawArc(bound, getMidAngle(i) + anglePerArc/2 -1, 1, true, mArcDividerPaint);
		}
		
		canvas.drawCircle(centerX, centerY, mInnerRadius, mInnerPaint);
		
		drawLetters(canvas);
		
	}
	
	private void drawLetters(Canvas canvas) {
		float textOffset = mArcTextPaint.getTextSize()/4;
		canvas.drawText(getText(), centerX - textOffset , centerY + textOffset, mInnerTextPaint);
		
		for(int i = 0; i<nArcs; i++){
			PointF textPos = getArcTextPoint(i);
			canvas.drawText(getTextForArc(i), textPos.x, textPos.y, mArcTextPaint);
		}
		
	}

	@Override
	protected void onMeasure(int measuredWidth, int measuredHeight){
		setMeasuredDimension((int)(4*mOuterRadius),(int)(4*mOuterRadius));
	}
	
	public static void setHalantExists(boolean b){
		halantExists = b;
	}

	public boolean isHalant(){
		boolean thisIsHalant = !currentKey.showCustomChakra;
		return (arc == 0) && halantExists && thisIsHalant;
	}
	
	public float getMidAngle(int region){
		float anglePerArc = (float) (360.0/nArcs);
		float offset = -90;
		float midAngle = region*anglePerArc + offset;
		return midAngle;
	}
	
	private PointF getArcTextPoint(int region){
		PointF textPos = new PointF();
		float textOffset = mArcTextPaint.getTextSize()/4;
		float angleRad = (float) Math.toRadians(getMidAngle(region));
		textPos.x = centerX + (float) (mArcTextRadius*Math.cos(angleRad)) - textOffset;
		textPos.y = centerY + (float) (mArcTextRadius*Math.sin(angleRad)) + textOffset;
		return textPos;
	}
	
	public String getText() {
		if(arc < 0){
			return keyLabel;
		}
		return getTextForArc(arc);
	}
	
	public String getTextForArc(int region){
		String[] chakra = defaultChakra;
		if(currentKey.showCustomChakra){
			chakra = currentKey.customChakraLayout;
			return chakra[region];
		}
		String text = keyLabel + chakra[region];
		return text;
	}
	
}
