package com.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lib.Consts;

import java.util.List;


public class DBDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASES_VER = 4;    //資料庫版本號


    public DBDatabaseHelper(Context context) {
        super(context, context.getFilesDir().getPath()
                + "/databases/" + Consts.DATABASE_NAME, null, DATABASES_VER);
        Log.d("beacon", "======Database PATH ==========" + context.getFilesDir().getPath()
                   + "/databases/" + Consts.DATABASE_NAME);

        //   DBDataManager dbManager = new DBDataManager(context);
      // dbManager.getMyFriends(globalVariable.selfDataModel.act_id);


    }

    //更改格式後，請同時更新 Consts.DATABASES_VER 及 onUpgrade()
    @Override
    public void onCreate(SQLiteDatabase db) {
       Log.d("beacon", "=========DatabaseHelper onCreate 有更動結構時  ==========");

        String sBuffer = "CREATE TABLE [" + Consts.TABLE_location + "] ("
                + "[auto_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + "[no] TEXT,"
                + "[v1] INT,"
                + "[v2] INT,"
                + "[v3] INT,"
                + "[v4] INT,"
                + "[v5] INT,"
                + "[v6] INT,"
                + "[ex] INT)";
        db.execSQL(sBuffer);

         sBuffer = "CREATE TABLE [" + Consts.TABLE_mark + "] ("
                + "[auto_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + "[mark] TEXT,"
                + "[address] TEXT,"
                + "[ex] INT)";
        db.execSQL(sBuffer);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("beacon", "=========DROP TABLE==========");
        db.execSQL("DROP TABLE IF EXISTS " + Consts.TABLE_location);
        db.execSQL("DROP TABLE IF EXISTS " + Consts.TABLE_mark);
        this.onCreate(db);
    }


}
