package Model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class Message implements Serializable {
    private int id;
    private String senderId;
    private String receiverId; // "all" hoặc username
    private String content;
    private String type; // "text", "file", "image", "voice", "private", "userlist"
    private String fileName;
    private byte[] fileData;
    private List<String> userList;
    private Timestamp timestamp;

    public Message(String senderId, String receiverId, String content, String type) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.type = type;
    }
    public Message(List<String> userList) {
        this.type = "userlist";
        this.userList = userList;
    }

    // Getter/setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }
    public List<String> getUserList() { return userList; }
    public void setUserList(List<String> userList) { this.userList = userList; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}