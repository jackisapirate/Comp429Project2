package com.app;

import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
/**
 * @author Kunlong Wang
 * @create 2023-03-02 1:44 PM
 */
public class Application {
    private TopoNode topoNode = new TopoNode();

    public void startChat() {
        System.out.println("Hello! Your terminal has launched!");
        Scanner scanner = new Scanner(System.in);
        String port = "0";
        String msg = scanner.nextLine();
        if (msg.startsWith("server")) {
//          server -t <topology-file-name> -i <routing-update-interval>

            String[] input = msg.split(" ");
            if (input.length != 5) {
                System.out.println("Please check your input!");
                System.out.println("server -t <topology-file-name> -i <routing-update-interval>");
            } else {
                String[] serverInfo;
                Map<Integer, int[]> table = new HashMap<>();
                int whoIam = 7999;

//                String root = Application.class.getResource("").toString() + input[2];
//
//                InputStream is = this.getClass().getClassLoader().getResourceAsStream(input[2]);
                int numServers = 0; // number of servers
                int numEdges = 0; // number of edges or neighbors

                try { // open input file
//                    Scanner inf = new Scanner(new File(root.substring(6)));
//                    FileInputStream inputStream = new FileInputStream(root.substring(6));
                    InputStream is = this.getClass().getResourceAsStream(input[2]);

                    InputStreamReader inputStream = new InputStreamReader(is, "utf-8");
                    BufferedReader bufferedReader = new BufferedReader((inputStream));


//                    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(input[2]);
//                    new InputStreamReader(in,"UTF-8")
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));

                    String str = null;
                    numServers = Integer.parseInt(bufferedReader.readLine());//read number of servers
                    numEdges = Integer.parseInt(bufferedReader.readLine());// read number of edges or neighbors
                    serverInfo = new String[numServers + 1];
                    String[] currentRead;
                    for (int i = 0; i < numServers; i++) {
                        currentRead = bufferedReader.readLine().split(" ");
                        String localIp = InetAddress.getLocalHost().getHostAddress();
//                                    localIp = currentRead[1];
                        serverInfo[Integer.parseInt(currentRead[0])] = localIp + " " + currentRead[2];
                    }
                    int interval = 100;

                    for (int i = 0; i < numEdges; i++) {
                        currentRead = bufferedReader.readLine().split(" ");
                        whoIam = Integer.parseInt(currentRead[0]);
                        int key = Integer.parseInt(currentRead[1]);
                        int cost = Integer.parseInt(currentRead[2]);
                        int next = key;
                        // Structure of table: key->{next, cost, isNeighbour, isRunning}

                        table.put((key), new int[]{next, cost, 1, 1});
                        interval = Integer.parseInt(input[4]);
                        port = serverInfo[whoIam].split(" ")[1];
                    }

                    for (int i = 1; i <= numServers; i++) {
                        if(i!=whoIam && !table.containsKey(i)){
                            table.put((i), new int[]{0, Integer.MAX_VALUE, 0, 1});
                        }
                    }
                    this.topoNode.setServerInfo(serverInfo);
                    this.topoNode.setWhoIam(whoIam);
                    this.topoNode.setTable(table);
                    this.topoNode.setInterval(interval);
                    // close input file
//                    inf.close();
                    inputStream.close();
                    bufferedReader.close();
                } catch (IOException e) {
//                    System.out.printf("\nI/O Error %s", e);
                }
            }
            // initial the countdown thread
            this.topoNode.start();
        }
        Server server = new Server(Integer.parseInt(port), topoNode);
        Client client = new Client(topoNode);
        new Thread(server).start();
        new Thread(client).start();
    }

    public static void main(String[] args) {
        Application chat = new Application();
        chat.startChat();
    }
}
