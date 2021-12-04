package routeSearch;

import network.*;
import java.util.*;
import general.*;

public class Dijkstra {

    //dijkstra最短路由算法。找到最短路由，并设置光路跳数
    public void dijkstra(Node srcNode, Node desNode, Layer layer,Lightpath lightpath) {


        ArrayList<Node>visitedNodelist=new ArrayList<>();
        HashMap<String,Node>map=layer.getNodelist();

        //将map中的节点初始化
        for (String s : map.keySet()) {
            Node node = map.get(s);    //创建节点对象，指向map中正在迭代的节点元素
            node.setParentNode(null);
            node.setLength_from_src(Constant.infinite);
            node.setStatus(Constant.unvisited);
        }

        //声明中间节点,初始赋值为srcNode
        Node currentNode=srcNode;
        currentNode.setLength_from_src(0);
        currentNode.setStatus(Constant.visitedTwice);


/*
 *  算法思想：
 *  	取已访问相邻节点访问集合中距离源点最近的节点作为中间点，当中间点不为终点时，将中间点从已访问节点集合中删除。
 *
 *      第一次遍历时，中间点的所有相邻节点状态均为未访问，遍历将所有相邻节点添加到已访问相邻节点集合中
 *      接下来再遍历，中间点的所有相邻节点状态均为访问一次，比较所有相邻节点到源点的距离>中间点到源点的距离+link的长度？取较小值
 *      遍历结束，令中间节点等于相邻节点集合中距离源点最近的节点。
 *      继续遍历中间节点的相邻节点集合，一步步更新中间节点，直到中间节点等于源点
 *
 *      第一次遍历之后，接下来的遍历都是更新节点到终点的距离，一次更新后，令中间点等于更新后节点中距离源点最近的节点，并通过新的中间点
 *      再次更新节点到源点的距离，经过多次更新之后使得中间点等于终点，循环结束。
 */

        while(currentNode!=desNode) {
            visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除

            for(Node node:currentNode.getNeinodelist()) {	//当currentNode不为desNode时，遍历currentNode节点的相邻节点集合
                Link link=layer.findLink(currentNode, node);		//link指向currentNode节点到相邻节点的链接
                if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的距离
                    node.setLength_from_src(currentNode.getLength_from_src()+link.getLength());
                    node.setStatus(Constant.visitedOnce);
                    visitedNodelist.add(node);
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
            currentNode=this.getShortestLengthNode(visitedNodelist);
        }

        //当currentNode=desNode时，while结束，输出结果

//        System.out.print("["+srcNode.getName()+","+desNode.getName()+"]  ");   //输出节点对
        Node currentNode1=desNode;
        int distance=0;
        lightpath.setViaLink(currentNode1.getName()+lightpath.getViaLink());
        while (currentNode1!=srcNode){
            Link link= layer.findLink(currentNode1,currentNode1.getParentNode());
            lightpath.getLplink().add(link);
            lightpath.setHops(lightpath.getHops()+1);   //光路的跳数
            distance=distance+link.getLength();
            currentNode1=currentNode1.getParentNode();
            lightpath.setViaLink(currentNode1.getName()+"-"+lightpath.getViaLink());
        }
        desNode.setLength_from_src(distance);

//        System.out.println(desNode.getLength_from_scr()+"   ");     //输出距离
//        this.assignWavelength(srcNode,desNode,layer);
    }


    //找到节点对之间的最短距离
    public int shortestLength(Node srcNode,Node desNode,Layer layer){
        ArrayList<Node>visitedNodelist=new ArrayList<>();
        HashMap<String,Node>map=layer.getNodelist();

        //将map中的节点初始化
        for (String s : map.keySet()) {
            Node node = map.get(s);    //创建节点对象，指向map中正在迭代的节点元素
            node.setParentNode(null);
            node.setLength_from_src(Constant.infinite);
            node.setStatus(Constant.unvisited);
        }

        //声明中间节点,初始赋值为srcNode
        Node currentNode=srcNode;
        currentNode.setLength_from_src(0);
        currentNode.setStatus(Constant.visitedTwice);

        while(currentNode!=desNode) {
            visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除

            for(Node node:currentNode.getNeinodelist()) {	//当currentNode不为desNode时，遍历currentNode节点的相邻节点集合
                Link link=layer.findLink(currentNode, node);		//link指向currentNode节点到相邻节点的链接
                if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的距离
                    node.setLength_from_src(currentNode.getLength_from_src()+link.getLength());
                    node.setStatus(Constant.visitedOnce);
                    visitedNodelist.add(node);
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
            currentNode=this.getShortestLengthNode(visitedNodelist);
        }

        //当currentNode=desNode时，while结束，输出结果

        Node currentNode1=desNode;
        int distance=0;
        while (currentNode1!=srcNode){
            Link link= layer.findLink(currentNode1,currentNode1.getParentNode());
            distance=distance+link.getLength();
            currentNode1=currentNode1.getParentNode();
        }

        return distance;
    }


    //找到最小cost的路由
    public void findMinCostRoute(Node srcNode, Node desNode, Layer layer,Lightpath lightpath) {

        ArrayList<Node>visitedNodelist=new ArrayList<>();
        HashMap<String,Node>map=layer.getNodelist();

        //将map中的节点初始化
        for (String s : map.keySet()) {
            Node node = map.get(s);    //创建节点对象，指向map中正在迭代的节点元素
            node.setParentNode(null);
            node.setCost_from_src(Constant.infinite);   //初始化节点到源点的cost为无穷大
            node.setStatus(Constant.unvisited);
        }

        //声明中间节点,初始赋值为srcNode
        Node currentNode=srcNode;
        currentNode.setCost_from_src(0);
        currentNode.setStatus(Constant.visitedTwice);


        while(currentNode!=desNode) {
            visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除

            for(Node node:currentNode.getNeinodelist()) {	//当currentNode不为desNode时，遍历currentNode节点的相邻节点集合
                Link link=layer.findLink(currentNode, node);		//link指向currentNode节点到相邻节点的链接
                if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的cost
                    node.setCost_from_src(currentNode.getCost_from_src()+link.getCost());
                    node.setStatus(Constant.visitedOnce);
                    visitedNodelist.add(node);
                    node.setParentNode(currentNode);
                }

                else if(node.getStatus()==Constant.visitedOnce) {	//之后再遍历，更新currentNode节点相邻节点到源点的cost
                    if(node.getCost_from_src()>currentNode.getCost_from_src()+link.getCost()) {
                        node.setCost_from_src(currentNode.getCost_from_src()+link.getCost());
                        node.setLength_from_src(currentNode.getLength_from_src()+ link.getLength());
                        node.setParentNode(currentNode);
                    }
                }
            }
            currentNode=this.getMinimalCostNode(visitedNodelist);
        }

//        System.out.print("["+srcNode.getName()+","+desNode.getName()+"]  ");   //输出节点对

        Node currentNode1=desNode;
        int distance=0;
        lightpath.setViaLink(currentNode.getName()+lightpath.getViaLink());
        while (currentNode1!=srcNode){
            Link link= layer.findLink(currentNode1,currentNode1.getParentNode());
            lightpath.getLplink().add(link);
            lightpath.setHops(lightpath.getHops()+1);   //光路的跳数
            distance=distance+link.getLength();
            currentNode1=currentNode1.getParentNode();
            lightpath.setViaLink(currentNode1.getName()+"-"+lightpath.getViaLink());
        }
        desNode.setLength_from_src(distance);

    }

    /*
    找到k条最短路由
     */
    public ArrayList<Route> findShortestRouteList(Nodepair nodepair, Layer layer, int k){
        Node srcNode=nodepair.getSrcNode();
        Node desNode=nodepair.getDesNode();
        ArrayList<Node>visitedNodelist=new ArrayList<>();
        HashMap<String,Node>map=layer.getNodelist();
        ArrayList<Route>routelist=new ArrayList<>();

        LinkedHashMap<String ,Nodepair>nodepairlist= layer.getNodepairlist();   //得到节点对列表
        for (Nodepair nodepair1:nodepairlist.values()) {
            Node src=nodepair1.getSrcNode();
            Node des=nodepair1.getDesNode();
            Link link= layer.findLink(src,des);
        }

        ArrayList<Integer>lengthList=new ArrayList<>();

        int route_num=0;
        Loop:  while (true){
            //将map中的节点初始化
            for (String s : map.keySet()) {
                Node node = map.get(s);    //创建节点对象，指向map中正在迭代的节点元素
                node.setParentNode(null);
                node.setLength_from_src(Constant.infinite);
                node.setStatus(Constant.unvisited);
            }

            //声明中间节点,初始赋值为srcNode
            Node currentNode=srcNode;
            currentNode.setLength_from_src(0);
            currentNode.setStatus(Constant.visitedTwice);

            visitedNodelist.clear();

            while(currentNode!=desNode) {
                visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除

                if (currentNode==null) break Loop;

                for(Node node:currentNode.getNeinodelist()) {	//当currentNode不为desNode时，遍历currentNode节点的相邻节点集合

                    Link link=layer.findLink(currentNode, node);		//link指向currentNode节点到相邻节点的链接

                    if (node==desNode&&lengthList.contains(currentNode.getLength_from_src()+ link.getLength())){
                        continue;
                    }

                    if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的cost
                        node.setLength_from_src(currentNode.getLength_from_src()+ link.getLength());
                        node.setStatus(Constant.visitedOnce);
                        visitedNodelist.add(node);
                        node.setParentNode(currentNode);
                    }

                    else if(node.getStatus()==Constant.visitedOnce) {	//之后再遍历，更新currentNode节点相邻节点到源点的距离
                        if(node.getLength_from_src()>currentNode.getLength_from_src()+link.getLength()) {
                            node.setLength_from_src(currentNode.getLength_from_src()+ link.getLength());
                            node.setParentNode(currentNode);
                        }
                    }
                }
                currentNode=this.getMinimalCostNode(visitedNodelist);
            }


            Route newRoute=new Route("",0,Constant.Free);

            Node currentNode1=desNode;
            newRoute.setLength(0);
            newRoute.setName(currentNode1.getName()+newRoute.getName());
            while (currentNode1!=srcNode){
                Link link=layer.findLink(currentNode1,currentNode1.getParentNode());
                newRoute.setLength(newRoute.getLength()+link.getLength());
                newRoute.getLinklist().add(link);
                currentNode1=currentNode1.getParentNode();
                newRoute.setName(currentNode1.getName()+"-"+newRoute.getName());
            }
            routelist.add(newRoute);

            nodepair.setRouteList(routelist);

//            System.out.print("["+srcNode.getName()+","+desNode.getName()+"]  cost:"+desNode.getRoutecost_from_src()+"  ");   //输出节点对
//            System.out.print("length:"+desNode.getLength_from_src()+"  ");
//            this.findvialink(srcNode,desNode);
            lengthList.add(desNode.getLength_from_src());

            //输出路由的距离
//            System.out.println(nodepair.getName().replace("]",",")+route_num+"] "+newRoute.getLength());

//            System.out.print("set Route"+nodepair.getName().replace("]",",")+route_num+"]:=");
//            this.findvialink(srcNode,desNode);
//            System.out.println();

            if (++route_num==k){
                break;
            }

        }

        return routelist;

    }


