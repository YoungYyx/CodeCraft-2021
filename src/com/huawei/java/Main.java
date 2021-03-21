package com.huawei.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.*;

/**
 * @author yangyx
 * @create 2021-03-21 12:36
 */
class Server{
    String serverName;//服务器名称
    int cores;//服务器核心数
    int memory;//内存
    int fixedCost;//固定花销
    int dailyCost;//每日花销
    double coreCostPerformance;//核性价比
    double memCostPerformance;//内存性价比
    HashMap<Character,HashMap<Integer,VM>> vms;
    Server(String s){
        String[] ss = s.substring(1,s.length()-1).split(",");
        serverName = ss[0];
        cores = Integer.valueOf(ss[1].trim());
        memory = Integer.valueOf(ss[2].trim());
        fixedCost = Integer.valueOf(ss[3].trim());
        dailyCost = Integer.valueOf(ss[4].trim());
        coreCostPerformance = (fixedCost + 3*dailyCost)/cores;
        memCostPerformance = (fixedCost + 3*dailyCost)/memory;
    }
    @Override
    public String toString() {
        return "("+serverName+","+cores+","+memory+","+fixedCost+","+dailyCost+")";
    }
}
class VM{
    String vmName;
    int cores;
    int memory;
    int doubleNode;
    VM(String s){
        String[] ss = s.substring(1,s.length()-1).split(",");
        vmName = ss[0];
        cores = Integer.valueOf(ss[1].trim());
        memory = Integer.valueOf(ss[2].trim());
        doubleNode = Integer.valueOf(ss[3].trim());
    }
    @Override
    public String toString() {
        return "("+vmName+","+cores+","+memory+","+doubleNode+")";
    }
}
class BuyedServer{
    static int counter = 0;
    Integer serverId;
    String serverName;
    int leftAcore;
    int leftBcore;
    int leftAmem;
    int leftBmem;
    Map<Integer,BuyedVM> vmInA;
    Map<Integer,BuyedVM> vmInB;
    BuyedServer(String name,int lac,int lbc,int lam,int lbm){
        serverId = counter++;
        serverName = name;
        leftAcore = lac;
        leftBcore = lbc;
        leftAmem = lam;
        leftBmem = lbm;
        vmInA = new HashMap<Integer,BuyedVM>();
        vmInB = new HashMap<Integer,BuyedVM>();
    }
}
class BuyedVM{
    int vmId;
    String vmName;
    int serverId;
    int doubleNode;
    char node;
    BuyedVM(int id,String name,int sId,int dn){
        vmId = id;
        vmName = name;
        serverId = sId;
        doubleNode = dn;
        node = 'N';
    }
}
class Order{
    int operation; // 0 for del and 1 for add;
    String vmName;
    int vmId;
    Order(String s){
        String[] ss = s.substring(1,s.length()-1).split(",");
        if(ss.length==3){
            operation = 1;
            vmName = ss[1];
            vmId = Integer.valueOf(ss[2].trim());
        }else{
            operation = 0;
            vmName = "";
            vmId = Integer.valueOf(ss[2].trim());
        }
    }
}
public class Main {
    public static LinkedList<Server> serverListOrderByCore;//核性价比排序的列表
    public static LinkedList<Server> serverListOrderByMem;//内存性价比排序的列表
    public static Map<String,VM> vmMap;//虚拟机类型列表
    public static Map<Integer,BuyedServer> ownedServer;//拥有的server
    public static Map<Integer,BuyedVM> ownedVm;//拥有的虚拟机

