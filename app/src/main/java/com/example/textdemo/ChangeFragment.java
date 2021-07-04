package com.example.textdemo;

import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ChangeFragment {

    public  ChangeFragment(Fragment fragment, FragmentManager fragmentManager, View view){
            //replaces current fragment with passed fragment
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(((ViewGroup) view.getParent()).getId(), fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }


}
