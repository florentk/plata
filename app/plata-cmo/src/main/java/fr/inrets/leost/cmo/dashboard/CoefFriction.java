package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.cmo.utils.Physics;

import fr.inrets.leost.weather.*;
import net.sf.jweather.metar.*;

import java.util.Iterator;

public class CoefFriction implements Indicator {

	private Weather w;
	private Double coef = Physics.COEF_FRICTION_AVG;
	
	
	
	public CoefFriction(Weather weather) {
		this.w = weather;
	}



	@Override
	public void update() {
		Metar m = w.getCurrentCondition();
		
		coef = Physics.COEF_FRICTION_AVG;
		boolean rain = false;
	
		if(m != null) {
			if (m.getWeatherConditions() != null) {
				Iterator i = m.getWeatherConditions().iterator();
				while (i.hasNext()){
					WeatherCondition wc=(WeatherCondition)i.next();
					if (wc.isDrizzle()) {
						rain = true;
					} else if (wc.isRain() || wc.isHail() || wc.isSmallHail()) {
						rain = true;						
					} else if (wc.isSnow() || wc.isSnowGrains() || wc.isIceCrystals() || wc.isIcePellets()) {
						rain = true;						
					}			
				}
			}
			if (rain) coef /= 2.0;
		}		
	}

	/**
	 * @return the speed
	 */
	public Double getCoef() {
		return coef;
	}

	public String name(){
		return "Coefficient of friction";
	}


	public String toString(){
		return String.format("%01.2f", getCoef()) ;
	}

}
