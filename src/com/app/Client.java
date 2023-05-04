package com.app;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Kunlong Wang
 * @create 2023-03-02 1:44 PM
 */
public class Client implements Runnable {
    TopoNode topoNode;

    public Client(TopoNode topoNode) {
        this.topoNode = topoNode;
    }

    public void sendMsg() {
        System.out.println("---------------Server in running SUCCESSFULLY---------------------");
        try {
            Scanner scanner = new Scanner(System.in);

            while (this.topoNode.isTrigger()) {
                String msg = scanner.nextLine();
                if (!msg.isEmpty()) {
                    if (msg.startsWith("update")){
                        // update <server-ID1> <server-ID2> <Link Cost>
                        String[] input = msg.split(" ");
                        if(input.length != 4){
                            System.out.println("Please check your input!");
                        } else{
                            // update 1 2 inf
                            // update 1 2 8
                            int sender = Integer.parseInt(input[1]);
                            int receiver = Integer.parseInt(input[2]);
                            // Structure of table: key->{next, cost, isNeighbour, isRunning}
                            Map<Integer, int[]> table = this.topoNode.getTable();
                            if(sender != topoNode.getWhoIam() || !table.containsKey(receiver) || table.get(receiver)[2] != 1){
                                if(table.get(receiver)[2] != 1){
                                    System.out.println("Previously, the connection between two nodes had been manually disconnected.");
                                }
                                System.out.println("Please check your input! First variable is the number of server itself. Second variable is its neighbour. Third variable is new link cost or 'inf'");
                                continue; // stop this operation
                            }

                            int newCost;
                            if("inf".equals(input[3])){
                                newCost = Integer.MAX_VALUE;
                            } else {
                                newCost = Integer.parseInt(input[3]);
                            }

                            // 1.update my own table
                            // Structure of table: key->{next, cost, isNeighbour, isRunning}

                            if(newCost!=Integer.MAX_VALUE){
                                int difference = newCost - table.get(receiver)[1];
                                for(int key:table.keySet()){
                                    if(table.get(key)[0] == receiver){
                                        if(table.get(key)[1] == Integer.MAX_VALUE && key != receiver){
//                                            table.get(key)[0] = 0;
                                        } else {
                                            table.get(key)[1] = table.get(key)[1] + difference;
                                        }
                                    }
                                }
                            } else {
                                for(int key:table.keySet()){
                                    if(table.get(key)[0] == receiver){
                                        table.get(key)[1] = Integer.MAX_VALUE;
                                    }
                                }

                            }

                            table.get(receiver)[0] = receiver;
                            table.get(receiver)[1] = newCost;

                            // 2.send neighbors
                            // send
                            // Dest ip and port
                            String ip = topoNode.getServerInfo()[receiver].split(" ")[0];
                            String port = topoNode.getServerInfo()[receiver].split(" ")[1];

                            StringBuilder sb = new StringBuilder();
                            sb.append("update" + topoNode.getWhoIam() + "_" + receiver + "_" + newCost + "@");
                            for (Integer key : table.keySet()) {
                                int[] cur = table.get(key);

                                sb.append(key + "_" + cur[0] + "_" + cur[1] + "_" + cur[2] + "_" + cur[3] + " ");
                            }
                            String message = sb.toString();
                            if(" ".equals(sb.substring(sb.length()))){
                                message = sb.substring(0, sb.length() - 1);
                            }

                            Tool.transmit(ip, port, message);
                            System.out.println("UPDATE SUCCESS");

                        }
                    } else if (msg.startsWith("step")){
                        Map<Integer, int[]> table = this.topoNode.getTable();
                        // Structure of table: key->{next, cost, isNeighbour, isRunning}
                        Tool.stepPrepare(this.topoNode.getWhoIam(), table, this.topoNode.getServerInfo());
                        System.out.println("STEP SUCCESS");

                    } else if (msg.startsWith("packets")){
                        // Show the number of packets and reset the number
                        System.out.println("PACKETS SUCCESS");
                        System.out.println("The number of received packages: " + this.topoNode.getPacketsCount());
                        this.topoNode.resetPacketsCount();

                    } else if (msg.startsWith("display")){
                        // print table
                        System.out.println("ID | Next Hop | Cost");
                        // Structure of table: key->{next, cost, isNeighbour, isRunning}
                        Map<Integer, int[]> table = this.topoNode.getTable();
                        System.out.println(this.topoNode.getWhoIam() + "\t" + this.topoNode.getWhoIam() + "\t" + 0);
                        for(Integer key:table.keySet()){
                            String val = "";
                            if(table.get(key)[1] == Integer.MAX_VALUE){
                                val = "INF";
                            } else {
                                val = table.get(key)[1] + "";
                            }
                            String nextHop = "0";
                            if(table.get(key)[0] == 0){
                                nextHop = "-";
                            } else {
                                nextHop = table.get(key)[0] + "";
                            }
                            if(table.get(key)[3] == 1){
                                System.out.println(key + "\t" + nextHop + "\t" + val);
                            }
                        }

                    } else if (msg.startsWith("disable")){
                        // disable: disconnect the two nodes.
                        Map<Integer, int[]> table = this.topoNode.getTable();

                        int sender = this.topoNode.getWhoIam();
                        if(msg.split(" ").length != 2){
                            System.out.println("DISABLE ERROR: The format you entered is incorrect!");
                            System.out.println("disable <server-ID>");
                        }
                        int receiver = Integer.parseInt(msg.split(" ")[1]);
                        if(!table.containsKey(receiver)){
                            System.out.println("The given server is not its neighbor!");
                            continue;
                        }
                        String ip = this.topoNode.getServerInfo()[receiver].split(" ")[0];
                        String port = this.topoNode.getServerInfo()[receiver].split(" ")[1];
                        String message = "disable " + sender;
                        Tool.transmit(ip, port, message);
                        table.get(receiver)[0] = 0;
                        table.get(receiver)[1] = Integer.MAX_VALUE;
                        table.get(receiver)[2] = 0;
                        table.get(receiver)[3] = 1;

                        System.out.println("DISABLE SUCCESS");
                    } else if (msg.startsWith("crash")){
                        // power off
                        Map<Integer, int[]> table = this.topoNode.getTable();
                        int sender = this.topoNode.getWhoIam();
                        String message = "crash " + sender;

                        for(Integer key:table.keySet()){
                            if(table.get(key)[2] == 1 && table.get(key)[3] == 1){
                                String ip = topoNode.getServerInfo()[key].split(" ")[0];
                                String port = topoNode.getServerInfo()[key].split(" ")[1];
                                Tool.transmit(ip, port, message);
                            }
                        }
                        System.out.println("BYE, GUYS! <This is to simulate server crashes>");
                        topoNode.setTrigger(false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        sendMsg();
    }
}
