import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jnetpcap.packet.PcapPacket;


public class ProtocolStateMachine {
	StateMachine SM;
	static int packetOffset = -1;
	Transition transit;
	static Packet currentPacket;
	static HashMap<String,Long> Queries;
	static  Hasher responsePackets;
	final static String file = "data\\DNS_Traffic000.pcap";
	final static long timeout = 60000;   //1 minute = 60,000 millisecond
	static DBConnection dbConnect;
	
	ProtocolStateMachine(int nost,String dbms,String location,String dbname){
		SM = new StateMachine(nost);
		transit = new Transition();
		SM.addObserver(transit);
		Queries = new HashMap<String,Long>();
		Queries.clear();
		responsePackets = new Hasher();
		responsePackets.clear();
		dbConnect = new DBConnection(dbms,location,dbname);
		dbConnect.getConnection();
	}

	public static boolean isTimeOut(long timer,long time){
		if(timer - time > timeout){
			return true;
		}
		return false;
	}
	
	public static void setCurrentPacket(Packet packet){
		currentPacket = packet;
	}
	
	
	public static void main(String[] args){
		ProtocolStateMachine psm = new ProtocolStateMachine(5,"h2","~/","DNSPackets"); //4
		try {
			dbConnect.createSchema(dbConnect.getDBName());
			//dbConnect.dropTable("Response");
			//dbConnect.dropTable("Referral");
			//dbConnect.dropTable("Query");
			dbConnect.dropTable("Packets");
			
			dbConnect.createTable("Packets","TIME_STAMP BIGINT, "+"TRANSACTION_ID VARCHAR(255), "+"TYPE_OF_PACKET VARCHAR(255), "+"TYPE_OF_QUERY SMALLINT, "+"AUTH_RESPONSE BOOLEAN, "+"RECURSION_DESIRED BOOLEAN, "+"RECURSION_AVAILABLE BOOLEAN, "+"RESPONSE_CODE SMALLINT, "+"QUESTION_COUNT SMALLINT, "+"RESPONSE_COUNT SMALLINT, "+"AUTHORITY_COUNT SMALLINT, "+"ADDITIONAL_COUNT SMALLINT, "+"QUESTION_NAME VARCHAR(255), "+"QUESTION_TYPE SMALLINT, "+"QUESTION_CLASS SMALLINT, "+"PAYLOAD BINARY(1000)");
			//dbConnect.createTable("Query","TRANSACTION_ID VARCHAR(255) PRIMARY KEY, "+"TIME_STAMP BIGINT, "+"PACKET OTHER");
			//dbConnect.createTable("Response","TRANSACTION_ID VARCHAR(255), "+"PACKET OTHER");//, "+"FOREIGN KEY fk (TRANSACTION_ID) REFERENCES "+dbConnect.getDBName()+".Query(TRANSACTION_ID))");
			//dbConnect.createTable("Referral","TRANSACTION_ID VARCHAR(255), "+"TIME_STAMP BIGINT");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//Class<?> act;
		try {
			//act = Class.forName("Activity");
			psm.SM.getInitialState().onEntry = Activity.class.getMethod("EntryActionInit", null);
			psm.SM.getInitialState().nextState = Activity.class.getMethod("nextStateInit", StateMachine.class);
			psm.SM.getInitialState().onExit = Activity.class.getMethod("ExitActionInit", null);
			
			psm.SM.getState("A").onEntry = Activity.class.getMethod("EntryActionA", null);
			psm.SM.getState("A").nextState = Activity.class.getMethod("nextStateA", StateMachine.class);
			psm.SM.getState("A").onExit = Activity.class.getMethod("ExitActionA", null);
			
			psm.SM.getState("B").onEntry = Activity.class.getMethod("EntryActionB", null);
			psm.SM.getState("B").nextState = Activity.class.getMethod("nextStateB", StateMachine.class);
			psm.SM.getState("B").onExit = Activity.class.getMethod("ExitActionB", null);
			
			psm.SM.getState("C").onEntry = Activity.class.getMethod("EntryActionC", null);
			psm.SM.getState("C").nextState = Activity.class.getMethod("nextStateC", StateMachine.class);
			psm.SM.getState("C").onExit = Activity.class.getMethod("ExitActionC", null);
			
			psm.SM.getState("D").onEntry = Activity.class.getMethod("EntryActionD", null);
			psm.SM.getState("D").nextState = Activity.class.getMethod("nextStateD", StateMachine.class);
			psm.SM.getState("D").onExit = Activity.class.getMethod("ExitActionD", null);
			
			psm.SM.getState("E").onEntry = Activity.class.getMethod("EntryActionE", null);
			psm.SM.getState("E").nextState = Activity.class.getMethod("nextStateE", StateMachine.class);
			psm.SM.getState("E").onExit = Activity.class.getMethod("ExitActionE", null);
			
			psm.SM.getEndState().onEntry = Activity.class.getMethod("EntryActionEnd", null);
			psm.SM.getEndState().nextState = Activity.class.getMethod("nextStateEnd", StateMachine.class);
			psm.SM.getEndState().onExit = Activity.class.getMethod("ExitActionEnd", null);
		}  catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			//!(psm.SM.getCurrentState().equals(psm.SM.getEndState()))
			while(packetOffset < 31 ){
				psm.SM.getCurrentState().nextState.invoke(new Activity(), psm.SM);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*for(String s : TransactionIDs){
			System.out.println(s);
		}*/
	
    	/*for(String s : Queries.keySet()){
    		System.out.print(s + ": ");
    		if(!responsePackets.get(s).isEmpty()){
    		for(Packet p : responsePackets.get(s)){
    			byte[] payload = PayloadRetriever.getUDPPayload(p);
    			System.out.print(DNSWrapper.getResponseCode(payload)+" ");
    		}
    	}
    		else
    			System.out.println("Empty");
     }*/
	
	}
}
