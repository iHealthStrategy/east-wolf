package com.ihealth.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.ihealth.facecheckinapp.R;

public class LoadingProgressBar {
    private Activity sourceActivity;
    private ProgressBar progressBar;

    public LoadingProgressBar(Activity sourceActivity) {
        this.sourceActivity = sourceActivity;
    }

    public void show(){
        progressBar = addProgressBarToActivity(sourceActivity, null);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hide(){
        FrameLayout rootContainer = (FrameLayout) sourceActivity.findViewById(android.R.id.content);
        progressBar.setVisibility(View.GONE);
        rootContainer.removeView(progressBar);
    }

    /**
     * 在屏幕上添加一个progress bar，默认为隐藏状态
     *
     * @param activity 需要添加progress bar的Activity
     * @param customIndeterminateDrawable 自定义的progress bar图片，可以为null，此时为系统默认图案
     * @return {ProgressBar} progress bar 对象
     */
    private static ProgressBar addProgressBarToActivity(Activity activity, Drawable customIndeterminateDrawable) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

        ProgressBar progressBar = new ProgressBar(activity, null, R.layout.progress_dialog);
        progressBar.setVisibility(View.GONE);
        progressBar.setLayoutParams(lp);
        if (customIndeterminateDrawable != null) {
            progressBar.setIndeterminateDrawable(customIndeterminateDrawable);
        }

        FrameLayout rootContainer = (FrameLayout) activity.findViewById(android.R.id.content);
        rootContainer.addView(progressBar);

        return progressBar;
    }
}
