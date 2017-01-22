package com.android.plugin;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.morgoo.droidplugin.pm.PluginManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_SUCCEEDED;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    Handler handler=new Handler(Looper.getMainLooper());
    String PACKAGE_NAME="com.android.hello";
    FileOutputStream fos;
    String ASSETS_NAME= "hello";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //判断服务是否开启
        if(PluginManager.getInstance().isConnected()){
            startLoad();
        }else{
            PluginManager.getInstance().addServiceConnection(this);
        }


    }

    @Override
    public void onDestroy() {
        PluginManager.getInstance().removeServiceConnection(this);
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        startLoad();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    /**
     * 加载插件
     */
    private void startLoad(){
        if(!PluginManager.getInstance().isConnected()){
            return;
        }
        try {
            //判断插件是否已安装,已安装则直接启动插件
            if(PluginManager.getInstance().getPackageInfo(PACKAGE_NAME, 0) != null){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startPlugin(PACKAGE_NAME);
                    }
                },1000);
            }else{//未安装，则安装插件
                writeFile();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动插件
     * @param PACKAGE_NAME
     */
    private void startPlugin(String PACKAGE_NAME){
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(PACKAGE_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * 安装插件
     * @param path sdcard 路径
     */
    private void installPlugin(final String path){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final int result=PluginManager.getInstance().installPackage(path, 0);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(result==INSTALL_SUCCEEDED){
                                startPlugin(PACKAGE_NAME);
                            }
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 开启写入文件
     */
    private void writeFile(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String path=writeTo();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!TextUtils.isEmpty(path)){
                            installPlugin(path);
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 写入文件到SDCard
     * 示例从assets 写入sdcard ,可从网络下载
     * 注意后缀名称尽量不要用.apk ，因为违反google政策，apk包内不能包含可执行程序
     * @return
     */
    private String writeTo(){
        String filePath="";
        try {
            InputStream is=getAssets().open(ASSETS_NAME+".apk");
            File dir = new File(
                    Environment.getExternalStorageDirectory(),
                    "Android/data/"+getPackageName());
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file=new File(dir.getAbsolutePath()+"/"+ASSETS_NAME+".apk");
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
            try {
                fos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return filePath;
    }
}
