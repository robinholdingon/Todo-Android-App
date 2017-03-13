package com.feng.jian.simpletodo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jian_feng on 3/12/17.
 */

public class ItemsAdapter extends ArrayAdapter<Item> {
    public ItemsAdapter(Context context, List<Item> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_row, parent, false);
        }
        TextView itemTitle = (TextView) convertView.findViewById(R.id.itemTitle);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
        Button editButton = (Button) convertView.findViewById(R.id.editItem);
        Button deleteButton = (Button) convertView.findViewById(R.id.deleteItem);

        itemTitle.setText(item.getTitle());
        if (item.isComplete()) {
            itemTitle.setTextColor(Color.GRAY);
            itemTitle.setPaintFlags(itemTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            itemTitle.setTextColor(Color.BLACK);
            itemTitle.setPaintFlags(( itemTitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG)));
        }
        checkBox.setChecked(item.isComplete());
        setEditButtonListener(position, editButton);
        setDeleteButtonListener(position, deleteButton);
        setCheckboxListener(position, checkBox, itemTitle);
        return convertView;
    }

    private void startItemActivity(int position) {
        Intent intent = new Intent(this.getContext(), ItemActivity.class);
        intent.putExtra("id", this.getItem(position).getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.getContext().startActivity(intent);
    }

    private void setEditButtonListener(final int position, Button editButton) {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startItemActivity(position);
            }
        });
    }

    private void setDeleteButtonListener(final int position, Button deleteButton) {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item = getItem(position);
                long itemId = item.getId();
                remove(item);
                ItemDatabaseHelper.getInstance(getContext().getApplicationContext()).deleteItem(itemId);
            }
        });
    }

    private void setCheckboxListener(final int position, CheckBox checkBox, final TextView itemTitle) {
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item = getItem(position);
                item.setComplete(!item.isComplete());
                if (item.isComplete()) {
                    itemTitle.setTextColor(Color.GRAY);
                    itemTitle.setPaintFlags(itemTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    itemTitle.setTextColor(Color.BLACK);
                    itemTitle.setPaintFlags(( itemTitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG)));
                }
            }
        });
    }
}
