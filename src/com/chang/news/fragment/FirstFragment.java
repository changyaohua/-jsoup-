package com.chang.news.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chang.news.ClientNewsActivity;
import com.chang.news.adapter.NoticeAdapter;
import com.chang.news.model.NoticeBeam;
import com.chang.news.util.RefreshableView;
import com.chang.news.util.RefreshableView.PullToRefreshListener;
import com.imooc.tab03.R;

public class FirstFragment extends Fragment implements OnItemClickListener {
	String urlPath;
	String urlNextPath;
	String urlContentPath;
	String type;
	
	ProgressBar progressBar;
	RefreshableView refreshableView;
	ListView listView;

	NoticeAdapter adapter;
	View view;

	boolean mHasLoadedOnce = false;

	String currUrlPath;

	List<NoticeBeam> noticeList;

	Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initUrlPath();
	}

	protected void initUrlPath() {
		// TODO Auto-generated method stub
		urlPath = "http://xsc.nuc.edu.cn/xwzx/zytz.htm";
		urlNextPath = "http://xsc.nuc.edu.cn/xwzx/zytz";
		urlContentPath = "http://xsc.nuc.edu.cn";
		type = "最要通知";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.layout_first, container, false);
		progressBar = (ProgressBar) view.findViewById(R.id.load_bar);
		refreshableView = (RefreshableView) view
				.findViewById(R.id.refreshable_view);
		listView = (ListView) view.findViewById(R.id.list_view);
		noticeList = new ArrayList<NoticeBeam>();

		this.listView.setOnItemClickListener(this);
		
		new LoadHtml().execute(urlPath);

		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {

//				try {
//					Thread.sleep(3000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

				List<NoticeBeam> tempList = getDataFromWeb(urlPath);

				String currDate = noticeList.get(0).getUrl();
				String newsDate = tempList.get(0).getUrl();

				if (currDate.equals(newsDate)) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getActivity(), "暂无新消息",
									Toast.LENGTH_SHORT).show();
						}
					});

				} else {
					List<NoticeBeam> mList = new ArrayList<NoticeBeam>();
					for (NoticeBeam newsBeam : tempList) {
						if (currDate.equals(newsBeam.getUrl())) {
							break;
						} else {
							mList.add(newsBeam);
						}
					}

					noticeList.addAll(0, mList);
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
				new Thread(new Runnable() {

					@Override
					public void run() {

						String nextUrl = getNextUrlPath(currUrlPath);
						currUrlPath = urlNextPath + nextUrl;
						List<NoticeBeam> mlist = getDataFromWeb(currUrlPath);
						if (mlist == null || nextUrl == null) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getActivity(), "已无更多消息",
											Toast.LENGTH_SHORT).show();
								}

							});
						} else {
							noticeList.addAll(mlist);
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

	public String getNextUrlPath(String url) {
		Document doc = null;
		String nextUrlPath = null;
		Document content = null;
		try {
			doc = Jsoup.connect(url).get();
			content = Jsoup.parse(doc.toString());
		} catch (Exception e) {
			return null;
		}
		Elements elements = content.getElementsByClass("Next");
		for (Element element : elements) {
			if (element.text().equals("下页")) {
				nextUrlPath = "/" + getIndexFromString(element.attr("href"))
						+ ".htm";
				Log.d("TAG", nextUrlPath);
				System.out.println(nextUrlPath);
				break;
			}
		}
		return nextUrlPath;
	}

	private String getIndexFromString(String str) {
		String index = null;
		Pattern p = Pattern.compile("(\\d+)");
		Matcher m = p.matcher(str);
		if (m.find()) {
			index = m.group();
		}
		return index;
	}

	public List<NoticeBeam> getDataFromWeb(String url) {

		Document doc = null;
		List<NoticeBeam> tempList = new ArrayList<NoticeBeam>();
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			return null;
		}
		Document content = Jsoup.parse(doc.toString());
		Elements elements = content.getElementsByClass("c44514");
		NoticeBeam notice;
		for (Element element : elements) {
			notice = new NoticeBeam();
			notice.setTitle(element.text());
			String time = element.parent().nextElementSibling().getElementsByClass("timestyle44514").text().replace("&nbsp;", "");
			notice.setTime(time);
			String contentUrl = urlContentPath
					+ element.attr("href").replace("..", "");
			notice.setUrl(contentUrl);
			tempList.add(notice);
		}
		return tempList;
	}

	class LoadHtml extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {

			currUrlPath = params[0];
			Document doc = null;
			try {
				doc = Jsoup.connect(currUrlPath).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Document content = Jsoup.parse(doc.toString());
			Elements elements = content.getElementsByClass("c44514");
			NoticeBeam notice;
			for (Element element : elements) {
				notice = new NoticeBeam();
				notice.setTitle(element.text());
				String time = element.parent().nextElementSibling().getElementsByClass("timestyle44514").text().replace("&nbsp;", "");
				notice.setTime(time);
				String contentUrl = urlContentPath
						+ element.attr("href").replace("..", "");
				notice.setUrl(contentUrl);
				noticeList.add(notice);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			adapter = new NoticeAdapter(getActivity(), noticeList);
			listView.setAdapter(adapter);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		NoticeBeam temp = noticeList.get(position);
		
		Intent intent= new Intent(getActivity(),ClientNewsActivity.class);
		NoticeBeam news = new NoticeBeam();
		news.setTitle(temp.getTitle());
		news.setTime("time");
		news.setUrl(temp.getUrl());
		intent.putExtra("news",news);
		intent.putExtra("type", type);
		startActivity(intent);
	}

}
