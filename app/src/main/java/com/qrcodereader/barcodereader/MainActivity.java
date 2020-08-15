package com.qrcodereader.barcodereader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.facebook.ads.*;

import com.google.android.material.tabs.TabLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.qrcodereader.barcodereader.historyViewModel.HistoryViewModel;
import com.qrcodereader.barcodereader.historyViewModel.MFactory;

public class MainActivity extends AppCompatActivity {

    public static TextView textView;
    private AdView adView;
    private Toast backToast;
    public static  WebView display;
    public static String url = "http://www.google.com/#q=";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static MenuItem itemToHide;
    public static MenuItem deleteHistoryItem;
    private long backPressedTime;
    private Context context;
    // Camera Permission Request Code
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    public static HistoryViewModel historyViewModel;
    public static InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        historyViewModel= new ViewModelProvider(this, new MFactory(getApplication())).get(HistoryViewModel.class);
        context=MainActivity.this;

        AudienceNetworkAds.initialize(this);

        adView = new AdView(this, "656464118411956_656498615075173", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();

        // Interstitial Ad

        interstitialAd = new InterstitialAd(this, "656464118411956_670140153711019");
        // Set listeners for the Interstitial Ad
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                interstitialAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                //Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                // Show the ad
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();

        toolbar=(Toolbar) findViewById(R.id.myToolbar);
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        viewPager=(ViewPager)findViewById(R.id.myPager);

        setSupportActionBar(toolbar);
        setupViewAdapter(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                if(position==1){
                    getSupportActionBar().setTitle("History");
                }else {
                    getSupportActionBar().setTitle("QR & Barcode Reader");
                }

                if(position==1 && HistoryFragment.myAdapter.models.size()>0){
                    deleteHistoryItem.setVisible(true);
                }else {
                    deleteHistoryItem.setVisible(false);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                // Check Camera permission is granted or not
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Camera  permission granted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),ScanCodeActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Camera  permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if(backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            finish();
        }else {
            backToast=Toast.makeText(this,"Press Back again to exit",Toast.LENGTH_SHORT);
            backToast.show();

        }
        backPressedTime=System.currentTimeMillis();
    }

    private void setupViewAdapter(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ScanFragment(),"Scan");
        viewPagerAdapter.addFragment(new HistoryFragment(),"History");
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        itemToHide = menu.findItem(R.id.shareBtn);
        deleteHistoryItem=menu.findItem(R.id.deleteBtn);
        itemToHide.setVisible(false);
        deleteHistoryItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    private AlertDialog AskOption()
    {
        final AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete history?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        context.getSharedPreferences("History",0).edit().clear().apply();
                        HistoryFragment.myAdapter.models.clear();
                        HistoryFragment.myAdapter.notifyDataSetChanged();
                        HistoryFragment.notFoundTextView.setVisibility(View.VISIBLE);
                        deleteHistoryItem.setVisible(false);
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        myQuittingDialogBox.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button negButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0,0,10,0);
                negButton.setLayoutParams(params);
            }
        });

        return myQuittingDialogBox;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.deleteBtn:
                AlertDialog diaBox = AskOption();
                diaBox.show();
                break;
            case R.id.shareBtn:
                shareCode();
        }

        return super.onOptionsItemSelected(item);
    }

    public void shareCode(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareTitle="QR/Barcode";
        String shareContent=ScanFragment.textView.getText().toString();

        intent.putExtra(Intent.EXTRA_TEXT,shareContent);
        intent.putExtra(Intent.EXTRA_SUBJECT,shareTitle);
        startActivity(Intent.createChooser(intent,"Share via"));
    }
}