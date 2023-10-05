import java.net.*;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import javax.swing.*;

public class Client extends JFrame {
    Socket client;
    BufferedReader br_in;
    PrintWriter pw_out;

    private JLabel label = new JLabel("Client");
    private JTextArea messageArea = new JTextArea();
    private JTextField input = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    public Client() {
        try {
            System.out.println("Sending request to server");
            client = new Socket("127.0.0.1", 2020);
            System.out.println("Connection established with server");
            br_in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            pw_out = new PrintWriter(client.getOutputStream());

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
        this.setTitle("Client End");
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
        // thread to read data from server
        Runnable r1 = () -> {
            System.out.println("Reader working fine.");
            try {
                while (true) {
                    // read message from the client
                    String msg = br_in.readLine();
                    if (msg.equals("exit chat")) {
                        // System.out.println("Server left the chat");
                        JOptionPane.showMessageDialog(this, "Server left the chat");
                        input.setEnabled(false);
                        client.close();
                        break;
                    }
                    // System.out.println("Server: " + msg);
                    messageArea.append("Server: " + msg + "\n");
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
