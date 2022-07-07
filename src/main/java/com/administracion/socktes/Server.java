package com.administracion.socktes;

import java.awt.BorderLayout;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Server {

    public static void main(String[] args) {
        ServerWindow serverw = new ServerWindow();
        serverw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class ServerWindow extends JFrame implements Runnable {

    JTextArea textarea;

    public ServerWindow() {
        setBounds(1200, 300, 280, 350);
        JPanel window = new JPanel();
        window.setLayout(new BorderLayout());
        textarea = new JTextArea();
        window.add(textarea, BorderLayout.CENTER);
        add(window);
        setVisible(true);

        Thread process = new Thread(this);
        process.start();
    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(2882);

            String name, message, ip;
            ClientData dataRecieved;
            
            while (true) {
                Socket socket = server.accept();
                
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                dataRecieved = (ClientData) input.readObject();
                name = dataRecieved.getName();
                message = dataRecieved.getMessage();
                ip = dataRecieved.getIp();
                
                //DataInputStream input = new DataInputStream(socket.getInputStream());
                //String message = input.readUTF();
                
                textarea.append("\n" + name + ": " + message + " - " + ip);
                
                Socket envio = new Socket(ip, 6565);
                ObjectOutputStream output = new ObjectOutputStream(envio.getOutputStream());
                output.writeObject(dataRecieved);
                envio.close();                
                
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
