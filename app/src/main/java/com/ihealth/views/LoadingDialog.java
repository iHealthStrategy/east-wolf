package com.ihealth.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihealth.BaseDialog;
import com.ihealth.bean.AppointmentsBean;
import com.ihealth.facecheckin.R;

/**
 * 选择检查项目dialog
 * Created by Liuhuan on 2019/06/04.
 */
public class LoadingDialog extends Dialog {

    private BaseDialog dialogCheck;
    private Context mContext;

    private String appointmentsBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
    }

    public LoadingDialog(Context context, String content) {
        super(context);
        this.mContext = context;
        this.appointmentsBean = appointmentsBean;
        dialogCheck = new BaseDialog(mContext);
        if ("".equals(content)) {
            content = "加载中...";
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.progress_dialog, null);
        TextView tv = view.findViewById(R.id.progress_status_name);
        tv.setText(content);
        dialogCheck.setContentView(view);
        dialogCheck.setCancelable(false);

    }

    public void show() {
        if (null != dialogCheck && !dialogCheck.isShowing()) {
            dialogCheck.show();
        }
    }

    public void hide() {
        if (dialogCheck.isShowing()) {
            dialogCheck.dismiss();
        }
    }

}
