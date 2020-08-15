package com.qrcodereader.barcodereader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.qrcodereader.barcodereader.historyViewModel.HistoryViewModel;
import com.qrcodereader.barcodereader.historyViewModel.MFactory;
import com.google.gson.Gson;
import com.google.zxing.Result;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;
    public static HistoryViewModel historyViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);
        historyViewModel= new ViewModelProvider(this, new MFactory(getApplication())).get(HistoryViewModel.class);
        setTitle("Scanning QR/Barcode");
    }

    @Override
    public void handleResult(Result result) {
        historyViewModel=MyFunctions.Scan(result);
        saveData();
        Uri beepSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +getApplicationContext().getPackageName()+"/"+R.raw.scanner_beeps_barcode_reader);
        Ringtone r = RingtoneManager.getRingtone(this, beepSound);
        r.play();
        if(MainActivity.interstitialAd.isAdLoaded()){
            MainActivity.interstitialAd.show();
        }
        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        scannerView.stopCamera();
    }

    public void saveData(){
        SharedPreferences sharedPreferences=getSharedPreferences("History",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(historyViewModel.list);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("historyList",json);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();

    }

}