package com.app;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kunlong Wang
 * @create 2023-05-01 10:45 PM
 */
public class TopoNode extends Thread  {

    // this structure is working for project2
    private String[] serverInfo;
    // Structure of table: key->{next, cost, isNeighbour, isRunning}
    private Map<Integer, int[]> table = new HashMap<>();
    private int whoIam = 0;

    private int interval;
    private boolean trigger =  true;
    private int packetsCount = 0;

    public TopoNode(String[] serverInfo, Map<Integer, int[]> table, int whoIam, int interval) {
        this.serverInfo = serverInfo;
        this.table = table;
        this.whoIam = whoIam;
        this.interval = interval;
    }

    public TopoNode() {
        this.serverInfo = null;
        this.table = null;
        this.whoIam = 0;
        this.interval = 0;
    }

    @Override
    public void run() {
        while(this.trigger){
            try{
                Thread.sleep(1000 * interval);
            }catch(Exception ex){
                ex.printStackTrace();
            }

            // transmit to neighbour
            Tool.stepPrepare(whoIam, table, serverInfo);
            System.out.println("AUTO: routing is updating periodically...");
        }
    }


    public String[] getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(String[] serverInfo) {
        this.serverInfo = serverInfo;
    }

    public Map<Integer, int[]> getTable() {
        return table;
    }

    public void setTable(Map<Integer, int[]> table) {
        this.table = table;
    }

    public int getWhoIam() {
        return whoIam;
    }

    public void setWhoIam(int whoIam) {
        this.whoIam = whoIam;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isTrigger() {
        return trigger;
    }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }

    public int getPacketsCount() {
        return packetsCount;
    }

    public void resetPacketsCount() {
        this.packetsCount = 0;
    }
    public void addPacketsCount() {
        this.packetsCount++;
    }
}
