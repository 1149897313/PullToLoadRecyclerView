package com.rajesh.pulltoloadrecyclerview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.rajesh.pulltoloadrecyclerview.adapter.LoadAdapter;
import com.rajesh.pulltoloadrecyclerview.util.PullToLoadAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context mContext = this;
    private RecyclerView mRecyclerView;

    private List<Object> mDataList = new ArrayList<>();
    private LoadAdapter mAdapter;

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                for (int i = 0; i < 10; i++) {
                    mDataList.add(null);
                }
                if (mAdapter.getItemCount() < 50) {
                    mAdapter.setLocked(false);
                    mAdapter.setStatus(PullToLoadAdapter.RecyclerStatus.LOADING);
                } else {
                    //加载完全
                    mAdapter.setStatus(PullToLoadAdapter.RecyclerStatus.COMPLETE);
                }
                mAdapter.notifyDataSetChanged();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.content);
    }

    private void initData() {
        mAdapter = new LoadAdapter(mContext, mDataList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.addBindRecyclerView(mRecyclerView);
        getData();
    }

    private void initEvent() {
        mAdapter.setOnProgressListener(new PullToLoadAdapter.OnProgressListener() {
            @Override
            public void nextPage() {
                getData();
            }

            @Override
            public void tryAgain() {
                getData();
            }
        });
    }

    private void getData() {
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }
}
