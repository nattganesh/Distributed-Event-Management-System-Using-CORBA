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

    public static enum InputType 
    {
        CLIENT_ID, EVENT_ID
    };

    public static void addFileHandler(Logger log, String fileName) throws SecurityException, IOException
    {
        log.setUseParentHandlers(false);
        FileHandler fileHandler = new FileHandler(System.getProperty("user.dir") + "/CORBA/Records/" + fileName + ".log", true);
        log.addHandler(fileHandler);
        fileHandler.setFormatter(new SimpleFormatter());
    }

    public static boolean isInputValid(String id, InputType type)
    {
        if(type == InputType.CLIENT_ID)
        {
            String serverId   = id.substring(0, 3);
            String clientType = id.substring(3, 4);
            String clientID   = id.substring(4, 8);
                
            return id.length() == 8 && (clientType.equals(CUSTOMER_ClientType) || clientType.equals(EVENT_MANAGER_ClientType))
                                    && (serverId.equals(TORONTO) || serverId.equals(MONTREAL) || serverId.equals(OTTAWA))
                                    && (clientID.matches("^[0-9]+$"));
        }
        
        if(type == InputType.EVENT_ID)
        {
            String serverId  = id.substring(0, 3);
            String eventType = id.substring(3, 4);
            String day       = id.substring(4, 6);
            String month     = id.substring(6, 8);
            String year      = id.substring(8, 10);
                
            return id.length() == 10 && (eventType.equals(MORNING) || eventType.equals(EVENING) || eventType.equals(AFTERNOON))
                                     && (serverId.equals(TORONTO) || serverId.equals(MONTREAL) || serverId.equals(OTTAWA))
                                     && (day.length() == 2 && day.matches("0[1-9]|[1-2][0-9]|3[0-1]")) 
                                     && (month.length() == 2 && month.matches("0[1-9]|1[0-2]")) 
                                     && (year.length() == 2 && year.matches("19|[2-9][0-9]"));
        }
        return false;
    }
    
    public static String capitalize(String input)
    {
        return input.toUpperCase();
    }

}