    /*
    寻找k条最小cost路由
     */
//    public ArrayList<Route> findGroomingRouteList(Nodepair nodepair, Layer layer, int k){
//        Node srcNode=nodepair.getSrcNode();
//        Node desNode=nodepair.getDesNode();
//        ArrayList<Node>visitedNodelist=new ArrayList<>();
//        HashMap<String,Node>map=layer.getNodelist();
//        ArrayList<Route>routelist=new ArrayList<>();
//
//        LinkedHashMap<String ,Nodepair>nodepairlist= layer.getNodepairlist();   //得到节点对列表
//        for (Nodepair nodepair1:nodepairlist.values()) {
//            Node src=nodepair1.getSrcNode();
//            Node des=nodepair1.getDesNode();
//            Link link= layer.findLink(src,des);
//            if (link!=null) link.setRoutecost(0);
//        }
//
//
//        ArrayList<Integer>lengthList=new ArrayList<>();
//
//        int route_num=0;
//        int i=0;
//        Loop:  while (true){
//
//            //将map中的节点初始化
//            for (String s : map.keySet()) {
//                Node node = map.get(s);    //创建节点对象，指向map中正在迭代的节点元素
//                node.setParentNode(null);
//                node.setCost_from_src(Constant.infinite);
//                node.setRoutecost_from_src(Constant.infinite);
//                node.setLength_from_src(Constant.infinite);
//                node.setStatus(Constant.unvisited);
//            }
//
//            //声明中间节点,初始赋值为srcNode
//            Node currentNode=srcNode;
//            currentNode.setCost_from_src(0);
//            currentNode.setRoutecost_from_src(0);
//            currentNode.setLength_from_src(0);
//            currentNode.setStatus(Constant.visitedTwice);
//
//            visitedNodelist.clear();
//
//            while(currentNode!=desNode) {
//                visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除
//
//                if (currentNode==null) break Loop;
//
//                for(Node node:currentNode.getNeinodelist()) {	//当currentNode不为desNode时，遍历currentNode节点的相邻节点集合
//
//                    Link link=layer.findLink(currentNode, node);		//link指向currentNode节点到相邻节点的链接
//
//                    //以路由的长度来判断该路由是否已存在于路由列表中
//                    if (node==desNode&&lengthList.contains(currentNode.getLength_from_src()+ link.getLength())){
//                        continue;
//                    }
//
//                    if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的cost
//                        node.setRoutecost_from_src(currentNode.getRoutecost_from_src()+link.getRoutecost());
//                        node.setCost_from_src(currentNode.getCost_from_src()+ link.getCost());
//                        node.setLength_from_src(currentNode.getLength_from_src()+ link.getLength());
//                        node.setStatus(Constant.visitedOnce);
//                        visitedNodelist.add(node);
//                        node.setParentNode(currentNode);
//                    }
//
//                    else if(node.getStatus()==Constant.visitedOnce) {	//之后再遍历，更新currentNode节点相邻节点到源点的距离
//                        if(node.getCost_from_src()>currentNode.getCost_from_src()+link.getCost()) {
//
//                            //尽量寻找拓扑中未被使用的链路
////                        if(node.getRoutecost_from_src()>currentNode.getRoutecost_from_src()+link.getRoutecost()) {
//
//                            node.setRoutecost_from_src(currentNode.getRoutecost_from_src()+link.getRoutecost());
//                            node.setCost_from_src(currentNode.getCost_from_src()+ link.getCost());
//                            node.setLength_from_src(currentNode.getLength_from_src()+ link.getLength());
//                            node.setParentNode(currentNode);
//                        }
//                    }
//                }
//                currentNode=this.getMinimalCostNode(visitedNodelist);
//            }
//
//
//            Route newRoute=new Route("",0,Constant.Free);
//
//            Node currentNode1=desNode;
//            while (currentNode1!=srcNode){
//                if (srcNode.getNeinodelist().contains(currentNode1)){
//                    currentNode1.setUsed(1);    //当该节点是源点的直接相邻节点时，设置为1，代表该节点已经使用过
//                }
//                Link link=layer.findLink(currentNode1,currentNode1.getParentNode());
//                link.setRoutecost(link.getRoutecost()+1);
//                newRoute.setRemCapacity(newRoute.getRemCapacity()+ link.getRemCapacity());  //路由的剩余资源
//                newRoute.setLoad(newRoute.getLoad()+link.getChannels());    //路由的负载，即占用的总光通道数
//                newRoute.getLinklist().add(link);
//                if (newRoute.getMaxWaveNum()< link.getChannels()){          //找到路由的最大波长数
//                    newRoute.setMaxWaveNum(link.getChannels());
//                }
//                currentNode1=currentNode1.getParentNode();
//            }
//            routelist.add(newRoute);
//
////            System.out.print("["+srcNode.getName()+","+desNode.getName()+"]  cost:"+desNode.getCost_from_src()+"  ");   //输出节点对
////            System.out.print("length:"+desNode.getLength_from_src()+"  ");
////            this.findvialink(srcNode,desNode);
//            lengthList.add(desNode.getLength_from_src());
//
////            System.out.println();
//
//            if (++route_num==k){
//                break;
//            }
//
//        }
//        return routelist;
//
//    }


