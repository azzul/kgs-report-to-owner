package com.kgs.report.BottomFragments;

import static android.content.Context.DOWNLOAD_SERVICE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.kgs.report.MainActivity.baseurl;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kgs.report.R;


public class PayBrnFragment extends Fragment {
    String webUrlPayBrn= baseurl+"android/pembayaran_piutang_brn.php";
    private ProgressBar progressBar;
    private FrameLayout webView_container;
    public static WebView webViewPayBrn;

    public static String payInv="true";
    OnComplete onComplete;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pay_brn, container, false);

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize webView - JavaScript Enable - load url
        webViewPayBrn = view.findViewById(R.id.webViewId);
        webViewPayBrn.getSettings().setJavaScriptEnabled(true);
        webViewPayBrn.getSettings().setBuiltInZoomControls(true);
        webViewPayBrn.getSettings().setDisplayZoomControls(false);
        webViewPayBrn.loadUrl(webUrlPayBrn);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#5ec7ff"),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        // Find the FrameLayout and set it to mLayout
        webView_container = view.findViewById(R.id.webView_container_id);
        progressBar.setVisibility(View.VISIBLE);
        webView_container.setAlpha(0.5f);
        //download function
        webViewPayBrn.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                downloadDialog(url,userAgent,contentDisposition,mimetype);
            }
        });
        onComplete = new OnComplete();
        webViewPayBrn.setOnKeyListener(new View.OnKeyListener(){

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && webViewPayBrn.canGoBack()) {
                    webViewPayBrn.goBack();
                    return true;
                }

                return false;
            }

        });
        webViewPayBrn.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Show the progress bar when the page starts loading
                progressBar.setVisibility(View.VISIBLE);
                webView_container.setAlpha(0.5f);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                // Hide the progress bar when the page finishes loading
                progressBar.setVisibility(View.GONE);
                webView_container.setAlpha(1.0f);
            }
        });

    }
    public void downloadDialog(final String url,final String userAgent,String contentDisposition,String mimetype)
    {
        //getting filename from url.
        final String filename = URLUtil.guessFileName(url,contentDisposition,mimetype);
        //alertdialog
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        //title of alertdialog
        builder.setTitle("Konfirmasi Pengunduhan");
        //message of alertdialog
        builder.setMessage("Klik Ya untuk mengunduh file " +filename);
        //if Yes button clicks.
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimetype);
                //------------------------COOKIE!!------------------------
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                //------------------------COOKIE!!------------------------
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Kgs Report App");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);
                getContext().registerReceiver(onCompleted, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                //download enqued
                downloadManager.enqueue(request);
                Toast.makeText(getContext(), "Sedang Mendownload File", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                webView_container.setAlpha(0.5f);
            }

        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //cancel the dialog if Cancel clicks
                dialog.cancel();
            }

        });
        //alertdialog shows.
        builder.create().show();

    }
    BroadcastReceiver onCompleted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.GONE);
            webView_container.setAlpha(1.0f);
            Toast.makeText(context.getApplicationContext(), "Download Finish", Toast.LENGTH_SHORT).show();
        }
    };
    private static final int STORAGE_PERMISSION_CODE = 23;
    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            try {
                Log.d(TAG, "requestPermission: try");

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            }
            catch (Exception e){
                Log.e(TAG, "requestPermission: catch", e);
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        }
        else {
            //Android is below 11(R)
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE
            );
        }
    }

    private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");
                    //here we will handle the result of our intent
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                        //Android is 11(R) or above
                        if (Environment.isExternalStorageManager()){
                            //Manage External Storage Permission is granted
                            Log.d(TAG, "onActivityResult: Manage External Storage Permission is granted");

                        }
                        else{
                            //Manage External Storage Permission is denied
                            Log.d(TAG, "onActivityResult: Manage External Storage Permission is denied");
                            //Toast.makeText(getContext(), "Manage External Storage Permission is denied", Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setTitle("Perizinan Aplikasi");
                            alert.setMessage("Mohon izinkan akses penyimpanan agar dapat download file dari server");
                            alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermission();
                                    dialog.dismiss();
                                }
                            });

                            alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(), "Anda menolak izin penyimpanan dan akan gagal ketika download file laporan", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });

                            alert.show();
                        }
                    }
                    else {
                        //Android is below 11(R)
                    }
                }
            }
    );

    public boolean checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            return Environment.isExternalStorageManager();
        }
        else{
            //Android is below 11(R)
            int write = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    /*Handle permission request results*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0){
                //check each permission if granted or not
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (write && read){
                    //External Storage permissions granted
                    Log.d(TAG, "onRequestPermissionsResult: External Storage permissions granted");

                }
                else{
                    //External Storage permission denied
                    Log.d(TAG, "onRequestPermissionsResult: External Storage permission denied");
                    Toast.makeText(getContext(), "Anda menolak izin penyimpanan dan akan gagal ketika download file laporan", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Perizinan Aplikasi");
                    alert.setMessage("Mohon izinkan akses penyimpanan agar dapat download file dari server");
                    alert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermission();
                            dialog.dismiss();
                        }
                    });

                    alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(), "Anda menolak izin penyimpanan dan akan gagal ketika download file laporan", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });

                    alert.show();
                }
            }
        }
    }
}