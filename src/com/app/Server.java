package com.app;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kunlong Wang
 * @create 2023-03-02 1:44 PM
 */
public class Server implements Runnable {
    private int port;
    private Map<Integer, Integer> contacts = new HashMap<>();
    private boolean flag = true;

    TopoNode topoNode;


    public Server(int port, TopoNode topoNode) {
        this.port = port;
        this.topoNode = topoNode;
    }

    public void startServer() {
        try {
            System.out.println("My Server Start and My Ip: " + InetAddress.getLocalHost().getHostAddress() + ", Port: " + port);
            ServerSocket serverSocket = new ServerSocket(port);
            while (this.flag) {
                Socket socket = serverSocket.accept();
                new ServerThread(socket, this).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startServer();
    }

    public int getPort() {
        return port;
    }

    public Map<Integer, Integer> getContacts() {
        return contacts;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setContacts(Map<Integer, Integer> contacts) {
        this.contacts = contacts;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
