/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatapp;

import java.io.*;
import java.net.*;

public class DownloadHomepage {
    public static void main(String[] args) {
        String host = "example.com";
        int port = 80;

        try {
            Socket socket = new Socket(host, port);

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            BufferedWriter file = new BufferedWriter(
                    new FileWriter("homepage.html"));

            out.write("GET / HTTP/1.1\r\n");
            out.write("Host: " + host + "\r\n");
            out.write("Connection: close\r\n");
            out.write("\r\n");
            out.flush();

            String line;
            boolean isBody = false;

            while ((line = in.readLine()) != null) {
                // End of HTTP headers
                if (!isBody) {
                    if (line.isEmpty()) {
                        isBody = true;
                    }
                    continue;
                }

                if (line.matches("^[0-9a-fA-F]+$")) {
                    continue;
                }

                // End of chunked body
                if (line.equals("0")) {
                    break;
                }

                file.write(line);
                file.newLine();
            }

            file.close();
            in.close();
            out.close();
            socket.close();

            System.out.println("Download complete! File saved as homepage.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}