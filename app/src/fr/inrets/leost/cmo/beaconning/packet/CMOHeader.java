package fr.inrets.leost.cmo.beaconning.packet;

import fr.inrets.leost.cmo.utils.ByteArrayConvert;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

/**
 * Packet for exchange between Communicating Mobile Object (CMO)
 * 
 * @author florent kaisser <florent.kaisse@free.fr>
 *
 */
public class CMOHeader {	
	
	//CMO with energy sufficient
	public static final short CMO_TYPE_CAR = 0x0001;
	public static final short CMO_TYPE_TRUCK = 0x0002;
	public static final short CMO_TYPE_BUS = 0x0003;	
	public static final short CMO_TYPE_MOTORBIKE = 0x0004;	
	
	//CMO with low energy
	public static final short CMO_TYPE_WALKER = 0x0011;
	public static final short CMO_TYPE_BIKE = 0x0012;	
	
	//CMO with energy sufficient but static
	public static final short CMO_TYPE_SPOT = 0x0081;
	
	public static final short ETHERTYPE_CMO = 0x0870;
	
	public static final short CMO_HEADER_LENGTH = 27;	
	private static final short CMO_IDENTITY_LENGHT = 16;	
	
	
	//Map String cmo type with the id cmo type
    private static final Map<Short, String> cmoTypeString;
    static {
        Map<Short, String> aMap = new HashMap<Short, String>(7);
        aMap.put(CMO_TYPE_CAR, "Car");
        aMap.put(CMO_TYPE_TRUCK, "Truck");
        aMap.put(CMO_TYPE_BUS, "Bus");
        aMap.put(CMO_TYPE_MOTORBIKE, "Motorbike");
        aMap.put(CMO_TYPE_WALKER, "Walker");
        aMap.put(CMO_TYPE_BIKE, "Bike");
        aMap.put(CMO_TYPE_SPOT, "Spot");

        cmoTypeString = Collections.unmodifiableMap(aMap);
    }    
    
	
	/** Time To Leave */
	private byte ttl;
	
	/** sequence number */
	private int seq;
	
	/**  the time for which CMO considere not accessible  */
	private int lifetime;
	
	/** CMO identity, in ASCII code  */
	private char cmoID[] = new char[CMO_IDENTITY_LENGHT];	
	
	/** CMO type */
	private short cmoType;
	


	public CMOHeader(byte ttl, int seq, int lifetime, String cmoID,
			short cmoType) {
		this.ttl = ttl;
		this.seq = seq;
		this.lifetime = lifetime;
		
		//complete with zero
		for (int i=0; i<CMO_IDENTITY_LENGHT; i++) 
			if ( i < cmoID.length())
				this.cmoID[i] = cmoID.charAt(i);
			else
				this.cmoID[i] = 0;
		
		this.cmoType = cmoType;

	}
	
	public CMOHeader(byte[] data){
		int i=0;
		
		ttl = ByteArrayConvert.toByte(ByteArrayConvert.memcpy(data, i, 1));i+=1;
		seq = ByteArrayConvert.toInt(ByteArrayConvert.memcpy(data, i, 4));i+=4;
		lifetime = ByteArrayConvert.toInt(ByteArrayConvert.memcpy(data, i, 4));i+=4;	
		cmoID = ByteArrayConvert.toCharA(ByteArrayConvert.memcpy(data, i, CMO_IDENTITY_LENGHT));i+=CMO_IDENTITY_LENGHT;
		cmoType = ByteArrayConvert.toShort(ByteArrayConvert.memcpy(data, i, 2));i+=2;		

	}
	
	public static String getTypeAvailable(){
		StringBuffer s=new StringBuffer("[ ");
		
		for (String cmostr : cmoTypeString.values())
			s.append(cmostr+" ");
			
		return s.toString()+"]";
	}
	
	public static short typeFromString(String str){

		for (Map.Entry<Short, String> e : cmoTypeString.entrySet())
			if(str.compareToIgnoreCase(e.getValue())==0)
				return  e.getKey();

		return -1;
	}
	
	public static String typeToString(short cmoType){	
		return cmoTypeString.get(cmoType);
	}

	/**
	 * @return the TTL
	 */
	public byte getTTL() {
		return ttl;
	}

	/**
	 * @return the seq number
	 */
	public int getSeq() {
		return seq;
	}

	/**
	 * @return the time for which vehicule considere not accessible 
	 */
	public int getLifetime() {
		return lifetime;
	}

	/**
	 * @return the CMO identity
	 */
	public String getCmoID() {
		return new String(cmoID);
		
		/*int end=0;
		
		for(int i=0;i<cmoID.length;i++ ){
			if(cmoID[i]==0){
				end = i;
				break;
			}
		}
		
		return  new String(cmoID).substring(0, end);*/
	}

	/**
	 * @return the CMO type
	 */
	public short getCmoType() {
		return cmoType;
	}
	
	public byte[] toByteArray(){
		byte b[];
		
		b = ByteArrayConvert.toByta(getTTL());
		b = ByteArrayConvert.concat(b, ByteArrayConvert.toByta(getSeq()));
		b = ByteArrayConvert.concat(b, ByteArrayConvert.toByta(getLifetime()));
		b = ByteArrayConvert.concat(b, ByteArrayConvert.toByta(getCmoID()));
		b = ByteArrayConvert.concat(b, ByteArrayConvert.toByta(getCmoType()));
		
		return b;
	}	
	
	public String toString(){
		String s="";
		
		s+="TTL : "+getTTL()+"\n";
		s+="Sequence number : "+getSeq()+"\n";
		s+="Lifetime : "+getLifetime()+"\n";
		s+="Identity : "+getCmoID()+"\n";
		s+="Type : "+typeToString(getCmoType())+"\n";
		
		return s;
	}
	
}
