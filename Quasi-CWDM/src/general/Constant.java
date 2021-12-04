package general;

public class Constant {

    //全局变量（常量），只有被定义时能够赋值，其他所有地方都无法更改变量值
    public static final int unvisited=0;
    public final static int visitedOnce=1;
    public final static int visitedTwice=2;
    public final static int infinite=Integer.MAX_VALUE;
    public final static int Working=1;
    public final static int Free=0;
    public final static int lowBound=50;
    public final static int bound=51;
    public final static int hop = 4;                //6节点网络中流量疏导时的跳数限制(不考虑单跳多跳业务分开建立的情况)
    public final static int seed = 3;
    public final static int serviceSum = 10;      //节点对之间的业务数
    public final static int shuffle = 10000;      //业务打乱次数：6节点10000次；14节点1000次
    public final static double threshold = 0.3;      //阈值

}
//final关键字
