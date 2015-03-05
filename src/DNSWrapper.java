import java.util.Arrays;




public class DNSWrapper {
	
	private static int HeaderLength = 12;
	
	public static String getTransactionID(byte[] payloadData){
		return Integer.toString(Integer.parseInt(Convert.toBinaryString(Arrays.copyOfRange(payloadData, 0, HeaderLength - 10)), 2));
	}
	
	private static String getFlags(byte[] payloadData){
		return Convert.toBinaryString(Arrays.copyOfRange(payloadData, HeaderLength - 10, HeaderLength - 8));
	}
	
/*	private int getBit(byte data,int pos){
		if((data & (1 << pos)) == 0){
			return 0;
		}
			return 1;	
	}*/
	
	public static boolean isQuery(byte[] payloadData){
		String flags = getFlags(payloadData);
		if(flags.charAt(0) == '0'){
			return true;
		}
		return false;
	}
	
	public static boolean isResponse(byte[] payloadData){
		String flags = getFlags(payloadData);
		if(flags.charAt(0) == '1'){
			return true;
		}
		return false;
	}
	
	public static int gettypeOfQuery(byte[] payloadData){
		String flags = getFlags(payloadData);
		int opCode = Integer.parseInt(flags.substring(1, 5),2);
		switch(opCode){
		
		case 0 : {System.out.println("QUERY");return opCode;}
		case 1 : {System.out.println("IQUERY");return opCode;}
		case 2 : {System.out.println("STATUS");return opCode;}
		case 3 : {System.out.println("RESERVED/NOT USED");return opCode;}
		case 4 : {System.out.println("NOTIFY");return opCode;}
		case 5 : {System.out.println("UPDATE");return opCode;}
	    default : return opCode;
	    
		}
	}
	
	public static boolean isResponseServerAuthoritative(byte[] payloadData){
		if(isResponse(payloadData)){
			String flags = getFlags(payloadData);
			if(flags.charAt(5) == '1'){
				return true;
			}
		}
		else{
			System.out.println("Invalid argument(Not a response packet)!");
		}
		return false;
	}
	
	public static boolean isRecursionDesired(byte[] payloadData){
		String flags = getFlags(payloadData);
		if(flags.charAt(7) == '1'){
			if(isResponse(payloadData)){
				System.out.println("Recursion is supported by the responding server.");
			}
			return true;
		}
		else if(isResponse(payloadData)){
			System.out.println("Recursion is not supported by the responding server.");
		}
		return false;
	}
	
	public static boolean isRecursionAvailable(byte[] payloadData){
		if(isResponse(payloadData)){
			String flags = getFlags(payloadData);
			if(flags.charAt(8) == '1'){
				return true;
			}
		}
		else{
			System.out.println("Invalid argument(Not a response packet)!");
		}
		return false;
	}
	
	public static int getResponseCode(byte[] payloadData){
		if(isResponse(payloadData)){
			String flags = getFlags(payloadData);
			int responseCode = Integer.parseInt(flags.substring(12, 16),2);
			switch(responseCode){
				
			case 0 : {System.out.println("NO ERROR");return responseCode;}
			case 1 : {System.out.println("FORMAT ERROR");return responseCode;}
			case 2 : {System.out.println("SERVER FAILURE");return responseCode;}
			case 3 : {System.out.println("NAME ERROR");return responseCode;}
			case 4 : {System.out.println("NOT IMPLEMENTED");return responseCode;}
			case 5 : {System.out.println("REFUSED");return responseCode;}
			case 6 : {System.out.println("YX DOMAIN");return responseCode;}
			case 7 : {System.out.println("YX RR SET");return responseCode;}
			case 8 : {System.out.println("NX RR SET");return responseCode;}
			case 9 : {System.out.println("NOT AUTHORIZED");return responseCode;}
			case 10 :{System.out.println("NOT ZONE");return responseCode;}
			default : return responseCode;
			}
		}
		else{
			System.out.println("Invalid argument(Not a response packet)!");
			return -1;
		}
		
	}
	
	public static int getQuestionCount(byte[] payloadData){
		return Integer.parseInt(Convert.toBinaryString(Arrays.copyOfRange(payloadData, HeaderLength - 8 , HeaderLength - 6)),2);
	}
	
