package Main;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Model.Message;

public class ClientHandler extends Thread {
    private Socket socket;
    private String username;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public ClientHandler(Socket socket, String username, ObjectInputStream ois, ObjectOutputStream oos) {
        this.socket = socket;
        this.username = username;
        this.ois = ois;
        this.oos = oos;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object obj = ois.readObject();
                if (obj instanceof Message) {
                    Message msg = (Message) obj;
                    // Chat all hoặc broadcast
                    if ("all".equalsIgnoreCase(msg.getReceiverId()) || "All".equalsIgnoreCase(msg.getReceiverId())) {
                        ChatServer.broadcastMessage(msg);
                    } else {
                        // Chat riêng
                        ChatServer.sendPrivate(msg.getReceiverId(), msg);
                    }
                }
            }
        } catch (Exception e) {
            // Xử lý client disconnect
            ChatServer.onlineUsers.remove(username);
            ChatServer.broadcastUserList();
            try { socket.close(); } catch (Exception ex) {}
        }
    }

    public void sendMessage(Message msg) {
        try {
            oos.writeObject(msg);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
