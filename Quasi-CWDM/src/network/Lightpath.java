package network;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class Lightpath {

    private LinkedList<Integer> avaWavelist=new LinkedList<>();        //光路可用波长集合
    private LinkedList<Integer>wavelengthList=new LinkedList<>();     //光路占用波长集合
    private String name;
    private HashSet<Link> Lplink=new HashSet<>();      //光路链接集合
    private ModuFormat ModuFormat=null;             //调制格式
    private int channels=0;         //光路上光通道数
    private int Demand=0;
    private int status=0;       //判断是否在原先的拓扑上启用该光路
    private int grooming=0;
    private int hops=0;
    private int remCapacity=0;      //光路剩余容量
    private int capacity=0;         //光路容量
    private int distance=0;
    private Node srcNode=null;
    private Node desNode=null;
    private int NipB=0;
    private int NipQ=0;
    private int NipM=0;
    private int NregenB=0;
    private int NregenQ=0;
    private int NregenM=0;
    private String viaLink="";
    private int occupyWavelengthIndex;



    public Lightpath(String name){
        this.name=name;
    }



    /********************  getters and setters  *******************************/

    public void setOccupyWavelengthIndex(int occupyWavelengthIndex) {
        this.occupyWavelengthIndex = occupyWavelengthIndex;
    }

    public int getOccupyWavelengthIndex() {
        return occupyWavelengthIndex;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getViaLink() {
        return viaLink;
    }

    public void setViaLink(String viaLink) {
        this.viaLink = viaLink;
    }

    public int getGrooming() {
        return grooming;
    }

    public void setGrooming(int grooming) {
        this.grooming = grooming;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<Integer> getAvaWavelist() {
        return avaWavelist;
    }

    public void setAvaWavelist(LinkedList<Integer> avaWavelist) {
        this.avaWavelist = avaWavelist;
    }

    public LinkedList<Integer> getWavelengthList() {
        return wavelengthList;
    }

    public void setWavelengthList(LinkedList<Integer> wavelengthList) {
        this.wavelengthList = wavelengthList;
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

    public int getDemand() {
        return Demand;
    }

    public void setDemand(int Demand) {
        this.Demand = Demand;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ModuFormat getModuFormat() {
        return ModuFormat;
    }

    public void setModuFormat(ModuFormat moduFormat) {
        ModuFormat = moduFormat;
    }

    public HashSet<Link> getLplink() {
        return Lplink;
    }

    public void setLplink(HashSet<Link> lplink) {
        Lplink = lplink;
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

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public int getRemCapacity() {
        return remCapacity;
    }

    public void setRemCapacity(int remCapacity) {
        this.remCapacity = remCapacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}