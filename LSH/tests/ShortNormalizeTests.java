import com.LSH.server.Normalizer;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.LSH.server.LSHService.siteLink;

/**
 * Created by @author AlNat on 23.11.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Тесты нормализатора
 * Да, некотрые коды могут быть не валидными, но это проверяет Shortner
 */
public class ShortNormalizeTests {

    private static final String errorCode = "Error!";

    @Test//(timeOut = 100)
    public void TestShort() throws Exception {
        //Assert.assertEquals (Normalizer.ShortNormalize("localhost/" + "lkt1"), "lkt1");
        Assert.assertEquals (Normalizer.ShortNormalize(siteLink + "lkt1"), "lkt1");
        Assert.assertEquals (Normalizer.ShortNormalize(siteLink.substring(7) + "lkt2"), "lkt2");
        Assert.assertEquals (Normalizer.ShortNormalize(siteLink), "");
        Assert.assertEquals (Normalizer.ShortNormalize("com/#"), errorCode);

    }

    @Test(timeOut = 100)
    public void TestStopWords() throws Exception {
        Assert.assertEquals( Normalizer.ShortNormalize("www.site.com/# DROP DATABASE"), errorCode);
        Assert.assertEquals( Normalizer.ShortNormalize("www.site.com/#CREATE TABLE"), errorCode);
    }

    @Test(timeOut = 100)
    public void TestStopSymbols() throws Exception {
        Assert.assertEquals( Normalizer.ShortNormalize("www.site.com/#<>"), errorCode);
        Assert.assertEquals( Normalizer.ShortNormalize("www.site.com/#'"), errorCode);
        Assert.assertEquals( Normalizer.ShortNormalize("www.site.com/#\t"), errorCode);
        Assert.assertEquals( Normalizer.ShortNormalize("www.site.com/#\n"), errorCode);
    }

}
