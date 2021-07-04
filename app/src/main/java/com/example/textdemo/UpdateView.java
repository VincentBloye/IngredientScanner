package com.example.textdemo;

import android.app.Activity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import static com.example.textdemo.MainActivity.backgroundSelectId;
import static com.example.textdemo.MainActivity.textColourSelectId;
import static com.example.textdemo.MainActivity.textSizeSelectId;

public class UpdateView {

    ViewGroup parent;
    Activity activity;

    public UpdateView(ViewGroup parent1, Activity activity1) {
        this.parent = parent1;
        this.activity = activity1;
    }

    public void setColourScheme() {
        //changes background colour to selected choice
        if (backgroundSelectId == 0) {
            parent.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorBackgroundWhite));
        } else if (backgroundSelectId == 1) {
            parent.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorBackgroundGrey));
        } else if (backgroundSelectId == 2) {
            parent.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorBackgroundYellow));
        }
        //changes text colour to selected choice
        if (textColourSelectId == 0) {
            changeTextColor(parent, ContextCompat.getColor(activity, R.color.colorTextBlack));
        } else if (textColourSelectId == 1) {
            changeTextColor(parent, ContextCompat.getColor(activity, R.color.colorTextWhite));
        }
    }

    public void setTextSize(){
        if (textSizeSelectId == 1) {
            for (int count = 0; count < parent.getChildCount(); count++) {
                View view = parent.getChildAt(count);
                if (!(view instanceof Button)) { //checks that element is not button
                    if (view instanceof TextView) { //checks if element is text field
                        //gets text size of element and stores it as sp
                        float pxCurrent = ((TextView) view).getTextSize();
                        float spCurrent = pxCurrent / parent.getResources().getDisplayMetrics().scaledDensity;
                        //increases size of current element by 4sp
                        float spNew = spCurrent + 4;
                        System.out.println(spNew);
                        ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, spNew);
                    }
                }
            }
        }
    }

    public void changeTextColor(ViewGroup parentLayout, int colour) {
        for (int count = 0; count < parentLayout.getChildCount(); count++) {
            View view = parentLayout.getChildAt(count);
            if (!(view instanceof Button)) {
                if (view instanceof TextView) {
                    //changes text colour for standard text fields
                    ((TextView) view).setTextColor(colour);
                }
            }
        }
    }
}
