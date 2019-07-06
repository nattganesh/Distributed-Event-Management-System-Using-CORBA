/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */
package ServerInterface;

import java.rmi.Remote;

/**
 *
 * @author Gursimran Singh
 */
public interface ServerInterface extends Remote {

    //Manager Operations
    String addEvent(String eventID, String eventType, String bookingCapacity, String managerID) throws java.rmi.RemoteException;

    String removeEvent(String eventID, String eventType, String managerID) throws java.rmi.RemoteException;

    String listEventAvailability(String eventType, String managerID) throws java.rmi.RemoteException;

    //Customer Operations
    String bookEvent(String customerID, String eventID, String eventType, String bookingAmount) throws java.rmi.RemoteException;

    String getBookingSchedule(String customerID, String managerID) throws java.rmi.RemoteException;

    String cancelEvent(String customerID, String eventID, String eventType) throws java.rmi.RemoteException;

    String nonOriginCustomerBooking(String customerID, String eventID) throws java.rmi.RemoteException;
    
    String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType) throws java.rmi.RemoteException;
    
    String eventAvailable(String eventID, String eventType) throws java.rmi.RemoteException;
    
    String validateBooking(String customerID, String eventID, String eventType) throws java.rmi.RemoteException;

}
