package Main;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import Model.Message;

public class ChatServer {
    public static final int PORT = 8000;
    // Danh sách user đang online: username -> ClientHandler
    public static final Map<String, ClientHandler> onlineUsers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String username = (String) ois.readObject();

                ClientHandler handler = new ClientHandler(socket, username, ois, oos);
                onlineUsers.put(username, handler);
                handler.start();

                broadcastUserList(); // gửi lại danh sách user mỗi lần có user login mới
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Broadcast danh sách user online cho tất cả client
    public static void broadcastUserList() {
        List<String> users = new ArrayList<>(onlineUsers.keySet());
        Message msg = new Message(users);
        msg.setType("userlist");
        for (ClientHandler ch : onlineUsers.values()) {
            ch.sendMessage(msg);
        }
    }

    // Broadcast 1 message tới tất cả client (vd: chat All)
    public static void broadcastMessage(Message msg) {
        for (ClientHandler ch : onlineUsers.values()) {
            ch.sendMessage(msg);
        }
    }

    // Gửi message tới một client cụ thể
    public static void sendPrivate(String toUser, Message msg) {
        ClientHandler ch = onlineUsers.get(toUser);
        if (ch != null) {
            ch.sendMessage(msg);
        }
    }
}
