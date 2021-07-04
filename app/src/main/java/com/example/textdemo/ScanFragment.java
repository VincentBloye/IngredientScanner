package com.example.textdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.textdemo.MainActivity.bitmap;
import static com.example.textdemo.MainActivity.colourblindSelect;
import static com.example.textdemo.MainActivity.invalidIngredients;
import static com.example.textdemo.MainActivity.savedBitmap;
import static com.example.textdemo.MainActivity.textColourSelectId;
import static com.example.textdemo.MainActivity.textSizeSelectId;
import static com.example.textdemo.MainActivity.ingredientSelected;




public class ScanFragment extends Fragment implements View.OnClickListener {

    private ImageView imageProduct, imageResult;
    private TextView textIngredients,  ingredientsHeader, invalidHeader, resultHeader, scanNowHeader;
    private Button btnRotate;
    boolean manualRotate = false, validProduct;

    private ListView listInvalidIngredients;
    private int rotateCounter = 0;

    private ArrayList<String> scannedIngredients = new ArrayList<String>();
    private ArrayList<String> scannedInvalidIngredients = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);


        imageProduct = rootView.findViewById(R.id.imageProduct);
        imageResult = rootView.findViewById(R.id.imageResult);


        textIngredients = rootView.findViewById(R.id.textIngredients);
        ingredientsHeader = rootView.findViewById(R.id.ingredientsHeader);
        invalidHeader = rootView.findViewById(R.id.invalidHeader);
        scanNowHeader = rootView.findViewById(R.id.scanNowHeader);

        resultHeader = rootView.findViewById(R.id.resultHeader);
        RelativeLayout importLayout = (RelativeLayout) rootView.findViewById(R.id.importLayout);

        listInvalidIngredients = (ListView) rootView.findViewById(R.id.listInvalidIngredients);

        UpdateView updateView = new UpdateView(importLayout, getActivity());
        updateView.setColourScheme();
        updateView.setTextSize();

        btnRotate = (Button) rootView.findViewById(R.id.btnRotate);
        Button btnImport = (Button) rootView.findViewById(R.id.btnImport);
        Button btnCapture = (Button) rootView.findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(this);
        btnImport.setOnClickListener(this);
        btnRotate.setOnClickListener(this);

        if (savedBitmap){
            //checks if an image has been saved and analyses it
            imageProduct.setImageBitmap(bitmap);
            analyseText();
            ingredientsHeader.setVisibility(View.VISIBLE);
            invalidHeader.setVisibility(View.VISIBLE);
        }

        listInvalidIngredients.setOnItemClickListener((parent, view, position, id) -> {
            //stores selected ingredient and transitions to ShowIngredientFragment
            ingredientSelected = scannedInvalidIngredients.get(position);
            ChangeFragment changeFragment = new ChangeFragment(new ShowIngredientFragment(new ScanFragment()), getParentFragmentManager(), getView());
        });
        return rootView;
    }



    @Override
    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.btnImport:
                resetScan();
                //creates intent to import image
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, 101);
                break;

            case R.id.btnRotate:
                manualRotate = true;
                resetScan();
                //rotates image by 90 degrees and calls analyseText
                bitmap = rotateBitmap(bitmap, 90);
                imageProduct.setImageBitmap(bitmap);
                analyseText();
                break;
            case R.id.btnCapture:
                resetScan();
                //creates intent to capture image
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 102);
                break;
        }
        btnRotate.setVisibility(View.VISIBLE);

        btnRotate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rotate_icon_white, 0, 0, 0);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        imageResult.setVisibility(View.INVISIBLE);
        ingredientsHeader.setVisibility(View.VISIBLE);
        invalidHeader.setVisibility(View.VISIBLE);


        if (requestCode == 101) {
            Uri imageSelected = data.getData();
            try {
                //store selected image as bitmap
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageSelected);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == 102) {
            //store captured image as bitmap
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
        }
        //apply bitmap to image view
        imageProduct.setImageBitmap(bitmap);
        analyseText();
    }

    private void analyseText() {
        scanNowHeader.setVisibility(View.INVISIBLE);

        //start text recognition on bitmap
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient();
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                //split result into array at each comma
                                String[] ingredientsArray = visionText.getText().split(",");
                                //add array to array list
                                scannedIngredients.addAll(Arrays.asList(ingredientsArray));
                                outputText();

                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });


    }

    private void outputText() {
        boolean hasTitle = false;
        for (String ingredient : scannedIngredients) {
            //sets ingredient to upper case
            ingredient = ingredient.toUpperCase();

            if (ingredient.contains("INGREDIENTS")) {
                //removes "ingredients:" from first ingredient
                ingredient = ingredient.substring(12);
                hasTitle = true;
            }
            if (ingredient.startsWith(" ")) {
                //removes space from start of ingredients
                ingredient = ingredient.substring(1);
                hasTitle = true;
            }

            for (String invalidIngredient : invalidIngredients) {
                if (ingredient.contains(invalidIngredient)) {
                    //checks if ingredient is invalid
                    scannedInvalidIngredients.add(ingredient);
                    ingredient = "";
                    validProduct = false;
                }
            }


            if (ingredient.matches(".*[^a-z].*")) {
                //adds ingredient to text view if it contains letters
                textIngredients.append(ingredient);
                textIngredients.append("\n");

            }
        }
        if (hasTitle) {
            //if image contains "ingredients" saves image and shows result
            savedBitmap = true;
            outputInvalid();
            showResult();
        } else {
            //outputs error message if "ingredients" not found
            resultHeader.setText("Ingredients not found");
            textIngredients.setText(null);
            if (!manualRotate && rotateCounter < 4) {
                //rotates image and attempts to analyse again up to 4 times
                rotateCounter++;
                bitmap = rotateBitmap(bitmap, 90);
                imageProduct.setImageBitmap(bitmap);
                analyseText();
            }
        }
        textIngredients.setMovementMethod(new ScrollingMovementMethod()); //sets text view as scrollable
    }

    private void outputInvalid() {
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>
                (getActivity(), R.layout.row, scannedInvalidIngredients) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView listTextView = (TextView) view.findViewById(R.id.txtName);

                //applies accessibility settings to list view
                if (textSizeSelectId == 1) {
                    listTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                }
                if (textColourSelectId == 1) {
                    listTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorTextWhite));
                }

                return view;
            }
        };
        listInvalidIngredients.setAdapter(listAdapter);
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle) {
        //rotates passed bitmap by 90 degrees and returns it
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    private void showResult() {
        String resultText;
        resultHeader.setText(null);
        if (validProduct) {
            //displays positive result if validProduct is true
            imageResult.setVisibility(View.VISIBLE);
            imageResult.setImageResource(R.drawable.thumb_up);
            resultText = "Suitable for you!";
            resultHeader.setText(resultText);
        } else {
            //displays negative result if validProduct is false
            resultText = "Not suitable for you";
            imageResult.setVisibility(View.VISIBLE);
            resultHeader.setText(resultText);
            if (colourblindSelect) { //applies colourblind setting to result icon
                imageResult.setImageResource(R.drawable.thumb_down_magenta);
            } else {
                imageResult.setImageResource(R.drawable.thumb_down_red);
            }
        }
    }

    private void resetScan() {
        //resets results to allow fresh scan
        validProduct = true;
        scannedInvalidIngredients.clear();
        scannedIngredients.clear();
        textIngredients.setText(null);
    }


}