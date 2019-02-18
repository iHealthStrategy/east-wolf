package com.ihealth.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.ihealth.BaseActivity;
import com.ihealth.facecheckinapp.R;

public class SplashStartActivity extends BaseActivity implements View.OnClickListener {

    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_start);

        initData();
        initView();
        initListeners();
    }

    private void initData() {

    }

    private void initView() {
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabtn_login_next_step);

    }

    private void initListeners() {
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabtn_login_next_step:
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                this.finish();
                break;
            default:
                break;
        }
    }
}
