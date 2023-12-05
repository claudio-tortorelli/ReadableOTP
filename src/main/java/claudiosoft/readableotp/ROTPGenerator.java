package claudiosoft.readableotp;

import claudiosoft.pocbase.BasicConsoleLogger;
import claudiosoft.pocbase.POCException;
import static claudiosoft.readableotp.ROTPConstants.*;
import static java.lang.Math.pow;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * ReadableOTP
 *
 * @author Claudio Tortorelli
 */
public class ROTPGenerator {

    private List<ROTPSchema> schemas;
    private final Random rand;

    private static String[] rOtpArray;
    private double rotpFrequency; // (0,1)

    public ROTPGenerator() throws POCException, NoSuchAlgorithmException {
        this(null);
    }

    //TODO rules should be externally configurable
    //TODO score is not used by now
    //TODO every rule could swap and reload to file its possible ROTP
    public ROTPGenerator(ROTPSchema schema) throws POCException, NoSuchAlgorithmException {
        rand = new Random();
        schemas = new LinkedList<>();
        rotpFrequency = 1.0;

        if (schema != null) {
            schemas.add(schema);
        } else {
            schemas.add(new ROTPSchema("xxxxxx", "0,9", PART_2, SCORE_NONE));
            schemas.add(new ROTPSchema("xxxyyy", "0,9", "!", PART_2, SCORE_NONE));
            schemas.add(new ROTPSchema("xxyxxy", "0,9", "!", PART_2, SCORE_NONE));
            schemas.add(new ROTPSchema("xyyxyy", "0,9", "!", PART_2, SCORE_NONE));
            schemas.add(new ROTPSchema("xyxxyx", "0,9", "!", PART_2, SCORE_NONE));
            schemas.add(new ROTPSchema("xxyyxx", "0,9", "!", PART_2, SCORE_NONE));
            schemas.add(new ROTPSchema("xyyyyx", "0,9", "!", PART_2, SCORE_NONE));

            schemas.add(new ROTPSchema("xxxxxy", "0,9", "!", PART_2, SCORE_NONE));
            schemas.add(new ROTPSchema("xxxxyx", "0,9", "!", PART_2, SCORE_NONE));
            schemas.add(new ROTPSchema("xxxyxx", "0,9", "!", PART_2, SCORE_NONE));
            schemas.add(new ROTPSchema("xxyxxx", "0,9", "!", PART_2, SCORE_NONE));
            schemas.add(new ROTPSchema("xyxxxx", "0,9", "!", PART_2, SCORE_NONE));
            schemas.add(new ROTPSchema("yxxxxx", "0,9", "!", PART_2, SCORE_NONE));

            schemas.add(new ROTPSchema("xyzxyz", "0,9", "!", "!", PART_2, SCORE_NONE));
            schemas.add(new ROTPSchema("xyzxyz", "0,7", "+1", "+1", PART_2, SCORE_NONE));
            schemas.add(new ROTPSchema("xyzxyz", "2,9", "-1", "-1", PART_2, SCORE_NONE));

            schemas.add(new ROTPSchema("xxxxyy", "0,9", "!", PART_3, SCORE_NONE));
            schemas.add(new ROTPSchema("xxyyyy", "0,9", "!", PART_3, SCORE_NONE));
            schemas.add(new ROTPSchema("xyxyxy", "0,9", "!", PART_3, SCORE_NONE));
            schemas.add(new ROTPSchema("xxyyzz", "0,9", "!", "!", PART_3, SCORE_NONE));
        }
        validate();
    }

