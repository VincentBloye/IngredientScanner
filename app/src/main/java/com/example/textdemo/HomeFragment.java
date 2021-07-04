package com.example.textdemo;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.example.textdemo.MainActivity.backgroundSelectId;
import static com.example.textdemo.MainActivity.textColourSelectId;
import static com.example.textdemo.MainActivity.textSizeSelectId;


public class HomeFragment extends Fragment {
    RelativeLayout homeLayout;
    Button btnSettings, btnInfo, btnScan;
    TextView bodyHome;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        //finds layout components
        homeLayout = (RelativeLayout) rootView.findViewById(R.id.homeLayout);
        btnScan = rootView.findViewById(R.id.btnScan);
        btnInfo = rootView.findViewById(R.id.btnInfo);
        btnSettings = rootView.findViewById(R.id.btnSettings);
        bodyHome = rootView.findViewById(R.id.bodyHome);
        bodyHome.setMovementMethod(new ScrollingMovementMethod()); //sets text as scrollable

        //call change fragment class on button click
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ChangeFragment changeFragment = new ChangeFragment(new ScanFragment(), getParentFragmentManager(), getView());
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment changeFragment = new ChangeFragment(new InfoFragment(), getParentFragmentManager(), getView());
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment changeFragment = new ChangeFragment(new SettingsFragment(), getParentFragmentManager(), getView());
            }
        });

        //updates layouts accessibility settings
        UpdateView updateView = new UpdateView(homeLayout, getActivity());
        updateView.setColourScheme();
        updateView.setTextSize();

        return rootView;
    }




}
