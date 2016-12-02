import com.LSH.server.Normalizer;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by @author AlNat on 23.11.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Тесты нормализатора
 */
public class NormalizerTests {
//TODO Еще тесты

    @Test(timeOut = 100)
    public void TestSimpleNormalize() throws Exception {
        Assert.assertEquals (Normalizer.Normalize("BigBrownFox"), "BigBrownFox");
    }

    @Test(timeOut = 100)
    public void TestURLNormalize() throws Exception {
        Assert.assertEquals (Normalizer.Normalize("BigBrownFox"), "BigBrownFox");
    }

    @Test(timeOut = 100)
    public void TestStopWords() throws Exception {
        Assert.assertEquals(Normalizer.Normalize("CREATE"), "ERROR");
    }


    @Test(timeOut = 100)
    public void TestCodeNormalize() throws Exception {
        Assert.assertEquals (Normalizer.ShortNormalize("www.site.com/lkt2"), "lkt2");
    }

}
