import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;


public class Activity {
  
	
	public void EntryActionInit(){
		System.out.println("Entering initial state..");
	}
	
	public void ExitActionInit(){
		System.out.println("Exiting initial state..");
	}
	
	public void nextStateInit(StateMachine SM){
		System.out.println("In initial state..");
		SM.setCurrentState(new TransitionSupport(SM.getInitialState(),SM.getState("A")));
	}
	
	public void EntryActionA(){
		System.out.println("Entering State A..");
		ProtocolStateMachine.packetOffset += 1;
		ProtocolStateMachine.setCurrentPacket(PcapReader.readPacket(ProtocolStateMachine.file,ProtocolStateMachine.packetOffset));
	}
	
	public void nextStateA(StateMachine SM){
		System.out.println("In State A..");
		long time,time_p;
		time = 0;
		byte[] payload = PayloadRetriever.getUDPPayload(ProtocolStateMachine.currentPacket);
		String trID = DNSWrapper.getTransactionID(payload);
		if(DNSWrapper.isQuery(payload)){
			time = ProtocolStateMachine.currentPacket.getCaptureHeader().timestampInMillis();
			//ProtocolStateMachine.Queries.put(trID, Long.valueOf(time));
			System.out.println(trID + " " + time);
			try {
				ResultSet rs = ProtocolStateMachine.dbConnect.getConnect().prepareStatement("SELECT TRANSACTION_ID FROM "+ProtocolStateMachine.dbConnect.getDBName()+".Query "+"WHERE TRANSACTION_ID="+trID).executeQuery();
				if(!rs.first())
					ProtocolStateMachine.dbConnect.populateTable("INSERT INTO "+ProtocolStateMachine.dbConnect.getDBName()+".Query "+"VALUES(?,?,?)",trID,new Long(time),ProtocolStateMachine.currentPacket);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//ProtocolStateMachine.responsePackets.put(DNSWrapper.getTransactionID(payload), new ArrayList<Packet>());
			SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getState("A")));
		}
		else{
			try {
				ResultSet rs = ProtocolStateMachine.dbConnect.getConnect().prepareStatement("SELECT TIME_STAMP FROM "+ProtocolStateMachine.dbConnect.getDBName()+".Query "+"WHERE TRANSACTION_ID="+trID).executeQuery(); //ProtocolStateMachine.dbConnect.retrieveResultSet("SELECT TIME_STAMP FROM "+ProtocolStateMachine.dbConnect.getDBName()+".Query "+"WHERE TRANSACTION_ID="+trID);
				if(rs.first()){
					time = rs.getLong("TIME_STAMP");
				time_p = ProtocolStateMachine.currentPacket.getCaptureHeader().timestampInMillis();
				System.out.println(trID + " " + time + " " + time_p);
				if(ProtocolStateMachine.isTimeOut(time_p,time)){
					SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getEndState()));
				}
				else{
					SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getState("B")));
				}
			}
				else{
					System.out.println("Query not recorded!");
					SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getEndState()));
				}
					
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	
	public void ExitActionA(){
		System.out.println("Exiting State A..");
	}
	
	public void EntryActionB(){
		System.out.println("Entering State B..");
	}
	
	public void nextStateB(StateMachine SM){
		System.out.println("In State B..");
		byte[] payload = PayloadRetriever.getUDPPayload(ProtocolStateMachine.currentPacket);
		String trID = DNSWrapper.getTransactionID(payload);
		try {
			ResultSet rs = ProtocolStateMachine.dbConnect.getConnect().prepareStatement("SELECT TRANSACTION_ID FROM "+ProtocolStateMachine.dbConnect.getDBName()+".Query "+"WHERE TRANSACTION_ID="+trID).executeQuery();//ProtocolStateMachine.dbConnect.retrieveResultSet("SELECT TRANSACTION_ID FROM "+ProtocolStateMachine.dbConnect.getDBName()+".Query "+"WHERE TRANSACTION_ID="+t_ID);
			if(rs.first()){	
				if(DNSWrapper.getResponseCode(payload) == 0 && (DNSWrapper.getResourceRecordType(payload, DNSWrapper.getEndOfQuestionSection(payload)) != 6)){
					SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getState("C")));
				}
				else{
					SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getState("D")));
				}
			}
			else{
				System.out.println("Query not recorded!");
				SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getEndState()));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void ExitActionB(){
		System.out.println("Exiting State B..");
	}
	
	public void EntryActionC(){
		System.out.println("Entering State C..");
	}
	
	public void nextStateC(StateMachine SM){
		System.out.println("In State C..");
		byte[] payload = PayloadRetriever.getUDPPayload(ProtocolStateMachine.currentPacket);
		String trID = DNSWrapper.getTransactionID(payload);
		long time = ProtocolStateMachine.currentPacket.getCaptureHeader().timestampInMillis();
		try {
			ProtocolStateMachine.dbConnect.populateTable("INSERT INTO "+ProtocolStateMachine.dbConnect.getDBName()+".Response "+"VALUES(?,?)",trID,ProtocolStateMachine.currentPacket);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ProtocolStateMachine.responsePackets.add(DNSWrapper.getTransactionID(payload),ProtocolStateMachine.currentPacket);
		if(DNSWrapper.getAnswerCount(payload) == 0 && DNSWrapper.getResourceRecordType(payload, DNSWrapper.getEndOfQuestionSection(payload)) == 2){
			//SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getState("A")));
			try {
				ProtocolStateMachine.dbConnect.populateTable("INSERT INTO "+ProtocolStateMachine.dbConnect.getDBName()+".Referral "+"VALUES(?,?)",trID,new Long(time));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getState("E")));
			
		}
		else{
			SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getEndState()));
		}
		
	}
	
	public void ExitActionC(){
		System.out.println("Exiting State C..");
	}
	
	public void EntryActionD(){
		System.out.println("Entering State D..");
	}
	
	public void nextStateD(StateMachine SM){
		System.out.println("In State D..");
		byte[] payload = PayloadRetriever.getUDPPayload(ProtocolStateMachine.currentPacket);
		if(DNSWrapper.getResponseCode(payload) == 3){
			System.out.println("No such domain exists!");
		}
		else if(DNSWrapper.getResourceRecordType(payload, DNSWrapper.getEndOfQuestionSection(payload)) == 6){
			System.out.println("Resource Records not available!");
		}
		else if(DNSWrapper.getResponseCode(payload) != 0){
			System.out.println("Error!");
		}
		SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getEndState()));
	}
	
	public void ExitActionD(){
		System.out.println("Exiting State D..");
	}
	
	public void EntryActionE(){
		System.out.println("Entering State E..");
		ProtocolStateMachine.packetOffset += 1;
		ProtocolStateMachine.setCurrentPacket(PcapReader.readPacket(ProtocolStateMachine.file,ProtocolStateMachine.packetOffset));
	}
	
	public void nextStateE(StateMachine SM){
		System.out.println("In State E..");
		long time,time_p;
		byte[] payload = PayloadRetriever.getUDPPayload(ProtocolStateMachine.currentPacket);
		String trID = DNSWrapper.getTransactionID(payload);
		ResultSet rs1,rs2;
		if(DNSWrapper.isResponse(payload)){
			try {
				rs1 = ProtocolStateMachine.dbConnect.getConnect().prepareStatement("SELECT TRANSACTION_ID FROM "+ProtocolStateMachine.dbConnect.getDBName()+".Query "+"WHERE TRANSACTION_ID="+trID).executeQuery();//ProtocolStateMachine.dbConnect.retrieveResultSet("SELECT TRANSACTION_ID FROM "+ProtocolStateMachine.dbConnect.getDBName()+".Query "+"WHERE TRANSACTION_ID="+trID);
				if(rs1.first()){	
					ProtocolStateMachine.dbConnect.populateTable("INSERT INTO "+ProtocolStateMachine.dbConnect.getDBName()+".Response "+"VALUES(?,?)",trID,ProtocolStateMachine.currentPacket);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//ProtocolStateMachine.responsePackets.put(DNSWrapper.getTransactionID(payload), new ArrayList<PcapPacket>());
			SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getState("E")));
		}
		else{
			try {
				rs2 =  ProtocolStateMachine.dbConnect.getConnect().prepareStatement("SELECT TIME_STAMP FROM "+ProtocolStateMachine.dbConnect.getDBName()+".Referral "+"WHERE TRANSACTION_ID="+trID+" ORDER BY TIME_STAMP DESC").executeQuery();//ProtocolStateMachine.dbConnect.retrieveResultSet("SELECT TIME_STAMP FROM "+ProtocolStateMachine.dbConnect.getDBName()+".Referral "+"WHERE TRANSACTION_ID="+trID+" ORDER BY TIME_STAMP DESC");
				if(rs2.first()){
					time = rs2.getLong("TIME_STAMP");
				time_p = ProtocolStateMachine.currentPacket.getCaptureHeader().timestampInMillis();
				if(ProtocolStateMachine.isTimeOut(time_p,time)){
					SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getEndState()));
				}
				else{
					SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getState("A")));
				}
			}
				else{
					System.out.println("Query not recorded!");
					SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getEndState()));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	
	public void ExitActionE(){
		System.out.println("Exiting State E..");
	}
	
	public void EntryActionEnd(){
		System.out.println("Entering end state..");
	}
	
	public void ExitActionEnd(){
		System.out.println("Exiting end state..");
	}
	
	public void nextStateEnd(StateMachine SM){
		System.out.println("In end state..");
		SM.setCurrentState(new TransitionSupport(SM.getCurrentState(),SM.getInitialState()));
	}
}
