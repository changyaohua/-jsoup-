package com.chang.news.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chang.news.BrowseNewsActivity;
import com.chang.news.adapter.NewsAdapter;
import com.chang.news.model.NewsBeam;
import com.chang.news.util.HttpUtil;
import com.chang.news.util.RefreshableView;
import com.chang.news.util.RefreshableView.PullToRefreshListener;
import com.imooc.tab03.R;

public class FourFragment extends Fragment implements OnItemClickListener
{
	private static final String urlPath = "http://apis.baidu.com/txapi/world/world";

	ProgressBar progressBar;
	RefreshableView refreshableView;
	ListView listView;

	List<NewsBeam> newsBeamsList;
	NewsAdapter adapter;
	View view;

	int initPage = 1;
	int currPage = 1;
	
	boolean mHasLoadedOnce;

	Handler handler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view =inflater.inflate(R.layout.layout_first, container, false);
		progressBar = (ProgressBar) view.findViewById(R.id.load_bar);
		refreshableView = (RefreshableView) view.findViewById(R.id.refreshable_view);
		listView = (ListView) view.findViewById(R.id.list_view);
		
		this.listView.setOnItemClickListener(this);

//		if(mHasLoadedOnce) {
        	new NewAsyncTask().execute(urlPath, "initPage");
//        } 

		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				List<NewsBeam> tempList = getJsonData(urlPath, "" + initPage);

				String currDate = newsBeamsList.get(0).newsTime;
				String newsDate = tempList.get(0).newsTime;
				if (currDate.equals(newsDate)) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getActivity(), "暂无新消息",
									Toast.LENGTH_SHORT).show();
						}
					});

				} else {
					List<NewsBeam> mList = new ArrayList<NewsBeam>();
					for (NewsBeam newsBeam : tempList) {
						if (currDate.equals(newsBeam.newsTime)) {
							break;
						} else {
							mList.add(newsBeam);
						}
					}
					newsBeamsList.addAll(0, mList);
					handler.post(new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
						}
					});
				}

				refreshableView.finishRefreshing();
			}

			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				currPage = currPage + 1;
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						List<NewsBeam> mlist = getJsonData(urlPath, ""
								+ currPage);
						if (mlist.size() == 0) {
							Toast.makeText(getActivity(), "已更多消息",
									Toast.LENGTH_SHORT).show();
						} else {
							newsBeamsList.addAll(mlist);
						}
						handler.post(new Runnable() {
							@Override
							public void run() {
								adapter.notifyDataSetChanged();
								refreshableView.loadComplete();
							}

						});
					}
				}).start();
			}
		}, 0);
		
		return view;
	}
	
