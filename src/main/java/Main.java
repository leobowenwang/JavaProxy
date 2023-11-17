import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // Port to listen on
        int localPort = 3333;
        // Target server and port
        String remoteHost = "localhost";
        int remotePort = 8080;

        // Create server socket to listen for connections
        ServerSocket serverSocket = new ServerSocket(localPort);
        System.out.println("Listening on port " + localPort);

        while (true) {
            // Accept incoming connections
            Socket clientSocket = serverSocket.accept();
            System.out.println("Received connection from " + clientSocket);

            // Create a new thread for each connection
            new Thread(() -> handleRequest(clientSocket, remoteHost, remotePort)).start();
        }
    }

    private static void handleRequest(Socket clientSocket, String remoteHost, int remotePort) {
        try {
            // Connect to the target server
            Socket serverSocket = new Socket(remoteHost, remotePort);

            // Setup streams for communication
            DataInputStream clientIn = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream clientOut = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream serverIn = new DataInputStream(serverSocket.getInputStream());
            DataOutputStream serverOut = new DataOutputStream(serverSocket.getOutputStream());

            // Forward client's request to the server
            new Thread(() -> forwardData(clientIn, serverOut)).start();

            // Forward server's response to the client
            forwardData(serverIn, clientOut);

            // Close sockets
            clientSocket.close();
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void forwardData(DataInputStream in, DataOutputStream out) {
        try {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
