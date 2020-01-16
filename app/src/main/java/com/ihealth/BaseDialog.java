package com.ihealth;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ihealth.facecheckinapp.R;

public class BaseDialog extends Dialog {
    public BaseDialog(@NonNull Context context) {
        this(context, R.style.CostumeDialog);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
}
