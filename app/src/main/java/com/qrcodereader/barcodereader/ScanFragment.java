package com.qrcodereader.barcodereader;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.qrcodereader.barcodereader.historyViewModel.HistoryViewModel;
import com.qrcodereader.barcodereader.historyViewModel.MFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ScanFragment extends Fragment {

    public static TextView textView;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    public static Button searchBtn;
    public static CardView cardView;
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageButton imageGalleryBtn;
    private TextView decodedText;
    private HistoryViewModel historyViewModel;

    public ScanFragment() {
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
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        ImageButton scanBtn=view.findViewById(R.id.imageButtonScan);
        textView=view.findViewById(R.id.textView);
        searchBtn=view.findViewById(R.id.searchBtn);
        cardView=view.findViewById(R.id.cardView);
        imageGalleryBtn=view.findViewById(R.id.imageButtonGallery);
        cardView.setVisibility(View.GONE);

        imageGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),WebViewActivity.class);
                String searchableText;
                if(textView.getText().toString().contains("http") || textView.getText().toString().contains(".com")){
                    searchableText=textView.getText().toString();
                }else {
                    searchableText=MainActivity.url+textView.getText().toString();
                }
                intent.putExtra("URL",searchableText);
                cardView.setVisibility(View.GONE);
                MainActivity.itemToHide.setVisible(false);
                startActivity(intent);
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(getActivity().getApplicationContext(),ScanCodeActivity.class));
                } else {
                    // Request Camera Permission
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Bitmap converetdImage = getResizedBitmap(selectedImage, 300);
                Result result=decodeCodeFromGallery(converetdImage);
                historyViewModel=MyFunctions.Scan(result);
                saveData();
                Uri beepSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +getActivity().getApplicationContext().getPackageName()+"/"+R.raw.scanner_beeps_barcode_reader);
                Ringtone r = RingtoneManager.getRingtone(getActivity(), beepSound);
                r.play();
                if(MainActivity.interstitialAd.isAdLoaded()){
                    MainActivity.interstitialAd.show();
                }
                /*textView.setText(result);
                cardView.setVisibility(View.VISIBLE);*/
                //image_view.setImageBitmap(selectedImage);
            } catch (FileNotFoundException | FormatException | ChecksumException | NotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    public void saveData(){
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("History",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(historyViewModel.list);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("historyList",json);
        editor.apply();
    }

    public Result decodeCodeFromGallery(Bitmap bMap) throws FormatException, ChecksumException, NotFoundException {
        String contents = null;

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        Result result = reader.decode(bitmap);
        //contents = result.getText();
        return result;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}