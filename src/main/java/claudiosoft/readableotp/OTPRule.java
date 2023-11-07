package claudiosoft.readableotp;

import claudiosoft.pocbase.BasicConsoleLogger;
import static claudiosoft.readableotp.OTPConstants.SCORE_NONE;

/**
 *
 * @author Claudio
 */
public class OTPRule {

    private String schema;
    private String xRule;
    private String yRule;
    private String zRule;
    private int score;
    private int parts;

    public OTPRule(String schema, String xRule, int parts) {
        this(schema, xRule, parts, SCORE_NONE);
    }

    public OTPRule(String schema, String xRule, int parts, int score) {
        this(schema, xRule, null, parts, score);
    }

    public OTPRule(String schema, String xRule, String yRule, int parts, int score) {
        this(schema, xRule, yRule, null, parts, score);
    }

    public OTPRule(String schema, String xRule, String yRule, String zRule, int parts, int score) {
        this.schema = schema;
        this.xRule = xRule;
        this.yRule = yRule;
        this.zRule = zRule;
        this.score = score;
        this.parts = parts;
        BasicConsoleLogger.get().debug("added rule: " + schema);
    }

    public String getSchema() {
        return schema;
    }

    public String getxRule() {
        return xRule;
    }

    public String getyRule() {
        return yRule;
    }

    public String getzRule() {
        return zRule;
    }

    public int getScore() {
        return score;
    }

    public int getParts() {
        return parts;
    }

    public int getDigits() {
        int nDigits = 1;
        if (schema.contains("y")) {
            nDigits++;
        }
        if (schema.contains("z")) {
            nDigits++;
        }
        return nDigits;
    }

    public int getMaxOtp() {
        return Integer.parseInt(schema.replace("x", "9").replace("y", "9").replace("z", "9"));
    }

    public int getLength() {
        return schema.length();
    }

    public boolean isMatching(String candidateOtp) {
        String match = schema + " -> " + candidateOtp;
        if (candidateOtp.length() != schema.length()) {
            return false;
        }
        if (candidateOtp.chars().distinct().count() != getDigits()) {
            return false;
        }
        String[] digits = new String[3];
        digits[0] = null;
        digits[1] = null;
        digits[2] = null;

        for (int i = 0; i <= 9; i++) {
            String curDig = String.format("%d", i);
            if (candidateOtp.contains(curDig) && digits[0] == null) {
                digits[0] = curDig;
                continue;
            }
            if (candidateOtp.contains(curDig) && digits[1] == null) {
                digits[1] = curDig;
                continue;
            }
            if (candidateOtp.contains(curDig) && digits[2] == null) {
                digits[2] = curDig;
                break;
            }
        }

        String curCandidate = candidateOtp;
        if (digits[0] != null) {
            curCandidate = curCandidate.replace(digits[0], "x");
        }
        if (digits[1] != null) {
            curCandidate = curCandidate.replace(digits[1], "y");
        }
        if (digits[2] != null) {
            curCandidate = curCandidate.replace(digits[2], "z");
        }
        if (curCandidate.equalsIgnoreCase(schema)) {
            BasicConsoleLogger.get().info(match);
            return true;
        }

        curCandidate = candidateOtp;
        if (digits[0] != null) {
            curCandidate = curCandidate.replace(digits[0], "y");
        }
        if (digits[1] != null) {
            curCandidate = curCandidate.replace(digits[1], "x");
        }
        if (digits[2] != null) {
            curCandidate = curCandidate.replace(digits[2], "z");
        }
        if (curCandidate.equalsIgnoreCase(schema)) {
            BasicConsoleLogger.get().info(match);
            return true;
        }

        curCandidate = candidateOtp;
        if (digits[0] != null) {
            curCandidate = curCandidate.replace(digits[0], "y");
        }
        if (digits[1] != null) {
            curCandidate = curCandidate.replace(digits[1], "z");
        }
        if (digits[2] != null) {
            curCandidate = curCandidate.replace(digits[2], "x");
        }
        if (curCandidate.equalsIgnoreCase(schema)) {
            BasicConsoleLogger.get().info(match);
            return true;
        }

        curCandidate = candidateOtp;
        if (digits[0] != null) {
            curCandidate = curCandidate.replace(digits[0], "z");
        }
        if (digits[1] != null) {
            curCandidate = curCandidate.replace(digits[1], "y");
        }
        if (digits[2] != null) {
            curCandidate = curCandidate.replace(digits[2], "x");
        }
        if (curCandidate.equalsIgnoreCase(schema)) {
            BasicConsoleLogger.get().info(match);
            return true;
        }

        curCandidate = candidateOtp;
        if (digits[0] != null) {
            curCandidate = curCandidate.replace(digits[0], "y");
        }
        if (digits[1] != null) {
            curCandidate = curCandidate.replace(digits[1], "z");
        }
        if (digits[2] != null) {
            curCandidate = curCandidate.replace(digits[2], "x");
        }
        if (curCandidate.equalsIgnoreCase(schema)) {
            BasicConsoleLogger.get().info(match);
            return true;
        }

        return false;
    }

    public void validate() {
        //TODO apply rule validation and throw exception if invalid
    }

}
