package network;


//import Quasi.Quasi_CWDM;

import java.util.ArrayList;
import java.util.HashMap;

public class Nodepair {
    String name;
    int index;
    private Node srcNode;
    private Node desNode;
    private int demand;
    private int blockService=0;
    private HashMap<Integer, Integer> serviceList =new HashMap<>();    //节点对之间的业务集合：key编号，value需求
//    private HashMap<String, Route> routeList = new HashMap<>();
    private ArrayList<Route> routeList = new ArrayList<>();



    public Nodepair(String name,int index,Node srcNode,Node desNode){
        this.name=name;
        this.index=index;
        this.desNode=desNode;
        this.srcNode=srcNode;
    }



    /***************setters and getters******************/
    public int getBlockService() {
        return blockService;
    }

    public void setBlockService(int blockService) {
        this.blockService = blockService;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public Node getSrcNode() {
        return srcNode;
    }

    public void setSrcNode(Node srcNode) {
        this.srcNode = srcNode;
    }

    public Node getDesNode() {
        return desNode;
    }

    public void setDesNode(Node desNode) {
        this.desNode = desNode;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public HashMap<Integer, Integer> getServiceList() {
        return serviceList;
    }

    public void setServiceList(HashMap<Integer, Integer> serviceList) {
        this.serviceList = serviceList;
    }

    public ArrayList<Route> getRouteList() {
        return routeList;
    }

    public void setRouteList(ArrayList<Route> routeList) {
        this.routeList = routeList;
    }

//    public HashMap<String, Route> getRouteList() {
//        return routeList;
//    }
//
//    public void setRouteList(HashMap<String, Route> routeList) {
//        this.routeList = routeList;
//    }
}
