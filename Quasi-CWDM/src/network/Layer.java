package network;

import java.io.*;
import java.util.*;

//import Quasi.Quasi_CWDM;
import general.*;

public class Layer extends Common{

    private Layer serverlayer = null; //the server layer of the current layer
    private Layer clientlayer = null; //the client layer of the current layer

    private HashMap<String,Node>nodelist=null;
    private HashMap<String,Link>linklist=null;
    private LinkedHashMap<String,Nodepair>nodepairlist=new LinkedHashMap<>();
    private LinkedHashMap<String,IPnodepair>IPnodepairlist=new LinkedHashMap<>();
    private LinkedHashMap<String,Nodepair>invnodepairlist=new LinkedHashMap<>();
    private int demand;

    public Layer(String name, int index){
        super(name, index);
        this.nodelist=new HashMap<String,Node>();
        this.linklist=new HashMap<String,Link>();
        this.nodepairlist=new LinkedHashMap<String,Nodepair>();
    }

    public void addNode(Node node){
        this.nodelist.put(node.getName(),node); //添加以节点名字为（key）键，节点值为（value）值的新元素
    }
    /**
     * remove node from the layer
     * when remove the node, we need to remove the links that source from it as well
     */

    public void addLink(Link link){
        this.linklist.put(link.getName(),link);
    }

    public void addNodepair(Nodepair nodepair){
        this.nodepairlist.put(nodepair.getName(),nodepair);
    }

    public void addIPNodepair(IPnodepair IPNodepair){
        this.IPnodepairlist.put(IPNodepair.getName(),IPNodepair);
    }


