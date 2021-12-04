package network;

import general.Constant;
import routeSearch.Dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.zip.DeflaterOutputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Jq zhou
 * @Date: 2021/07/29/21:21
 * @Description:
 */
public class WavePlane {
    private HashMap<String,Node> nodelist=new HashMap<>();
    private HashMap<String,Link>linklist=new HashMap<>();
    private Route route;

    private String name;
    private int index;


    public WavePlane() {

    }


    public WavePlane(String name, int index) {
        this.name = name;
        this.index = index;
    }



    //波平面算法
    /*
    根据网络上链路的占用波长情况来选择分配的波长
    当前波平面可以用，则用。不可用，则启用新的波平面
    传进来的光路参数包含了其映射到光层上的所有链路，根据这个信息去判断。
     */
    //返回光路集合
    public ArrayList<Lightpath> wavePlaneAlgorithm(LinkedList<WavePlane> wavePlaneList, Layer layer, Nodepair nodepair, Gv gv){
        ArrayList<Lightpath> lightpathList=new ArrayList<>();
        Wavelength wave=new Wavelength();
        ModuFormat BPSK=new ModuFormat("BPSK",175);
        ModuFormat QPSK=new ModuFormat("QPSK",350);
        ModuFormat eightQAM=new ModuFormat("8-QAM",525);
        Dijkstra dijkstra=new Dijkstra();

        Node srcNode=nodepair.getSrcNode();
        Node desNode=nodepair.getDesNode();

        int shortestLength=dijkstra.shortestLength(srcNode,desNode,layer);
        int service=nodepair.getDemand();
        //根据光通道源-目的节点以及得到的波平面集合，在能满足节点对之间业务的波平面选择跳数最小的路由
        Loop:for (WavePlane wavePlane:wavePlaneList) {
            String Lp_name=nodepair.getSrcNode().getName()+"-"+nodepair.getDesNode().getName();
            Lightpath lightpath=new Lightpath(Lp_name);
            lightpath.setSrcNode(nodepair.getSrcNode());
            lightpath.setDesNode(nodepair.getDesNode());
            //根据波平面中的链路得到已有链路集合
            HashSet<Link> existLinklist = new HashSet<>(wavePlane.getLinklist().values());
            ArrayList<Route>routeList=findWavePlaneRoute(srcNode,desNode,layer,existLinklist,5);
            //得到波平面上的所有路由集合
            if (routeList.size()!=0){
//                LinkedList<Integer>routeArr=new LinkedList<>();
//                for (Route r:routeList) {
//                    routeArr.add(r.getHops());
//                }
//                Collections.sort(routeArr);
//                int hop=routeArr.getFirst();    //得到所有路由中跳数最小的
                for (Route r:routeList) {
                    //选择最小跳数路由建立光通道并分配波长
//                    if (r.getHops()==hop){
                    int distance=r.getLength();     //路由的距离

                    //当前路由距离与最短路由距离进行比较
                    //调制格式最多允许降一级
//                        if (shortestLength<1000&&distance>2000){
//                            continue Loop;
//                        }
                    //不允许降级
                    if (shortestLength<1000&&distance>1000||shortestLength<2000&&distance>2000){
                        continue Loop;
                    }

                    lightpath.setOccupyWavelengthIndex(wavePlane.getIndex());   //光路所占的波长编号
                    lightpath.setHops(r.getLinklist().size());
                    lightpath.setDistance(distance);
                    lightpath.setViaLink(r.getName());
                    if (distance>=2000){
                        //BPSK调制格式下单个光通道不满足当前业务
                        if (service>BPSK.getCapacity()){
                            lightpath.setDemand(175);
                            lightpathList.add(lightpath);
                            lightpath.setChannels(1);
                            lightpath.setModuFormat(BPSK);
                            //光路容量
                            lightpath.setCapacity(lightpath.getChannels()*lightpath.getModuFormat().getCapacity());
                            //光路剩余容量
                            lightpath.setRemCapacity(0);
//                                gv.setRemCapacity(BPSK.getCapacity()-service+ gv.getRemCapacity());
                            lightpath.setNipB(2);
                            srcNode.setNipB(1+srcNode.getNipB());
                            desNode.setNipB(1+desNode.getNipB());
                            for (Link link:r.getLinklist()) {
                                wavePlane.getLinklist().remove(link.getName());
                                link.getAvaWavelist().remove(wavePlane.getIndex());
                                link.getWavelengthList().add(wavePlane.getIndex());
                                lightpath.getLplink().add(link);            //光路添加链路，波长分配
                                link.getLightpathList().add(lightpath);     //链路添加光路，grooming
                                link.setChannels(link.getChannels()+1);
                                System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
                                link.setCapacity(link.getCapacity()+ BPSK.getCapacity());
//                                    System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
//                                    link.setRemCapacity(link.getRemCapacity()+BPSK.getCapacity()-service);
//                                    System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
                            }
                            System.out.println();
                            if (distance>=4000) lightpath.setNregenB(1);
                            service=service-175;
                            continue Loop;
                        }else {
                            lightpath.setDemand(service);
                            lightpathList.add(lightpath);
                            lightpath.setChannels(1);
                            lightpath.setModuFormat(BPSK);
                            //光路容量
                            lightpath.setCapacity(lightpath.getChannels()*lightpath.getModuFormat().getCapacity());
                            //光路剩余容量
                            lightpath.setRemCapacity(lightpath.getCapacity()-service);
                            gv.setRemCapacity(BPSK.getCapacity()-service+ gv.getRemCapacity());
                            lightpath.setNipB(2);
                            srcNode.setNipB(1+srcNode.getNipB());
                            desNode.setNipB(1+desNode.getNipB());
                            for (Link link:r.getLinklist()) {
                                wavePlane.getLinklist().remove(link.getName());
                                link.getAvaWavelist().remove(wavePlane.getIndex());
                                link.getWavelengthList().add(wavePlane.getIndex());
                                lightpath.getLplink().add(link);            //光路添加链路，波长分配
                                link.getLightpathList().add(lightpath);     //链路添加光路，grooming
                                link.setChannels(link.getChannels()+1);
                                System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
                                link.setCapacity(link.getCapacity()+ BPSK.getCapacity());
                                System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
                                link.setRemCapacity(link.getRemCapacity()+BPSK.getCapacity()-service);
                                System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
                            }
                            System.out.println();
                            if (distance>=4000) lightpath.setNregenB(1);
                            break;
                        }
                    }
                    else if(distance>=1000){
                        lightpath.setDemand(service);
                        lightpathList.add(lightpath);
                        lightpath.setChannels(1);
                        lightpath.setModuFormat(QPSK);
                        //光路容量
                        lightpath.setCapacity(lightpath.getChannels()*lightpath.getModuFormat().getCapacity());
                        //光路剩余容量
                        lightpath.setRemCapacity(lightpath.getCapacity()-service);
                        gv.setRemCapacity(QPSK.getCapacity()-nodepair.getDemand()+ gv.getRemCapacity());
                        lightpath.setNipQ(2);
                        srcNode.setNipQ(1+srcNode.getNipQ());
                        desNode.setNipQ(1+desNode.getNipQ());
                        for (Link link:r.getLinklist()) {
                            wavePlane.getLinklist().remove(link.getName());
                            link.getAvaWavelist().remove(wavePlane.getIndex());
                            link.getWavelengthList().add(wavePlane.getIndex());
                            lightpath.getLplink().add(link);            //光路添加链路，波长分配
                            link.getLightpathList().add(lightpath);     //链路添加光路，grooming
                            link.setChannels(link.getChannels()+1);
                            System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
                            link.setCapacity(link.getCapacity()+ QPSK.getCapacity());
                            System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
                            link.setRemCapacity(link.getRemCapacity()+QPSK.getCapacity()-nodepair.getDemand());
                            System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
                        }
                        System.out.println();

                        break;
                    }
                    else {
                        lightpath.setDemand(service);
                        lightpathList.add(lightpath);
                        lightpath.setChannels(1);
                        lightpath.setModuFormat(eightQAM);
                        //光路容量
                        lightpath.setCapacity(lightpath.getChannels()*lightpath.getModuFormat().getCapacity());
                        //光路剩余容量
                        lightpath.setRemCapacity(lightpath.getCapacity()-service);
                        gv.setRemCapacity(eightQAM.getCapacity()-nodepair.getDemand()+ gv.getRemCapacity());
                        lightpath.setNipM(2);
                        srcNode.setNipM(1+srcNode.getNipM());
                        desNode.setNipM(1+desNode.getNipM());
                        for (Link link:r.getLinklist()) {
                            wavePlane.getLinklist().remove(link.getName());

                            link.getAvaWavelist().remove(wavePlane.getIndex());
                            link.getWavelengthList().add(wavePlane.getIndex());

                            lightpath.getLplink().add(link);            //光路添加链路，波长分配
                            link.getLightpathList().add(lightpath);     //链路添加光路，grooming
                            link.setChannels(link.getChannels()+1);
                            System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
                            link.setCapacity(link.getCapacity()+ eightQAM.getCapacity());
                            System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
                            link.setRemCapacity(link.getRemCapacity()+eightQAM.getCapacity()-nodepair.getDemand());
                            System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
                        }
                        System.out.println();

                        break;
                    }

//                    }
                }

                break;
            }
        }

        return lightpathList;
    }


