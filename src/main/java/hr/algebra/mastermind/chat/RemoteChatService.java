package hr.algebra.mastermind.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteChatService extends Remote {
    String REMOTE_CHAT_OBJECT_NAME = "hr.algebra.mastermind.chat";

    void sendMessage(String message) throws RemoteException;
    List<String> getAllChatMessages() throws RemoteException;
}