    /*寻找IP层上跳数最小的虚拟链路路由满足grooming，不考虑光层*/
    public ArrayList<Route> findLeastHopsRoute(Nodepair nodepair, Layer layer, int k, Gv gv){
        Node srcNode=nodepair.getSrcNode();
        Node desNode=nodepair.getDesNode();
        ArrayList<Node>visitedNodelist=new ArrayList<>();
        HashMap<String,Node>map=layer.getNodelist();

        layer.generateIPNeiNodepairs(gv);
        for (Vlink v:gv.getVlinks().values()) {
            v.setHop_in_IP(1);      //初始化所有IP层虚拟链路跳数为1
        }

        ArrayList<Route>routelist=new ArrayList<>();

        //删除剩余资源不满足业务大小的光路（虚拟链路）
        HashSet<Vlink> existlp = new HashSet<>(gv.getVlinks().values());
        existlp.removeIf(l -> l.getRemCapacity() < nodepair.getDemand());   //删除集合中剩余容量小于节点对之间业务需求的虚拟链路

        ArrayList<Integer>routeLengthList=new ArrayList<>();

        int route_num=0;

        Loop:  while (true){
            //将map中的节点初始化
            for (String s : map.keySet()) {
                Node node = map.get(s);    //创建节点对象，指向map中正在迭代的节点元素
                node.setParentNode(null);
                node.setCost_from_src(Constant.infinite);
                node.setLength_from_src(Constant.infinite);
                node.setStatus(Constant.unvisited);
            }

            //声明中间节点,初始赋值为srcNode
            Node currentNode=srcNode;
            currentNode.setCost_from_src(0);
            currentNode.setLength_from_src(0);
            currentNode.setStatus(Constant.visitedTwice);

            visitedNodelist.clear();
            int routeLength=0;     //路由长度，用来判断是否已经存在路由列表中

            while(currentNode!=desNode) {
                visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除

                if (currentNode==null) break Loop;

                for(Node node:currentNode.getIPneinodelist()) {	//当currentNode不为desNode时，遍历currentNode节点的相邻节点集合

                    Vlink vlink= gv.findVlink(currentNode,node);         //IP层上寻找路由都是针对虚拟链路

                    //当虚拟链路不满足满足流量疏导条件时，跳过此次循环
                    if (!existlp.contains(vlink)) continue ;

                    routeLength= currentNode.getLength_from_src()+vlink.getLength();
                    //以路由的长度来判断该路由是否已存在于路由列表中（手动定义虚拟链路的长度）
                    if (node==desNode&&routeLengthList.contains(routeLength)){
                        continue;
                    }

                    //根据剩余容量来寻找路由
                    if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的cost
                        node.setLength_from_src(currentNode.getLength_from_src()+ vlink.getLength());
                        node.setCost_from_src(currentNode.getCost_from_src()+ vlink.getHop_in_IP());  //cost定义为虚拟链接的剩余容量
                        node.setStatus(Constant.visitedOnce);
                        visitedNodelist.add(node);
                        node.setParentNode(currentNode);
                    }

                    else if(node.getStatus()==Constant.visitedOnce) {	//之后再遍历，更新currentNode节点相邻节点到源点的属性
                        if(node.getCost_from_src()>currentNode.getCost_from_src()+vlink.getHop_in_IP()) {
                            node.setLength_from_src(currentNode.getLength_from_src()+ vlink.getLength());
                            node.setCost_from_src(currentNode.getCost_from_src()+ vlink.getHop_in_IP());
                            node.setParentNode(currentNode);
                        }
                    }
                }
                currentNode=this.getMinimalCostNode(visitedNodelist);
            }

            Route newRoute=new Route("",0,Constant.Free);

            Node currentNode1=desNode;
            newRoute.setName(currentNode1.getName()+newRoute.getName());
            routeLength=0;
            while (currentNode1!=srcNode){
                Vlink vlink= gv.findVlink(currentNode1,currentNode1.getParentNode());
                newRoute.getVlinklist().add(vlink);

                routeLength+= vlink.getLength();

                currentNode1=currentNode1.getParentNode();
                newRoute.setName(currentNode1.getName()+"-"+newRoute.getName());
            }
            newRoute.setHops(newRoute.getVlinklist().size());
            routelist.add(newRoute);

//            System.out.print("["+srcNode.getName()+","+desNode.getName()+"]  cost:"+desNode.getCost_from_src()+"  ");   //输出节点对
//            System.out.print("length:"+desNode.getLength_from_src()+"  ");
//            this.findvialink(srcNode,desNode);
            routeLengthList.add(routeLength);

//            System.out.println();

            if (++route_num==k){
                break;
            }

        }

