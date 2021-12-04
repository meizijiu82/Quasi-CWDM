package network;

import java.util.*;
import general.*;

public class Route extends Common{

    private  int type=Constant.Working;
    private ArrayList<Node> nodelist = new ArrayList<>();//node list
    private HashSet<Link> linklist =new HashSet<>(); //link list
    private ArrayList<Vlink> vlinklist =new ArrayList<>(); //virtual link list

    private int remCapacity =0;
    private int length =0;
    private int hops=0;         //跳数

    public Route(String name, int index, int type){
        super(name, index);
        this.type=type;


    }




/***************setters and getters******************/

    public int getRemCapacity() {
        return remCapacity;
    }

    public void setRemCapacity(int remCapacity) {
        this.remCapacity = remCapacity;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ArrayList<Vlink> getVlinklist() {
        return vlinklist;
    }

    public void setVlinklist(ArrayList<Vlink> vlinklist) {
        this.vlinklist = vlinklist;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<Node> getNodelist() {
        return nodelist;
    }

    public void setNodelist(ArrayList<Node> nodelist) {
        this.nodelist = nodelist;
    }

    public HashSet<Link> getLinklist() {
        return linklist;
    }

    public void setLinklist(HashSet<Link> linklist) {
        this.linklist = linklist;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }
}
