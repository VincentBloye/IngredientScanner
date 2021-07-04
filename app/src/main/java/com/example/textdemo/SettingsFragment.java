package com.example.textdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import static com.example.textdemo.MainActivity.backgroundSelectId;
import static com.example.textdemo.MainActivity.colourblindSelect;
import static com.example.textdemo.MainActivity.dietSelect;
import static com.example.textdemo.MainActivity.dietSelectId;
import static com.example.textdemo.MainActivity.ingredientNames;
import static com.example.textdemo.MainActivity.textColourSelectId;
import static com.example.textdemo.MainActivity.textSizeSelectId;
import static com.example.textdemo.MainActivity.customWords;
import static com.example.textdemo.MainActivity.viewCustom;


public class SettingsFragment extends Fragment {
    RelativeLayout settingsLayout;

    private int dietArray = R.array.array_diet;
    private int textColourArray = R.array.array_text_colour;
    private int backgroundArray = R.array.array_background_colour;
    private int textSizeArray = R.array.array_text_size;
    private ArrayAdapter<CharSequence> dietAdapter, textColourAdapter, backgroundColourAdapter, textSizeAdapter;

    private AutoCompleteTextView autoDiet, autoTextColour, autoBackgroundColour, autoTextSize;
    private SwitchCompat switchColourblind;
    private TextInputEditText textAddIngredient;

    private String textColourSelect, backgroundSelect, textSizeSelect;

    private boolean largeText = false;


    private SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        settingsLayout = rootView.findViewById(R.id.settingsLayout);
        switchColourblind = rootView.findViewById(R.id.switchColourblind);
        Button btnAddIngredient = rootView.findViewById(R.id.btnAddIngredient);
        Button btnViewCustomIngredients = rootView.findViewById(R.id.btnViewCustomIngredients);
        textAddIngredient = rootView.findViewById(R.id.textAddIngredient);
        textAddIngredient.setHint("Add Custom Ingredients");

