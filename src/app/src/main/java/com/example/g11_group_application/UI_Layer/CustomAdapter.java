package com.example.g11_group_application.UI_Layer;
/**
 * @Author: Divyesh Srivastava (u7726856)
 * Created: 08-May-2024
 * Comments: This is the helper method to get the proper view for the list view
 */
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.example.g11_group_application.R;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {
    private List<String> items;
    public CustomAdapter(Context context, List<String> items) {
        super(context, 0, items);
        this.items = items;  // Store the reference to the list
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_linear_layout, parent, false);
            holder = new ViewHolder();
            holder.editText = convertView.findViewById(R.id.txtChildItem);
            convertView.setTag(holder);

            holder.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Update the current position item's data with the new text
                    items.set(position, s.toString());
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.editText.setText(items.get(position)); // Set text based on current position in the data model
        return convertView;
    }

    static class ViewHolder {
        EditText editText;
    }
}
