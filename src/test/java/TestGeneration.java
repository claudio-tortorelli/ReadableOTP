
/**
 *
 *
 */
import claudiosoft.pocbase.BasicConsoleLogger;
import claudiosoft.pocbase.POCException;
import static claudiosoft.readableotp.OTPConstants.*;
import claudiosoft.readableotp.OTPGenerator;
import claudiosoft.readableotp.OTPRule;
import claudiosoft.readableotp.ROTP;
import java.io.IOException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestGeneration extends BaseJUnitTest {

    private OTPGenerator gen;

    private OTPGenerator getGenerator() {
        BasicConsoleLogger.get().info("building generator...");
        if (gen == null) {
            gen = new OTPGenerator();
        }
        return gen;
    }

    @Test
    public void t01GenerationRules() throws InterruptedException, IOException, POCException {
        OTPGenerator gen = new OTPGenerator(new OTPRule("xxxxxx", "0,9", PART_2));
        BasicConsoleLogger.get().info(gen.generate().get());

        gen = new OTPGenerator(new OTPRule("xxxyyy", "0,9", "!", PART_2));
        BasicConsoleLogger.get().info(gen.generate().get());

        gen = new OTPGenerator(new OTPRule("xxyxxy", "0,9", "!", PART_2));
        BasicConsoleLogger.get().info(gen.generate().get());

        gen = new OTPGenerator(new OTPRule("xyyxyy", "0,9", "!", PART_2));
        BasicConsoleLogger.get().info(gen.generate().get());

        gen = new OTPGenerator(new OTPRule("xyxxyx", "0,9", "!", PART_2));
        BasicConsoleLogger.get().info(gen.generate().get());

        gen = new OTPGenerator(new OTPRule("xxyyxx", "0,9", "!", PART_2));
        BasicConsoleLogger.get().info(gen.generate().get());

        gen = new OTPGenerator(new OTPRule("xyyyyx", "0,9", "!", PART_2));
        BasicConsoleLogger.get().info(gen.generate().get());

        gen = new OTPGenerator(new OTPRule("xyzxyz", "0,9", "!", "!", PART_2));
        BasicConsoleLogger.get().info(gen.generate().get());
    }

    @Test
    public void t02OTPMatchRule() throws InterruptedException, IOException, POCException {
        Assert.assertTrue(!new OTPRule("xxyxxy", "0,9", "!", PART_2).isMatching("100100"));
        Assert.assertTrue(!new OTPRule("xxyxxy", "0,9", "!", PART_2).isMatching("11110"));
        Assert.assertTrue(new OTPRule("xxyxxy", "0,9", "!", PART_2).isMatching("110110"));
    }

    @Test
    public void t02GenerateRandom() throws InterruptedException, IOException, POCException {
        for (int i = 0; i < 10; i++) {
            ROTP otp = getGenerator().generate();
            BasicConsoleLogger.get().info(String.format("%s -> %s", otp.getSchema(), otp.get()));
        }
    }

    @Test
    public void t03CountAllOTPInstances() throws InterruptedException, IOException, POCException {
        int nOtp = getGenerator().countMax(true);
        BasicConsoleLogger.get().info(String.format("OTP counter: %d", nOtp));
        BasicConsoleLogger.get().info(String.format("OTP readable on total: %.1f%%", (nOtp / (double) 1000000) * 100));
    }

    @Test
    public void t04BruteForce() throws InterruptedException, IOException, POCException {
        OTPGenerator brute = getGenerator();
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
    @Ignore("TODO")
    public void t04BruteForceAverage() throws InterruptedException, IOException, POCException {
        //TODO reiterato
    }

    @Test
    public void t05WrapToNext() throws InterruptedException, IOException, POCException {
        OTPGenerator gen = new OTPGenerator(new OTPRule("xxxyyy", "0,9", "!", PART_2));
        Assert.assertTrue(gen.wrapToNext("111221").get().equals("111 222"));
        Assert.assertTrue(gen.wrapToNext("999999").get().equals("000 111"));
    }

}
