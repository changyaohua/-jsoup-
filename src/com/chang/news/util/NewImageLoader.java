package com.chang.news.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class NewImageLoader {
    private HashMap<String, SoftReference<Bitmap>> imageCache;
    
    public NewImageLoader() {
        imageCache = new HashMap<String, SoftReference<Bitmap>>();
    }
 
    public Bitmap loadDrawable(final String imageUrl,  final ImageCallback imageCallback) {  
        if (imageCache.containsKey(imageUrl)) {  
            SoftReference<Bitmap> softReference = imageCache.get(imageUrl);  
            Bitmap bitmap = softReference.get();  
            if (bitmap != null) {  
                return bitmap;  
            }  
        }  
        final Handler handler = new Handler() {  
            @Override  
            public void handleMessage(Message message) {  
                imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);  
            }  
        };  
        	 new Thread() {
                 @Override
                 public void run() {
                	 Bitmap bitmap = loadImageFromUrl(imageUrl);
                     imageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap));
                     Message message = handler.obtainMessage(0, bitmap);
                     handler.sendMessage(message);
                 }
             }.start();
			return null;
       
    }
 
    private  Bitmap loadImageFromUrl(String imageUrl) {
    	Bitmap bitmap = null;
		InputStream is = null;
		try
		{
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			is = new BufferedInputStream(conn.getInputStream());

			bitmap = BitmapFactory.decodeStream(is);
			conn.disconnect();
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				is.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return bitmap;
    }
    
   
    private Bitmap comp(Bitmap image) {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int width = newOpts.outWidth;
        int height = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float reqHeight = 250f;//这里设置高度为800f
        float reqWidth = 400f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (width > height && width > reqWidth) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / reqWidth);
        } else if (width < height && height > reqHeight) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / reqHeight);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
        return bitmap;
    }
    
    private Bitmap compressImage(Bitmap image) {
    	  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
    
    public interface ImageCallback {  
        public void imageLoaded(Bitmap imageDrawable, String imageUrl);  
    }  
 
}



