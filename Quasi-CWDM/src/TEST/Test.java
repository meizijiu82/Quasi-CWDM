package TEST;

import java.util.*;

import network.*;
import routeSearch.*;
import general.*;


public class Test {

    public static void main(String[] args) {
        System.out.println("*************** readTopology **************");
        Layer layer=new Layer("Mylayer",0);
        layer.readTopology("6.csv");
        layer.generateNodepairs();
        LinkedHashMap<String ,Nodepair> nodepairlist= layer.getNodepairlist();   //得到节点对列表
        Iterator<String> iter=nodepairlist.keySet().iterator(); //节点对集合迭代



        /*输出物理链路集合*/
//        HashMap<String,Node>map=layer.getNodelist();    //map为层中节点集合
//        HashMap<String,Node>map2=layer.getNodelist();
//        Iterator iter2=map.keySet().iterator();
////        System.out.println("set E:=");
//        while (iter2.hasNext()){
//            Node node= map2.get(iter2.next());
//            int i=0;
//            int size=node.getNeinodelist().size();
//            //相邻节点集合
//            System.out.print("set Ni["+node.getName()+"]:=");
////            System.out.print("("+node.getName()+",*) ");
//            while (i<size){
//                System.out.print(node.getNeinodelist().get(i).getName()+" ");
//                i++;
//            }
//            System.out.println(";");
//        }

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

        HashSet<Integer>list=new HashSet<>();

        Dijkstra dijkstra=new Dijkstra();
        for (Nodepair nodepair: nodepairlist.values()) {
            int sum=0;
            for (int i = 0; i < nodepair.getServiceList().size(); i++) {
//                System.out.println(nodepair.getName().replace("]",",")+i+"] "+nodepair.getServiceList().get(i));
                sum+=nodepair.getServiceList().get(i);
            }
            System.out.println(nodepair.getName()+" "+sum);
            Node srcNode= nodepair.getSrcNode();
            Node desNode= nodepair.getDesNode();
            Lightpath l=new Lightpath(srcNode.getName());
////            System.out.print("set Route"+nodepair.getName()+":=");

            ArrayList<Route>routes=dijkstra.findShortestRouteList(nodepair,layer,3);

            for (Route r:routes){
//                if(r.getLength()>10000)
//                    continue;
//                System.out.println(r.getName()+" "+r.getLength());
//                System.out.println(r.getLength());
//                list.add(r.getLength());
//                for (Link link: r.getLinklist()) {
//                    System.out.print(link.getSrcNode().getName()+link.getDesNode().getName()+",");
//                }
            }
//            dijkstra.dijkstra(srcNode,desNode,layer,l);
//            dijkstra.findvialink(srcNode,desNode);
        }
        list.add(3410*3);
        list.add(4268*3);
        list.add(3240*3);
        list.add(3488*3);
        list.add(3344*3);
        list.add(3096*3);
        list.add(3146*3);
        list.add(3122*3);
//        System.out.println(3410*3);
//        System.out.println(4268*3);
//        System.out.println(3240*3);
//        System.out.println(3488*3);
//        System.out.println(3344*3);
//        System.out.println(3096*3);
//        System.out.println(3146*3);
//        System.out.println(3122*3);


//        for (Integer i:list) {
//            System.out.println(i);
////            System.out.print(i+"  ");
////            if (i>10000) {
////                System.out.println(0);
////            }
////            else {
////                System.out.println(PS.get(i));
////            }
//        }
    }

}


