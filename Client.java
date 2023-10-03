import java.net.*;
import java.io.*;

public class Client {
    Socket client;
    BufferedReader br_in;
    PrintWriter pw_out;

    public Client() {
        try {
            System.out.println("Sending request to server");
            client = new Socket("127.0.0.1", 2020);
            System.out.println("Connection established with server");

            br_in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            pw_out = new PrintWriter(client.getOutputStream());

            startReading();
            startWriting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startReading() {
        // thread to read data from server
        Runnable r1 = () -> {
            System.out.println("Reader working fine.");
            try {
                while (true) {
                    // read message from the client
                    String msg = br_in.readLine();
                    if (msg.equals("exit chat")) {
                        System.out.println("Server left the chat");
                        client.close();
                        break;
                    }
                    System.out.println("Server: " + msg);
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("Connection closed.");
            }
        };
        new Thread(r1).start();
    }

    public void startWriting() {
        // thread to get data from user and send to server
        Runnable r2 = () -> {
            System.out.println("Writer working fine.");
            try {
                while (!client.isClosed()) {
                    BufferedReader content = new BufferedReader(new InputStreamReader(System.in));
                    String msg = content.readLine();
                    pw_out.println(msg);
                    pw_out.flush();

                    if (msg.equals("exit chat")) {
                        client.close();
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
        System.out.println("Client started...");
        new Client();
    }
}
