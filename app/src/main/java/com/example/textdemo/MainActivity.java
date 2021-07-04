package com.example.textdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity {
    //define static variables
    static ArrayList<IngredientsList> ingredientsList = new ArrayList<IngredientsList>();
    static ArrayList<String> invalidIngredients = new ArrayList<String>();
    static ArrayList<String> ingredientNames = new ArrayList<String>();
    static ArrayList<String> variedSuitability = new ArrayList<String>();
    static ArrayList<String> customWords = new ArrayList<String>();

    static boolean savedBitmap = false;
    static Bitmap bitmap;

    public static int dietSelectId;
    public static String dietSelect;
    public static int textColourSelectId;
    public static int backgroundSelectId;
    public static int textSizeSelectId;
    public static boolean colourblindSelect;

    public static boolean viewCustom;

    public static String ingredientSelected;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadSettings();
        readFile();
        setInvalidIngredients();

        //sorts ingredientNames into alphabetical order
        ingredientNames.sort(new Comparator<String>() {
            @Override
            public int compare(String ingredient1, String ingredient2) {
                return ingredient1.compareTo(ingredient2);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.navBar);
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        //check for permissions
        if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
    }


    private void readFile() {
        String name;
        int type;
        String description;

        int i = 0;
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader((getAssets().open("IngredientsList.txt"))));
            while (reader.ready()) {
                //creates array list of IngredientsList objects
                name = reader.readLine();
                type = Integer.parseInt(reader.readLine());
                description = reader.readLine();
                ingredientsList.add(new IngredientsList(i, name, type, description));
                //adds name to ingredientsName array list
                ingredientNames.add(name);
                i += 1;
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void setInvalidIngredients() {
        invalidIngredients.clear();
        //adds unsuitable ingredients to array list based on diet setting
        for (IngredientsList ingredient : ingredientsList) {
            String ingredientName = ingredient.getIngredientName();
            int ingredientType = ingredient.getIngredientType();
            if (ingredientType == 2) {
                if (dietSelectId == 0 || dietSelectId == 1) {
                    invalidIngredients.add(ingredientName.toUpperCase());
                }
            } else if (ingredientType == 1) {
                if (dietSelectId == 0) {
                    invalidIngredients.add(ingredientName.toUpperCase());
                }
            } else if (ingredientType == 0) {
                invalidIngredients.add(ingredientName.toUpperCase());
            }
            if (ingredientType == 3) {
                variedSuitability.add(ingredientName);
            }
        }
        //adds custom words to invalidIngredients
        if (customWords != null && !customWords.isEmpty()) {
            for (String ingredient : customWords) {
                invalidIngredients.add(ingredient.toUpperCase());
            }
        }
    }

    private void loadSettings() {
        sharedPreferences = this.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        //retrieves key values from shared preferences file and stores them in static variables
        dietSelect = sharedPreferences.getString("diet", "");
        dietSelectId = sharedPreferences.getInt("dietId", 0);
        textColourSelectId = sharedPreferences.getInt("textColourId", 0);
        backgroundSelectId = sharedPreferences.getInt("backgroundId", 0);
        textSizeSelectId = sharedPreferences.getInt("textSizeId", 0);
        colourblindSelect = sharedPreferences.getBoolean("colourblind", false);
        customWords = getArrayList();
    }

    private ArrayList<String> getArrayList() {
        Gson gson = new Gson();
        //customWords retrieved from shared preferences as json string
        String json = sharedPreferences.getString("customWords", null);
        //json string converted into array list and returned
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

}

