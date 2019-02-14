package com.ihealth.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ihealth.MainActivity;
import com.ihealth.facecheckinapp.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initData();
        initView();
        initListeners();
    }

    private void initData() {

    }

    private void initView() {
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabtn_login);

    }

    private void initListeners() {
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabtn_login:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
