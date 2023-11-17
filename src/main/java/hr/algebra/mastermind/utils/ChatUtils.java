package hr.algebra.mastermind.utils;

import hr.algebra.mastermind.MastermindApplication;
import hr.algebra.mastermind.controller.MastermindController;

import java.rmi.RemoteException;

public class ChatUtils {
    private ChatUtils() {}

    public static void sendChatMessage(String message){
        try {
            MastermindController.remoteChatService.sendMessage(MastermindApplication.loggedInNetworkRole.name() + ": " + message);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
