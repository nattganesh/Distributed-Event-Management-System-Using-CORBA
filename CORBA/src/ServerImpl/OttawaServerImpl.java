/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */
package ServerImpl;

//import ServerInterface.ServerInterface;
import EventManagementServerApp.ServerInterface;
import EventManagementServerApp.ServerInterfacePOA;
import org.omg.CORBA.ORB;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static CommonUtils.CommonUtils.*;

/**
 *
 * @author Gursimran Singh, Natheepan Ganeshamoorthy
 */
public class OttawaServerImpl extends ServerInterfacePOA {

    private ORB orb;
    private static HashMap<String, HashMap< String, String>> databaseOttawa = new HashMap<>();
    private static HashMap<String, HashMap<String, HashMap< String, Integer>>> customerEventsMapping = new HashMap<>();
    private static Logger logger;

    
    {
        //item1
        databaseOttawa.put(CONFERENCE, new HashMap<>());
        databaseOttawa.get(CONFERENCE).put("OTWM999999", "999");
        databaseOttawa.get(CONFERENCE).put("OTWE251023", "40");
        databaseOttawa.get(CONFERENCE).put("OTWA030326", "90");

        //item2
        databaseOttawa.put(SEMINAR, new HashMap<>());
        databaseOttawa.get(SEMINAR).put("OTWM140147", "50");
        databaseOttawa.get(SEMINAR).put("OTWE999999", "999");
        databaseOttawa.get(SEMINAR).put("OTWA260939", "90");

        //item6
        databaseOttawa.put(TRADESHOW, new HashMap<>());
        databaseOttawa.get(TRADESHOW).put("OTWM070728", "50");
        databaseOttawa.get(TRADESHOW).put("OTWE210322", "40");
        databaseOttawa.get(TRADESHOW).put("OTWA999999", "999");
    }

