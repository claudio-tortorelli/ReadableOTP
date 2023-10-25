
/**
 *
 *
 */
import claudiosoft.pocbase.POCException;
import claudiosoft.readableotp.OTPGenerator;
import java.io.IOException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestGeneration extends BaseJUnitTest {

    @Test
    public void t01Generation1() throws InterruptedException, IOException, POCException {
        OTPGenerator gen = new OTPGenerator();
        for (int i = 0; i < 10; i++) {
            System.out.println(gen.generate().get());
        }
    }

}
