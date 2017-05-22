package com.android.plugin.data;

/**
 * author   : kevin.bai
 * date      : 2017/5/18 下午6:27
 * qq        :904869397@qq.com
 */

public class Bean {


    /**
     * pluginIcon : http://www.xxx.jpg
     * pluginName : 摩拜单车
     * pluginUrl : http://www.xxx.apk
     * pluginPackage : com.android.mobike
     * pluginUpdate : 1
     */

    private String pluginIcon;
    private String pluginName;
    private String pluginUrl;
    private String pluginPackage;
    private int pluginUpdate;

    public String getPluginIcon() {
        return pluginIcon;
    }

    public void setPluginIcon(String pluginIcon) {
        this.pluginIcon = pluginIcon;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginUrl() {
        return pluginUrl;
    }

    public void setPluginUrl(String pluginUrl) {
        this.pluginUrl = pluginUrl;
    }

    public String getPluginPackage() {
        return pluginPackage;
    }

    public void setPluginPackage(String pluginPackage) {
        this.pluginPackage = pluginPackage;
    }

    public int getPluginUpdate() {
        return pluginUpdate;
    }

    public void setPluginUpdate(int pluginUpdate) {
        this.pluginUpdate = pluginUpdate;
    }
}