    public OttawaServerImpl()
    {
        super();
        logger = Logger.getLogger(OttawaServerImpl.class.getName());
        try
        {
            addFileHandler(logger, "Ottawa_Server");
        }
        catch (SecurityException | IOException ex)
        {
            Logger.getLogger(MontrealServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ORB getOrb() {
        return orb;
    }

    public void setOrb(ORB orb) {
        this.orb = orb;
    }

    @Override
    public synchronized String addEvent(String eventID, String eventType, String bookingCapacity, String managerID)
    {
        String message = null;
        
        if(!eventID.substring(0, 3).equals(OTTAWA))
        {
            message = "Operations Unsuccessful!. Event Not Added in Ottawa Server "
                    + "for Event ID: " + eventID + " Event Type: " + eventType + " because the Event ID: " + eventID + ""
                    + " is not of Ottawa format (OTW)";
            logger.info(message);

            return message;
        }
        
        logger.info("Received request to add an event with event id " + eventID + " , Event Type" + eventType
                + " & Booking Capacity " + bookingCapacity);
        if (!databaseOttawa.get(eventType).containsKey(eventID))
        {
            databaseOttawa.get(eventType).put(eventID, bookingCapacity);
            message = "Operations Successful!. Event Added in Ottawa Server for Event ID: "
                    + eventID + " Event Type: " + eventType + " Booking Capacity: " + bookingCapacity;
            logger.info(message);

            return message;
        }
        else
        {
            databaseOttawa.get(eventType).replace(eventID, bookingCapacity);
            message = "Operations Unsuccessful!. Event Not Added in Ottawa Server "
                    + "for Event ID: " + eventID + " Event Type: " + eventType + " because the Event ID: " + eventID + ""
                    + " is already added for the Event Type: " + eventType + ". But, the Booking Capacity is updated to " + bookingCapacity;
            logger.info(message);

            return message;
        }
    }

    @Override
    public synchronized String removeEvent(String eventID, String eventType, String managerID)
    {
        String message = null;
        if (databaseOttawa.get(eventType).containsKey(eventID))
        {
            if (customerEventsMapping != null)
            {
                for (String customer : customerEventsMapping.keySet())
                {
                    if (customerEventsMapping.get(customer).containsKey(eventType))
                    {
                        if (customerEventsMapping.get(customer).get(eventType).containsKey(eventID))
                        {
                            message += "\nCustomer ID: " + customer + " for event id " + eventID + " event Type " + eventType + " with customer booking of " + customerEventsMapping.get(customer).get(eventType).get(eventID) + " who was booked in this event has been removed from record.";
                            customerEventsMapping.get(customer).get(eventType).remove(eventID);
                        }
                    }
                }
            }
            
            databaseOttawa.get(eventType).remove(eventID);
            message = "Operations Successful!. Event Removed in Ottawa Server by Manager: " + managerID + " for Event ID: "
                    + eventID + " Event Type: " + eventType;
            logger.info(message);
            return message;
        }
        else
        {
            message = "Operations Unsuccessful!. Event Not Removed in Ottawa Server by Manager: " + managerID + " f"
                    + "or Event ID: " + eventID + " Event Type: " + eventType + " because the Event ID: " + eventID
                    + " does not exist";
            logger.info(message);
            return message;
        }
    }

    private static int serverPortSelection(String str)
    {
        str = str.substring(0, 3);
        if (str.equals(TORONTO))
        {
            return TORONTO_SERVER_PORT;
        }
        else if (str.equals(OTTAWA))
        {
            return OTTAWA_SERVER_PORT;
        }
        else if (str.equals(MONTREAL))
        {
            return MONTREAL_SERVER_PORT;
        }
        return 0;
    }

    @Override
    public synchronized String listEventAvailability(String eventType, String managerID)
    {
        //Eg: Seminars - MTLE130519 3, OTWA060519 6, TORM180519 0, MTLE190519 2.
        String message = null;
        StringBuilder returnMessage = new StringBuilder();

        if (managerID.substring(0, 3).equals(MONTREAL))
        {
            logger.info("Requesting other server from Server: " + TORONTO_SERVER_NAME);
            String torrontoEvents = requestToOtherServers(managerID, null, null, 3, eventType, TORONTO_SERVER_PORT, null, null, null);
            logger.info("Requesting other server from Server: " + OTTAWA_SERVER_NAME);
            String ottawaEvents = requestToOtherServers(managerID, null, null, 3, eventType, OTTAWA_SERVER_PORT, null, null, null);
            returnMessage.append(torrontoEvents).append("\n\n").append(ottawaEvents).append("\n\n");

        }
        if (managerID.substring(0, 3).equals(TORONTO))
        {
            logger.info("Requesting other server from Server: " + MONTREAL_SERVER_NAME);
            String montrealEvents = requestToOtherServers(managerID, null, null, 3, eventType, MONTREAL_SERVER_PORT, null, null, null);
            logger.info("Requesting other server from Server: " + OTTAWA_SERVER_NAME);
            String ottawaEvents = requestToOtherServers(managerID, null, null, 3, eventType, OTTAWA_SERVER_PORT, null, null, null);

            returnMessage.append(ottawaEvents).append("\n\n").append(montrealEvents).append("\n\n");
        }
        if (managerID.substring(0, 3).equals(OTTAWA))
        {
            logger.info("Requesting other server from Server: " + MONTREAL_SERVER_NAME);
            String montrealEvents = requestToOtherServers(managerID, null, null, 3, eventType, MONTREAL_SERVER_PORT, null, null, null);
            logger.info("Requesting other server from Server: " + TORONTO_SERVER_NAME);
            String torrontoEvents = requestToOtherServers(managerID, null, null, 3, eventType, TORONTO_SERVER_PORT, null, null, null);

            returnMessage.append(torrontoEvents).append("\n\n").append(montrealEvents).append("\n\n");
        }

        if (!databaseOttawa.get(eventType).isEmpty())
        {
            for (Map.Entry<String, String> entry : databaseOttawa.get(eventType).entrySet())
            {
                returnMessage.append("EventID: " + entry.getKey() + "| Booking Capacity " + entry.getValue() + "\n");
            }
            message = "Operation Successful, List of events retrieved for Event Type: " + eventType + " by Manager: " + managerID + "in server" + OTTAWA_SERVER_NAME;
            logger.info(message);

            return returnMessage.toString();
        }
        else
        {
            message = "Operation UnSuccessful, List of events not retrieved for Event Type: " + eventType + " by Manager: " + managerID + " in server " + OTTAWA_SERVER_NAME;
            logger.info(message);
            return message;
        }

    }

    @Override
    public synchronized String bookEvent(String customerID, String eventID, String eventType, String bookingAmount)
    {
        if (!customerID.substring(0, 3).equals(OTTAWA) && !customerID.substring(0, 3).equals(eventID.substring(0, 3)))
        {
            int customerBookingsCurrent = Integer.parseInt(this.nonOriginCustomerBooking(customerID, eventID));
            int customerBookingsOther = customerID.substring(0, 3).equals(MONTREAL) ? Integer.parseInt(requestToOtherServers(customerID, eventID, null, 7, null, TORONTO_SERVER_PORT, null, null, null).trim())
                    : Integer.parseInt(requestToOtherServers(customerID, eventID, null, 7, null, MONTREAL_SERVER_PORT, null, null, null).trim());

            if (customerBookingsCurrent + customerBookingsOther >= 3)
            {
                logger.log(Level.INFO, "Operation Unsuccessful, Book Event Requested by {0} for Event Type {1} with Event ID {2} cannot be booked. Customer can book as many events in his/her own city, but only at most 3 events from other cities overall in a month", new Object[]
                {
                    customerID, eventType, eventID
                });
                return "Operation Unsuccessful, Book Event Requested by " + customerID + " for Event Type " + eventType + " with Event ID " + eventID + " cannot be booked. Customer can book as many events in his/her own\n"
                        + "city, but only at most 3 events from other cities overall in a month";
            }
        }
        
        if (eventID.substring(0, 3).equals(OTTAWA))
        {
            logger.log(Level.INFO, "Book Event Requested by {0} for Event Type {1} with Event ID {2}", new Object[]
            {
                customerID, eventType, eventID
            });
            HashMap< String, String> event = databaseOttawa.get(eventType);
            if (event.containsKey(eventID))
            {
                if (customerEventsMapping.containsKey(customerID) && customerEventsMapping.get(customerID).containsKey(eventType))
                {
                    if (customerEventsMapping.get(customerID).get(eventType).containsKey(eventID))
                    {
                        logger.log(Level.INFO, "Operation Unsuccessful, Book Event Requested by {0} for Event Type {1} with Event ID {2} cannot be booked. Customer already booked for this event.", new Object[]
                        {
                            customerID, eventType, eventID
                        });
                        return "Operation Unsuccessful, Book Event Requested by " + customerID + " for Event Type " + eventType + " with Event ID " + eventID + " cannot be booked. Customer already booked for this event.";
                    }
                }
                int bookingLeft = Integer.parseInt(event.get(eventID).trim());
                String tempBookingAmount = bookingAmount.replaceAll("[^\\d.]", "");
                int bookingRequested = Integer.parseInt(tempBookingAmount);
                if (bookingLeft >= bookingRequested)
                {
                    bookingLeft -= bookingRequested;
                    event.put(eventID, "" + bookingLeft);

                    customerEventsMapping.putIfAbsent(customerID, new HashMap<>());
                    customerEventsMapping.get(customerID).putIfAbsent(eventType, new HashMap<>());
                    customerEventsMapping.get(customerID).get(eventType).put(eventID, bookingRequested);

                    logger.log(Level.INFO, "Operation Successful, Book Event Requested by {0} for Event Type {1} with Event ID {2} has been booked.", new Object[]
                    {
                        customerID, eventType, eventID
                    });
                    return "Operation Successful, Book Event Requested by " + customerID + " for Event Type " + eventType + " with Event ID " + eventID + " has been booked.";
                }
                else
                {
                    logger.log(Level.INFO, "Operation Unsuccessful, Book Event Requested by {0} for Event Type {1} with Event ID {2} cannot be booked. Event Capacity < Booking Capacity Requested", new Object[]
                    {
                        customerID, eventType, eventID
                    });
                    return "Operation Unsuccessful, Book Event Requested by " + customerID + " for Event Type " + eventType + " with Event ID " + eventID + " cannot be booked. Event Capacity < Booking Capacity Requested";
                }
            }
            else
            {
                logger.log(Level.INFO, "Operation Unsuccessful, Book Event Requested by {0} for Event Type {1} with Event ID {2} cannot be booked. Event Does Not Exist.", new Object[]
                {
                    customerID, eventType, eventID
                });
                return "Operation Unsuccessful, Book Event Requested by " + customerID + " for Event Type " + eventType + " with Event ID " + eventID + " cannot be booked. Event Does Not Exist.";
            }
        }
        if (eventID.substring(0, 3).equals(TORONTO))
        {
            return requestToOtherServers(customerID, eventID, bookingAmount, 4, eventType, TORONTO_SERVER_PORT, null, null, null);
        }
        if (eventID.substring(0, 3).equals(MONTREAL))
        {
            return requestToOtherServers(customerID, eventID, bookingAmount, 4, eventType, MONTREAL_SERVER_PORT, null, null, null);
        }
        return "";
    }

    @Override
    public synchronized String nonOriginCustomerBooking(String customerID, String eventID)
    {
        int numberOfCustomerEvents = 0;
        if (customerEventsMapping.containsKey(customerID))
        {
            if (customerEventsMapping.get(customerID).containsKey(CONFERENCE))
            {
                for (String currentEventID : customerEventsMapping.get(customerID).get(CONFERENCE).keySet())
                {
                    if (eventID.substring(6, 8).equals(currentEventID.substring(6, 8)))
                    {
                        numberOfCustomerEvents++;
                    }
                }
            }
            if (customerEventsMapping.get(customerID).containsKey(SEMINAR))
            {
                for (String currentEventID : customerEventsMapping.get(customerID).get(SEMINAR).keySet())
                {
                    if (eventID.substring(6, 8).equals(currentEventID.substring(6, 8)))
                    {
                        numberOfCustomerEvents++;
                    }
                }
            }
            if (customerEventsMapping.get(customerID).containsKey(TRADESHOW))
            {
                for (String currentEventID : customerEventsMapping.get(customerID).get(TRADESHOW).keySet())
                {
                    if (eventID.substring(6, 8).equals(currentEventID.substring(6, 8)))
                    {
                        numberOfCustomerEvents++;
                    }
                }
            }
        }
        return "" + numberOfCustomerEvents;
    }

    @Override
    public synchronized String getBookingSchedule(String customerID, String managerID)
    {
        String returnMsg = "";
        if(managerID == null || managerID.equalsIgnoreCase("Default")) managerID = "null";
        if(managerID.equals("null"))
            logger.log(Level.INFO, "Booking Schedule Requested by {0}", customerID);
        else
            logger.log(Level.INFO, "Booking Schedule Requested by {0} for customer {1}", new Object[] {managerID, customerID});
        HashMap<String, HashMap< String, Integer>> customerEvents = customerEventsMapping.get(customerID);

        if ((customerID.substring(0, 3).equals(OTTAWA) && managerID.equals("null"))||(!managerID.equals("null") && managerID.substring(0, 3).equals(OTTAWA)))
        {
            returnMsg += requestToOtherServers(customerID, null, null, 5, null, TORONTO_SERVER_PORT, "null", null, null);
            returnMsg += requestToOtherServers(customerID, null, null, 5, null, MONTREAL_SERVER_PORT, "null", null, null);
        }
        if (customerEvents != null && !customerEvents.isEmpty())
        {
            HashMap< String, Integer> customerConferenceEventID = customerEvents.get(CONFERENCE);
            HashMap< String, Integer> customerSeminarEventID = customerEvents.get(SEMINAR);
            HashMap< String, Integer> customerTradeshowEventID = customerEvents.get(TRADESHOW);

            if (customerConferenceEventID != null && !customerConferenceEventID.isEmpty())
            {
                returnMsg += "\nFor Conference Events in Ottawa: ";
                for (String event : customerConferenceEventID.keySet())
                {
                    returnMsg += "\nEvent ID: " + event + " Booking for " + customerConferenceEventID.get(event);
                }
            }
            if (customerSeminarEventID != null && !customerSeminarEventID.isEmpty())
            {
                returnMsg += "\nFor Seminar Events in Ottawa: ";
                for (String event : customerSeminarEventID.keySet())
                {
                    returnMsg += "\nEvent ID: " + event + " Booking for " + customerSeminarEventID.get(event);
                }
            }
            if (customerTradeshowEventID != null && !customerTradeshowEventID.isEmpty())
            {
                returnMsg += "\nFor Tradeshow Events in Ottawa: ";
                for (String event : customerTradeshowEventID.keySet())
                {
                    returnMsg += "\nEvent ID: " + event + " Booking for " + customerTradeshowEventID.get(event);
                }
            }
            if (!returnMsg.trim().equals(""))
            {
                logger.log(Level.INFO, "Operation Sucessful. Records for {0} have been found", customerID);
            }
        }
        if (returnMsg.trim().equals(""))
        {
            logger.log(Level.INFO, "Records for {0} do not exist.", customerID);
            if ((customerID.substring(0, 3).equals(OTTAWA) && managerID.equals("null"))||(!managerID.equals("null") && managerID.substring(0, 3).equals(OTTAWA)))
            {
                returnMsg += "\nRecords for " + customerID + " do not exist.";
            }
        }
        return returnMsg;
    }

    @Override
    public synchronized String cancelEvent(String customerID, String eventID, String eventType)
    {
        switch (eventID.substring(0, 3))
        {
            case OTTAWA:
                if (customerEventsMapping.containsKey(customerID))
                {
                    if (customerEventsMapping.get(customerID).containsKey(eventType) && customerEventsMapping.get(customerID).get(eventType).containsKey(eventID))
                    {
                        Integer bookValue = customerEventsMapping.get(customerID).get(eventType).remove(eventID);
                        Integer currentValue = 0;
                        Integer sum = 0;
                        
                        if (databaseOttawa.get(CONFERENCE).containsKey(eventID))
                        {
                            currentValue = Integer.parseInt(databaseOttawa.get(CONFERENCE).get(eventID));
                            sum = currentValue + bookValue;
                            databaseOttawa.get(CONFERENCE).put(eventID, sum.toString());
                        }
                        else if (databaseOttawa.get(SEMINAR).containsKey(eventID))
                        {
                            currentValue = Integer.parseInt(databaseOttawa.get(SEMINAR).get(eventID));
                            sum = currentValue + bookValue;
                            databaseOttawa.get(SEMINAR).put(eventID, sum.toString());
                        }
                        else if (databaseOttawa.get(TRADESHOW).containsKey(eventID))
                        {
                            currentValue = Integer.parseInt(databaseOttawa.get(TRADESHOW).get(eventID));
                            sum = currentValue + bookValue;
                            databaseOttawa.get(TRADESHOW).put(eventID, sum.toString());
                        }
                        logger.log(Level.INFO, "This event has been removed from customer record.");
                        return "This event has been removed from customer record.";
                    }
                }
                logger.log(Level.INFO, "This event does not exist in customer record.");
                return "This event does not exist in customer record.";
            case TORONTO:
                return requestToOtherServers(customerID, eventID, null, 6, eventType, TORONTO_SERVER_PORT, null, null, null);
            case MONTREAL:
                return requestToOtherServers(customerID, eventID, null, 6, eventType, MONTREAL_SERVER_PORT, null, null, null);
            default:
                break;
        }
        return null;
    }

    public String requestToOtherServers(String userID, String eventID, String bookingCapacity, int serverNumber, String eventType, int serPort, String managerId, String newEventID, String newEventType)
    {
        int serverPort;
//        if (eventID != null)
//        {
//            serverPort = serverPortSelection(eventID);
//        }
//        else
//        {
            serverPort = serPort;
//        }
        String stringServer = Integer.toString(serverNumber);
        DatagramSocket aSocket = null;
        String response = null;
        String userIDName = userID != null ? userID : "Default";
        String eventTypeName = eventType != null ? eventType : "Default";
        String eventIDName = eventID != null ? eventID : "Default";
        String bookingCap = bookingCapacity != null ? bookingCapacity : "Default";
        String managerID = managerId != null ? managerId : "Default";
        String new_EventID = newEventID != null ? newEventID : "Default";
        String new_EventType = newEventType != null ? newEventType : "Default";

        try
        {
            aSocket = new DatagramSocket();
            String message = userIDName.concat(" ").concat(eventIDName).concat(" ").concat(stringServer).concat(" ").concat(eventTypeName).concat(" ").concat(bookingCap).concat(" ").concat(managerID).concat(" ").concat(new_EventID).concat(" ").concat(new_EventType);
            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(), host, serverPort);
            aSocket.send(sendPacket);
            logger.info("Request send " + sendPacket.getData());
            byte[] receiveBuffer = new byte[1500];
            DatagramPacket recievedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            aSocket.receive(recievedPacket);
            response = new String(recievedPacket.getData());
            logger.info("Reply received" + response);
        }
        catch (IOException e)
        {
            
        }
        finally
        {
            if (aSocket != null)
            {
                aSocket.close();
            }
        }
        return response;
    }

    @Override
    public String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType)
    {
        boolean isNewEventValid = false;
        boolean isOldEventValid = false;
        boolean isCustomerEligibleToBook = true;

        if (newEventID.substring(0, 3).equals(OTTAWA))
        {
            isNewEventValid = eventAvailable(newEventID, newEventType).trim().equals("1");
        }
        else
        {
            isNewEventValid = requestToOtherServers(customerID, oldEventID, null, 9, oldEventType, newEventID.substring(0, 3).equals(MONTREAL) ? MONTREAL_SERVER_PORT : TORONTO_SERVER_PORT, null, newEventID, newEventType).trim().equals("1");
        }

        if (!isNewEventValid)
        {
            logger.log(Level.INFO, "Operation Unsuccessful, Swap Event Requested by {0} for New Event Type {1} with New Event ID {2} with Old Event Type {3} with old Event ID {4}  cannot be swaped. "
                    + "New Event is Invalid", new Object[]
                    {
                        customerID, newEventType, newEventID, oldEventType, oldEventID
                    });
            return "Operation Unsuccessful, Swap Event Requested by " + customerID + " for New Event Type " + newEventType + " with New Event ID " + newEventID + " with Old Event Type " + oldEventType + " with old Event ID " + oldEventID + " cannot be swaped. "
                    + "\nNew Event is Invalid";
        }

        if (oldEventID.substring(0, 3).equals(OTTAWA))
        {
            isOldEventValid = validateBooking(customerID, oldEventID, oldEventType).trim().equals("1");
        }
        else
        {
            isOldEventValid = requestToOtherServers(customerID, oldEventID, null, 10, oldEventType, oldEventID.substring(0, 3).equals(MONTREAL) ? MONTREAL_SERVER_PORT : TORONTO_SERVER_PORT, null, newEventID, newEventType).trim().equals("1");
        }

        if (!isOldEventValid)
        {
            logger.log(Level.INFO, "Operation Unsuccessful, Swap Event Requested by {0} for New Event Type {1} with New Event ID {2} with Old Event Type {3} with old Event ID {4}  cannot be swaped. "
                    + "Old Event is Invalid", new Object[]
                    {
                        customerID, newEventType, newEventID, oldEventType, oldEventID
                    });
            return "Operation Unsuccessful, Swap Event Requested by " + customerID + " for New Event Type " + newEventType + " with New Event ID " + newEventID + " with Old Event Type " + oldEventType + " with old Event ID " + oldEventID + " cannot be swaped. "
                    + "\nOld Event is Invalid";
        }
        
        if(customerID.substring(0, 3).equals(OTTAWA) && newEventID.substring(0, 3).equals(OTTAWA)) isCustomerEligibleToBook = true;
        else if(!customerID.substring(0, 3).equals(oldEventID.substring(0, 3)) && !oldEventID.substring(0, 3).equals(OTTAWA)) isCustomerEligibleToBook = true;
        else if (!customerID.substring(0, 3).equals(OTTAWA) && !customerID.substring(0, 3).equals(newEventID.substring(0, 3)))
        {
            int customerBookingsCurrent = Integer.parseInt(this.nonOriginCustomerBooking(customerID, newEventID));
            int customerBookingsOther = customerID.substring(0, 3).equals(MONTREAL) ? Integer.parseInt(requestToOtherServers(customerID, newEventID, null, 7, null, TORONTO_SERVER_PORT, null, null, null).trim())
                    : Integer.parseInt(requestToOtherServers(customerID, newEventID, null, 7, null, MONTREAL_SERVER_PORT, null, null, null).trim());

            if (customerBookingsCurrent + customerBookingsOther >= 3)
            {
                isCustomerEligibleToBook = false;
                logger.log(Level.INFO, "Operation Unsuccessful, Swap Event Requested by {0} for New Event Type {1} with New Event ID {2} with Old Event Type {3} with old Event ID {4}  cannot be swaped. "
                                     + "Customer can book as many events in his/her own city, but only at most 3 events from other cities overall in a month", new Object[]
                {
                    customerID, newEventType, newEventID, oldEventType, oldEventID
                });
                return "Operation Unsuccessful, Swap Event Requested by " + customerID + " for New Event Type " + newEventType + " with New Event ID " + newEventID + " with Old Event Type " + oldEventType + " with old Event ID " + oldEventID + " cannot be swaped. "
                        + "\nCustomer can book as many events in his/her own city, but only at most 3 events from other cities overall in a month";
            }
        }

        if (isNewEventValid && isOldEventValid && isCustomerEligibleToBook)
        {
            String msg = "";
            try
            {
                msg = cancelEvent(customerID, oldEventID, oldEventType) + "\n" + bookEvent(customerID, newEventID, newEventType, "1") + "\n Events Have Been Swapped";
                logger.log(Level.INFO, msg);
                logger.log(Level.INFO, "Operation successful, Swap Event Requested by {0} for New Event Type {1} with New Event ID {2} with Old Event Type {3} with old Event ID {4}  has been swaped. ", new Object[]
                {
                    customerID, newEventType, newEventID, oldEventType, oldEventID
                });
                return msg + "\nOperation successful, Swap Event Requested by " + customerID + " for New Event Type " + newEventType + " with New Event ID " + newEventID + " with Old Event Type " + oldEventType + " with old Event ID " + oldEventID + " has been swaped. ";
            }
            catch (Exception ex)
            {

            }
        }

        return "Operation Unsuccessful";
    }

    @Override
    public String eventAvailable(String eventID, String eventType)
    {
        eventType = eventType.substring(0,3).equalsIgnoreCase("CON")? CONFERENCE : eventType.substring(0,3).equalsIgnoreCase("SEM")? SEMINAR : TRADESHOW;
        return (databaseOttawa.containsKey(eventType) && databaseOttawa.get(eventType).containsKey(eventID) && Integer.parseInt(databaseOttawa.get(eventType).get(eventID)) > 0) ? "1" : "0";
    }

    @Override
    public String validateBooking(String customerID, String eventID, String eventType)
    {
        eventType = eventType.substring(0,3).equalsIgnoreCase("CON")? CONFERENCE : eventType.substring(0,3).equalsIgnoreCase("SEM")? SEMINAR : TRADESHOW;
        return (customerEventsMapping.containsKey(customerID) && customerEventsMapping.get(customerID).containsKey(eventType)  && customerEventsMapping.get(customerID).get(eventType).containsKey(eventID)) ? "1" : "0";
    }
}

// MTLA090619 TORE050619 TORC1234