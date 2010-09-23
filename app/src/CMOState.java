
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
	
	CMOHeader cmo;

	public CMOState(CMOHeader cmo,Float longitude, Float latitude, Float h, Float speed,
			Float track) {
		super(cmo.getHopCount(), cmo.getSeq(), cmo.getLifetime(), cmo.getCmoID(),
				cmo.getCmoType());
		this.longitude = longitude;
		this.latitude = latitude;
		this.h = h;
		this.speed = speed;
		this.track = track;
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


	public byte[] toByteArray(){
		byte b[];
		
		b = super.toByteArray();
		b = ByteArrayConvertTool.concat(b, ByteArrayConvertTool.toByta(getLongitude().floatValue()));		
		b = ByteArrayConvertTool.concat(b, ByteArrayConvertTool.toByta(getLatitude().floatValue()));
		b = ByteArrayConvertTool.concat(b, ByteArrayConvertTool.toByta(getH().floatValue()));
		b = ByteArrayConvertTool.concat(b, ByteArrayConvertTool.toByta(getSpeed().floatValue()));
		b = ByteArrayConvertTool.concat(b, ByteArrayConvertTool.toByta(getTrack().floatValue()));

		
		return b;
	}
	
}
