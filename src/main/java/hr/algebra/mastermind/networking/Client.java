package hr.algebra.mastermind.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private static void sendRequest(){
        try(Socket clientSocket = new Socket(Server.HOST, Server.PORT)){
            System.err.println("Client is connecting to: " + clientSocket.getInetAddress());
            sendSerializableRequest(clientSocket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendSerializableRequest(Socket clientSocket) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());


    }
}
