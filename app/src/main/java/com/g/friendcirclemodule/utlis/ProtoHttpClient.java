package com.g.friendcirclemodule.utlis;

import android.util.Log;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import user.UserOuterClass;

public class ProtoHttpClient {
    private static final String BASE_URL = "http://10.0.2.2:5000";
    private static final MediaType MEDIA_TYPE_PROTO = MediaType.parse("application/octet-stream");
    private final OkHttpClient client = new OkHttpClient();

    // 缓存单挑信息
    public void createUser(int useId, String decStr, String friendImageId,
                                            String time, String friendVideoId, String friendVideoTime,
                                            int likeState, String likesId) throws IOException {
        try {
            UserOuterClass.User user = UserOuterClass.User.newBuilder()
                    .setUseId(useId)
                    .setDecStr(decStr)
                    .setFriendImageId(friendImageId)
                    .setTimeStr(time)
                    .setFriendVideoId(friendVideoId)
                    .setFriendVideoTime(friendVideoTime)
                    .setLikeState(likeState)
                    .setLikesId(likesId)
                    .build();


            Log.i("111111", "服务端返回: 1");
            RequestBody body = RequestBody.create(
                    user.toByteArray(),
                    MEDIA_TYPE_PROTO
            );

            Log.i("111111", "服务端返回: 2");
            Request request = new Request.Builder()
                    .url(BASE_URL + "/create_user")
                    .post(body)
                    .build();

            Log.i("111111", "服务端返回: 3 " + request);
            Response response = client.newCall(request).execute();
            Log.i("111111", "服务端返回: 4 " + response);

            if (response.isSuccessful() && response.body() != null) {
                byte[] respBytes = response.body().bytes();

                // 3. 解析 Protobuf 响应消息
                UserOuterClass.UserId reply = UserOuterClass.UserId.parseFrom(respBytes);

                Log.i("111111", "服务端返回: " + reply.getId());
            } else {
                Log.e("111111", "请求失败: " + response.code());
            }

        } catch (IOException e) {
            Log.e("111111", "IO异常", e);
        }
    }

    // 获取全部消息
    public UserOuterClass.UserList listUsers() throws IOException {
        try {
            UserOuterClass.Empty empty = UserOuterClass.Empty.newBuilder().build();
            Log.i("111111", "服务端返回: 1");
            RequestBody body = RequestBody.create(
                    empty.toByteArray(),
                    MEDIA_TYPE_PROTO
            );
            Log.i("111111", "服务端返回: 2");
            Request request = new Request.Builder()
                    .url(BASE_URL + "/list_users")
                    .post(body)
                    .build();
            Log.i("111111", "服务端返回: 3 " + request);
            Response response = client.newCall(request).execute();
            Log.i("111111", "服务端返回: 4 " + response + response.isSuccessful());

            if (response.isSuccessful() && response.body() != null) {
                byte[] respBytes = response.body().bytes();

                return UserOuterClass.UserList.parseFrom(respBytes);
            } else {
                Log.e("111111", "请求失败: " + response.code());
            }

        } catch (IOException e) {
            Log.e("111111", "IO异常", e);
        }
        return null;
    }
}