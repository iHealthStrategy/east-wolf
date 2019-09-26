package com.ihealth.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ihealth.facecheckinapp.R;

public class SameCircleView extends View {
    private Paint mPaint;
    private Context mContext;
    private int xPosition;
    private int yPosition;
    private int minRadius;
    private int secondRadius;
    private int maxRadius;
    private int [] innerColors;
    private int textSize;
    private Paint mTextPaint;
    private int spaceDis=10;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SameCircleView(Context context) {
        this(context,null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SameCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SameCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SameCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context,attrs , defStyleAttr ,defStyleRes);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.mContext = context;
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.colorF5FAFF));
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mTextPaint = new Paint();
        xPosition =(int)context.getResources().getDimension(R.dimen.layout_size_123dp);
        spaceDis =(int)context.getResources().getDimension(R.dimen.layout_size_4dp);
        yPosition = (int)context.getResources().getDimension(R.dimen.layout_size_123dp);
        maxRadius =  (int)context.getResources().getDimension(R.dimen.layout_size_123dp);
        secondRadius =  (int)context.getResources().getDimension(R.dimen.layout_size_108dp);
        minRadius = (int)context.getResources().getDimension(R.dimen.layout_size_94dp);
        this.
//        textSize = (int)PixelTool.spToPx(context, );
        getAndroiodScreenProperty();
        innerColors = new int[]{mContext.getResources().getColor(R.color.color3381FB),mContext.getResources().getColor(R.color.color2ACBDD)};
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }

    public void getAndroiodScreenProperty() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)


        Log.d("h_bl", "屏幕宽度（像素）：" + width);
        Log.d("h_bl", "屏幕高度（像素）：" + height);
        Log.d("h_bl", "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        Log.d("h_bl", "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        Log.d("h_bl", "屏幕宽度（dp）：" + screenWidth);
        Log.d("h_bl", "xPosition：" + xPosition+"context.getResources().getDimension(R.dimen.layout_size_64dp)"+mContext.getResources().getDimension(R.dimen.layout_size_64dp));
        Log.d("h_bl", "secondRadius：" + secondRadius);
        Log.d("h_bl", "minRadius（dp）：" + minRadius);
        Log.d("h_bl", "屏幕高度（dp）：" + screenHeight);
        Log.d("h_bl", "屏幕高度（dp）：" + screenHeight);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(xPosition,yPosition,maxRadius,mPaint);
        mPaint.setColor(mContext.getResources().getColor(R.color.colorE1F0FE));
        canvas.drawCircle(xPosition,yPosition,secondRadius,mPaint);

//        Shader shader = new RadialGradient(xPosition,yPosition,minRadius,innerColors,null, Shader.TileMode.CLAMP);//均匀分布
        LinearGradient shader=new LinearGradient(0,0,xPosition*2,yPosition*2, innerColors[1],innerColors[0],Shader.TileMode.MIRROR);  //
        mPaint.setShader(shader);
        canvas.drawCircle(xPosition,yPosition,minRadius,mPaint);
        mTextPaint.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_21sp));
        mTextPaint.setColor(mContext.getResources().getColor(R.color.colorFFFFFF));
        mTextPaint.setTextAlign(Paint.Align.CENTER);//居中

        Rect rect = new Rect();
        mTextPaint.getTextBounds("点击开始", 0, "点击开始".length(), rect);
        int textHeight = rect.height();
        canvas.drawText("点击开始",xPosition,yPosition - textHeight/2,mTextPaint);
        canvas.drawText("人脸签到",xPosition,yPosition +textHeight/2+spaceDis,mTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int temp =(int)mContext.getResources().getDimension(R.dimen.layout_size_246dp);
//        int temp= 740/3；\
//        Log.d("h_bl", "temp：" + temp+"context.getResources().getDimension(R.dimen.layout_size_140dp)"+mContext.getResources().getDimension(R.dimen.layout_size_140dp));
        setMeasuredDimension(temp,temp);
    }
}
