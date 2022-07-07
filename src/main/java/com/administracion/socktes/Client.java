package com.administracion.socktes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import javax.swing.JPanel;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    public static void main(String[] args) {
        ClientWindow window = new ClientWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class ClientWindow extends JFrame {

    public ClientWindow() {
        setBounds(600, 300, 280, 350);
        ClientPanel panel = new ClientPanel();
        add(panel);
        setVisible(true);
    }
}

class ClientData implements Serializable {

    private String name;
    private String message;
    private String ip;

    public ClientData() {
    }

    public ClientData(String name, String message, String ip) {
        this.name = name;
        this.message = message;
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}

class ClientPanel extends JPanel implements Runnable {

    private JTextField textf1, name;
    private JButton btnSend;
    private JTextArea resArea;

    public ClientPanel() {
        JLabel text = new JLabel("SOCKET CHAT");
        add(text);
        textf1 = new JTextField(20);
        resArea = new JTextArea(12, 20);
        btnSend = new JButton("Enviar");
        name = new JTextField(10);
        add(name);
        add(textf1);
        add(btnSend);
        add(resArea);
        name.setText("Nombre");
        SendData eventData = new SendData();
        btnSend.addActionListener(eventData);
        Thread thr = new Thread(this);
        thr.start();
    }

    @Override
    public void run() {
        try {
            ServerSocket remote = new ServerSocket(6565);
            Socket client;
            ClientData dataRecieved;
            while (true) {
                client = remote.accept();
                ObjectInputStream input = new ObjectInputStream(client.getInputStream());
                dataRecieved = (ClientData) input.readObject();
                resArea.append("\n" + dataRecieved.getName() + ": " + dataRecieved.getMessage());
                client.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private class SendData implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Socket csock = new Socket("192.168.1.16", 2882);

                ClientData data = new ClientData();
                data.setName(name.getText());
                data.setMessage(textf1.getText());
                //Obtener ip
                try (final DatagramSocket socket = new DatagramSocket()) {
                    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                    data.setIp(socket.getLocalAddress().getHostAddress());
                }

                /*URL url = new URL("http://checkip.amazonaws.com/");
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                data.setIp(br.readLine());*/

                ObjectOutputStream output = new ObjectOutputStream(csock.getOutputStream());
                output.writeObject(data);
                output.close();

                //DataOutputStream output = new DataOutputStream(csock.getOutputStream());
                //output.writeUTF(textf1.getText());
                //output.close();
                data.setMessage("");
                textf1.setText("");
            } catch (IOException ex) {
                Logger.getLogger(ClientPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
