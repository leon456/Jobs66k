package com.leon456.jobs66k;



import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leon456.jobs66k.view.MyListView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.SimpleFormatter;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class JobListFragment extends Fragment implements OnDetectScrollListener{


    private static final String TAG = "JobListFragment";
    private MyListView listView;
    private LayoutInflater mInflater;
    private List<HashMap<String,String>> mDatas;
    private JobListAdapter mAdapter;
    private MainActivityInterface mListener;


    public void setmDatas(List<HashMap<String, String>> mDatas) {
        this.mDatas = mDatas;
    }


    public JobListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;

        View view = inflater.inflate(R.layout.fragment_job_list, container, false);
        listView = (MyListView)view.findViewById(R.id.listView);
        listView.setOnDetectScrollListener(this);
        listView.setAdapter(mAdapter = new JobListAdapter());
        mAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onUpScrolling() {
        //Toast.makeText(getActivity(),"Up",Toast.LENGTH_LONG).show();
        //getActivity().getActionBar().hide();
    }

    @Override
    public void onDownScrolling() {
        //Toast.makeText(getActivity(),"Down",Toast.LENGTH_LONG).show();
        //getActivity().getActionBar().show();
    }

    class JobListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.jobs_list_item,null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.detail(mDatas.get(position).get("link"));
                }
            });
            HashMap<String,String> data =  mDatas.get(position);

            Log.i(TAG,"data:"+data);
            String title = data.get("title");

            if(!title.trim().contains("NEW")) {
                ((Button) view.findViewById(R.id.newImageView)).setText("");
                ((Button) view.findViewById(R.id.newImageView)).setBackgroundColor(Color.TRANSPARENT);
            }

            ((TextView)view.findViewById(R.id.titleTextView)).setText(title.replaceAll("NEW", ""));


            ((TextView)view.findViewById(R.id.jobTextView)).setText(data.get("job"));


             String color = data.get("color");
            if(color.equals("1")){
                ((TextView)view.findViewById(R.id.jobTextView)).setBackgroundResource(R.drawable.label_success);
            }else  if(color.equals("2")){
                ((TextView)view.findViewById(R.id.jobTextView)).setBackgroundResource (R.drawable.label_danger);
            }else{
                ((TextView)view.findViewById(R.id.jobTextView)).setBackgroundResource (R.drawable.label_default);
            }


            ((TextView)view.findViewById(R.id.dateTextView)).setText(data.get("date"));

            return view;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MainActivityInterface) activity;
        } catch (ClassCastException e) {
            mListener = null;
        }
    }
}
