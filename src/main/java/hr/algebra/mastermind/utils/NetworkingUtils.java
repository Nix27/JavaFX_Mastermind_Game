package hr.algebra.mastermind.utils;

import hr.algebra.mastermind.model.GameState;
import hr.algebra.mastermind.networking.NetworkConfiguration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkingUtils {
    private NetworkingUtils() {}

    public static void sendGameStateToServer(GameState gameState){
        sendRequest(gameState);
    }

    public static void sendRequest(GameState gameState){
        try(Socket clientSocket = new Socket(NetworkConfiguration.HOST, NetworkConfiguration.PORT)){
            System.err.println("Client is connecting to: " + clientSocket.getInetAddress());
            sendSerializableRequest(clientSocket, gameState);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendSerializableRequest(Socket clientSocket, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

        oos.writeObject(gameState);
        System.out.println("Confirmation received: " + (String) ois.readObject());
    }
}
