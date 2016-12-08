import com.LSH.server.Config;
import org.testng.Assert;
import org.testng.annotations.Test;
/**
 * Created by @author AlNat on 08.12.2016.
 * Licensed by Apache License, Version 2.0
 */
public class ConfigTest {

    @Test//(timeOut = 100)
    public void Test() throws Exception {
        Assert.assertEquals(Config.instance.getLogin(), "LSH");
        Assert.assertEquals(Config.instance.getURL(), "//localhost/LSH");
        Assert.assertEquals(Config.instance.getSiteLink(), "http://127.0.0.1/");

    }

}


