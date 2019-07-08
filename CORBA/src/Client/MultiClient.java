package Client;

import CommonUtils.CommonUtils;
import EventManagementServerApp.ServerInterface;
import EventManagementServerApp.ServerInterfaceHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
//import ServerInterface.ServerInterface;


/**
 * @author gursimransingh
 * This class contains a few threads to check the multithreading aspect of the assignment.
 */
public class MultiClient {
    public static void main(String[] args) throws Exception {

        ORB orb = ORB.init(args, null);
        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        ServerInterface serverInterfaceMontreal = (ServerInterface) ServerInterfaceHelper.narrow(ncRef.resolve_str(CommonUtils.MONTREAL_SERVER_NAME));
        ServerInterface serverInterfaceToronto = (ServerInterface) ServerInterfaceHelper.narrow(ncRef.resolve_str(CommonUtils.TORONTO_SERVER_NAME));
        ServerInterface serverInterfaceOttawa = (ServerInterface) ServerInterfaceHelper.narrow(ncRef.resolve_str(CommonUtils.OTTAWA_SERVER_NAME));


        Runnable runnable1 = () ->
        {
            try {
                String response = serverInterfaceOttawa.bookEvent("OTWC3434", "OTWE251023", CommonUtils.CONFERENCE, "1");
                System.out.println("Thread 1: " + Thread.currentThread().getName() + " Response from server: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread thread1 = new Thread(runnable1);
        thread1.setName("Thread 1");
        Runnable runnable2 = () ->
        {
            try {
                String response = serverInterfaceMontreal.bookEvent("OTWC8475", "MTLM190124", CommonUtils.TRADESHOW, "1");
                System.out.println("Thread 2: " + Thread.currentThread().getName() + " Response from server: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread thread2 = new Thread(runnable2);
        thread2.setName("Thread 2");
        Runnable runnable3 = () ->
        {
            try {
                String response = serverInterfaceOttawa.bookEvent("OTWC3425", "OTWA090619", CommonUtils.TRADESHOW, "1");
                System.out.println("Thread 3: " + Thread.currentThread().getName() + " Response from server: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread thread3 = new Thread(runnable3);
        thread3.setName("Thread 3");

        Runnable runnable4 = () ->
        {
            try {
                String response = serverInterfaceOttawa.bookEvent("OTWC3456", "OTWA090619", CommonUtils.CONFERENCE, "1");
                System.out.println("Thread 4: " + Thread.currentThread().getName() + " Response from server: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread thread4 = new Thread(runnable4);
        thread4.setName("Thread 4");

        Runnable runnable5 = () ->
        {
            try {
                String response = serverInterfaceMontreal.addEvent("MTLM130720", CommonUtils.CONFERENCE, "33", "MTLM130720");
                System.out.println("Thread 5: " + Thread.currentThread().getName() + " Response from server: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread thread5 = new Thread(runnable5);
        thread5.setName("Thread 5");

        Runnable runnable6 = () ->
        {
            try {
                String response = serverInterfaceOttawa.swapEvent("OTWC3434","OTWM140147", CommonUtils.SEMINAR, "OTWE251023", CommonUtils.CONFERENCE);
                System.out.println("Thread 6: " + Thread.currentThread().getName() + " Response from server: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread thread6 = new Thread(runnable6);
        thread6.setName("Thread 6");

        Runnable runnable7 = () ->
        {
            try {
                String response = serverInterfaceOttawa.swapEvent("OTWC3434","OTWE251023", CommonUtils.CONFERENCE, "OTWM140147", CommonUtils.SEMINAR);
                System.out.println("Thread 7: " + Thread.currentThread().getName() + " Response from server: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread thread7 = new Thread(runnable7);
        thread7.setName("Thread 7");

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();

    }
}
