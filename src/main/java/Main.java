import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) throws IOException {
        int localPort = 3333;
        String remoteHost = "localhost";
        int remotePort = 8080;

        try (ServerSocket serverSocket = new ServerSocket(localPort)) {
            System.out.println("Listening on port " + localPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Received connection from " + clientSocket);

                new Thread(() -> handleRequest(clientSocket, remoteHost, remotePort)).start();
            }
        }
    }

    private static void handleRequest(Socket clientSocket, String remoteHost, int remotePort) {
        try {
            Socket serverSocket = new Socket(remoteHost, remotePort);

            DataInputStream clientIn = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream clientOut = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream serverIn = new DataInputStream(serverSocket.getInputStream());
            DataOutputStream serverOut = new DataOutputStream(serverSocket.getOutputStream());

            new Thread(() -> forwardData(clientIn, serverOut)).start();

            forwardData(serverIn, clientOut);

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
