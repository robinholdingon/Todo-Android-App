package com.feng.jian.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jian_feng on 3/11/17.
 */

public class ItemDatabaseHelper extends SQLiteOpenHelper {
    public static final String dateFormat = "MM/dd/yyyy HH:mm:ss";
    private static final String DATABASE_NAME = "itemDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ITEM = "items";

    private static final String KEY_ITEM_ID = "id";
    private static final String KEY_ITEM_TITLE = "title";
    private static final String KEY_ITEM_DESCRIPTION = "description";
    private static final String KEY_ITEM_DUE = "due";
    private static final String KEY_ITEM_PRIORITY = "priority";
    private static final String KEY_ITEM_IS_COMPLETE = "isComplete";

    private static ItemDatabaseHelper instance;

    private ItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized  ItemDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ItemDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEM_TABLE = "CREATE TABLE " + TABLE_ITEM +
            "(" +
                KEY_ITEM_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_ITEM_TITLE + " TEXT," +
                KEY_ITEM_DESCRIPTION + " TEXT," +
                KEY_ITEM_DUE + " TEXT," +
                KEY_ITEM_PRIORITY + " TEXT," +
                KEY_ITEM_IS_COMPLETE + " BOOLEAN" +
            ")";

        db.execSQL(CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
            onCreate(db);
        }
    }

    public long addItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ITEM_TITLE, item.getTitle());
            values.put(KEY_ITEM_DESCRIPTION, item.getDescription());
            values.put(KEY_ITEM_DUE, item.getDue() == null ? null : item.getDue().toString());
            values.put(KEY_ITEM_IS_COMPLETE, item.isComplete());
            values.put(KEY_ITEM_PRIORITY, item.getPriority().toString());

            long id = db.insertOrThrow(TABLE_ITEM, null, values);
            db.setTransactionSuccessful();

            return id;
        } catch (Exception e) {
            Log.d("ERROR", e.toString());
        } finally {
            db.endTransaction();
        }
        return -1;
    }

    public boolean deleteItem(long itemId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_ITEM, KEY_ITEM_ID + "=" + Long.toString(itemId), null) > 0;
    }

    public Item getItem(long id) {
        String ITEM_SELECT_QUERY =
                String.format("SELECT * FROM " + TABLE_ITEM + " WHERE " + KEY_ITEM_ID  + " = " + Long.toString(id));
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(ITEM_SELECT_QUERY, null);
        cursor.moveToFirst();
        return abstractItemFromCursor(cursor);
    }

    public void updateItem(Item item) {
        DateFormat df = new SimpleDateFormat(dateFormat);

        long id = item.getId();
        ContentValues cv = new ContentValues();
        cv.put(KEY_ITEM_PRIORITY, item.getPriority().toString());
        cv.put(KEY_ITEM_IS_COMPLETE, item.isComplete());
        cv.put(KEY_ITEM_DUE, df.format(item.getDue()));
        cv.put(KEY_ITEM_DESCRIPTION, item.getDescription());
        cv.put(KEY_ITEM_TITLE, item.getTitle());

        getReadableDatabase().update(TABLE_ITEM, cv, KEY_ITEM_ID + "=" + Long.toString(id), null);
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList();
        String ITEM_SELECT_QUERY =
                String.format("SELECT * FROM " + TABLE_ITEM);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ITEM_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    items.add(abstractItemFromCursor(cursor));
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("ERROR", e.toString());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return items;
    }

    private Item abstractItemFromCursor(Cursor cursor) {
        Item newItem = new Item(cursor.getString(cursor.getColumnIndex(KEY_ITEM_TITLE)));

        newItem.setDescription(cursor.getString(cursor.getColumnIndex(KEY_ITEM_DESCRIPTION)));

        try {
            newItem.setDue(cursor.getString(cursor.getColumnIndex(KEY_ITEM_DUE)) == null ?
                    new Date() :
                    new SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(
                            cursor.getString(cursor.getColumnIndex(KEY_ITEM_DUE))));
        } catch (Exception e) {
            newItem.setDue(new Date());
        }

        newItem.setId(cursor.getLong(cursor.getColumnIndex(KEY_ITEM_ID)));
        newItem.setComplete(cursor.getInt(cursor.getColumnIndex(KEY_ITEM_IS_COMPLETE)) > 0);
        newItem.setPriority(
                Item.Priority.valueOf(cursor.getString(cursor.getColumnIndex(KEY_ITEM_PRIORITY))));
        return newItem;
    }
}
