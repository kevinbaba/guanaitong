package com.yapai.guanaitong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainMessage extends ListActivity {
	private final String TAG = "MainMesage";
	private ListView mListView;
	private TextView mUnread;
	private TextView mReaded;
	private List<Map<String, Object>> mItems_unRead;
	private List<Map<String, Object>> mItems_readed;
	private List<Map<String, Object>> mData;
	private MyAdapter mAdapter_unRead;
	private MyAdapter mAdapter_readed;
	private final int READED = 0;
	private final int UNREAD = 1;
	private int mCurrentIsReadedOrUnread = UNREAD;
	private View foot;

	private int itemsCount = 10; // ����ӵ����ݸ���

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_message);
		mListView = (ListView) findViewById(R.id.listView);
		mUnread = (TextView)findViewById(R.id.unread);
		mReaded = (TextView)findViewById(R.id.readed);
		
		mUnread.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mCurrentIsReadedOrUnread == UNREAD) return;
				Log.d(TAG, "mUnread clicked");
				mUnread.setBackgroundResource(R.drawable.home_btn_bg_d);
				mReaded.setBackgroundResource(0);
				mListView.setAdapter(mAdapter_unRead);
				mCurrentIsReadedOrUnread = UNREAD;
			}
		});
		
		mReaded.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mCurrentIsReadedOrUnread == READED) return;
				Log.d(TAG, "mReaded clicked");
				mUnread.setBackgroundResource(0);
				mReaded.setBackgroundResource(R.drawable.home_btn_bg_d);
				mListView.setAdapter(mAdapter_readed);
				mCurrentIsReadedOrUnread = READED;
			}
		});

		//TODO ��ȡ����
		mItems_unRead = getData();
		mItems_readed = getData();
		mData = mItems_unRead;

		/* ��footview�Ĳ���ת����View���� */
		foot = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.main_message_foot, null, false);

		/* ��footview��ӵ���¼������� */
		foot.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mCurrentIsReadedOrUnread == UNREAD){
					mItems_unRead = getData();
					mAdapter_unRead.notifyDataSetChanged(); // ֪ͨ��������������
				}else if(mCurrentIsReadedOrUnread == READED){
					mItems_readed = getData();
					mAdapter_readed.notifyDataSetChanged(); // ֪ͨ��������������
				}
			}
		});
		
		foot.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				float y = event.getY();
//				Log.d(TAG,"y:"+y+" top:"+v.getTop()+" bo:"+v.getBottom());
				if(y > v.getBottom() - v.getTop())
					foot.setBackgroundResource(0);
				int action = event.getAction();
				if(action == MotionEvent.ACTION_DOWN)
					foot.setBackgroundResource(R.drawable.home_btn_bg_s);
				else if(action == MotionEvent.ACTION_UP)
					foot.setBackgroundResource(0);
				return false;
			}
		});
		
		/* ��listview���footview */
		mListView.addFooterView(foot);

		mAdapter_unRead = new MyAdapter(this);
		mAdapter_readed = new MyAdapter(this);

		/* listview��������� */
		mListView.setAdapter(mAdapter_unRead);

	}
	
    // ListView ��ĳ�ѡ�к���߼�
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
         
        Log.v(TAG, (String)mData.get(position).get("title"));
    }
     
    /**
     * listview�е�����������Ի���
     */
    public void showInfo(){
        new AlertDialog.Builder(this)
        .setTitle("�ҵ�listview")
        .setMessage("����...")
        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
         
    }
     
     
     
    public final class ViewHolder{
        public ImageView img;
        public TextView msg;
        public TextView arrow;
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
                holder.img = (ImageView)convertView.findViewById(R.id.img);
                holder.msg = (TextView)convertView.findViewById(R.id.msg);
                holder.arrow = (TextView)convertView.findViewById(R.id.arrow);
                convertView.setTag(holder);
                 
            }else {
                 
                holder = (ViewHolder)convertView.getTag();
            }
             
             
            holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));
            holder.msg.setText((String)mData.get(position).get("msg"));
            holder.arrow.setText((String)mData.get(position).get("arrow"));
             
            holder.arrow.setOnClickListener(new View.OnClickListener() {
                 
                @Override
                public void onClick(View v) {
                    showInfo();                 
                }
            });
             
             
            return convertView;
        }
         
    }

	private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
 
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("msg", "111");
        map.put("img", R.drawable.header);
        list.add(map);
 
        map = new HashMap<String, Object>();
        map.put("msg", "222");
        map.put("img", R.drawable.header);
        list.add(map);
 
        map = new HashMap<String, Object>();
        map.put("msg", "333");
        map.put("img", R.drawable.header);
        list.add(map);
         
        return list;
    }

}
