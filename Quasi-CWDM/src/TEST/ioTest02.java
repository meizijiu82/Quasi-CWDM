package TEST;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Jq zhou
 * @Date: 2021/07/05/16:26
 * @Description:
 */
public class ioTest02 {

        public static void main(String[] args) throws IOException {

            File f=new File("6_Node.txt");
            f.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
            System.out.println("默认输出到控制台的这一句，输出到了文件 6_Node.txt");
        }
}
