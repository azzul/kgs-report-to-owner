package com.kgs.report.BottomFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.kgs.report.R;


public class SalesOrderFragment extends Fragment {
    private ProgressBar progressBar;
    private FrameLayout webView_container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sales_order, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.card_report_so).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do something
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container,new ReportSOFragment());
                fr.addToBackStack(null);
                fr.commit();
                getFragmentManager().executePendingTransactions();
            }
        });
        view.findViewById(R.id.card_list_so).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container,new SoFragment());
                fr.addToBackStack(null);
                fr.commit();
                getFragmentManager().executePendingTransactions();

            }
        });
        view.findViewById(R.id.card_report_brn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container,new ReportSoBrnFragment());
                fr.addToBackStack(null);
                fr.commit();
                getFragmentManager().executePendingTransactions();

            }
        });
        view.findViewById(R.id.card_list_so_brn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container,new SoBrnFragment());
                fr.addToBackStack(null);
                fr.commit();
                getFragmentManager().executePendingTransactions();

            }
        });

    }
}