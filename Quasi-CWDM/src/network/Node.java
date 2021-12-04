package network;

import java.util.ArrayList;
import java.util.HashSet;

import general.*;


public class Node extends Common {
    String name;
    int index;
    private int NipB=0;
    private int NipQ=0;
    private int NipM=0;
    private int NregenB=0;
    private int NregenQ=0;
    private int NregenM=0;
    private ArrayList<Node>neinodelist=null;                    //相邻节点列表集合
    private HashSet<Node> IPneinodelist=new HashSet<>();     //相邻IP节点列表集合
    private Node parentNode=null;
    private int Length_from_src =Integer.MAX_VALUE;
    private int shortestPath=Integer.MAX_VALUE;
    private int status=Constant.unvisited;
    private int used=0;     //判断该节点是否已经被使用
    private double cost_from_src =0.000001;   //以波长为cost，cost值为节点到源点之间的链路上波长数总和

    public Node(String name,int index){
        super(name,index);
        this.name=name;
        this.index=index;
        this.neinodelist=new ArrayList<>();
    }

    public void addNeinode(Node node){
        this.neinodelist.add(node);
    }

    public void addIPNeinode(Node node){
        this.IPneinodelist.add(node);
    }




/********************setters and getters******************/

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public HashSet<Node> getIPneinodelist() {
        return IPneinodelist;
    }

    public void setIPneinodelist(HashSet<Node> IPneinodelist) {
        this.IPneinodelist = IPneinodelist;
    }

    public double getCost_from_src() {
        return cost_from_src;
    }

    public void setCost_from_src(double cost_from_src) {
        this.cost_from_src = cost_from_src;
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

    public void setIndex(int index) {
        this.index = index;
    }

    public ArrayList<Node> getNeinodelist() {
        return neinodelist;
    }

    public void setNeinodelist(ArrayList<Node> neinodelist) {
        this.neinodelist = neinodelist;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public int getLength_from_src() {
        return Length_from_src;
    }

    public void setLength_from_src(int length_from_src) {
        Length_from_src = length_from_src;
    }

    public int getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(int shortestPath) {
        this.shortestPath = shortestPath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
}
