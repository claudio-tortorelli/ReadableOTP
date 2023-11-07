
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
import java.util.LinkedList;
import java.util.List;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestGeneration extends BaseJUnitTest {

    @Test
    public void t01GenerationRules() throws InterruptedException, IOException, POCException {
        OTPGenerator gen = new OTPGenerator();
        List rules = new LinkedList<>();

        rules.add(new OTPRule("xxxxxx", "0,9", SCORE_3, PART_2));
        gen.overrideRules(rules);
        BasicConsoleLogger.get().info(gen.generate().get());

        rules.clear();
        rules.add(new OTPRule("xxxyyy", "0,9", "!", SCORE_2, PART_2));
        gen.overrideRules(rules);
        BasicConsoleLogger.get().info(gen.generate().get());

        rules.clear();
        rules.add(new OTPRule("xxyxxy", "0,9", "!", SCORE_2, PART_2));
        gen.overrideRules(rules);
        BasicConsoleLogger.get().info(gen.generate().get());

        rules.clear();
        rules.add(new OTPRule("xyyxyy", "0,9", "!", SCORE_2, PART_2));
        gen.overrideRules(rules);
        BasicConsoleLogger.get().info(gen.generate().get());

        rules.clear();
        rules.add(new OTPRule("xyxxyx", "0,9", "!", SCORE_2, PART_2));
        gen.overrideRules(rules);
        BasicConsoleLogger.get().info(gen.generate().get());

        rules.clear();
        rules.add(new OTPRule("xxyyxx", "0,9", "!", SCORE_2, PART_2));
        gen.overrideRules(rules);
        BasicConsoleLogger.get().info(gen.generate().get());

        rules.clear();
        rules.add(new OTPRule("xyyyyx", "0,9", "!", SCORE_2, PART_2));
        gen.overrideRules(rules);
        BasicConsoleLogger.get().info(gen.generate().get());

        rules.clear();
        rules.add(new OTPRule("xyzxyz", "0,9", "!", SCORE_2, PART_2));
        gen.overrideRules(rules);
        BasicConsoleLogger.get().info(gen.generate().get());
    }

    @Test
    public void t02GenerationRandom() throws InterruptedException, IOException, POCException {
        OTPGenerator gen = new OTPGenerator();
        for (int i = 0; i < 10; i++) {
            ROTP otp = gen.generate();
            BasicConsoleLogger.get().info(String.format("%s -> %s", otp.getSchema(), otp.get()));
        }
    }

    @Test
    public void t03CountAllOTPInstances() throws InterruptedException, IOException, POCException {
        OTPGenerator gen = new OTPGenerator();
        int nOtp = gen.countMax();
        BasicConsoleLogger.get().info(String.format("OTP counter: %d", nOtp));
        BasicConsoleLogger.get().info(String.format("OTP readable on total: %.1f%%", (nOtp / (double) 1000000) * 100));
    }

    @Test
    @Ignore("Manual")
    public void t04BruteForce() throws InterruptedException, IOException, POCException {
        //TODO
    }

}