//	@Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//         
//        if(this.isVisible()) {
//        	
//        	
//			if (isVisibleToUser && !mHasLoadedOnce) {
//        		mHasLoadedOnce = true;
//			}
//        } 
//        
//    }


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		NewsBeam news = newsBeamsList.get(position);
		Intent intent = new Intent(getActivity(), BrowseNewsActivity.class);
		intent.putExtra("content_url", news.newsUrl);
		startActivity(intent);
	}

	/**
	 * 从 URL 中获取数据
	 * 
	 * @param url
	 * @return
	 */
	public List<NewsBeam> getJsonData(String url, String page) {
		List<NewsBeam> tempList = new ArrayList<NewsBeam>();
		String httpUrl = url;
		String httpArg = "num=10&page=" + page;

		try {
			String jsonString = request(httpUrl, httpArg);

			JSONObject jsonObject;
			JSONObject childJsonObject;
			NewsBeam newsBeam;

			jsonObject = new JSONObject(jsonString);
			for (int i = 0; i < 10; i++) {
				childJsonObject = jsonObject.getJSONObject("" + i);
				newsBeam = new NewsBeam();

				newsBeam.newsTime = childJsonObject.getString("time");
				newsBeam.newsTitle = childJsonObject.getString("title");
				newsBeam.newsIconUrl = childJsonObject.optString("picUrl");
				newsBeam.newsContent = childJsonObject.getString("description");
				newsBeam.newsUrl = childJsonObject.getString("url");
				tempList.add(newsBeam);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return tempList;
	}

	/**
	 * @param urlAll
	 *            :请求接口
	 * @param httpArg
	 *            :参数
	 * @return 返回结果
	 */
	public String request(String httpUrl, String httpArg) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		httpUrl = httpUrl + "?" + httpArg;

		try {
			URL url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			// 填入apikey到HTTP header
			connection.setRequestProperty("apikey","eae754b531605317793b9e47a5fe34e1");
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 实现网络的异步访问
	 */
	class NewAsyncTask extends AsyncTask<String, Void, List<NewsBeam>> {
		// ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (!HttpUtil.isNetworkStatusOK(getActivity())) {
				// progressDialog =
				// ProgressDialog.show(MainActivity.this,"请稍等...",
				// "数据正在加载中......", true);
				progressBar.setVisibility(View.VISIBLE);
			} else {
				Toast.makeText(getActivity(), "当前网络不可用！",
						Toast.LENGTH_SHORT).show();
				this.cancel(true);
			}

		}

		@Override
		protected List<NewsBeam> doInBackground(String... params) {
			return getJsonData(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(List<NewsBeam> newsBeams) {
			super.onPostExecute(newsBeams);
			adapter = new NewsAdapter(getActivity(), newsBeams, listView);
			listView.setAdapter(adapter);
			progressBar.setVisibility(View.GONE);
			// progressDialog.dismiss();
		}

		/**
		 * 从 URL 中获取数据
		 * 
		 * @param url
		 * @return
		 */
		public List<NewsBeam> getJsonData(String url, String page) {
			newsBeamsList = new ArrayList<NewsBeam>();
			String httpUrl = url;
			String httpArg = "num=10&page=" + page;

			try {
				String jsonString = request(httpUrl, httpArg);

				JSONObject jsonObject;
				JSONObject childJsonObject;
				NewsBeam newsBeam;

				jsonObject = new JSONObject(jsonString);
				for (int i = 0; i < 10; i++) {
					childJsonObject = jsonObject.getJSONObject("" + i);
					newsBeam = new NewsBeam();
					newsBeam.newsTime = childJsonObject.getString("time");
					newsBeam.newsTitle = childJsonObject.getString("title");
					newsBeam.newsIconUrl = childJsonObject.optString("picUrl");
					newsBeam.newsContent = childJsonObject
							.getString("description");
					newsBeam.newsUrl = childJsonObject.getString("url");
					newsBeamsList.add(newsBeam);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return newsBeamsList;
		}

		/**
		 * 从 inpustStream 获取的信息
		 * 
		 * @param is
		 * @return
		 */
		public String readStream(InputStream is) {
			InputStreamReader isr;
			String result = "";
			try {
				isr = new InputStreamReader(is, "utf-8");
				BufferedReader br = new BufferedReader(isr);
				String line = "";
				while ((line = br.readLine()) != null) {
					result += line;
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		/**
		 * @param urlAll
		 *            :请求接口
		 * @param httpArg
		 *            :参数
		 * @return 返回结果
		 */
		public String request(String httpUrl, String httpArg) {
			BufferedReader reader = null;
			String result = null;
			StringBuffer sbf = new StringBuffer();
			httpUrl = httpUrl + "?" + httpArg;

			try {
				URL url = new URL(httpUrl);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("GET");
				// 填入apikey到HTTP header
				connection.setRequestProperty("apikey","eae754b531605317793b9e47a5fe34e1");
				connection.connect();
				InputStream is = connection.getInputStream();
				reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String strRead = null;
				while ((strRead = reader.readLine()) != null) {
					sbf.append(strRead);
					sbf.append("\r\n");
				}
				reader.close();
				result = sbf.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
	}
}
