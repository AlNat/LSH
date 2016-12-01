import com.LSH.server.Normalizer;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by @author AlNat on 23.11.2016.
 * Licensed by Apache License, Version 2.0
 */
// TODO Комментари + JavaDoc + Еще тесты
public class NormalizerTests {

    @Test(timeOut = 100)
    public void TestSimpleNormalize() throws Exception {
        Assert.assertEquals ("", Normalizer.Normalize("BigBrownFox"));
    }

    @Test(timeOut = 100)
    public void TestStopWords() throws Exception {
        System.out.println(Normalizer.Normalize("CREATE"));
        //Assert.assertEquals(Normalizer.Normolize("CREATE"), "ERROR");
    }

}
