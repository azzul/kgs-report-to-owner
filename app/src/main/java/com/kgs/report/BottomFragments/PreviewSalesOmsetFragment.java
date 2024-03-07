package com.kgs.report.BottomFragments;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.kgs.report.MainActivity.baseurl;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Environment;
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


public class PreviewSalesOmsetFragment extends Fragment {

    private ProgressBar progressBar;
    private FrameLayout webView_container_id;
    public static WebView webViewPreview;

    public static String payInv="true";
    OnComplete onComplete ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preview_sales_omset, container, false);

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView_container_id = view.findViewById(R.id.webView_container_id);
        String url = getArguments().getString("url");
        String webUrlPreview = baseurl + url;
        Log.e("Urlnya", webUrlPreview);
        //initialize webView - JavaScript Enable - load url
        webViewPreview = view.findViewById(R.id.webViewId);
        webViewPreview.getSettings().setJavaScriptEnabled(true);
        webViewPreview.getSettings().setBuiltInZoomControls(true);
        webViewPreview.getSettings().setDisplayZoomControls(false);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#5ec7ff"),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
        webView_container_id.setAlpha(0.5f);
        webViewPreview.loadUrl(webUrlPreview);


        // Find the FrameLayout and set it to mLayout

        //download function
        webViewPreview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                downloadDialog(url,userAgent,contentDisposition,mimetype);
            }
        });
        onComplete = new OnComplete();


        webViewPreview.setFocusableInTouchMode(true);
        webViewPreview.requestFocus();
        webViewPreview.setOnKeyListener(new View.OnKeyListener(){

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && webViewPreview.canGoBack()) {
                    webViewPreview.goBack();
                    return true;
                }

                return false;
            }

        });
        webViewPreview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Show the progress bar when the page starts loading

            }
            @Override
            public void onPageFinished(WebView view, String url) {
                // Hide the progress bar when the page finishes loading
                progressBar.setVisibility(View.GONE);
                webView_container_id.setAlpha(1.0f);
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
                webView_container_id.setAlpha(0.5f);
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
            webView_container_id.setAlpha(1.0f);
            Toast.makeText(context.getApplicationContext(), "Proses Download selesai silahkan buka file di folder download anda", Toast.LENGTH_LONG).show();
        }
    };

}