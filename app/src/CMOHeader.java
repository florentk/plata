

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
	
	
	
	/** number of hop */
	private byte hopCount;
	
	/** sequence number */
	private int seq;
	
	/**  the time for which CMO considere not accessible  */
	private int lifetime;
	
	/** CMO identity, in ASCII code  */
	private char cmoID[] = new char[CMO_IDENTITY_LENGHT];	
	
	/** CMO type */
	private short cmoType;
	


	public CMOHeader(byte hopCount, int seq, int lifetime, String cmoID,
			short cmoType) {
		this.hopCount = hopCount;
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
		
		hopCount = ByteArrayConvertTool.toByte(ByteArrayConvertTool.memcpy(data, i, 1));i+=1;
		seq = ByteArrayConvertTool.toInt(ByteArrayConvertTool.memcpy(data, i, 4));i+=4;
		lifetime = ByteArrayConvertTool.toInt(ByteArrayConvertTool.memcpy(data, i, 4));i+=4;	
		cmoID = ByteArrayConvertTool.toCharA(ByteArrayConvertTool.memcpy(data, i, CMO_IDENTITY_LENGHT));i+=CMO_IDENTITY_LENGHT;
		cmoType = ByteArrayConvertTool.toShort(ByteArrayConvertTool.memcpy(data, i, 2));i+=2;		

	}

	/**
	 * @return the hopCount
	 */
	public byte getHopCount() {
		return hopCount;
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
		return  new String(cmoID);
	}

	/**
	 * @return the CMO type
	 */
	public short getCmoType() {
		return cmoType;
	}
	
	public byte[] toByteArray(){
		byte b[];
		
		b = ByteArrayConvertTool.toByta(getHopCount());
		b = ByteArrayConvertTool.concat(b, ByteArrayConvertTool.toByta(getSeq()));
		b = ByteArrayConvertTool.concat(b, ByteArrayConvertTool.toByta(getLifetime()));
		b = ByteArrayConvertTool.concat(b, ByteArrayConvertTool.toByta(getCmoID()));
		b = ByteArrayConvertTool.concat(b, ByteArrayConvertTool.toByta(getCmoType()));
		
		return b;
	}	
	
	public String toString(){
		String s="";
		
		s+="HopCount : "+getHopCount()+"\n";
		s+="Sequence number : "+getSeq()+"\n";
		s+="Lifetime : "+getLifetime()+"\n";
		s+="Identity : "+getCmoID()+"\n";
		s+="Type : "+getCmoType()+"\n";
		
		return s;
	}
	
}