    public ArrayList<Lightpath> PS_WavePlane(LinkedList<WavePlane> wavePlaneList, Layer layer, Nodepair nodepair, Gv gv,HashMap<Integer,Double>PS){
        ModuFormat PS64QAM=new ModuFormat();

        ArrayList<Lightpath> lightpathList=new ArrayList<>();
        Wavelength wave=new Wavelength();
        Dijkstra dijkstra=new Dijkstra();

        Node srcNode=nodepair.getSrcNode();
        Node desNode=nodepair.getDesNode();

        int shortestLength=dijkstra.shortestLength(srcNode,desNode,layer);
        int service=nodepair.getDemand();

        //根据光通道源-目的节点以及得到的波平面集合，在能满足节点对之间业务的波平面选择跳数最小的路由
        Loop:for (WavePlane wavePlane:wavePlaneList) {
            String Lp_name=nodepair.getSrcNode().getName()+"-"+nodepair.getDesNode().getName();
            Lightpath lightpath=new Lightpath(Lp_name);
            lightpath.setSrcNode(nodepair.getSrcNode());
            lightpath.setDesNode(nodepair.getDesNode());
            //根据波平面中的链路得到已有链路集合
            HashSet<Link> existLinklist = new HashSet<>(wavePlane.getLinklist().values());
            ArrayList<Route>routeList=findWavePlaneRoute(srcNode,desNode,layer,existLinklist,5);
            //得到波平面上的所有路由集合
            if (routeList.size()!=0){
//                LinkedList<Integer>routeArr=new LinkedList<>();
//                for (Route r:routeList) {
//                    routeArr.add(r.getHops());
//                }
//                Collections.sort(routeArr);
//                int hop=routeArr.getFirst();    //得到所有路由中跳数最小的
                for (Route r:routeList) {
                    //选择最小跳数路由建立光通道并分配波长
//                    if (r.getHops()==hop){
                    int distance=r.getLength();             //路由的距离
                    if (r.getHops()>3||distance>10000) continue Loop;       //跳数限制
                    System.out.println(distance);
                    double speEfficiency= PS.get(distance); //路由对应的频谱效率

                    lightpath.setOccupyWavelengthIndex(wavePlane.getIndex());   //光路所占的波长编号
                    lightpath.setHops(r.getLinklist().size());
                    lightpath.setDistance(distance);
                    lightpath.setViaLink(r.getName());

                    /*Quasi-CWDM网络下200G栅格*/
                    double capacity1= speEfficiency*200;

                    /*WDM网络下50G栅格*/
//                    double capacity1= speEfficiency*50;


                    int capacity=(int) capacity1;

                    lightpath.setDemand(service);
                    lightpathList.add(lightpath);
                    lightpath.setChannels(1);
                    lightpath.setModuFormat(PS64QAM);
                    //光路容量
                    lightpath.setCapacity(capacity);
                    //光路剩余容量
                    lightpath.setRemCapacity(lightpath.getCapacity()-service);
                    gv.setRemCapacity(capacity-service+ gv.getRemCapacity());
                    for (Link link:r.getLinklist()) {
                        wavePlane.getLinklist().remove(link.getName());

                        link.getAvaWavelist().remove(wavePlane.getIndex());
                        link.getWavelengthList().add(wavePlane.getIndex());

                        lightpath.getLplink().add(link);            //光路添加链路，波长分配
                        link.getLightpathList().add(lightpath);     //链路添加光路，grooming
                        link.setChannels(link.getChannels()+1);
                        System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
                        link.setCapacity(link.getCapacity()+ capacity);
                        System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
                        link.setRemCapacity(link.getRemCapacity()+capacity-service);
                        System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
                    }
                    System.out.println();
                    break;

                }
                break;
            }
        }

        return lightpathList;
    }



