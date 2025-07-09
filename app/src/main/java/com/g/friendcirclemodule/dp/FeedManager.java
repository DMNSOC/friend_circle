package com.g.friendcirclemodule.dp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FeedManager {
    private static SQLiteDatabase db;
    public static void initDB(Context context) {
        DBOpenHelper helper = new DBOpenHelper(context);
        db = helper.getWritableDatabase();
    }
    // int friendName, int friendHead, String decStr, Integer[] friendImageId, String time, Integer friendVideoId
    public static List<DMEntryBase>getTypeList(){
        List<DMEntryBase> list = new ArrayList<>();
        String sql = "SELECT * FROM accounttb ORDER BY id DESC";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int useId = cursor.getInt(cursor.getColumnIndexOrThrow("useId"));
            String decStr = cursor.getString(cursor.getColumnIndexOrThrow("decStr"));
            String friendImageId = cursor.getString(cursor.getColumnIndexOrThrow("friendImageId"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            String friendVideoId = cursor.getString(cursor.getColumnIndexOrThrow("friendVideoId"));
            String friendVideoTime = cursor.getString(cursor.getColumnIndexOrThrow("friendVideoTime"));
            DMEntryBase typeBean = new DMEntryBase(id, useId, decStr, friendImageId, time, friendVideoId, friendVideoTime);
            list.add(typeBean);
        }
        return list;
    }
    public static List<DMEntryUseInfoBase>getUseInfo(long lId, int uId){
        List<DMEntryUseInfoBase> list = new ArrayList<>();
        String sql = "SELECT * FROM userinfo WHERE id=? and useId=? ORDER BY id DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(lId), String.valueOf(uId)});
        while (cursor.moveToNext()) {
            long id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int useId = cursor.getInt(cursor.getColumnIndexOrThrow("useId"));
            String friendName = cursor.getString(cursor.getColumnIndexOrThrow("friendName"));
            String friendHead = cursor.getString(cursor.getColumnIndexOrThrow("friendHead"));
            DMEntryUseInfoBase typeBean = new DMEntryUseInfoBase(id, useId, friendName, friendHead);
            list.add(typeBean);
        }
        return list;
    }
    /*
    表插入
     */
    public static void InsertItemToAccounttb(DMEntryBase bean){
        ContentValues values = new ContentValues();
        values.put("id",bean.getId());
        values.put("useId",bean.getUseId());
        values.put("decStr",bean.getDecStr());
        values.put("friendImageId",bean.getFriendImageId());
        values.put("time",bean.getTime());
        values.put("friendVideoId",bean.getFriendVideoId());
        values.put("friendVideoTime",bean.getFriendVideoTime());
        db.insert("accounttb", null,values);
    }

    public static int deleteItemFromAccounttbById(int id) {
        int i = db.delete("accounttb", "id=?", new String[]{id + ""});
        return i;
    };

    /*
    表插入
     */
    public static void InsertItemToUserInfo(DMEntryUseInfoBase bean){
        ContentValues values = new ContentValues();
        values.put("id",bean.getId());
        values.put("useId",bean.getUseId());
        if (!Objects.equals(bean.getFriendName(), "")) {
            values.put("friendName",bean.getFriendName());
        }
        if (!Objects.equals(bean.getFriendHead(), "")) {
            values.put("friendHead",bean.getFriendHead());
        }
        db.insertWithOnConflict("userinfo", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


}
