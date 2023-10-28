
/**
 *
 *
 */
import claudiosoft.pocbase.BasicConsoleLogger;
import claudiosoft.pocbase.POCException;
import static claudiosoft.readableotp.OTPConstants.PART_2;
import static claudiosoft.readableotp.OTPConstants.SCORE_3;
import claudiosoft.readableotp.OTPGenerator;
import claudiosoft.readableotp.OTPRule;
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

        rules.add(new OTPRule("xxxxxx", "[0,9]", SCORE_3, PART_2));
        gen.overrideRules(rules);
        BasicConsoleLogger.get().info(gen.generate().get());

//        rules.clear();
//        rules.add(new OTPRule("xxxyyy", "[0,9]", "!", SCORE_2, PART_2));
//        gen.overrideRules(rules);
//        BasicConsoleLogger.get().info(gen.generate().get());
//
//        rules.clear();
//        rules.add(new OTPRule("xxxyyy", "[0,9]", "!", SCORE_2, PART_2));
//        gen.overrideRules(rules);
//        BasicConsoleLogger.get().info(gen.generate().get());
//
//        rules.clear();
//        rules.add(new OTPRule("xyyxyy", "[0,9]", "!", SCORE_2, PART_2));
//        gen.overrideRules(rules);
//        BasicConsoleLogger.get().info(gen.generate().get());
//
//        rules.clear();
//        rules.add(new OTPRule("xyxxyx", "[0,9]", "!", SCORE_2, PART_2));
//        gen.overrideRules(rules);
//        BasicConsoleLogger.get().info(gen.generate().get());

//        rules.clear();
//        rules.add(new OTPRule("xxyyxx", "[0,9]", "!", SCORE_2, PART_2));
//        gen.overrideRules(rules);
//        BasicConsoleLogger.get().info(gen.generate().get());
//
//        rules.clear();
//        rules.add(new OTPRule("xyyyyx", "[0,9]", "!", SCORE_2, PART_2));
//        gen.overrideRules(rules);
//        BasicConsoleLogger.get().info(gen.generate().get());

//        rules.clear();
//        rules.add(new OTPRule("xyzxyz", "[0,9]", "!", SCORE_2, PART_2));
//        gen.overrideRules(rules);
//        BasicConsoleLogger.get().info(gen.generate().get());
    }

    @Test
    @Ignore
    public void t02GenerationRandom() throws InterruptedException, IOException, POCException {
        OTPGenerator gen = new OTPGenerator();
        for (int i = 0; i < 10; i++) {
            System.out.println(gen.generate().get());
        }
    }

}
