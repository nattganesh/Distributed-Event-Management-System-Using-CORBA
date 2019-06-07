/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */
package ServerImpl;

import ServerInterface.ServerInterface;

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
public class MontrealServerImpl extends UnicastRemoteObject implements ServerInterface {

    private static HashMap<String, HashMap< String, String>> databaseMontreal = new HashMap<>();
    private static HashMap<String, HashMap<String, HashMap< String, Integer>>> customerEventsMapping = new HashMap<>();
    private static Logger logger;

    //Events Database
    {
        //item1
        databaseMontreal.put(CONFERENCE, new HashMap<>());
        databaseMontreal.get(CONFERENCE).put("MTLM130720", "50");
        databaseMontreal.get(CONFERENCE).put("MTLE031219", "60");
        databaseMontreal.get(CONFERENCE).put("MTLA230721", "90");

        //item2
        databaseMontreal.put(SEMINAR, new HashMap<>());
        databaseMontreal.get(SEMINAR).put("MTLM310522", "20");
        databaseMontreal.get(SEMINAR).put("MTLE050620", "40");
        databaseMontreal.get(SEMINAR).put("MTLA201121", "50");

        //item6
        databaseMontreal.put(TRADESHOW, new HashMap<>());
        databaseMontreal.get(TRADESHOW).put("MTLM190124", "50");
        databaseMontreal.get(TRADESHOW).put("MTLE201123", "40");
        databaseMontreal.get(TRADESHOW).put("MTLA180925", "90");
    }

