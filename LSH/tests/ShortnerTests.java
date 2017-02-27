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

    //TODO redu

    @Test(timeOut = 100)
    public void TestEncode() throws Exception {
        Assert.assertEquals ( Shortner.GetShort(0), "" );
        Assert.assertEquals( Shortner.GetShort(-1), errorCode);
        Assert.assertEquals( Shortner.GetShort(Long.MAX_VALUE), "BDjzUwjfke9");
        Assert.assertEquals( Shortner.GetShort(Long.MAX_VALUE-2), "BDjzUwjfke7");

    }

    @Test(timeOut = 100)
    public void TestCode() throws Exception {
        Assert.assertEquals ( Shortner.GetID(""), 0);
        Assert.assertEquals ( Shortner.GetID("-1"), -1);
        Assert.assertEquals ( Shortner.GetID("BDjzUwjfke9"), Long.MAX_VALUE);

    }

}
