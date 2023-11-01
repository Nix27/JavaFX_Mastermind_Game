package hr.algebra.mastermind.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final String HOST = "localhost";
    public static final int PORT = 1989;

    private static void acceptRequests(){
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.err.println("Server listening on port:" + serverSocket.getLocalPort());

            while (true){
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());

                new Thread(() -> processSerializableClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processSerializableClient(Socket clientSocket) {
        try(ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())){

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
