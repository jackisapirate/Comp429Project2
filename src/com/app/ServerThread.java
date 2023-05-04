package com.app;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kunlong Wang
 * @create 2023-03-02 1:44 PM
 */
public class ServerThread extends Thread {
    private Socket socket;
    private PrintWriter out;
    private Server server;

    private int port = 0;

    public ServerThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String msg;
            String remoteMsgArray[] = socket.getRemoteSocketAddress().toString().split(":");
            String remoteIp = remoteMsgArray[0].substring(1, remoteMsgArray[0].length());

            while (((msg = br.readLine()) != null) && this.server.topoNode.isTrigger()) {
                if (msg.startsWith("step")) {

                    String[] information = msg.substring(4).split("@");
                    int senderNode = Integer.parseInt(information[0]);
                    int receiverNode = this.server.topoNode.getWhoIam();
                    int costDirectly = 0;
                    this.server.topoNode.addPacketsCount();

//                    System.out.println("STEP SUCCESS");
                    System.out.println("RECEIVED A MESSAGE FROM SERVER: " + senderNode);

                    String[]  messages = information[1].split(" ");

                    Map<Integer, int[]> table = this.server.topoNode.getTable();
                    Map<Integer, Integer[]> newMessageTable = new HashMap<>();
                    for(String message:messages){
                        String[] data = message.split("_");
                        int key = Integer.parseInt(data[0]);
                        int skip = Integer.parseInt(data[1]);
                        int cost = Integer.parseInt(data[2]);
                        int isNeighbour = Integer.parseInt(data[3]);
                        int isRunning = Integer.parseInt((data[4]));

                        newMessageTable.put(key, new Integer[]{skip, cost, isNeighbour, isRunning});
                        if(key == receiverNode){
                            costDirectly = cost;
//                            if(!table.containsKey(senderNode)){
                                table.put(senderNode, new int[]{senderNode, cost, 1, 1});
//                            }
                        }
                    }
                    // algorithm start
                    for(Integer key : newMessageTable.keySet()){
                        int next = newMessageTable.get(key)[0];
                        int cost = newMessageTable.get(key)[1];
                        int isNeighbour = newMessageTable.get(key)[2];
                        int isRunning = newMessageTable.get(key)[3];

                        if(isRunning == 0){ // so this server has been closed
                            table.put(key, new int[]{0, Integer.MAX_VALUE, 0, 0});
                            continue;
                        }

                        if(key != receiverNode){
                            cost = (cost== Integer.MAX_VALUE || costDirectly == Integer.MAX_VALUE) ? Integer.MAX_VALUE : cost + costDirectly;
                            if(!table.containsKey(key)){
                                table.put(key, new int[]{senderNode, cost, 0, 1});
                            } else{
                                int oldNext = table.get(key)[0];
                                int oldCost = table.get(key)[1];
                                int oldIsNeighbour = table.get(key)[2];
                                int oldIsRunning = table.get(key)[3];
                                if(oldIsRunning == 0){
                                    continue;
                                }
                                if(oldNext == senderNode){
                                    table.get(key)[1] = cost;
                                } else if(cost<oldCost){
                                    table.put(key, new int[]{senderNode, cost, oldIsNeighbour, 1});
                                }
                            }
                        }

                    }
                } else if (msg.startsWith("update")) {
                    String[] information = msg.substring(6).split("@");
                    String[] updateInfo = information[0].split("_");
                    int senderNode = Integer.parseInt(updateInfo[0]);
                    int receiverNode = this.server.topoNode.getWhoIam();
                    int costDirectly = Integer.parseInt(updateInfo[2]);

                    this.server.topoNode.addPacketsCount();
                    System.out.println("RECEIVED A MESSAGE FROM SERVER: " + senderNode);

                    String[]  messages = information[1].split(" ");

                    Map<Integer, int[]> table = this.server.topoNode.getTable();
                    Map<Integer, Integer[]> newMessageTable = new HashMap<>();
                    for(String message:messages){
                        String[] data = message.split("_");
                        int key = Integer.parseInt(data[0]);
                        int skip = Integer.parseInt(data[1]);
                        int cost = Integer.parseInt(data[2]);
                        int isNeighbour = Integer.parseInt(data[3]);
                        int isRunning = Integer.parseInt((data[4]));
                        newMessageTable.put(key, new Integer[]{skip, cost, isNeighbour, isRunning});
                    }
                    table.put(senderNode, new int[]{senderNode, costDirectly, 1, 1});

                    // algorithm start
                    for(Integer key : newMessageTable.keySet()){
                        int next = newMessageTable.get(key)[0];
                        int cost = newMessageTable.get(key)[1];
                        int isNeighbour = newMessageTable.get(key)[2];
                        int isRunning = newMessageTable.get(key)[3];

                        if(isRunning == 0){ // so this server has been closed
                            table.put(key, new int[]{key, 0, Integer.MAX_VALUE, 0, 0});
                            continue;
                        }

                        if(key != receiverNode){
                            cost = (cost== Integer.MAX_VALUE || costDirectly == Integer.MAX_VALUE) ? Integer.MAX_VALUE : cost + costDirectly;
                            if(!table.containsKey(key)){
                                table.put(key, new int[]{senderNode, cost, 0, 1});
                            } else{
                                int oldNext = table.get(key)[0];
                                int oldCost = table.get(key)[1];
                                int oldIsNeighbour = newMessageTable.get(key)[2];
                                int oldIsRunning = newMessageTable.get(key)[3];
                                if(oldIsRunning == 0){
                                    continue;
                                }
                                if(oldNext == senderNode){
                                    table.get(key)[1] = cost;
                                } else if(cost<oldCost){
                                    table.put(key, new int[]{senderNode, cost, oldIsNeighbour, 1});
                                }
                            }
                        }

                    }
                } else if(msg.startsWith("disable")){
                    Map<Integer, int[]> table = this.server.topoNode.getTable();
                    int sender = Integer.parseInt(msg.split(" ")[1]);
                    table.get(sender)[0] = 0;
                    table.get(sender)[1] = Integer.MAX_VALUE;
                    table.get(sender)[2] = 0;
                    table.get(sender)[3] = 1;

                    for(int key:table.keySet()){
                        if(sender == table.get(key)[0]){
                            table.get(key)[0] = 0;
                            table.get(key)[1] = Integer.MAX_VALUE;
                        }
                    }

                    this.server.topoNode.addPacketsCount();
                    System.out.println("RECEIVED A MESSAGE FROM SERVER: " + sender);
                } else if(msg.startsWith("crash")){
                    Map<Integer, int[]> table = this.server.topoNode.getTable();
                    int sender = Integer.parseInt(msg.split(" ")[1]);
                    table.get(sender)[0] = 0;
                    table.get(sender)[1] = Integer.MAX_VALUE;
                    table.get(sender)[2] = 0;
                    table.get(sender)[3] = 0;

                    for(int key:table.keySet()){
                        if(sender == table.get(key)[0]){
                            table.get(key)[1] = Integer.MAX_VALUE;
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(socket.getRemoteSocketAddress() + " exit");
        }
    }

    public static String cutString(String str, String start, String end) {
        if ("".equals(str)) {
            return str;
        }
        int strStartIndex = str.indexOf(start);
        int strEndIndex = str.indexOf(end);
        String s = str.substring(strStartIndex, strEndIndex).substring(start.length());
        return s;
    }
}
