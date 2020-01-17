package com.ihealth.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihealth.bean.HospitalBean;
import com.ihealth.facecheckinapp.R;

import java.util.List;

public class LoginSpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater layoutInflater;
    private List<HospitalBean> hospitalList;

    public LoginSpinnerAdapter(Context mContext, List<HospitalBean> hospitalList) {
        this.mContext = mContext;
        this.hospitalList = hospitalList;
    }

    @Override
    public int getCount() {
        return hospitalList.size();
    }

    @Override
    public Object getItem(int position) {
        return hospitalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.view_spinner_login_layout, null);
            new SpinnerViewHolder(convertView);
        }
        SpinnerViewHolder spinnerViewHolder = (SpinnerViewHolder) convertView.getTag();

        HospitalBean hospital = hospitalList.get(position);
        // spinnerViewHolder.ivLoginSelectHospitalImage.setImageResource(hospitalBean.getLogoImg());
//        if (!TextUtils.isEmpty(hospital.getLogoImg()) && hospital.getLogoImg().startsWith("http")){
//            Glide.with(mContext).load(hospital.getLogoImg()).into(spinnerViewHolder.ivLoginSelectHospitalImage);
//        }
        spinnerViewHolder.tvLoginSelectHospitalName.setText(hospital.getFullname());

        return convertView;
    }


    @Override
    public boolean isEmpty() {
        return hospitalList.isEmpty();
    }

    private class SpinnerViewHolder {
        ImageView ivLoginSelectHospitalImage;
        TextView tvLoginSelectHospitalName;

        public SpinnerViewHolder(View convertView){
            ivLoginSelectHospitalImage = (ImageView) convertView.findViewById(R.id.iv_login_spinner_hospital_logo);
            tvLoginSelectHospitalName = (TextView) convertView.findViewById(R.id.tv_login_spinner_hospital_name);
            convertView.setTag(this);
        }
    }
}
