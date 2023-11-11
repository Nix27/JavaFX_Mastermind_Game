package hr.algebra.mastermind.chat;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class RemoteChat implements RemoteChatService {
    private List<String> chatMessages;

    public RemoteChat(){
        chatMessages = new ArrayList<>();
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        chatMessages.add(message);
    }

    @Override
    public List<String> getAllChatMessages() throws RemoteException {
        return new ArrayList<>(chatMessages);
    }
}