    /*
    先扫一遍所有的波平面，然后在所有波平面中选距离最短/跳数最小的
     */
//    public ArrayList<Lightpath> wavePlaneAlgorithm1(LinkedList<WavePlane>wavePlaneList,Layer layer,Nodepair nodepair,Gv gv){
//        ArrayList<Lightpath> lightpathList=new ArrayList<>();
//        Wavelength wave=new Wavelength();
//        ModuFormat BPSK=new ModuFormat("BPSK",175);
//        ModuFormat QPSK=new ModuFormat("QPSK",350);
//        ModuFormat eightQAM=new ModuFormat("8-QAM",525);
//
//        Node srcNode=nodepair.getSrcNode();
//        Node desNode=nodepair.getDesNode();
//
//        int service=nodepair.getDemand();
//        Loop:while (true){
//
//            //根据光通道源-目的节点以及得到的波平面集合，在能满足节点对之间业务的波平面选择跳数最小的路由
//            HashMap<WavePlane,Route> wpMap=new HashMap<>();
//
//            for (WavePlane w:wavePlaneList) {
//                String Lp_name=nodepair.getSrcNode().getName()+"-"+nodepair.getDesNode().getName();
//                Lightpath lightpath=new Lightpath(Lp_name);
//                lightpath.setSrcNode(nodepair.getSrcNode());
//                lightpath.setDesNode(nodepair.getDesNode());
//
//                //根据波平面中的链路得到已有链路集合
//                HashSet<Link> existLinklist = new HashSet<>(w.getLinklist().values());
//                Route route=findWavePlaneRoute(srcNode,desNode,layer,existLinklist);
//
//                if (route!=null){
//                    w.setRoute(route);
//                    wpMap.put(w,route);
//                }
//            }
//
//            int routeLength=Integer.MAX_VALUE;
//            for (Route route:wpMap.values()) {
//                if (route.getLength()<routeLength){
//                    routeLength=route.getLength();
//                }
//            }
//
//            //在所有波平面中选择距离最短/跳数最小的
//            for (WavePlane w:wavePlaneList) {
//                if (w.getRoute().getLength()==routeLength){
//                    Route r=w.getRoute();
//                    String Lp_name=nodepair.getSrcNode().getName()+"-"+nodepair.getDesNode().getName();
//                    Lightpath lightpath=new Lightpath(Lp_name);
//                    lightpath.setDistance(routeLength);
//                    lightpath.setOccupyWavelengthIndex(w.getIndex());
//                    lightpath.setSrcNode(nodepair.getSrcNode());
//                    lightpath.setDesNode(nodepair.getDesNode());
//                    lightpath.setViaLink(r.getName());
//                    if (routeLength >=2000){
//                        //BPSK调制格式下单个光通道不满足当前业务
//                        if (service>BPSK.getCapacity()){
//                            lightpath.setDemand(175);
//                            lightpathList.add(lightpath);
//                            lightpath.setChannels(1);
//                            lightpath.setModuFormat(BPSK);
//                            //光路容量
//                            lightpath.setCapacity(lightpath.getChannels()*lightpath.getModuFormat().getCapacity());
//                            //光路剩余容量
//                            lightpath.setRemCapacity(0);
//        //                                gv.setRemCapacity(BPSK.getCapacity()-service+ gv.getRemCapacity());
//                            lightpath.setNipB(2);
//                            for (Link link:r.getLinklist()) {
//                                w.getLinklist().remove(link.getName());
//        //                                    link.getAvaWavelist().remove(w.getIndex());
//        //                                    link.getWavelengthList().add(w.getIndex());
//                                lightpath.getLplink().add(link);            //光路添加链路，波长分配
//                                link.getLightpathList().add(lightpath);     //链路添加光路，grooming
//                                link.setChannels(link.getChannels()+1);
//                                System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
//                                link.setCapacity(link.getCapacity()+ BPSK.getCapacity());
//        //                                    System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
//        //                                    link.setRemCapacity(link.getRemCapacity()+BPSK.getCapacity()-service);
//        //                                    System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
//                            }
//                            System.out.println();
//                            if (routeLength >=4000) lightpath.setNipB(1);
//                            service=service-175;
//                            continue Loop;
//                        }else {
//                            lightpath.setDemand(service);
//                            lightpathList.add(lightpath);
//                            lightpath.setChannels(1);
//                            lightpath.setModuFormat(BPSK);
//                            //光路容量
//                            lightpath.setCapacity(lightpath.getChannels()*lightpath.getModuFormat().getCapacity());
//                            //光路剩余容量
//                            lightpath.setRemCapacity(lightpath.getCapacity()-service);
//                            gv.setRemCapacity(BPSK.getCapacity()-service+ gv.getRemCapacity());
//                            lightpath.setNipB(2);
//                            for (Link link:r.getLinklist()) {
//                                w.getLinklist().remove(link.getName());
//        //                                    link.getAvaWavelist().remove(w.getIndex());
//        //                                    link.getWavelengthList().add(w.getIndex());
//                                lightpath.getLplink().add(link);            //光路添加链路，波长分配
//                                link.getLightpathList().add(lightpath);     //链路添加光路，grooming
//                                link.setChannels(link.getChannels()+1);
//                                System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
//                                link.setCapacity(link.getCapacity()+ BPSK.getCapacity());
//                                System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
//                                link.setRemCapacity(link.getRemCapacity()+BPSK.getCapacity()-service);
//                                System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
//                            }
//                            System.out.println();
//                            if (routeLength >=4000) lightpath.setNipB(1);
//                            break Loop;
//                        }
//                    }
//                    else if(routeLength >=1000){
//                        lightpath.setDemand(service);
//                        lightpathList.add(lightpath);
//                        lightpath.setChannels(1);
//                        lightpath.setModuFormat(QPSK);
//                        //光路容量
//                        lightpath.setCapacity(lightpath.getChannels()*lightpath.getModuFormat().getCapacity());
//                        //光路剩余容量
//                        lightpath.setRemCapacity(lightpath.getCapacity()-service);
//                        gv.setRemCapacity(QPSK.getCapacity()-nodepair.getDemand()+ gv.getRemCapacity());
//                        lightpath.setNipQ(2);
//                        for (Link link:r.getLinklist()) {
//                            w.getLinklist().remove(link.getName());
//        //                                link.getAvaWavelist().remove(w.getIndex());
//        //                                link.getWavelengthList().add(w.getIndex());
//                            lightpath.getLplink().add(link);            //光路添加链路，波长分配
//                            link.getLightpathList().add(lightpath);     //链路添加光路，grooming
//                            link.setChannels(link.getChannels()+1);
//                            System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
//                            link.setCapacity(link.getCapacity()+ QPSK.getCapacity());
//                            System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
//                            link.setRemCapacity(link.getRemCapacity()+QPSK.getCapacity()-nodepair.getDemand());
//                            System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
//                        }
//                        System.out.println();
//
//                        break Loop;
//                    }
//                    else {
//                        lightpath.setDemand(service);
//                        lightpathList.add(lightpath);
//                        lightpath.setChannels(1);
//                        lightpath.setModuFormat(eightQAM);
//                        //光路容量
//                        lightpath.setCapacity(lightpath.getChannels()*lightpath.getModuFormat().getCapacity());
//                        //光路剩余容量
//                        lightpath.setRemCapacity(lightpath.getCapacity()-service);
//                        gv.setRemCapacity(eightQAM.getCapacity()-nodepair.getDemand()+ gv.getRemCapacity());
//                        lightpath.setNipM(2);
//                        for (Link link:r.getLinklist()) {
//                            w.getLinklist().remove(link.getName());
//        //                                link.getAvaWavelist().remove(w.getIndex());
//        //                                link.getWavelengthList().add(w.getIndex());
//                            lightpath.getLplink().add(link);            //光路添加链路，波长分配
//                            link.getLightpathList().add(lightpath);     //链路添加光路，grooming
//                            link.setChannels(link.getChannels()+1);
//                            System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
//                            link.setCapacity(link.getCapacity()+ eightQAM.getCapacity());
//                            System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
//                            link.setRemCapacity(link.getRemCapacity()+eightQAM.getCapacity()-nodepair.getDemand());
//                            System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
//                        }
//                        System.out.println();
//
//                        break Loop;
//                    }
////                    break Loop;
//                }
//            }
//        }
//
//        return lightpathList;
//    }


