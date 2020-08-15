package com.qrcodereader.barcodereader.historyViewModel;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.qrcodereader.barcodereader.Model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HistoryViewModel extends AndroidViewModel {

    public static ArrayList<Model> list;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
    }

    public ArrayList<Model> loadData(){
        if(list==null){
            list=new ArrayList<Model>();
        }
        Gson gson = new Gson();
        SharedPreferences sharedPreferences= getApplication().getSharedPreferences("History",MODE_PRIVATE);
        String json = sharedPreferences.getString("historyList", "");
        if (json.isEmpty()) {
            //Toast.makeText(this,"There is something error",Toast.LENGTH_LONG).show();
        } else {
            Type type = new TypeToken<List<Model>>() {}.getType();
            List<Model> arrPackageData = gson.fromJson(json, type);
            int i=1;
            list.clear();
            for(Model data:arrPackageData) {
                data.setIndex(String.valueOf(i));
                list.add(data);
                i++;
            }
        }
        return (ArrayList<Model>)list;
    }


}
