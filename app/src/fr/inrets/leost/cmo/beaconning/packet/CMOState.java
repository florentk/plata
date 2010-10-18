package fr.inrets.leost.cmo.beaconning.packet;

import fr.inrets.leost.cmo.utils.ByteArrayConvert;


/**
 * Packet for exchange the CMO state between Communicating Mobile Object (CMO)
 * 
 * @author florent kaisser <florent.kaisse@free.fr>
 * @assoc - - - ByteArrayConvert
 */
public final class CMOState extends CMOHeader{

	
	/**
	 * longitude (in ddmm.mmmm)
	 */
	private Float longitude;
	/**
	 * latitude (in ddmm.mmmm)
	 */
	private Float latitude;
	
	/**
	 * ellipsoidal elevation (in meters)
	 */
	private Float h;
	
	/** speed in meter per second*/
	private Float speed;

	/** orientation in degree (0 to 360) */
	private Float track;	
	
	/** time when the stat has changed*/
	private int time;
	

	public CMOState(CMOHeader cmo,Float longitude, Float latitude, Float h, Float speed,
			Float track, int time) {
		super(cmo.getTTL(), cmo.getSeq(), cmo.getLifetime(), cmo.getCmoID(),
				cmo.getCmoType());
		this.longitude = longitude;
		this.latitude = latitude;
		this.h = h;
		this.speed = speed;
		this.track = track;
		this.time = time;		
	}
	
	public CMOState(byte data[]){
		super(data);
		int i=CMO_HEADER_LENGTH;
		
		longitude = new Float(ByteArrayConvert.toFloat(ByteArrayConvert.memcpy(data, i, 4)));i+=4;
		latitude = new Float(ByteArrayConvert.toFloat(ByteArrayConvert.memcpy(data, i, 4)));i+=4;
		h = new Float(ByteArrayConvert.toFloat(ByteArrayConvert.memcpy(data, i, 4)));i+=4;
		speed = new Float(ByteArrayConvert.toFloat(ByteArrayConvert.memcpy(data, i, 4)));i+=4;
		track = new Float(ByteArrayConvert.toFloat(ByteArrayConvert.memcpy(data, i, 4)));i+=4;
		time = ByteArrayConvert.toInt(ByteArrayConvert.memcpy(data, i, 4));i+=4;		
		
	}

	/**
	 * @return the longitude (in ddmm.mmmm)
	 */
	public Float getLongitude() {
		return longitude;
	}

	/**
	 * @return the latitude (in ddmm.mmmm)
	 */
	public Float getLatitude() {
		return latitude;
	}

	/**
	 * @return the h (in meters)
	 */
	public Float getH() {
		return h;
	}


	/**
	 * @return the speed in meter per second
	 */
	public Float getSpeed() {
		return speed;
	}

	/**
	 * @return the orientation in degree (0 to 360)
	 */
	public Float getTrack() {
		return track;
	}
	
	
	/**
	 * @return time when state has changed (in second)
	 */
	public int getTime() {
		return time;
	}	


	public byte[] toByteArray(){
		byte b[];
		
		b = super.toByteArray();
		b = ByteArrayConvert.concat(b, ByteArrayConvert.toByta(getLongitude().floatValue()));		
		b = ByteArrayConvert.concat(b, ByteArrayConvert.toByta(getLatitude().floatValue()));
		b = ByteArrayConvert.concat(b, ByteArrayConvert.toByta(getH().floatValue()));
		b = ByteArrayConvert.concat(b, ByteArrayConvert.toByta(getSpeed().floatValue()));
		b = ByteArrayConvert.concat(b, ByteArrayConvert.toByta(getTrack().floatValue()));
		b = ByteArrayConvert.concat(b, ByteArrayConvert.toByta(getTime()));
		
		return b;
	}
	
	public String toString(){
		String s=super.toString();
	
		s+="Longitude : "+getLongitude()+"\n";
		s+="Latitude : "+getLatitude()+"\n";
		s+="Altitude : "+getH()+"\n";
		s+="Speed : "+getSpeed()+"\n";
		s+="Orientation : "+getTrack()+"\n";
		s+="Time : "+getTime()+"\n";		
		
		return s;
	}
}
