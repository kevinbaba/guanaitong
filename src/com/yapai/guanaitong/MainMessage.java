package com.yapai.guanaitong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yapai.guanaitong.db.MessageDb;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainMessage extends ListActivity {
	private final String TAG = "MainMesage";
	MessageDb db;
	private ListView mListView;
	private List<Map<String, Object>> mData;
	private MyAdapter mAdapter;
	private View foot;
	Button buttonMore;
	
	private final int RANK_NORAML = 0;
	private final int RANK_HIGH = 1;
	private final int RANK_URGENT = 2;

	private final int GET_DATAS_ONCETIME = 10;

	Cursor cursor;
	int idIndex;
	int rankIndex;
	int msgIndex;
	int timeIndex;
	
	void initCursor(){
		cursor = db.getCursor(null);
		idIndex=cursor.getColumnIndexOrThrow(MessageDb.ID);
		rankIndex=cursor.getColumnIndexOrThrow(MessageDb.RANK);
		msgIndex=cursor.getColumnIndexOrThrow(MessageDb.MSG);
		timeIndex=cursor.getColumnIndexOrThrow(MessageDb.TIME);
	}

	
	void initView(){
		/* 将footview的布局转换成View对象 */
		foot = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.main_message_foot, null, false);
		
		buttonMore = (Button) foot.findViewById(R.id.more);

		/* 给button添加点击事件监听器 */
		buttonMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
					int count = getData();
					if(count < GET_DATAS_ONCETIME){
						buttonMore.setClickable(false);
						buttonMore.setText("");
					}
					mAdapter.notifyDataSetChanged(); // 通知适配器重新适配
			}
		});
		
		/* 给listview添加footview */
		mListView.addFooterView(foot);

		//获取数据
		mData = new ArrayList<Map<String, Object>>();
		int count = getData();
		if(count < GET_DATAS_ONCETIME){
			buttonMore.setClickable(false);
			buttonMore.setText("");
		}
		mAdapter = new MyAdapter(this);

		/* listview添加适配器 */
		mListView.setAdapter(mAdapter);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_message);
		db = MessageDb.getDBInstanc(this);
		mListView = getListView();
		
		//////////TEST
//		for(int i=0;i<30;i++){
//			addData(i, 0, "消息....."+i, "20120517");
//		}
		//////////

		initCursor();
		initView();

	}
	
    // ListView 中某项被选中后的逻辑
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        showInfo(0, (Integer)mData.get(position).get("rank"), 
        		(String)mData.get(position).get("time"), 
        		(String)mData.get(position).get("msg"));
    }
     
    /**
     * listview中点击按键弹出对话框
     */
	public void showInfo(int status, int rank, String time, String msg) {
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        switch(rank){
        case RANK_NORAML:
    		dlg.setIcon(R.drawable.header);
        	break;
        case RANK_HIGH:
    		dlg.setIcon(R.drawable.header);
        	break;
        case RANK_URGENT:
    		dlg.setIcon(R.drawable.header);
        	break;
        }
		dlg.setTitle(time);
		dlg.setMessage(msg);
		dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		dlg.show();

	}
     
    public final class ViewHolder{
        public ImageView rank;
        public TextView msg;
        public TextView time;
    }
     
     
    public class MyAdapter extends BaseAdapter{
 
        private LayoutInflater mInflater;
         
         
        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }
 
        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }
 
        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
             
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();  
                convertView = mInflater.inflate(R.layout.main_message_list, null);
                holder.rank = (ImageView)convertView.findViewById(R.id.rank);
                holder.msg = (TextView)convertView.findViewById(R.id.msg);
                holder.time = (TextView)convertView.findViewById(R.id.time);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
            int rank = (Integer) mData.get(position).get("rank");
            switch(rank){
            case RANK_NORAML:
                holder.rank.setBackgroundResource(R.drawable.header);
            	break;
            case RANK_HIGH:
                holder.rank.setBackgroundResource(R.drawable.header);
            	break;
            case RANK_URGENT:
                holder.rank.setBackgroundResource(R.drawable.header);
            	break;
            }
            holder.msg.setText((String)mData.get(position).get("msg"));
            holder.time.setText((String)mData.get(position).get("time"));
             
            return convertView;
        }
         
    }

	private int getData() {
 
    	int id, rank;
    	String msg, time;
    	int count = 0;
    	try{
	    	if(cursor.getCount()>0){
	    		do{
	    			id=cursor.getInt(idIndex);
	    			rank=cursor.getInt(rankIndex);
	    			msg = cursor.getString(msgIndex);
	    			time = cursor.getString(timeIndex);
	
//	    	        Log.d(TAG, "id:"+id+" rank:"+rank+" msg:"+msg+" time:"+time);
	
	    	        Map<String, Object> map = new HashMap<String, Object>();
	    	        map.put("id", id);
	    	        map.put("rank", rank);
	    	        map.put("msg", msg);
	    	        map.put("time", time);
	    	        mData.add(map);
	    	        count++;
	        	}while(cursor.moveToNext() && count < GET_DATAS_ONCETIME);
	    	}
    	}catch (Exception e) {
			Log.d(TAG, e+"");
		}
        return count;
    }
	
	private long addData(int id,int rank, String msg, String time) {
        return db.insert(id, rank, msg, time);
    }
	
	@Override
	protected void onDestroy() {
		if(cursor != null)
			cursor.close();
		super.onDestroy();
	}

}
