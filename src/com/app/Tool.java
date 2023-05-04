package com.app;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author Kunlong Wang
 * @create 2023-03-02 1:44 PM
 */
public class Tool {
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
//            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * Check whether a certain port of the machine is occupied
     * @param port
     */
    public static boolean isLoclePortUsing(int port){
        boolean flag = true;
        try{
            flag = isPortUsing("127.0.0.1", port);
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    public static boolean isPortUsing(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        try{
            Socket socket = new Socket(theAddress, port);
            flag = true;
        } catch (IOException e) {

        }
        return flag;
    }

    public static void launch(String ip, int port, String message){
        Socket socket = null;
        OutputStream os = null;
        PrintStream ps = null;
        try {
            socket = new Socket(ip, port);
            os = socket.getOutputStream();
            ps = new PrintStream(os);
            System.out.println("The message has been launched!");
            ps.println(message);
            ps.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void transmit(String ip, String port, String message){
        Socket socket = null;
        OutputStream os = null;
        PrintStream ps = null;
        try {
            socket = new Socket(ip, Integer.parseInt(port));
            os = socket.getOutputStream();
            ps = new PrintStream(os);
            ps.println(message);
            ps.flush();
        } catch (IOException e) {
//            e.printStackTrace();
        } finally {
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void stepPrepare(int whoIam, Map<Integer, int[]> table, String[] serverInfo){
        // transmit to neighbour
        // Structure of table: key->{next, cost, isNeighbour, isRunning}
        StringBuilder sb = new StringBuilder();
        sb.append("step" + whoIam + "@");
        for (Integer key : table.keySet()) {
            int[] cur = table.get(key);

            sb.append(key + "_" + cur[0] + "_" + cur[1] + "_" + cur[2] + "_" + cur[3] + " ");
        }
        String message = sb.toString();
        if(" ".equals(sb.substring(sb.length()-1))){
            message = sb.substring(0, sb.length() - 1);
        }
        for (Integer key : table.keySet()) {
            // Just update the neighbour servers
            if(table.get(key)[2] == 0 || table.get(key)[3] == 0){
                // If the target server is not my neighbour or has been closed, we just skip this one.
                continue;
            }
            String ip = serverInfo[key].split(" ")[0];
            String port = serverInfo[key].split((" "))[1];
            Tool.transmit(ip, port, message);
        }
    }
}
