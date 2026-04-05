/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatapp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class GuiClient extends JFrame {
    private JTextField txtIP;
    private JTextField txtPort;
    private JButton btnConnect;
    private JTextArea chatArea;
    private JTextField txtMessage;
    private JButton btnSend;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean connected = false;

    public GuiClient() {
        setTitle("Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JLabel lblIP = new JLabel("IP:");
        txtIP = new JTextField("127.0.0.1", 10);

        JLabel lblPort = new JLabel("Port:");
        txtPort = new JTextField("8080", 8);

        btnConnect = new JButton("Connect");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(lblIP);
        topPanel.add(txtIP);
        topPanel.add(lblPort);
        topPanel.add(txtPort);
        topPanel.add(btnConnect);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        txtMessage = new JTextField();
        btnSend = new JButton("Send");

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(txtMessage, BorderLayout.CENTER);
        bottomPanel.add(btnSend, BorderLayout.EAST);

        setLayout(new BorderLayout(5, 5));
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        btnConnect.addActionListener(e -> connectToServer());
        btnSend.addActionListener(e -> sendMessage());
        txtMessage.addActionListener(e -> sendMessage());
    }

    private void connectToServer() {
        if (connected) return;

        String host = txtIP.getText().trim();
        int port;

        try {
            port = Integer.parseInt(txtPort.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid port.");
            return;
        }

        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;

            append("Connected to server.");
            btnConnect.setEnabled(false);
            txtIP.setEditable(false);
            txtPort.setEditable(false);

            Thread receiveThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        append(response);
                    }
                } catch (IOException e) {
                    append("Disconnected from server.");
                }
            });
            receiveThread.start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot connect: " + e.getMessage());
        }
    }

    private void sendMessage() {
        String msg = txtMessage.getText().trim();
        if (msg.isEmpty() || !connected) return;

        out.println(msg);
        txtMessage.setText("");
    }

    private void append(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiClient().setVisible(true));
    }
}
