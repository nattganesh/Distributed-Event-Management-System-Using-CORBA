/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */
package Client;

import ServerImpl.MontrealServerImpl;
import ServerImpl.OttawaServerImpl;
import ServerImpl.TorontoServerImpl;
import ServerInterface.ServerInterface;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static CommonUtils.CommonUtils.*;

/**
 *
 * @author Natheepan Ganeshamoorthy, Gursimran Singh
 */
public class Client {

    private static Logger LOGGER;
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        while (true)
        {
            System.out.print("Enter Your ID Number: ");
            String id = scanner.next();
            System.out.println();
            if (id.length() != 8)
            {
                System.out.println("Invalid ID !!!");
            }
            else
            {
                String serverId = id.substring(0, 3).toUpperCase();
                String clientType = id.substring(3, 4).toUpperCase();
                String clientID = id.substring(4, 8).toUpperCase();
                if ((clientType.equals(CUSTOMER_ClientType) || clientType.equals(EVENT_MANAGER_ClientType))
                        && (serverId.equals(TORONTO) || serverId.equals(MONTREAL) || serverId.equals(OTTAWA))
                        && (clientID.matches("[0-9]+")))
                {
                    runClientService(clientType, serverId, clientID);
                }
                else
                {
                    System.out.println("Invalid ID !!!");
                }
            }
        }
    }

    private static void customerService(String serverId, String clientID)
    {
        ServerInterface server;
        try
        {
            String customerID = serverId + "C" + clientID;
            System.out.println("Welcome Customer " + customerID);
            Registry registry = LocateRegistry.getRegistry(getServerPort(serverId));
            LOGGER = Logger.getLogger(getServerClassName(serverId));
            addFileHandler(LOGGER, customerID);
            server = (ServerInterface) registry.lookup(getServerName(serverId));

            runCustomerMenu(server, customerID);
        }
        catch (SecurityException | IOException | NotBoundException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void eventManagerService(String serverId, String clientID)
    {
        ServerInterface server;
        try
        {
            String customerID = serverId + "M" + clientID;
            System.out.println("Welcome Manager " + clientID);
            Registry registry = LocateRegistry.getRegistry(getServerPort(serverId));
            LOGGER = Logger.getLogger(getServerClassName(serverId));
            addFileHandler(LOGGER, customerID);
            server = (ServerInterface) registry.lookup(getServerName(serverId));
            runManagerMenu(server, customerID);
        }
        catch (SecurityException | IOException | NotBoundException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void runClientService(String clientType, String serverId, String clientID)
    {
        switch (clientType)
        {
            case CUSTOMER_ClientType:
                customerService(serverId, clientID);
                break;
            case EVENT_MANAGER_ClientType:
                eventManagerService(serverId, clientID);
                break;
        }
    }

    private static int getServerPort(String serverId)
    {
        switch (serverId)
        {
            case TORONTO:
                return TORONTO_SERVER_PORT;
            case MONTREAL:
                return MONTREAL_SERVER_PORT;
            case OTTAWA:
                return OTTAWA_SERVER_PORT;
            default:
                return -1;
        }
    }

    private static String getServerName(String serverId)
    {
        switch (serverId)
        {
            case TORONTO:
                return TORONTO_SERVER_NAME;
            case MONTREAL:
                return MONTREAL_SERVER_NAME;
            case OTTAWA:
                return OTTAWA_SERVER_NAME;
            default:
                return "Server Does Not Exist";
        }
    }

    private static String getServerClassName(String serverId)
    {
        switch (serverId)
        {
            case TORONTO:
                return TorontoServerImpl.class.getName();
            case MONTREAL:
                return MontrealServerImpl.class.getName();
            case OTTAWA:
                return OttawaServerImpl.class.getName();
            default:
                return "Server Does Not Exist";
        }
    }

    private static void runCustomerMenu(ServerInterface server, String customerID) throws RemoteException
    {
        String itemNum = "";
        while (!itemNum.equals("0"))
        {
            System.out.println("============================");
            System.out.println("Customer Menu");
            System.out.println("0: Quit");
            System.out.println("1: Book Event");
            System.out.println("2: Get Booking Schedule");
            System.out.println("3: Cancel Event");
            System.out.println("============================");

            if (scanner.hasNextInt())
            {
                itemNum = scanner.next();
                switch (itemNum.trim())
                {
                    case "0":
                        System.out.println("Good Bye !!!");
                        System.exit(0);

                        break;
                    case "1":
                        runBookEvent(server, customerID);
                        break;
                    case "2":
                        runBookingSchedule(server, customerID,null);
                        break;
                    case "3":
                        System.out.println("Enter Event Type of The Event to Cancel? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
                        String eventType = getEventType();
                        System.out.println("Enter Event ID to Cancel: ");
                        String eventID = enterEventID();
                        String response = server.cancelEvent(customerID, eventID, eventType);
                        System.out.println("Response from server: " + response);
                        LOGGER.log(Level.INFO, "Response of server: {0}", response);
                        break;
                    default:
                        System.out.println("Invalid Choice !!!");
                        break;
                }
            }
            else
            {
                System.out.println("Please select a valid choice!");
                scanner.next();
            }
        }
        scanner.close();
    }

    private static void runManagerMenu(ServerInterface server, String managerID) throws RemoteException
    {
        int itemNum = -1;
        while (itemNum != 0)
        {
            System.out.println("============================");
            System.out.println("Manager Menu");
            System.out.println("0: Quit");
            System.out.println("1: Add Event");
            System.out.println("2: Remove Event");
            System.out.println("3: List Event Availability");
            System.out.println("4: Book Event");
            System.out.println("5: Get Booking Schedule");
            System.out.println("6: Cancel Event");
            System.out.println("============================");

            if (scanner.hasNextInt())
            {
                itemNum = scanner.nextInt();
                switch (itemNum)
                {
                    case 0:
                        System.out.println("Good Bye !!!");
                        System.exit(0);
                        break;
                    case 1:
                        System.out.println("What event do you wish to add?");
                        managerAddEvent(server, managerID);
                        break;
                    case 2:
                        System.out.println("What event do you wish to remove?");
                        managerRemoveEvent(server, managerID);
                        break;
                    case 3:
                        System.out.println("Which type of event you wish to list? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
                        managerListEvents(server, managerID);
                        break;
                    case 4:
                        System.out.println("What event do you wish to Book?");
                        runBookEvent(server, getCustomerID());
                        break;
                    case 5:
                        System.out.println("What event do you wish to get Booking Schedule for?");
                        runBookingSchedule(server, getCustomerID(), managerID);
                        break;
                    case 6:
                        System.out.println("What event do you wish to cancel?");
                        System.out.println("Enter Event Type of The Event to Cancel? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
                        String eventType = getEventType();
                        System.out.println("Enter Event ID to Cancel: ");
                        String eventID = enterEventID();
                        String customerID = getCustomerID();
                        String response = server.cancelEvent(customerID, eventID, eventType);
                        System.out.println("Response from server: " + response);
                        break;
                    default:
                        System.out.println("Invalid Choice !!!");
                        break;
                }
            }
            else
            {
                System.out.println("Please select a valid choice!");
                scanner.next();
            }
        }
        scanner.close();
    }

    private static boolean validateCustomerID(String id) {

        if (id.length() == 8) {
            String serverId = id.substring(0, 3).toUpperCase();
            String clientType = id.substring(3, 4).toUpperCase();
            String clientID = id.substring(4, 8).toUpperCase();
            return clientType.equals(CUSTOMER_ClientType)
                    && (serverId.equals(TORONTO) || serverId.equals(MONTREAL) || serverId.equals(OTTAWA))
                    && (clientID.matches("[0-9]+"));
        } else return false;
    }

    private static String getCustomerID() {
        System.out.println("Enter Customer ID: ");
        String customerID;
        while (true) {
            customerID = scanner.next().trim().toUpperCase();
            if (validateCustomerID(customerID)) {
                break;
            } else {
                System.out.println("Enter correct Customer ID!");
            }
        }
        return customerID;
    }

    private static String getEventType()
    {
        String eventType;

        while (true)
        {
            eventType = scanner.next().trim().toUpperCase();
            if (eventType.equals("A"))
            {
                eventType = CONFERENCE;
                break;
            }
            else if (eventType.equals("B"))
            {
                eventType = TRADESHOW;
                break;
            }
            else if (eventType.equals("C"))
            {
                eventType = SEMINAR;
                break;
            }
            else
            {
                System.out.println("Select an appropriate option!");
            }
        }
        return eventType;
    }

    private static void managerListEvents(ServerInterface server, String customerID)
    {
        try
        {
            String eventType = getEventType();
            String str = server.listEventAvailability(eventType, customerID);
            System.out.println(str);
            LOGGER.log(Level.INFO, "Response of Server: {0}", str);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void managerAddEvent(ServerInterface server, String managerID)
    {
        try
        {
            String eventID;
            String eventType;
            String bookingCapacity;
            System.out.print("Please enter Event id: ");
            eventID = enterEventID();
            System.out.println();
            System.out.println("Please enter Event Type: (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR) ");
            eventType = getEventType();
            System.out.println();
            System.out.print("Please enter Booking Capacity: ");
            bookingCapacity = validateBookingCapacity();
            LOGGER.log(Level.INFO, "Manager: {0} adding a new Event with Event id: {1} ,Event Type: {2} and Booking Capacity: {3}", new Object[]
            {
                managerID, eventID, eventType, bookingCapacity
            });
            String string = server.addEvent(eventID, eventType, bookingCapacity, managerID);
            LOGGER.log(Level.INFO, "Response of server: {0}", string);
            System.out.println("Response of server: " + string);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String validateBookingCapacity()
    {
        String returnBC;
        while (true)
        {
            String bc = scanner.nextLine().trim();
            if (isNumeric(bc))
            {
                returnBC = bc;
                break;
            }
            else
            {
                System.out.println("Please enter Valid Booking Capacity");
            }
        }
        return returnBC;
    }

    private static Integer validateIntBookingCapacity() {
        Integer returnBC;
        while (true) {
            String bc = scanner.nextLine().trim();
            if (isNumeric(bc)) {
                returnBC = Integer.parseInt(bc);
                break;
            } else {
                System.out.println("Please enter Valid Number");
            }
        }
        return returnBC;
    }

    private static String enterEventID()
    {
        String eventID = "";
        //scanner.nextLine();
        while (true)
        {
            String input = scanner.nextLine().trim();
            if (validateEventID(input))
            {
                eventID = input;
                break;
            }
            else
            {
                System.out.println("Enter a valid event ID!. E.g OTWA100519");
                //scanner.nextLine();
            }
        }
        return eventID;
    }

    private static boolean isNumeric(String str)
    {
        try
        {
            Double.parseDouble(str);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    private static boolean validateEventID(String s)
    {

        if (s.length() == 10)
        {
            String serverId = s.substring(0, 3).toUpperCase();
            String time = s.substring(3, 4).toUpperCase();
            String eventID = s.substring(4, 10).toUpperCase();

            return (time.equals(MORNING) || time.equals(EVENING) || time.equals(AFTERNOON))
                    && (serverId.equals(TORONTO) || serverId.equals(MONTREAL) || serverId.equals(OTTAWA))
                    && (eventID.matches("[0-9]+"));
        }
        else
        {
            return false;
        }

    }

    private static void managerRemoveEvent(ServerInterface server, String managerID)
    {
        // We need to check if the event is booked by a client before
        String eventID;
        String eventType;
        try
        {
            System.out.print("Please enter Event id: ");
            eventID = enterEventID();
            System.out.println();
            System.out.println("Please enter Event Type: (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR) ");
            eventType = getEventType();

            LOGGER.log(Level.INFO, "Manager {0} removing Event with Event ID {1} of type: {2}", new Object[]
            {
                managerID, eventID, eventType
            });
            String string = server.removeEvent(eventID, eventType, managerID);
            System.out.println("Response of the server: " + string);
            LOGGER.log(Level.INFO, "Response of server: {0}", string);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void runBookingSchedule(ServerInterface server, String customerID, String managerId) throws RemoteException
    {
        LOGGER.log(Level.INFO, "Booking Schedule Requested by {0}", customerID);
        System.out.println(customerID + "'s Bookings Schedule");
        String booking = server.getBookingSchedule(customerID, managerId);
        System.out.println(booking);

        if (!booking.equalsIgnoreCase(OPERATIONFAILURE))
        {
            LOGGER.log(Level.INFO, "Operation Sucessful. Records for {0} have been found", customerID);
            LOGGER.info(booking);
        }
        else
        {
            LOGGER.log(Level.INFO, "Operation Failure. Records for {0} do not exist.", customerID);
        }
    }

    private static void runBookEvent(ServerInterface server, String customerID) throws RemoteException
    {
        System.out.println("What type of event do you wish to book? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
        String eventType = getEventType();

        System.out.println("Enter Event ID: ");
        String eventID = enterEventID();
        System.out.println("Enter the number of people attending: ");
        Integer booking = validateIntBookingCapacity();
        if (booking > 0)
            {
                String book = booking.toString();
                String msg = server.bookEvent(customerID, eventID, eventType, book);
                LOGGER.info(msg);
                System.out.println(msg);
            } else
            {
                System.out.println("Invalid Number !!! number of people attending > 0");
            }
    }
}
