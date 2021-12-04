package TEST;

import java.io.FileReader;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Jq zhou
 * @Date: 2021/06/09/9:57
 * @Description:
 */
public class IoProperties {
    public static void main(String[] args) throws Exception {

        String path=Thread.currentThread().getContextClassLoader().getResource("classinfo.properties").getPath();

        System.out.println(path);
        FileReader fileReader=new FileReader(path);
        Properties pro=new Properties();
        pro.load(fileReader);
        fileReader.close();
        //
        String className=pro.getProperty("className");
        System.out.println(className);
    }
}
