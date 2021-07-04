package com.example.textdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import static com.example.textdemo.MainActivity.backgroundSelectId;
import static com.example.textdemo.MainActivity.customWords;
import static com.example.textdemo.MainActivity.ingredientSelected;
import static com.example.textdemo.MainActivity.ingredientsList;
import static com.example.textdemo.MainActivity.textColourSelectId;
import static com.example.textdemo.MainActivity.textSizeSelectId;
import static com.example.textdemo.MainActivity.viewCustom;


public class ShowIngredientFragment extends Fragment {

    TextView textName, textType, textDescription;
    Button btnBack, btnRemove;
    RelativeLayout showIngredientLayout;
    Fragment backStack;

    public ShowIngredientFragment(Fragment backStack){
        this.backStack = backStack;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_ingredient, container, false);

        textName = rootView.findViewById(R.id.textName);
        textType = rootView.findViewById(R.id.textType);
        textDescription = rootView.findViewById(R.id.textDescription);
        btnBack = rootView.findViewById(R.id.btnBack);
        btnRemove = rootView.findViewById(R.id.btnRemove);
        showIngredientLayout = (RelativeLayout) rootView.findViewById(R.id.showIngredientLayout);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                //creates yes/no dialogue box
                builder.setMessage("This will remove this ingredient.\n\nAre you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("Cancel", dialogClickListener).show();
            }

        });

        updateFields();
        UpdateView updateView = new UpdateView(showIngredientLayout, getActivity());
        updateView.setColourScheme();
        updateView.setTextSize();
        return rootView;
    }

    private void backPage() {
        //returns to previous fragment
        ChangeFragment changeFragment = new ChangeFragment(backStack, getParentFragmentManager(), getView());
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    for (int i = 0; i < customWords.size(); i++) {
                        //removes selected word from custom words array list
                        if (ingredientSelected.toUpperCase().contains(customWords.get(i).toUpperCase())) {
                            customWords.remove(i);
                        }
                    }
                    //saves updated custom words array list to shared preferences
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(customWords);
                    editor.putString("customWords", json);
                    editor.apply();
                    ((MainActivity) getActivity()).setInvalidIngredients();
                    backPage(); //returns to previous page
                    break;

                case DialogInterface.BUTTON_NEGATIVE:

                    break;
            }
        }
    };

    private void updateFields() {
        for (String ingredient : customWords) {
            if (ingredientSelected.toUpperCase().contains(ingredient.toUpperCase())) {
                //displays custom ingredient information
                textName.setText(ingredient);
                textType.setText("Custom");
                textDescription.setText("This ingredient was added by the user");
                //shows remove button
                btnRemove.setVisibility(View.VISIBLE);

            }
        }

        for (IngredientsList ingredient : ingredientsList) {
            if (ingredientSelected.toUpperCase().contains(ingredient.getIngredientName().toUpperCase())) {
                textName.setText(ingredient.getIngredientName());
                switch (ingredient.getIngredientType()) {
                    //shows indication of ingredient type
                    case 0:
                        textType.setText("Not suitable for Vegans, Vegetarians or Pescatarians");
                        break;
                    case 1:
                        textType.setText("Suitable for Vegetarians");
                        break;
                    case 2:
                        textType.setText("Suitable for Pescatarians");
                        break;
                    case 3:
                        textType.setText("Varies with Manufacturer");
                        break;
                }
                textDescription.setText(ingredient.getIngredientDescription());
            }
        }

    }



}