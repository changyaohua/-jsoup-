package com.chang.news.util;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.Toast;

public class HttpUtil {
	
	
	public static void getNewsJSON(final String url,final Handler handler,final Context context){
		if (isNetworkStatusOK(context)) {
			new Thread(new Runnable(){
				ProgressDialog progressDialog = ProgressDialog.show(context,"请稍等...", "数据正在加载中......", true);
				@Override
				public void run() {
					HttpURLConnection conn;
					InputStream is ;
					try {
						conn = (HttpURLConnection) new URL(url).openConnection();
						is = conn.getInputStream();
						BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
						String line = null;
						StringBuilder result = new StringBuilder();
						while((line = buffer.readLine()) != null){
							result.append(line);
						}
						buffer.close();
						//System.out.println(result);
						Message msg = new Message();
						msg.obj = result.toString();
						handler.sendMessage(msg);
						progressDialog.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}).start();
		} else {
			Toast.makeText(context, "当前网络不可用！", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void setImg(final ImageView ivPic,final String img_url){
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
				try {
					HttpURLConnection conn = (HttpURLConnection) new URL(img_url).openConnection();
					InputStream is = conn.getInputStream();
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					ivPic.setImageBitmap(bitmap);
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
//			}
//		}).start();
	}
	
	public static boolean isNetworkStatusOK(Context Context) {
		boolean netStatus = false;
		try {
			ConnectivityManager connectManager = (ConnectivityManager) 
					Context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectManager
					.getActiveNetworkInfo();
			if (activeNetworkInfo != null) {
				if (activeNetworkInfo.isAvailable()
						&& activeNetworkInfo.isConnected()) {
					netStatus = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return netStatus;
	}
	
}
