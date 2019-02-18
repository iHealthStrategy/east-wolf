package com.ihealth.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.ihealth.BaseActivity;
import com.ihealth.facecheckinapp.R;

/**
 * 新用户注册
 *
 * @author liyanwen
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    TextView tvRegisterTitle;
    ViewFlipper vfNewUserInfos;

    Button btnNewUserStep1Next;

    Button btnNewUserStep2Previous;
    Button btnNewUserStep2Next;

    Button btnNewUserStep3Previous;
    Button btnNewUserStep3Next;

    Button btnNewUserStep4Previous;
    Button btnNewUserStep4TakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        initListeners();
    }

    private void initView(){

        tvRegisterTitle = (TextView) findViewById(R.id.tv_register_title);

        vfNewUserInfos = (ViewFlipper) findViewById(R.id.vf_register_new_user_infos);
        vfNewUserInfos.setAutoStart(false);
        vfNewUserInfos.setDisplayedChild(0);

        btnNewUserStep1Next = (Button) findViewById(R.id.btn_detect_new_user_step_1_next);
        btnNewUserStep2Previous = (Button) findViewById(R.id.btn_detect_new_user_step_2_previous);
        btnNewUserStep2Next = (Button) findViewById(R.id.btn_detect_new_user_step_2_next);
        btnNewUserStep3Previous = (Button) findViewById(R.id.btn_detect_new_user_step_3_previous);
        btnNewUserStep3Next = (Button) findViewById(R.id.btn_detect_new_user_step_3_next);
        btnNewUserStep4Previous = (Button) findViewById(R.id.btn_detect_new_user_step_4_previous);
        btnNewUserStep4TakePhoto = (Button) findViewById(R.id.btn_detect_new_user_step_4_take_photo);



    }

    private void initListeners(){
        btnNewUserStep1Next.setOnClickListener(this);
        btnNewUserStep2Previous.setOnClickListener(this);
        btnNewUserStep2Next.setOnClickListener(this);
        btnNewUserStep3Previous.setOnClickListener(this);
        btnNewUserStep3Next.setOnClickListener(this);
        btnNewUserStep4Previous.setOnClickListener(this);
        btnNewUserStep4TakePhoto.setOnClickListener(this);
    }

    private void changeTitle(){
        int index = vfNewUserInfos.getDisplayedChild();
        switch (index){
            case 0:
                tvRegisterTitle.setText("请录入患者姓名");
                break;
            case 1:
                tvRegisterTitle.setText("请录入患者手机号");
                break;
            case 2:
                tvRegisterTitle.setText("请录入患者身份证号(选填)");
                break;
            case 3:
                tvRegisterTitle.setText("请录入患者人脸信息");
                break;
                default:
                    tvRegisterTitle.setText("请录入患者姓名");
                    break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_detect_new_user_step_1_next:
                vfNewUserInfos.setDisplayedChild(1);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_2_previous:
                vfNewUserInfos.setDisplayedChild(0);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_2_next:
                vfNewUserInfos.setDisplayedChild(2);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_3_previous:
                vfNewUserInfos.setDisplayedChild(1);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_3_next:
                vfNewUserInfos.setDisplayedChild(3);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_4_previous:
                vfNewUserInfos.setDisplayedChild(2);
                changeTitle();
                break;
            case R.id.btn_detect_new_user_step_4_take_photo:
                break;
                default:
                    changeTitle();
                    break;
        }
    }
}
