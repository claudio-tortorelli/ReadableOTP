package claudiosoft.readableotp;

import claudiosoft.pocbase.BasicConsoleLogger;
import claudiosoft.pocbase.POCException;
import static claudiosoft.readableotp.OTPConstants.*;
import java.util.HashMap;
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

    private HashMap otpMap;

    public OTPGenerator() {
        this.otpMap = new HashMap<>();
        this.rand = new Random();

        //TODO rules should be externally configurable
        //TODO score is not used by now
        rules = new LinkedList<>();
        rules.add(new OTPRule("xxxxxx", "0,9", PART_2, SCORE_NONE));
        rules.add(new OTPRule("xxxyyy", "0,9", "!", PART_2, SCORE_NONE));
        rules.add(new OTPRule("xxyxxy", "0,9", "!", PART_2, SCORE_NONE));
        rules.add(new OTPRule("xyyxyy", "0,9", "!", PART_2, SCORE_NONE));
        rules.add(new OTPRule("xyxxyx", "0,9", "!", PART_2, SCORE_NONE));
        rules.add(new OTPRule("xxyyxx", "0,9", "!", PART_2, SCORE_NONE));
        rules.add(new OTPRule("xyyyyx", "0,9", "!", PART_2, SCORE_NONE));

        rules.add(new OTPRule("xxxxxy", "0,9", "!", PART_2, SCORE_NONE));
        rules.add(new OTPRule("xxxxyx", "0,9", "!", PART_2, SCORE_NONE));
        rules.add(new OTPRule("xxxyxx", "0,9", "!", PART_2, SCORE_NONE));
        rules.add(new OTPRule("xxyxxx", "0,9", "!", PART_2, SCORE_NONE));
        rules.add(new OTPRule("xyxxxx", "0,9", "!", PART_2, SCORE_NONE));
        rules.add(new OTPRule("yxxxxx", "0,9", "!", PART_2, SCORE_NONE));

        rules.add(new OTPRule("xyzxyz", "0,9", "!", "!", PART_2, SCORE_NONE));
        rules.add(new OTPRule("xyzxyz", "0,7", "+1", "+1", PART_2, SCORE_NONE));
        rules.add(new OTPRule("xyzxyz", "2,9", "-1", "-1", PART_2, SCORE_NONE));

        rules.add(new OTPRule("xxxxyy", "0,9", "!", PART_3, SCORE_NONE));
        rules.add(new OTPRule("xxyyyy", "0,9", "!", PART_3, SCORE_NONE));
        rules.add(new OTPRule("xyxyxy", "0,9", "!", PART_3, SCORE_NONE));
        rules.add(new OTPRule("xxyyzz", "0,9", "!", "!", PART_3, SCORE_NONE));
        validate();
    }

    private void validate() {
        BasicConsoleLogger.get().debug("validating rules...");
        //TODO avoid doubled schema and rules;
        //avoid invalid schema len;
        //avoid schema with too much digit;
        //avoid inconsistent rule's domain...
//        if (len%2 > 0 part == 2) {
//                throw new POCException("")
//            }
    }

    public ROTP generate() throws POCException {
        //TODO filter by score too
        int iRule = rand.nextInt(rules.size());
        OTPRule rule = rules.get(iRule);

        int nDigits = rule.getDigits();

        // x domain
        String[] domain = rule.getxRule().split(",");
        if (domain.length != 2) {
            throw new POCException("Invalid x domain");
        }
        int xMin = Integer.parseInt(domain[0]);
        int xMax = Integer.parseInt(domain[1]);
        int x = xMin + rand.nextInt(xMax - xMin);

        int y = -1;
        if (nDigits > 1) { // y
            if (rule.getyRule().equalsIgnoreCase("!")) {
                y = x;
                while (y == x) {
                    y = rand.nextInt(9);
                }
            } else if (rule.getyRule().equalsIgnoreCase("+1")) {
                y = x + 1;
            } else if (rule.getyRule().equalsIgnoreCase("-1")) {
                y = x - 1;
            }
        }

        int z = -1;
        if (nDigits > 2) { // z
            if (rule.getyRule().equalsIgnoreCase("!")) {
                z = y;
                while (z == y || z == x) {
                    z = rand.nextInt(9);
                }
            } else if (rule.getyRule().equalsIgnoreCase("+1")) {
                z = y + 1;
            } else if (rule.getyRule().equalsIgnoreCase("-1")) {
                z = y - 1;
            }
        }

        // compose schema
        String xStr = String.format("%d", x);
        String yStr = String.format("%d", y);
        String zStr = String.format("%d", z);

        String otpStr = rule.getSchema().toLowerCase().replace("x", xStr).replace("y", yStr).replace("z", zStr);
        int otp = Integer.parseInt(otpStr);

        return new ROTP(otp, rule.getParts(), rule.getSchema());
    }

    public void overrideRules(List<OTPRule> rules) throws POCException {
        if (rules == null || rules.isEmpty()) {
            throw new POCException("Invalid rule list");
        }
        this.rules = rules;
        validate();
    }

    public int countMax() {
        return countMax(false);
    }

    public int countMax(boolean verbose) {
        int nOtp = 0;
        for (OTPRule rule : rules) {
            int max = rule.getMaxOtp() + 1;
            for (int iOtp = 0; iOtp < max; iOtp++) {
                String candidateOtp = String.format("%0" + rule.getLength() + "d", iOtp);
                if (rule.isMatching(candidateOtp, verbose)) {
                    nOtp++;
                }
            }
        }
        return nOtp;
    }

}
