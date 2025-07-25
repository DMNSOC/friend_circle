package com.g.friendcirclemodule.uc;

import static com.g.friendcirclemodule.uc.ProtoApiClient.baseUrl;
import io.socket.client.IO;
import io.socket.client.Socket;
import user.UserOuterClass;
import org.json.JSONObject;
import java.net.URISyntaxException;

public class WebSocketManager {
    private Socket socket;
    public void connect(int myUserId, Runnable callback) {
        try {
            socket = IO.socket(baseUrl);
            // 连接成功后注册身份
            socket.on(Socket.EVENT_CONNECT, args -> {
                try {
                    JSONObject reg = new JSONObject();
                    reg.put("user_id", myUserId);
                    socket.emit("register", reg);
                    System.out.println("WebSocket 注册成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // 监听用户事件
            socket.on("user_updated", args -> {
                try {
                    // args[0] 是 byte[]
                    byte[] pbData = (byte[]) args[0];
                    // Protobuf 解析
                    UserOuterClass.User user = UserOuterClass.User.parseFrom(pbData);
                    System.out.println("收到用户更新: " + user.getId() + " " + user.getUseId());
                    callback.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            socket.on(Socket.EVENT_DISCONNECT, args -> {
                System.out.println("WebSocket 已断开");
            });

            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }
}