	public static int getAnswerCount(byte[] payloadData){
		return Integer.parseInt(Convert.toBinaryString(Arrays.copyOfRange(payloadData, HeaderLength - 6, HeaderLength - 4)),2);
	}
	public static int getAuthorityRecordCount(byte[] payloadData){
		return Integer.parseInt(Convert.toBinaryString(Arrays.copyOfRange(payloadData, HeaderLength - 4, HeaderLength - 2)),2);
	}
	public static int getAdditionalRecordCount(byte[] payloadData){
		return Integer.parseInt(Convert.toBinaryString(Arrays.copyOfRange(payloadData, HeaderLength - 2, HeaderLength)),2);
	}
	private static byte[] getPostHeader(byte[] payloadData){
		return Arrays.copyOfRange(payloadData, HeaderLength, payloadData.length);
	}
	private static int getEndOfName(byte[] postHeader,int begin){
			int i = 0;
			int endOfName = begin; //0
			for(i= begin;i<postHeader.length;i++){
				if(Integer.parseInt(Convert.toBinaryString(postHeader[i]),2) == 0){
					endOfName = i;
					break;
				}
			}
			return endOfName;
	}
	
	public static String getName(byte[] payloadData,int begin){
		byte[] postHeader = getPostHeader(payloadData);
		if(begin < postHeader.length && begin >= 0){
			int i = 0;
			int j,sublength;
			int endOfName = getEndOfName(postHeader,begin);
			byte[] name = Arrays.copyOfRange(postHeader, begin, endOfName);
			String Name = "";
			while(i < name.length){
				if(isPointer(postHeader,begin+i)){
					Name = Name + getName(payloadData,getOffsetOfResourceRecordName(payloadData,begin+i));
					i += 2;
				}
				else{
					sublength = Integer.parseInt(Convert.toBinaryString(name[i]), 2);
					j = i+1;
					while((j-i) <= sublength){
						Name = Name + (char)name[j];
						j++;
					}
					i = i + sublength + 1;
				}
				Name = Name + ".";
			}
			return Name.substring(0, Name.length() - 1);
		}
		else{
			System.out.println("The corresponding record does not exist!");
			return null;
		}
	}
	
	public static int getNameLength(byte[] payloadData,int begin){
		if(getName(payloadData,begin) != null){
			if(isPointer(getPostHeader(payloadData),begin)){
				return 2;
			}
			else
				return getName(payloadData,begin).length() + 2;
		}
		else
			return 0;
	}
	
	public static String getQuestionName(byte[] payloadData){
		 byte[] postHeader = getPostHeader(payloadData);
			int i = 0;
			int j,sublength;
			int endOfName = getEndOfName(postHeader,0);
			byte[] name = Arrays.copyOfRange(postHeader, 0, endOfName);
			String Name = "";
			while(i < name.length){
					sublength = Integer.parseInt(Convert.toBinaryString(name[i]), 2);
					j = i+1;
					while((j-i) <= sublength){
						Name = Name + (char)name[j];
						j++;
					}
					i = i + sublength + 1;
					Name = Name + ".";
				}	
			return Name.substring(0, Name.length() - 1);
		}
	
	public static int getRecordTypeToBeReturned(byte[] payloadData){
		byte[] postHeader = getPostHeader(payloadData);
		int endOfName = getEndOfName(postHeader,0);
		byte[] questionType = Arrays.copyOfRange(postHeader, endOfName + 1, endOfName + 3);
		int hexEquivalent = Integer.parseInt(Convert.toBinaryString(questionType), 2);//, 16);
		if(hexEquivalent == 1)
			System.out.println("Host(A) Record");
		else if(hexEquivalent == 2)
			System.out.println("Name Server(NS) Record");
		else if(hexEquivalent == 5)
			System.out.println("Canonical Name(Alias/CNAME) Record");
		else if(hexEquivalent == 6)
			System.out.println("Start Of Authority(SOA) Record");
		else if(hexEquivalent == 11)
			System.out.println("Well Known Source(WKS) Record");
		else if(hexEquivalent == 12)
			System.out.println("Pointer(Reverse-lookup/PTR) Record");
		else if(hexEquivalent == 15)
			System.out.println("Mail Exchange(MX) Record");
		else if(hexEquivalent == 33)
			System.out.println("Services(SVR) Record");
		else if(hexEquivalent == 251)
			System.out.println("Incremental Zone Transfer(IXFR) Record");
		else if(hexEquivalent == 252)
			System.out.println("Standard Zone Transfer(AXFR) Record");
		else if(hexEquivalent == 255)
			System.out.println("All Records");
		else
			System.out.println("Not commonly used");

			return hexEquivalent;
		
	}
	
