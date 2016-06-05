package com.thunderproxy.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thunderproxy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 6a209 on 16/5/3.
 */
public class IndexAdapter extends RecyclerView.Adapter{

    List<IndexItemData> mListData;

    public void setData(@NonNull List<IndexItemData> listData){
        mListData = listData;
        notifyDataSetChanged();
    }

    public void addData(IndexItemData itemData){
        if(null == mListData){
            mListData = new ArrayList<>();
        }
        mListData.add(itemData);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IndexViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.index_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position < 0 || position >= mListData.size()){
            return;
        }
        IndexItemData itemData = mListData.get(position);
        IndexViewHolder indexViewHolder = (IndexViewHolder)holder;
        indexViewHolder.mCreateTime.setText(itemData.getCreateTime());
        indexViewHolder.mUrl.setText(itemData.getRequest().getUrl());
        indexViewHolder.mMethod.setText(itemData.getRequest().getMethod().toString());
        indexViewHolder.mElapsedTime.setText(itemData.getElapsedTime() + "ms");
    }

    @Override
    public int getItemCount() {
        if(null == mListData){
            return 0;
        }
        return mListData.size();
    }


    static class IndexViewHolder extends RecyclerView.ViewHolder{

        TextView mUrl;
        TextView mMethod;
        TextView mCreateTime;
        TextView mElapsedTime;

        public IndexViewHolder(View itemView) {
            super(itemView);
            mUrl = (TextView)itemView.findViewById(R.id.url);
            mMethod = (TextView)itemView.findViewById(R.id.method);
            mCreateTime = (TextView)itemView.findViewById(R.id.time);
            mElapsedTime = (TextView)itemView.findViewById(R.id.elapsed_time);

        }
    }
}