    //根据波平面的链路集合找到所有路由
    public ArrayList<Route> findWavePlaneRoute(Node srcNode, Node desNode, Layer layer, HashSet<Link>exitlinklist,int k) {

        HashMap<String,Node>map=layer.getNodelist();
        ArrayList<Node>visitedNodelist=new ArrayList<>();
        Dijkstra dijkstra=new Dijkstra();


        int routeNum=0;
        ArrayList<Integer>routeLengthList=new ArrayList<>();
        ArrayList<Route>routeList=new ArrayList<>();

        Loop:while (true){

            //将map中的节点初始化
            for (String a : map.keySet()) {
                Node node = map.get(a);    //创建节点对象，指向map中正在迭代的节点元素
                node.setParentNode(null);
                node.setStatus(Constant.unvisited);
                node.setLength_from_src(Constant.infinite);
                node.setCost_from_src(Constant.infinite);
            }

            //声明中间节点,初始赋值为srcNode
            Node currentNode=srcNode;
            currentNode.setStatus(Constant.visitedTwice);
            currentNode.setLength_from_src(0);
            currentNode.setCost_from_src(0);

            visitedNodelist.clear();
            int routeLength=0;

            while(currentNode!=desNode) {
                visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除

                if (currentNode==null){
                    break Loop;
                }

                for (Node node : currentNode.getNeinodelist()) {
                    Link link = layer.findLink(currentNode, node);        //link指向currentNode节点到相邻节点的链接

                    /* *********关键步骤********/
                    if (!exitlinklist.contains(link)) {
                        continue;           //当已存在链路不包含当前链路时，跳过这次循环，继续寻找其他已存在的链路
                    }

                    routeLength= currentNode.getLength_from_src()+link.getLength();
                    if (node==desNode&&routeLengthList.contains(routeLength)){
                        continue ;
                    }

                    if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的距离
                        node.setLength_from_src(currentNode.getLength_from_src()+link.getLength());
                        node.setCost_from_src(currentNode.getCost_from_src()+link.getCost());
                        node.setStatus(Constant.visitedOnce);
                        visitedNodelist.add(node);
                        node.setParentNode(currentNode);
                    }

                    else if(node.getStatus()==Constant.visitedOnce) {	//之后再遍历，更新currentNode节点相邻节点到源点的距离
                        if(node.getLength_from_src()>currentNode.getLength_from_src()+link.getLength()) {
//                        if(node.getCost_from_src()>currentNode.getCost_from_src()+link.getCost()) {
                            node.setLength_from_src(currentNode.getLength_from_src()+link.getLength());
                            node.setCost_from_src(currentNode.getCost_from_src()+link.getCost());
                            node.setParentNode(currentNode);
                        }
                    }

                }
                //遍历结束，在已访问节点集合中找到距离源点最短的节点，赋给currentNode节点
//                currentNode=this.getShortestLengthNode(visitedNodelist);
                currentNode=dijkstra.getMinimalCostNode(visitedNodelist);
            }

            //当currentNode=desNode时，while结束，输出结果
            Route newRoute=new Route("",0,Constant.Free);

            Node currentNode1=desNode;
            newRoute.setName(currentNode1.getName()+newRoute.getName());
            routeLength=0;
            while (currentNode1!=srcNode){
                Link link= layer.findLink(currentNode1,currentNode1.getParentNode());
                newRoute.getLinklist().add(link);
                routeLength=routeLength+link.getLength();
                currentNode1=currentNode1.getParentNode();
                newRoute.setName(currentNode1.getName()+"-"+newRoute.getName());
            }
            newRoute.setLength(routeLength);
            routeLengthList.add(routeLength);
            newRoute.setHops(newRoute.getLinklist().size());
            routeList.add(newRoute);
            if (++routeNum==k){
                break;
            }
        }
        return routeList;

    }



    /***************setters and getters******************/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
