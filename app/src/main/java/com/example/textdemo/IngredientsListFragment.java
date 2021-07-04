package com.example.textdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import static com.example.textdemo.MainActivity.backgroundSelectId;
import static com.example.textdemo.MainActivity.customWords;

import static com.example.textdemo.MainActivity.ingredientNames;
import static com.example.textdemo.MainActivity.ingredientSelected;
import static com.example.textdemo.MainActivity.textColourSelectId;
import static com.example.textdemo.MainActivity.textSizeSelectId;
import static com.example.textdemo.MainActivity.viewCustom;


public class IngredientsListFragment extends Fragment {

    SearchView searchIngredients;
    ListView listViewIngredients;
    Button btnChangeList, btnClear;
    IngredientsAdapter ingredientsAdapter;
    RelativeLayout ingredientsListRelativeLayout;
    LinearLayout ingredientsListLinearLayout;
    TextView titleList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ingredients_list, container, false);


        ingredientsListRelativeLayout = rootView.findViewById(R.id.ingredientsListRelativeLayout);
        ingredientsListLinearLayout = rootView.findViewById(R.id.ingredientsListLinearLayout);
        listViewIngredients = (ListView) rootView.findViewById(R.id.listViewCustom);
        searchIngredients = (SearchView) rootView.findViewById(R.id.searchIngredients);
        btnChangeList = rootView.findViewById(R.id.btnChangeList);
        btnClear = rootView.findViewById(R.id.btnClear);
        titleList = rootView.findViewById(R.id.titleList);


        updateList();
        listViewIngredients.setTextFilterEnabled(true);
        setupSearchView();


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                //create yes/no dialogue box
                builder.setMessage("This will remove all custom added ingredients.\n\nAre you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("Cancel", dialogClickListener).show();
            }
        });

        btnChangeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //swap displayed list
                if (viewCustom) {
                    viewCustom = false;
                } else {
                    viewCustom = true;
                }
                updateList();
            }
        });

        listViewIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //store selected ingredient and transition to ShowIngredientFragment
                ingredientSelected = ingredientsAdapter.getItem(position).toString();
                ChangeFragment changeFragment = new ChangeFragment(new ShowIngredientFragment(new IngredientsListFragment()), getParentFragmentManager(), getView());
            }
        });

        //update relative layout and linear layout
        UpdateView updateView = new UpdateView(ingredientsListRelativeLayout, getActivity());
        updateView.setColourScheme();
        updateView.setTextSize();
        updateView = new UpdateView(ingredientsListLinearLayout, getActivity());
        updateView.setColourScheme();
        updateView.setTextSize();

        if (textColourSelectId == 1) {
            changeSearchViewTextColor(searchIngredients);
        }
        return rootView;
    }

    private void updateList() {
        //update layout to display correct list
        if (viewCustom) {
            titleList.setText("Custom Ingredients");
            ingredientsAdapter = new IngredientsAdapter(getActivity(), customWords);
            btnChangeList.setText("View Standard Ingredients");
            btnClear.setVisibility(View.VISIBLE);
        } else {
            titleList.setText("Ingredients");
            ingredientsAdapter = new IngredientsAdapter(getActivity(), ingredientNames);
            btnChangeList.setText("View Custom Ingredients");
            btnClear.setVisibility(View.INVISIBLE);
        }
        listViewIngredients.setAdapter(ingredientsAdapter);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //clear customWords and save to sharedPreferences if yes button clicked
                    customWords.clear();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(customWords);
                    editor.putString("customWords", json);
                    editor.apply();
                    listViewIngredients.setAdapter(ingredientsAdapter);
                    ((MainActivity) getActivity()).setInvalidIngredients();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private void setupSearchView() {
        searchIngredients.setIconifiedByDefault(false);
        searchIngredients.setSubmitButtonEnabled(true);
        searchIngredients.setQueryHint("Search Here");

        searchIngredients.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    //remove search filter
                    listViewIngredients.clearTextFilter();
                } else {
                    //set new search filter
                    listViewIngredients.setFilterText(newText);
                }
                return true;
            }
        });
    }


    private void changeSearchViewTextColor(View view) {
        int colorWhite = ContextCompat.getColor(getActivity(), R.color.colorTextWhite);
        if (view != null) {
            if (view instanceof TextView) {
                //change colour or search view text and hint
                ((TextView) view).setTextColor(colorWhite);
                ((TextView) view).setHintTextColor(colorWhite);

            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }
}