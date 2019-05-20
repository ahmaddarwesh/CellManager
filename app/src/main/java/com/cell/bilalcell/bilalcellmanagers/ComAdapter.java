package com.cell.bilalcell.bilalcellmanagers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ComAdapter extends ArrayAdapter<CompanyItems> {

    public ComAdapter(Context context, ArrayList<CompanyItems> arrayList) {
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.company_spinner,
                    parent, false);
        }
        ImageView logo = convertView.findViewById(R.id.img_company_spinner);
        TextView name = convertView.findViewById(R.id.name_company_spinner);

        CompanyItems currentitem = getItem(position);

        if (currentitem != null) {
            logo.setImageResource(currentitem.getLogo());
            name.setText(currentitem.getName());
        }

        return convertView;
    }
}
