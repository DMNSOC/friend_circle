package com.g.friendcirclemodule.uc;

import static com.g.friendcirclemodule.uc.ProtoApiClient.baseUrl;
import io.socket.client.IO;
import io.socket.client.Socket;
import user.UserOuterClass;
import org.json.JSONObject;
import java.net.URISyntaxException;

public class WebSocketManager {
    private Socket socket;

    // 使用uid连接注册身份
    public void connect(int myUserId, Runnable connectCallback) {
        try {
            socket = IO.socket(baseUrl);
            socket.on(Socket.EVENT_CONNECT, args -> {
                try {
                    JSONObject reg = new JSONObject();
                    reg.put("user_id", myUserId);
                    socket.emit("register", reg);
                    System.out.println("WebSocket 注册成功");
                    connectCallback.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    // 断开处理
    public void discon(Runnable disconCallback) {
        if (socket != null) {

            socket.on(Socket.EVENT_DISCONNECT, args -> {
                System.out.println("WebSocket 已断开");
                disconCallback.run();
            });

            socket.connect();
        }

    }
    // 监听用户事件
    public void userUpdatedEvent(Runnable userCallback) {
        if (socket != null) {
            socket.on("user_updated", args -> {
                try {
                    byte[] pbData = (byte[]) args[0];
                    UserOuterClass.User user = UserOuterClass.User.parseFrom(pbData);
                    System.out.println("收到用户更新: " + user.getId() + " " + user.getUseId());
                    userCallback.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            socket.connect();
        }
    }
    // 监听用户修改信息事件
    public void infoUpdatedEvent(Runnable infoCallback) {
        if (socket != null) {
            socket.on("info_updated", args -> {
                try {
                    byte[] pbData = (byte[]) args[0];
                    UserOuterClass.User user = UserOuterClass.User.parseFrom(pbData);
                    System.out.println("收到用户更新: " + user.getId() + " " + user.getUseId());
                    infoCallback.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            socket.connect();
        }
    }
    // 结束清理
    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }
}
