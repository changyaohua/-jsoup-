package com.chang.news;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.imooc.tab03.R;

public class BrowseNewsActivity extends Activity {

	private WebView webView;
	private ProgressBar bar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("国际新闻");  
		setContentView(R.layout.activity_browse_news);
		ActionBar actionBar = getActionBar();  
		actionBar.setDisplayHomeAsUpEnabled(true);
		webView = (WebView) findViewById(R.id.webView);
		bar = (ProgressBar) findViewById(R.id.progress_bar);
		
		String content_url = getIntent().getStringExtra("content_url");
		
		 webView.setWebChromeClient(new WebChromeClient(){
			 @Override
	          public void onProgressChanged(WebView view, int newProgress) {
	              if (newProgress == 100) {
	                  bar.setVisibility(View.INVISIBLE);
	              } else {
	                  if (View.INVISIBLE == bar.getVisibility()) {
	                      bar.setVisibility(View.VISIBLE);
	                  }
	                  bar.setProgress(newProgress);
	              }
	              super.onProgressChanged(view, newProgress);
	          }
		 });
		 
		 webView.setWebViewClient(new WebViewClient() {
			 @Override  
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {  
	                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面  
	                webView.loadUrl(url);  
	                // 消耗掉这个事件。Android中返回True的即到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉  
	                return true;  
	            }  
			 });
		webView.loadUrl(content_url);
//		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		
	}
	
	@Override  
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	    case android.R.id.home:  
	        finish();  
	    }  
	    return true;
	}
}
