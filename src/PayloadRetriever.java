
import org.jnetpcap.protocol.tcpip.Udp;


public class PayloadRetriever {
	
    public static byte[] getUDPPayload(Packet packet){
    	byte[] payload = null;
    	final Udp udp = new Udp();
    	if(packet.hasHeader(udp)){
    		if(udp.hasPayload()){
    			payload = udp.getPayload();
    		}
    	}
    	return payload;	
    }
}
