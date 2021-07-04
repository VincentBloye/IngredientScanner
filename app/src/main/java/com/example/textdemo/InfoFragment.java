package com.example.textdemo;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import static com.example.textdemo.MainActivity.backgroundSelectId;
import static com.example.textdemo.MainActivity.textColourSelectId;
import static com.example.textdemo.MainActivity.textSizeSelectId;
import static com.example.textdemo.MainActivity.viewCustom;

public class InfoFragment extends Fragment {

    TextView headerHowItWorks, headerTypes, bodyHowItWorks, bodyTypes;
    MaterialButton btnViewIngredients, btnViewCustom;
    RelativeLayout infoLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        headerHowItWorks = rootView.findViewById(R.id.headerHowItWorks);
        headerTypes = rootView.findViewById(R.id.headerTypes);
        bodyHowItWorks = rootView.findViewById(R.id.bodyHowItWorks);
        bodyTypes = rootView.findViewById(R.id.bodyTypes);
        infoLayout = (RelativeLayout) rootView.findViewById(R.id.infoLayout);

        UpdateView updateView = new UpdateView(infoLayout, getActivity());
        updateView.setColourScheme();
        updateView.setTextSize();

        //transitions to ingredients list fragment
        btnViewIngredients = rootView.findViewById(R.id.btnViewIngredients);
        btnViewIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCustom = false;
                ChangeFragment changeFragment = new ChangeFragment(new IngredientsListFragment(), getParentFragmentManager(), getView());
            }
        });

        btnViewCustom = rootView.findViewById(R.id.btnViewCustom);
        btnViewCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCustom = true;
                ChangeFragment changeFragment = new ChangeFragment(new IngredientsListFragment(), getParentFragmentManager(), getView());
            }
        });

        return rootView;
    }



}