    public MontrealServerImpl() throws RemoteException
    {
        super();
        logger = Logger.getLogger(MontrealServerImpl.class.getName());
        try
        {
            addFileHandler(logger, "Montreal_Server");
        }
        catch (SecurityException | IOException ex)
        {
            Logger.getLogger(MontrealServerImpl.class.getName()).log(Level.SEVERE, null, ex);
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
    public synchronized String addEvent(String eventID, String eventType, String bookingCapacity, String managerID) throws RemoteException
    {
        String message = null;
        logger.info("Received request to add an event with event id " + eventID + " , Event Type" + eventType
                + " & Booking Capacity " + bookingCapacity);
        if (!databaseMontreal.get(eventType).containsKey(eventID))
        {
            databaseMontreal.get(eventType).put(eventID, bookingCapacity);
            message = "Operations Successful!. Event Added in Montreal Server for Event ID: "
                    + eventID + " Event Type: " + eventType + " Booking Capacity: " + bookingCapacity;
            logger.info(message);

            return message;
        }
        else
        {
            databaseMontreal.get(eventType).replace(eventID, bookingCapacity);
            message = "Operations Unsuccessful!. Event Not Added in Montreal Server "
                    + "for Event ID: " + eventID + " Event Type: " + eventType + " because the Event ID: " + eventID + ""
                    + " is already added for the Event Type: " + eventType + ". But, the Booking Capacity is updated to " + bookingCapacity;
            logger.info(message);

            return message;
        }

    }

    @Override
    public synchronized String removeEvent(String eventID, String eventType, String managerID) throws RemoteException
    {
        String message = null;
        if (databaseMontreal.get(eventType).containsKey(eventID))
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
            databaseMontreal.get(eventType).remove(eventID);

            message = "\nOperations Successful!. Event Removed in Montreal Server by Manager: " + managerID + " for Event ID: "
                    + eventID + " Event Type: " + eventType;
            logger.info(message);
            return message;
        }
        else
        {
            message = "Operations Unsuccessful!. Event Not Removed in Montreal Server by Manager: " + managerID + " f"
                    + "or Event ID: " + eventID + " Event Type: " + eventType + " because the Event ID: " + eventID
                    + " does not exist";
            logger.info(message);
            return message;
        }

    }

    @Override
    public synchronized String listEventAvailability(String eventType, String managerID) throws RemoteException
    {
        //Eg: Seminars - MTLE130519 3, OTWA060519 6, TORM180519 0, MTLE190519 2.
        String message = null;
        StringBuilder returnMessage = new StringBuilder();
        if (managerID.substring(0, 3).equals(MONTREAL))
        {
            logger.info("Requesting other server from Server: " + TORONTO_SERVER_NAME);
            String torrontoEvents = requestToOtherServers(null, null, null, 3, eventType, TORONTO_SERVER_PORT,null);
            logger.info("Requesting other server from Server: " + OTTAWA_SERVER_NAME);
            String ottawaEvents = requestToOtherServers(null, null, null, 3, eventType, OTTAWA_SERVER_PORT,null);
            returnMessage.append(torrontoEvents).append("\n\n").append(ottawaEvents).append("\n\n");

        }
        if (managerID.substring(0, 3).equals(TORONTO))
        {
            logger.info("Requesting other server from Server: " + MONTREAL_SERVER_NAME);
            String montrealEvents = requestToOtherServers(null, null, null, 3, eventType, MONTREAL_SERVER_PORT,null);
            logger.info("Requesting other server from Server: " + OTTAWA_SERVER_NAME);
            String ottawaEvents = requestToOtherServers(null, null, null, 3, eventType, OTTAWA_SERVER_PORT,null);

            returnMessage.append(ottawaEvents).append("\n\n").append(montrealEvents).append("\n\n");
        }
        if (managerID.substring(0, 3).equals(OTTAWA))
        {
            logger.info("Requesting other server from Server: " + MONTREAL_SERVER_NAME);
            String montrealEvents = requestToOtherServers(null, null, null, 3, eventType, MONTREAL_SERVER_PORT,null);
            logger.info("Requesting other server from Server: " + TORONTO_SERVER_NAME);
            String torrontoEvents = requestToOtherServers(null, null, null, 3, eventType, TORONTO_SERVER_PORT,null);

            returnMessage.append(torrontoEvents).append("\n\n").append(montrealEvents).append("\n\n");
        }

        if (!databaseMontreal.get(eventType).isEmpty())
        {
            for (Map.Entry<String, String> entry : databaseMontreal.get(eventType).entrySet())
            {
                returnMessage.append("EventID: ").append(entry.getKey()).append("| Booking Capacity ").append(entry.getValue()).append("\n");
            }
            message = "Operation Successful, List of events retrieved for Event Type: " + eventType + " by Manager: " + managerID + "in server";
            logger.info(message);

            return returnMessage.toString();
        }
        else
        {
            message = "Operation UnSuccessful, List of events not retrieved for Event Type: " + eventType + " by Manager: " + managerID + " in server ";
            logger.info(message);
            return message;
        }

    }

    @Override
    public synchronized String bookEvent(String customerID, String eventID, String eventType, String bookingAmount) throws RemoteException
    {
        if (!customerID.substring(0, 3).equals(MONTREAL) && !customerID.substring(0, 3).equals(eventID.substring(0, 3)))
        {
            int customerBookingsCurrent = Integer.parseInt(this.nonOriginCustomerBooking(customerID));
            int customerBookingsOther = customerID.substring(0, 3).equals(OTTAWA) ? Integer.parseInt(requestToOtherServers(customerID, null, null, 7, null, TORONTO_SERVER_PORT, null).trim())
                    : Integer.parseInt(requestToOtherServers(customerID, null, null, 7, null, OTTAWA_SERVER_PORT, null).trim());

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
        
        if (eventID.substring(0, 3).equals(MONTREAL))
        {
            logger.log(Level.INFO, "Book Event Requested by {0} for Event Type {1} with Event ID {2}", new Object[]
            {
                customerID, eventType, eventID
            });
            HashMap< String, String> event = databaseMontreal.get(eventType);
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
            return requestToOtherServers(customerID, eventID, bookingAmount, 4, eventType, TORONTO_SERVER_PORT,null);
        }
        if (eventID.substring(0, 3).equals(OTTAWA))
        {
            return requestToOtherServers(customerID, eventID, bookingAmount, 4, eventType, OTTAWA_SERVER_PORT,null);
        }
        return "";
    }

    @Override
    public synchronized String getBookingSchedule(String customerID, String managerID) throws RemoteException
    {
        String returnMsg = "";
        if(managerID != null && managerID.equalsIgnoreCase("Default")) managerID = null;
        if(managerID == null)
            logger.log(Level.INFO, "Booking Schedule Requested by {0}", customerID);
        else
            logger.log(Level.INFO, "Booking Schedule Requested by {0} for customer {1}", new Object[] {managerID, customerID});
        HashMap<String, HashMap< String, Integer>> customerEvents = customerEventsMapping.get(customerID);

        if ((customerID.substring(0, 3).equals(MONTREAL) && managerID == null)||(managerID != null && managerID.substring(0, 3).equals(MONTREAL)))
        {
            returnMsg += requestToOtherServers(customerID, null, null, 5, null, TORONTO_SERVER_PORT,null);
            returnMsg += requestToOtherServers(customerID, null, null, 5, null, OTTAWA_SERVER_PORT,null);
        }
        if (customerEvents != null && !customerEvents.isEmpty())
        {
            HashMap< String, Integer> customerConferenceEventID = customerEvents.get(CONFERENCE);
            HashMap< String, Integer> customerSeminarEventID = customerEvents.get(SEMINAR);
            HashMap< String, Integer> customerTradeshowEventID = customerEvents.get(TRADESHOW);

            if (customerConferenceEventID != null && !customerConferenceEventID.isEmpty())
            {
                returnMsg += "\nFor Conference Events in Montreal: ";
                for (String event : customerConferenceEventID.keySet())
                {
                    returnMsg += "\nEvent ID: " + event + " Booking for " + customerConferenceEventID.get(event);
                }
            }
            if (customerSeminarEventID != null && !customerSeminarEventID.isEmpty())
            {
                returnMsg += "\nFor Seminar Events in Montreal: ";
                for (String event : customerSeminarEventID.keySet())
                {
                    returnMsg += "\nEvent ID: " + event + " Booking for " + customerSeminarEventID.get(event);
                }
            }
            if (customerTradeshowEventID != null && !customerTradeshowEventID.isEmpty())
            {
                returnMsg += "\nFor Tradeshow Events in Montreal: ";
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
            if ((customerID.substring(0, 3).equals(MONTREAL) && managerID == null)||(managerID != null && managerID.substring(0, 3).equals(MONTREAL)))
            {
                returnMsg += "\nRecords for " + customerID + " do not exist.";
            }
        }

        return returnMsg;
    }

    @Override
    public synchronized String cancelEvent(String customerID, String eventID, String eventType) throws RemoteException
    {
        switch (eventID.substring(0, 3))
        {
            case MONTREAL:
                if (customerEventsMapping.containsKey(customerID))
                {
                    if (customerEventsMapping.get(customerID).containsKey(eventType) && customerEventsMapping.get(customerID).get(eventType).containsKey(eventID))
                    {
                        Integer bookValue = customerEventsMapping.get(customerID).get(eventType).remove(eventID);
                        Integer currentValue = 0;
                        Integer sum = 0;
                        
                        if (databaseMontreal.get(CONFERENCE).containsKey(eventID))
                        {
                            currentValue = Integer.parseInt(databaseMontreal.get(CONFERENCE).get(eventID));
                            sum = currentValue + bookValue;
                            databaseMontreal.get(CONFERENCE).put(eventID, sum.toString());
                        }
                        else if (databaseMontreal.get(SEMINAR).containsKey(eventID))
                        {
                            currentValue = Integer.parseInt(databaseMontreal.get(SEMINAR).get(eventID));
                            sum = currentValue + bookValue;
                            databaseMontreal.get(SEMINAR).put(eventID, sum.toString());
                        }
                        else if (databaseMontreal.get(TRADESHOW).containsKey(eventID))
                        {
                            currentValue = Integer.parseInt(databaseMontreal.get(TRADESHOW).get(eventID));
                            sum = currentValue + bookValue;
                            databaseMontreal.get(TRADESHOW).put(eventID, sum.toString());
                        }
                        logger.log(Level.INFO, "This event has been removed from customer record.");
                        return "This event has been removed from customer record.";
                    }
                }
                logger.log(Level.INFO, "This event does not exist in customer record.");
                return "This event does not exist in customer record.";
            case TORONTO:
                return requestToOtherServers(customerID, eventID, null, 6, eventType, TORONTO_SERVER_PORT,null);
            case OTTAWA:
                return requestToOtherServers(customerID, eventID, null, 6, eventType, OTTAWA_SERVER_PORT,null);
            default:
                break;
        }
        return null;
    }

    @Override
    public synchronized String nonOriginCustomerBooking(String customerID)
    {
        int numberOfCustomerEvents = 0;
        if (customerEventsMapping.containsKey(customerID))
        {
            if (customerEventsMapping.get(customerID).containsKey(CONFERENCE))
            {
                numberOfCustomerEvents += customerEventsMapping.get(customerID).get(CONFERENCE).keySet().size();
            }
            if (customerEventsMapping.get(customerID).containsKey(SEMINAR))
            {
                numberOfCustomerEvents += customerEventsMapping.get(customerID).get(SEMINAR).keySet().size();
            }
            if (customerEventsMapping.get(customerID).containsKey(TRADESHOW))
            {
                numberOfCustomerEvents += customerEventsMapping.get(customerID).get(TRADESHOW).keySet().size();
            }
        }
        return "" + numberOfCustomerEvents;
    }

    public String requestToOtherServers(String userID, String eventID, String bookingCapacity, int serverNumber, String eventType, int serPort, String managerId)
    {
        int serverPort;
        if (eventID != null)
        {
            serverPort = serverPortSelection(eventID);
        }
        else
        {
            serverPort = serPort;
        }
        String stringServer = Integer.toString(serverNumber);
        DatagramSocket aSocket = null;
        String response = null;
        String userIDName = userID != null ? userID : "Default";
        String eventTypeName = eventType != null ? eventType : "Default";
        String eventIDName = eventID != null ? eventID : "Default";
        String bookingCap = bookingCapacity != null ? bookingCapacity : "Default";
        String managerID = managerId != null ? managerId : "Default";

        try
        {
            aSocket = new DatagramSocket();
            String message = userIDName.concat(" ").concat(eventIDName).concat(" ").concat(stringServer).concat(" ").concat(eventTypeName).concat(" ").concat(bookingCap).concat(" ").concat(managerID);
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
}
