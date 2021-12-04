package network;

import general.Constant;
import routeSearch.Dijkstra;

import java.util.*;

public class Gv {
    private int remCapacity;
    private int Capacity=0;
    private LinkedList<Lightpath>lpLinklist=new LinkedList<>();
    private HashMap<String,Link>linklist=null;
    //无序，不可重复
    private HashMap<String,Vlink> vlinks=new HashMap<>();      //每个节点对之间有且仅有一条虚拟链路。每条虚拟链路上可以存在多条光通道
    private int blockServiceSum=0;
    private double Total_Cost=0;
    private double Nip=0;
    private double Nregen=0;
    private int NipB=0;
    private int NipQ=0;
    private int NipM=0;
    private int NregenB=0;
    private int NregenQ=0;
    private int NregenM=0;


    public Gv() {

    }


    @Override
    public boolean equals(Object obj) {
        Gv gv=(Gv) obj;
        return this.getTotal_Cost()==gv.getTotal_Cost();
    }

    //
    public void addVlink(Vlink vlink){
//        this.vlinks.add(vlink);
        this.vlinks.put(vlink.getName(),vlink);
    }


    //根据网络中的节点对创建虚拟链路集合
    public void generateVlinkList(Layer layer){
        LinkedHashMap<String ,Nodepair>nodepairlist= layer.getNodepairlist();   //得到节点对列表
        for (Nodepair nodepair:nodepairlist.values()) {
            String name=nodepair.getSrcNode().getName()+"-"+nodepair.getDesNode().getName();
            Vlink vlink=new Vlink(name,nodepair.getSrcNode(),nodepair.getDesNode());
            Dijkstra dijkstra=new Dijkstra();
            vlink.setIndex(this.vlinks.size());
            vlink.setLength(dijkstra.shortestLength(vlink.getSrcNode(), vlink.getDesNode(), layer));
            this.addVlink(vlink);
        }
    }

    //find a virtual link based on two nodes
    public Vlink findVlink(Node NodeA,Node NodeB){
        //根据已有的虚拟链路集合返回该虚拟链路
        String name;
        if (NodeA.getIndex()<NodeB.getIndex()){
            name=NodeA.getName()+"-"+NodeB.getName();
        }else name=NodeB.getName()+"-"+NodeA.getName();

        return this.getVlinks().get(name);

//        for (Vlink v:this.getVlinks().values()) {
//            if (v.getName().equals(name)){
//                return v;
//            }
//        }
//        return null;
    }


/********************  getters and setters  *******************************/
    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public HashMap<String, Link> getLinklist() {
        return linklist;
    }

    public void setLinklist(HashMap<String, Link> linklist) {
        this.linklist = linklist;
    }

    public int getBlockServiceSum() {
        return blockServiceSum;
    }

    public void setBlockServiceSum(int blockServiceSum) {
        this.blockServiceSum = blockServiceSum;
    }

    public LinkedList<Lightpath> getLpLinklist() {
        return lpLinklist;
    }

    public void setLpLinklist(LinkedList<Lightpath> lpLinklist) {
        this.lpLinklist = lpLinklist;
    }

    public double getNip() {
        return Nip;
    }

    public void setNip(double nip) {
        Nip = nip;
    }

    public double getNregen() {
        return Nregen;
    }

    public void setNregen(double nregen) {
        Nregen = nregen;
    }

    public int getRemCapacity() {
        return remCapacity;
    }

    public void setRemCapacity(int remCapacity) {
        this.remCapacity = remCapacity;
    }

    public double getTotal_Cost() {
        return Total_Cost;
    }

    public void setTotal_Cost(double total_Cost) {
        Total_Cost = total_Cost;
    }

    public int getNipB() {
        return NipB;
    }

    public void setNipB(int nipB) {
        NipB = nipB;
    }

    public int getNipQ() {
        return NipQ;
    }

    public void setNipQ(int nipQ) {
        NipQ = nipQ;
    }

    public int getNipM() {
        return NipM;
    }

    public void setNipM(int nipM) {
        NipM = nipM;
    }

    public int getNregenB() {
        return NregenB;
    }

    public void setNregenB(int nregenB) {
        NregenB = nregenB;
    }

    public int getNregenQ() {
        return NregenQ;
    }

    public void setNregenQ(int nregenQ) {
        NregenQ = nregenQ;
    }

    public int getNregenM() {
        return NregenM;
    }

    public void setNregenM(int nregenM) {
        NregenM = nregenM;
    }

    public HashMap<String, Vlink> getVlinks() {
        return vlinks;
    }

    public void setVlinks(HashMap<String, Vlink> vlinks) {
        this.vlinks = vlinks;
    }

    //    public LinkedList<Vlink> getVlinks() {
//        return vlinks;
//    }
//
//    public void setVlinks(LinkedList<Vlink> vlinks) {
//        this.vlinks = vlinks;
//    }
}
