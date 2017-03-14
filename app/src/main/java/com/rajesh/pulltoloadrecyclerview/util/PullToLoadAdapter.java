package com.rajesh.pulltoloadrecyclerview.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rajesh.pulltoloadrecyclerview.R;

/**
 * Created by zhufeng on 2016/8/12.
 * 用于上拉加载的RecyclerView的适配器
 */
public abstract class PullToLoadAdapter extends RecyclerView.Adapter {
    private final int TYPE_FOOTER = -1;
    private Context mContext;
    private RecyclerStatus status;
    /**
     * 锁住当前状态（防止多次加载）
     * 在网络加载时locked = true
     * 需要在网络加载成功后置为false
     */
    private boolean locked = false;
    /**
     * 是否出现上拉加载的状态提示条
     */
    private boolean pullLoadEnable = false;
    private OnProgressListener onProgressListener;
    private OnCompleteListener onCompleteListener;

    public PullToLoadAdapter(Context mContext) {
        this.mContext = mContext;
        status = RecyclerStatus.NONE;
    }

    @Override
    public int getItemCount() {
        if (pullLoadEnable) {
            return getItemCountChild() + 1;
        } else {
            return getItemCountChild();
        }
    }

    protected abstract int getItemCountChild();

    @Override
    public int getItemViewType(int position) {
        if (pullLoadEnable && position == getItemCountChild()) {
            return TYPE_FOOTER;
        }
        if (getItemViewTypeChild(position) == TYPE_FOOTER) {
            throw new IllegalArgumentException("type类型不能与底部条一样,请修改继承类的getItemViewTypeChild方法返回值");
        }
        return getItemViewTypeChild(position);
    }

    protected abstract int getItemViewTypeChild(int position);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.view_recycler_footer, parent, false);
            return new FooterViewHolder(contentView);
        } else {
            return onCreateViewHolderChild(parent, viewType);
        }
    }

    protected abstract RecyclerView.ViewHolder onCreateViewHolderChild(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).text.setText(status.getName());
            if (status == RecyclerStatus.LOADING) {
                ((FooterViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
            } else {
                ((FooterViewHolder) holder).progressBar.setVisibility(View.GONE);
            }
            ((FooterViewHolder) holder).text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onProgressListener != null) {
                        if (status == RecyclerStatus.ERROR) {
                            onProgressListener.tryAgain();
                        }
                    }
                    if (onCompleteListener != null) {
                        if (status == RecyclerStatus.COMPLETE) {
                            onCompleteListener.onComplete();
                        }
                    }
                }
            });
        } else {
            onBindViewHolderChild(holder, position);
        }
    }

    protected abstract void onBindViewHolderChild(RecyclerView.ViewHolder holder, int position);

    public void setLocked(boolean locked) {
        if (this.locked != locked) {
            this.locked = locked;
        }
    }

    public void setStatus(RecyclerStatus status) {
        if (this.status != status) {
            this.status = status;
            if (this.status == RecyclerStatus.NONE) {
                pullLoadEnable = false;
            } else {
                pullLoadEnable = true;
            }
        }
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public void addBindRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isSlideToBottom(recyclerView)) {
                    if (status == RecyclerStatus.LOADING) {
                        if (!locked) {
                            locked = true;
                            onProgressListener.nextPage();
                        }
                    }
                }
            }
        });
    }

    private boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return false;
        }
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()) {
            return true;
        }
        return false;
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public ProgressBar progressBar;

        public FooterViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.footer);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_item);
        }
    }

    public interface OnProgressListener {
        void nextPage();

        void tryAgain();
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    public enum RecyclerStatus {
        NONE(""),
        LOADING("加载中"),
        ERROR("点击重试"),
        COMPLETE("加载完成");

        String name;

        RecyclerStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
