import com.LSH.server.Config.Config;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Created by @author AlNat on 08.12.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Тесты конфигурационного файла
 */
public class ConfigTests {

    @Test(timeOut = 100)
    public void TestGetConfig() throws Exception {
        Assert.assertEquals(Config.instance.getLogFile(), "C:\\Users\\AlNat\\Source\\Studi\\Diplom\\log.txt");
        Assert.assertEquals(Config.instance.getSiteLink(), "http://127.0.0.1/");
    }

    @Test(timeOut = 100)
    public void TestGetDBConfig() throws Exception {
        Assert.assertEquals(Config.instance.getLogin(), "LSH");
        Assert.assertEquals(Config.instance.getURL(), "//localhost/LSH");
    }

}


