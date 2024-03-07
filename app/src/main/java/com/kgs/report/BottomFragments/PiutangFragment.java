package com.kgs.report.BottomFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.kgs.report.MainActivity;
import com.kgs.report.R;

public class PiutangFragment extends Fragment {
    String webUrlCart = "https://arachis.my.id/android/under-development.php";
    private ProgressBar progressBar;
    private FrameLayout webView_container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.piutang, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.card_inv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do something
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container,new InvoiceFragment());
                fr.addToBackStack(null);
                fr.commit();
                getFragmentManager().executePendingTransactions();
            }
        });
        view.findViewById(R.id.card_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container,new PayFragment());
                fr.addToBackStack(null);
                fr.commit();
                getFragmentManager().executePendingTransactions();

            }
        });
        view.findViewById(R.id.card_kartu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do something
                FragmentTransaction fr2 = getFragmentManager().beginTransaction();
                fr2.replace(R.id.fragment_container,new PiutangCardFragment());
                fr2.addToBackStack(null);
                fr2.commit();
                getFragmentManager().executePendingTransactions();
            }
        });
        view.findViewById(R.id.card_omset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do something
                FragmentTransaction fr2 = getFragmentManager().beginTransaction();
                fr2.replace(R.id.fragment_container,new SalesOmsetFragment());
                fr2.addToBackStack(null);
                fr2.commit();
                getFragmentManager().executePendingTransactions();
            }
        });
        view.findViewById(R.id.card_inv_brn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do something
                FragmentTransaction fr2 = getFragmentManager().beginTransaction();
                fr2.replace(R.id.fragment_container,new InvBrnFragment());
                fr2.addToBackStack(null);
                fr2.commit();
                getFragmentManager().executePendingTransactions();
            }
        });
        view.findViewById(R.id.card_kartu_brn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do something
                FragmentTransaction fr2 = getFragmentManager().beginTransaction();
                fr2.replace(R.id.fragment_container,new PiutangCardBrnFragment());
                fr2.addToBackStack(null);
                fr2.commit();
                getFragmentManager().executePendingTransactions();
            }
        });
        view.findViewById(R.id.card_pay_brn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do something
                FragmentTransaction fr2 = getFragmentManager().beginTransaction();
                fr2.replace(R.id.fragment_container,new PayBrnFragment());
                fr2.addToBackStack(null);
                fr2.commit();
                getFragmentManager().executePendingTransactions();
            }
        });


    }


}