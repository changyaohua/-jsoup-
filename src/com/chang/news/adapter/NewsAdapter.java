package com.chang.news.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chang.news.model.NewsBeam;
import com.chang.news.util.NewImageLoader;
import com.chang.news.util.NewImageLoader.ImageCallback;
import com.imooc.tab03.R;

public class NewsAdapter extends BaseAdapter
{
	private List<NewsBeam> mList;
	private LayoutInflater mInflater;

	public static String[] URLS;

	private NewImageLoader newImageLoader;
	ListView listView;

	public NewsAdapter(Context context, List<NewsBeam> data, ListView listView)
	{
		mList = data;
		this.listView = listView;
		mInflater = LayoutInflater.from(context);
		newImageLoader = new NewImageLoader();
		URLS = new String[data.size()];
		for(int i = 0; i < data.size(); i++)
		{
			URLS[i] = data.get(i).newsIconUrl;
		}
	}

	@Override
	public int getCount()
	{
		return mList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if(convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_layout, null);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.id_icon);
			viewHolder.title = (TextView) convertView.findViewById(R.id.id_title);
			viewHolder.content = (TextView) convertView.findViewById(R.id.id_content);
			viewHolder.time = (TextView) convertView.findViewById(R.id.id_time);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.icon.setImageResource(R.drawable.ic_launcher);
		String url = mList.get(position).newsIconUrl;
		if (url.equals("")) {
			viewHolder.icon.setVisibility(View.GONE);
		} else {
			viewHolder.icon.setVisibility(View.VISIBLE);
			viewHolder.icon.setTag(url);
			
			Bitmap cacheDrawable = newImageLoader.loadDrawable(url, new ImageCallback(){

				@Override
				public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
					// TODO Auto-generated method stub
					 ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);  
		                if (imageViewByTag != null) {  
		                    imageViewByTag.setImageBitmap(imageDrawable);  
		                }  
				}
				
			});
			
			viewHolder.icon.setImageBitmap(cacheDrawable);
		}
		
		viewHolder.title.setText(mList.get(position).newsTitle);
		viewHolder.content.setText(mList.get(position).newsContent);
		viewHolder.time.setText(mList.get(position).newsTime);
		return convertView;
	}

	class ViewHolder
	{
		public TextView title;
		public TextView content;
		public TextView time;
		public ImageView icon;
	}

}
