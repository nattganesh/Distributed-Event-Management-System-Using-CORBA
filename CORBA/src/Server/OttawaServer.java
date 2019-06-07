/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */
package Server;

import ServerImpl.OttawaServerImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static CommonUtils.CommonUtils.OTTAWA_SERVER_NAME;
import static CommonUtils.CommonUtils.OTTAWA_SERVER_PORT;

/**
 *
 * @author Gursimran Singh
 */
public class OttawaServer {

    public static void main(String[] args) throws RemoteException
    {
        // TODO code application logic here
        OttawaServerImpl ottawaServerStub = new OttawaServerImpl();
        Runnable runnable = () ->
        {
            receiveRequestsFromOthers(ottawaServerStub);
        };

        Thread thread = new Thread(runnable);
        thread.start();

        Registry registry = LocateRegistry.createRegistry(OTTAWA_SERVER_PORT);

        try
        {
            registry.bind(OTTAWA_SERVER_NAME, ottawaServerStub);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (AlreadyBoundException e)
        {
            e.printStackTrace();
        }

    }

    private static void receiveRequestsFromOthers(OttawaServerImpl ottawaServer)
    {
        DatagramSocket aSocket = null;
        try
        {
            aSocket = new DatagramSocket(OTTAWA_SERVER_PORT);
            byte[] buffer = new byte[1000];
            System.out.println("Ottawa server started.....");
            //Server waits for the request
            while (true)
            {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String response = requestsFromOthers(new String(request.getData()), ottawaServer);
                DatagramPacket reply = new DatagramPacket(response.getBytes(StandardCharsets.UTF_8), response.length(), request.getAddress(),
                        request.getPort());
                //reply sent
                aSocket.send(reply);
            }
        }
        catch (SocketException e)
        {
            System.out.println("Socket: " + e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println("IO: " + e.getMessage());
        }
        finally
        {
            if (aSocket != null)
            {
                aSocket.close();
            }
        }
    }

    //clientudp
    public static String requestsFromOthers(String data, OttawaServerImpl ottawaServer)
    {
        try
        {
            String[] receivedDataString = data.split(" ");
            String userId = receivedDataString[0];
            String eventID = receivedDataString[1];
            String methodNumber = receivedDataString[2].trim();
            String eventType = receivedDataString[3].trim();
            String bookingCapacity = receivedDataString[4].trim();
            String managerID = receivedDataString[5].trim();

            switch (methodNumber)
            {
                case "1":
                    return ottawaServer.addEvent(eventID, eventType, bookingCapacity, userId);
                case "2":
                    return ottawaServer.removeEvent(eventID, eventType, userId);
                case "3":
                    return ottawaServer.listEventAvailability(eventType, userId);
                case "4":
                    return ottawaServer.bookEvent(userId, eventID, eventType, bookingCapacity);
                case "5":
                    return ottawaServer.getBookingSchedule(userId,managerID);
                case "6":
                    return ottawaServer.cancelEvent(userId, eventID, eventType);
                case "7":
                    return ottawaServer.nonOriginCustomerBooking(userId);
            }
        }
        catch (RemoteException e)
        {
            
        }
        return "Incorrect";
    }

}
