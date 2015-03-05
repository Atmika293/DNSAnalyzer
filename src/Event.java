import java.util.Date;
import java.util.Observable;

import org.jnetpcap.packet.PcapPacket;


public class Event extends Observable  {
		private PcapPacket packet;
		private long timeStamp;
		
		Event(PcapPacket p){
			this.packet = p;
			this.timeStamp = p.getCaptureHeader().timestampInMillis();
		}
		
		public PcapPacket getPacket(){
			return this.packet;
		}
		
		public long getTimeStampInMillis(){
			return this.timeStamp;
		}
		
		public Date getTimeStamp(){
			return new Date(this.timeStamp);
		}
}
