
/**
 *
 *
 */
import claudiosoft.pocbase.BasicConsoleLogger;
import claudiosoft.pocbase.POCException;
import static claudiosoft.readableotp.ROTPConstants.*;
import claudiosoft.readableotp.ROTPGenerator;
import claudiosoft.readableotp.ROTPSchema;
import claudiosoft.readableotp.ROTP;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestGeneration extends BaseJUnitTest {

    private ROTPGenerator gen;

    private ROTPGenerator getGenerator() throws POCException, NoSuchAlgorithmException {
        if (gen == null) {
            gen = new ROTPGenerator();
        }
        return gen;
    }

    @Test
    public void t01GenerationRules() throws POCException, NoSuchAlgorithmException {
        ROTPGenerator gen = new ROTPGenerator(new ROTPSchema("xxxxxx", "0,9", PART_2));
        BasicConsoleLogger.get().info(gen.generate().get());

        gen = new ROTPGenerator(new ROTPSchema("xxxyyy", "0,9", "!", PART_2));
        BasicConsoleLogger.get().info(gen.generate().get());

        gen = new ROTPGenerator(new ROTPSchema("xxyxxy", "0,9", "!", PART_2));
        BasicConsoleLogger.get().info(gen.generate().get());
    }

    @Test
    public void t01RuleInvalidSchema() throws POCException, NoSuchAlgorithmException {
        boolean done = false;
        try {
            new ROTPGenerator(new ROTPSchema("kxxxxx", "0,9", PART_2));
        } catch (POCException ex) {
            done = true;
        }
        Assert.assertTrue(done);
    }

    @Test
    public void t01RuleInvalidLen() throws POCException, NoSuchAlgorithmException {
        boolean done = false;
        try {
            new ROTPGenerator(new ROTPSchema("xyxxx", "0,9", PART_2));
        } catch (POCException ex) {
            done = true;
        }
        Assert.assertTrue(done);
    }

    @Test
    public void t02OTPMatchRule() throws InterruptedException, IOException, POCException {
        Assert.assertTrue(!new ROTPSchema("xxyxxy", "0,9", "!", PART_2).isMatching("100100"));
        Assert.assertTrue(!new ROTPSchema("xxyxxy", "0,9", "!", PART_2).isMatching("11110"));
        Assert.assertTrue(new ROTPSchema("xxyxxy", "0,9", "!", PART_2).isMatching("110110"));
    }

    @Test
    public void t02GenerateRandom() throws InterruptedException, IOException, POCException, NoSuchAlgorithmException {
        for (int i = 0; i < 10; i++) {
            ROTP otp = getGenerator().generate();
            BasicConsoleLogger.get().info(String.format("%s -> %s", otp.getSchema(), otp.get()));
        }
    }

    @Test
    public void t03CountAllOTPInstances() throws InterruptedException, IOException, POCException, NoSuchAlgorithmException {
        int nOtp = getGenerator().countMax(true);
        BasicConsoleLogger.get().info(String.format("OTP readable on total: %.1f%%", (nOtp / (double) 1000000) * 100));
    }

    @Test
    public void t04BruteForce() throws InterruptedException, IOException, POCException, NoSuchAlgorithmException {
        ROTPGenerator brute = getGenerator();
        final int maxAttemps = 3; // because of ROTP are simpler to write
        int trial = 0;
        boolean bruted = false;
        while (true) {
            ROTP otp = getGenerator().generate();
            for (int i = 0; i < maxAttemps; i++) {
                if (brute.generate().get().equals(otp.get())) {
                    bruted = true;
                    break;
                }
                trial++;
            }
            if (bruted) {
                break;
            }
        }
        BasicConsoleLogger.get().info(String.format("OTP bruted after %d trials and %d ban", trial, (trial / maxAttemps)));
    }

    @Test
    public void t05WrapToNext() throws InterruptedException, IOException, POCException, NoSuchAlgorithmException {
        ROTPGenerator gen = new ROTPGenerator(new ROTPSchema("xxxyyy", "0,9", "!", PART_2));
        Assert.assertTrue(gen.wrapToNext("111221").get().equals("111 222"));
        Assert.assertTrue(gen.wrapToNext("999999").get().equals("000 111"));
    }

    @Test
    public void t06NotROPT() throws InterruptedException, IOException, POCException, NoSuchAlgorithmException {
        getGenerator().setRotpFrequency(0.0);
        BasicConsoleLogger.get().info(getGenerator().generate().get());
        BasicConsoleLogger.get().info(getGenerator().generate().get());
        BasicConsoleLogger.get().info(getGenerator().generate().get());
    }

    @Test
    public void t06ROPTWithFreq() throws InterruptedException, IOException, POCException, NoSuchAlgorithmException {
        getGenerator().setRotpFrequency(0.5);
        for (int i = 0; i < 10; i++) {
            BasicConsoleLogger.get().info(getGenerator().generate().get());
        }
    }

    @Test
    public void t06BruteForceWithFreq() throws InterruptedException, IOException, POCException, NoSuchAlgorithmException {
        ROTPGenerator bruteGen = getGenerator();
        bruteGen.setRotpFrequency(0.75);
        final int maxAttemps = 3; // because of ROTP are simpler to write
        int trial = 0;
        boolean bruted = false;
        while (true) {
            ROTP otp = bruteGen.generate();
            for (int i = 0; i < maxAttemps; i++) {
                if (bruteGen.generate().get().equals(otp.get())) {
                    bruted = true;
                    break;
                }
                trial++;
            }
            if (bruted) {
                break;
            }
        }
        BasicConsoleLogger.get().info(String.format("OTP bruted after %d trials and %d ban", trial, (trial / maxAttemps)));
    }

}