        //applies array adapters to dropdowns
        autoDiet = rootView.findViewById(R.id.autoDiet);
        dietAdapter = ArrayAdapter.createFromResource(getActivity(), dietArray, R.layout.dropdown_item);
        dietAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoDiet.setAdapter(dietAdapter);
        autoDiet.setText(autoDiet.getAdapter().getItem(0).toString());
        dietAdapter.getFilter().filter(null);
        autoDiet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dietSelect = dietAdapter.getItem(position).toString();
                dietSelectId = (int) dietAdapter.getItemId(position);
                saveSettings();
                //updates invalid ingredients for diet selection
                ((MainActivity) getActivity()).setInvalidIngredients();
            }
        });

        autoTextColour = rootView.findViewById(R.id.autoTextColour);
        textColourAdapter = ArrayAdapter.createFromResource(getActivity(), textColourArray, R.layout.dropdown_item);
        textColourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoTextColour.setAdapter(textColourAdapter);
        autoTextColour.setText(autoTextColour.getAdapter().getItem(0).toString());
        textColourAdapter.getFilter().filter(null);
        autoTextColour.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textColourSelectId = (int) textColourAdapter.getItemId(position);
                textColourSelect = textColourAdapter.getItem(position).toString();
                updateTextColour();
                updateBackground();
                updateDropdowns();
            }
        });

        autoBackgroundColour = rootView.findViewById(R.id.autoBackgroundColour);
        backgroundColourAdapter = ArrayAdapter.createFromResource(getActivity(), backgroundArray, R.layout.dropdown_item);
        backgroundColourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoBackgroundColour.setAdapter(backgroundColourAdapter);
        autoBackgroundColour.setText(autoBackgroundColour.getAdapter().getItem(0).toString());
        backgroundColourAdapter.getFilter().filter(null);
        autoBackgroundColour.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                backgroundSelectId = (int) backgroundColourAdapter.getItemId(position);
                backgroundSelect = backgroundColourAdapter.getItem(position).toString();
                updateBackground();
                updateTextColour();
                updateDropdowns();

            }
        });

        autoTextSize = rootView.findViewById(R.id.autoTextSize);
        textSizeAdapter = ArrayAdapter.createFromResource(getActivity(), textSizeArray, R.layout.dropdown_item);
        textSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoTextSize.setAdapter(textSizeAdapter);
        autoTextSize.setText(autoTextSize.getAdapter().getItem(0).toString());
        textSizeAdapter.getFilter().filter(null);
        autoTextSize.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textSizeSelect = textSizeAdapter.getItem(position).toString();
                textSizeSelectId = (int) textSizeAdapter.getItemId(position);
                updateTextSize();

            }
        });

        switchColourblind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //changes colourblind variable on switch change
                if (isChecked) {
                    colourblindSelect = true;
                } else {
                    colourblindSelect = false;
                }
                updateColourblind();
            }
        });

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validIngredient = true;
                String newIngredient = textAddIngredient.getEditableText().toString();
                if (!(newIngredient.equals(""))) { //checks input is not null
                    for (String ingredient : ingredientNames) {
                        if (newIngredient.toUpperCase().contains(ingredient.toUpperCase())) { //checks ingredient is not in standard database
                            Toast.makeText(getActivity(), "This ingredient is already in our database", Toast.LENGTH_LONG).show();
                            validIngredient = false;
                        }
                    }
                    for (String ingredient : customWords) {
                        if (newIngredient.toUpperCase().contains(ingredient.toUpperCase())) { //checks ingredient is not in custom list already
                            Toast.makeText(getActivity(), "You have already added this ingredient", Toast.LENGTH_LONG).show();
                            validIngredient = false;
                        }
                    }
                    if (validIngredient) { //adds new ingredient if valid
                        customWords.add(newIngredient);
                        saveSettings();
                        ((MainActivity) getActivity()).setInvalidIngredients();
                    }
                    textAddIngredient.setText(""); //clears text field
                } else {
                    Toast.makeText(getActivity(), "Please enter an ingredient name", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnViewCustomIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //changes fragment to show custom list
                viewCustom = true;
                ChangeFragment changeFragment = new ChangeFragment(new IngredientsListFragment(), getParentFragmentManager(), getView());
            }
        });

        //updates accessibility settings and layout elements
        updateTextSize();
        updateBackground();
        updateTextColour();
        updateDropdowns();
        updateColourblind();
        return rootView;
    }

    public void saveCustomWords() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        //customWords serialised into a json string and stored in shared preferences
        String json = gson.toJson(customWords);
        editor.putString("customWords", json);
        editor.apply();
    }

    private void updateColourblind() {
        //updates indicator for colourblind switch
        if (colourblindSelect) {
            switchColourblind.setText("On");
            switchColourblind.setChecked(true);
        } else {
            switchColourblind.setText("Off");
            switchColourblind.setChecked(false);
        }
        saveSettings();
    }

    private void saveSettings() {
        sharedPreferences = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        //stores variables in shared preferences
        editor.putString("diet", dietSelect);
        editor.putString("textColour", textColourSelect);
        editor.putString("background", backgroundSelect);
        editor.putString("textSize", textSizeSelect);

        editor.putInt("dietId", dietSelectId);
        editor.putInt("textColourId", textColourSelectId);
        editor.putInt("backgroundId", backgroundSelectId);
        editor.putInt("textSizeId", textSizeSelectId);
        editor.putBoolean("colourblind", colourblindSelect);

        saveCustomWords();
        editor.apply();
    }

    private void updateBackground() {
        //updates background colour, changes text colour to avoid non-contrasting pairs
        if (backgroundSelectId == 0) {
            if (textColourSelectId == 1) {
                changeTextColour(ContextCompat.getColor(getActivity(), R.color.colorTextBlack));
                textColourSelectId = 0;
            }
            settingsLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBackgroundWhite));
        }
        if (backgroundSelectId == 1) {
            if (textColourSelectId == 0) {
                changeTextColour(ContextCompat.getColor(getActivity(), R.color.colorTextWhite));
                textColourSelectId = 1;
            }
            settingsLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBackgroundGrey));
        }
        if (backgroundSelectId == 2) {
            if (textColourSelectId == 1) {
                changeTextColour(ContextCompat.getColor(getActivity(), R.color.colorTextBlack));
                textColourSelectId = 0;
            }
            settingsLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBackgroundYellow));
        }
        saveSettings();
    }

    private void updateTextColour() {
        //updates text colours, changes background colour to avoid non-contrasting pairs
        if (textColourSelectId == 0) {
            if (backgroundSelectId == 1) {
                settingsLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBackgroundWhite));
                backgroundSelectId = 0;
            }
            changeTextColour(ContextCompat.getColor(getActivity(), R.color.colorTextBlack));

        }
        if (textColourSelectId == 1) {
            if (backgroundSelectId == 0 || backgroundSelectId == 2) {
                settingsLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBackgroundGrey));
                backgroundSelectId = 1;
            }
            changeTextColour(ContextCompat.getColor(getActivity(), R.color.colorTextWhite));
        }
        saveSettings();
    }

    private void updateTextSize() {

        if (textSizeSelectId == 0) {
            if (largeText) { //decreases text size if text is currently large
                for (int count = 0; count < settingsLayout.getChildCount(); count++) {
                    View view = settingsLayout.getChildAt(count);
                    if (!(view instanceof Button)) {
                        if (view instanceof TextView) {
                            float pxCurrent = ((TextView) view).getTextSize();
                            float spCurrent = pxCurrent / getResources().getDisplayMetrics().scaledDensity;
                            float spNew = spCurrent - 4;
                            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, spNew);
                        }
                    }
                }
                largeText = false;
            }
        }
        if (textSizeSelectId == 1) {
            if (!largeText) { //increases text size if text is currently standard
                UpdateView updateView = new UpdateView(settingsLayout, getActivity());
                updateView.setTextSize();
                largeText = true;
            }
        }
        saveSettings();
    }


    private void changeTextColour(int colour) {
        for (int count = 0; count < settingsLayout.getChildCount(); count++) {
            View view = settingsLayout.getChildAt(count);
            if (!(view instanceof Button)) {
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(colour); //changes colour of standard text fields
                }
                if (view instanceof TextInputEditText) { //changes colour of editable text field
                    ((TextInputEditText) view).setTextColor(colour);
                    ((TextInputEditText) view).setHintTextColor(colour);
                }
            }
            switchColourblind.setTextColor(colour);
        }
        //updates dropdown text colour
        autoDiet.setTextColor(colour);
        autoTextColour.setTextColor(colour);
        autoBackgroundColour.setTextColor(colour);
        autoTextSize.setTextColor(colour);
    }

    private void updateDropdowns() {
        //updates dropdown selections
        autoDiet.setText(getResources().getStringArray(dietArray)[dietSelectId], false);
        autoTextColour.setText(getResources().getStringArray(textColourArray)[textColourSelectId], false);
        autoBackgroundColour.setText(getResources().getStringArray(backgroundArray)[backgroundSelectId], false);
        autoTextSize.setText(getResources().getStringArray(textSizeArray)[textSizeSelectId], false);
    }
}