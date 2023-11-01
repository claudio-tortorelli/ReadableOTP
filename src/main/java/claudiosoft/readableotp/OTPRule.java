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
        if (candidateOtp.length() != schema.length()) {
            return false;
        }
        if (candidateOtp.chars().distinct().count() != getDigits()) {
            return false;
        }
        boolean[] xyz = new boolean[3];
        xyz[0] = false;
        xyz[1] = false;
        xyz[2] = false;
        for (int i = 0; i < 10; i++) {
            String curDig = String.format("%d", i);
            if (candidateOtp.contains(curDig) && !xyz[0]) {
                candidateOtp = candidateOtp.replace(curDig, "x");
                xyz[0] = true;
            } else if (candidateOtp.contains(curDig) && !xyz[1]) {
                candidateOtp = candidateOtp.replace(curDig, "y");
                xyz[1] = true;
            } else if (candidateOtp.contains(curDig) && !xyz[2]) {
                candidateOtp = candidateOtp.replace(curDig, "z");
                xyz[2] = true;
            }
        }
        if (!candidateOtp.equalsIgnoreCase(schema)) {
            return false;
        }
        return true;
    }

    public void validate() {
        //TODO apply rule validation and throw exception if invalid
    }

}
