package com.chang.news;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chang.news.model.NoticeBeam;
import com.imooc.tab03.R;

public class ClientNewsActivity extends Activity {
	private TextView noticeTitle;
	private TextView noticeAuthor;
	private TextView noticeTime;
	private TextView noticeContent;
	private ProgressBar loadContentBar;

	private String author;
	private String time;
	private Spanned desc;
	
	public Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client_news);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		initView();
		setTitle(getIntent().getStringExtra("type"));
		NoticeBeam news = (NoticeBeam) getIntent().getSerializableExtra("news");
		this.noticeTitle.setText(news.getTitle());
		String contentUrl = news.getUrl();
		new LoadHtml().execute(contentUrl);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
		}
		return true;
	}

	private void initView() {
		this.noticeTitle = (TextView) findViewById(R.id.noticeTitle);
		this.noticeAuthor = (TextView) findViewById(R.id.noticeAuthor);
		this.noticeTime = (TextView) findViewById(R.id.noticeTime);
		this.noticeContent = (TextView) findViewById(R.id.noticeContent);
		this.loadContentBar = (ProgressBar) findViewById(R.id.bar_content);

	}

	class LoadHtml extends AsyncTask<String, String, String> {
		Document doc;
		URLImageParser imageParser = new URLImageParser();

		@Override
		protected String doInBackground(String... params) {

			try {
				doc = Jsoup.connect(params[0]).get();
				Document content = Jsoup.parse(doc.toString());
				Element elementContent = content.getElementById("vsb_content");
				if (elementContent == null) {
					elementContent = content.getElementById("vsb_content_2");
				}
				String temp = elementContent.html();
				
				author = content.getElementsByClass("authorstyle44518").text();
				time = content.getElementsByClass("timestyle44518").text();
				desc = android.text.Html.fromHtml(temp, imageParser, null);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			loadContentBar.setVisibility(View.GONE);
			noticeAuthor.setText(author);
			noticeTime.setText(time);
			noticeContent.setText(desc);
			noticeContent.setMovementMethod(LinkMovementMethod.getInstance());
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			loadContentBar.setVisibility(View.VISIBLE);
		}

	}
	
    public class URLImageParser implements ImageGetter {  
        TextView mTextView;  
        Handler mHandler;
        
        public URLImageParser() {  
        }  
      
        public URLImageParser(TextView textView,Handler mHandler) {  
            this.mTextView = textView;  
            this.mHandler = mHandler;
        }  
      
        @Override  
        public Drawable getDrawable(String source) {  
        	String urlPath = "http://xsc.nuc.edu.cn" + source.replace("../../", "/");
			Drawable drawable = null;
			URL url;
			try {
				url = new URL(urlPath);
				drawable = Drawable.createFromStream(
						url.openStream(), ""); // 获取网路图片
			} catch (Exception e) {
				return null;
			}
			DisplayMetrics dm = getResources().getDisplayMetrics();  
			int dwidth = dm.widthPixels-10;//padding left + padding right
			int wid = dwidth;
			float dheight = (float)drawable.getIntrinsicHeight()*(float)dwidth/(float)drawable.getIntrinsicWidth();
			int dh = (int)(dheight+0.5);
			int hei = dh;
			drawable.setBounds(0, 0, wid, hei);
//			mHandler.post(new Runnable() {
//				
//				@Override
//				public void run() {
//					
//					mTextView.requestLayout();  
//		            mTextView.setText(mTextView.getText()); // 解决图文重叠  
//				}
//			});
			
            
			return drawable;
        }  
    }  
}
