package com.ihealth.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihealth.BaseDialog;
import com.ihealth.facecheckinapp.R;
import com.ihealth.utils.ConstantArguments;

public class FaceDetectResultDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private LayoutInflater inflater;
    private Button secondBtn;
    private Button firstBtn;
    private ImageView photo;
    private TextView message;
    private TextView name;
    private BaseDialog dialogCheck;
    private OnFirstAndSecondClicker mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public FaceDetectResultDialog(@NonNull Context context) {
        super(context,R.style.CostumeDialog);
        initView(context);
    }

    public FaceDetectResultDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    protected FaceDetectResultDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        dialogCheck = new BaseDialog(mContext);
        View view = inflater.inflate(R.layout.dialog_face_detect, null, false);
        setContentView(R.layout.dialog_face_detect);
        name = (TextView) view.findViewById(R.id.dialog_face_detect_name);
        message = (TextView) view.findViewById(R.id.dialog_face_detect_name);
        photo = (ImageView) view.findViewById(R.id.dialog_face_detect_photo);
        firstBtn = (Button) view.findViewById(R.id.dialog_face_detect_first_btn);
        secondBtn = (Button) view.findViewById(R.id.dialog_face_detect_second_btn);
        firstBtn.setOnClickListener(this);
        secondBtn.setOnClickListener(this);
//        setCancelable(true);
        setCanceledOnTouchOutside(true);//点击外部Dialog消失
        dialogCheck.setContentView(view);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_face_detect_first_btn:
                dialogCheck.dismiss();
                if(mListener!=null){
                    mListener.onFirstClick();
                }
                break;
            case R.id.dialog_face_detect_second_btn:
                dialogCheck.dismiss();
                if(mListener!=null){
                    mListener.onSecondClick();
                }
                break;
        }
    }

    public void setData( int status ) {
//        name.setText();
//        message.setText();
//        photo.setImageDrawable();


        switch (status) {
            case ConstantArguments.DETECT_RESULT_SUCESS_SIGN_PREPARE_CLINIC:
                firstBtn.setVisibility(View.GONE);
                secondBtn.setVisibility(View.VISIBLE);
                secondBtn.setText("打印就诊小条");
                break;
            case ConstantArguments.DETECT_RESULT_SUCESS_SIGN_MORE_TIME:
                firstBtn.setVisibility(View.VISIBLE);
                secondBtn.setVisibility(View.VISIBLE);
                firstBtn.setText("返回拍照");
                secondBtn.setText("重新打印就诊小条");
                break;
            case ConstantArguments.DETECT_RESULT_SUCESS_NOT_SUBSCRIBE_SELECT_OTHER:
                firstBtn.setVisibility(View.VISIBLE);
                secondBtn.setVisibility(View.GONE);
                firstBtn.setText("选择其他病种");
                secondBtn.setText("打印就诊小条");
                break;
            case ConstantArguments.DETECT_RESULT_SUCESS_NOT_SUBSCRIBE_ADD_CLINIC:
                firstBtn.setVisibility(View.VISIBLE);
                secondBtn.setVisibility(View.GONE);
                firstBtn.setText("我要加诊");
                secondBtn.setText("打印就诊小条");
                break;
            case ConstantArguments.DETECT_RESULT_FAILED:
                firstBtn.setVisibility(View.VISIBLE);
                secondBtn.setVisibility(View.GONE);
                firstBtn.setText("重新拍照");
                secondBtn.setText("打印就诊小条");
                break;
        }
        if (null != dialogCheck && !dialogCheck.isShowing())
        {
            dialogCheck.show();
        }
    }
    public interface  OnFirstAndSecondClicker{
        public void onFirstClick();
        public void onSecondClick();

    }
    public void setOnFirstAndSecondClicker(OnFirstAndSecondClicker listener){
        mListener = listener;
    }
}