import com.LSH.server.Normalizer;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Created by @author AlNat on 23.11.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Тесты нормализатора
 */
public class NormalizerTests {

    private static String errorCode = "Error!";
    private Normalizer normalizer;

    @BeforeTest
    public void Create () {
        //normalizer = new Normalizer();
    }

    @Test(timeOut = 100)
    public void TestSimpleNormalize() throws Exception {
        Assert.assertEquals (Normalizer.Normalize("site.com"), "http://www.site.com");
        Assert.assertEquals (Normalizer.Normalize("www.site.com"), "http://www.site.com");
        Assert.assertEquals (Normalizer.Normalize("http://www.site.com"), "http://www.site.com");
    }

    @Test(timeOut = 100)
    public void TestURLNormalize() throws Exception {
        Assert.assertEquals (Normalizer.Normalize("site.com/chat"), "http://www.site.com/chat");
        Assert.assertEquals (Normalizer.Normalize("www.site.com/chat"), "http://www.site.com/chat");
        Assert.assertEquals (Normalizer.Normalize("http://www.site.com/chat"), "http://www.site.com/chat");

        Assert.assertEquals (Normalizer.Normalize("site.com/chat/page.html"), "http://www.site.com/chat/page.html");
        Assert.assertEquals (Normalizer.Normalize("www.site.com/chat/page.html"), "http://www.site.com/chat/page.html");
        Assert.assertEquals (Normalizer.Normalize("http://www.site.com/chat/page.html"), "http://www.site.com/chat/page.html");
    }

    @Test(timeOut = 100)
    public void TestSubdomainNormalize() throws Exception {
        Assert.assertEquals (Normalizer.Normalize("sub.site.com"), "http://www.sub.site.com");
        Assert.assertEquals (Normalizer.Normalize("www.site.com"), "http://www.sub.site.com");
        Assert.assertEquals (Normalizer.Normalize("http://www.sub.site.com"), "http://www.sub.site.com");

    }

    @Test(timeOut = 100)
    public void TestHashNormalize() throws Exception {
        Assert.assertEquals (Normalizer.Normalize("site.com/chat#id"), "http://www.site.com/chat#id");
        Assert.assertEquals (Normalizer.Normalize("www.site.com/chat"), "http://www.site.com/chat#id");
        Assert.assertEquals (Normalizer.Normalize("http://www.site.com/chat#id"), "http://www.site.com/chat#id");
    }

    @Test(timeOut = 100)
    public void TestStopWords() throws Exception {
        Assert.assertEquals( Normalizer.Normalize("CREATE TABLE users"), errorCode);
        Assert.assertEquals( Normalizer.Normalize("DROP TABLE users"), errorCode);
        Assert.assertEquals( Normalizer.Normalize("http://www.site.com/DROP TABLE users"), errorCode);
    }



    @Test(timeOut = 100)
    public void TestCodeNormalize() throws Exception {
        //Assert.assertEquals (Normalizer.ShortNormalize("www.site.com/lkt2"), "lkt2");
    }

}
