package com.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lib.Consts;

import java.util.ArrayList;
import java.util.List;

public class DBDataManager {
    private Context context;

    public DBDataManager(Context context) {
        this.context = context;
    }


    public void apppend_location_value(String no, int v1, int v2, int v3, int v4, int v5, int v6) {

        Log.e("beacon", "=＝    apppend_location_value      ==");
        DBDatabaseHelper helper = new DBDatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransaction();
        try {
            Log.d("beacon", "=＝＝＝＝＝＝刪除原資料===");
            String where = " no = ?";
            String[] whereValue = {no};
            db.delete(Consts.TABLE_location, where, whereValue);
            Log.d("beacon", "=＝＝＝＝＝＝再加入取到===");
            db.execSQL("INSERT INTO " + Consts.TABLE_location
                            + " VALUES(null,?,?,?,?,?,?,?,?)",
                    new Object[]{
                            no,
                            v1,
                            v2,
                            v3,
                            v4,
                            v5,
                            v6,
                            ""});
            db.setTransactionSuccessful(); // 设置事务成功完成
        } catch (Exception e) {
            Log.d("beacon", "=apppend_location_value    ERROR====" + e.toString());
        } finally {
            db.endTransaction(); // 结束事务
            db.close();
        }
    }


    public List<LocationValueModel> get_location_value() {
        List<LocationValueModel> allData = new ArrayList<LocationValueModel>();
        DBDatabaseHelper helper = new DBDatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            String sql = "SELECT no,v1,v2,v3,v4,v5,v6 FROM  " + Consts.TABLE_location;
            Cursor result = db.rawQuery(sql, null);
            if (result.getCount() > 0) {
                for (result.moveToFirst(); !result.isAfterLast(); result
                        .moveToNext()) {
                    LocationValueModel tt = new LocationValueModel();
                    tt.no = result.getString(0);
                    tt.value[0] = result.getInt(1);
                    tt.value[1] = result.getInt(2);
                    tt.value[2] = result.getInt(3);
                    tt.value[3] = result.getInt(4);
                    tt.value[4] = result.getInt(5);
                    tt.value[5] = result.getInt(6);
                    allData.add(tt);
                }
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        } finally {
            db.endTransaction(); // 结束事务
            db.close();
        }
        return allData;
    }


    public void apppend_location_mark(String mark, String address) {

        Log.e("beacon", "=＝    apppend_location_mark     ==");

        Log.e("beacon", "       mark: " + mark + "   address:" + address);

        DBDatabaseHelper helper = new DBDatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransaction();
        try {


            Log.d("beacon", "=＝＝＝＝＝＝刪除原資料===");
            String where = " address = ?";
            String[] whereValue = {address};
            db.delete(Consts.TABLE_mark, where, whereValue);

            Log.d("beacon", "=＝＝＝＝＝＝再加入取到===");
            db.execSQL("INSERT INTO " + Consts.TABLE_mark
                            + " VALUES(null,?,?,?)",
                    new Object[]{
                            mark,
                            address,
                            ""});


            db.setTransactionSuccessful(); // 设置事务成功完成
        } catch (Exception e) {
            Log.d("beacon", "=apppend_location_value    ERROR====" + e.toString());
        } finally {
            db.endTransaction(); // 结束事务
            db.close();
        }
    }


    public List<MarkModel> get_mark_list() {
        List<MarkModel> allData = new ArrayList<MarkModel>();
        DBDatabaseHelper helper = new DBDatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            String sql = "SELECT mark,address FROM  " + Consts.TABLE_mark;
            Cursor result = db.rawQuery(sql, null);
            if (result.getCount() > 0) {


                for (result.moveToFirst(); !result.isAfterLast(); result
                        .moveToNext()) {
                    MarkModel mm = new MarkModel();
                    mm.mark = result.getString(0);
                    mm.address = result.getString(1);
                    allData.add(mm);
                }
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        } finally {
            db.endTransaction(); // 结束事务
            db.close();
        }
        return allData;
    }


    public void list_table_data3(String dbName) {
        Log.e("beacon", "---\n");
        Log.e("beacon", "----===============================================--");
        Log.e("beacon", "------條列----Table :" + dbName + "---------------------------------------");
        Log.e("beacon", "----================================================--=----");
        DBDatabaseHelper helper = new DBDatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            String sql = "SELECT * FROM  " + dbName;
            Cursor result = db.rawQuery(sql, null);
            if (result.getCount() > 0) {

                String ss = "";
                for (int i = 0; i < result.getColumnCount(); i++) {
                    ss = ss + result.getColumnName(i) + ",  ";
                }
                Log.d("beacon", "----------------------------------------");
                Log.d("beacon", "-----欄位----->>> " + ss);
                Log.d("beacon", "-------------------------------------------");
                for (result.moveToFirst(); !result.isAfterLast(); result
                        .moveToNext()) {
                    ss = "";
                    for (int i = 0; i < result.getColumnCount(); i++) {
                        ss = ss + result.getString(i) + ",  ";
                    }

                    Log.d("beacon", "----記錄------>>> " + ss);

                }
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        } finally {
            db.endTransaction(); // 结束事务
            db.close();
        }

    }


}