package com.example.textdemo;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import static com.example.textdemo.MainActivity.textColourSelectId;
import static com.example.textdemo.MainActivity.textSizeSelectId;


public class IngredientsAdapter extends BaseAdapter implements Filterable {

    public Context context;
    public ArrayList<String> list;
    public ArrayList<String> originalList;

    public IngredientsAdapter(Context context, ArrayList<String> list) {
        super();
        //initialise class variables
        this.context = context;
        this.list = list;
        this.originalList = new ArrayList<String>();
        this.originalList.addAll(list);
    }

    public class IngredientHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IngredientHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
            holder = new IngredientHolder();
            holder.name = (TextView) convertView.findViewById(R.id.txtName);
            //apply accessibility settings to list view
            if (textSizeSelectId == 1) {
                holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
            if (textColourSelectId == 1) {
                holder.name.setTextColor(ContextCompat.getColor(context, R.color.colorTextWhite));
            }
            convertView.setTag(holder);
        } else {
            holder = (IngredientHolder) convertView.getTag();
        }

        holder.name.setText(list.get(position));
        return convertView;
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<String> results = new ArrayList<String>();
                if (originalList == null)
                    //store original array list
                    originalList.addAll(list);
                //create array list for search results
                if (constraint != null) {
                    if (originalList != null && originalList.size() > 0) {
                        for (String ingredient : originalList) {
                            if (ingredient.toUpperCase()
                                    .contains(constraint.toString().toUpperCase()))
                                results.add(ingredient);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                //display search results
                list = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
