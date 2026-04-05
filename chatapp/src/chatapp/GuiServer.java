/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatapp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class GuiServer extends JFrame {
    private JTextField txtPort;
    private JButton btnStart;
    private JTextArea chatArea;
    private JTextField txtMessage;
    private JButton btnSend;

    private ServerSocket serverSocket;
    private final List<ClientConnection> clients = new ArrayList<>();
    private boolean running = false;

    public GuiServer() {
        setTitle("Server");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JLabel lblPort = new JLabel("Port:");
        txtPort = new JTextField("8080", 8);
        btnStart = new JButton("Start");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(lblPort);
        topPanel.add(txtPort);
        topPanel.add(btnStart);

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

        btnStart.addActionListener(e -> startServer());
        btnSend.addActionListener(e -> sendServerMessage());
        txtMessage.addActionListener(e -> sendServerMessage());
    }

    private void startServer() {
        if (running) return;

        int port;
        try {
            port = Integer.parseInt(txtPort.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid port.");
            return;
        }

        try {
            serverSocket = new ServerSocket(port);
            running = true;
            btnStart.setEnabled(false);
            txtPort.setEditable(false);
            append("Server is running on port " + port);

            Thread acceptThread = new Thread(() -> {
                while (running) {
                    try {
                        Socket socket = serverSocket.accept();
                        append("New client connected: " + socket);

                        ClientConnection client = new ClientConnection(socket);
                        synchronized (clients) {
                            clients.add(client);
                        }
                        client.start();
                    } catch (IOException e) {
                        if (running) {
                            append("Accept error: " + e.getMessage());
                        }
                    }
                }
            });
            acceptThread.start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot start server: " + e.getMessage());
        }
    }

    private void sendServerMessage() {
        String msg = txtMessage.getText().trim();
        if (msg.isEmpty()) return;

        String full = "Server: " + msg;
        append(full);
        broadcast(full, null);
        txtMessage.setText("");
    }

    private void broadcast(String message, ClientConnection exclude) {
        synchronized (clients) {
            for (ClientConnection client : clients) {
                if (client != exclude) {
                    client.send(message);
                }
            }
        }
    }

    private void append(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    private class ClientConnection extends Thread {
        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientConnection(Socket socket) {
            this.socket = socket;
        }

        public void send(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Connected to server.");

                String message;
                while ((message = in.readLine()) != null) {
                    String full = "Client: " + message;
                    append(full);
                    broadcast(full, this);
                }
            } catch (IOException e) {
                append("Client disconnected.");
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
                synchronized (clients) {
                    clients.remove(this);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiServer().setVisible(true));
    }
}
