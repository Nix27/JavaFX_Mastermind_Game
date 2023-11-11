package hr.algebra.mastermind;

import hr.algebra.mastermind.chat.RemoteChat;
import hr.algebra.mastermind.chat.RemoteChatService;
import hr.algebra.mastermind.controller.MastermindController;
import hr.algebra.mastermind.enums.NetworkRole;
import hr.algebra.mastermind.model.GameState;
import hr.algebra.mastermind.networking.NetworkConfiguration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MastermindApplication extends Application {
    public static NetworkRole loggedInNetworkRole;
    private static MastermindController mastermindController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MastermindApplication.class.getResource("view/mastermind.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 870, 700);
        stage.setTitle(loggedInNetworkRole.name());
        stage.setScene(scene);
        stage.show();

        mastermindController = fxmlLoader.getController();
    }

    public static void main(String[] args) {
        new Thread(Application::launch).start();

        String networkRoleFromArgs = args[0];
        loggedInNetworkRole = NetworkRole.CLIENT;
        boolean loggedIn = false;

        for(var nr : NetworkRole.values()){
            if(nr.name().equals(networkRoleFromArgs)){
                loggedInNetworkRole = nr;
                loggedIn = true;
                break;
            }
        }

        if(loggedIn){
            if(loggedInNetworkRole.name().equals(NetworkRole.SERVER.name())){
                startServer();
            }else {
                startClient();
            }
        }

    }

    private static void startServer(){
        acceptRequestsOnServer();
    }

    private static void acceptRequestsOnServer(){
        try(ServerSocket serverSocket = new ServerSocket(NetworkConfiguration.SERVER_PORT)){
            System.err.println("Server listening on port:" + serverSocket.getLocalPort());

            while (true){
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());

                Platform.runLater(() -> processSerializableClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startClient(){
        acceptRequestsOnClient();
    }

    private static void acceptRequestsOnClient(){
        try(ServerSocket serverSocket = new ServerSocket(NetworkConfiguration.CLIENT_PORT)){
            System.err.println("Server listening on port:" + serverSocket.getLocalPort());

            while (true){
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());

                Platform.runLater(() -> processSerializableClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processSerializableClient(Socket clientSocket) {
        try(ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())){
            GameState gameState = (GameState) ois.readObject();
            mastermindController.loadGameState(gameState);
            System.out.println("Game state received.");
            oos.writeObject("Confirmation");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}