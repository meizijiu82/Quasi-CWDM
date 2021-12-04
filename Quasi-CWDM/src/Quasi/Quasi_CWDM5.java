//package Quasi;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.PrintStream;
//import java.util.*;
//
//import javafx.scene.effect.Light;
//import network.*;
//import routeSearch.*;
//import general.*;
//
//public class Quasi_CWDM5 {
//
//    public void Quasi_CWDM() throws IOException {
//        System.out.println("*************** readTopology **************");
//        Layer layer=new Layer("Mylayer",0);
//        layer.readTopology("6.csv");
//        layer.generateNodepairs();
//        LinkedHashMap<String ,Nodepair>nodepairlist= layer.getNodepairlist();   //得到节点对列表
//        Iterator <String>iter=nodepairlist.keySet().iterator(); //节点对集合迭代
//
//
//        /*对节点对之间的流量需求按从大到小的顺序进行排序*/
//        LinkedList<Integer>servicelist=new LinkedList<>();
//
//
//        for (Nodepair nodepair:nodepairlist.values()){
//            for (int k:nodepair.getServiceList().keySet()) {
//                servicelist.add(nodepair.getServiceList().get(k));
//            }
//        }
//        servicelist.sort(Collections.reverseOrder()); //调用sort方法与reverseOrder方法对集合进行从大到小排序
//
//
//        for (Nodepair nodepair:nodepairlist.values()) {
//            System.out.println(nodepair.getName()+" "+nodepair.getServiceList().values());
////            System.out.println(nodepair.getName());
//        }
//
//
//        HashMap<String,Node>map=layer.getNodelist();    //map为层中节点集合
//        Gv gv;     //引用虚拟拓扑Gv类
//        Gv gv1;
//        Gv gv2;
//
//
//        /*输出物理链路集合*/
////        HashMap<String,Node>map2=layer.getNodelist();
////        Iterator iter2=map.keySet().iterator();
////        System.out.println("set E:=");
////        while (iter2.hasNext()){
////            Node node= map2.get(iter2.next());
////            int i=0;
////            int size=node.getNeinodelist().size();
////            System.out.print("("+node.getName()+",*) ");
////            while (i<size){
////                System.out.print(node.getNeinodelist().get(i).getName()+" ");
////                i++;
////            }
////            System.out.println("");
////        }
//
//        /*调制格式*/
//        ModuFormat BPSK=new ModuFormat("BPSK",175);
//        ModuFormat QPSK=new ModuFormat("QPSK",350);
//        ModuFormat eightQAM=new ModuFormat("8-QAM",525);
//
//
//        Dijkstra dijkstra=new Dijkstra();
//
//        gv=this.trafficGrooming(nodepairlist,layer,servicelist);    //流量疏导方法: 节点对集合，层
//        double Cost=Integer.MAX_VALUE;
//        while (gv.getTotal_Cost()<Cost){
//            Cost= gv.getTotal_Cost();
//            gv=this.replaceChannels(layer,gv,map,nodepairlist,Cost,servicelist);        //当变换调制格式之后成本升高，应返回之前的拓扑
//        }
//
//        int totalHops=0;
//        for (Lightpath lightpath: gv.getLpLinklist()) {
//            if (lightpath.getModuFormat()==null) continue;
////            System.out.println("lightpath:"+lightpath.getName()+"  moduFormat:"+lightpath.getModuFormat().getName()+"  channels:"+lightpath.getChannels());
//            totalHops+= lightpath.getHops();
//        }
//        float avaHops=(float) totalHops/gv.getLpLinklist().size();
//
//        File f=new File("src/Quasi/6_Node.txt");
//        f.createNewFile();
//        FileOutputStream fileOutputStream = new FileOutputStream(f,true);
//        PrintStream printStream = new PrintStream(fileOutputStream);
//        System.setOut(printStream);
//
//        System.out.println();
//        int sum=Constant.lowBound+Constant.bound-1;
//        System.out.println("CLLB  业务大小"+sum+"G"+"   seed:"+Constant.seed);
//        System.out.print("最终拓扑：平均跳数："+avaHops);
//        System.out.print("   总成本："+gv.getTotal_Cost());
//        System.out.println("   光路数："+gv.getLpLinklist().size());
//
//        for (Nodepair nodepair:nodepairlist.values()) {
//            Node srcNode = nodepair.getSrcNode();
//            Node desNode = nodepair.getDesNode();
//            Link link = layer.findLink(srcNode, desNode);
//            if(link!=null){
//                System.out.print("link name:"+link.getName()+"  Capacity:"+link.getCapacity()+"  ");
//                System.out.println("remCapacity:"+link.getRemCapacity()+"  channel:"+link.getChannels());
//            }
//        }
//
//        gv.setRemCapacity(0);
//        gv.setCapacity(0);
//        for (Nodepair nodepair:nodepairlist.values()) {
//            Node srcNode=nodepair.getSrcNode();
//            Node desNode=nodepair.getDesNode();
//            Link link= layer.findLink(srcNode,desNode);
//            float s=0;
//            if (link!=null){
//                gv.setCapacity(gv.getCapacity()+ link.getCapacity());
//                gv.setRemCapacity(gv.getRemCapacity()+ link.getRemCapacity());
////                System.out.print("link name:"+link.getName()+"  Capacity:"+link.getCapacity()+"  ");
////                System.out.print("remCapacity:"+link.getRemCapacity()+"  channel:"+link.getChannels());
////                System.out.print("  === 链路占用波长数为: ");
////                System.out.println(link.getWavelengthList().size()+"  ===");
//                System.out.println(link.getWavelengthList().size());
//                s = 1 - (float) link.getRemCapacity()/link.getCapacity();
//                link.setSpectrumEfficiency(s);
//            }
//        }
//
//        double aveSpeUtiRate=0;
//        for (Nodepair nodepair:nodepairlist.values()) {
//            Link link= layer.findLink(nodepair.getSrcNode(), nodepair.getDesNode());
//            if (link!=null){
////                System.out.println(link.getName()+"  "+link.getSpectrumEfficiency());
//                System.out.println(link.getSpectrumEfficiency());
//                aveSpeUtiRate+=link.getSpectrumEfficiency();
//            }
//        }
//
//        System.out.println("链路平均资源利用率："+aveSpeUtiRate/9);
//        System.out.println("Gv的资源利用率为："+(1-(float)gv.getRemCapacity()/gv.getCapacity()));
//
//
//        fileOutputStream.close();
//        printStream.close();
//
//    }   //Quasi-CWDM方法
//
//
//    public Gv replaceChannels(Layer layer,Gv gv,HashMap<String,Node>map, LinkedHashMap<String,Nodepair>nodepairlist,double Total_Cost,LinkedList<Integer>serviceList){
//        double reTotal_Cost=0;
//        Gv newGv=new Gv();
//        Dijkstra dijkstra=new Dijkstra();
//        /*调制格式*/
//        ModuFormat BPSK=new ModuFormat("BPSK",175);
//        ModuFormat QPSK=new ModuFormat("QPSK",350);
//        ModuFormat eightQAM=new ModuFormat("8-QAM",525);
//        /*
//         * 改变光路调制格式后，给该光路重新配置属性（IP端口，信号再生器，光路链路容量），之后grooming按新光路的属性判断是否满足条件
//         * 仅需要改变光路的属性，链路属性在reOptimization里进行更新
//         */
//
//        /*  用 QPSK 代替  */
//        for (int i=0;i< gv.getLpLinklist().size();i++) {
//            Lightpath lightpath = gv.getLpLinklist().get(i);
//            if (lightpath.getModuFormat()==null) continue;
//            if (lightpath.getModuFormat().getName().equals("QPSK")||lightpath.getModuFormat().getName().equals("8-QAM")) {   //当光路调制格式为最高调制格式8-QAM时，无法再调高光路调制格式，跳过这次循环
//                continue;
//            }
//            System.out.println("****" + lightpath.getName() + "  " + lightpath.getModuFormat().getName() + "****");
//
//            Node srcNode= lightpath.getSrcNode();
//            Node desNode= lightpath.getDesNode();
//
//
//
//
//            String srcName=srcNode.getName();
//            String desName=desNode.getName();
//            int distance= lightpath.getDistance();
//
//            if (lightpath.getModuFormat().getName().equals("BPSK")){
//                lightpath.setNipB(0);
//                for (int k=0;k<7;k++){
//                    if (lightpath.getDemand()<QPSK.getCapacity()*k){
//                        lightpath.setChannels(k);
//                        lightpath.setNipQ(2* lightpath.getChannels());
//                        break;
//                    }
//                }
//                lightpath.setNregenB(0);
//                lightpath.setModuFormat(QPSK);
//            }
//            if (distance>2000) lightpath.setNregenQ(lightpath.getChannels());
//
//            newGv=this.reOptimization(nodepairlist,layer,gv,serviceList);    //运行再优化算法得到优化后的总成本
//            if (newGv==gv){
////                System.out.println("优化之后无法满足所有节点对之间的流量需求！");
////                System.out.println("当前拓扑的总成本为："+Total_Cost);
//                continue;
//            }
//            reTotal_Cost=newGv.getNipB()*2+newGv.getNipQ()*2.6+newGv.getNipM()*3+newGv.getNregenB()+newGv.getNregenQ()*1.3+newGv.getNregenM()*1.5;
//            newGv.setTotal_Cost(reTotal_Cost);
//            if (reTotal_Cost>=Total_Cost){
//                lightpath.setModuFormat(BPSK);
//                lightpath.setNipQ(0);
//                lightpath.setNregenQ(0);
//                for (int k=0;k<8;k++){
//                    if (lightpath.getDemand()<BPSK.getCapacity()*k){
//                        lightpath.setChannels(k);
//                        lightpath.setNipB(2* lightpath.getChannels());
//                        break;
//                    }
//                }
//                if (distance>=4000) lightpath.setNregenB(lightpath.getChannels());
//
//                System.out.println("优化之前的成本："+Total_Cost+"  优化之后的成本："+reTotal_Cost);
//                continue;
//            }
//
//            System.out.println("优化之后的总成本："+reTotal_Cost+"  被阻塞业务总数:"+newGv.getBlockServiceSum()+"  光路数:"+newGv.getLpLinklist().size());
//            Total_Cost=reTotal_Cost;
//            System.out.println();
//
//        }
//
//        /*  用 8-QAM 代替  */
//        for (Lightpath lightpath: gv.getLpLinklist()) {
//
//            if (lightpath.getModuFormat()==null) continue;
//            if (lightpath.getModuFormat().getName().equals("8-QAM")){   //当光路调制格式为最高调制格式8-QAM时，无法再调高光路调制格式，跳过这次循环
//                continue;
//            }
//            System.out.println("****"+lightpath.getName()+"  "+lightpath.getModuFormat().getName()+"****");
//
//            Node srcNode= lightpath.getSrcNode();
//            Node desNode= lightpath.getDesNode();
//
//            String Moduname=null;
//            String srcName=srcNode.getName();
//            String desName=desNode.getName();
//            int distance= lightpath.getDistance();
//            /*改变调制格式后，更新光路的IP端口和信号再生器类型*/
//            if (lightpath.getModuFormat().getCapacity()<eightQAM.getCapacity()){
//
//                if (lightpath.getModuFormat().getName().equals("BPSK")){
//                    Moduname="BPSK";
//                    for (int k=0;k<3;k++){
//                        if (lightpath.getDemand()<eightQAM.getCapacity()*k){
//                            lightpath.setChannels(k);
//                            lightpath.setNipM(2* lightpath.getChannels());
//                            break;
//                        }
//                    }
//                    lightpath.setNipB(0);
//                    lightpath.setNregenB(0);
//                }
//                else if (lightpath.getModuFormat().getName().equals("QPSK")){
//                    Moduname="QPSK";
//                    for (int k=0;k<3;k++){
//                        if (lightpath.getDemand()<eightQAM.getCapacity()*k){
//                            lightpath.setChannels(k);
//                            lightpath.setNipM(2* lightpath.getChannels());
//                            break;
//                        }
//                    }
//                    lightpath.setNipQ(0);
//                    lightpath.setNregenQ(0);
//                }
//
//                for (int i=1;i<5;i++){
//                    if (distance>1000*i){
//                        lightpath.setNregenM(lightpath.getChannels()*i);
//                    }
//                }
//                lightpath.setModuFormat(eightQAM);
//            }
//
//            newGv=this.reOptimization(nodepairlist,layer,gv,serviceList);    //运行再优化算法得到优化后的总成本
//
//            if (newGv==gv){
////                System.out.println("优化之后无法满足所有节点对之间的流量需求！");
////                System.out.println("当前拓扑的总成本为："+Total_Cost);
//                continue;
//            }
//            reTotal_Cost=newGv.getNipB()*2+newGv.getNipQ()*2.6+newGv.getNipM()*3+newGv.getNregenB()+newGv.getNregenQ()*1.3+newGv.getNregenM()*1.5;
//            newGv.setTotal_Cost(reTotal_Cost);
//            if (reTotal_Cost>=Total_Cost){
//                newGv.setTotal_Cost(Total_Cost);
//                if (Moduname!=null){
//                    switch (Moduname){
//                        case "BPSK":
//                            lightpath.setModuFormat(BPSK);
//                            for (int k=0;k<8;k++){
//                                if (lightpath.getDemand()<BPSK.getCapacity()*k){
//                                    lightpath.setChannels(k);
//                                    lightpath.setNipB(2* lightpath.getChannels());
//                                    break;
//                                }
//                            }
//                            lightpath.setNipM(0);
//                            lightpath.setNregenM(0);
//                            if (distance>=4000){
//                                lightpath.setNregenB( lightpath.getChannels());
//                            }
//                            break;
//                        case "QPSK":
//                            lightpath.setModuFormat(QPSK);
//                            for (int k=0;k<8;k++){
//                                if (lightpath.getDemand()<QPSK.getCapacity()*k){
//                                    lightpath.setChannels(k);
//                                    lightpath.setNipQ(2* lightpath.getChannels());
//                                    break;
//                                }
//                            }
//                            lightpath.setNipM(0);
//                            lightpath.setNregenM(0);
//                            if (distance>2000){
//                                lightpath.setNregenQ(lightpath.getChannels());
//                            }
//                            break;
//                    }
//                }
//
//                System.out.println("优化之前成本为:"+Total_Cost+" ========== 优化之后成本为："+reTotal_Cost+"==============");
//                continue;
//            }
//            System.out.println("优化之后的总成本："+reTotal_Cost+"  被阻塞业务总数："+newGv.getBlockServiceSum()+"  光路数："+newGv.getLpLinklist().size());
//            Total_Cost=reTotal_Cost;
//            System.out.println();
//
//        }
//
//        //所有光路遍历完全之后，得到的新拓扑与之前的拓扑进行成本对比，返回成本更低的那个
//        if (newGv.getTotal_Cost()>=gv.getTotal_Cost()){
//            return gv;
//        }
//        else return newGv;
//
//    }
//
//    public Gv trafficGrooming(LinkedHashMap<String,Nodepair> nodepairlist,Layer layer,LinkedList<Integer>serviceList){
//        Gv gv=new Gv();     //声明虚拟拓扑，用来添加光路
//        Iterator <String>iter=nodepairlist.keySet().iterator(); //节点对集合迭代
//
//        ModuFormat BPSK=new ModuFormat("BPSK",175);
//        ModuFormat QPSK=new ModuFormat("QPSK",350);
//        ModuFormat eightQAM=new ModuFormat("8-QAM",525);
//        Wavelength wavelength=new Wavelength();
//
//        double Total_Cost;
//        HashSet<Node>existNodeList=new HashSet<>(); //已有节点集合，在流量疏导的时候判断已有光路是否可以连接给定节点对
//        HashSet<Link>exitLinkList=new HashSet<>();  //用HashSet元素不会重复的特性，访问已存在链路集合
//        LinkedList<Vlink>existVlinkList=new LinkedList<>();
////        HashSet<Lightpath>existVlinkList=new HashSet<>();
//
//        int serial=0;
//        int judgeNum=serviceList.size()/3;
//
//        for (int service:serviceList) {
//            serial++;
//            Loop: for (Nodepair nodepair:nodepairlist.values()) {
//
//                for (int a:nodepair.getServiceList().keySet()) {
//
//                    if (nodepair.getServiceList().get(a)==service){     //遍历节点对之间的业务，当节点对之间的业务需求等于业务集合当前值时，继续执行
//
//                        nodepair.getServiceList().remove(a);
//                        nodepair.setDemand(service);        //令节点对之间的业务需求等于当前的service
//                        Node srcNode = nodepair.getSrcNode();
//                        Node desNode = nodepair.getDesNode();
//
//                        String LP_name = srcNode.getName() + "-" + desNode.getName();
//                        Lightpath lightpath = new Lightpath(LP_name);     //得到光路：源点到终点的光通道
//                        lightpath.setSrcNode(srcNode);
//                        lightpath.setDesNode(desNode);
//                        lightpath.setDemand(service);
//                        Vlink vlink=new Vlink(LP_name,srcNode,desNode,service); //IP层的虚拟链路
//
//
//                        Dijkstra dijkstra = new Dijkstra();
//
//                        System.out.println(nodepair.getName()+" demand:"+nodepair.getDemand());
//
//                        //尝试使用Gv的剩余容量来满足节点对之间的流量需求
//                        int judge = 0;
//                        int judge1=0;
//
//                        /*当拓扑剩余容量大于节点对之间的需求时且已存在节点集合包含此节点对的源点、终点时，判断拓扑是否能满足该节点对之间的需求*/
//                        if (gv.getRemCapacity() > nodepair.getDemand()&&existNodeList.contains(desNode) && existNodeList.contains(srcNode)) {
//                            boolean success=false;
//
//                            Link chargelink= layer.findLink(srcNode,desNode);
//                            /*源点到终点之间有多条链路 或 直连链路无法满足Grooming*/
//                            if (!success){
//                                //运行改进后的最短路由算法，从exitlinklist中找到一条满足节点对之间路由的光路vialinklist
//
////                                ArrayList<Route>routeArrayList= dijkstra.findGroomingRouteList(nodepair,layer,10,existVlinkList,gv);
//                                ArrayList<Route>routeArrayList= dijkstra.findShortestRoute(nodepair,layer,10,existVlinkList,gv);
//
//                                if (routeArrayList.size()==0){
////                                    System.out.println("途经链路不满足节点对需求！");
//                                }
//
//                                else {
//                                    for (Route route:routeArrayList){
//                                        L:for (Vlink v:route.getVlinklist()) {
//                                            for (Link l:v.getLinks()) {
//                                                if (l.getRemCapacity()<nodepair.getDemand()){
//                                                    route.getVlinklist().clear();
//                                                }
//                                                break L;
//                                            }
//                                        }
//                                    }
//
//
//                                    //考虑IP层负载均衡，最大剩余容量
//                                    LinkedList<Integer>arr=new LinkedList<>();
//                                    for (Route route:routeArrayList){
//                                        if (route.getVlinklist().size()!=0){
//                                            for (Vlink v: route.getVlinklist()) {
//                                                arr.add(v.getRemCapacity());    //路由中所有虚拟链路的剩余容量集合
//                                            }
//                                            Collections.sort(arr);                      //从小到大排序
//                                            route.setRemCapacity(arr.getFirst());       //选择最小的剩余容量作为路由的剩余容量
//                                        }
//                                    }
//
//                                    LinkedList<Integer>arr1=new LinkedList<>();
//                                    for (Route route:routeArrayList) {
//                                        arr1.add(route.getRemCapacity());
//                                    }
//                                    arr1.sort(Collections.reverseOrder());
//
//                                    int remCapacity=0;
//                                    if (arr1.size()!=0){
//                                        remCapacity=arr1.getLast();    //选择剩余资源最小的路由
////                                        remCapacity=arr1.getFirst();  //选择剩余资源最大的路由
//                                    }
//
//                                    boolean satisfyGrooming=true;
//                                    for (Route route:routeArrayList){
//                                        if (route.getVlinklist().size()!=0){
//                                            if (route.getRemCapacity()==remCapacity){
//                                                for (Vlink v: route.getVlinklist()) {
//                                                    //虚拟链路映射到光层上更新资源并更新虚拟链路容量
//                                                    for (Link l: v.getLinks()) {
//                                                        System.out.print("流量疏导之前的链路");
//                                                        System.out.print("  " + l.getName() + "  remCapacity:" + l.getRemCapacity() + " ");
//                                                        l.setRemCapacity(l.getRemCapacity()-nodepair.getDemand());
//                                                        System.out.print("之后的链路");
//                                                        System.out.print("  " + l.getName() + "  remCapacity:" + l.getRemCapacity() + " ");
//                                                        v.setRemCapacity(v.getRemCapacity()-nodepair.getDemand());  //更新虚拟链路的容量
//                                                    }
//                                                }
//
//                                                for (Vlink v:route.getVlinklist()) {
//                                                    for (Link l:v.getLinks()) {
//                                                        if (l.getRemCapacity() < 0) {
//                                                            satisfyGrooming = false;
//                                                            break ;
//                                                        }
//                                                    }
//                                                }
//                                                if (satisfyGrooming){
//                                                    System.out.println("虚拟拓扑满足流量疏导");
//                                                    judge=1;
//                                                }
//                                                else {
//                                                    for (Vlink v: route.getVlinklist()) {
//                                                        //还原之前的链路容量并更新虚拟链路容量
//                                                        for (Link l: v.getLinks()) {
//                                                            l.setRemCapacity(l.getRemCapacity()+nodepair.getDemand());
//                                                            v.setRemCapacity(v.getRemCapacity()+nodepair.getDemand());  //更新虚拟链路的容量
//                                                        }
//                                                    }
//                                                }
//                                                break ;
//                                            }
//                                        }
//
//                                    }
//
//                                }
//
//                            }
//
//                        }
//
//                        //若不成立，建立一条合适调制格式的光通道来满足节点对上的流量需求
//                        if (judge==0){
//
//                            System.out.print("建立新的光通道    ");
//                            gv.getLpLinklist().add(lightpath);
//
//
//                            existNodeList.add(srcNode);
//                            existNodeList.add(desNode);
//                            HashMap<String,Node>map= layer.getNodelist();
//
////                            dijkstra.dijkstra(srcNode,desNode,layer,lightpath);     //运行最短路由算法，找到节点对之间的最短路径,并设置各个节点的父节点
//                            dijkstra.findMinCostRoute(srcNode,desNode,layer,lightpath);
//
//                            lightpath.setDistance(desNode.getLength_from_src());
//
//                            Wavelength wave=new Wavelength();
//                            /*按节点对之间的距离建立光通道*/
//                            if (desNode.getLength_from_src()>=2000){
//                                for (int i=1;i<10;i++){
//                                    if (nodepair.getDemand()<BPSK.getCapacity()*i){
//
//                                        lightpath.setChannels(i);
//                                        lightpath.setModuFormat(BPSK);
//                                        gv.setRemCapacity(BPSK.getCapacity()*i-nodepair.getDemand()+ gv.getRemCapacity());
//                                        lightpath.setNipB(2*i);
//                                        Node currentNode1=desNode;
//                                        while (currentNode1!=srcNode){
//                                            Link link=layer.findLink(currentNode1,currentNode1.getParentNode());    //找到光路途经链路
//                                            lightpath.getLplink().add(link);
//                                            exitLinkList.add(link);
//                                            link.setChannels(link.getChannels()+i);
//                                            System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
//                                            link.setCapacity(link.getCapacity()+ BPSK.getCapacity()*i);
//                                            System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
//                                            link.setRemCapacity(link.getRemCapacity()+BPSK.getCapacity()*i-nodepair.getDemand());
//                                            System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
//
//                                            lightpath.setRemCapacity(lightpath.getRemCapacity()+link.getRemCapacity());
//
//                                            currentNode1=currentNode1.getParentNode();
//                                        }
//                                        System.out.println();
//                                        //波长分配
//                                        dijkstra.AssignWavelength(lightpath);
//
//                                        int maxWaveNum=0;
//                                        for (Link l:lightpath.getLplink()) {
//                                            if (l.getChannels()>maxWaveNum){
//                                                maxWaveNum=l.getChannels();
//                                            }
//                                        }
//                                        vlink.setMaxWaveNum(maxWaveNum);    //最大波长数
//                                        vlink.setHop(lightpath.getHops());
//                                        vlink.setLength(desNode.getLength_from_src());
//                                        vlink.setLinks(lightpath.getLplink());
//
//                                        if (desNode.getLength_from_src()>=4000) lightpath.setNipB(i);
//                                        break;
//                                    }
//                                }
//                            }
//                            else if(desNode.getLength_from_src()>=1000){
//                                for (int i=1;i<5;i++){
//                                    if (nodepair.getDemand()<QPSK.getCapacity()*i){
//
//                                        lightpath.setChannels(i);
//                                        lightpath.setModuFormat(QPSK);
//                                        gv.setRemCapacity(QPSK.getCapacity()*i-nodepair.getDemand()+ gv.getRemCapacity());
//                                        lightpath.setNipQ(2*i);
//                                        Node currentNode1=desNode;
//                                        while (currentNode1!=srcNode){
//                                            Link link=layer.findLink(currentNode1,currentNode1.getParentNode());
//                                            lightpath.getLplink().add(link);
//                                            exitLinkList.add(link);
//                                            link.setChannels(link.getChannels()+i);
//                                            System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
//                                            link.setCapacity(link.getCapacity()+ QPSK.getCapacity()*i);
//                                            System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
//                                            link.setRemCapacity(link.getRemCapacity()+QPSK.getCapacity()*i-nodepair.getDemand());
//                                            System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
//
//                                            lightpath.setRemCapacity(lightpath.getRemCapacity()+link.getRemCapacity());
//
//                                            currentNode1=currentNode1.getParentNode();
//                                        }
//                                        System.out.println();
//                                        //波长分配
//                                        dijkstra.AssignWavelength(lightpath);
//
//                                        int maxWaveNum=0;
//                                        for (Link l:lightpath.getLplink()) {
//                                            if (l.getChannels()>maxWaveNum){
//                                                maxWaveNum=l.getChannels();
//                                            }
//                                        }
//                                        vlink.setMaxWaveNum(maxWaveNum);    //最大波长数
//                                        vlink.setHop(lightpath.getHops());
//                                        vlink.setLength(desNode.getLength_from_src());
//                                        vlink.setLinks(lightpath.getLplink());
//
//                                        if (desNode.getLength_from_src()>=2000) lightpath.setNregenQ(i);
//                                        break;
//                                    }
//                                }
//                            }
//                            else if (desNode.getLength_from_src()<1000){
//                                for (int i=1;i<3;i++){
//                                    /*节点对之间的距离小于1000时，先用QPSK尝试建立连接，若失败，再用8-QAM建立*/
//                                    if (nodepair.getDemand()<350){
//
//                                        lightpath.setChannels(i);
//                                        lightpath.setModuFormat(QPSK);
//                                        gv.setRemCapacity(QPSK.getCapacity()*i-nodepair.getDemand()+ gv.getRemCapacity());
//                                        lightpath.setNipQ(2*i);
//                                        Node currentNode1=desNode;
//                                        while (currentNode1!=srcNode){
//                                            Link link=layer.findLink(currentNode1,currentNode1.getParentNode());
//                                            lightpath.getLplink().add(link);
//                                            exitLinkList.add(link);
//                                            link.setChannels(link.getChannels()+i);
//                                            System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
//                                            link.setCapacity(link.getCapacity()+ QPSK.getCapacity()*i);
//                                            System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
//                                            link.setRemCapacity(link.getRemCapacity()+QPSK.getCapacity()*i-nodepair.getDemand());
//                                            System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
//
//                                            lightpath.setRemCapacity(lightpath.getRemCapacity()+link.getRemCapacity());
//
//                                            currentNode1=currentNode1.getParentNode();
//                                        }
//                                        System.out.println();
//                                        //波长分配
//                                        dijkstra.AssignWavelength(lightpath);
//
//                                        int maxWaveNum=0;
//                                        for (Link l:lightpath.getLplink()) {
//                                            if (l.getChannels()>maxWaveNum){
//                                                maxWaveNum=l.getChannels();
//                                            }
//                                        }
//                                        vlink.setMaxWaveNum(maxWaveNum);    //最大波长数
//                                        vlink.setHop(lightpath.getHops());
//                                        vlink.setLength(desNode.getLength_from_src());
//                                        vlink.setLinks(lightpath.getLplink());
//
//                                        if (desNode.getLength_from_src()>=1000&&desNode.getLength_from_src()<2000) lightpath.setNregenQ(i);
//                                        else if (desNode.getLength_from_src()>=2000) lightpath.setNregenQ(2*i);
//                                        break;
//                                    }
//                                    else if (nodepair.getDemand()<eightQAM.getCapacity()*i){
//
//                                        lightpath.setChannels(i);
//                                        lightpath.setModuFormat(eightQAM);
//                                        gv.setRemCapacity(eightQAM.getCapacity()*i-nodepair.getDemand()+ gv.getRemCapacity());
//                                        lightpath.setNipM(2*i);
//                                        Node currentNode1=desNode;
//                                        while (currentNode1!=srcNode){
//                                            Link link=layer.findLink(currentNode1,currentNode1.getParentNode());
//                                            lightpath.getLplink().add(link);
//                                            exitLinkList.add(link);
//                                            link.setChannels(link.getChannels()+i);
//                                            System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
//                                            link.setCapacity(link.getCapacity()+ eightQAM.getCapacity()*i);
//                                            System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
//                                            link.setRemCapacity(link.getRemCapacity()+eightQAM.getCapacity()*i-nodepair.getDemand());
//                                            System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
//                                            currentNode1=currentNode1.getParentNode();
//                                        }
//                                        System.out.println();
//                                        //波长分配
//                                        dijkstra.AssignWavelength(lightpath);
//
//                                        int maxWaveNum=0;
//                                        for (Link l:lightpath.getLplink()) {
//                                            if (l.getChannels()>maxWaveNum){
//                                                maxWaveNum=l.getChannels();
//                                            }
//                                        }
//                                        vlink.setMaxWaveNum(maxWaveNum);    //最大波长数
//                                        vlink.setHop(lightpath.getHops());
//                                        vlink.setLength(desNode.getLength_from_src());
//                                        vlink.setLinks(lightpath.getLplink());
//
//                                        if (desNode.getLength_from_src()>=1000&&desNode.getLength_from_src()<2000) lightpath.setNregenM(i);
//                                        else if (desNode.getLength_from_src()>=2000) lightpath.setNregenM(2*i);
//                                        break;
//                                    }
//                                }
//                            }
//
//                            vlink.setRemCapacity(vlink.getRemCapacity()+lightpath.getRemCapacity());
//                            existVlinkList.add(vlink);
//                        }
//                        if (lightpath.getModuFormat()!=null){
//                            System.out.print(lightpath.getName()+" moduformat："+lightpath.getModuFormat().getName()+"  光路上通道数：："+lightpath.getChannels()+"  link:");
//                        }
//                        for (Link link:lightpath.getLplink()) {
//                            System.out.print("  "+link.getName()+"  remCapacity:"+link.getRemCapacity()+"   ");
//                        }
//                        System.out.println();
//
//                        break Loop;
//                    }
//                }
//            }
//        }
//
//
//        //根据拓扑中光路的IP端口数和信号再生器数得到拓扑总的IP端口数和信号再生器数
//        this.addLightPath(gv);
//
//        System.out.println("拓扑中所有光路： ");
//        for (Lightpath lightpath: gv.getLpLinklist()) {
//            if (lightpath.getModuFormat()==null) continue;
//            System.out.print(lightpath.getName()+"  "+lightpath.getModuFormat().getName()+"  "+lightpath.getChannels()+"  "+lightpath.getDistance());
//            System.out.println("  "+lightpath.getDemand());
//        }
//
//        for (Nodepair nodepair:nodepairlist.values()) {
//            Node srcNode=nodepair.getSrcNode();
//            Node desNode=nodepair.getDesNode();
//            Link link= layer.findLink(srcNode,desNode);
//            if (link!=null){
//                System.out.print("link name:"+link.getName()+"  Capacity:"+link.getCapacity()+"  ");
//                System.out.println("remCapacity:"+link.getRemCapacity()+"  channel:"+link.getChannels());
//            }
//        }
//        int sum=0;
//        for (Nodepair nodepair:nodepairlist.values()) {
//            sum+=nodepair.getBlockService();
//        }
//        gv.setBlockServiceSum(sum);  //拓扑被阻塞业务总数
//
//        Total_Cost=gv.getNipB()*2+gv.getNipQ()*2.6+gv.getNipM()*3+gv.getNregenB()+gv.getNregenQ()*1.3+gv.getNregenM()*1.5;
//
//        gv.setTotal_Cost(Total_Cost);
//        System.out.println("硬件总成本为："+gv.getTotal_Cost()+"  被阻塞业务总数:"+gv.getBlockServiceSum()+"  光路数:"+gv.getLpLinklist().size());
//
//        return gv;
//    }
//
//    public Gv reOptimization(LinkedHashMap<String,Nodepair> nodepairlist,Layer layer,Gv gv,LinkedList<Integer>serviceList) {
//
//        Random random=new Random(Constant.seed);
//        for (Nodepair nodepair:nodepairlist.values()) {
//            for (int i=0;i<nodepair.getServiceSum();i++){
//                int serviceDemand=Constant.lowBound+ random.nextInt(Constant.bound);
//                nodepair.getServiceList().put(i,serviceDemand);     //创建节点对之间的业务集合
//            }
//        }
//
//        Gv newGv=new Gv();
//        double cost;
//        HashSet<Link>linklist=new HashSet<>();
//
//        /*调制格式*/
//        ModuFormat BPSK=new ModuFormat("BPSK",175);
//        ModuFormat QPSK=new ModuFormat("QPSK",350);
//        ModuFormat eightQAM=new ModuFormat("8-QAM",525);
//
//        System.out.println("****************新拓扑***************");
//
//
//        for (Lightpath lightpath: gv.getLpLinklist()) {
//            lightpath.setStatus(0);
//        }
//
//        //初始化链路属性
//        for (Nodepair nodepair: nodepairlist.values()) {
//            Node srcNode=nodepair.getSrcNode();
//            Node desNode=nodepair.getDesNode();
//            nodepair.setBlockService(0);
//            Link link= layer.findLink(srcNode,desNode);
//            if (link!=null) {
//                link.setCost(0.001);
//                link.setRemCapacity(0);
//                link.setCapacity(0);
//                link.setChannels(0);
//                link.getWavelengthList().clear();
//            }
//        }
//
//        /*在原有拓扑的虚拟链路剩余容量上进行流量疏导，并得到新的拓扑*/
//        HashSet<Node>existNodeList=new HashSet<>();
//        LinkedList<Vlink>existVlinkList=new LinkedList<>();
//
//        Dijkstra dijkstra=new Dijkstra();
//        Wavelength wavelength=new Wavelength();
//        boolean satisfy=true;
//
//        for (int service:serviceList){  //遍历业务集合
//            int judge=0;
//            Loop:for (Nodepair nodepair:nodepairlist.values()) {    //遍历节点对集合
//
//                for (int s:nodepair.getServiceList().keySet()) {    //遍历节点对之间业务集合
//                    if (nodepair.getServiceList().get(s)==service){
//
//                        nodepair.getServiceList().remove(s);    //在节点对的业务集合中删除准备建立的业务
//                        nodepair.setDemand(service);            //节点对设置当前流量需求
//                        Node srcNode=nodepair.getSrcNode();
//                        Node desNode=nodepair.getDesNode();
//                        String vlinkName=srcNode.getName()+"-"+desNode.getName();
//                        Vlink vlink=new Vlink(vlinkName,srcNode,desNode,service);
//
////                        System.out.println("nodepair:"+nodepair.getName()+"  demand:"+nodepair.getDemand());
//
//                        Link link= layer.findLink(srcNode,desNode);
//
//                        if (existNodeList.contains(srcNode)&& existNodeList.contains(desNode)){
//                            boolean success=false;
//
//                            /*源点到终点需要经过多条链路 或 直连链路无法满足Grooming*/
//                            if (!success){
//
//                                //找到k条满足grooming的路由
//                                ArrayList<Route>routeArrayList= dijkstra.findShortestRoute(nodepair,layer,10,existVlinkList,gv);
////                                ArrayList<Route>routeArrayList= dijkstra.findGroomingRouteList(nodepair,layer,10,existVlinkList,gv);
//                                if (routeArrayList.size()==0){
////                                   System.out.print("途经链路不满足节点对需求！  ");
//                                }
//                                else {
//                                    for (Route route:routeArrayList){
//                                        L: for (Vlink v:route.getVlinklist()) {
//                                            for (Link l:v.getLinks()) {
//                                                //当此路由上有一条链路的剩余容量小于节点对之间需求，则清空该路由上的链路，表示该路由无法满足grooming
//                                                if (l.getRemCapacity()<nodepair.getDemand()){
//                                                    route.getVlinklist().clear();
//                                                }
//                                                break L;
//                                            }
//                                        }
//                                    }
//
//                                    //考虑IP层负载均衡，最大剩余容量
//                                    LinkedList<Integer>arr=new LinkedList<>();
//                                    for (Route route:routeArrayList){
//                                        if (route.getVlinklist().size()!=0){
//                                            for (Vlink v: route.getVlinklist()) {
//                                                arr.add(v.getRemCapacity());    //路由中所有虚拟链路的剩余容量集合
//                                            }
//                                            Collections.sort(arr);                      //从小到大排序
//                                            route.setRemCapacity(arr.getFirst());       //选择最小的剩余容量作为路由的剩余容量
//                                        }
//                                    }
//
//                                    LinkedList<Integer>arr1=new LinkedList<>();
//                                    for (Route route:routeArrayList) {
//                                        arr1.add(route.getRemCapacity());
//                                    }
//                                    arr1.sort(Collections.reverseOrder());
//
//                                    int remCapacity=0;
//                                    if (arr1.size()!=0){
////                                        remCapacity=arr1.getFirst();    //选择剩余资源最大的路由
//                                        remCapacity=arr1.getLast();    //选择剩余资源最小的路由
//                                    }
//
//                                    boolean satisfyGrooming=true;
//                                    for (Route route:routeArrayList){
//                                        if (route.getVlinklist().size()!=0){
//                                            if (route.getRemCapacity()==remCapacity){
//
//                                                for (Vlink v: route.getVlinklist()) {
//                                                    //虚拟链路映射到光层上更新资源并更新虚拟链路容量
//                                                    for (Link l: v.getLinks()) {
////                                                    System.out.print("流量疏导之前的链路");
////                                                    System.out.print("  " + l.getName() + "  remCapacity:" + l.getRemCapacity() + " ");
//                                                        l.setRemCapacity(l.getRemCapacity()-nodepair.getDemand());
////                                                    System.out.print("之后的链路");
////                                                    System.out.print("  " + l.getName() + "  remCapacity:" + l.getRemCapacity() + " ");
//                                                        v.setRemCapacity(v.getRemCapacity()-nodepair.getDemand());  //更新虚拟链路的容量
//                                                    }
//                                                }
//
//                                                for (Vlink v:route.getVlinklist()) {
//                                                    for (Link l:v.getLinks()) {
//                                                        if (l.getRemCapacity() < 0) {
//                                                            satisfyGrooming = false;
//                                                            break ;
//                                                        }
//                                                    }
//                                                }
//                                                if (satisfyGrooming){
////                                                System.out.println("虚拟拓扑满足流量疏导");
//                                                    judge=1;
//                                                }
//                                                else {
////                                                System.out.println("虚拟拓扑不满足流量疏导");
//                                                    for (Vlink v: route.getVlinklist()) {
//                                                        //还原之前的链路容量并更新虚拟链路容量
//                                                        for (Link l: v.getLinks()) {
//                                                            l.setRemCapacity(l.getRemCapacity()+nodepair.getDemand());
//                                                            v.setRemCapacity(v.getRemCapacity()+nodepair.getDemand());  //更新虚拟链路的容量
//                                                        }
//                                                    }
//                                                }
//                                                break ;
//                                            }
//                                        }
//                                    }
//
//                                }
//
//                            }
//                        }
//
//                        //程序执行到这，表示无法对此业务进行grooming，启用拓扑中之前建立的光路以满足该业务需求
//                        if (judge==0){
//                            int a=0;
//                            for (Lightpath lightpath:gv.getLpLinklist()){       //遍历原本拓扑光路集合
//                                if (lightpath.getDemand()==service&&lightpath.getStatus()==0){
//                                    if (lightpath.getModuFormat()==null)  break;
//                                    lightpath.setStatus(1);
//
//                                    lightpath.setRemCapacity(0);    //初始化光路剩余容量
//
//                                    existNodeList.add(srcNode);
//                                    existNodeList.add(desNode);
//                                    HashMap<String,Node>map= layer.getNodelist();
//
//
//                                    //更新光路上的链路属性
//                                    for (Link link1:lightpath.getLplink()) {
////                                        System.out.println("启用原先拓扑上的光路");
//                                        link1.setChannels(link1.getChannels()+lightpath.getChannels());
//                                        link1.setCapacity(link1.getCapacity()+lightpath.getModuFormat().getCapacity()* lightpath.getChannels());
//                                        link1.setRemCapacity(link1.getRemCapacity()+lightpath.getModuFormat().getCapacity()* lightpath.getChannels());
////                                        System.out.print(link1.getName()+"之前的链路容量："+link1.getRemCapacity());
//                                        link1.setRemCapacity(link1.getRemCapacity()-lightpath.getDemand());
////                                        System.out.println("  "+link1.getName()+"之后的链路容量："+link1.getRemCapacity());
//
//                                        lightpath.setRemCapacity(lightpath.getRemCapacity()+link1.getRemCapacity());   //得到光路剩余容量
//                                        vlink.setLength(vlink.getLength()+link1.getLength());
//                                    }
//
//
//                                    dijkstra.AssignWavelength(lightpath);
//
//                                    int maxWaveNum=0;
//                                    for (Link l:lightpath.getLplink()) {
//                                        if (l.getChannels()>maxWaveNum){
//                                            maxWaveNum=l.getChannels();
//                                        }
//                                    }
//                                    vlink.setMaxWaveNum(maxWaveNum);    //最大波长数
//                                    vlink.setHop(lightpath.getHops());
//                                    vlink.setLength(desNode.getLength_from_src());
//                                    vlink.setLinks(lightpath.getLplink());
//
////                                   System.out.println("新拓扑添加光路:"+lightpath.getName()+"   channels:"+lightpath.getChannels()+
////                                           "  moduFormat:"+lightpath.getModuFormat().getName()+"   distance:"+lightpath.getDistance());
//
//                                    newGv.getLpLinklist().add(lightpath);
//                                    vlink.setRemCapacity(lightpath.getRemCapacity());
//                                    existVlinkList.add(vlink);
//
//                                    a=1;
//                                    break ;
//                                }
//                            }
//                            if (a==0){
//                                /*拓扑中不存在该光路 或 拓扑中的光路无法满足节点对之间业务需求，建立新光路*/
//                                HashMap<String,Node>map= layer.getNodelist();
//                                String name=srcNode.getName()+"-"+desNode.getName();
//                                Lightpath lightpath=new Lightpath(name);
//                                lightpath.setSrcNode(srcNode);
//                                lightpath.setDesNode(desNode);
//
//                                dijkstra.findMinCostRoute(srcNode,desNode,layer,lightpath);       //找到最小cost路由
//
//                                lightpath.setDemand(service);               //建立新光路，设置光路的业务需求
//
//
//                                /*以光路的距离为参考建立光通道*/
//                                if (desNode.getLength_from_src()>=2000){
//                                    for (int i=1;i<10;i++){
//                                        if (nodepair.getDemand()<BPSK.getCapacity()*i){
//                                            lightpath.setChannels(i);
//
//                                            lightpath.setModuFormat(BPSK);
//                                            gv.setRemCapacity(BPSK.getCapacity()*i-nodepair.getDemand()+ gv.getRemCapacity());
//                                            lightpath.setNipB(2*i);
//
//                                            int distance=0;
//                                            for (Link layerlink:lightpath.getLplink()) {
//                                                distance+=layerlink.getLength();
////                                                existLinkList.add(layerlink);
//                                                layerlink.setChannels(layerlink.getChannels()+i);
//                                                layerlink.setCapacity(layerlink.getCapacity()+ BPSK.getCapacity()*i);
//                                                layerlink.setRemCapacity(layerlink.getRemCapacity()+BPSK.getCapacity()*i-nodepair.getDemand());
//
//                                                lightpath.setRemCapacity(lightpath.getRemCapacity()+layerlink.getRemCapacity());
//                                            }
//                                            lightpath.setDistance(distance);    //设置光路距离
//                                            /*波长分配*/
//                                            dijkstra.AssignWavelength(lightpath);
//
//                                            int maxWaveNum=0;
//                                            for (Link l:lightpath.getLplink()) {
//                                                if (l.getChannels()>maxWaveNum){
//                                                    maxWaveNum=l.getChannels();
//                                                }
//                                            }
//                                            vlink.setMaxWaveNum(maxWaveNum);    //最大波长数
//                                            vlink.setHop(lightpath.getHops());
//                                            vlink.setLength(desNode.getLength_from_src());
//                                            vlink.setLinks(lightpath.getLplink());
//
//                                            if (desNode.getLength_from_src()>=4000) lightpath.setNipB(i);
//                                            break;
//                                        }
//                                    }
//                                }
//                                else if(desNode.getLength_from_src()>=1000){
//                                    for (int i=1;i<5;i++){
//                                        if (nodepair.getDemand()<QPSK.getCapacity()*i){
//                                            lightpath.setChannels(i);
//
//                                            lightpath.setModuFormat(QPSK);
//                                            gv.setRemCapacity(QPSK.getCapacity()*i-nodepair.getDemand()+ gv.getRemCapacity());
//                                            lightpath.setNipQ(2*i);
//
//                                            int distance=0;
//                                            for (Link layerlink:lightpath.getLplink()) {
//                                                distance+=layerlink.getLength();
////                                                existLinkList.add(layerlink);
//                                                layerlink.setChannels(layerlink.getChannels()+i);
//                                                layerlink.setCapacity(layerlink.getCapacity()+ QPSK.getCapacity()*i);
//                                                layerlink.setRemCapacity(layerlink.getRemCapacity()+QPSK.getCapacity()*i-nodepair.getDemand());
//                                                lightpath.setRemCapacity(lightpath.getRemCapacity()+layerlink.getRemCapacity());
//                                            }
//                                            lightpath.setDistance(distance);    //设置光路距离
//                                            /*波长分配*/
//                                            dijkstra.AssignWavelength(lightpath);
//
//                                            int maxWaveNum=0;
//                                            for (Link l:lightpath.getLplink()) {
//                                                if (l.getChannels()>maxWaveNum){
//                                                    maxWaveNum=l.getChannels();
//                                                }
//                                            }
//                                            vlink.setMaxWaveNum(maxWaveNum);    //最大波长数
//                                            vlink.setHop(lightpath.getHops());
//                                            vlink.setLength(desNode.getLength_from_src());
//                                            vlink.setLinks(lightpath.getLplink());
//
//                                            if (desNode.getLength_from_src()>=2000) lightpath.setNregenQ(i);
//                                            break;
//                                        }
//                                    }
//                                }
//                                else if (desNode.getLength_from_src()<1000){
//                                    for (int i=1;i<3;i++){
//                                        /*节点对之间的距离小于1000时，先用QPSK尝试建立连接，若失败，再用8-QAM建立*/
//                                        if (nodepair.getDemand()<350){
//                                            lightpath.setChannels(i);
//
//                                            lightpath.setModuFormat(QPSK);
//                                            gv.setRemCapacity(QPSK.getCapacity()*i-nodepair.getDemand()+ gv.getRemCapacity());
//                                            lightpath.setNipQ(2*i);
//
//                                            int distance=0;
//                                            for (Link layerlink:lightpath.getLplink()) {
//                                                distance+= layerlink.getLength();
////                                                existLinkList.add(layerlink);
//                                                layerlink.setChannels(layerlink.getChannels()+i);
//                                                layerlink.setCapacity(layerlink.getCapacity()+ QPSK.getCapacity()*i);
//                                                layerlink.setRemCapacity(layerlink.getRemCapacity()+QPSK.getCapacity()*i-nodepair.getDemand());
//                                                lightpath.setRemCapacity(lightpath.getRemCapacity()+layerlink.getRemCapacity());
//                                            }
//                                            lightpath.setDistance(distance);    //设置光路距离
//
//                                            /*波长分配*/
//                                            dijkstra.AssignWavelength(lightpath);
//
//                                            int maxWaveNum=0;
//                                            for (Link l:lightpath.getLplink()) {
//                                                if (l.getChannels()>maxWaveNum){
//                                                    maxWaveNum=l.getChannels();
//                                                }
//                                            }
//                                            vlink.setMaxWaveNum(maxWaveNum);    //最大波长数
//                                            vlink.setHop(lightpath.getHops());
//                                            vlink.setLength(desNode.getLength_from_src());
//                                            vlink.setLinks(lightpath.getLplink());
//
//                                            if (desNode.getLength_from_src()>=1000&&desNode.getLength_from_src()<2000) lightpath.setNregenQ(i);
//                                            else if (desNode.getLength_from_src()>=2000) lightpath.setNregenQ(2*i);
//                                            break;
//                                        }
//                                        else if (nodepair.getDemand()<eightQAM.getCapacity()*i){
//                                            lightpath.setChannels(i);
//
//
//                                            lightpath.setModuFormat(eightQAM);
//                                            gv.setRemCapacity(eightQAM.getCapacity()*i-nodepair.getDemand()+ gv.getRemCapacity());
//                                            lightpath.setNipM(2*i);
//                                            int distance=0;
//                                            for (Link layerlink:lightpath.getLplink()) {
//                                                distance+=layerlink.getLength();
////                                                existLinkList.add(layerlink);
//                                                layerlink.setChannels(layerlink.getChannels()+i);
//                                                layerlink.setCapacity(layerlink.getCapacity()+ eightQAM.getCapacity()*i);
//                                                layerlink.setRemCapacity(layerlink.getRemCapacity()+eightQAM.getCapacity()*i-nodepair.getDemand());
//                                                lightpath.setRemCapacity(lightpath.getRemCapacity()+layerlink.getRemCapacity());
//                                            }
//                                            lightpath.setDistance(distance);    //设置光路距离
//
//                                            /*波长分配*/
//                                            dijkstra.AssignWavelength(lightpath);
//
//                                            int maxWaveNum=0;
//                                            for (Link l:lightpath.getLplink()) {
//                                                if (l.getChannels()>maxWaveNum){
//                                                    maxWaveNum=l.getChannels();
//                                                }
//                                            }
//                                            vlink.setMaxWaveNum(maxWaveNum);    //最大波长数
//                                            vlink.setHop(lightpath.getHops());
//                                            vlink.setLength(desNode.getLength_from_src());
//                                            vlink.setLinks(lightpath.getLplink());
//
//                                            if (desNode.getLength_from_src()>=1000&&desNode.getLength_from_src()<2000) lightpath.setNregenM(i);
//                                            else if (desNode.getLength_from_src()>=2000) lightpath.setNregenM(2*i);
//                                            break;
//                                        }
//                                    }
//                                }
//
//                                vlink.setRemCapacity(vlink.getRemCapacity()+lightpath.getRemCapacity());
//                                existVlinkList.add(vlink);
//
//
//                                //建立光通道的时候已经更新了链路的属性，接下来的步骤是为了在光路上添加链路
//                                existNodeList.add(srcNode);
//                                existNodeList.add(desNode);
////                                System.out.println();
////                                System.out.println("新拓扑添加光路:"+lightpath.getName()+"   channels:"+lightpath.getChannels()
////                                 +"  moduFormat:"+lightpath.getModuFormat().getName());
////                                for (Link l:lightpath.getLplink()) {
////                                    System.out.println(l.getName()+"  rem:"+l.getRemCapacity());
////                                }
//                                newGv.getLpLinklist().add(lightpath);
//
//                            }
//
//                        }
//
//                        /*
//                         * 程序执行到此，说明业务集合中的一个业务已经建立成功，直接跳过遍历节点对循环，建立业务集合中的下一个业务
//                         * 遍历业务之后会将其从节点对的业务集合中删除，如果不加下面这条语句，该业务建立之后会一直遍历节点对之间的业务集合，
//                         * 会出现ConcurrentModificationException异常
//                         */
//                        break Loop;
//                    }
//                }
//
//            }
//
//        }
//
//        if (satisfy){
//            this.addLightPath(newGv);
//            cost=newGv.getNipB()*2+newGv.getNipQ()*2.6+newGv.getNipM()*3+newGv.getNregenB()+newGv.getNregenQ()*1.3+newGv.getNregenM()*1.5;
//            newGv.setTotal_Cost(cost);
//
//            for (Lightpath lightpath: newGv.getLpLinklist()) {
//                System.out.println(lightpath.getName()+"  "+lightpath.getModuFormat().getName()+"  "+lightpath.getChannels()+"  "+lightpath.getDistance());
//            }
//            int sum=0;
//            for (Nodepair nodepair:nodepairlist.values()) {
//                sum+=nodepair.getBlockService();
//            }
//            newGv.setBlockServiceSum(sum);  //拓扑被阻塞业务总数
//
//            if (newGv.getTotal_Cost()<gv.getTotal_Cost()){
////                System.out.println("新拓扑被阻塞业务总数："+newGv.getBlockServiceSum());
//
//                for (Nodepair nodepair:nodepairlist.values()) {
//                    Node srcNode=nodepair.getSrcNode();
//                    Node desNode=nodepair.getDesNode();
//                    Link link= layer.findLink(srcNode,desNode);
//                    float s=0;
//                    if (link!=null){
//                        System.out.print("link name:"+link.getName()+"  Capacity:"+link.getCapacity()+"  ");
//                        System.out.print("remCapacity:"+link.getRemCapacity()+"  channel:"+link.getChannels());
//                        System.out.print("  === 链路占用波长数为: ");
//                        System.out.println(link.getWavelengthList().size()+"  ===");
//                        s = 1 - (float) link.getRemCapacity()/link.getCapacity();
//                        link.setSpectrumEfficiency(s);  //链路频谱资源利用率
//                    }
//                }
//            }
//
//            return newGv;
//        }
//        else return gv;
//
//    }           //reOptimization方法结束
//
//
//    private void addLightPath(Gv newGv) {
//        for (Lightpath lightpath: newGv.getLpLinklist()){
//            newGv.setNipB(newGv.getNipB()+lightpath.getNipB());
//            newGv.setNipQ(newGv.getNipQ()+lightpath.getNipQ());
//            newGv.setNipM(newGv.getNipM()+lightpath.getNipM());
//            newGv.setNregenB(newGv.getNregenB()+lightpath.getNregenB());
//            newGv.setNregenQ(newGv.getNregenQ()+lightpath.getNregenQ());
//            newGv.setNregenM(newGv.getNregenM()+lightpath.getNregenM());
//        }
//
//        System.out.print("ip数：B "+newGv.getNipB()+"  Q:"+newGv.getNipQ()+"  M:"+newGv.getNipM());
//        System.out.println("  regen数：B "+newGv.getNregenB()+"  Q:"+newGv.getNregenQ()+"  M:"+newGv.getNregenM());
//
//    }
//
//    private void shortestPath(HashMap<String, Node> nodeList, Layer layer, Node srcNode, Node desNode) {
//
//        Dijkstra dijkstra = new Dijkstra();
//        ArrayList<Node>visitedNodeList=new ArrayList<>();
//
//        for (String s : nodeList.keySet()) {     //将map中的节点初始化
//            Node node = nodeList.get(s);    //创建节点对象，指向map中正在迭代的节点元素
//            node.setLength_from_src(Constant.infinite);
//            node.setParentNode(null);
//            node.setStatus(Constant.unvisited);
//        }
//
//        Node currentNode= srcNode;       //声明中间节点,初始赋值为srcNode
//        currentNode.setLength_from_src(0);
//        currentNode.setStatus(Constant.visitedTwice);
//        //找到最短路径
//        while(currentNode!= desNode) {
//            visitedNodeList.remove(currentNode);
//
//            for(Node node:currentNode.getNeinodelist()) {
//                Link link= layer.findLink(currentNode, node);
//                if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的距离
//                    node.setStatus(Constant.visitedOnce);
//                    node.setLength_from_src(currentNode.getLength_from_src()+link.getLength());
//                    visitedNodeList.add(node);
//                    node.setParentNode(currentNode);
//                }
//                else if(node.getStatus()==Constant.visitedOnce) {	//之后再遍历，更新currentNode节点相邻节点到源点的距离
//                    if(node.getLength_from_src()>currentNode.getLength_from_src()+link.getLength()) {
//                        node.setLength_from_src(currentNode.getLength_from_src()+link.getLength());
//                        node.setParentNode(currentNode);
//                    }
//                }
//            }
//            //遍历结束，在已访问节点集合中找到距离源点最小的节点，赋给currentNode节点
//            currentNode= dijkstra.getShortestLengthNode(visitedNodeList);
//        }
//        int distance= currentNode.getLength_from_src();
//        desNode.setLength_from_src(distance);
//    }
//
//    public int getWavelengthOccupancy(LinkedHashMap<String,Nodepair>nodepairlist, Layer layer){
//        Wavelength wave=new Wavelength();
//        LinkedList<Integer>List=new LinkedList<>();
//        LinkedList<Integer>totusedlist=new LinkedList<>();      //总使用波长集合
//        for (int i = 0; i<wave.getWaveTotalNumbers(); i++){
//            totusedlist.add(i);
//        }
//        int i=0,waveNum=0;
//        for (String s : nodepairlist.keySet()) {     //根据节点对列表遍历网络上的所有链路
//            Nodepair nodePair = nodepairlist.get(s);
//            Node nodeA = nodePair.getSrcNode();
//            Node nodeB = nodePair.getDesNode();
//            Link link = layer.findLink(nodeA, nodeB);     //得到节点和链路
//
//            if (link != null) {
//                System.out.print(link.getName() + "所占波长：" + link.getWavelengthList().size() + "  ");
//                i++;
//                if (i % 6 == 0) {
//                    System.out.println();
//                }
//                List.addAll(link.getWavelengthList());
//            }
//        }
//        System.out.println();
//        totusedlist.retainAll(List);
//        waveNum=totusedlist.size();
//        System.out.println("网络上所有占用波长："+totusedlist+" 总波长数："+waveNum);
//
//        return waveNum;
//    }
//
//}