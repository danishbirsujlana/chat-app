import java.net.*;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import javax.swing.*;

public class Server extends JFrame {
    ServerSocket server;
    Socket socket;
    BufferedReader br_in;
    PrintWriter pw_out;

    private JLabel label = new JLabel("Server");
    private JTextArea messageArea = new JTextArea();
    private JTextField input = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

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

            createGUI();
            handleEvents();

            startReading();
            // startWriting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEvents() {
        input.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    String content = input.getText();
                    messageArea.append("Me: " + content + "\n");
                    pw_out.println(content);
                    pw_out.flush();
                    input.setText("");
                }
            }

        });
    }

    private void createGUI() {
        // GUI code
        this.setTitle("Server End");
        this.setSize(550, 750);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // inserting components
        messageArea.setEditable(false);
        label.setFont(font);
        messageArea.setFont(font);
        input.setFont(font);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // set layout
        this.setLayout(new BorderLayout());

        // adding components
        this.add(label, BorderLayout.NORTH);
        JScrollPane jscroll = new JScrollPane(messageArea);
        this.add(jscroll, BorderLayout.CENTER);
        this.add(input, BorderLayout.SOUTH);

        this.setVisible(true);
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
                        // System.out.println("Client left the chat");
                        JOptionPane.showMessageDialog(this, "CLient left the chat");
                        input.setEnabled(false);
                        socket.close();
                        break;
                    }
                    // System.out.println("Client: " + msg);
                    messageArea.append("Client: " + msg + "\n");
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