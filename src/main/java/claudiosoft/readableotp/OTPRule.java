package claudiosoft.readableotp;

import claudiosoft.pocbase.BasicConsoleLogger;

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

    public OTPRule(String schema, String xRule, int score, int parts) {
        this(schema, xRule, null, score, parts);
    }

    public OTPRule(String schema, String xRule, String yRule, int score, int parts) {
        this(schema, xRule, yRule, null, score, parts);
    }

    public OTPRule(String schema, String xRule, String yRule, String zRule, int score, int parts) {
        this.schema = schema;
        this.xRule = xRule;
        this.yRule = yRule;
        this.zRule = zRule;
        this.score = score;
        this.parts = parts;
        BasicConsoleLogger.get().info("added rule: " + schema);
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

}
