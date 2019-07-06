/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */
package Server;

import CommonUtils.CommonUtils;
import EventManagementServerApp.ServerInterface;
import EventManagementServerApp.ServerInterfaceHelper;
import ServerImpl.MontrealServerImpl;
//import ServerInterface.ServerInterface;
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

import static CommonUtils.CommonUtils.MONTREAL_SERVER_NAME;
import static CommonUtils.CommonUtils.MONTREAL_SERVER_PORT;

/**
 *
 * @author Gursimran Singh
 */
public class MontrealServer {

    public static void main(String[] args)
    {
        // TODO code application logic here
        MontrealServerImpl montrealServerStub = new MontrealServerImpl();

        Runnable runnable = () ->
        {
            receiveRequestsFromOthers(montrealServerStub);
        };

        Runnable task1 = () -> {
            handleMontrealRequests(montrealServerStub,args);
        };
        Thread thread = new Thread(runnable);
        Thread thread1 = new Thread(task1);
        thread.start();
        thread1.start();


       // Registry registry = LocateRegistry.createRegistry(MONTREAL_SERVER_PORT);

//        try
//        {
//            registry.bind(MONTREAL_SERVER_NAME, montrealServerStub);
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
    private static void handleMontrealRequests(MontrealServerImpl monImpl, String[] args) {
        ORB orb = ORB.init(args, null);
        try {
            POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPoa.the_POAManager().activate();

            monImpl.setOrb(orb);

            ServerInterface href = ServerInterfaceHelper.narrow(rootPoa.servant_to_reference(monImpl));

            NamingContextExt namingContextReference = NamingContextExtHelper.narrow(orb.resolve_initial_references("NameService"));
            NameComponent[] path = namingContextReference.to_name(MONTREAL_SERVER_NAME);

            namingContextReference.rebind(path, href);
            System.out.println("Montreal Server is ready");
            while(true) {
                orb.run();
            }

        } catch (InvalidName | AdapterInactive | org.omg.CosNaming.NamingContextPackage.InvalidName | ServantNotActive | WrongPolicy | NotFound | CannotProceed e) {
            System.out.println("Something went wrong in montreal server: "+e.getMessage());
        }

    }
    private static void receiveRequestsFromOthers(MontrealServerImpl monStub)
    {
        DatagramSocket aSocket = null;
        try
        {
            aSocket = new DatagramSocket(MONTREAL_SERVER_PORT);
            byte[] buffer = new byte[1500];
            System.out.println("Montreal server started.....");
            //Server waits for the request
            while (true)
            {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String response = requestsFromOthers(new String(request.getData()), monStub);
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
    public static String requestsFromOthers(String data, MontrealServerImpl montrealServer)
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
                    return montrealServer.addEvent(eventID, eventType, bookingCapacity, userId);
                case "2":
                    return montrealServer.removeEvent(eventID, eventType, userId);
                case "3":
                    return montrealServer.listEventAvailability(eventType, managerID);
                case "4":
                    return montrealServer.bookEvent(userId, eventID, eventType, bookingCapacity);
                case "5":
                    return montrealServer.getBookingSchedule(userId,managerID);
                case "6":
                    return montrealServer.cancelEvent(userId, eventID, eventType);
                case "7":
                    return montrealServer.nonOriginCustomerBooking(userId, eventID);
                case "8":
                    return montrealServer.swapEvent(userId, newEventID, newEventType, eventID, eventType);
                case "9":
                    return montrealServer.eventAvailable(newEventID, newEventType);
                case "10":
                    return montrealServer.validateBooking(userId, eventID, eventType);
            }
        }
        catch (Exception e)
        {
            
        }
        return "Incorrect";
    }

}
