import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;

import org.jnetpcap.packet.format.FormatUtils;

public class Convert {

	public static byte[] toByteArray(Object o){
		byte[] objBytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(o);
			oos.flush();
			objBytes = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Unable to open OOS!");
		}
		finally{
			if(bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("Unable to close BOS!");
				}
			}
			if(oos != null){
				try {
					oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("Unable to close OOS!");
				}
			}
		}
		return objBytes;
		
	}
	
	public static Object toObject(byte[] bytes){
		Object obj = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = null;
		try{
			ois = new ObjectInputStream(bis);
			obj = ois.readObject();
		}
		catch(IOException e){
			System.out.println("Unable to open OIS!");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Unable to read object!");
		}
		finally{
			if(bis != null){
				try {
					bis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("Unable to close BIS!");
				}
			}
			if(ois != null){
				try {
					ois.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("Unable to close OIS!");
				}
			}
		}
		return obj;
	}
	
	public static String toHex(byte[] bytes){
		return FormatUtils.hexdump(bytes);
	}
	
	public static String toBinaryString(byte[] bytes){
		int i;
		String str = "";// = new StringBuilder();
		for(i=0;i<bytes.length;i++){
			String s = Integer.toBinaryString((bytes[i] & 0xFF));
			s = "00000000".substring(0, 8 - s.length()) + s;
			str = str + s;//.append(s);
		}
		return str;//.toString();
	}
	
	public static String toBinaryString(byte bytes){
		String s = Integer.toBinaryString((bytes & 0xFF));
		s = "00000000".substring(0, 8 - s.length()) + s;
		return s;

	}
	
	public static String toHex(String bytes){
		String s = "";
		String nibble;
		int length = bytes.length();
		bytes = "00000000".substring(0, 4 - (length % 4)) + bytes;
		int i = 0;
		int j = 4;
		while(i < length){
			nibble = bytes.substring(i, j);
			if(nibble.equals("0000")){
				s = s + "0";
			}
			else if(nibble.equals("0001")){
				s = s + "1";
			}
			else if(nibble.equals("0010")){
				s = s + "2";
			}
			else if(nibble.equals("0011")){
				s = s + "3";
			}
			else if(nibble.equals("0100")){
				s = s + "4";
			}
			else if(nibble.equals("0101")){
				s = s + "5";
			}
			else if(nibble.equals("0110")){
				s = s + "6";
			}
			else if(nibble.equals("0111")){
				s = s + "7";
			}
			else if(nibble.equals("1000")){
				s = s + "8";
			}
			else if(nibble.equals("1001")){
				s = s + "9";
			}
			else if(nibble.equals("1010")){
				s = s + "A";
			}
			else if(nibble.equals("1011")){
				s = s + "B";
			}
			else if(nibble.equals("1100")){
				s = s + "C";
			}
			else if(nibble.equals("1101")){
				s = s + "D";
			}
			else if(nibble.equals("1110")){
				s = s + "E";
			}
			else if(nibble.equals("1111")){
				s = s + "F";
			}
			else{
				s = s + "";
			}
			i = j;
			j = j + 4;
		}
		return s;
	}
	
	public static void main(String[] args){
		byte[] bytes = new byte[2];
		bytes[0] = 0x23;
		bytes[1] = 0x48;
		System.out.println(toHex(bytes));
		System.out.println(toBinaryString(bytes));
		System.out.println(toBinaryString(bytes[1]));
		System.out.println(toHex("111100011"));
	}
	
}

