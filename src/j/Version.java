package j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Version {
    private static Version version;

    public static String str() {
        return get().getVersion();
    }

    static Version get() {
        if (version != null)
            return version;
        version = new Version();
        return version;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy");
    private String latest = "0.7";
    private Date reldate;

    private Version() {
        try {
            reldate = sdf.parse("04-Apr-07");
        } catch (ParseException e) {
            throw new RuntimeException("You botched the date");
        }
    }

    public String getVersion() {
        return latest;
    }

    public String getVersionDate() {
        return sdf.format(reldate);
    }
}
