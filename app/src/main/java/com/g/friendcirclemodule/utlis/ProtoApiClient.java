package com.g.friendcirclemodule.utlis;

import static com.g.friendcirclemodule.activity.MainActivity.hostActivity;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.g.friendcirclemodule.R;
import com.google.protobuf.MessageLite;
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class ProtoApiClient {
    public static final String baseUrl = "http://10.0.2.2:5000";
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType MEDIA_TYPE_PROTO = MediaType.parse("application/octet-stream");

    public static <T extends MessageLite> T postProto(String path, MessageLite requestBodyObj, Class<T> respClass) throws IOException {
        RequestBody reqBody = RequestBody.create(requestBodyObj.toByteArray(), MEDIA_TYPE_PROTO);

        Request request = new Request.Builder()
                .url(baseUrl + path)
                .post(reqBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            byte[] respBytes = null;
            if (response.body() != null) {
                respBytes = response.body().bytes();
            }
            try {
                Method parseFromMethod = respClass.getMethod("parseFrom", byte[].class);
                @SuppressWarnings("unchecked")
                T respObj = (T) parseFromMethod.invoke(null, (Object) respBytes);
                return respObj;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     *
     * @param path 接口的路径
     * @param requestBodyObj 请求体proto对象
     * @param respClass 响应proto类class
     * @param activity 用于显示提示依托的activity
     * @param callback 用于处理请求结果后的逻辑处理
     * @param <T> 接口返回的类型
     */
    public static <T extends MessageLite> void achieveProto(String path, MessageLite requestBodyObj, Class<T> respClass, Activity activity, Consumer<T> callback) {
        new Thread(() -> {
            T respObj = null;
            try {
                respObj = postProto(path, requestBodyObj, respClass);
            } catch (IOException e) {
                Activity ac = hostActivity;
                if (ac != null) {
                    ac = activity;
                }
                Activity finalAc = ac;
                if (ac != null) {
                    ac.runOnUiThread(() ->
                            Toast.makeText(finalAc, R.string.tip_title_5, Toast.LENGTH_SHORT).show()
                    );
                }
            }
            if (respObj == null) return;
            T finalRespObj = respObj;
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(finalRespObj));
        }).start();
    }


}