    public static boolean canFindServer(Order order){
        switch (order.operation){
            case 0:{//删除操作
                BuyedVM vm = ownedVm.get(order.vmId);
                ownedVm.remove(vm.vmId);
                BuyedServer server = ownedServer.get(vm.serverId);
                if(vm.doubleNode==1) {//双节点部署
                    server.vmInA.remove(vm.vmId);
                    server.vmInB.remove(vm.vmId);
                    server.leftAcore += vmMap.get(vm.vmName).cores/2;
                    server.leftBcore += vmMap.get(vm.vmName).cores/2;
                    server.leftAmem += vmMap.get(vm.vmName).memory/2;
                    server.leftBmem += vmMap.get(vm.vmName).memory/2;
                }else{
                    char node = vm.node;
                    switch (node){
                        case 'A':{
                            server.vmInA.remove(vm.vmId);
                            server.leftAcore += vmMap.get(vm.vmName).cores;
                            server.leftAmem += vmMap.get(vm.vmName).memory;
                            break;
                        }
                        case 'B':{
                            server.vmInB.remove(vm.vmId);
                            server.leftBcore += vmMap.get(vm.vmName).cores;
                            server.leftBmem += vmMap.get(vm.vmName).memory;
                            break;
                        }
                    }
                }
                return true;
            }
            case 1:{//添加操作
                VM vm = vmMap.get(order.vmName);
                switch (vm.doubleNode){
                    case 0:{//单节点部署
                        for (BuyedServer server : ownedServer.values()) {
                            if(vm.cores<=server.leftAcore && vm.memory<=server.leftAmem){//可以部署到A节点
                                BuyedVM bvm = new BuyedVM(order.vmId,vm.vmName,server.serverId,0);
                                bvm.node = 'A';
                                server.leftAcore -= vm.cores;
                                server.leftAmem -= vm.memory;
                                server.vmInA.put(bvm.vmId,bvm);
                                return true;
                            }else if(vm.cores<=server.leftBcore && vm.memory<=server.leftBmem){//可以部署到B节点
                                BuyedVM bvm = new BuyedVM(order.vmId,vm.vmName,server.serverId,0);
                                bvm.node = 'B';
                                server.leftBcore -= vm.cores;
                                server.leftBmem -= vm.memory;
                                server.vmInB.put(bvm.vmId,bvm);
                                return true;
                            }
                        }
                        return false;
                    }
                    case 1:{
                        int c = vm.cores/2;
                        int m = vm.memory/2;
                        for (BuyedServer server : ownedServer.values()) {
                            if((c<=server.leftAcore)&&(c<=server.leftBcore)&&(m<=server.leftAmem)&&(m<=server.leftBmem)){//可以部署到双节点
                                BuyedVM bvm = new BuyedVM(order.vmId,vm.vmName,server.serverId,1);
                                server.leftAcore -= c;
                                server.leftAmem -= m;
                                server.vmInA.put(bvm.vmId,bvm);
                                server.leftBcore -= c;
                                server.leftBmem -= m;
                                server.vmInB.put(bvm.vmId,bvm);
                                return true;
                            }
                        }
                        return false;
                    }
                }

            }
        }
        return false;
    }

    public static void main(String[] args) {
        serverListOrderByCore = new LinkedList<Server>();
        serverListOrderByMem = new LinkedList<Server>();
        ownedServer = new HashMap<Integer, BuyedServer>();
        ownedVm = new HashMap<Integer, BuyedVM>();

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String s;//每次读一行文本

        try {
            int numberOfServer = 0,readLines = 0;
            numberOfServer = Integer.valueOf(stdin.readLine());//服务器种类
            while (readLines<numberOfServer){//读取服务器类型
                s = stdin.readLine();
                Server server = new Server(s);
                serverListOrderByCore.add(server);
                serverListOrderByMem.add(server);
                readLines++;
            }
            Collections.sort(serverListOrderByCore, new Comparator<Server>() {
                @Override
                public int compare(Server o1, Server o2) {
                    if(o1.coreCostPerformance>o2.coreCostPerformance){
                        return 1;
                    }else if(o1.coreCostPerformance==o2.coreCostPerformance){
                        return 0;
                    }else{
                        return -1;
                    }
                }
            });
            Collections.sort(serverListOrderByMem, new Comparator<Server>() {
                @Override
                public int compare(Server o1, Server o2) {
                    if(o1.memCostPerformance>o2.memCostPerformance){
                        return 1;
                    }else if(o1.memCostPerformance==o2.memCostPerformance){
                        return 0;
                    }else{
                        return -1;
                    }
                }
            });
            int numberOfVM = Integer.valueOf(stdin.readLine());
            readLines = 0;
            while (readLines<numberOfVM){//读取虚拟机类型
                s = stdin.readLine();
                VM vm = new VM(s);
                vmMap.put(vm.vmName,vm);
                readLines++;
            }
            int days =  Integer.valueOf(stdin.readLine());//请求天数
            for(int i=0;i<days;i++){//处理每天的请求
                int numberOfRequest = Integer.valueOf(stdin.readLine());
                readLines = 0;
                LinkedList<String> list = new LinkedList<String>();
                while(readLines<numberOfRequest){
                    s = stdin.readLine();
                    Order order = new Order(s);
                    if(!canFindServer(order)){//当前服务器不满足

                    }
                }
            }


        } catch (IOException e) {
            System.err.println("I/O Error");
        }

    }
}
