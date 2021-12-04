package Quasi;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {


        //14节点网络
        /*
         * 用最短路由算法得到初始拓扑,在进行成本优化的同时使用最短路由算法
         */
//        Quasi_CWDM0 quasi_cwdm0=new Quasi_CWDM0();
//        quasi_cwdm0.Quasi_CWDM();        //光层：最短路由；IP层：最小跳数

//        Quasi_CWDM1 quasi_cwdm1 =new Quasi_CWDM1();
//        quasi_cwdm1.Quasi_CWDM();     //光层：自适应负载均衡路由；IP层：最小跳数

        //根据波平面算法
//        Quasi_CWDM2 quasi_cwdm2 =new Quasi_CWDM2();
//        quasi_cwdm2.Quasi_CWDM();




        //6节点网络
        /*
         * 用最短路由算法得到初始拓扑，在进行成本优化的同时使用最短路由算法
         */
        //不考虑负载均衡
//        Quasi_CWDM6 quasi_cwdm6 = new Quasi_CWDM6();
//        quasi_cwdm6.Quasi_CWDM();           //不考虑负载均衡

        //根据自适应路由算法
//        Quasi_CWDM7 quasi_cwdm7 =new Quasi_CWDM7();
//        quasi_cwdm7.Quasi_CWDM();

        //根据扩展波平面算法
//        Quasi_CWDM8 quasi_cwdm8=new Quasi_CWDM8();
//        quasi_cwdm8.Quasi_CWDM();



        /*
         * 根据模型优化启发式算法
         */
        //6节点
        BasedModelWavePlane b6=new BasedModelWavePlane();
        b6.Quasi_CWDM();


        //14节点
//        BasedModelWavePlane14Node b14=new BasedModelWavePlane14Node();
//        b14.Quasi_CWDM();


//        BasedModelShortest bs6=new BasedModelShortest();
//        bs6.Quasi_CWDM();



        //14节点，频谱间隔为400G
//        Quasi_CWDM3 quasi_cwdm3=new Quasi_CWDM3();
//        quasi_cwdm3.Quasi_CWDM();     //不考虑负载均衡

//        Quasi_CWDM4 quasi_cwdm4=new Quasi_CWDM4();
//        quasi_cwdm4.Quasi_CWDM();     //只考虑光层负载均衡

//        Quasi_CWDM5 quasi_cwdm5=new Quasi_CWDM5();
//        quasi_cwdm5.Quasi_CWDM();     //考虑双层负载均衡





    }

}
