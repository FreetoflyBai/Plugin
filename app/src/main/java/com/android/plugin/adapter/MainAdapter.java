package com.android.plugin.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.plugin.R;
import com.android.plugin.data.Bean;
import com.android.plugin.ui.MainActivity;

import java.util.List;

/**
 * author   : kevin.bai
 * date      : 2017/5/18 下午6:15
 * qq        :904869397@qq.com
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{

    MainActivity mActivity;
    List<Bean> mList;
    public MainAdapter(MainActivity activity,List<Bean> list){
        this.mActivity=activity;
        this.mList=list;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_main,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(mList.get(position).getPluginName());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.item_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.onItemClick(mList.get(getPosition()).getPluginPackage(),
                            mList.get(getPosition()).getPluginUpdate());
                }
            });
        }
    }
}
