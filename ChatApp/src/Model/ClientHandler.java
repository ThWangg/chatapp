	package Model;
	
	import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
	
	public class ClientHandler extends Thread {
	    private Socket socket;
	    private ObjectInputStream ois;
	    private ObjectOutputStream oos;
	    private String username;
	    // Map quản lý user -> ClientHandler toàn server
	    private Map<String, ClientHandler> clientMap; // Được truyền vào từ ServerController
	
	    // Danh sách user online (nên là thread-safe, ví dụ CopyOnWriteArrayList)
	    private List<String> onlineUsers;
	
	    public ClientHandler(Socket socket, Map<String, ClientHandler> clientMap, List<String> onlineUsers) {
	        this.socket = socket;
	        this.clientMap = clientMap;
	        this.onlineUsers = onlineUsers;
	        try {
	            oos = new ObjectOutputStream(socket.getOutputStream());
	            ois = new ObjectInputStream(socket.getInputStream());
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
	
	    public String getUsername() {
	        return username;
	    }
	
	    public void sendMessage(Message msg) {
	        try {
	            oos.writeObject(msg);
	            oos.flush();
	        } catch (Exception e) {
	            System.out.println("Lỗi gửi message đến " + username);
	        }
	    }
	
	    @Override
	    public void run() {
	        try {
	            // Nhận username từ client (hoặc có thể nhận object User)
	            Object first = ois.readObject();
	            if (first instanceof String) {
	                username = (String) first;
	            } else if (first instanceof User) {
	                username = ((User) first).getUsername();
	            }
	            // Đăng ký vào map
	            clientMap.put(username, this);
	            onlineUsers.add(username);
	
	            // Gửi thông báo đến mọi người
	            broadcast(new Message("Server", "all", username + " đã tham gia.", "text"));
	            sendUserListToAll();
	
	            // Lắng nghe message từ client
	            Object obj;
	            while ((obj = ois.readObject()) != null) {
	                if (!(obj instanceof Message)) continue;
	                Message msg = (Message) obj;
	                if ("all".equals(msg.getReceiverId())) {
	                    broadcast(msg); // chat chung, file, voice...
	                } else if ("private".equals(msg.getType())) {
	                    sendPrivate(msg.getReceiverId(), msg);
	                } else if ("file".equals(msg.getType()) || "voice".equals(msg.getType())) {
	                    // Gửi tới tất cả hoặc tới 1 user
	                    if ("all".equals(msg.getReceiverId()))
	                        broadcast(msg);
	                    else
	                        sendPrivate(msg.getReceiverId(), msg);
	                }
	            }
	        } catch (Exception e) {
	            System.out.println(username + " đã ngắt kết nối.");
	        } finally {
	            // Xoá user, thông báo logout
	            clientMap.remove(username);
	            onlineUsers.remove(username);
	            broadcast(new Message("Server", "all", username + " đã rời phòng.", "text"));
	            sendUserListToAll();
	            try { socket.close(); } catch (Exception ex) {}
	        }
	    }
	
	    private void broadcast(Message msg) {
	        for (ClientHandler handler : clientMap.values()) {
	            handler.sendMessage(msg);
	        }
	    }
	
	    private void sendPrivate(String toUser, Message msg) {
	        ClientHandler handler = clientMap.get(toUser);
	        if (handler != null) {
	            handler.sendMessage(msg);
	        }
	    }
	
	    private void sendUserListToAll() {
	        Message msg = new Message(onlineUsers);
	        msg.setType("userlist");
	        for (ClientHandler handler : clientMap.values()) {
	            handler.sendMessage(msg);
	        }
	    }
	}