	public static int getQuestionClass(byte[] payloadData){
		byte[] postHeader = getPostHeader(payloadData);
		int endOfName = getEndOfName(postHeader,0);
		byte[] questionClass = Arrays.copyOfRange(postHeader, endOfName + 3, endOfName + 5);
		if(Integer.parseInt(Convert.toBinaryString(questionClass),2) == 1){
			System.out.println("Internet Class");
		}
		return Integer.parseInt(Convert.toBinaryString(questionClass), 2);//, 16);
	}
	
	public static int getEndOfQuestionSection(byte[] payloadData){
		return getNameLength(payloadData,0) + 4;
	}
	
	private static boolean isPointer(byte[] postHeader,int start){
			String isPointer = Convert.toBinaryString(postHeader[start]).substring(0, 2);
			if(isPointer.equals("11")){
				return true;
			}
		return false;
	}
	
	public static int getResourceRecordLength(byte[] payloadData,int start){
		int length = 0;
		byte[] postHeader = getPostHeader(payloadData);
		if(start < postHeader.length){
			if(isPointer(postHeader,start)){
				length += 2;
			}
			else{
				length += (getEndOfName(postHeader,start) - start) + 1;
			}
			length += 10 + Integer.parseInt(Convert.toBinaryString(Arrays.copyOfRange(postHeader, start + 10,start + 12 )), 2);
			return length;
		}
		else
			return 0;
	}
	private static int getOffsetOfResourceRecordName(byte[] payloadData,int start){
		byte[] postHeader = getPostHeader(payloadData);
		if(isPointer(postHeader,start)){
			return Integer.parseInt(Convert.toBinaryString(Arrays.copyOfRange(postHeader, start, start + 2)).substring(2,16), 2) - HeaderLength;
		}
		return start;
	}
	public static int getResourceRecordType(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			byte[] postHeader = getPostHeader(payloadData);
			if(start < postHeader.length && start >= 0){
				int begin = start + getNameLength(payloadData,start);
				//int begin = getEndOfName(payloadData,start);
				byte[] RRType = Arrays.copyOfRange(postHeader, begin, begin + 2);
				int hexEquivalent = Integer.parseInt(Convert.toBinaryString(RRType), 2);//, 16);
				if(hexEquivalent == 1)
					System.out.println("Host(A) Record");
				else if(hexEquivalent == 2)
					System.out.println("Name Server(NS) Record");
				else if(hexEquivalent == 5)
					System.out.println("Canonical Name(Alias/CNAME) Record");
				else if(hexEquivalent == 6)
					System.out.println("Start Of Authority(SOA) Record");
				else if(hexEquivalent == 11)
					System.out.println("Well Known Source(WKS) Record");
				else if(hexEquivalent == 12)
					System.out.println("Pointer(Reverse-lookup/PTR) Record");
				else if(hexEquivalent == 15)
					System.out.println("Mail Exchange(MX) Record");
				else if(hexEquivalent == 33)
					System.out.println("Services(SVR) Record");
				else if(hexEquivalent == 251)
					System.out.println("Incremental Zone Transfer(IXFR) Record");
				else if(hexEquivalent == 252)
					System.out.println("Standard Zone Transfer(AXFR) Record");
				else if(hexEquivalent == 255)
					System.out.println("All Records");
				else
					System.out.println("Not commonly used");
					
				return hexEquivalent;
			}
			else{
				System.out.println("The corresponding record does not exist!");
				return -1;
			}
		}
			else {
				System.out.println("Invalid argument(Not a response packet!");
				return -1;
			}
	}
	
	public static int getResourceRecordClass(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			byte[] postHeader = getPostHeader(payloadData);
			if(start < postHeader.length && start >= 0){
				int begin = start + getNameLength(payloadData,start);
				byte[] RRClass = Arrays.copyOfRange(postHeader, begin + 2, begin + 4);
				int rrClass = Integer.parseInt(Convert.toBinaryString(RRClass),2);
				if(rrClass == 1){
					System.out.println("Internet Class");
				}
				return rrClass;//, 16);
			}
			else{
				System.out.println("The corresponding record does not exist!");
				return -1;
			}
		}
		else{
			System.out.println("Invalid argument(Not a response packet!");
			return -1;
		}
	} 
	
	public static int getTimeToLIVE(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			byte[] postHeader = getPostHeader(payloadData);
			if(start < postHeader.length && start >= 0){
				int begin = start + getNameLength(payloadData,start);
				byte[] TTL = Arrays.copyOfRange(postHeader, begin + 4, begin + 8);
				return Integer.parseInt(Convert.toBinaryString(TTL), 2);
			}
			else{
				System.out.println("The corresponding record does not exist!");
				return 0;
			}
		}
		else
			System.out.println("Invalid argument(Not a response packet)!");
		return 0;
	}
	
	public static int getRecordDataLength(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			byte[] postHeader = getPostHeader(payloadData);
			if(start < postHeader.length && start >= 0){
				int begin = start + getNameLength(payloadData,start);
				byte[] RDataLength = Arrays.copyOfRange(postHeader, begin + 8, begin + 10);
				return Integer.parseInt(Convert.toBinaryString(RDataLength), 2);
			}
			else{
				System.out.println("The corresponding record does not exist!");
				return 0;
			}
		}
		else
			System.out.println("Invalid argument(Not a response packet)!");
		return 0;
	}
	
	
	public static String getHostIP(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			String IPv4 = "";
			int i = 0;
			byte[] ip;
			byte[] postHeader = getPostHeader(payloadData);
			if(start < postHeader.length && start >= 0){
				int begin = start + getNameLength(payloadData,start);
				byte[] RRType = Arrays.copyOfRange(postHeader, begin, begin + 2);
				int hexEquivalent = Integer.parseInt(Convert.toBinaryString(RRType), 2);
				if(hexEquivalent == 1){
					ip = Arrays.copyOfRange(postHeader, begin + 10, begin + 14);
					for(i = 0;i < ip.length;i++){
						IPv4 = IPv4 + Integer.toString(Integer.parseInt(Convert.toBinaryString(ip[i]), 2)) + ".";
					}
					return IPv4.substring(0, IPv4.length()-1);
				}
				else
					return "Resource Record is not of the type 'Host Record'";
			}
			else{
				System.out.println("The corresponding record does not exist!");
				return null;
			}
		}
		else{
			System.out.println("Invalid argument(Not a response packet!");
			return null;
		}
	}
	
	public static String getHostName(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			byte[] postHeader = getPostHeader(payloadData);
			byte[] RRType = Arrays.copyOfRange(postHeader, start + 2, start + 4);
			int hexEquivalent = Integer.parseInt(Convert.toBinaryString(RRType), 2);
			if(hexEquivalent == 12){
				int begin = start + 10 + getNameLength(payloadData,start);
				if(!isPointer(postHeader,begin))
					return getName(payloadData,begin);
				else
					return getName(payloadData,getOffsetOfResourceRecordName(payloadData,begin));
			}
			else{
				System.out.println("Resource Record is not of the type 'Pointer Record'");
				return null;
			}
		}
		else{
			System.out.println("Invalid argument(Not a response packet!");
			return null;
		}
	}
	
	public static String getNameServerName(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			byte[] postHeader = getPostHeader(payloadData);
			byte[] RRType = Arrays.copyOfRange(postHeader, start + 2, start + 4);
			int hexEquivalent = Integer.parseInt(Convert.toBinaryString(RRType), 2);
			if(hexEquivalent == 2){
					int begin = start + 10 + getNameLength(payloadData,start);
					if(!isPointer(postHeader,begin))
						return getName(payloadData,begin);
					else
						return getName(payloadData,getOffsetOfResourceRecordName(payloadData,begin));
				}
				else{
					System.out.println("Resource Record is not of the type 'Name Server Record'");
					return null;
				}
		}
			else{
				System.out.println("Invalid argument(Not a response packet!");
				return null;
			}
	}
	
	public static String getPrimaryMaster(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			String name = "";
			byte[] postHeader = getPostHeader(payloadData);
			byte[] RRType = Arrays.copyOfRange(postHeader, start + 2, start + 4);
			int hexEquivalent = Integer.parseInt(Convert.toBinaryString(RRType), 2);
			if(hexEquivalent == 6){
				int begin = start + 10 + getNameLength(payloadData,start);
					if(isPointer(postHeader,begin)){
						return getName(payloadData,getOffsetOfResourceRecordName(payloadData,begin));
					}
					else{
						name = getName(payloadData,begin);
						return name.substring(0, name.indexOf("net.") + 4);
					}
					
			}
			else{
				System.out.println("Resource Record is not of the type 'Start Of Authority Record'");
				return null;
			}
	}
		else{
			System.out.println("Invalid argument(Not a response packet!");
			return null;
		}
	}
	
	public static String getAdministratorMB(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			String name = "";
			byte[] postHeader = getPostHeader(payloadData);
			byte[] RRType = Arrays.copyOfRange(postHeader, start + 2, start + 4);
			int hexEquivalent = Integer.parseInt(Convert.toBinaryString(RRType), 2);
			if(hexEquivalent == 6){
				int begin = start + 10 + getNameLength(payloadData,start);
					if(isPointer(postHeader,begin)){
						return getName(payloadData,getOffsetOfResourceRecordName(payloadData,begin));
					}
					else{
						name = getName(payloadData,begin);
						return name.substring(name.indexOf("net.") + 4, name.length()) + ".";
					}
					
			}
			else{
				System.out.println("Resource Record is not of the type 'Start Of Authority Record'");
				return null;
			}
	}
		else{
			System.out.println("Invalid argument(Not a response packet!");
			return null;
		}
	}
	
	private static int getEndOfAdminMB(byte[] payloadData,int start){
            return start + DNSWrapper.getRecordDataLength(payloadData, start) - 8;
	}
	
	public static String getSOASerialNumber(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			byte[] postHeader = getPostHeader(payloadData);
			byte[] RRType = Arrays.copyOfRange(postHeader, start + 2, start + 4);
			int hexEquivalent = Integer.parseInt(Convert.toBinaryString(RRType), 2);
			if(hexEquivalent == 6){
				int end = getEndOfAdminMB(payloadData,start);
				return Integer.toString(Integer.parseInt(Convert.toBinaryString(Arrays.copyOfRange(payloadData, end, end + 4)), 2));
			}
			else{
				System.out.println("Resource Record is not of the type 'Start of Authority Record'");
				return null;
			}
	}
		else{
			System.out.println("Invalid argument(Not a response packet!");
			return null;
		}
	}
	
	public static int getSOARefreshInterval(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			byte[] postHeader = getPostHeader(payloadData);
			byte[] RRType = Arrays.copyOfRange(postHeader, start + 2, start + 4);
			int hexEquivalent = Integer.parseInt(Convert.toBinaryString(RRType), 2);
			if(hexEquivalent == 6){
				int end = getEndOfAdminMB(payloadData,start);
				return Integer.parseInt(Convert.toBinaryString(Arrays.copyOfRange(payloadData, end + 4, end + 8)), 2);
			}
			else
				System.out.println("Resource Record is not of the type 'Start of Authority Record'");
		}
		else
			System.out.println("Invalid argument(Not a response packet)!");
	    return 0;
			
	}
	
	public static int getSOARetryInterval(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			byte[] postHeader = getPostHeader(payloadData);
			byte[] RRType = Arrays.copyOfRange(postHeader, start + 2, start + 4);
			int hexEquivalent = Integer.parseInt(Convert.toBinaryString(RRType), 2);
			if(hexEquivalent == 6){
				int end = getEndOfAdminMB(payloadData,start);
				return Integer.parseInt(Convert.toBinaryString(Arrays.copyOfRange(payloadData, end + 8, end + 12)), 2);
			}
			else
				System.out.println("Resource Record is not of the type 'Start of Authority Record'");
		}
		else
			System.out.println("Invalid argument(Not a response packet)!");
	    return 0;	
	}
	
	public static int getSOAExpirationLimit(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			byte[] postHeader = getPostHeader(payloadData);
			byte[] RRType = Arrays.copyOfRange(postHeader, start + 2, start + 4);
			int hexEquivalent = Integer.parseInt(Convert.toBinaryString(RRType), 2);
			if(hexEquivalent == 6){
				int end = getEndOfAdminMB(payloadData,start);
				return Integer.parseInt(Convert.toBinaryString(Arrays.copyOfRange(payloadData, end + 12, end + 16)), 2);
			}
			else
				System.out.println("Resource Record is not of the type 'Start of Authority Record'");
		}
		else
			System.out.println("Invalid argument(Not a response packet)!");
	    return 0;	
	}
	
	public static int getSOAMinimumTTL(byte[] payloadData,int start){
		if(isResponse(payloadData)){
			byte[] postHeader = getPostHeader(payloadData);
			byte[] RRType = Arrays.copyOfRange(postHeader, start + 2, start + 4);
			int hexEquivalent = Integer.parseInt(Convert.toBinaryString(RRType), 2);
			if(hexEquivalent == 6){
				int end = getEndOfAdminMB(payloadData,start);
				return Integer.parseInt(Convert.toBinaryString(Arrays.copyOfRange(payloadData, end + 16, end + 20)), 2);
			}
			else
				System.out.println("Resource Record is not of the type 'Start of Authority Record'");
		}
		else
			System.out.println("Invalid argument(Not a response packet)!");
	    return 0;	
	}
	
	public static void main(String[] args){
   	 String file = "data\\DNS_Traffic000.pcap";
   	 byte[] payload = PayloadRetriever.getUDPPayload(PcapReader.readPacket(file));
   	 byte[] payload2 = PayloadRetriever.getUDPPayload(PcapReader.readPacket(file,55));
   	 System.out.println(Convert.toHex(payload));
   	 System.out.println(Convert.toHex(payload2));
   	 System.out.println(""+DNSWrapper.isQuery(payload));
   	 System.out.println(DNSWrapper.getFlags(payload));
   	 System.out.println(""+DNSWrapper.isResponse(payload2));
   	 System.out.println(DNSWrapper.gettypeOfQuery(payload2));
   	System.out.println(DNSWrapper.getResponseCode(payload));
   	System.out.println(""+DNSWrapper.getQuestionCount(payload2));
   	System.out.println(""+DNSWrapper.getAnswerCount(payload2));
   	System.out.println(""+DNSWrapper.getAuthorityRecordCount(payload2));
   	System.out.println(""+DNSWrapper.getAdditionalRecordCount(payload2));
   	System.out.println(""+DNSWrapper.getName(payload2,0)+"\n" + DNSWrapper.getNameLength(payload2,0));
    System.out.println(DNSWrapper.getRecordTypeToBeReturned(payload2));
    System.out.println(""+DNSWrapper.getNameLength(payload2,0));
    int end = getEndOfQuestionSection(payload2);
    int i = 0;
    //System.out.println(""+end + Convert.toBinaryString(payload2[12+end]) + Convert.toBinaryString(payload2[12+end+getNameLength(payload2,end)+10]) + Convert.toBinaryString(payload2[12+end+getNameLength(payload2,end)+11]));
    System.out.println(""+DNSWrapper.getName(payload2,end));
    while(i < DNSWrapper.getAnswerCount(payload2)){
    	System.out.println(""+DNSWrapper.getResourceRecordType(payload2, end));
    	System.out.println(DNSWrapper.getHostIP(payload2, end));
    	end = end + DNSWrapper.getResourceRecordLength(payload2, end);
    	i++;
    }
    
    //System.out.println(""+DNSWrapper.getPrimaryMaster(payload2, end));
    //System.out.println(""+DNSWrapper.getAdministratorMB(payload2, end));
    //System.out.println(""+DNSWrapper.getSOASerialNumber(payload2,end));
    //System.out.println(DNSWrapper.getHostIP(payload2, end));
    //System.out.println(""+DNSWrapper.getResourceRecordType(payload2, end + DNSWrapper.getResourceRecordLength(payload2, end)));
    //System.out.println(DNSWrapper.getHostIP(payload2, end + DNSWrapper.getResourceRecordLength(payload2, end)));
	}
	
}
