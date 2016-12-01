import com.LSH.server.Shortner;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by @author AlNat on 24.05.2016.
 * Licensed by Apache License, Version 2.0
 */

// TODO Комментари + JavaDoc + Еще тесты
public class ShortnerTests {

    @Test(timeOut = 100)
    public void TestEncode() throws Exception {
        Assert.assertEquals ("pgK8p", Shortner.GetShort(123456789) );
        Assert.assertEquals ("", Shortner.GetShort(0) );
        //Assert.assertEquals (" ", Shortner.GetShort(-1) );
    }

    @Test(timeOut = 100)
    public void TestCode() throws Exception {
        Assert.assertEquals (123456789, Shortner.GetID("pgK8p") );
        Assert.assertEquals (0, Shortner.GetID("") );
    }


}
