package com.android.plugin.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Description:
 * Author     : kevin.bai
 * Time       : 2016/11/23 19:42
 * QQ         : 904869397@qq.com
 */

public class AssetsUtils {
    /**
     * assets file to json string
     *
     * @param context
     * @param assetsFile
     * @return
     * @throws IOException
     */
    public static String fromJson(Context context, String assetsFile)  {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = context.getAssets().open(assetsFile);
            String result = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
