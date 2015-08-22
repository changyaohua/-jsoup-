package com.chang.news.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chang.news.model.NoticeBeam;
import com.imooc.tab03.R;

public class NoticeAdapter extends BaseAdapter
{
	private List<NoticeBeam> mList;
	private LayoutInflater mInflater;

	public NoticeAdapter(Context context, List<NoticeBeam> data)
	{
		mList = data;
		mInflater = LayoutInflater.from(context);
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
			convertView = mInflater.inflate(R.layout.item_notice, null);
			viewHolder.title = (TextView) convertView.findViewById(R.id.notice_title);
			viewHolder.time = (TextView) convertView.findViewById(R.id.notice_time);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.title.setText(mList.get(position).getTitle());
		viewHolder.time.setText(mList.get(position).getTime());
		return convertView;
	}

	class ViewHolder
	{
		public TextView title;
		public TextView time;
	}

}
