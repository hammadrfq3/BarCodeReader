package com.qrcodereader.barcodereader;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qrcodereader.barcodereader.historyViewModel.HistoryViewModel;
import com.qrcodereader.barcodereader.historyViewModel.MFactory;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private ArrayList<Model> list;
    RecyclerView mRecyclerView;
    public static MyAdapter myAdapter;
    public static TextView notFoundTextView;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        historyViewModel= new ViewModelProvider(this, new MFactory(getActivity().getApplication())).get(HistoryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_history, container, false);
        mRecyclerView=view.findViewById(R.id.recyclerView);
        notFoundTextView=view.findViewById(R.id.notFoundTextView);
        notFoundTextView.setVisibility(View.GONE);

        if(list==null){
            list=new ArrayList<Model>(){};
        }
        list=historyViewModel.loadData();

        if(list!=null){
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            myAdapter=new MyAdapter(getActivity(),list);
            mRecyclerView.setAdapter(myAdapter);
        }

        if(list==null || list.size()<=0){
            notFoundTextView.setVisibility(View.VISIBLE);
        }

        return view;
    }
}