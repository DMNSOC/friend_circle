package com.g.friendcirclemodule.dp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(@Nullable Context context) {
        super(context, "data.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表 int friendName, int friendHead, String decStr, Integer[] friendImageId, int time, Integer friendVideoId
        String sql1 = "create table accounttb(id integer primary key autoincrement,useId integer,decStr varchar(80),friendImageId varchar(80)," +
                "time varchar(60), friendVideoId varchar(80), friendVideoTime varchar(80))";
        db.execSQL(sql1);
        String sql2 = "create table userinfo(id integer primary key autoincrement, useId integer, friendName varchar(60),friendHead varchar(60), friendBg varchar(60))";
        db.execSQL(sql2);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
