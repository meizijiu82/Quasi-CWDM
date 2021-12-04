//package TEST;
//import java.util.*;
//
//import network.*;
//import routeSearch.*;
//import general.*;
//
//public class Test01{
//
//    public HashSet<Link> findClosestNode(Node srcNode, Node desNode, Layer layer, HashSet<Link>exitlinklist) {
//
//        HashMap<String,Node>map=layer.getNodelist();
//        ArrayList<Node>visitedNodelist=new ArrayList<>();
//
//        //将map中的节点初始化
//        for (String a : map.keySet()) {
//            Node node = map.get(a);    //创建节点对象，指向map中正在迭代的节点元素
//            node.setParentNode(null);
//            node.setStatus(Constant.unvisited);
//            node.setLength_from_src(Constant.infinite);
//        }
//
//        //声明中间节点,初始赋值为srcNode
//        Node currentNode=srcNode;
//        currentNode.setLength_from_src(0);
//        currentNode.setStatus(Constant.visitedTwice);
//
//
//        while(currentNode!=desNode) {
//            visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除
//
//            if (currentNode==null){
//                System.out.println("=================================================================================");
//                System.out.println(srcNode.getName()+"-"+desNode.getName()+"    已有链路无法满足节点对之间的路由！");
//                System.out.println("=================================================================================");
//                break;
//            }
//
//            for (Node node : currentNode.getNeinodelist()) {
//                Link link = layer.findLink(currentNode, node);        //link指向currentNode节点到相邻节点的链接
//
//                /* *********关键步骤********/
//                if (!exitlinklist.contains(link)) {
//                    continue;           //当已存在链路不包含当前链路时，跳过这次循环，继续寻找其他已存在的链路
//                }
//
//                if (node.getStatus() == Constant.unvisited) {        //第一次遍历，初始所有currentNode节点相邻节点到源点的距离
//                    visitedNodelist.add(node);
//                    node.setLength_from_src(currentNode.getLength_from_src() + link.getLength());
//                    node.setStatus(Constant.visitedOnce);
//                    node.setParentNode(currentNode);
//                } else if (node.getStatus() == Constant.visitedOnce) {    //之后再遍历，更新currentNode节点相邻节点到源点的距离
//                    if (node.getLength_from_src() > currentNode.getLength_from_src() + link.getLength()) {
//                        node.setLength_from_src(currentNode.getLength_from_src() + link.getLength());
//                        node.setParentNode(currentNode);
//                    }
//                }
//
//            }
//            //遍历结束，在已访问节点集合中找到距离源点最小的节点，赋给currentNode节点
//            currentNode=this.getShortestLengthNode(visitedNodelist);
//        }
//        //当currentNode=desNode时，while结束，输出结果
//
//        if (currentNode!=null){
//            int distance= currentNode.getLength_from_src();
//            desNode.setLength_from_src(distance);
//            return this.getVialinklist(srcNode,desNode,layer);
//        }
//        else return null;
//
//    }
//
//    public ArrayList<Route> findLeastHopsRoute(Nodepair nodepair, Layer layer, int k, Gv gv){
//        Node srcNode=nodepair.getSrcNode();
//        Node desNode=nodepair.getDesNode();
//        ArrayList<Node>visitedNodelist=new ArrayList<>();
//        HashMap<String,Node>map=layer.getNodelist();
//
//        layer.generateIPNeiNodepairs(gv);
//        for (Vlink v:gv.getVlinks().values()) {
//            v.setHop_in_IP(1);      //初始化所有IP层虚拟链路跳数为1
//        }
//
//        ArrayList<Route>routelist=new ArrayList<>();
//
//        //删除剩余资源不满足业务大小的光路（虚拟链路）
//        HashSet<Vlink> existlp = new HashSet<>(gv.getVlinks().values());
//        existlp.removeIf(l -> l.getRemCapacity() < nodepair.getDemand());   //删除集合中剩余容量小于节点对之间业务需求的虚拟链路
//
//        ArrayList<Integer>routeLengthList=new ArrayList<>();
//
//        int route_num=0;
//
//        Loop:  while (true){
//            //将map中的节点初始化
//            for (String s : map.keySet()) {
//                Node node = map.get(s);    //创建节点对象，指向map中正在迭代的节点元素
//                node.setParentNode(null);
//                node.setCost_from_src(Constant.infinite);
//                node.setLength_from_src(Constant.infinite);
//                node.setStatus(Constant.unvisited);
//            }
//
//            //声明中间节点,初始赋值为srcNode
//            Node currentNode=srcNode;
//            currentNode.setCost_from_src(0);
//            currentNode.setLength_from_src(0);
//            currentNode.setStatus(Constant.visitedTwice);
//
//            visitedNodelist.clear();
//            int routeLength=0;     //路由长度，用来判断是否已经存在路由列表中
//
//            while(currentNode!=desNode) {
//                visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除
//
//                if (currentNode==null) break Loop;
//
//                for(Node node:currentNode.getIPneinodelist()) {	//当currentNode不为desNode时，遍历currentNode节点的相邻节点集合
//
//                    Vlink vlink= gv.findVlink(currentNode,node);         //IP层上寻找路由都是针对虚拟链路
//
//                    //当虚拟链路不满足满足流量疏导条件时，跳过此次循环
//                    if (!existlp.contains(vlink)) continue ;
//
//                    routeLength= currentNode.getLength_from_src()+vlink.getLength();
//                    //以路由的长度来判断该路由是否已存在于路由列表中（手动定义虚拟链路的长度）
//                    if (node==desNode&&routeLengthList.contains(routeLength)){
//                        continue;
//                    }
//
//                    //根据剩余容量来寻找路由
//                    if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的cost
//                        node.setLength_from_src(currentNode.getLength_from_src()+ vlink.getLength());
//                        node.setCost_from_src(currentNode.getCost_from_src()+ vlink.getHop_in_IP());  //cost定义为虚拟链接的剩余容量
//                        node.setStatus(Constant.visitedOnce);
//                        visitedNodelist.add(node);
//                        node.setParentNode(currentNode);
//                    }
//
//                    else if(node.getStatus()==Constant.visitedOnce) {	//之后再遍历，更新currentNode节点相邻节点到源点的属性
//                        if(node.getCost_from_src()>currentNode.getCost_from_src()+vlink.getHop_in_IP()) {
//                            node.setLength_from_src(currentNode.getLength_from_src()+ vlink.getLength());
//                            node.setCost_from_src(currentNode.getCost_from_src()+ vlink.getHop_in_IP());
//                            node.setParentNode(currentNode);
//                        }
//                    }
//                }
//                currentNode=this.getMinimalCostNode(visitedNodelist);
//            }
//
//            Route newRoute=new Route("",0,Constant.Free);
//
//            Node currentNode1=desNode;
//            newRoute.setName(currentNode1.getName()+newRoute.getName());
//            routeLength=0;
//            while (currentNode1!=srcNode){
//                Vlink vlink= gv.findVlink(currentNode1,currentNode1.getParentNode());
//                newRoute.getVlinklist().add(vlink);
//
//                routeLength+= vlink.getLength();
//
//                currentNode1=currentNode1.getParentNode();
//                newRoute.setName(currentNode1.getName()+"-"+newRoute.getName());
//            }
//            newRoute.setHops(newRoute.getVlinklist().size());
//            routelist.add(newRoute);
//
////            System.out.print("["+srcNode.getName()+","+desNode.getName()+"]  cost:"+desNode.getCost_from_src()+"  ");   //输出节点对
////            System.out.print("length:"+desNode.getLength_from_src()+"  ");
////            this.findvialink(srcNode,desNode);
//            routeLengthList.add(routeLength);
//
////            System.out.println();
//
//            if (++route_num==k){
//                break;
//            }
//
//        }
//
//        return routelist;
//
//    }
//
//
//    public void AssignWavelength(Lightpath lightpath) {
//        Wavelength wave=new Wavelength();
//
//        LinkedList<Integer>List= availableWavelengthList(wave, lightpath);
//
//        lightpath.setAvaWavelist(List);        //设置光路可用波长集合为List
//
//        for (int i = 0; i < lightpath.getChannels(); i++) {            //BPSK调制格式下一个光通道占用一个波长
//            int w = List.getFirst();                    //w为光路所分配波长
//            for (Link link : lightpath.getLplink()) {
//                link.getWavelengthList().add(w);        //链路赋予波长
//                link.setCost(link.getCost()+1);         //链路分配一个波长，cost值加1
//            }
//            List.removeFirst();     //分配后在可用波长集合中删除已分配波长
//        }
//
//    }
//
//    public ArrayList<Lightpath> wavePlaneAlgorithm(LinkedList<WavePlane>wavePlaneList,Layer layer,Nodepair nodepair,Gv gv){
//        ArrayList<Lightpath> lightpathList=new ArrayList<>();
//        Wavelength wave=new Wavelength();
//        ModuFormat BPSK=new ModuFormat("BPSK",175);
//        ModuFormat QPSK=new ModuFormat("QPSK",350);
//        ModuFormat eightQAM=new ModuFormat("8-QAM",525);
//
//
//        Node srcNode=nodepair.getSrcNode();
//        Node desNode=nodepair.getDesNode();
//
//        int service=nodepair.getDemand();
//        //根据光通道源-目的节点以及得到的波平面集合，在能满足节点对之间业务的波平面选择跳数最小的路由
//        Loop:for (WavePlane w:wavePlaneList) {
//            String Lp_name=nodepair.getSrcNode().getName()+"-"+nodepair.getDesNode().getName();
//            Lightpath lightpath=new Lightpath(Lp_name);
//            lightpath.setSrcNode(nodepair.getSrcNode());
//            lightpath.setDesNode(nodepair.getDesNode());
//            //根据波平面中的链路得到已有链路集合
//            HashSet<Link> existLinklist = new HashSet<>(w.getLinklist().values());
//            ArrayList<Route>routeList=findWavePlaneRoute(srcNode,desNode,layer,existLinklist,5);
//            //得到波平面上的所有路由集合
//            if (routeList.size()!=0){
////                LinkedList<Integer>routeArr=new LinkedList<>();
////                for (Route r:routeList) {
////                    routeArr.add(r.getHops());
////                }
////                Collections.sort(routeArr);
////                int hop=routeArr.getFirst();    //得到所有路由中跳数最小的
//                for (Route r:routeList) {
//                    //选择最小跳数路由建立光通道并分配波长
////                    if (r.getHops()==hop){
//                    int distance=r.getLength();     //路由的距离
//                    lightpath.setOccupyWavelengthIndex(w.getIndex());   //光路所占的波长编号
//                    lightpath.setHops(r.getLinklist().size());
//                    lightpath.setDistance(distance);
//                    lightpath.setViaLink(r.getName());getName
//                    if (distance>=2000){
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
////                                gv.setRemCapacity(BPSK.getCapacity()-service+ gv.getRemCapacity());
//                            lightpath.setNipB(2);
//                            for (Link link:r.getLinklist()) {
//                                w.getLinklist().remove(link.getName());
////                                    link.getAvaWavelist().remove(w.getIndex());
////                                    link.getWavelengthList().add(w.getIndex());
//                                lightpath.getLplink().add(link);            //光路添加链路，波长分配
//                                link.getLightpathList().add(lightpath);     //链路添加光路，grooming
//                                link.setChannels(link.getChannels()+1);
//                                System.out.print("   "+link.getName()+"   link channels: "+link.getChannels()+"   ");
//                                link.setCapacity(link.getCapacity()+ BPSK.getCapacity());
////                                    System.out.print("  "+link.getName()+"之前链路剩余容量："+link.getRemCapacity()+"  ");
////                                    link.setRemCapacity(link.getRemCapacity()+BPSK.getCapacity()-service);
////                                    System.out.print("  "+link.getName()+"之后链路剩余容量："+link.getRemCapacity()+"  ");
//                            }
//                            System.out.println();
//                            if (distance>=4000) lightpath.setNipB(1);
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
////                                    link.getAvaWavelist().remove(w.getIndex());
////                                    link.getWavelengthList().add(w.getIndex());
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
//                            if (distance>=4000) lightpath.setNipB(1);
//                            break;
//                        }
//                    }
//                    else if(distance>=1000){
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
////                                link.getAvaWavelist().remove(w.getIndex());
////                                link.getWavelengthList().add(w.getIndex());
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
//                        break;
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
////                                link.getAvaWavelist().remove(w.getIndex());
////                                link.getWavelengthList().add(w.getIndex());
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
//                        break;
//                    }
//
////                    }
//                }
//
//                break;
//            }
//        }
//
//        return lightpathList;
//    }
//
//    public ArrayList<Route> findWavePlaneRoute(Node srcNode, Node desNode, Layer layer, HashSet<Link>exitlinklist,int k) {
//
//        HashMap<String,Node>map=layer.getNodelist();
//        ArrayList<Node>visitedNodelist=new ArrayList<>();
//
//        int routeNum=0;
//        ArrayList<Integer>routeLengthList=new ArrayList<>();
//        ArrayList<Route>routeList=new ArrayList<>();
//
//        Loop:while (true){
//
//            //将map中的节点初始化
//            for (String a : map.keySet()) {
//                Node node = map.get(a);    //创建节点对象，指向map中正在迭代的节点元素
//                node.setParentNode(null);
//                node.setStatus(Constant.unvisited);
//                node.setLength_from_src(Constant.infinite);
//                node.setCost_from_src(Constant.infinite);
//            }
//
//            //声明中间节点,初始赋值为srcNode
//            Node currentNode=srcNode;
//            currentNode.setStatus(Constant.visitedTwice);
//            currentNode.setLength_from_src(0);
//            currentNode.setCost_from_src(0);
//
//            visitedNodelist.clear();
//            int routeLength=0;
//
//            while(currentNode!=desNode) {
//                visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除
//
//                if (currentNode==null){
//                    break Loop;
//                }
//
//                for (Node node : currentNode.getNeinodelist()) {
//                    Link link = layer.findLink(currentNode, node);        //link指向currentNode节点到相邻节点的链接
//
//                    /* *********关键步骤********/
//                    if (!exitlinklist.contains(link)) {
//                        continue;           //当已存在链路不包含当前链路时，跳过这次循环，继续寻找其他已存在的链路
//                    }
//
//                    routeLength= currentNode.getLength_from_src()+link.getLength();
//                    if (node==desNode&&routeLengthList.contains(routeLength)){
//                        continue ;
//                    }
//
//                    if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的距离
//                        node.setLength_from_src(currentNode.getLength_from_src()+link.getLength());
//                        node.setCost_from_src(currentNode.getCost_from_src()+link.getCost());
//                        node.setStatus(Constant.visitedOnce);
//                        visitedNodelist.add(node);
//                        node.setParentNode(currentNode);
//                    }
//
//                    else if(node.getStatus()==Constant.visitedOnce) {	//之后再遍历，更新currentNode节点相邻节点到源点的距离
//                        if(node.getLength_from_src()>currentNode.getLength_from_src()+link.getLength()) {
////                        if(node.getCost_from_src()>currentNode.getCost_from_src()+link.getCost()) {
//                            node.setLength_from_src(currentNode.getLength_from_src()+link.getLength());
//                            node.setCost_from_src(currentNode.getCost_from_src()+link.getCost());
//                            node.setParentNode(currentNode);
//                        }
//                    }
//
//                }
//                //遍历结束，在已访问节点集合中找到距离源点最短的节点，赋给currentNode节点
////                currentNode=this.getShortestLengthNode(visitedNodelist);
//                currentNode=this.getMinimalCostNode(visitedNodelist);
//            }
//
//            //当currentNode=desNode时，while结束，输出结果
//            Route newRoute=new Route("",0,Constant.Free);
//
//            Node currentNode1=desNode;
//            newRoute.setName(currentNode1.getName()+newRoute.getName());
//            routeLength=0;
//            while (currentNode1!=srcNode){
//                Link link= layer.findLink(currentNode1,currentNode1.getParentNode());
//                newRoute.getLinklist().add(link);
//                routeLength=routeLength+link.getLength();
//                currentNode1=currentNode1.getParentNode();
//                newRoute.setName(currentNode1.getName()+"-"+newRoute.getName());
//            }
//            newRoute.setLength(routeLength);
//            routeLengthList.add(routeLength);
//            newRoute.setHops(newRoute.getLinklist().size());
//            routeList.add(newRoute);
//            if (++routeNum==k){
//                break;
//            }
//        }
//        return routeList;
//
//    }
//}
