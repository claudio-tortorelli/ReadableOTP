package claudiosoft.readableotp;

import claudiosoft.pocbase.BasicConsoleLogger;
import static claudiosoft.readableotp.OTPConstants.*;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Claudio
 */
public class OTPGenerator {

    List<OTPRule> rules;

    public OTPGenerator() {
        List<OTPRule> rules = new LinkedList<>();
        rules.add(new OTPRule("xxxxxx", "x=[0,9]", SCORE_3, PART_2));
        rules.add(new OTPRule("xxxyyy", "x=[0,9]", "y!=x", SCORE_2, PART_2));
        validate();
    }

    private void validate() {
        BasicConsoleLogger.get().info("validating rules...");
        //TODO
    }

}
