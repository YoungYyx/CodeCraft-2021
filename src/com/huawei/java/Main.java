package com.huawei.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @author yangyx
 * @create 2021-03-21 12:36
 */
class Server implements Comparable<Server>{
    private String serverId;
    private int cores;
    private int memory;
    private int fixedCost;
    private int dailyCost;
    Server(String s){
        String[] ss = s.substring(1,s.length()-1).split(",");
        serverId = ss[0];
        cores = Integer.valueOf(ss[1].trim());
        memory = Integer.valueOf(ss[2].trim());
        fixedCost = Integer.valueOf(ss[3].trim());
        dailyCost = Integer.valueOf(ss[4].trim());
    }

    @Override
    public int compareTo(Server o) {
        if(this.fixedCost>o.fixedCost)
            return 1;
        else if(this.fixedCost==o.fixedCost)
            return 0;
        else
            return -1;
    }

    @Override
    public String toString() {
        return "("+serverId+","+cores+","+memory+","+fixedCost+","+dailyCost+")";
    }
}
class VM{
    String vmId;
    int cores;
    int memory;
    int doubleNode;
    VM(String s){
        String[] ss = s.substring(1,s.length()-1).split(",");
        vmId = ss[0];
        cores = Integer.valueOf(ss[1].trim());
        memory = Integer.valueOf(ss[2].trim());
        doubleNode = Integer.valueOf(ss[3].trim());

    }
    @Override
    public String toString() {
        return "("+vmId+","+cores+","+memory+","+doubleNode+")";
    }
}
public class Main {
    public static void main(String[] args) {

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String s;//每次读一行文本
        PriorityQueue<Server> servers = new PriorityQueue<Server>();
        Map<String,VM> map = new HashMap<String,VM>();
        try {
            int numberOfServer = 0,readLines = 0;
            numberOfServer = Integer.valueOf(stdin.readLine());//服务器种类
            while (readLines<numberOfServer){//读取服务器类型
                s = stdin.readLine();
                servers.add(new Server(s));
                readLines++;
            }
            int numberOfVM = Integer.valueOf(stdin.readLine());
            readLines = 0;
            while (readLines<numberOfVM){//读取虚拟机类型
                s = stdin.readLine();
                VM vm = new VM(s);
                map.put(vm.vmId,vm);
                readLines++;
            }
            int days =  Integer.valueOf(stdin.readLine());//请求天数
            for(int i=0;i<days;i++){//处理每天的请求
                int numberOfRequest = Integer.valueOf(stdin.readLine());
                readLines = 0;
                while(readLines<numberOfRequest){

                }
            }


        } catch (IOException e) {
            System.err.println("I/O Error");
        }

    }
}
