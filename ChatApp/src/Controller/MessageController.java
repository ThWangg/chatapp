package Controller;

import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageController {
    private MessageDAO messageDAO = new MessageDAO();

    public void sendTextMessage(String sender, String receiver, String content) {
        Message msg = new Message(sender, receiver, content, receiver.equalsIgnoreCase("all") ? "text" : "private");
        messageDAO.saveMessage(msg);
    }

    public List<Message> getPrivChatHistory(String user1, String user2) {
        return messageDAO.getPrivateChatHistory(user1, user2);
    }

//    public void sendFileMessage(String sender, String receiver, String fileName, byte[] fileData) {
//        Message msg = new Message(sender, receiver, "[File] " + fileName, "file");
//        msg.setFileName(fileName);
//        msg.setFileData(fileData);
//        messageDAO.saveMessage(msg);
//    }
//
//    public void sendImageMessage(String sender, String receiver, String fileName, byte[] fileData) {
//        Message msg = new Message(sender, receiver, "[Image] " + fileName, "image");
//        msg.setFileName(fileName);
//        msg.setFileData(fileData);
//        messageDAO.saveMessage(msg);
//    }
//
//    public void sendVoiceMessage(String sender, String receiver, String fileName, byte[] fileData) {
//        Message msg = new Message(sender, receiver, "[Voice] " + fileName, "voice");
//        msg.setFileName(fileName);
//        msg.setFileData(fileData);
//        messageDAO.saveMessage(msg);
//    }

}
