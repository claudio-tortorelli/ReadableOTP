package claudiosoft.readableotp;

import static claudiosoft.readableotp.ROTPConstants.*;

/**
 * ReadableOTP
 *
 * @author Claudio Tortorelli
 */
public class ROTP {

    private String otp;
    private int parts;
    private String schema;

    public ROTP(String otp, int parts) {
        this(otp, parts, "");
    }

    public ROTP(String otp, ROTPSchema rule) {
        this(otp, rule.getParts(), rule.getSchema());
    }

    public ROTP(String otp, int parts, String schema) {
        this.otp = otp;
        this.parts = parts;
        this.schema = schema;
    }

    public String get() {
        String ret = otp;
        int len = otp.length();
        if (parts == PART_2) {
            String part1 = ret.substring(0, len / 2);
            String part2 = ret.substring(len / 2, len);
            ret = String.format("%s %s", part1, part2);
        } else {
            String part1 = ret.substring(0, len / 3);
            String part2 = ret.substring(len / 3, (len / 3) * 2);
            String part3 = ret.substring((len / 3) * 2, len);
            ret = String.format("%s %s %s", part1, part2, part3);
        }
        return ret;
    }

    public String getSchema() {
        if (schema == null) {
            schema = "";
        }
        return schema;
    }
}
