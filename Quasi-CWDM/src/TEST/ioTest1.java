package TEST;

import java.io.*;

public class ioTest1 {

    public static void main(String[] args) {
        File file=new File("word.txt");
        if(file.exists()){
            String name= file.getName();
            long length= file.length();
            boolean hidden= file.isHidden();
            System.out.println(name);
            System.out.println(length);
            System.out.println(hidden);
        }else
            System.out.println("文件不存在");
        try {
            FileOutputStream out=new FileOutputStream(file);
            byte[] buy ="今天星期一".getBytes();
            out.write(buy);
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileInputStream in=new FileInputStream(file);
            byte[] byt =new byte[1024];
            int len=in.read(byt);
            System.out.println(new String(byt,0,len));
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
