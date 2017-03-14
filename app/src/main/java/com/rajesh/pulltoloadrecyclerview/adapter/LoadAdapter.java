package com.rajesh.pulltoloadrecyclerview.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rajesh.pulltoloadrecyclerview.util.PullToLoadAdapter;

import java.util.List;

/**
 * Created by zhufeng on 2017/3/10.
 */

public class LoadAdapter extends PullToLoadAdapter {
    private Context mContext;
    private List<Object> mDataList;

    public LoadAdapter(Context mContext, List<Object> mDataList){
        super(mContext);
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @Override
    protected int getItemCountChild() {
        return mDataList.size();
    }

    @Override
    protected int getItemViewTypeChild(int position) {
        return 0;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolderChild(ViewGroup parent, int viewType) {
        TextView tv = new TextView(mContext);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));
        tv.setGravity(Gravity.CENTER);
        return new MyViewHolder(tv);
    }

    @Override
    protected void onBindViewHolderChild(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder vh = (MyViewHolder) holder;
        vh.tv.setText(""+position);
        vh.tv.setTextColor(Color.BLACK);
        if(position % 2 != 0){
            vh.tv.setBackgroundColor(Color.GRAY);
        }else{
            vh.tv.setBackgroundColor(Color.WHITE);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView;
        }
    }
}
