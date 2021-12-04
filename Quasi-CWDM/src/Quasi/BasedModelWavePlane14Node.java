package Quasi;

import general.Constant;
import network.*;
import routeSearch.Dijkstra;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Jq zhou
 * @Date: 2021/08/24/11:12
 * @Description:
 */
public class BasedModelWavePlane14Node {
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


        Dijkstra dijkstra=new Dijkstra();

        gv=this.trafficGrooming(nodepairlist,servicelist);    //流量疏导方法: 节点对集合，层

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
        System.out.println("改进的WP  业务大小"+sum+"G"+"   seed:"+Constant.seed+"   业务数"+Constant.serviceSum+"  阈值："+Constant.threshold);
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


        //先建立单跳的业务，再建立多跳的业务。当虚拟链路剩余容量少于x%时，不grooming而是建立新的光通道
        //单跳路由的业务不是能grooming就grooming，而是先将建立光通道，可以留给后面的多跳路由来grooming
        for (int service:serviceList) {
            Loop: for (Nodepair nodepair:nodepairlist.values()) {
                Link LINK=layer.findLink(nodepair.getSrcNode(), nodepair.getDesNode());
                if (LINK!=null){

                    for (int a:nodepair.getServiceList().keySet()) {

                        if (nodepair.getServiceList().get(a)==service){     //遍历节点对之间的业务，当节点对之间的业务需求等于业务集合当前值时，继续执行

                            nodepair.getServiceList().remove(a);
                            nodepair.setDemand(service);        //令节点对之间的业务需求等于当前的service
                            Node srcNode = nodepair.getSrcNode();
                            Node desNode = nodepair.getDesNode();

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

                                    ArrayList<Route> routeArrayList= dijkstra.findLeastHopsRoute(nodepair,layer,10,gv);

                                    if (routeArrayList.size()==0){
//                                    System.out.println("途经链路不满足节点对需求！");
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


                                        L:for (Route route:routeArrayList){
                                            //单跳路由的业务不能经过多跳虚拟链路进行流量疏导
                                            if (route.getHops()>1){
                                                break ;
                                            }

                                            for (Vlink v:route.getVlinklist()) {
//                                                double lpRem=Integer.MAX_VALUE;
                                                double lpRem=0;
                                                //虚拟链路上光通道的总剩余容量
//                                                for (Lightpath l:v.getLightPathList()) {
//                                                    lpRem+=l.getRemCapacity();
//                                                }
                                                //虚拟链路上剩余容量最大的光通道
                                                for (Lightpath l:v.getLightPathList()) {
                                                    if (lpRem<l.getRemCapacity()){
                                                        lpRem=l.getRemCapacity();
                                                    }
                                                }
                                                lpRem=lpRem*Constant.threshold;
                                                if (service>lpRem){
                                                    break L;
                                                }
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

                                                int tempArr=0;      //最大的光通道容量
                                                for (Lightpath lp:v.getLightPathList()) {
                                                    if (lp.getRemCapacity()>=service){   //当光通道的容量大于业务时
                                                        if (tempArr<lp.getRemCapacity()){
                                                            tempArr=lp.getRemCapacity();
                                                        }
                                                    }
                                                }

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
                                                    if (lp.getRemCapacity()==tempArr){
                                                        lp.setRemCapacity(lp.getRemCapacity()-service);
                                                        v.setRemCapacity(0);
                                                        break;
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
//                                ArrayList<Lightpath>lightpathList=wavePlane.wavePlaneAlgorithm(wavePlaneList,layer,nodepair,gv);      //波平面
                                ArrayList<Lightpath>lightpathList=wavePlane.PS_WavePlane(wavePlaneList,layer,nodepair,gv,PS);      //波平面

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

        for (int service:serviceList) {
            Loop: for (Nodepair nodepair:nodepairlist.values()) {
                Link LINK=layer.findLink(nodepair.getSrcNode(), nodepair.getDesNode());
                if (LINK==null){
                    for (int a:nodepair.getServiceList().keySet()) {

                        if (nodepair.getServiceList().get(a)==service){     //遍历节点对之间的业务，当节点对之间的业务需求等于业务集合当前值时，继续执行

                            nodepair.getServiceList().remove(a);
                            nodepair.setDemand(service);        //令节点对之间的业务需求等于当前的service
                            Node srcNode = nodepair.getSrcNode();
                            Node desNode = nodepair.getDesNode();

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
//                                    System.out.println("途经链路不满足节点对需求！");
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
                                            if (route.getHops()>3){
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

                                                int tempArr=0;      //最大的光通道容量
                                                for (Lightpath lp:v.getLightPathList()) {
                                                    if (lp.getRemCapacity()>=service){   //当光通道的容量大于业务时
                                                        if (tempArr<lp.getRemCapacity()){
                                                            tempArr=lp.getRemCapacity();
                                                        }
                                                    }
                                                }

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
                                                    if (lp.getRemCapacity()==tempArr){
                                                        lp.setRemCapacity(lp.getRemCapacity()-service);
                                                        v.setRemCapacity(0);
                                                        break;
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
//                                ArrayList<Lightpath>lightpathList=wavePlane.wavePlaneAlgorithm(wavePlaneList,layer,nodepair,gv);      //波平面
                                ArrayList<Lightpath>lightpathList=wavePlane.PS_WavePlane(wavePlaneList,layer,nodepair,gv,PS);      //波平面

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
    }



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
}
