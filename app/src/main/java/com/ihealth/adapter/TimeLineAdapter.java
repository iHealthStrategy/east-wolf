package com.ihealth.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.ihealth.bean.TimeLineBean;
import com.ihealth.facecheckinapp.R;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter{

    private List<TimeLineBean> timeLineList;
    private Context context;

    public TimeLineAdapter(Context context, List<TimeLineBean> data){
        this.context = context;
        this.timeLineList = data;
    }

    @NonNull
    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.layout_time_line, null);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.tv_time.setText(timeLineList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return timeLineList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    class TimeLineViewHolder extends RecyclerView.ViewHolder {
        public TimelineView mTimelineView;
        public TextView tv_time;

        public TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);
            mTimelineView =  itemView.findViewById(R.id.timeline);
            tv_time = itemView.findViewById(R.id.tv_time);
            mTimelineView.initLine(viewType);
        }

    }
}


