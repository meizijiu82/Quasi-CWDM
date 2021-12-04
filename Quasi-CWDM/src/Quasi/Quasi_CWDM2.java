package Quasi;

import java.io.*;
import java.util.*;

import network.*;
import routeSearch.*;
import general.*;

public class Quasi_CWDM2 {
    Layer layer=new Layer("Mylayer",0);

    public void Quasi_CWDM() throws IOException {
        System.out.println("*************** readTopology **************");
        layer.readTopology("14.csv");
        layer.generateNodepairs();
        LinkedHashMap<String ,Nodepair>nodepairlist= layer.getNodepairlist();   //得到节点对列表
        Iterator <String>iter=nodepairlist.keySet().iterator(); //节点对集合迭代


        /*对节点对之间的流量需求按从大到小的顺序进行排序*/
        LinkedList<Integer>servicelist=new LinkedList<>();


        for (Nodepair nodepair:nodepairlist.values()){
            for (int k:nodepair.getServiceList().keySet()) {
                servicelist.add(nodepair.getServiceList().get(k));
            }
        }
        servicelist.sort(Collections.reverseOrder()); //调用sort方法与reverseOrder方法对集合进行从大到小排序


        for (Nodepair nodepair:nodepairlist.values()) {
            System.out.println(nodepair.getName()+" "+nodepair.getServiceList().values());
//            System.out.println(nodepair.getName());
        }


        HashMap<String,Node>map=layer.getNodelist();    //map为层中节点集合
        Gv gv=new Gv();     //引用虚拟拓扑Gv类

        /*输出物理链路集合*/
//        HashMap<String,Node>map2=layer.getNodelist();
//        Iterator iter2=map.keySet().iterator();
//        System.out.println("set E:=");
//        while (iter2.hasNext()){
//            Node node= map2.get(iter2.next());
//            int i=0;
//            int size=node.getNeinodelist().size();
//            System.out.print("("+node.getName()+",*) ");
//            while (i<size){
//                System.out.print(node.getNeinodelist().get(i).getName()+" ");
//                i++;
//            }
//            System.out.println("");
//        }




        Dijkstra dijkstra=new Dijkstra();

        gv=this.trafficGrooming(nodepairlist,servicelist);    //流量疏导方法: 节点对集合，层
//        double Cost=Integer.MAX_VALUE;
//        while (gv.getTotal_Cost()<Cost){
//            Cost= gv.getTotal_Cost();
//            gv=this.replaceChannels(layer,gv,map,nodepairlist,Cost,servicelist);        //当变换调制格式之后成本升高，应返回之前的拓扑
//        }

        int totalHops=0;
        for (Lightpath lightpath: gv.getLpLinklist()) {
            if (lightpath.getModuFormat()==null) continue;
//            System.out.println("lightpath:"+lightpath.getName()+"  moduFormat:"+lightpath.getModuFormat().getName()+"  channels:"+lightpath.getChannels());
            totalHops+= lightpath.getHops();
        }
        float avaHops=(float) totalHops/gv.getLpLinklist().size();


        File f=new File("src/Quasi/14Node.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(f,true);
        PrintStream printStream = new PrintStream(fileOutputStream);
        System.setOut(printStream);

        System.out.println();
        int sum=Constant.lowBound+Constant.bound-1;
        System.out.println("WP  业务大小"+sum+"G"+"   seed:"+Constant.seed+"   业务数"+Constant.serviceSum);
        System.out.print("最终拓扑：平均跳数："+avaHops);
        System.out.print("   总成本："+gv.getTotal_Cost());
        System.out.println("   光路数："+gv.getLpLinklist().size());
        System.out.println("BPSK:"+gv.getNipB()/2+"   QPSK:"+gv.getNipQ()/2+"   8-QAM:"+gv.getNipM()/2);

        for (Nodepair nodepair:nodepairlist.values()) {
            Node srcNode = nodepair.getSrcNode();
            Node desNode = nodepair.getDesNode();
            Link link = layer.findLink(srcNode, desNode);
            if(link!=null){
                link.setCapacity(0);
                link.setRemCapacity(0);
                //更新链路的剩余容量
                for (Lightpath l: link.getLightpathList()) {
                    link.setRemCapacity(link.getRemCapacity()+l.getRemCapacity());
                    link.setCapacity(link.getCapacity()+l.getCapacity());
                }
            }
        }

        for (Nodepair nodepair:nodepairlist.values()) {
            Node srcNode = nodepair.getSrcNode();
            Node desNode = nodepair.getDesNode();
            Link link = layer.findLink(srcNode, desNode);
            if(link!=null){
                System.out.print("link name:"+link.getName()+"  Capacity:"+link.getCapacity()+"  ");
                System.out.println("remCapacity:"+link.getRemCapacity()+"  channel:"+link.getChannels());
            }
        }

        for (Nodepair nodepair:nodepairlist.values()) {
            Node srcNode=nodepair.getSrcNode();
            Node desNode=nodepair.getDesNode();
            Link link= layer.findLink(srcNode,desNode);
            if (link!=null){
//                System.out.println(link.getWavelengthList().size());
                System.out.println(link.getChannels());
            }
        }

        double aveLpSpeUti=0;
        double aveRemCapacity=0;
        double totalRemCapacity=0;
        double sum1=0;
        for (Lightpath l:gv.getLpLinklist()) {
            sum1+=(double) (l.getCapacity()-l.getRemCapacity())/l.getCapacity();   //按绝对值，而不是按比例
            totalRemCapacity+=l.getRemCapacity();
        }
        aveRemCapacity=totalRemCapacity/gv.getLpLinklist().size();
        aveLpSpeUti=sum1/gv.getLpLinklist().size();
        System.out.println("平均频谱利用率："+aveLpSpeUti+"   平均剩余容量"+aveRemCapacity+"  总的剩余容量"+totalRemCapacity);


        int maxRemCapacity=0;
        for (Lightpath l: gv.getLpLinklist()) {
            if (l.getRemCapacity()>maxRemCapacity){
                maxRemCapacity=l.getRemCapacity();
            }
        }

        System.out.println("光通道最大的剩余容量为："+maxRemCapacity);

        fileOutputStream.close();
        printStream.close();

    }   //Quasi-CWDM方法


    public Gv trafficGrooming(LinkedHashMap<String,Nodepair> nodepairlist,LinkedList<Integer>serviceList){
        Gv gv=new Gv();     //声明虚拟拓扑，用来添加光路

        gv.generateVlinkList(layer);

        Iterator <String>iter=nodepairlist.keySet().iterator(); //节点对集合迭代

        ModuFormat BPSK=new ModuFormat("BPSK",175);
        ModuFormat QPSK=new ModuFormat("QPSK",350);
        ModuFormat eightQAM=new ModuFormat("8-QAM",525);
        Wavelength wavelength=new Wavelength();


        LinkedList<WavePlane>wavePlaneList=new LinkedList<>();
        Wavelength wave=new Wavelength();
        //第一步，读取整个网络上的链路占用的波长情况
        for (int i = 0; i < wave.getWaveTotalNumbers(); i++) {
            WavePlane wavePlane1=new WavePlane("wavePlane",i);
            for (Link link:layer.getLinklist().values()) {
                if (link.getAvaWavelist().containsValue(i)){
                    Node nodeA=link.getSrcNode();
                    Node nodeB=link.getDesNode();
                    wavePlane1.getNodelist().put(nodeA.getName(),nodeA);
                    wavePlane1.getNodelist().put(nodeB.getName(),nodeB);
                    wavePlane1.getLinklist().put(link.getName(),link);
                }
            }
            wavePlaneList.add(wavePlane1);
        }

        double Total_Cost;
        HashSet<Node>existNodeList=new HashSet<>(); //已有节点集合，在流量疏导的时候判断已有光路是否可以连接给定节点对

        satisfyServices(nodepairlist, layer,serviceList, gv, wavePlaneList, existNodeList);
        gv.setLinklist(layer.getLinklist());
        int maxWave=0;
        for (Link link:layer.getLinklist().values()) {
            if (maxWave<link.getChannels())
                maxWave=link.getChannels();
        }

        for (int shuffleNum = 0; shuffleNum < Constant.shuffle; shuffleNum++) {
            System.out.println("********第"+shuffleNum+"次打乱********");
            //业务打乱之后，重新运行之前的算法建立光通道。
            //增加临时变量，与之前运行得到的结果进行比较，返回结果更优的那个
            Collections.shuffle(serviceList);   //打乱
            Gv newGv=new Gv();
            Layer layer1=new Layer("newLayer",1);
            layer1.readTopology("14.csv");
            layer1.generateNodepairs();
            LinkedHashMap<String ,Nodepair>nodepairlist1 = layer1.getNodepairlist();   //得到节点对列表

            Random random=new Random(Constant.seed);
            for (Nodepair nodepair:nodepairlist1.values()) {
                for (int i=0;i<Constant.serviceSum;i++){
                    int serviceDemand=Constant.lowBound+ random.nextInt(Constant.bound);
                    nodepair.getServiceList().put(i,serviceDemand);     //创建节点对之间的业务集合
                }
            }   //重新给每个节点对分配与之前相同的业务

            //shuffle之后，layer已经改变，需要根据当前的layer创建新的波平面集合
            LinkedList<WavePlane>wavePlaneList1=new LinkedList<>();
            for (int i = 0; i < wave.getWaveTotalNumbers(); i++) {
                WavePlane wavePlane1=new WavePlane("wavePlane",i);
                for (Link link:layer1.getLinklist().values()) {
                    if (link.getAvaWavelist().containsValue(i)){
                        Node nodeA=link.getSrcNode();
                        Node nodeB=link.getDesNode();
                        wavePlane1.getNodelist().put(nodeA.getName(),nodeA);
                        wavePlane1.getNodelist().put(nodeB.getName(),nodeB);
                        wavePlane1.getLinklist().put(link.getName(),link);
                    }
                }
                wavePlaneList1.add(wavePlane1);
            }

            newGv.generateVlinkList(layer1);
            satisfyServices(nodepairlist1, layer1,serviceList, newGv, wavePlaneList1, existNodeList);
            newGv.setLinklist(layer1.getLinklist());
            int maxWave1=0;
            for (Link l:layer1.getLinklist().values()) {
                if (maxWave1<l.getChannels())
                    maxWave1=l.getChannels();
            }
            if (maxWave>maxWave1){
                maxWave=maxWave1;
                gv=newGv;
                layer=layer1;
            }
        }

        //根据拓扑中光路的IP端口数和信号再生器数得到拓扑总的IP端口数和信号再生器数
        this.addLightPath(gv);

        System.out.println("拓扑中所有光路： ");
        for (Lightpath lightpath: gv.getLpLinklist()) {
            if (lightpath.getModuFormat()==null) continue;
            System.out.print(lightpath.getName()+"  "+lightpath.getModuFormat().getName()+"  "+lightpath.getOccupyWavelengthIndex()+"  "+lightpath.getDistance());
            System.out.println("  "+lightpath.getDemand()+"  "+lightpath.getViaLink());
        }

        for (Nodepair nodepair:nodepairlist.values()) {
            Node srcNode=nodepair.getSrcNode();
            Node desNode=nodepair.getDesNode();
            Link link= layer.findLink(srcNode,desNode);
            if (link!=null){
                System.out.print("link name:"+link.getName()+"  Capacity:"+link.getCapacity()+"  ");
                System.out.println("remCapacity:"+link.getRemCapacity()+"  channel:"+link.getChannels());
            }
        }
        int sum=0;
        for (Nodepair nodepair:nodepairlist.values()) {
            sum+=nodepair.getBlockService();
        }
        gv.setBlockServiceSum(sum);  //拓扑被阻塞业务总数

        Total_Cost=gv.getNipB()*2+gv.getNipQ()*2.6+gv.getNipM()*3+gv.getNregenB()+gv.getNregenQ()*1.3+gv.getNregenM()*1.5;

        gv.setTotal_Cost(Total_Cost);
        System.out.println("硬件总成本为："+gv.getTotal_Cost()+"  被阻塞业务总数:"+gv.getBlockServiceSum()+"  光路数:"+gv.getLpLinklist().size());

        return gv;
    }

    private void satisfyServices(LinkedHashMap<String, Nodepair> nodepairlist,Layer layer,LinkedList<Integer> serviceList,
                                 Gv gv, LinkedList<WavePlane> wavePlaneList, HashSet<Node> existNodeList) {

        HashMap<Integer, Double>PS=new HashMap<>();
        ModuFormat moduFormat=new ModuFormat(PS);
        PS.put(2340,8.9);
        PS.put(6498,6.3);
        PS.put(3438,7.9);
        PS.put(5400,6.8);
        PS.put(6411,6.4);
        PS.put(6426,6.3);
        PS.put(4608,7.2);
        PS.put(9078,5.2);
        PS.put(7974,5.8);
        PS.put(9438,5.2);
        PS.put(3060,8.3);
        PS.put(5778,8.3);
        PS.put(3804,7.7);
        PS.put(5202,6.8);
        PS.put(2268,9.1);
        PS.put(6738,6.3);
        PS.put(5634,6.7);
        PS.put(7098,6.0);
        PS.put(2988,8.4);
        PS.put(6864,6.2);
        PS.put(5328,6.8);
        PS.put(5922,6.6);
        PS.put(6282,6.4);
        PS.put(8694,5.5);
        PS.put(2934,8.4);
        PS.put(6072,6.4);
        PS.put(3294,8.1);
        PS.put(6300,6.4);
        PS.put(3366,8.0);
        PS.put(6228,6.4);
        PS.put(9132,5.4);
        PS.put(7542,5.9);
        PS.put(6792,6.2);
        PS.put(6048,6.4);
        PS.put(8928,5.4);
        PS.put(8262,5.7);
        PS.put(9468,5.3);
        PS.put(6660,6.2);
        PS.put(8982,5.5);
        PS.put(8766,5.6);
        PS.put(8910,5.5);
        PS.put(9720,5.2);
        PS.put(9648,5.3);
        PS.put(9060,5.4);
        PS.put(8046,5.6);
        PS.put(8190,5.6);
        PS.put(9798,5.2);
        PS.put(6144,6.4);
        PS.put(9360,5.3);
        PS.put(9288,5.3);
        PS.put(9366,5.3);
        PS.put(9342,5.3);
        PS.put(8316,5.5);
        PS.put(8388,5.5);
        PS.put(8496,5.5);
        PS.put(9582,5.2);
        PS.put(1020,10.7);
        PS.put(2640,8.8);
        PS.put(8280,5.7);
        PS.put(1200,10.5);
        PS.put(2520,8.8);
        PS.put(6060,6.5);
        PS.put(1260,10.5);
        PS.put(6000,6.5);
        PS.put(2880,8.4);
        PS.put(4380,7.4);
        PS.put(6480,6.3);
        PS.put(2700,8.6);
        PS.put(4560,7.2);
        PS.put(4620,7.2);
        PS.put(4740,7.2);
        PS.put(3540,8.0);
        PS.put(5820,6.6);
        PS.put(8220,5.6);
        PS.put(4980,7.0);
        PS.put(6780,6.3);
        PS.put(7560,5.9);
        PS.put(8040,5.8);
        PS.put(8160,7.0);
        PS.put(8640,5.5);
        PS.put(6240,7.3);
        PS.put(7140,5.8);
        PS.put(6120,6.4);
        PS.put(6420,6.3);
        PS.put(7380,5.8);
        PS.put(5580,6.7);
        PS.put(7800,5.9);
        PS.put(7920,5.8);
        PS.put(1440,10.2);
        PS.put(2280,9.1);
        PS.put(3960,7.7);
        PS.put(2940,8.4);
        PS.put(5640,6.7);
        PS.put(7980,5.9);
        PS.put(3660,7.8);
        PS.put(5700,6.7);
        PS.put(2460,8.9);
        PS.put(6900,6.2);
        PS.put(8460,5.7);
        PS.put(3900,7.6);
        PS.put(7020,6.0);
        PS.put(9120,5.5);
        PS.put(5220,6.8);
        PS.put(5460,6.7);
        PS.put(7080,6.0);
        PS.put(7320,5.9);
        PS.put(5340,6.8);
        PS.put(7200,5.9);
        PS.put(7620,5.9);
        PS.put(7260,5.9);
        PS.put(4800,7.1);
        PS.put(3180,8.2);
        PS.put(4080,7.5);
        PS.put(6840,6.2);
        PS.put(1500,9.9);
        PS.put(5760,6.7);
        PS.put(9420,5.3);
        PS.put(4920,7.0);
        PS.put(5100,6.9);
        PS.put(7740,5.9);
        PS.put(3780,7.8);
        PS.put(5940,6.7);
        PS.put(7440,5.9);
        PS.put(6180,6.5);
        PS.put(8580,5.6);
        PS.put(8700,5.6);
        PS.put(1620,9.8);
        PS.put(3300,8.1);
        PS.put(3360,8.1);
        PS.put(3120,8.1);
        PS.put(9180,5.4);
        PS.put(9600,5.3);
        PS.put(4860,7.1);
        PS.put(6450,6.2);
        PS.put(1680,9.7);
        PS.put(1740,9.7);
        PS.put(6600,6.2);
        PS.put(6360,6.4);
        PS.put(3420,8.0);
        PS.put(5520,6.8);
        PS.put(7860,5.9);
        PS.put(4440,7.3);
        PS.put(5880,6.7);
        PS.put(4680,7.2);
        PS.put(8940,5.6);
        PS.put(7500,5.9);
        PS.put(9540,5.4);
        PS.put(9660,5.3);
        PS.put(7680,5.9);
        PS.put(3240,8.2);
        PS.put(1800,9.4);
        PS.put(8340,5.7);
        PS.put(5040,6.9);
        PS.put(5160,6.8);
        PS.put(6960,6.2);
        PS.put(3600,7.9);
        PS.put(1860,9.3);
        PS.put(9300,5.4);
        PS.put(1560,9.9);
        PS.put(8760,5.6);
        PS.put(8400,5.6);
        PS.put(1080,6.3);
        PS.put(6540,6.3);
        PS.put(6720,6.3);
        PS.put(4020,7.6);


        for (int service:serviceList) {
            Loop: for (Nodepair nodepair:nodepairlist.values()){

                for (int a:nodepair.getServiceList().keySet()) {
                    if (nodepair.getServiceList().get(a)==service){     //遍历节点对之间的业务，当节点对之间的业务需求等于业务集合当前值时，继续执行

                        nodepair.getServiceList().remove(a);
                        nodepair.setDemand(service);        //令节点对之间的业务需求等于当前的service
                        Node srcNode = nodepair.getSrcNode();
                        Node desNode = nodepair.getDesNode();

//                        String LP_name = srcNode.getName() + "-" + desNode.getName();
//                        Lightpath lightpath = new Lightpath(LP_name);     //得到光路：源点到终点的光通道
//                        lightpath.setSrcNode(srcNode);
//                        lightpath.setDesNode(desNode);
//                        lightpath.setDemand(service);

                        Vlink vlink= gv.findVlink(srcNode,desNode); //找到该节点对对应的IP层虚拟链路

                        Dijkstra dijkstra = new Dijkstra();
                        WavePlane wavePlane=new WavePlane();

                        System.out.println(nodepair.getName()+" demand:"+nodepair.getDemand());

                        //尝试使用Gv的剩余容量来满足节点对之间的流量需求
                        int judge = 0;
                        int judge1=0;

                        /*当拓扑剩余容量大于节点对之间的需求时且已存在节点集合包含此节点对的源点、终点时，判断拓扑是否能满足该节点对之间的需求*/
                        if (existNodeList.contains(desNode) && existNodeList.contains(srcNode)) {
                            boolean success=false;

                            Link chargelink= layer.findLink(srcNode,desNode);
                            /*源点到终点之间有多条链路 或 直连链路无法满足Grooming*/
                            if (!success){
                                //运行改进后的最短路由算法，从exitlinklist中找到一条满足节点对之间路由的光路vialinklist

//                                ArrayList<Route>routeArrayList= dijkstra.findCrossLeastHopsRoute(nodepair,layer,10,gv);
                                ArrayList<Route> routeArrayList= dijkstra.findLeastHopsRoute(nodepair,layer,10,gv);
//                                ArrayList<Route>routeArrayList= dijkstra.findShortestRoute(nodepair,layer,10,gv);

                                if (routeArrayList.size()==0){
//                                 System.out.println("途经链路不满足节点对需求！");
                                }

                                else {

                                    /*考虑IP层负载均衡，选择跳数最小的路由*/
                                    for (Route route:routeArrayList){
                                        //局部变量，只针对当前路由
                                        LinkedList<Integer>VlinkArr=new LinkedList<>(); //由虚拟链路确定路由的剩余容量
                                        for (Vlink v: route.getVlinklist()) {
                                            VlinkArr.add(v.getRemCapacity());       //路由中所有虚拟链路的剩余容量集合
                                        }
                                        Collections.sort(VlinkArr);                      //从小到大排序
                                        route.setRemCapacity(VlinkArr.getFirst());       //选择最小的剩余容量作为路由的剩余容量
                                    }

                                    LinkedList<Integer>routeArr=new LinkedList<>();
                                    for (Route route:routeArrayList) {
                                        routeArr.add(route.getHops());   //路由的跳数
                                    }
                                    Collections.sort(routeArr);  //按从小到大排序
//                                    routeArr.sort(Collections.reverseOrder());  //按从大到小排序

                                    int hops=0;
                                    if (routeArr.size()!=0){
                                        hops=routeArr.getFirst();    //排序后的第一个路由
                                    }

                                    routeArr.clear();

                                    for (Route route:routeArrayList) {
                                        //当路由跳数相同的时候，按路由的剩余容量排序
                                        if (route.getHops()==hops){
                                            routeArr.add(route.getRemCapacity());   //路由的剩余容量
                                        }
                                    }
                                    Collections.sort(routeArr);
                                    int routeRemCapacity=routeArr.getFirst();        //选择剩余容量最小的路由

                                    for (Route route:routeArrayList){
                                        if (route.getHops()>Constant.hop){
                                            break ;
                                        }

                                        //选择的路由应该是可以满足流量疏导需求且跳数最小的
//                                        if (route.getHops()==hops&&route.getRemCapacity()==routeRemCapacity){

                                        //当确定可以满足流量疏导时，更新光通道的剩余容量
//                                            for (Vlink v:route.getVlinklist()) {
////                                                int service2=service;
////                                                v.setRemCapacity(v.getRemCapacity()-nodepair.getDemand());  //更新虚拟链路的剩余容量
//
//                                                //FirstFit选择光路进行grooming
//                                                for (Lightpath lp : v.getLightPathList()) {
////                                                    //当取出的光路不足以满足此业务需求时，将业务拆分依次放入光通道上
////                                                    if ((service2 - lp.getRemCapacity()) >= 0) {
////                                                        service2 = service2 - lp.getRemCapacity();
////                                                        lp.setRemCapacity(0);
////                                                    } else {
////                                                        lp.setRemCapacity(lp.getRemCapacity() - service2);
////                                                        break;
////                                                    }
//
//                                                    //选取第一个能满足业务的光通道来Grooming。业务不能拆分
//                                                    if (lp.getRemCapacity()>=service){
//                                                        lp.setRemCapacity(lp.getRemCapacity()-service);
//                                                        v.setRemCapacity(0);
//                                                    }
//                                                }
//
//                                                //虚拟链路的剩余容量即虚拟链路上剩余容量最大的光路的剩余容量
//                                                for (Lightpath lp:v.getLightPathList()) {
//                                                    if (lp.getRemCapacity()>v.getRemCapacity()){
//                                                        v.setRemCapacity(lp.getRemCapacity());  //更新虚拟链路的剩余容量
//                                                    }
//                                                }
//
//                                            }

                                        for (Vlink v:route.getVlinklist()) {
//                                                int service2=service;
//                                                v.setRemCapacity(v.getRemCapacity()-nodepair.getDemand());  //更新虚拟链路的剩余容量

//                                                LinkedList<Integer>lpArr=new LinkedList<>();
//                                                //当IP层的虚拟链路有多条光通道时，将光通道按照剩余容量比例大小进行排序
//                                                for (Lightpath lp:v.getLightPathList()) {
//                                                    if (lp.getRemCapacity()>=service){   //当光通道的容量大于业务时
//                                                        lpArr.add(lp.getRemCapacity());
////                                                    lpArr.add(lp.getCapacity());    //按容量大小对所有光路进行排序
//                                                        lp.setGrooming(0);              //排序时，光路都没有进行grooming，将其设置为0
//                                                    }
//                                                }
//                                                Collections.sort(lpArr);                  //从小到大排序
//                                                lpArr.sort(Collections.reverseOrder());     //从大到小

//                                                L:for (Integer integer : lpArr) {
                                            for (Lightpath lp : v.getLightPathList()) {
//                                                        if (lp.getRemCapacity() == integer&&lp.getGrooming()==0) {    //从小到大的顺序取出链路上的所有光通道

//                                                            //当取出的光路不足以满足此业务需求时，将业务拆分依次放入光通道上
//                                                            if ((service2 - lp.getRemCapacity()) >= 0) {
//                                                                service2 = service2 - lp.getRemCapacity();
//                                                                lp.setRemCapacity(0);
//                                                                lp.setGrooming(1);
//                                                                break;
//                                                            } else {
//                                                                lp.setRemCapacity(lp.getRemCapacity() - service2);
//                                                                break L;
//                                                            }

                                                //业务不能拆分，选取能满足grooming且剩余容量最大的的光通道
                                                if (lp.getRemCapacity()>=service){
                                                    lp.setRemCapacity(lp.getRemCapacity()-service);
                                                    v.setRemCapacity(0);
                                                    break ;
                                                }
                                                //                                                        }
                                            }
                                            //                                                }

                                            //虚拟链路的剩余容量即虚拟链路上剩余容量最大的光路的剩余容量
                                            for (Lightpath lp:v.getLightPathList()) {
                                                if (lp.getRemCapacity()>v.getRemCapacity()){
                                                    v.setRemCapacity(lp.getRemCapacity());  //更新虚拟链路的剩余容量
                                                }
                                            }

                                        }
                                        System.out.print(route.getName()+"满足流量疏导,之前的剩余容量："+route.getRemCapacity());
                                        route.setRemCapacity(route.getRemCapacity()-nodepair.getDemand());
                                        System.out.println("  之后的剩余容量："+route.getRemCapacity());

                                        //更新链路的剩余容量
                                        for (Nodepair nd:nodepairlist.values()) {
                                            Link link= layer.findLink(nd.getSrcNode(), nd.getDesNode());
                                            if (link!=null){
                                                link.setRemCapacity(0);
                                                link.setCapacity(0);
                                                for (Lightpath lp: link.getLightpathList()) {
                                                    link.setRemCapacity(link.getRemCapacity()+ lp.getCapacity());
                                                    link.setCapacity(link.getCapacity()+ lp.getCapacity());
                                                }
                                            }
                                        }
                                        judge=1;

                                        break ;     //满足Grooming之后，跳出路由循环
                                        //                                        }
                                    }

                                }

                            }

                        }

                        //若不成立，建立一条合适调制格式的光通道来满足节点对上的流量需求
                        if (judge==0){

                            System.out.print("建立新的光通道    ");
//                            ArrayList<Lightpath>lightpathList=wavePlane.wavePlaneAlgorithm(wavePlaneList,layer,nodepair,gv);      //波平面
                            ArrayList<Lightpath>lightpathList=wavePlane.PS_WavePlane(wavePlaneList,layer,nodepair,gv,PS);

                            //波平面算法找到路由并给光通道分配波长，返回业务对应的光通道集合
                            for (Lightpath lightpath:lightpathList) {
                                gv.getLpLinklist().add(lightpath);  //拓扑中添加该光路
                                vlink.getLightPathList().add(lightpath);    //虚拟链路上添加该光路
                                existNodeList.add(srcNode);
                                existNodeList.add(desNode);

                                HashMap<String,Node>map= layer.getNodelist();
                                vlink.setCapacity(vlink.getCapacity()+lightpath.getCapacity());             //更新虚拟链路的容量
                                //将虚拟链路的剩余容量定义为虚拟链路上剩余容量最大的那条光路的剩余容量
                                if (lightpath.getRemCapacity()>vlink.getRemCapacity()){
                                    vlink.setRemCapacity(lightpath.getRemCapacity());
                                }
                                if (lightpath.getModuFormat()!=null){
                                    System.out.print(lightpath.getName()+" moduformat："+lightpath.getModuFormat().getName()+"  光路上通道数：："+lightpath.getChannels()+"  link:");
                                }
                                for (Link link:lightpath.getLplink()) {
                                    System.out.print("  "+link.getName()+"  remCapacity:"+link.getRemCapacity()+"   ");
                                }
                            }
                            System.out.println();
                        }

                        break Loop;
                    }
                }
            }
        }
    }


    public Gv replaceChannels(Layer layer,Gv gv,HashMap<String,Node>map, LinkedHashMap<String,Nodepair>nodepairlist,double Total_Cost,LinkedList<Integer>serviceList){
        double reTotal_Cost=0;
        Gv newGv=new Gv();
        Dijkstra dijkstra=new Dijkstra();
        /*调制格式*/
        ModuFormat BPSK=new ModuFormat("BPSK",175);
        ModuFormat QPSK=new ModuFormat("QPSK",350);
        ModuFormat eightQAM=new ModuFormat("8-QAM",525);
        /*
         * 改变光路调制格式后，给该光路重新配置属性（IP端口，信号再生器，光路链路容量），之后grooming按新光路的属性判断是否满足条件
         * 仅需要改变光路的属性，链路属性在reOptimization里进行更新
         */

        /*  用 QPSK 代替  */
        for (int i=0;i< gv.getLpLinklist().size();i++) {
            Lightpath lightpath = gv.getLpLinklist().get(i);
            if (lightpath.getModuFormat()==null) continue;
            if (lightpath.getModuFormat().getName().equals("QPSK")||lightpath.getModuFormat().getName().equals("8-QAM")) {   //当光路调制格式为最高调制格式8-QAM时，无法再调高光路调制格式，跳过这次循环
                continue;
            }
            System.out.println("****" + lightpath.getName() + "  " + lightpath.getModuFormat().getName() + "****");

            Node srcNode= lightpath.getSrcNode();
            Node desNode= lightpath.getDesNode();

            String srcName=srcNode.getName();
            String desName=desNode.getName();
            int distance= lightpath.getDistance();

            if (lightpath.getModuFormat().getName().equals("BPSK")){
                lightpath.setNipB(0);
                for (int k=0;k<7;k++){
                    if (lightpath.getDemand()<QPSK.getCapacity()*k){
                        lightpath.setChannels(k);
                        lightpath.setNipQ(2* lightpath.getChannels());
                        break;
                    }
                }
                lightpath.setNregenB(0);
                lightpath.setModuFormat(QPSK);
            }
            if (distance>2000) lightpath.setNregenQ(lightpath.getChannels());

            newGv=this.reOptimization(nodepairlist,layer,gv,serviceList);    //运行再优化算法得到优化后的总成本
            if (newGv==gv){
//                System.out.println("优化之后无法满足所有节点对之间的流量需求！");
//                System.out.println("当前拓扑的总成本为："+Total_Cost);
                continue;
            }
            reTotal_Cost=newGv.getNipB()*2+newGv.getNipQ()*2.6+newGv.getNipM()*3+newGv.getNregenB()+newGv.getNregenQ()*1.3+newGv.getNregenM()*1.5;
            newGv.setTotal_Cost(reTotal_Cost);
            if (reTotal_Cost>=Total_Cost){
                lightpath.setModuFormat(BPSK);
                lightpath.setNipQ(0);
                lightpath.setNregenQ(0);
                for (int k=0;k<8;k++){
                    if (lightpath.getDemand()<BPSK.getCapacity()*k){
                        lightpath.setChannels(k);
                        lightpath.setNipB(2* lightpath.getChannels());
                        break;
                    }
                }
                if (distance>=4000) lightpath.setNregenB(lightpath.getChannels());

                System.out.println("优化之前的成本："+Total_Cost+"  优化之后的成本："+reTotal_Cost);
                continue;
            }

            System.out.println("优化之后的总成本："+reTotal_Cost+"  被阻塞业务总数:"+newGv.getBlockServiceSum()+"  光路数:"+newGv.getLpLinklist().size());
            Total_Cost=reTotal_Cost;
            System.out.println();

        }

        /*  用 8-QAM 代替  */
        for (Lightpath lightpath: gv.getLpLinklist()) {

            if (lightpath.getModuFormat()==null) continue;
            if (lightpath.getModuFormat().getName().equals("8-QAM")){   //当光路调制格式为最高调制格式8-QAM时，无法再调高光路调制格式，跳过这次循环
                continue;
            }
            System.out.println("****"+lightpath.getName()+"  "+lightpath.getModuFormat().getName()+"****");

            Node srcNode= lightpath.getSrcNode();
            Node desNode= lightpath.getDesNode();

            String Moduname=null;
            String srcName=srcNode.getName();
            String desName=desNode.getName();
            int distance= lightpath.getDistance();
            /*改变调制格式后，更新光路的IP端口和信号再生器类型*/
            if (lightpath.getModuFormat().getCapacity()<eightQAM.getCapacity()){

                if (lightpath.getModuFormat().getName().equals("BPSK")){
                    Moduname="BPSK";
                    for (int k=0;k<3;k++){
                        if (lightpath.getDemand()<eightQAM.getCapacity()*k){
                            lightpath.setChannels(k);
                            lightpath.setNipM(2* lightpath.getChannels());
                            break;
                        }
                    }
                    lightpath.setNipB(0);
                    lightpath.setNregenB(0);
                }
                else if (lightpath.getModuFormat().getName().equals("QPSK")){
                    Moduname="QPSK";
                    for (int k=0;k<3;k++){
                        if (lightpath.getDemand()<eightQAM.getCapacity()*k){
                            lightpath.setChannels(k);
                            lightpath.setNipM(2* lightpath.getChannels());
                            break;
                        }
                    }
                    lightpath.setNipQ(0);
                    lightpath.setNregenQ(0);
                }

                for (int i=1;i<5;i++){
                    if (distance>1000*i){
                        lightpath.setNregenM(lightpath.getChannels()*i);
                    }
                }
                lightpath.setModuFormat(eightQAM);
            }


            newGv=this.reOptimization(nodepairlist,layer,gv,serviceList);    //运行再优化算法得到优化后的总成本

            if (newGv==gv){
//                System.out.println("优化之后无法满足所有节点对之间的流量需求！");
//                System.out.println("当前拓扑的总成本为："+Total_Cost);
                continue;
            }
            reTotal_Cost=newGv.getNipB()*2+newGv.getNipQ()*2.6+newGv.getNipM()*3+newGv.getNregenB()+newGv.getNregenQ()*1.3+newGv.getNregenM()*1.5;
            newGv.setTotal_Cost(reTotal_Cost);
            if (reTotal_Cost>=Total_Cost){
                newGv.setTotal_Cost(Total_Cost);
                if (Moduname!=null){
                    switch (Moduname){
                        case "BPSK":
                            lightpath.setModuFormat(BPSK);
                            for (int k=0;k<8;k++){
                                if (lightpath.getDemand()<BPSK.getCapacity()*k){
                                    lightpath.setChannels(k);
                                    lightpath.setNipB(2* lightpath.getChannels());
                                    break;
                                }
                            }
                            lightpath.setNipM(0);
                            lightpath.setNregenM(0);
                            if (distance>=4000){
                                lightpath.setNregenB( lightpath.getChannels());
                            }
                            break;
                        case "QPSK":
                            lightpath.setModuFormat(QPSK);
                            for (int k=0;k<8;k++){
                                if (lightpath.getDemand()<QPSK.getCapacity()*k){
                                    lightpath.setChannels(k);
                                    lightpath.setNipQ(2* lightpath.getChannels());
                                    break;
                                }
                            }
                            lightpath.setNipM(0);
                            lightpath.setNregenM(0);
                            if (distance>2000){
                                lightpath.setNregenQ(lightpath.getChannels());
                            }
                            break;
                    }
                }

                System.out.println("优化之前成本为:"+Total_Cost+" ========== 优化之后成本为："+reTotal_Cost+"==============");
                continue;
            }
            System.out.println("优化之后的总成本："+reTotal_Cost+"  被阻塞业务总数："+newGv.getBlockServiceSum()+"  光路数："+newGv.getLpLinklist().size());
            Total_Cost=reTotal_Cost;
            System.out.println();

        }

        //所有光路遍历完全之后，得到的新拓扑与之前的拓扑进行成本对比，返回成本更低的那个
        if (newGv.getTotal_Cost()>=gv.getTotal_Cost()){
            return gv;
        }
        else return newGv;

    }

    public Gv reOptimization(LinkedHashMap<String,Nodepair> nodepairlist,Layer layer,Gv gv,LinkedList<Integer>serviceList) {

        Random random=new Random(Constant.seed);
        for (Nodepair nodepair:nodepairlist.values()) {
            for (int i=0;i<Constant.serviceSum;i++){
                int serviceDemand=Constant.lowBound+ random.nextInt(Constant.bound);
                nodepair.getServiceList().put(i,serviceDemand);     //创建节点对之间的业务集合
            }
        }

        Gv newGv=new Gv();
        double cost;
        HashSet<Link>linklist=new HashSet<>();

        /*调制格式*/
        ModuFormat BPSK=new ModuFormat("BPSK",175);
        ModuFormat QPSK=new ModuFormat("QPSK",350);
        ModuFormat eightQAM=new ModuFormat("8-QAM",525);

        System.out.println("****************新拓扑***************");


        for (Lightpath lightpath: gv.getLpLinklist()) {
            lightpath.setStatus(0);
        }

        //初始化链路属性
        for (Nodepair nodepair: nodepairlist.values()) {
            Node srcNode=nodepair.getSrcNode();
            Node desNode=nodepair.getDesNode();
            nodepair.setBlockService(0);
            Link link= layer.findLink(srcNode,desNode);
            if (link!=null) {
                link.setCost(0.001);
                link.setRemCapacity(0);
                link.setCapacity(0);
                link.setChannels(0);
                link.getWavelengthList().clear();
            }
        }

        Dijkstra dij=new Dijkstra();
        //初始化虚拟链路的属性
        for (Vlink vlink:gv.getVlinks().values()) {
            vlink.setRemCapacity(0);
            vlink.setCapacity(0);
            vlink.getLightPathList().clear();
            vlink.setLength(dij.shortestLength(vlink.getSrcNode(), vlink.getDesNode(), layer));
        }

        /*在原有拓扑的虚拟链路剩余容量上进行流量疏导，并得到新的拓扑*/
//        HashSet<Link>existLinkList=new HashSet<>();
        HashSet<Node>existNodeList=new HashSet<>();

        Dijkstra dijkstra=new Dijkstra();
        Wavelength wavelength=new Wavelength();
        boolean satisfy=true;

        for (int service:serviceList){  //遍历业务集合
            int judge=0;    //
            Loop:for (Nodepair nodepair:nodepairlist.values()) {    //遍历节点对集合

                for (int s:nodepair.getServiceList().keySet()) {    //遍历节点对之间业务集合
                    if (nodepair.getServiceList().get(s)==service){

                        nodepair.getServiceList().remove(s);
                        nodepair.setDemand(service);         //节点对设置当前流量需求
                        Node srcNode=nodepair.getSrcNode();
                        Node desNode=nodepair.getDesNode();
                        Vlink vlink= gv.findVlink(srcNode,desNode);

//                        System.out.println("nodepair:"+nodepair.getName()+"  demand:"+nodepair.getDemand());

                        Link link= layer.findLink(srcNode,desNode);

                        if (existNodeList.contains(srcNode)&& existNodeList.contains(desNode)){
                            boolean success=false;

                            /*源点到终点需要经过多条链路 或 直连链路无法满足Grooming*/
                            if (!success){

                                //找到k条满足grooming的路由
//                                ArrayList<Route>routeArrayList= dijkstra.findShortestRoute(nodepair,layer,10,gv);
                                ArrayList<Route>routeArrayList= dijkstra.findLeastHopsRoute(nodepair,layer,10,gv);
                                if (routeArrayList.size()==0){
//                                   System.out.print("途经链路不满足节点对需求！  ");
                                }
                                else {

                                    /*考虑IP层负载均衡，选择跳数最小的路由*/
                                    for (Route route:routeArrayList){
                                        //局部变量，只针对当前路由
                                        LinkedList<Integer>VlinkArr=new LinkedList<>(); //由虚拟链路确定路由的剩余容量
                                        for (Vlink v: route.getVlinklist()) {
                                            VlinkArr.add(v.getRemCapacity());       //路由中所有虚拟链路的剩余容量集合
                                        }
                                        Collections.sort(VlinkArr);                      //从小到大排序
                                        route.setRemCapacity(VlinkArr.getFirst());       //选择最小的剩余容量作为路由的剩余容量
                                    }

                                    LinkedList<Integer>routeArr=new LinkedList<>();
                                    for (Route route:routeArrayList) {
                                        routeArr.add(route.getHops());   //路由的跳数
                                    }
                                    Collections.sort(routeArr);  //按从小到大排序
//                                    routeArr.sort(Collections.reverseOrder());  //按从大到小排序

                                    int hops=0;
                                    if (routeArr.size()!=0){
                                        hops=routeArr.getFirst();    //排序后的第一个路由
                                    }

                                    routeArr.clear();

                                    for (Route route:routeArrayList) {
                                        //当路由跳数相同的时候，按路由的剩余容量排序
                                        if (route.getHops()==hops){
                                            routeArr.add(route.getRemCapacity());   //路由的剩余容量
                                        }
                                    }
                                    Collections.sort(routeArr);
                                    int routeRemCapacity=routeArr.getFirst();        //选择剩余容量最大的路由

                                    for (Route route:routeArrayList){

                                        //选择的路由应该是可以满足流量疏导需求且跳数最小的
                                        if (route.getHops()==hops&&route.getRemCapacity()==routeRemCapacity){
                                            //当确定可以满足流量疏导时，更新光通道的剩余容量
//                                            for (Vlink v:route.getVlinklist()) {
////                                                int service2=service;
////                                                v.setRemCapacity(v.getRemCapacity()-nodepair.getDemand());  //更新虚拟链路的剩余容量
//
//                                                //FirstFit选择光路进行grooming
//                                                for (Lightpath lp : v.getLightPathList()) {
////                                                    //当取出的光路不足以满足此业务需求时，将业务拆分依次放入光通道上
////                                                    if ((service2 - lp.getRemCapacity()) >= 0) {
////                                                        service2 = service2 - lp.getRemCapacity();
////                                                        lp.setRemCapacity(0);
////                                                    } else {
////                                                        lp.setRemCapacity(lp.getRemCapacity() - service2);
////                                                        break;
////                                                    }
//
//                                                    //选取第一个能满足业务的光通道来Grooming。业务不能拆分
//                                                    if (lp.getRemCapacity()>=service){
//                                                        lp.setRemCapacity(lp.getRemCapacity()-service);
//                                                        v.setRemCapacity(0);
//                                                    }
//                                                }
//
//                                                //虚拟链路的剩余容量即虚拟链路上剩余容量最大的光路的剩余容量
//                                                for (Lightpath lp:v.getLightPathList()) {
//                                                    if (lp.getRemCapacity()>v.getRemCapacity()){
//                                                        v.setRemCapacity(lp.getRemCapacity());  //更新虚拟链路的剩余容量
//                                                    }
//                                                }
//
//
//                                            }

                                            for (Vlink v:route.getVlinklist()) {
//                                                int service2=service;
//                                                v.setRemCapacity(v.getRemCapacity()-nodepair.getDemand());  //更新虚拟链路的剩余容量

                                                LinkedList<Integer>lpArr=new LinkedList<>();
                                                //当IP层的虚拟链路有多条光通道时，将光通道按照剩余容量比例大小进行排序
                                                for (Lightpath lp:v.getLightPathList()) {
                                                    if (lp.getRemCapacity()>service){
                                                        lpArr.add(lp.getRemCapacity());
//                                                    lpArr.add(lp.getCapacity());    //按容量大小对所有光路进行排序
                                                        lp.setGrooming(0);              //排序时，光路都没有进行grooming，将其设置为0
                                                    }
                                                }
                                                Collections.sort(lpArr);                  //从小到大排序
//                                                lpArr.sort(Collections.reverseOrder());     //从大到小

                                                L:for (Integer integer : lpArr) {
                                                    for (Lightpath lp : v.getLightPathList()) {
                                                        if (lp.getRemCapacity() == integer&&lp.getGrooming()==0) {    //从小到大的顺序取出链路上的所有光通道

//                                                            //当取出的光路不足以满足此业务需求时，将业务拆分依次放入光通道上
//                                                            if ((service2 - lp.getRemCapacity()) >= 0) {
//                                                                service2 = service2 - lp.getRemCapacity();
//                                                                lp.setRemCapacity(0);
//                                                                lp.setGrooming(1);
//                                                                break;
//                                                            } else {
//                                                                lp.setRemCapacity(lp.getRemCapacity() - service2);
//                                                                break L;
//                                                            }

                                                            //业务不能拆分，选取能满足grooming且剩余容量最大的的光通道
                                                            if (lp.getRemCapacity()>=service){
                                                                lp.setRemCapacity(lp.getRemCapacity()-service);
                                                                v.setRemCapacity(0);
                                                                break L;
                                                            }
                                                        }
                                                    }
                                                }

                                                //虚拟链路的剩余容量即虚拟链路上剩余容量最大的光路的剩余容量
                                                for (Lightpath lp:v.getLightPathList()) {
                                                    if (lp.getRemCapacity()>v.getRemCapacity()){
                                                        v.setRemCapacity(lp.getRemCapacity());  //更新虚拟链路的剩余容量
                                                    }
                                                }

                                            }
//                                            System.out.print(route.getName()+"满足流量疏导,之前的剩余容量："+route.getRemCapacity());
                                            route.setRemCapacity(route.getRemCapacity()-nodepair.getDemand());
//                                            System.out.println("  之后的剩余容量："+route.getRemCapacity());

                                            //更新链路的剩余容量
                                            for (Nodepair nd:nodepairlist.values()) {
                                                Link link1= layer.findLink(nd.getSrcNode(), nd.getDesNode());
                                                if (link1!=null){
                                                    link1.setRemCapacity(0);
                                                    link1.setCapacity(0);
                                                    for (Lightpath lp: link1.getLightpathList()) {
                                                        link1.setRemCapacity(link1.getRemCapacity()+ lp.getCapacity());
                                                        link1.setCapacity(link1.getCapacity()+ lp.getCapacity());
                                                    }
                                                }
                                            }
                                            judge=1;

                                            break ;     //满足Grooming之后，跳出路由循环
                                        }
                                    }
                                }

                            }
                        }

                        //程序执行到这，表示无法对此业务进行grooming，启用拓扑中之前建立的光路以满足该业务需求
                        if (judge==0){
                            int a=0;
                            for (Lightpath lightpath:gv.getLpLinklist()){       //遍历原本拓扑光路集合
                                if (lightpath.getDemand()==service&&lightpath.getStatus()==0){
                                    if (lightpath.getModuFormat()==null)  break;
                                    lightpath.setStatus(1);         //启用光路，将该光路状态设置为1
                                    vlink.getLightPathList().add(lightpath);

                                    lightpath.setRemCapacity(0);    //初始化光路剩余容量

                                    lightpath.setCapacity(lightpath.getModuFormat().getCapacity()* lightpath.getChannels());

                                    existNodeList.add(srcNode);
                                    existNodeList.add(desNode);
                                    HashMap<String,Node>map= layer.getNodelist();


                                    //更新光路上的链路属性
                                    for (Link link1:lightpath.getLplink()) {
//                                        System.out.println("启用原先拓扑上的光路");
                                        link1.setChannels(link1.getChannels()+lightpath.getChannels());
                                        link1.setCapacity(link1.getCapacity()+lightpath.getCapacity());
                                        link1.setRemCapacity(link1.getRemCapacity()+lightpath.getCapacity());
//                                        System.out.print(link1.getName()+"之前的链路容量："+link1.getRemCapacity());
                                        link1.setRemCapacity(link1.getRemCapacity()-lightpath.getDemand());
//                                        System.out.println("  "+link1.getName()+"之后的链路容量："+link1.getRemCapacity());

//                                        vlink.setLength(vlink.getLength()+link1.getLength());
                                    }
                                    lightpath.setRemCapacity(lightpath.getCapacity()- nodepair.getDemand());   //更新光路剩余容量

                                    dijkstra.AssignWavelength(lightpath);

//                                    System.out.println("新拓扑添加光路:"+lightpath.getName()+"   channels:"+lightpath.getChannels()+
//                                            "  moduFormat:"+lightpath.getModuFormat().getName()+"   distance:"+lightpath.getDistance());

                                    newGv.getLpLinklist().add(lightpath);
                                    //更新虚拟链路属性（容量、剩余容量）
                                    vlink.setCapacity(vlink.getCapacity()+lightpath.getCapacity());
//                                    vlink.setRemCapacity(vlink.getRemCapacity()+lightpath.getRemCapacity());

                                    //将虚拟链路的剩余容量定义为虚拟链路上剩余容量最大的那条光路的剩余容量
                                    if (lightpath.getRemCapacity()>vlink.getRemCapacity()){
                                        vlink.setRemCapacity(lightpath.getRemCapacity());
                                    }

                                    a=1;
                                    break ;
                                }
                            }
                            if (a==0){
                                /*拓扑中不存在该光路 或 拓扑中的光路无法满足节点对之间业务需求，建立新光路*/
                                HashMap<String,Node>map= layer.getNodelist();
                                String name=srcNode.getName()+"-"+desNode.getName();
                                Lightpath lightpath=new Lightpath(name);
                                lightpath.setSrcNode(srcNode);
                                lightpath.setDesNode(desNode);
                                vlink.getLightPathList().add(lightpath);    //将新建立的光路添加到对应的虚拟链路光路集合中

                                dijkstra.findMinCostRoute(srcNode,desNode,layer,lightpath);       //找到最小cost路由
//                                dijkstra.dijkstra(srcNode,desNode,layer,lightpath);
                                lightpath.setDemand(service);               //建立新光路，设置光路的业务需求


                                /*以光路的距离为参考建立光通道*/
                                if (desNode.getLength_from_src()>=2000){
                                    for (int i=1;i<10;i++){
                                        if (nodepair.getDemand()<BPSK.getCapacity()*i){
                                            lightpath.setChannels(i);
                                            lightpath.setModuFormat(BPSK);
                                            lightpath.setCapacity(lightpath.getChannels()*lightpath.getModuFormat().getCapacity());
                                            gv.setRemCapacity(BPSK.getCapacity()*i-nodepair.getDemand()+ gv.getRemCapacity());
                                            lightpath.setNipB(2*i);

                                            int distance=0;
                                            for (Link layerlink:lightpath.getLplink()) {
                                                distance+=layerlink.getLength();
//                                                existLinkList.add(layerlink);
                                                layerlink.setChannels(layerlink.getChannels()+i);
                                                layerlink.setCapacity(layerlink.getCapacity()+ BPSK.getCapacity()*i);
                                                layerlink.setRemCapacity(layerlink.getRemCapacity()+BPSK.getCapacity()*i-nodepair.getDemand());

                                            }
                                            lightpath.setRemCapacity(lightpath.getCapacity()- nodepair.getDemand());
                                            lightpath.setDistance(distance);    //设置光路距离
                                            /*波长分配*/
                                            dijkstra.AssignWavelength(lightpath);

                                            if (desNode.getLength_from_src()>=4000) lightpath.setNipB(i);
                                            break;
                                        }
                                    }
                                }
                                else if(desNode.getLength_from_src()>=1000){
                                    for (int i=1;i<5;i++){
                                        if (nodepair.getDemand()<QPSK.getCapacity()*i){
                                            lightpath.setChannels(i);
                                            lightpath.setModuFormat(QPSK);
                                            lightpath.setCapacity(lightpath.getChannels()*lightpath.getModuFormat().getCapacity());
                                            gv.setRemCapacity(QPSK.getCapacity()*i-nodepair.getDemand()+ gv.getRemCapacity());
                                            lightpath.setNipQ(2*i);

                                            int distance=0;
                                            for (Link layerlink:lightpath.getLplink()) {
                                                distance+=layerlink.getLength();
//                                                existLinkList.add(layerlink);
                                                layerlink.setChannels(layerlink.getChannels()+i);
                                                layerlink.setCapacity(layerlink.getCapacity()+ QPSK.getCapacity()*i);
                                                layerlink.setRemCapacity(layerlink.getRemCapacity()+QPSK.getCapacity()*i-nodepair.getDemand());
                                            }
                                            lightpath.setRemCapacity(lightpath.getCapacity()-nodepair.getDemand());
                                            lightpath.setDistance(distance);    //设置光路距离
                                            /*波长分配*/
                                            dijkstra.AssignWavelength(lightpath);

                                            if (desNode.getLength_from_src()>=2000) lightpath.setNregenQ(i);
                                            break;
                                        }
                                    }
                                }
                                else if (desNode.getLength_from_src()<1000){
                                    for (int i=1;i<3;i++){
                                        /*节点对之间的距离小于1000时，先用QPSK尝试建立连接，若失败，再用8-QAM建立*/
                                        if (nodepair.getDemand()<350){
                                            lightpath.setChannels(i);
                                            lightpath.setModuFormat(QPSK);
                                            lightpath.setCapacity(lightpath.getChannels()*lightpath.getModuFormat().getCapacity());
                                            gv.setRemCapacity(QPSK.getCapacity()*i-nodepair.getDemand()+ gv.getRemCapacity());
                                            lightpath.setNipQ(2*i);

                                            int distance=0;
                                            for (Link layerlink:lightpath.getLplink()) {
                                                distance+= layerlink.getLength();
//                                                existLinkList.add(layerlink);
                                                layerlink.setChannels(layerlink.getChannels()+i);
                                                layerlink.setCapacity(layerlink.getCapacity()+ QPSK.getCapacity()*i);
                                                layerlink.setRemCapacity(layerlink.getRemCapacity()+QPSK.getCapacity()*i-nodepair.getDemand());
                                            }
                                            lightpath.setRemCapacity(lightpath.getCapacity()-nodepair.getDemand());
                                            lightpath.setDistance(distance);    //设置光路距离

                                            /*波长分配*/
                                            dijkstra.AssignWavelength(lightpath);

                                            if (desNode.getLength_from_src()>=1000&&desNode.getLength_from_src()<2000) lightpath.setNregenQ(i);
                                            else if (desNode.getLength_from_src()>=2000) lightpath.setNregenQ(2*i);
                                            break;
                                        }
                                        else if (nodepair.getDemand()<eightQAM.getCapacity()*i){
                                            lightpath.setChannels(i);
                                            lightpath.setModuFormat(eightQAM);
                                            lightpath.setCapacity(lightpath.getChannels()*lightpath.getModuFormat().getCapacity());
                                            gv.setRemCapacity(eightQAM.getCapacity()*i-nodepair.getDemand()+ gv.getRemCapacity());
                                            lightpath.setNipM(2*i);
                                            int distance=0;
                                            for (Link layerlink:lightpath.getLplink()) {
                                                distance+=layerlink.getLength();
//                                                existLinkList.add(layerlink);
                                                layerlink.setChannels(layerlink.getChannels()+i);
                                                layerlink.setCapacity(layerlink.getCapacity()+ eightQAM.getCapacity()*i);
                                                layerlink.setRemCapacity(layerlink.getRemCapacity()+eightQAM.getCapacity()*i-nodepair.getDemand());
                                            }
                                            lightpath.setRemCapacity(lightpath.getCapacity()-nodepair.getDemand());
                                            lightpath.setDistance(distance);    //设置光路距离

                                            /*波长分配*/
                                            dijkstra.AssignWavelength(lightpath);

                                            if (desNode.getLength_from_src()>=1000&&desNode.getLength_from_src()<2000) lightpath.setNregenM(i);
                                            else if (desNode.getLength_from_src()>=2000) lightpath.setNregenM(2*i);
                                            break;
                                        }
                                    }
                                }

                                vlink.setCapacity(vlink.getCapacity()+lightpath.getCapacity());
//                                vlink.setRemCapacity(vlink.getRemCapacity()+lightpath.getRemCapacity());

                                //将虚拟链路的剩余容量定义为虚拟链路上剩余容量最大的那条光路的剩余容量
                                if (lightpath.getRemCapacity()>vlink.getRemCapacity()){
                                    vlink.setRemCapacity(lightpath.getRemCapacity());
                                }

                                //建立光通道的时候已经更新了链路的属性，接下来的步骤是为了在光路上添加链路
                                existNodeList.add(srcNode);
                                existNodeList.add(desNode);
//                                System.out.println();
//                                System.out.println("新拓扑添加光路:"+lightpath.getName()+"   channels:"+lightpath.getChannels()
//                                        +"  moduFormat:"+lightpath.getModuFormat().getName());
//                                for (Link l:lightpath.getLplink()) {
//                                    System.out.println(l.getName()+"  rem:"+l.getRemCapacity());
//                                }
                                newGv.getLpLinklist().add(lightpath);

                            }

                        }

                        /*
                         * 程序执行到此，说明业务集合中的一个业务已经建立成功，直接跳过遍历节点对循环，建立业务集合中的下一个业务
                         * 遍历业务之后会将其从节点对的业务集合中删除，如果不加下面这条语句，该业务建立之后会一直遍历节点对之间的业务集合，
                         * 会出现ConcurrentModificationException异常
                         */
                        break Loop;
                    }
                }

            }

        }

        if (satisfy){
            this.addLightPath(newGv);
            cost=newGv.getNipB()*2+newGv.getNipQ()*2.6+newGv.getNipM()*3+newGv.getNregenB()+newGv.getNregenQ()*1.3+newGv.getNregenM()*1.5;
            newGv.setTotal_Cost(cost);

//            HashMap<String,Vlink>hashMap=new HashMap<>();
//            for (Vlink v:existVlinkList) {
//                hashMap.put(v.getName(),v);
//            }
//            newGv.setVlinks(existVlinkList);

            for (Lightpath lightpath: newGv.getLpLinklist()) {
                System.out.println(lightpath.getName()+"  "+lightpath.getModuFormat().getName()+"  "+lightpath.getChannels()+"  "+lightpath.getDistance());
            }
            int sum=0;
            for (Nodepair nodepair:nodepairlist.values()) {
                sum+=nodepair.getBlockService();
            }
            newGv.setBlockServiceSum(sum);  //拓扑被阻塞业务总数

            if (newGv.getTotal_Cost()<gv.getTotal_Cost()){
//                System.out.println("新拓扑被阻塞业务总数："+newGv.getBlockServiceSum());

                for (Nodepair nodepair:nodepairlist.values()) {
                    Node srcNode=nodepair.getSrcNode();
                    Node desNode=nodepair.getDesNode();
                    Link link= layer.findLink(srcNode,desNode);
                    float s=0;
                    if (link!=null){
                        System.out.print("link name:"+link.getName()+"  Capacity:"+link.getCapacity()+"  ");
                        System.out.print("remCapacity:"+link.getRemCapacity()+"  channel:"+link.getChannels());
                        System.out.print("  === 链路占用波长数为: ");
                        System.out.println(link.getWavelengthList().size()+"  ===");
                        s = 1 - (float) link.getRemCapacity()/link.getCapacity();
                        link.setSpectrumEfficiency(s);
                    }
                }
            }

            newGv.setVlinks(gv.getVlinks());
            return newGv;
        }
        else return gv;

    }           //reOptimization方法结束




    private void addLightPath(Gv newGv) {
        for (Lightpath lightpath: newGv.getLpLinklist()){
            newGv.setNipB(newGv.getNipB()+lightpath.getNipB());
            newGv.setNipQ(newGv.getNipQ()+lightpath.getNipQ());
            newGv.setNipM(newGv.getNipM()+lightpath.getNipM());
            newGv.setNregenB(newGv.getNregenB()+lightpath.getNregenB());
            newGv.setNregenQ(newGv.getNregenQ()+lightpath.getNregenQ());
            newGv.setNregenM(newGv.getNregenM()+lightpath.getNregenM());
        }

        System.out.print("ip数：B "+newGv.getNipB()+"  Q:"+newGv.getNipQ()+"  M:"+newGv.getNipM());
        System.out.println("  regen数：B "+newGv.getNregenB()+"  Q:"+newGv.getNregenQ()+"  M:"+newGv.getNregenM());

    }

    private void shortestPath(HashMap<String, Node> nodeList, Layer layer, Node srcNode, Node desNode) {

        Dijkstra dijkstra = new Dijkstra();
        ArrayList<Node>visitedNodeList=new ArrayList<>();

        for (String s : nodeList.keySet()) {     //将map中的节点初始化
            Node node = nodeList.get(s);    //创建节点对象，指向map中正在迭代的节点元素
            node.setLength_from_src(Constant.infinite);
            node.setParentNode(null);
            node.setStatus(Constant.unvisited);
        }

        Node currentNode= srcNode;       //声明中间节点,初始赋值为srcNode
        currentNode.setLength_from_src(0);
        currentNode.setStatus(Constant.visitedTwice);
        //找到最短路径
        while(currentNode!= desNode) {
            visitedNodeList.remove(currentNode);

            for(Node node:currentNode.getNeinodelist()) {
                Link link= layer.findLink(currentNode, node);
                if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的距离
                    node.setStatus(Constant.visitedOnce);
                    node.setLength_from_src(currentNode.getLength_from_src()+link.getLength());
                    visitedNodeList.add(node);
                    node.setParentNode(currentNode);
                }
                else if(node.getStatus()==Constant.visitedOnce) {	//之后再遍历，更新currentNode节点相邻节点到源点的距离
                    if(node.getLength_from_src()>currentNode.getLength_from_src()+link.getLength()) {
                        node.setLength_from_src(currentNode.getLength_from_src()+link.getLength());
                        node.setParentNode(currentNode);
                    }
                }
            }
            //遍历结束，在已访问节点集合中找到距离源点最小的节点，赋给currentNode节点
            currentNode= dijkstra.getShortestLengthNode(visitedNodeList);
        }
        int distance= currentNode.getLength_from_src();
        desNode.setLength_from_src(distance);
    }

    public int getWavelengthOccupancy(LinkedHashMap<String,Nodepair>nodepairlist, Layer layer){
        Wavelength wave=new Wavelength();
        LinkedList<Integer>List=new LinkedList<>();
        LinkedList<Integer>totusedlist=new LinkedList<>();      //总使用波长集合
        for (int i = 0; i<wave.getWaveTotalNumbers(); i++){
            totusedlist.add(i);
        }
        int i=0,waveNum=0;
        for (String s : nodepairlist.keySet()) {     //根据节点对列表遍历网络上的所有链路
            Nodepair nodePair = nodepairlist.get(s);
            Node nodeA = nodePair.getSrcNode();
            Node nodeB = nodePair.getDesNode();
            Link link = layer.findLink(nodeA, nodeB);     //得到节点和链路

            if (link != null) {
                System.out.print(link.getName() + "所占波长：" + link.getWavelengthList().size() + "  ");
                i++;
                if (i % 6 == 0) {
                    System.out.println();
                }
                List.addAll(link.getWavelengthList());
            }
        }
        System.out.println();
        totusedlist.retainAll(List);
        waveNum=totusedlist.size();
        System.out.println("网络上所有占用波长："+totusedlist+" 总波长数："+waveNum);

        return waveNum;
    }

}