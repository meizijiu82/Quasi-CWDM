package TEST;

import java.util.StringTokenizer;

public class StringTokenizerTest {
    public static void main(String[] args) {
        String s = "we are student ,and";   //创建一个字符串变量
        StringTokenizer fenxi = new StringTokenizer(s," ,");    //定义的分隔标志是空格或逗号
        int number = fenxi.countTokens();   //对象拥有多少个单词的值
        while(fenxi.hasMoreTokens()){   //对象如果还有单词，循环
            System.out.print(fenxi.nextToken());  //获取单词，每获取一次就自动删除单词
            System.out.println("还剩"+fenxi.countTokens()+"单词");
        }
        System.out.println("一共有"+number+"个单词");
    }
}