    private void validate() throws POCException, NoSuchAlgorithmException {
        BasicConsoleLogger.get().debug("validating rules...");

        for (int iRule = 0; iRule < schemas.size(); iRule++) {
            ROTPSchema ruleX = schemas.get(iRule);
            if (ruleX.getLength() != ROTPConstants.EXPECTED_DIGITS) {
                throw new POCException("found rule with invalid digits length: " + ruleX.getSchema());
            }
            if (!ruleX.getSchema().matches("^[xyzXYZ]+$")) {
                throw new POCException("found rule with invalid chars in schema: " + ruleX.getSchema());
            }
//            if (ruleX.getLength() % 2 == 0 && ruleX.getParts() != PART_2) {
//                throw new POCException("found rule with inconsistent partition: " + ruleX.getSchema());
//            }
//            if (ruleX.getLength() % 2 == 1 && ruleX.getParts() != PART_3) {
//                throw new POCException("found rule with inconsistent partition: " + ruleX.getSchema());
//            }
            for (int jRule = iRule + 1; jRule < schemas.size(); jRule++) {
                ROTPSchema ruleY = schemas.get(jRule);
                if (ruleX.isEquivalentTo(ruleY)) {
                    throw new POCException("found doubled rule: " + ruleX.getSchema());
                }
            }
            //TODO improving x y z rule check verifying their domain consistency
        }
        BasicConsoleLogger.get().debug("validation done...");
        rOtpArray = new String[(int) pow(10, ROTPConstants.EXPECTED_DIGITS)];
        countMax(false);
    }

    public ROTP generate() throws POCException {
        return generate(SCORE_NONE);
    }

    //TODO filter by score too
    public ROTP generate(int minScore) throws POCException {

        if (rand.nextDouble() > rotpFrequency) {
            String otp = String.format("%0" + ROTPConstants.EXPECTED_DIGITS + "d", rand.nextInt((int) pow(10, ROTPConstants.EXPECTED_DIGITS)));
            return new ROTP(otp, PART_2);
        }

        int iRule = rand.nextInt(schemas.size());
        ROTPSchema rule = schemas.get(iRule);

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

        String otp = rule.getSchema().toLowerCase().replace("x", xStr).replace("y", yStr).replace("z", zStr);
        return new ROTP(otp, rule.getParts(), rule.getSchema());
    }

    public void overrideRules(List<ROTPSchema> rules) throws POCException, NoSuchAlgorithmException {
        if (rules == null || rules.isEmpty()) {
            throw new POCException("Invalid rule list");
        }
        this.schemas = rules;
        validate();
    }

    public int countMax() {
        return countMax(false);
    }

    public int countMax(boolean verbose) {
        if (verbose) {
            BasicConsoleLogger.get().info("start counting rule's otp...");
        }
        for (ROTPSchema rule : schemas) {
            int max = rule.getMaxOtp() + 1;
            for (int iOtp = 0; iOtp < max; iOtp++) {
                String candidateOtp = String.format("%0" + rule.getLength() + "d", iOtp);
                if (rule.isMatching(candidateOtp, verbose)) {
                    rOtpArray[iOtp] = candidateOtp;
                }
            }
        }
        int nOtp = 0;
        for (String rotp : rOtpArray) {
            if (rotp != null && !rotp.isEmpty()) {
                nOtp++;
            }
        }
        if (verbose) {
            BasicConsoleLogger.get().info("end counting rule's otp: " + nOtp);
        }
        return nOtp;
    }

    private ROTPSchema findFirstMatchingRule(String candidateOtp) throws POCException {
        for (ROTPSchema rule : schemas) {
            if (rule.isMatching(candidateOtp)) {
                return rule;
            }
        }
        throw new POCException("no rule is matching this otp");
    }

    public ROTP wrapToNext(String otp) throws POCException {
        int lookOtp = Integer.parseInt(otp);
        for (int i = lookOtp; i < rOtpArray.length; i++) {
            if (rOtpArray[i] != null && !rOtpArray[i].isEmpty()) {
                return new ROTP(rOtpArray[i], findFirstMatchingRule(rOtpArray[i]));
            }
        }
        for (int i = 0; i < lookOtp; i++) {
            if (rOtpArray[i] != null && !rOtpArray[i].isEmpty()) {
                return new ROTP(rOtpArray[i], findFirstMatchingRule(rOtpArray[i]));
            }
        }
        throw new POCException("Unable to match any ROTP");
    }

    public double getRotpFrequency() {
        return rotpFrequency;
    }

    public void setRotpFrequency(double rotpFrequency) throws POCException {
        if (rotpFrequency > 1.0) {
            throw new POCException("invalid rotp frequency value (0,1)");
        } else if (rotpFrequency < 0.0) {
            throw new POCException("invalid rotp frequency value (0,1)");
        }
        this.rotpFrequency = rotpFrequency;
    }

}
