package network;

import java.util.HashMap;

public class ModuFormat {

    private int capacity=0;
    private String name=null;
    private double cost;
    private double p1;
    private double p2;
    private HashMap<Integer,Double>PS=new HashMap<>(); //距离，频谱效率

    public ModuFormat() {
    }

    public ModuFormat(String name, int capacity){
        this.name=name;
        this.capacity=capacity;
    }

    //时域混合调制格式，p1p2代表两种调制格式选择的概率
    public ModuFormat(int capacity, String name, double p1, double p2) {
        this.capacity = capacity;
        this.name = name;
        this.p1 = p1;
        this.p2 = p2;
    }


    //PS概率整形调制格式，不同距离对应不同的频谱效率


    public ModuFormat(HashMap<Integer, Double> PS) {
        this.PS = PS;
    }

    /***************************  getters and setters  *******************************/

    public String getName() {
        return name;
    }

    public HashMap<Integer, Double> getPS() {
        return PS;
    }

    public void setPS(HashMap<Integer, Double> PS) {
        this.PS = PS;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getP1() {
        return p1;
    }

    public void setP1(double p1) {
        this.p1 = p1;
    }

    public double getP2() {
        return p2;
    }

    public void setP2(double p2) {
        this.p2 = p2;
    }
}
