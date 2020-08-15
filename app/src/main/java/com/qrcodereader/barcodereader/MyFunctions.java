package com.qrcodereader.barcodereader;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.zxing.Result;
import com.qrcodereader.barcodereader.historyViewModel.HistoryViewModel;
import com.qrcodereader.barcodereader.historyViewModel.MFactory;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;

public class MyFunctions {

    private static ArrayList<Model> modelArrayList;
    private static HistoryViewModel historyViewModel;

    public static HistoryViewModel Scan(Result result){
        ScanFragment.searchBtn.setVisibility(View.VISIBLE);
        ScanFragment.cardView.setVisibility(View.VISIBLE);
        MainActivity.itemToHide.setVisible(true);
        if(modelArrayList==null){
            modelArrayList=new ArrayList<Model>();
        }
        MainActivity.historyViewModel.loadData();
        Model model=new Model();
        model.setContent(result.toString());
        model.setHeading(result.getBarcodeFormat().name());
        model.setIndex(String.valueOf(MainActivity.historyViewModel.list.size()+1));
        MainActivity.historyViewModel.list.add(model);
        if(HistoryFragment.myAdapter.models!=null || HistoryFragment.myAdapter.models.size()>0){
            HistoryFragment.notFoundTextView.setVisibility(View.GONE);
            //MainActivity.deleteHistoryItem.setVisible(true);
        }
        HistoryFragment.myAdapter.notifyDataSetChanged();
        ScanFragment.textView.setText(result.toString());

        return MainActivity.historyViewModel;
    }

}
