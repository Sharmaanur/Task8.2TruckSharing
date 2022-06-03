package com.suman.trucksharing.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemsDBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "ItemDB";
    private static final String TABLE = "items";
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "user";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_PICK_LOCATION = "picklocation";
    private static final String KEY_DROP_LOCATION = "droplocation";
    private static final String KEY_FROM_COORDINATES = "fromcord";
    private static final String KEY_TO_COORDINATES = "tocord";
    private static final String KEY_GOODSTYPE = "goodstype";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_LENGTH = "length";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_WIDTH = "width";
    private static final String KEY_VEHICLETYPE = "vehicletype";

    public ItemsDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT ,"
                + KEY_DATE + " TEXT ,"
                + KEY_TIME + " TEXT ,"
                + KEY_PICK_LOCATION + " TEXT ,"
                + KEY_DROP_LOCATION + " TEXT ,"
                + KEY_FROM_COORDINATES + " TEXT ,"
                + KEY_TO_COORDINATES + " TEXT ,"
                + KEY_GOODSTYPE + " TEXT,"
                + KEY_WEIGHT + " TEXT,"
                + KEY_LENGTH + " TEXT,"
                + KEY_HEIGHT + " TEXT,"
                + KEY_WIDTH + " TEXT,"
                + KEY_VEHICLETYPE + " TEXT,"
                + KEY_USERNAME + " TEXT"
                + ");";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old, int newVer) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void addItem(ItemModel itemmodel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, itemmodel.getName());
        values.put(KEY_DATE, itemmodel.getDate());
        values.put(KEY_TIME, itemmodel.getTime());
        values.put(KEY_PICK_LOCATION, itemmodel.getPickuplocation());
        values.put(KEY_DROP_LOCATION, itemmodel.getDroplocation());
        values.put(KEY_FROM_COORDINATES, itemmodel.getFromCoordinates());
        values.put(KEY_TO_COORDINATES, itemmodel.getToCoordinates());
        values.put(KEY_GOODSTYPE, itemmodel.getGoodstype());
        values.put(KEY_WEIGHT, itemmodel.getWeight());
        values.put(KEY_LENGTH, itemmodel.getLength());
        values.put(KEY_HEIGHT, itemmodel.getHeight());
        values.put(KEY_WIDTH, itemmodel.getWidth());
        values.put(KEY_VEHICLETYPE, itemmodel.getVehicletype());
        values.put(KEY_USERNAME, itemmodel.getUsername());
        // Inserting Row
        db.insert(TABLE, null, values);
        db.close();
    }

    public ArrayList<ItemModel> getAllItems() {
        ArrayList<ItemModel> itemlist = new ArrayList<ItemModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE + " ORDER BY"+ " id" + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemModel itemModel = new ItemModel();
                itemModel.setId(cursor.getString(0));
                itemModel.setName(cursor.getString(1));
                itemModel.setDate(cursor.getString(2));
                itemModel.setTime(cursor.getString(3));
                itemModel.setPickuplocation(cursor.getString(4));
                itemModel.setDroplocation(cursor.getString(5));
                itemModel.setFromCoordinates(cursor.getString(6));
                itemModel.setToCoordinates(cursor.getString(7));
                itemModel.setGoodstype(cursor.getString(8));
                itemModel.setWeight(cursor.getString(9));
                itemModel.setLength(cursor.getString(10));
                itemModel.setHeight(cursor.getString(11));
                itemModel.setWidth(cursor.getString(12));
                itemModel.setVehicletype(cursor.getString(13));
                // Adding contact to list
                itemlist.add(itemModel);
            } while (cursor.moveToNext());
        }

        // return list
        return itemlist;
    }
    public ArrayList<ItemModel> getMyOrder(String username) {
        ArrayList<ItemModel> itemlist = new ArrayList<ItemModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE + " WHERE "+KEY_USERNAME + " = '"+username+ "' ORDER BY "+  KEY_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemModel itemModel = new ItemModel();
                itemModel.setId(cursor.getString(0));
                itemModel.setName(cursor.getString(1));
                itemModel.setDate(cursor.getString(2));
                itemModel.setTime(cursor.getString(3));
                itemModel.setPickuplocation(cursor.getString(4));
                itemModel.setDroplocation(cursor.getString(5));
                itemModel.setFromCoordinates(cursor.getString(6));
                itemModel.setToCoordinates(cursor.getString(7));
                itemModel.setGoodstype(cursor.getString(8));
                itemModel.setWeight(cursor.getString(9));
                itemModel.setLength(cursor.getString(10));
                itemModel.setHeight(cursor.getString(11));
                itemModel.setWidth(cursor.getString(12));
                itemModel.setVehicletype(cursor.getString(13));
                itemModel.setUsername(cursor.getString(14));
                // Adding contact to list
                itemlist.add(itemModel);
            } while (cursor.moveToNext());
        }

        // return list
        return itemlist;
    }
/*    public void deleteClient(ItemModel itemModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE, KEY_USERNAME + " = ?",
                new String[] { String.valueOf(itemModel.getUsername()) });
        db.close();
    }*/
}
