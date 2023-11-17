package hr.algebra.mastermind.utils;

import hr.algebra.mastermind.chat.RemoteChat;
import hr.algebra.mastermind.chat.RemoteChatService;
import hr.algebra.mastermind.controller.MastermindController;
import hr.algebra.mastermind.networking.NetworkConfiguration;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiUtils {
    private RmiUtils() {}

    public static void startRmiChatServer(){
        try {
            Registry registry = LocateRegistry.createRegistry(NetworkConfiguration.RMI_PORT);
            MastermindController.remoteChatService = new RemoteChat();
            RemoteChatService skeleton = (RemoteChatService) UnicastRemoteObject.exportObject(MastermindController.remoteChatService, NetworkConfiguration.RANDOM_PORT_HINT);
            registry.rebind(RemoteChatService.REMOTE_CHAT_OBJECT_NAME, skeleton);
            System.err.println("Object registered in RMI registry");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void startRmiChatClient(){
        try {
            Registry registry = LocateRegistry.getRegistry(NetworkConfiguration.HOST, NetworkConfiguration.RMI_PORT);
            MastermindController.remoteChatService = (RemoteChatService) registry.lookup(RemoteChatService.REMOTE_CHAT_OBJECT_NAME);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
