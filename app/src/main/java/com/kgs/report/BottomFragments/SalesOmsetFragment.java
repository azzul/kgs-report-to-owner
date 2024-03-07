package com.kgs.report.BottomFragments;

import static android.content.Context.DOWNLOAD_SERVICE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.kgs.report.MainActivity.baseurl;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kgs.report.R;
import com.kgs.report.webapp.WebAppInterface;


public class SalesOmsetFragment extends Fragment {

    String webUrlBrnP = baseurl + "android/sales_omset.php";
    private ProgressBar progressBar;

    private FrameLayout webView_container;
    public static WebView webViewSlsOmset;
    public static String Inv = "true";
    public static String FRAGMENT_TAG = "FRAGEMEN";
    OnComplete onComplete;
   Button preview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sales_omset, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress_bar);
        // Find the FrameLayout and set it to mLayout
        webView_container = view.findViewById(R.id.webView_container_id);
        //initialize webView - JavaScript Enable - load url
        webViewSlsOmset = view.findViewById(R.id.webViewId);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#5ec7ff"),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
        webView_container.setAlpha(0.5f);

        webViewSlsOmset.loadUrl(webUrlBrnP);
        webViewSlsOmset.getSettings().setJavaScriptEnabled(true);
        webViewSlsOmset.getSettings().setBuiltInZoomControls(true);
        webViewSlsOmset.getSettings().setDisplayZoomControls(false);
        WebSettings webSettings = webViewSlsOmset.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webViewSlsOmset.addJavascriptInterface(new WebAppInterface(getActivity()),  "Android");

        //fungsi untuk download-lempar ke browser
        webViewSlsOmset.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                downloadDialog(url,userAgent,contentDisposition,mimetype);
            }

        });
        onComplete = new OnComplete();
        webViewSlsOmset.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && webViewSlsOmset.canGoBack()) {
                    webViewSlsOmset.goBack();
                    return true;
                }

                return false;
            }

        });
        webViewSlsOmset.setWebViewClient(new WebViewClient() {
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


    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface   // must be added for API 17 or higher
        public void showToast(String text) {
            //Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
            Log.e("data", text);

            //new DisplayProgressBar().execute(text);
            PreviewSalesOmsetFragment frag = new PreviewSalesOmsetFragment();
            Bundle b = new Bundle();
// put stuff into bundle...
            b.putString("url", text);

// Pass the bundle to the Fragment
            frag.setArguments(b);

// Use Fragment Transaction
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, frag, FRAGMENT_TAG);
            ft.addToBackStack(null);
            ft.commit();
        }
        @JavascriptInterface   // must be added for API 17 or higher
        public void downloadPdf(String text) {
            //Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
            Log.e("data", text);
            progressBar.setVisibility(View.VISIBLE);
            webView_container.setAlpha(0.5f);
        }

    }
    private class DisplayProgressBar extends AsyncTask<String, Void, String> {

        /**
         * This method displays progress bar in UI thread.
         */

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(getActivity());

            progress.setMessage("Your message");
            progress.show();
            progressBar.setVisibility(View.VISIBLE);
            webView_container.setAlpha(0.5f);
            Log.d("Runningman: ", "Started running");
            //this method will be running on UI thread

            super.onPreExecute();
        }

        /**
         * This method executes your bot calculation in background thread.
         */
        @Override
        protected String doInBackground(String... strings) {

            //  put your bot calculation code here
            String s = strings[0];
            webViewSlsOmset.loadUrl(baseurl+s);
            webViewSlsOmset.setWebViewClient(new WebViewClient() {
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
                    progress.dismiss();
                }
            });

            String urlExe = baseurl+s;
            Log.e("data", urlExe);
            return urlExe;
        }

        /**
         * This method removes progress bar from UI thread when calculation is over.
         */
        @Override
        protected void onPostExecute(String urlExce) {
            progress.dismiss();
            progressBar.setVisibility(View.INVISIBLE);
            Log.e("POST DATA", urlExce);
            super.onPostExecute(urlExce);

        }
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
                progressBar.setVisibility(View.GONE);
                webView_container.setAlpha(1.0f);
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
