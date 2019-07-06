/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */
package Server;

import EventManagementServerApp.ServerInterface;
import EventManagementServerApp.ServerInterfaceHelper;
import ServerImpl.OttawaServerImpl;
import ServerImpl.TorontoServerImpl;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static CommonUtils.CommonUtils.*;

/**
 *
 * @author Gursimran Singh
 */
public class TorontoServer {

    public static void main(String[] args)
    {

        TorontoServerImpl torontoServerStub = new TorontoServerImpl();
        Runnable runnable = () ->
        {
            receiveRequestsFromOthers(torontoServerStub);
        };

        Runnable task1 = () -> {
            handleTorrontoRequests(torontoServerStub,args);
        };
        Thread thread = new Thread(runnable);
        Thread thread1 = new Thread(task1);
        thread.start();
        thread1.start();


//        Registry registry = LocateRegistry.createRegistry(TORONTO_SERVER_PORT);
//
//        try
//        {
//            registry.bind(TORONTO_SERVER_NAME, torontoServerStub);
//        }
//        catch (RemoteException e)
//        {
//            e.printStackTrace();
//        }
//        catch (AlreadyBoundException e)
//        {
//            e.printStackTrace();
//        }

    }
    private static void handleTorrontoRequests(TorontoServerImpl torontoServer, String[] args) {
        ORB orb = ORB.init(args, null);
        try {
            POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPoa.the_POAManager().activate();

            torontoServer.setOrb(orb);

            ServerInterface href = ServerInterfaceHelper.narrow(rootPoa.servant_to_reference(torontoServer));

            NamingContextExt namingContextReference = NamingContextExtHelper.narrow(orb.resolve_initial_references("NameService"));
            NameComponent[] path = namingContextReference.to_name(TORONTO_SERVER_NAME);

            namingContextReference.rebind(path, href);
            System.out.println("Toronto Server is ready");
            while(true) {
                orb.run();
            }

        } catch (InvalidName | AdapterInactive | org.omg.CosNaming.NamingContextPackage.InvalidName | ServantNotActive | WrongPolicy | NotFound | CannotProceed e) {
            System.out.println("Something went wrong in Toronto server: "+e.getMessage());
        }

    }
    private static void receiveRequestsFromOthers(TorontoServerImpl torontoServer)
    {
        DatagramSocket aSocket = null;
        try
        {
            aSocket = new DatagramSocket(TORONTO_SERVER_PORT);
            byte[] buffer = new byte[1500];
            System.out.println("Toronto server started.....");
            //Server waits for the request
            while (true)
            {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String response = requestsFromOthers(new String(request.getData()), torontoServer);
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
    public static String requestsFromOthers(String data, TorontoServerImpl torontoServer)
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
            String newEventID = receivedDataString[6].trim();
            String newEventType = receivedDataString[7].trim();

            switch (methodNumber)
            {
                case "1":
                    return torontoServer.addEvent(eventID, eventType, bookingCapacity, userId);
                case "2":
                    return torontoServer.removeEvent(eventID, eventType, userId);
                case "3":
                    return torontoServer.listEventAvailability(eventType, managerID);
                case "4":
                    return torontoServer.bookEvent(userId, eventID, eventType, bookingCapacity);
                case "5":
                    return torontoServer.getBookingSchedule(userId,managerID);
                case "6":
                    return torontoServer.cancelEvent(userId, eventID, eventType);
                case "7":
                    return torontoServer.nonOriginCustomerBooking(userId, eventID);
                case "8":
                    return torontoServer.swapEvent(userId, newEventID, newEventType, eventID, eventType);
                case "9":
                    return torontoServer.eventAvailable(newEventID, newEventType);
                case "10":
                    return torontoServer.validateBooking(userId, eventID, eventType);
            }
        }
        catch (Exception e)
        {
            
        }
        return "Incorrect";
    }
}
