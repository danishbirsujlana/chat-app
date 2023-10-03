import java.io.*;
import java.net.*;

public class Server {
    ServerSocket server;
    Socket socket;
    BufferedReader br_in;
    PrintWriter pw_out;

    // Constructor to initialize server object
    public Server() {
        try {
            server = new ServerSocket(2020); // 2020 -> port number
            System.out.println("Server ready to accept connections");
            System.out.println("Wating....");

            // server accept the socket connection and return the socket object of accepeted
            // connection
            socket = server.accept();
            System.out.println("Connecte with client");

            br_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw_out = new PrintWriter(socket.getOutputStream());

            startReading();
            startWriting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startReading() {
        // thread to read data from client
        Runnable r1 = () -> {
            System.out.println("Reader working fine.");
            try {
                while (true) {
                    // read message from the client
                    String msg = br_in.readLine();
                    if (msg.equals("exit chat")) {
                        System.out.println("Client left the chat");
                        socket.close();
                        break;
                    }
                    System.out.println("Client: " + msg);
                }
            } catch (Exception e) {
                System.out.println("Connection closed.");
            }
        };
        new Thread(r1).start();
    }

    public void startWriting() {
        // thread to get data from user and send to client
        Runnable r2 = () -> {
            System.out.println("Writer working fine.");
            try {
                while (!socket.isClosed()) {
                    BufferedReader content = new BufferedReader(new InputStreamReader(System.in));
                    String msg = content.readLine();
                    pw_out.println(msg);
                    pw_out.flush();

                    if (msg.equals("exit chat")) {
                        socket.close();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Connection closed.");
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("Server started...");
        new Server();
    }
}