        return routelist;

    }


    /*在Gv中寻找满足流量疏导的路由(最短路径)*/
    public ArrayList<Route> findShortestRoute(Nodepair nodepair, Layer layer, int k, Gv gv){


        Node srcNode=nodepair.getSrcNode();
        Node desNode=nodepair.getDesNode();
        ArrayList<Node>visitedNodelist=new ArrayList<>();
        HashMap<String,Node>map=layer.getNodelist();

        layer.generateIPNeiNodepairs(gv);

        ArrayList<Route>routelist=new ArrayList<>();

        //删除剩余资源不满足业务大小的光路（虚拟链路）
        HashSet<Vlink> existlp = new HashSet<>(gv.getVlinks().values());
        existlp.removeIf(l -> l.getRemCapacity() < nodepair.getDemand());   //删除集合中剩余容量小于节点对之间业务需求的虚拟链路

        ArrayList<Integer>routeLengthList=new ArrayList<>();

        int route_num=0;

        Loop:  while (true){
            //将map中的节点初始化
            for (String s : map.keySet()) {
                Node node = map.get(s);    //创建节点对象，指向map中正在迭代的节点元素
                node.setParentNode(null);
                node.setCost_from_src(Constant.infinite);
                node.setLength_from_src(Constant.infinite);
                node.setStatus(Constant.unvisited);
            }

            //声明中间节点,初始赋值为srcNode
            Node currentNode=srcNode;
            currentNode.setCost_from_src(0);
            currentNode.setLength_from_src(0);
            currentNode.setStatus(Constant.visitedTwice);

            visitedNodelist.clear();

            int routeLength=0;     //路由长度，用来判断是否已经存在路由列表中

            while(currentNode!=desNode) {
                visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除

                if (currentNode==null) break Loop;

                for(Node node:currentNode.getIPneinodelist()) {	//当currentNode不为desNode时，遍历currentNode节点的相邻节点集合

                    Vlink vlink= gv.findVlink(currentNode,node);         //IP层上寻找路由都是针对虚拟链路

                    //当虚拟链路不满足满足流量疏导条件时，跳过此次循环
                    if (!existlp.contains(vlink)) continue ;


//                    double vlinkRemReciprocal= (double) 1/vlink.getRemCapacity();

                    routeLength=currentNode.getLength_from_src()+vlink.getLength();
                    //以路由的剩余容量来判断该路由是否已存在于路由列表中
                    if (node==desNode&&routeLengthList.contains(routeLength)){
                        continue;
                    }

                    //根据剩余容量来寻找路由
                    if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的cost
                        node.setLength_from_src(currentNode.getLength_from_src()+ vlink.getLength());

                        node.setCost_from_src(currentNode.getCost_from_src()+ vlink.getLength());  //cost定义为虚拟链接的剩余容量
//                        node.setCost_from_src(currentNode.getCost_from_src()+ vlinkRemReciprocal);  //虚拟链接剩余容量的倒数

                        node.setStatus(Constant.visitedOnce);
                        visitedNodelist.add(node);
                        node.setParentNode(currentNode);
                    }

                    else if(node.getStatus()==Constant.visitedOnce) {	//之后再遍历，更新currentNode节点相邻节点到源点的属性

//                        if(node.getCost_from_src()>currentNode.getCost_from_src()+vlinkRemReciprocal) {
                        if(node.getCost_from_src()>currentNode.getCost_from_src()+vlink.getLength()) {
//                            node.setCost_from_src(currentNode.getCost_from_src()+ vlinkRemReciprocal);
                            node.setCost_from_src(currentNode.getCost_from_src()+ vlink.getLength());

                            node.setLength_from_src(currentNode.getLength_from_src()+ vlink.getLength());
                            node.setParentNode(currentNode);
                        }
                    }
                }
                currentNode=this.getMinimalCostNode(visitedNodelist);
            }

            Route newRoute=new Route("",0,Constant.Free);

            Node currentNode1=desNode;
            newRoute.setName(currentNode1.getName()+newRoute.getName());
            routeLength=0;
            while (currentNode1!=srcNode){
                Vlink vlink= gv.findVlink(currentNode1,currentNode1.getParentNode());
                newRoute.getVlinklist().add(vlink);

                routeLength+= vlink.getLength();

                currentNode1=currentNode1.getParentNode();
                newRoute.setName(currentNode1.getName()+"-"+newRoute.getName());
            }
            newRoute.setLength(routeLength);
            newRoute.setHops(newRoute.getVlinklist().size());
            routelist.add(newRoute);

//            System.out.print("["+srcNode.getName()+","+desNode.getName()+"]  cost:"+desNode.getCost_from_src()+"  ");   //输出节点对
//            System.out.print("length:"+desNode.getLength_from_src()+"  ");
//            this.findvialink(srcNode,desNode);
            routeLengthList.add(routeLength);

//            System.out.println();

            if (++route_num==k){
                break;
            }

        }

        return routelist;

    }

    /*在Gv中寻找满足流量疏导的路由(考虑光层上的跳数)*/