    public void readTopology(String filename){
        String[] data =new String[10];
        File file=new File(filename);
        BufferedReader buffer=null;
        try {
            buffer=new BufferedReader(new FileReader(file));
        }catch (FileNotFoundException e){   //要使上条语句中的FileReader能够使用，必须加上这条语句，用来处理fileNotFoundException异常
            e.printStackTrace();
        }
        String line=null;
        int col=0;
        //read each line of .csv file
        try {
            boolean link=false;
            while ((line=buffer.readLine())!=null){ //line不为空时,读取这行文本
                StringTokenizer st=new StringTokenizer(line,",");   //分隔语句，定义的分隔标志是空格或逗号
                //StrintTokenizer(String s,String delim) ,delim就是自定义的分隔标记
                while (st.hasMoreTokens()){     //当对象还有值的时候
                    data[col]=st.nextToken();     //将该值赋予对应列，之后在对象中删除该值
                    col++;
                }
                col=0;
                String name=data[0];
                if (name.equals("Link")){
                    link=true;
                }
                if (!link) {    //node operation
                    if (!(name.equals("Node"))){
                        int index=this.getNodelist().size();
                        Node newnode=new Node(name,index);
                        this.addNode(newnode);
                    }                    //读取节点完毕
                }
                else {  //link operation
                    if (!(name.equals("Link"))){
                        Node NodeA=this.getNodelist().get(data[1]);
                        Node NodeB=this.getNodelist().get(data[2]);

                        //节点对之间路由距离放大3倍
//                        int length=Integer.parseInt(data[3]) * 3;
                        int length=Integer.parseInt(data[3]);
//
                        int cost=Integer.parseInt(data[4]);
                        int index=this.getLinklist().size();
                        if(NodeA.getIndex()<NodeB.getIndex()){
                            name=NodeA.getName()+"-"+NodeB.getName();
                        }else name=NodeB.getName()+"-"+NodeA.getName();

                        Link newlink=new Link(name,index,NodeA,NodeB,length,cost);   //创建新链路
                        Wavelength wave=new Wavelength();
                        for (int i = 0; i<wave.getWaveTotalNumbers(); i++){			//波长编号
                            newlink.getAvaWavelist().put(i,i);        //链路初始可用波长
                        }


                        this.addLink(newlink);      //将链路添加到链路集合中
                        NodeA.addNeinode(NodeB);
                        NodeB.addNeinode(NodeA);    //相邻节点
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    //create node pair based on the exiting nodelist
    public void generateNodepairs(){
        HashMap<String,Node>map1=this.getNodelist();
        HashMap<String,Node>map2=this.getNodelist();
        Iterator<String> iter1=map1.keySet().iterator(); //获得迭代器iter1
        Random random=new Random(Constant.seed);


        while (iter1.hasNext()){
            Node node1=map1.get(iter1.next());
            Iterator<String > iter2=map2.keySet().iterator();   //获得迭代器iter1
            while (iter2.hasNext()){
                Node node2=map2.get(iter2.next());
                if (!node1.equals(node2)){
                    if (node1.getIndex()<node2.getIndex()){
//                        String name="["+node1.getName()+","+node2.getName()+"]";    //节点对之间的名字
                        String name="["+node2.getName()+","+node1.getName()+"]";    //节点对之间的名字
                        int index=this.getNodepairlist().size();
                        Nodepair nodepair=new Nodepair(name,index,node1,node2);     //创造节点对

                        for (int i=0;i<Constant.serviceSum;i++){
                            int serviceDemand=Constant.lowBound+random.nextInt(Constant.bound);
                            nodepair.getServiceList().put(i,serviceDemand);     //每个节点对之间都创建业务
                        }

                        this.addNodepair(nodepair);
                    }
                }
            }
        }
    }


    //IP层相邻节点对
    public void generateIPNeiNodepairs(Gv gv) {

//            for (Vlink vlink:gv.getVlinks().values()) {
            for (Vlink vlink:gv.getVlinks().values()) {
                //根据Gv中存在的虚拟链路来创建IP层中的相邻节点

                Node node1= vlink.getSrcNode();
                Node node2= vlink.getDesNode();
                String name="["+vlink.getSrcNode().getName()+","+vlink.getDesNode().getName()+"]";
                int index=this.getIPnodepairlist().size();
                IPnodepair IPnodepair=new IPnodepair(name,index,node1,node2);     //创造节点对
                this.addIPNodepair(IPnodepair);

                node1.addIPNeinode(node2);
                node2.addIPNeinode(node1);
            }
    }

    //find a link based on two nodes
    public Link findLink(Node NodeA,Node NodeB){
        String name;
        if (NodeA.getIndex()<NodeB.getIndex()){
            name=NodeA.getName()+"-"+NodeB.getName();
        }else name=NodeB.getName()+"-"+NodeA.getName();
        return this.getLinklist().get(name);
    }





    /***************setters and getters******************/

    public Layer getServerlayer() {
        return serverlayer;
    }

    public void setServerlayer(Layer serverlayer) {
        this.serverlayer = serverlayer;
    }

    public Layer getClientlayer() {
        return clientlayer;
    }

    public void setClientlayer(Layer clientlayer) {
        this.clientlayer = clientlayer;
    }

    public HashMap<String, Node> getNodelist() {
        return nodelist;
    }

    public void setNodelist(HashMap<String, Node> nodelist) {
        this.nodelist = nodelist;
    }

    public HashMap<String, Link> getLinklist() {
        return linklist;
    }

    public void setLinklist(HashMap<String, Link> linklist) {
        this.linklist = linklist;
    }

    public LinkedHashMap<String, Nodepair> getNodepairlist() {
        return nodepairlist;
    }

    public void setNodepairlist(LinkedHashMap<String, Nodepair> nodepairlist) {
        this.nodepairlist = nodepairlist;
    }

    public LinkedHashMap<String, Nodepair> getInvnodepairlist() {
        return invnodepairlist;
    }

    public void setInvnodepairlist(LinkedHashMap<String, Nodepair> invnodepairlist) {
        this.invnodepairlist = invnodepairlist;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public LinkedHashMap<String, IPnodepair> getIPnodepairlist() {
        return IPnodepairlist;
    }

    public void setIPnodepairlist(LinkedHashMap<String, IPnodepair> IPnodepairlist) {
        this.IPnodepairlist = IPnodepairlist;
    }
}
