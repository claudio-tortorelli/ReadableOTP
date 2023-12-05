package claudiosoft.readableotp;

import claudiosoft.pocbase.BasicConsoleLogger;
import claudiosoft.pocbase.BasicUtils;
import claudiosoft.pocbase.POCException;
import static claudiosoft.readableotp.ROTPConstants.SCORE_NONE;
import java.security.NoSuchAlgorithmException;

/**
 * ReadableOTP
 *
 * @author Claudio Tortorelli
 */
public class ROTPSchema {

    private String schema;
    private String xRule;
    private String yRule;
    private String zRule;
    private int score;
    private int parts;

    public ROTPSchema(String schema, String xRule, int parts) {
        this(schema, xRule, parts, SCORE_NONE);
    }

    public ROTPSchema(String schema, String xRule, int parts, int score) {
        this(schema, xRule, null, parts, score);
    }

    public ROTPSchema(String schema, String xRule, String yRule, int parts) {
        this(schema, xRule, yRule, null, parts, SCORE_NONE);
    }

    public ROTPSchema(String schema, String xRule, String yRule, int parts, int score) {
        this(schema, xRule, yRule, null, parts, score);
    }

    public ROTPSchema(String schema, String xRule, String yRule, String zRule, int parts) {
        this(schema, xRule, yRule, zRule, parts, SCORE_NONE);
    }

    public ROTPSchema(String schema, String xRule, String yRule, String zRule, int parts, int score) {
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
        return isMatching(candidateOtp, false);
    }

    public boolean isMatching(String candidateOtp, boolean verbose) {
        String match = schema + " -> " + candidateOtp;
        if (candidateOtp.length() != schema.length()) {
            return false;
        }
        int nDig = getDigits();
        if (candidateOtp.chars().distinct().count() != nDig) {
            return false;
        }

        String[] digits = new String[3];
        digits[0] = null;
        digits[1] = null;
        digits[2] = null;

        for (char c : candidateOtp.toCharArray()) {
            if (digits[0] == null) {
                digits[0] = "" + c;
                continue;
            }
            if (digits[1] == null && !digits[0].equals("" + c)) {
                digits[1] = "" + c;
                continue;
            }
            if (digits[2] == null && !digits[0].equals("" + c) && !digits[1].equals("" + c)) {
                digits[2] = "" + c;
                break;
            }
        }

        int[] vals = new int[3];
        vals[0] = -1;
        vals[1] = -1;
        vals[2] = -1;
        vals[0] = Integer.parseInt(digits[0]);
        if (nDig > 1) {
            vals[1] = Integer.parseInt(digits[1]);
        }
        if (nDig > 2) {
            vals[2] = Integer.parseInt(digits[2]);
        }

        // match rules
        if (yRule != null && vals[1] != -1) {
            if (yRule.equals("!") && vals[0] == vals[1]) {
                return false;
            } else if (yRule.equals("+1") && vals[1] != vals[0] + 1) {
                return false;
            } else if (yRule.equals("-1") && vals[1] != vals[0] - 1) {
                return false;
            }
        }
        if (zRule != null && vals[1] != -1 && vals[2] != -1) {
            if (zRule.equals("!") && (vals[2] == vals[1] || vals[2] == vals[0])) {
                return false;
            } else if (zRule.equals("+1") && vals[2] != vals[1] + 1) {
                return false;
            } else if (zRule.equals("-1") && vals[2] != vals[1] - 1) {
                return false;
            }
        }

        // match schema
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
            if (verbose) {
                BasicConsoleLogger.get().info(match);
            }
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
            if (verbose) {
                BasicConsoleLogger.get().info(match);
            }
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
            if (verbose) {
                BasicConsoleLogger.get().info(match);
            }
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
            if (verbose) {
                BasicConsoleLogger.get().info(match);
            }
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
            if (verbose) {
                BasicConsoleLogger.get().info(match);
            }
            return true;
        }
        return false;
    }

    public boolean isEquivalentTo(ROTPSchema rule) throws NoSuchAlgorithmException {
        if (getId().equals(rule.getId())) {
            return true;
        }
        return false;
    }

    public void validate() throws POCException {
        throw new POCException("TODO apply rule validation and throw exception if invalid");
    }

    public String getId() throws NoSuchAlgorithmException {
        String input = schema;
        if (xRule != null) {
            input += xRule;
        }
        if (yRule != null) {
            input += yRule;
        }
        if (zRule != null) {
            input += zRule;
        }
        return new String(BasicUtils.getSHA256(input));
    }

}
