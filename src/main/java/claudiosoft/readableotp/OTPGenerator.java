package claudiosoft.readableotp;

import claudiosoft.pocbase.BasicConsoleLogger;
import claudiosoft.pocbase.POCException;
import static claudiosoft.readableotp.OTPConstants.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Claudio
 */
public class OTPGenerator {

    private List<OTPRule> rules;
    private final Random rand;

    public OTPGenerator() {
        this.rand = new Random();

        //TODO rules should be externally configurable
        rules = new LinkedList<>();
        rules.add(new OTPRule("xxxxxx", "[0,9]", SCORE_3, PART_2));
        rules.add(new OTPRule("xxxyyy", "[0,9]", "!", SCORE_2, PART_2));
        rules.add(new OTPRule("xxyxxy", "[0,9]", "!", SCORE_2, PART_2));
        rules.add(new OTPRule("xyyxyy", "[0,9]", "!", SCORE_2, PART_2));
        rules.add(new OTPRule("xyxxyx", "[0,9]", "!", SCORE_2, PART_2));
        rules.add(new OTPRule("xxyyxx", "[0,9]", "!", SCORE_2, PART_2));
        rules.add(new OTPRule("xyyyyx", "[0,9]", "!", SCORE_2, PART_2));
        rules.add(new OTPRule("xyzxyz", "[0,9]", "!", SCORE_2, PART_2));
        validate();
    }

    private void validate() {
        BasicConsoleLogger.get().info("validating rules...");
        //TODO avoid doubled schema; avoid invalid schema len; avoid schema with too much digit; avoid inconsistent rule's domain
//        if (len%2 > 0 part == 2) {
//                throw new POCException("")
//            }
    }

    public ROTP generate() {
        //TODO by score too
        int iRule = rand.nextInt(rules.size());
        OTPRule rule = rules.get(iRule);

        int nDigits = 1;
        if (rule.getSchema().contains("y")) {
            nDigits++;
        }
        if (rule.getSchema().contains("z")) {
            nDigits++;
        }

        // x domain
        int xMin = Integer.parseInt(rule.getxRule().substring(3, 4));
        int xMax = Integer.parseInt(rule.getxRule().substring(5, 6));
        int x = xMin + rand.nextInt(xMax - xMin);

        int y = -1;
        if (nDigits > 1) { // y
            if (rule.getyRule().equalsIgnoreCase("y!=")) {
                y = x;
                while (y == x) {
                    y = rand.nextInt(9);
                }
            } else if (rule.getyRule().equalsIgnoreCase("+1")) {
                y = x + 1;
            }
        }

        int z = -1;
        if (nDigits > 2) { // z
            if (rule.getyRule().equalsIgnoreCase("z!=")) {
                z = y;
                while (z == y || z == x) {
                    z = rand.nextInt(9);
                }
            } else if (rule.getyRule().equalsIgnoreCase("+1")) {
                z = y + 1;
            }
        }

        // compose schema
        String xStr = String.format("%d", x);
        String yStr = String.format("%d", y);
        String zStr = String.format("%d", z);

        String otpStr = rule.getSchema().toLowerCase().replace("x", xStr).replace("y", yStr).replace("z", zStr);
        int otp = Integer.parseInt(otpStr);

        return new ROTP(otp, rule.getParts());
    }

    public void overrideRules(List<OTPRule> rules) throws POCException {
        if (rules == null || rules.isEmpty()) {
            throw new POCException("Invalid rule list");
        }
        this.rules = rules;
    }

}
