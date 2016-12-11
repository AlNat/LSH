import com.LSH.server.Shortner;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by @author AlNat on 24.05.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Тесты сокращения
 */
public class ShortnerTests {

    @SuppressWarnings("FieldCanBeLocal")
    private static final String errorCode = "Error!";

    @Test(timeOut = 100)
    public void TestEncode() throws Exception {
        Assert.assertEquals ( Shortner.GetShort(123456789), "pgK8p" );
        Assert.assertEquals ( Shortner.GetShort(123456788), "pgK8n" );
        Assert.assertEquals ( Shortner.GetShort(0), "" );
        Assert.assertEquals( Shortner.GetShort(-1), errorCode);
        Assert.assertEquals( Shortner.GetShort(2498), "Z_");
    }

    @Test(timeOut = 100)
    public void TestCode() throws Exception {
        Assert.assertEquals ( Shortner.GetID("pgK8p"), 123456789 );
        Assert.assertEquals ( Shortner.GetID("pgK8n"), 123456788 );
        Assert.assertEquals ( Shortner.GetID(""), 0);
        Assert.assertEquals ( Shortner.GetID("2"), 0);
        Assert.assertEquals ( Shortner.GetID("-1"), -1);
        Assert.assertEquals ( Shortner.GetID("Z_"), 2498);
    }

}
