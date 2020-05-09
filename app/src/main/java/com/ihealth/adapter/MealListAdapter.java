package com.ihealth.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihealth.bean.MealListDataBean;
import com.ihealth.facecheckin.R;
import com.ihealth.utils.MeasureMealDataUtils;

import java.util.List;

/**
 * 测量时间选择列表的adapter
 * Created by Liuhuan on 2020/05/07.
 */
public class MealListAdapter extends BaseAdapter {

    private List<MealListDataBean> mealDataList;
    private Context mContext;

    public MealListAdapter(Context mContext){
        this.mContext = mContext;
        this.mealDataList = MeasureMealDataUtils.getData();
    }

    @Override
    public int getCount() {
        return mealDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MealListDataBean dataBean = mealDataList.get(position);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_meal_list, null);
        LinearLayout llMeal = view.findViewById(R.id.ll_meal);
//        ImageView fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
        TextView mealText =  view.findViewById(R.id.tv_meal_name);
//        fruitImage.setImageResource(fruit.getImageId());
        mealText.setText(dataBean.getValue());
//        mealText.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryGreen));
//        llMeal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                popWnd.dismiss();
//            }
//        });
        return view;
    }
}
