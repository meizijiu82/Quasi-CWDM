package Quasi;

import general.Constant;
import network.*;
import routeSearch.Dijkstra;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Jq zhou
 * @Date: 2021/09/06/20:28
 * @Description:
 */
public class BasedModelShortest {
    Layer layer=new Layer("Mylayer",0);

    public void Quasi_CWDM() throws IOException {
        System.out.println("*************** readTopology **************");
        layer.readTopology("6.csv");
        layer.generateNodepairs();
        LinkedHashMap<String , Nodepair> nodepairlist= layer.getNodepairlist();   //得到节点对列表
        Iterator<String> iter=nodepairlist.keySet().iterator(); //节点对集合迭代


        /*对节点对之间的流量需求按从大到小的顺序进行排序*/
        LinkedList<Integer> servicelist=new LinkedList<>();


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


        HashMap<String, Node> map=layer.getNodelist();    //map为层中节点集合
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


        File f=new File("src/Quasi/6Node.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(f,true);
        PrintStream printStream = new PrintStream(fileOutputStream);
        System.setOut(printStream);

        System.out.println();
        int sum= Constant.lowBound+Constant.bound-1;
        System.out.println("改进之后的SR  业务大小"+sum+"G"+"   seed:"+Constant.seed+"   业务数"+Constant.serviceSum);
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
            Node srcNode=nodepair.getSrcNode();
            Node desNode=nodepair.getDesNode();
            Link link= layer.findLink(srcNode,desNode);
            if (link!=null){
                System.out.print("link name:"+link.getName()+"  Capacity:"+link.getCapacity()+"  ");
                System.out.println("remCapacity:"+link.getRemCapacity()+"  channel:"+link.getChannels());
            }
        }



        for (Nodepair nodepair:nodepairlist.values()) {
            Node srcNode=nodepair.getSrcNode();
            Node desNode=nodepair.getDesNode();
            Link link= layer.findLink(srcNode,desNode);
            if (link!=null){
                System.out.println(link.getWavelengthList().size());
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

        double Total_Cost;
        HashSet<Node>existNodeList=new HashSet<>(); //已有节点集合，在流量疏导的时候判断已有光路是否可以连接给定节点对


        LinkedList<Integer>serviceList1=new LinkedList<>();
        for (Nodepair nodepair:nodepairlist.values()) {
            Link LINK= layer.findLink(nodepair.getSrcNode(), nodepair.getDesNode());
            if (LINK!=null){
                serviceList1.addAll(nodepair.getServiceList().values());
            }
        }
        serviceList1.sort(Collections.reverseOrder());  //从大到小排序

        LinkedList<Integer>serviceList2=new LinkedList<>();
        for (Nodepair nodepair:nodepairlist.values()) {
            Link LINK= layer.findLink(nodepair.getSrcNode(), nodepair.getDesNode());
            if (LINK==null){
                serviceList2.addAll(nodepair.getServiceList().values());
            }
        }
        serviceList2.sort(Collections.reverseOrder());  //从大到小排序

        //得到初始拓扑
        satisfyServices(nodepairlist, layer, serviceList1,serviceList2, gv, BPSK, QPSK, eightQAM, existNodeList);
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
            Collections.shuffle(serviceList1);   //打乱
            Collections.shuffle(serviceList2);   //打乱
            Gv newGv=new Gv();
            Layer layer1=new Layer("newLayer",1);
            layer1.readTopology("6.csv");
            layer1.generateNodepairs();
            LinkedHashMap<String ,Nodepair>nodepairlist1 = layer1.getNodepairlist();   //得到节点对列表

            Random random=new Random(Constant.seed);
            for (Nodepair nodepair:nodepairlist1.values()) {
                for (int i=0;i<Constant.serviceSum;i++){
                    int serviceDemand=Constant.lowBound+ random.nextInt(Constant.bound);
                    nodepair.getServiceList().put(i,serviceDemand);     //创建节点对之间的业务集合
                }
            }   //重新给每个节点对分配与之前相同的业务

            newGv.generateVlinkList(layer1);
            satisfyServices(nodepairlist1, layer1, serviceList1, serviceList2, newGv, BPSK, QPSK, eightQAM, existNodeList);
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


    private void satisfyServices(LinkedHashMap<String, Nodepair> nodepairlist, Layer layer, LinkedList<Integer> serviceList1,LinkedList<Integer> serviceList2,
                                 Gv gv, ModuFormat BPSK, ModuFormat QPSK, ModuFormat eightQAM, HashSet<Node> existNodeList) {
        for (int service:serviceList1) {
            Loop: for (Nodepair nodepair:nodepairlist.values()) {
                Link LINK=layer.findLink(nodepair.getSrcNode(),nodepair.getDesNode());
                if (LINK!=null){
                    for (int a:nodepair.getServiceList().keySet()) {

                        if (nodepair.getServiceList().get(a)==service){     //遍历节点对之间的业务，当节点对之间的业务需求等于业务集合当前值时，继续执行

                            nodepair.getServiceList().remove(a);
                            nodepair.setDemand(service);        //令节点对之间的业务需求等于当前的service
                            Node srcNode = nodepair.getSrcNode();
                            Node desNode = nodepair.getDesNode();

                            String LP_name = srcNode.getName() + "-" + desNode.getName();
                            Lightpath lightpath = new Lightpath(LP_name);     //得到光路：源点到终点的光通道
                            lightpath.setSrcNode(srcNode);
                            lightpath.setDesNode(desNode);
                            lightpath.setDemand(service);

                            Vlink vlink= gv.findVlink(srcNode,desNode); //找到该节点对对应的IP层虚拟链路

                            Dijkstra dijkstra = new Dijkstra();

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
        //                                ArrayList<Route>routeArrayList= dijkstra.findShortestRoute(nodepair,layer,10,gv);

                                    if (routeArrayList.size()==0){
        //                                    System.out.println("途经链路不满足节点对需求！");
                                    }

                                    else {
                                        /*考虑IP层负载均衡，最大剩余容量*/
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
                                            routeArr.add(route.getHops());   //得到所有路由的剩余容量
                                        }
                                        Collections.sort(routeArr);                     //按从小到大排序,剩余容量少的优先考虑
        //                                    routeArr.sort(Collections.reverseOrder());    //剩余容量多的优先考虑

                                        int routeHops=0;     //路由剩余容量
                                        if (routeArr.size()!=0){
                                            routeHops=routeArr.getFirst();    //排序后的第一个路由
                                        }

                                        L:for (Route route:routeArrayList){
                                            if (route.getHops()>1){
                                                break ;
                                            }


                                            //虚拟链路上光通道的总剩余容量
                                            for (Vlink v:route.getVlinklist()) {
                                                double lpRem=0,threshold=0.05;
                                                for (Lightpath l:v.getLightPathList()) {
                                                    lpRem+=l.getRemCapacity();
                                                }
                                                if (service>lpRem*threshold){
                                                    break L;
                                                }
                                            }

                                            //选择的路由应该是可以满足流量疏导需求且剩余容量最少的
        //                                        if (route.getLength()==routeLength&&route.getVlinklist().size()!=0){

                                            // 当确定可以满足流量疏导时，更新光通道的剩余容量
                                            for (Vlink v:route.getVlinklist()) {
        //                                                int service2=service;
        //                                                v.setRemCapacity(v.getRemCapacity()-nodepair.getDemand());  //更新虚拟链路的剩余容量

                                                //FirstFit选择光路进行grooming
                                                for (Lightpath lp : v.getLightPathList()) {
        //                                                    //当取出的光路不足以满足此业务需求时，将业务拆分依次放入光通道上
        //                                                    if ((service2 - lp.getRemCapacity()) >= 0) {
        //                                                        service2 = service2 - lp.getRemCapacity();
        //                                                        lp.setRemCapacity(0);
        //                                                    } else {
        //                                                        lp.setRemCapacity(lp.getRemCapacity() - service2);
        //                                                        break;
        //                                                    }

                                                    //选取第一个能满足业务的光通道来Grooming。业务不能拆分
                                                    if (lp.getRemCapacity()>=service){
                                                        lp.setRemCapacity(lp.getRemCapacity()-service);
                                                        v.setRemCapacity(0);
                                                        break;
                                                    }
                                                }

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
                            Quasi_CWDM6.buildShortestLightpath(layer, gv, BPSK, QPSK, eightQAM, existNodeList, service, nodepair, srcNode, desNode, lightpath, vlink, dijkstra, judge);
                        }
                    }
                }
            }
        }
        for (int service:serviceList2) {
            Loop: for (Nodepair nodepair:nodepairlist.values()) {
                Link LINK=layer.findLink(nodepair.getSrcNode(),nodepair.getDesNode());
                if (LINK==null){
                    for (int a:nodepair.getServiceList().keySet()) {

                        if (nodepair.getServiceList().get(a)==service){     //遍历节点对之间的业务，当节点对之间的业务需求等于业务集合当前值时，继续执行

                            nodepair.getServiceList().remove(a);
                            nodepair.setDemand(service);        //令节点对之间的业务需求等于当前的service
                            Node srcNode = nodepair.getSrcNode();
                            Node desNode = nodepair.getDesNode();

                            String LP_name = srcNode.getName() + "-" + desNode.getName();
                            Lightpath lightpath = new Lightpath(LP_name);     //得到光路：源点到终点的光通道
                            lightpath.setSrcNode(srcNode);
                            lightpath.setDesNode(desNode);
                            lightpath.setDemand(service);

                            Vlink vlink= gv.findVlink(srcNode,desNode); //找到该节点对对应的IP层虚拟链路

                            Dijkstra dijkstra = new Dijkstra();

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
//                                ArrayList<Route>routeArrayList= dijkstra.findShortestRoute(nodepair,layer,10,gv);

                                    if (routeArrayList.size()==0){
//                                    System.out.println("途经链路不满足节点对需求！");
                                    }

                                    else {
                                        /*考虑IP层负载均衡，最大剩余容量*/
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
                                            routeArr.add(route.getHops());   //得到所有路由的剩余容量
                                        }
                                        Collections.sort(routeArr);                     //按从小到大排序,剩余容量少的优先考虑
//                                    routeArr.sort(Collections.reverseOrder());    //剩余容量多的优先考虑

                                        int routeHops=0;     //路由剩余容量
                                        if (routeArr.size()!=0){
                                            routeHops=routeArr.getFirst();    //排序后的第一个路由
                                        }

                                        for (Route route:routeArrayList){
                                            if (route.getHops()>3){
                                                break ;
                                            }

                                            //选择的路由应该是可以满足流量疏导需求且剩余容量最少的
//                                        if (route.getLength()==routeLength&&route.getVlinklist().size()!=0){

                                            // 当确定可以满足流量疏导时，更新光通道的剩余容量
                                            for (Vlink v:route.getVlinklist()) {
//                                                int service2=service;
//                                                v.setRemCapacity(v.getRemCapacity()-nodepair.getDemand());  //更新虚拟链路的剩余容量

                                                //FirstFit选择光路进行grooming
                                                for (Lightpath lp : v.getLightPathList()) {
//                                                    //当取出的光路不足以满足此业务需求时，将业务拆分依次放入光通道上
//                                                    if ((service2 - lp.getRemCapacity()) >= 0) {
//                                                        service2 = service2 - lp.getRemCapacity();
//                                                        lp.setRemCapacity(0);
//                                                    } else {
//                                                        lp.setRemCapacity(lp.getRemCapacity() - service2);
//                                                        break;
//                                                    }

                                                    //选取第一个能满足业务的光通道来Grooming。业务不能拆分
                                                    if (lp.getRemCapacity()>=service){
                                                        lp.setRemCapacity(lp.getRemCapacity()-service);
                                                        v.setRemCapacity(0);
                                                        break;
                                                    }
                                                }

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
                            Quasi_CWDM6.buildShortestLightpath(layer, gv, BPSK, QPSK, eightQAM, existNodeList, service, nodepair, srcNode, desNode, lightpath, vlink, dijkstra, judge);
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
