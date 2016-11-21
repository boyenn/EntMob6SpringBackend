package be.pxl.backend.helpers;

import com.mongodb.BasicDBObject;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Boyen on 21/11/2016.
 */
public class DBObjectToDate {
    public static Date dbObjectToDate(BasicDBObject dateObject){
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        c.set(Calendar.YEAR, dateObject.getInt("jaar"));
        c.set(Calendar.MONTH, dateObject.getInt("maand")-1);
        int dayOfMonth = tryOrNull(dateObject, "dag");
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth==0?1: dayOfMonth);
        c.set(Calendar.HOUR_OF_DAY, tryOrNull(dateObject, "uur"));
        c.set(Calendar.MINUTE, tryOrNull(dateObject, "minuut"));
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    private static int tryOrNull(BasicDBObject db, String field) {
        try {
            return db.getInt(field);
        } catch (NullPointerException ignored) {

        }
        return 0;
    }

}
