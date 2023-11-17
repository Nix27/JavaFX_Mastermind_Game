package hr.algebra.mastermind.utils;

import hr.algebra.mastermind.MastermindApplication;
import hr.algebra.mastermind.controller.MastermindController;
import javafx.scene.control.TextArea;

import java.rmi.RemoteException;
import java.util.List;

public class ChatUtils {
    private ChatUtils() {}

    public static void sendChatMessage(String message){
        try {
            MastermindController.remoteChatService.sendMessage(MastermindApplication.loggedInNetworkRole.name() + ": " + message);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static void refreshChatMessages(TextArea taChatMessages){
        taChatMessages.clear();

        try {
            List<String> allChatMessages = MastermindController.remoteChatService.getAllChatMessages();

            for(var msg : allChatMessages){
                taChatMessages.appendText(msg + System.lineSeparator());
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