//    public ArrayList<Route> findCrossLeastHopsRoute(Nodepair nodepair, Layer layer, int k, Gv gv){
//        Node srcNode=nodepair.getSrcNode();
//        Node desNode=nodepair.getDesNode();
//        ArrayList<Node>visitedNodelist=new ArrayList<>();
//        HashMap<String,Node>map=layer.getNodelist();
//
//        layer.generateIPNeiNodepairs(gv);
//
//
//        ArrayList<Route>routelist=new ArrayList<>();
//
//        //删除剩余资源不满足业务大小的光路（虚拟链路）
//        HashSet<Vlink> existlp = new HashSet<>(gv.getVlinks().values());
//        existlp.removeIf(l -> l.getRemCapacity() < nodepair.getDemand());   //删除集合中剩余容量小于节点对之间业务需求的虚拟链路
//
//        for (Vlink v:existlp) {
//            for (Lightpath l:v.getLightPathList()) {
//                if (l.getRemCapacity()>=nodepair.getDemand()){
//                    if (v.getHop()>l.getHops()){
//                        v.setHop(l.getHops());
//                    }
//                }
//            }
//        }
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
//                        node.setCost_from_src(currentNode.getCost_from_src()+ vlink.getHop());  //cost定义为虚拟链接的剩余容量
//                        node.setStatus(Constant.visitedOnce);
//                        visitedNodelist.add(node);
//                        node.setParentNode(currentNode);
//                    }
//
//                    else if(node.getStatus()==Constant.visitedOnce) {	//之后再遍历，更新currentNode节点相邻节点到源点的属性
//                        if(node.getCost_from_src()>currentNode.getCost_from_src()+vlink.getHop()) {
//                            node.setLength_from_src(currentNode.getLength_from_src()+ vlink.getLength());
//                            node.setCost_from_src(currentNode.getCost_from_src()+ vlink.getHop());
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

    /*在Gv中寻找满足流量疏导的路由(考虑剩余容量)*/
