/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */
package CommonUtils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Natheepan Ganeshamoorthy
 */
public class CommonUtils {

    public static final String CUSTOMER_ClientType = "C";
    public static final String EVENT_MANAGER_ClientType = "M";

    public static final String MORNING = "M";
    public static final String EVENING = "E";
    public static final String AFTERNOON = "A";

    public static final String TORONTO = "TOR";
    public static final String MONTREAL = "MTL";
    public static final String OTTAWA = "OTW";

    public static final String TORONTO_SERVER_NAME = "TORONTO";
    public static final String MONTREAL_SERVER_NAME = "MONTREAL";
    public static final String OTTAWA_SERVER_NAME = "OTTAWA";

    public static final int TORONTO_SERVER_PORT = 1111;
    public static final int MONTREAL_SERVER_PORT = 2222;
    public static final int OTTAWA_SERVER_PORT = 3333;

    public static final String CONFERENCE = "Conferences";
    public static final String SEMINAR = "Seminars";
    public static final String TRADESHOW = "TradeShows";

    public static final String OPERATIONFAILURE = "Operation Failure";

    public static void addFileHandler(Logger log, String fileName) throws SecurityException, IOException
    {
        log.setUseParentHandlers(false);
        FileHandler fileHandler = new FileHandler(System.getProperty("user.dir") + "/Records/" + fileName + ".log", true);
        log.addHandler(fileHandler);
        fileHandler.setFormatter(new SimpleFormatter());
    }

}
