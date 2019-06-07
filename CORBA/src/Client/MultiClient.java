package Client;

import CommonUtils.CommonUtils;
import ServerInterface.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author gursimransingh
 * This class contains a few threads to check the multithreading aspect of the assignment.
 */
public class MultiClient {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(CommonUtils.OTTAWA_SERVER_PORT);
        Registry registrymon = LocateRegistry.getRegistry(CommonUtils.MONTREAL_SERVER_PORT);

        ServerInterface serverInterfaceOttawa = (ServerInterface) registry.lookup(CommonUtils.OTTAWA_SERVER_NAME);
        ServerInterface serverInterfaceMontreal = (ServerInterface) registrymon.lookup(CommonUtils.MONTREAL_SERVER_NAME);

        Runnable runnable1 = () ->
        {
            try {
                String response = serverInterfaceOttawa.bookEvent("OTWC3425", "MTLM130720", CommonUtils.CONFERENCE, "25");
                System.out.println("Thread 1: " + Thread.currentThread().getName() + "Respnse from server: " + response);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };

        Thread thread1 = new Thread(runnable1);
        thread1.setName("Thread 1");
        Runnable runnable2 = () ->
        {
            try {
                String response = serverInterfaceOttawa.bookEvent("OTWC3425", "TORE101022", CommonUtils.SEMINAR, "15");
                System.out.println("Thread 2: " + Thread.currentThread().getName() + "Respnse from server: " + response);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };

        Thread thread2 = new Thread(runnable2);
        thread2.setName("Thread 2");
        Runnable runnable3 = () ->
        {
            try {
                String response = serverInterfaceOttawa.bookEvent("OTWC3425", "OTWA090619", CommonUtils.TRADESHOW, "80");
                System.out.println("Thread 3: " + Thread.currentThread().getName() + "Respnse from server: " + response);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };

        Thread thread3 = new Thread(runnable3);
        thread3.setName("Thread 3");

        Runnable runnable4 = () ->
        {
            try {
                String response = serverInterfaceOttawa.bookEvent("OTWC3456", "OTWA090619", CommonUtils.CONFERENCE, "33");
                System.out.println("Thread 4: " + Thread.currentThread().getName() + "Respnse from server: " + response);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };

        Thread thread4 = new Thread(runnable4);
        thread4.setName("Thread 4");

        Runnable runnable5 = () ->
        {
            try {
                String response = serverInterfaceMontreal.addEvent("MTLM130720", CommonUtils.CONFERENCE, "33", "MTLM130720");
                System.out.println("Thread 5: " + Thread.currentThread().getName() + "Respnse from server: " + response);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };

        Thread thread5 = new Thread(runnable5);
        thread4.setName("Thread 5");

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();

    }
}
