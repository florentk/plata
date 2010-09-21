/**
 * The WGS84 international system is a geographic coordinate system
 * that is used by GPS and coverts the whole earth. It is used as
 * a central system for all coordinate conversions.
 * 
 * @author Johan Montagnat <johan@creatis.insa-lyon.fr>
 */
public class WGS84 {
	/**
	 * longitude (in radian)
	 */
	private Double longitude;
	/**
	 * latitude (in radian)
	 */
	private Double latitude;
	/**
	 * ellipsoidal elevation (in meters)
	 */
	private Double h;



	/**
	 * initializes a new WGS84 coordinate at (0, 0, 0)
	 */
	public WGS84() {
		this.latitude = new Double (0.0);
		this.longitude = new Double (0.0);
		this.h = new Double (0.0);
	}

	/**
	 * initializes a new WGS84 coordinate
	 *
	 * @param longitude longitude in radian
	 * @param latitude latitude in radian
	 * @param h ellipsoidal elevation in meters
	 */
	public WGS84(Double longitude, Double latitude, Double h) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.h = h;
	}

	/**
	 * returns longitude angle in radian
	 */
	public double longitude() {
		return longitude;
	}

	/**
	 * returns latitude angle in radian
	 */
	public double latitude() {
		return latitude;
	}

	/**
	 * returns ellipsoidal elevation in meters
	 */
	public double h() {
		return h;
	}
	
	public String toString(){
		return "Longitude : " + longitude + " Latitude : "+ latitude + " Altitude : " + h;
	}
	
	public boolean equals(WGS84 a, WGS84 b){
		return 		a.latitude.equals(b.latitude)
				&& 	a.longitude.equals(b.longitude)
				&&  a.h.equals(b.h);
	}

}