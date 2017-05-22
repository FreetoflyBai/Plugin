package com.android.plugin.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.plugin.R;
import com.android.plugin.adapter.MainAdapter;
import com.android.plugin.data.Bean;
import com.android.plugin.decoration.GridSpaceItemDecoration;
import com.android.plugin.utils.AssetsUtils;
import com.android.plugin.widget.LoadingDialog;
import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.compat.PackageManagerCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executors;

import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_SUCCEEDED;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    RecyclerView mRecyclerView;
    /**
     * 全局loading
     */
    LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initData();

    }


    private void initView(){
        mRecyclerView= (RecyclerView) this.findViewById(R.id.main_rv);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpaceItemDecoration(16,GridLayoutManager.VERTICAL));
        openPlugin();

    }

    private void initListener(){

    }

    private void initData(){
        List<Bean> list=JSON.parseArray(AssetsUtils.fromJson(this,"http.json"), Bean.class);
        MainAdapter mainAdapter=new MainAdapter(this,list);
        mRecyclerView.setAdapter(mainAdapter);

    }


    private void openPlugin(){
        PluginManager.getInstance().addServiceConnection(this);
    }

    @Override
    public void onDestroy() {
        PluginManager.getInstance().removeServiceConnection(this);
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }


    public void onItemClick(String packageName,int update){
        startLoading();
        startLoad(packageName,update);

    }


    Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            stopLoading();
            if(msg.what==0){
                Toast.makeText(MainActivity.this,"插件启动失败",Toast.LENGTH_LONG).show();
            }else if(msg.what==1){
                startPlugin((String)msg.obj);
            }
        }
    };


    /**
     * 按需启动插件
     */
    private void startLoad(final String packageName,int update){
        //判断服务是否开启
        if(!PluginManager.getInstance().isConnected()){
            stopLoading();
            return;
        }
        try {
            //判断插件是否已安装,已安装则直接启动插件
            if(PluginManager.getInstance().getPackageInfo(packageName, 0) != null&&update!=1){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopLoading();
                        startPlugin(packageName);
                    }
                },1000);
            }else{//未安装，则下载安装
                downloadAndInstall(packageName);
            }
        } catch (RemoteException e) {
            stopLoading();
        }
    }

    /**
     * 直接打开插件
     * @param packageName
     */
    private void startPlugin(String packageName){
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 开启写入文件
     */
    private void downloadAndInstall(final String packageName){
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String path= writeFileTo(packageName);
                    if(TextUtils.isEmpty(path)){
                        handler.sendEmptyMessage(0);
                    }else{
                        int result=PluginManager.getInstance().installPackage(path,
                                PackageManagerCompat.INSTALL_REPLACE_EXISTING);
                        if(result==INSTALL_SUCCEEDED){
                            Message message=new Message();
                            message.what=1;
                            message.obj=packageName;
                            handler.sendMessage(message);
                        }else{
                            handler.sendEmptyMessage(0);
                        }
                    }
                } catch (RemoteException e) {
                    stopLoading();
                }
            }
        });
    }


    /**
     * 写入文件到SDCard
     * 示例从assets 写入sdcard ,可从网络下载
     * 注意后缀名称尽量不要用.apk ，因为违反google政策，apk包内不能包含可执行程序
     * @return
     */
    FileOutputStream fos;
    private String writeFileTo(String packageName){
        String assetsName=packageName.replace(".","@").split("@")[2];
        String filePath="";
        try {
            InputStream is=getAssets().open(assetsName+".apk");
            File dir = new File(
                    Environment.getExternalStorageDirectory(),
                    "Android/data/"+getPackageName());
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file=new File(dir.getAbsolutePath()+"/"+assetsName+".apk");
            if(!file.exists()){
                file.createNewFile();
            }
            fos=new FileOutputStream(file);
            int ch=-1;
            byte[] buffer = new byte[1024];
            while((ch=is.read(buffer))!=-1){
                fos.write(buffer,0,ch);
            }
            fos.flush();
            filePath=file.getAbsolutePath();
        } catch (IOException e) {
            stopLoading();
            try {
                fos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return filePath;
    }


    /**
     * 显示加载框
     */
    private void startLoading(){
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            return;
        }
        mLoadingDialog = new LoadingDialog(this);
        mLoadingDialog.setCanceledOnTouchOutside(true);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.setMessage("Loading...");
        mLoadingDialog.show();
    }

    /**
     * 取消加载框
     */
    private void stopLoading(){
        try {
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
