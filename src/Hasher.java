import java.util.ArrayList;
import java.util.HashMap;

import org.jnetpcap.packet.PcapPacket;


public class Hasher extends HashMap<String,ArrayList<Packet>>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void add(String key,Packet packet){
		this.get(key).add(packet);
	}
	
	public ArrayList<Packet> put(String key,ArrayList<Packet> list){
		super.put(key, list);
		this.get(key).clear();
		return this.get(key);
	}
}
