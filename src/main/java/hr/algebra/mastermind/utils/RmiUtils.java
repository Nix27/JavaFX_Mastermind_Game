package hr.algebra.mastermind.utils;

import hr.algebra.mastermind.chat.RemoteChat;
import hr.algebra.mastermind.chat.RemoteChatService;
import hr.algebra.mastermind.controller.MastermindController;
import hr.algebra.mastermind.enums.ConfigurationKey;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiUtils {
    private RmiUtils() {}

    public static void startRmiChatServer(){
        try {
            int rmiPort = ConfigurationReader.getIntValueOfKey(ConfigurationKey.RMI_PORT);
            int randomPortHint = ConfigurationReader.getIntValueOfKey(ConfigurationKey.RANDOM_PORT_HINT);

            Registry registry = LocateRegistry.createRegistry(rmiPort);
            MastermindController.remoteChatService = new RemoteChat();
            RemoteChatService skeleton = (RemoteChatService) UnicastRemoteObject.exportObject(MastermindController.remoteChatService, randomPortHint);
            registry.rebind(RemoteChatService.REMOTE_CHAT_OBJECT_NAME, skeleton);
            System.err.println("Object registered in RMI registry");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void startRmiChatClient(){
        try {
            int rmiPort = ConfigurationReader.getIntValueOfKey(ConfigurationKey.RMI_PORT);
            String host = ConfigurationReader.getStringValueOfKey(ConfigurationKey.HOST);

            Registry registry = LocateRegistry.getRegistry(host, rmiPort);
            MastermindController.remoteChatService = (RemoteChatService) registry.lookup(RemoteChatService.REMOTE_CHAT_OBJECT_NAME);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