//    public ArrayList<Route> findGroomingRouteList(Nodepair nodepair, Layer layer, int k, LinkedList<Vlink>existVlinkList, Gv gv){
//        Node srcNode=nodepair.getSrcNode();
//        Node desNode=nodepair.getDesNode();
//        ArrayList<Node>visitedNodelist=new ArrayList<>();
//        HashMap<String,Node>map=layer.getNodelist();
//
//        HashMap<String,Vlink>hashMap=new HashMap<>();
//        for (Vlink v:existVlinkList) {
//            hashMap.put(v.getName(),v);
//        }
////        gv.setVlinks(existVlinkList);
//
//        layer.generateIPNeiNodepairs(gv);
//
//        ArrayList<Route>routelist=new ArrayList<>();
//
//        //删除剩余资源不满足业务大小的光路（虚拟链路）
//        HashSet<Vlink>existlp=new HashSet<>();
//        existlp.addAll(existVlinkList);
////        existlp.removeIf(l -> l.getRemCapacity() < nodepair.getDemand());
//
////        LinkedHashMap<String ,Nodepair>nodepairlist= layer.getNodepairlist();   //得到节点对列表
//        LinkedHashMap<String ,IPnodepair>nodepairlist= layer.getIPnodepairlist();   //得到IP节点对列表
//
//        ArrayList<Integer>lengthList=new ArrayList<>();
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
//
//            while(currentNode!=desNode) {
//                visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除
//
//                if (currentNode==null) break Loop;
//
//                for(Node node:currentNode.getIPneinodelist()) {	//当currentNode不为desNode时，遍历currentNode节点的相邻节点集合
//
////                    Link link=layer.findLink(currentNode, node);		//link指向currentNode节点到相邻节点的链接
//
//                    String vlinkName=srcNode.getName()+"-"+desNode.getName();
//                    Vlink vlink= gv.findVlink(srcNode,desNode);
//
//
//                    //当虚拟链路不满足满足流量疏导条件时，跳过此次循环
//                    if (!existlp.contains(vlink)) continue ;
//
//                    //以路由的长度来判断该路由是否已存在于路由列表中
//                    if (node==desNode&&lengthList.contains(currentNode.getLength_from_src()+ vlink.getLength())){
//                        continue;
//                    }
//
//                    //根据虚拟链路的最大剩余容量来寻找路由
//                    if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的cost
//                        node.setLength_from_src(currentNode.getLength_from_src()+ vlink.getLength());
//                        node.setCost_from_src(currentNode.getCost_from_src()+ vlink.getRemCapacity());
//                        node.setStatus(Constant.visitedOnce);
//                        visitedNodelist.add(node);
//                        node.setParentNode(currentNode);
//                    }
//
//                    else if(node.getStatus()==Constant.visitedOnce) {	//之后再遍历，更新currentNode节点相邻节点到源点的距离
//                        if(node.getCost_from_src()>currentNode.getCost_from_src()+vlink.getRemCapacity()) {
//                            node.setLength_from_src(currentNode.getLength_from_src()+ vlink.getLength());
//                            node.setCost_from_src(currentNode.getCost_from_src()+ vlink.getRemCapacity());
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
//
//            while (currentNode1!=srcNode){
//                String vlinkName=currentNode1.getName()+"-"+currentNode1.getParentNode().getName();
//                Vlink vlink= gv.findVlink(currentNode1,currentNode1.getParentNode());
//                newRoute.getVlinklist().add(vlink);
//
////                newRoute.setLinklist(vlink.getLinks());
//
//                currentNode1=currentNode1.getParentNode();
//                newRoute.setName(currentNode1.getName()+"-"+newRoute.getName());
//            }
//            routelist.add(newRoute);
//
////            System.out.print("["+srcNode.getName()+","+desNode.getName()+"]  cost:"+desNode.getCost_from_src()+"  ");   //输出节点对
////            System.out.print("length:"+desNode.getLength_from_src()+"  ");
////            this.findvialink(srcNode,desNode);
//            lengthList.add(desNode.getLength_from_src());
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


    //找到已访问节点集合中距离源点最近的点
    public Node getShortestLengthNode(ArrayList<Node>visitedNodeList) {
        Node currentNode=null;
        int distant=Integer.MAX_VALUE;
        for(Node node:visitedNodeList) {
            if(node.getLength_from_src()<distant) {
                distant=node.getLength_from_src();
                currentNode=node;
            }
        }
        return currentNode;
    }

    //找到已访问节点集合中距离源点cost最小的点
    public Node getMinimalCostNode(ArrayList<Node>visitedNodelist) {
        Node currentNode=null;
        double cost=Integer.MAX_VALUE;
        for(Node node:visitedNodelist) {
            if(node.getCost_from_src()<cost) {
                cost=node.getCost_from_src();
                currentNode=node;
            }
        }
        return currentNode;
    }




    /*
     * 找到最小跳数的路由
     */
