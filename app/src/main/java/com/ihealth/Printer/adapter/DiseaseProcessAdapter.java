package com.ihealth.Printer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ihealth.bean.AppointmentsBean;
import com.ihealth.facecheckinapp.R;

import java.util.List;

/**
 * 病程的ListView的adapter
 * Created by Liuhuan on 2019/09/23.
 */
public class DiseaseProcessAdapter extends BaseAdapter {

    private List<AppointmentsBean.PatientReport> dataList;
    private LayoutInflater layoutInflater;
    private Context mContext;

    public DiseaseProcessAdapter(Context context, List<AppointmentsBean.PatientReport> dataList) {
        this.mContext = context;
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(mContext);
    }

    public void setList(List<AppointmentsBean.PatientReport> dataList) {
        this.dataList.clear();
        if (null != dataList) {
            this.dataList.addAll(dataList);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public AppointmentsBean.PatientReport getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.item_disease_process_listview, null);
            holder = new ViewHolder();
            holder.date = convertView.findViewById(R.id.tv_date);
            holder.content = convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppointmentsBean.PatientReport report = dataList.get(position);
        holder.date.setText(report.getStartDate());
        holder.content.setText(report.getContent());

        return convertView;
    }

    private class ViewHolder {
        TextView date, content;
    }

}
