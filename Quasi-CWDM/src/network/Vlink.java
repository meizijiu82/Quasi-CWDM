package network;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Jq zhou
 * @Date: 2021/06/24/10:33
 * @Description:
 */
public class Vlink {

    private Node srcNode=null;
    private Node desNode=null;
    private String name;
    private int length=0;
    private int index=0;
    private int remCapacity=0;
    private HashSet<Lightpath>lightPathList=new HashSet<>();        //虚拟链路上的光路集合
    private int capacity=0;
    private int hop=20;              //虚拟链路根据光通道映射到光层上得到的最小跳数,初始时定为20
    private int hop_in_IP=0;        //虚拟链接在IP层上的跳数，不考虑光层，都设为1

    public Vlink(String name,Node srcNode, Node desNode) {
        this.name=name;
        this.srcNode = srcNode;
        this.desNode = desNode;
    }




    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public HashSet<Lightpath> getLightPathList() {
        return lightPathList;
    }

    public void setLightPathList(HashSet<Lightpath> lightPathList) {
        this.lightPathList = lightPathList;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public int getHop_in_IP() {
        return hop_in_IP;
    }

    public void setHop_in_IP(int hop_in_IP) {
        this.hop_in_IP = hop_in_IP;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getRemCapacity() {
        return remCapacity;
    }

    public void setRemCapacity(int remCapacity) {
        this.remCapacity = remCapacity;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHop() {
        return hop;
    }

    public void setHop(int hop) {
        this.hop = hop;
    }

}
