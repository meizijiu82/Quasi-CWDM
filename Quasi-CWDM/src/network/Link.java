package network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import general.*;


public class Link extends Common {

    private Node srcNode=null;
    private Node desNode=null;
    String name;
    int index;
    private int length=0;
    private int status=Constant.unvisited;


    private HashMap<Integer,Integer> avaWavelist=new HashMap<>();


    private LinkedList<Integer>wavelengthList=new LinkedList<>();
    private int remCapacity=0;
    private int Capacity=0;
    private HashSet<Lightpath> lightpathList=new HashSet<>();      //光路链接集合
    private int channels=0;
    private int maxUsedWaveIndex=0; //链路上最大使用波长的index
    private double cost=0.001;     //cost值为链路上占用波长数
    private float spectrumEfficiency=0;

    public Link(String name,int index,Node srcNode,Node desNode,int length,double cost){
        super(name,index);
        this.name=name;
        this.index=index;
        this.srcNode=srcNode;
        this.desNode=desNode;
        this.length=length;
        this.cost=cost;
    }





/***************setters and getters******************/

    public int getMaxUsedWaveIndex(Link link) {

        for (Integer w:link.getWavelengthList()) {
            this.maxUsedWaveIndex=Math.max(this.maxUsedWaveIndex,w);
        }
        return this.maxUsedWaveIndex+1;
    }

    public void setMaxUsedWaveIndex(int maxUsedWaveIndex) {
        this.maxUsedWaveIndex = maxUsedWaveIndex;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public HashMap<Integer, Integer> getAvaWavelist() {
        return avaWavelist;
    }

    public void setAvaWavelist(HashMap<Integer, Integer> avaWavelist) {
        this.avaWavelist = avaWavelist;
    }

    public LinkedList<Integer> getWavelengthList() {
        return wavelengthList;
    }

    public void setWavelengthList(LinkedList<Integer> wavelengthList) {
        this.wavelengthList = wavelengthList;
    }

    public float getSpectrumEfficiency() {
        return spectrumEfficiency;
    }

    public void setSpectrumEfficiency(float spectrumEfficiency) {
        this.spectrumEfficiency = spectrumEfficiency;
    }

    public int getRemCapacity() {

        return remCapacity;
    }

    public void setRemCapacity(int remCapacity) {
        this.remCapacity = remCapacity;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public HashSet<Lightpath> getLightpathList() {
        return lightpathList;
    }

    public void setLightpathList(HashSet<Lightpath> lightpathList) {
        this.lightpathList = lightpathList;
    }
}
