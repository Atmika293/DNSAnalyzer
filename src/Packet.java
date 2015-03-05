
import java.io.Serializable;

import org.jnetpcap.packet.PcapPacket;


public class Packet extends PcapPacket implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		Packet(PcapPacket packet){
			super(packet);
		}
}
