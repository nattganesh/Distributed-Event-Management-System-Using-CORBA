package EventManagementServerApp;


/**
* EventManagementServerApp/_ServerInterfaceStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /Users/gursimransingh/IdeaProjects/DistributedAssignment2/CORBA/src/corbainterface/LibrariesServer.idl
* Saturday, 6 July, 2019 5:23:54 PM EDT
*/

public class _ServerInterfaceStub extends org.omg.CORBA.portable.ObjectImpl implements EventManagementServerApp.ServerInterface
{

  public String addEvent (String eventID, String eventType, String bookingCapacity, String managerID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("addEvent", true);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $out.write_string (bookingCapacity);
                $out.write_string (managerID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return addEvent (eventID, eventType, bookingCapacity, managerID        );
            } finally {
                _releaseReply ($in);
            }
  } // addEvent

  public String removeEvent (String eventID, String eventType, String managerID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("removeEvent", true);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $out.write_string (managerID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return removeEvent (eventID, eventType, managerID        );
            } finally {
                _releaseReply ($in);
            }
  } // removeEvent

  public String listEventAvailability (String eventType, String managerID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("listEventAvailability", true);
                $out.write_string (eventType);
                $out.write_string (managerID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return listEventAvailability (eventType, managerID        );
            } finally {
                _releaseReply ($in);
            }
  } // listEventAvailability

  public String bookEvent (String customerID, String eventID, String eventType, String bookingAmount)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("bookEvent", true);
                $out.write_string (customerID);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $out.write_string (bookingAmount);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return bookEvent (customerID, eventID, eventType, bookingAmount        );
            } finally {
                _releaseReply ($in);
            }
  } // bookEvent

  public String getBookingSchedule (String customerID, String managerID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getBookingSchedule", true);
                $out.write_string (customerID);
                $out.write_string (managerID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getBookingSchedule (customerID, managerID        );
            } finally {
                _releaseReply ($in);
            }
  } // getBookingSchedule

  public String cancelEvent (String customerID, String eventID, String eventType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("cancelEvent", true);
                $out.write_string (customerID);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return cancelEvent (customerID, eventID, eventType        );
            } finally {
                _releaseReply ($in);
            }
  } // cancelEvent

  public String nonOriginCustomerBooking (String customerID, String eventID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("nonOriginCustomerBooking", true);
                $out.write_string (customerID);
                $out.write_string (eventID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return nonOriginCustomerBooking (customerID, eventID        );
            } finally {
                _releaseReply ($in);
            }
  } // nonOriginCustomerBooking

  public String swapEvent (String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("swapEvent", true);
                $out.write_string (customerID);
                $out.write_string (newEventID);
                $out.write_string (newEventType);
                $out.write_string (oldEventID);
                $out.write_string (oldEventType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return swapEvent (customerID, newEventID, newEventType, oldEventID, oldEventType        );
            } finally {
                _releaseReply ($in);
            }
  } // swapEvent

  public String eventAvailable (String eventID, String eventType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("eventAvailable", true);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return eventAvailable (eventID, eventType        );
            } finally {
                _releaseReply ($in);
            }
  } // eventAvailable

  public String validateBooking (String customerID, String eventID, String eventType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("validateBooking", true);
                $out.write_string (customerID);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return validateBooking (customerID, eventID, eventType        );
            } finally {
                _releaseReply ($in);
            }
  } // validateBooking

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:EventManagementServerApp/ServerInterface:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _ServerInterfaceStub