//    public Route findWavePlaneRoute(Node srcNode, Node desNode, Layer layer, HashSet<Link>exitlinklist) {
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
//            node.setCost_from_src(Constant.infinite);
//        }
//
//        //声明中间节点,初始赋值为srcNode
//        Node currentNode=srcNode;
//        currentNode.setStatus(Constant.visitedTwice);
//        currentNode.setLength_from_src(0);
//        currentNode.setCost_from_src(0);
//
//        int routeLength=0;
//
//        while(currentNode!=desNode) {
//            visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除
//
//            if (currentNode==null){
//                return null;
////                break ;
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
//                routeLength= currentNode.getLength_from_src()+link.getLength();
//
//                if(node.getStatus()==Constant.unvisited) {		//第一次遍历，初始所有currentNode节点相邻节点到源点的距离
//                    node.setLength_from_src(currentNode.getLength_from_src()+link.getLength());
//                    node.setCost_from_src(currentNode.getCost_from_src()+link.getCost());
//                    node.setStatus(Constant.visitedOnce);
//                    visitedNodelist.add(node);
//                    node.setParentNode(currentNode);
//                }
//
//                else if(node.getStatus()==Constant.visitedOnce) {	//之后再遍历，更新currentNode节点相邻节点到源点的距离
//                    if(node.getLength_from_src()>currentNode.getLength_from_src()+link.getLength()) {
////                        if(node.getCost_from_src()>currentNode.getCost_from_src()+link.getCost()) {
//                        node.setLength_from_src(currentNode.getLength_from_src()+link.getLength());
//                        node.setCost_from_src(currentNode.getCost_from_src()+link.getCost());
//                        node.setParentNode(currentNode);
//                    }
//                }
//
//            }
//            //遍历结束，在已访问节点集合中找到距离源点最短的节点，赋给currentNode节点
////                currentNode=this.getShortestLengthNode(visitedNodelist);
//            currentNode=this.getMinimalCostNode(visitedNodelist);
//        }
//
//        //当currentNode=desNode时，while结束，输出结果
//        Route newRoute=new Route("",0,Constant.Free);
//
//        Node currentNode1=desNode;
//        newRoute.setName(currentNode1.getName()+newRoute.getName());
//        routeLength=0;
//        while (currentNode1!=srcNode){
//            Link link= layer.findLink(currentNode1,currentNode1.getParentNode());
//            newRoute.getLinklist().add(link);
//            routeLength=routeLength+link.getLength();
//            currentNode1=currentNode1.getParentNode();
//            newRoute.setName(currentNode1.getName()+"-"+newRoute.getName());
//        }
//        newRoute.setLength(routeLength);
//        newRoute.setHops(newRoute.getLinklist().size());
//
//        return newRoute;
//
//    }



    /*
     * First-Fit波长分配
     */
    public void AssignWavelength(Lightpath lightpath) {

        Wavelength wave=new Wavelength();
        LinkedList<Integer>List= availableWavelengthList(wave, lightpath);
        lightpath.setAvaWavelist(List);        //设置光路可用波长集合为List

        for (int i = 0; i < lightpath.getChannels(); i++) {            
            int w = List.getFirst();                    //w为光路所分配波长
            lightpath.setOccupyWavelengthIndex(w);
            for (Link link : lightpath.getLplink()) {
                link.getWavelengthList().add(w);        //链路赋予波长
                link.getAvaWavelist().remove(w);
                link.setCost(link.getCost()+1);         //链路分配一个波长，cost值加1
            }
            List.removeFirst();     //分配后在可用波长集合中删除已分配波长
        }

    }


    //考虑链路上的波长一致性，得到可用波长集合
    public static LinkedList<Integer> availableWavelengthList(Wavelength wavelength, Lightpath lightpath) {
        LinkedList<Integer> List = new LinkedList<>();
        for (int l = 0; l < wavelength.getWaveTotalNumbers(); l++) {		//List初始化为总波长集合
            List.add(l);
        }

        for (Link link:lightpath.getLplink()) {
            LinkedList<Integer>linkAvaWaveList=new LinkedList<>();
            for (int i = 0; i < wavelength.getWaveTotalNumbers(); i++) {        //当链路已占用波长i时，在链路可用波长集合中删除该波长
                if (link.getWavelengthList() == null) {
                    break;
                } else if (link.getWavelengthList().contains(i)) {
                    linkAvaWaveList.add(i);              //linkAvaWavelist为链路已占用波长集合
//                    link.getAvaWavelist().remove(i);    //获得链路可用波长集合
                }
            }
            List.removeAll(linkAvaWaveList);					//总波长集合List中删除链路已占用波长
        }
        return List;
    }



    //找到途经光路上的链路集合
    public HashSet<Link> getVialinklist(Node srcNode,Node desNode,Layer layer){
        HashSet<Link>viaLink=new HashSet<>();

        ArrayList<Node>routelist=new ArrayList<>();
        Node currentNode=desNode;
        while (currentNode!=srcNode){
            int i=routelist.size();
            routelist.add(i,currentNode);				//路由列表
            Link link= layer.findLink(currentNode,currentNode.getParentNode());
            viaLink.add(link);          //将该链路添加到途经链路集合中
            currentNode=currentNode.getParentNode();       //找到上一个经过的节点
        }
        /*输出最短路径*/
        int num=routelist.size();
        System.out.print(srcNode.getName());
        while (num-->0){
            System.out.print("->"+routelist.get(num).getName());
        }

        return viaLink;
    }


    /*找到最短路由途经路径*/
    public void findvialink(Node srcNode, Node desNode){

        ArrayList<Node>routelist=new ArrayList<>();
        Node currentNode=desNode;
        while (currentNode!=srcNode){
            int i=routelist.size();
            routelist.add(i,currentNode);				//路由列表
            currentNode=currentNode.getParentNode();       //找到上一个经过的节点
        }


        /*First Fit 波长分配算法*/
//        Node currentNode1=desNode;
//        Wavelength wave=new Wavelength();
//        String lightpathname=srcNode.getName()+"--"+desNode.getName();	   //光路名字
//        Lightpath lightpath=new Lightpath(lightpathname);
//
//        LinkedList<Integer> List = new LinkedList<>();
//        for (int l = 0; l < wave.getWaveSum(); l++) {		//List初始化为总波长集合
//            List.add(l);
//        }
//        while (currentNode1 != srcNode) {
//            Link link = layer.findLink(currentNode1, currentNode1.getParentNode());    //从终点开始反向寻找最短路由途经链路
//            lightpath.getLplink().add(link);                 //添加链路到光路中
//            LinkedList<Integer>linkWavelist=new LinkedList<>();
//            for (int i = 0; i < wave.getWaveSum(); i++) {        //当链路已占用波长i时，在链路可用波长集合中删除该波长
//                if (link.getWavelengthList() == null) {
//                    break;
//                } else if (link.getWavelengthList().contains(i)) {	//linkWavelist为链路已占用波长集合
//                    linkWavelist.add(i);
//                }
//            }
//            link.getAvaWavelist().removeAll(linkWavelist);		//获得链路可用波长集合
//            List.removeAll(linkWavelist);					//总波长集合List中删除链路已占用波长
//
//            currentNode1 = currentNode1.getParentNode();
//        }

        /*输出光路占据链路*/
//        System.out.print("光路占据链路：");
//        for (int i=0;i<lightpath.getLplink().size();i++){
//            System.out.print(lightpath.getLplink().get(i).getName()+"   ");
//        }

//        lightpath.setAvaWavelist(List);        //设置光路可用波长集合为List
//        int w=List.getFirst();					//w为光路所分配波长
//
//        Node currentNode2=desNode;
//        while (currentNode2!=srcNode){
//            Link link=layer.findLink(currentNode2,currentNode2.getParentNode());
//            link.getWavelengthList().add(w);			//更新链路占用波长
//            currentNode2=currentNode2.getParentNode();
//        }


        /*输出最短路径*/
        int num=routelist.size();
//        System.out.print(srcNode.getName());
//        while (num-->0){
//            System.out.print("->"+routelist.get(num).getName());
//        }


        /* 反向输出最短路由路径途经节点对 */
//        int num1= routelist.size();
//        System.out.print("("+desNode.getName());
//        if (num1==1){
//            System.out.print(","+srcNode.getName()+")");
//        }else {
//            while (num-->0){
//                if (num!=0){
//                    System.out.print(","+routelist.get(num1-num).getName()+")");
//                }else
//                    System.out.print(","+srcNode.getName()+")");
//                if (num!=0){
//                    System.out.print(",("+routelist.get(num1-num).getName());
//                }
//            }
//        }
//        System.out.println(";");

        /*输出最短路由路径途经节点对 */
        System.out.print("("+srcNode.getName());
        while (num-->0){
            System.out.print(","+routelist.get(num).getName()+")");	//输出最短路由的途经路径
            if ((num)!=0){
                System.out.print(",("+routelist.get(num).getName());
            }
        }
        System.out.println(";");

        /*输出光路所占用波长*/
//        System.out.println("    "+lightpath.getName()+"最短路径所占用波长为: "+w+"  ");
    }



    /*没有使用的方法*/
