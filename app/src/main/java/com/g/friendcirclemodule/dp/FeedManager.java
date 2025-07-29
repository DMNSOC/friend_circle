package com.g.friendcirclemodule.dp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.g.friendcirclemodule.uc.ProtoApiClient;
import com.g.friendcirclemodule.utlis.UtilityMethod;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import user.UserOuterClass;

public class FeedManager {
    private static SQLiteDatabase db;
    public static void initDB(Context context) {
        DBOpenHelper helper = new DBOpenHelper(context);
        db = helper.getWritableDatabase();

        int uid = UtilityMethod.getUniqueId(context);
        UserOuterClass.Info info = UserOuterClass.Info.newBuilder()
                .setUseId(uid)
                .setFriendName("")
                .setFriendHead("")
                .setFriendBg("")
                .build();

        ProtoApiClient.achieveProto("/create_info", info, UserOuterClass.Info.class, null, result -> {
            UpdateUseInfo();
        });
    }


    public static List<CommentBase> getCommentList(int gId){
        List<CommentBase> list = new ArrayList<>();
        String sql = "SELECT * FROM comment WHERE groupId=? ORDER BY id DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(gId)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int groupId = cursor.getInt(cursor.getColumnIndexOrThrow("groupId"));
            String commenter = cursor.getString(cursor.getColumnIndexOrThrow("commenter"));
            String replyTo = cursor.getString(cursor.getColumnIndexOrThrow("replyTo"));
            String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            CommentBase bean = new CommentBase(id, groupId, commenter, replyTo, content);
            list.add(bean);
        }
        return list;
    }
    /*
    表插入
     */
    public static void InsertItemToComment(CommentBase bean){
        ContentValues values = new ContentValues();
        values.put("id",bean.getId());
        values.put("groupId",bean.getGroupId());
        values.put("commenter",bean.getCommenter());
        values.put("replyTo",bean.getReplyTo());
        values.put("content",bean.getContent());
        db.insert("comment", null,values);
    }

    public static void deleteAllComment() {
        String sql = "DELETE FROM comment";
        db.execSQL(sql);

    }

    public static List<DMEntryUseInfoBase> getUseInfo(int uId){
        List<DMEntryUseInfoBase> list = new ArrayList<>();
        String sql = "SELECT * FROM userinfo WHERE useId=? ORDER BY useId DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(uId)});
        while (cursor.moveToNext()) {
            long id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int useId = cursor.getInt(cursor.getColumnIndexOrThrow("useId"));
            String friendName = cursor.getString(cursor.getColumnIndexOrThrow("friendName"));
            String friendHead = cursor.getString(cursor.getColumnIndexOrThrow("friendHead"));
            String friendBg = cursor.getString(cursor.getColumnIndexOrThrow("friendBg"));
            DMEntryUseInfoBase typeBean = new DMEntryUseInfoBase(id, useId, friendName, friendHead, friendBg);
            list.add(typeBean);
        }
        return list;
    }
    public static void UpdateUseInfo(){
        UserOuterClass.Empty infoEmpty = UserOuterClass.Empty.newBuilder().build();
        ProtoApiClient.achieveProto("/list_info", infoEmpty, UserOuterClass.InfoList.class, null, result -> {
            Log.i("dddddddd", String.valueOf(result.getInfosList()));
            for (UserOuterClass.Info info : result.getInfosList()) {

                ContentValues values = new ContentValues();
                values.put("id", info.getId());
                values.put("useId",info.getUseId());
                if (!Objects.equals(info.getFriendName(), "")) {
                    values.put("friendName",info.getFriendName());
                }
                if (!Objects.equals(info.getFriendHead(), "")) {
                    values.put("friendHead",info.getFriendHead());
                }
                if (!Objects.equals(info.getFriendBg(), "")) {
                    values.put("friendBg",info.getFriendBg());
                }
                db.insertWithOnConflict("userinfo", null, values, SQLiteDatabase.CONFLICT_REPLACE);
//                Log.i("Testttttttt", String.valueOf(values));
            }
        });
    }

    /*
    用户信息表插入修改
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
        if (!Objects.equals(bean.getFriendBg(), "")) {
            values.put("friendBg",bean.getFriendBg());
        }
        db.insertWithOnConflict("userinfo", null, values, SQLiteDatabase.CONFLICT_REPLACE);
//        Log.i("Testttttttt", String.valueOf(values));
    }


}
