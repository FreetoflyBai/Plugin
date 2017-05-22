package com.android.plugin.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.android.plugin.R;


public class LoadingDialog extends ProgressDialog{

	private Context mContext;
	public LoadingDialog(Context context) {
		super(context, R.style.loading_dialog);
		mContext=context;
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view=LayoutInflater.from(mContext).inflate(R.layout.view_loading, null);
		setContentView(view);
	}
}