//    public ArrayList<Route> findClosestNode(Node srcNode, Node desNode, Layer layer, HashSet<Link>exitlinklist) {
//
//        HashMap<String,Node>map=layer.getNodelist();
//        ArrayList<Node>visitedNodelist=new ArrayList<>();
//
//
//        Loop:while (true){
//
//            //将map中的节点初始化
//            for (String a : map.keySet()) {
//                Node node = map.get(a);    //创建节点对象，指向map中正在迭代的节点元素
//                node.setParentNode(null);
//                node.setStatus(Constant.unvisited);
//                node.setLength_from_src(Constant.infinite);
//            }
//
//            //声明中间节点,初始赋值为srcNode
//            Node currentNode=srcNode;
//            currentNode.setLength_from_src(0);
//            currentNode.setCost_from_src(0);
//            currentNode.setStatus(Constant.visitedTwice);
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
//                    if (node.getStatus() == Constant.unvisited) {        //第一次遍历，初始所有currentNode节点相邻节点到源点的距离
//                        visitedNodelist.add(node);
//                        node.setLength_from_src(currentNode.getLength_from_src() + link.getLength());
//                        node.setStatus(Constant.visitedOnce);
//                        node.setParentNode(currentNode);
//                    } else if (node.getStatus() == Constant.visitedOnce) {    //之后再遍历，更新currentNode节点相邻节点到源点的距离
//                        if (node.getLength_from_src() > currentNode.getLength_from_src() + link.getLength()) {
//                            node.setLength_from_src(currentNode.getLength_from_src() + link.getLength());
//                            node.setParentNode(currentNode);
//                        }
//                    }
//
//                }
//                //遍历结束，在已访问节点集合中找到距离源点最小的节点，赋给currentNode节点
//                currentNode=this.getShortestLengthNode(visitedNodelist);
//            }
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

    public void findMinCostNode(Node srcNode, Node desNode, Layer layer) {

        HashMap<String,Node>map=layer.getNodelist();
        ArrayList<Node>visitedNodelist=new ArrayList<>();

        //将map中的节点初始化
        for (String a : map.keySet()) {
            Node node = map.get(a);    //创建节点对象，指向map中正在迭代的节点元素
            node.setParentNode(null);
            node.setStatus(Constant.unvisited);
            node.setCost_from_src(Constant.infinite);
        }

        //声明中间节点,初始赋值为srcNode
        Node currentNode=srcNode;
        currentNode.setStatus(Constant.visitedTwice);
        currentNode.setCost_from_src(0);


        while(currentNode!=desNode) {
            visitedNodelist.remove(currentNode);		//currentNode节点当过中间点后，将其从已访问节点集合中删除

            if (currentNode==null){
                System.out.println("=================================================================================");
                System.out.println(srcNode.getName()+"-"+desNode.getName()+"    已有链路无法满足节点对之间的路由！");
                System.out.println("=================================================================================");
                break;
            }

            for (Node node : currentNode.getNeinodelist()) {
                Link link = layer.findLink(currentNode, node);        //link指向currentNode节点到相邻节点的链接

                if (node.getStatus() == Constant.unvisited) {        //第一次遍历，初始所有currentNode节点相邻节点到源点的距离
                    visitedNodelist.add(node);
                    node.setCost_from_src(currentNode.getCost_from_src() + link.getCost());
                    node.setStatus(Constant.visitedOnce);
                    node.setParentNode(currentNode);
                } else if (node.getStatus() == Constant.visitedOnce) {    //之后再遍历，更新currentNode节点相邻节点到源点的距离
                    if (node.getCost_from_src() > currentNode.getCost_from_src() + link.getCost()) {
                        node.setCost_from_src(currentNode.getCost_from_src() + link.getCost());
                        node.setParentNode(currentNode);
                    }
                }

            }
            //遍历结束，在已访问节点集合中找到距离源点最小的节点，赋给currentNode节点
            currentNode=this.getMinimalCostNode(visitedNodelist);
        }
        //当currentNode=desNode时，while结束，输出结果

        if (currentNode!=null){
            Node currentNode1=desNode;
            int distance=0;
            while (currentNode1!=srcNode){
                Link link= layer.findLink(currentNode1,currentNode1.getParentNode());
                distance=distance+link.getLength();
                currentNode1=currentNode1.getParentNode();
            }
            desNode.setLength_from_src(distance);
            this.getVialinklist(srcNode, desNode, layer);
        }

    }

}


