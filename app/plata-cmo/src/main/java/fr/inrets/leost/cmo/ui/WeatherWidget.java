package fr.inrets.leost.cmo.ui;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;

import fr.inrets.leost.weather.*;
import net.sf.jweather.metar.*;

/**
 * Widget for representing weather icon
 * @author florent kaisser
 */
public class WeatherWidget extends Canvas implements PaintListener {
	public static final double KMH = 1.609344d;
	
	private Image sun,cloud,fog,moon,overcloud,rain,rainfall,snow,snowfall,thunder;
	private Weather w;
	private Font font;
	
	public WeatherWidget(Composite arg0, int arg1, Display d, Weather w) {
		super(arg0, arg1);
		
		font = new Font(d,"Arial",22,SWT.BOLD); 
		
	      sun = new Image(d, getClass().getResourceAsStream("/weather-sun.png"));
	      cloud = new Image(d, getClass().getResourceAsStream("/weather-cloud.png"));  
            fog = new Image(d, getClass().getResourceAsStream("/weather-fog.png"));
            moon = new Image(d, getClass().getResourceAsStream("/weather-moon.png"));
            overcloud = new Image(d, getClass().getResourceAsStream("/weather-overcloud.png"));
            rain = new Image(d, getClass().getResourceAsStream("/weather-rain.png"));
            rainfall = new Image(d, getClass().getResourceAsStream("/weather-rainfall.png"));
            snow = new Image(d, getClass().getResourceAsStream("/weather-snow.png"));
            snowfall = new Image(d, getClass().getResourceAsStream("/weather-snowfall.png"));
            thunder = new Image(d, getClass().getResourceAsStream("/weather-thunder.png"));
		
		this.w=w;
		
            addPaintListener(this);		
	}
	
    private void addSky(Set set, SkyCondition sc){
    
            Image astre = sun;
    
 		if (sc.isClear()) {
                  set.add(astre);
		} else if (sc.isFewClouds() || sc.isScatteredClouds() || sc.isBrokenClouds()) {
                  set.add(astre);		
                  set.add(cloud);              
		} else if (sc.isOvercast()) {
                  set.add(cloud);
                  set.add(overcloud); 
		} else if (sc.isNoSignificantClouds()) {
                  set.add(astre);
            }
    
    }
    
    
    private void addWeather(Set set, WeatherCondition wc){
    

            if (wc.isThunderstorms()) {
			set.add(thunder);
            }

		if (wc.isDrizzle()) {
			set.add(rain);
			if (wc.isHeavy()) set.add(rainfall);
		} else if (wc.isShowers() || wc.isRain() || wc.isHail() || wc.isSmallHail()) {
			set.add(rain);
			if (wc.isHeavy()) set.add(rainfall);
		} else if (wc.isSnow() || wc.isSnowGrains() || wc.isIceCrystals() || wc.isIcePellets()) {
			set.add(snow);
			if (wc.isHeavy()) set.add(snowfall);
		} else if (wc.isMist() || wc.isFog()) {
			set.add(fog);
		} 
    
    }    

    public boolean checkAndDraw(GC gc, Set set, Image img){
      if(set.contains(img)) {
            gc.drawImage(img , getSize().x/2 - sun.getBounds().width / 2,0);
            return true;
      }
      
      return false;
    }

	private int mphToKmh (double s){
		return new Double(s*KMH).intValue();
	}
    
    public void drawTemp(GC gc, Metar m, boolean up) {
            int x,y;
    		StringBuffer str = new StringBuffer();
		str.append(m.getTemperatureMostPreciseInCelsius() + " °C ");		
		str.append(mphToKmh(m.getWindSpeedInMPH()) + " km/h ");
            gc.setFont(font); 

            x = getSize().x/2 - gc.textExtent(str.toString()).x / 2;
            y = sun.getBounds().height + 2;
            if(up) y -= 100;

            
            gc.drawText( str.toString(), x, y); 
    }
	
    public void paintControl(PaintEvent e) { 
      Metar m = w.getCurrentCondition();
      Set<Image> set = new HashSet<Image>();
      boolean up = true;
  
      	if(m != null) {
		if (m.getWeatherConditions() != null) {
			Iterator i = m.getWeatherConditions().iterator();
			while (i.hasNext()) 
				addWeather(set, (WeatherCondition)i.next());
		}
		if (m.getSkyConditions() != null) {
			Iterator i = m.getSkyConditions().iterator();
			while (i.hasNext()) 
				addSky(set, (SkyCondition)i.next());
		}  
		
		
	}
    
    	checkAndDraw(e.gc,set,sun);
    	checkAndDraw(e.gc,set,moon);   
 
    	checkAndDraw(e.gc,set,overcloud);      	
    	checkAndDraw(e.gc,set,cloud); 
  	
     	up = !checkAndDraw(e.gc,set,thunder) && up;    	  
    	up = !checkAndDraw(e.gc,set,fog) && up;

    	up = !checkAndDraw(e.gc,set,rain) && up;   
    	up = !checkAndDraw(e.gc,set,rainfall) && up;
    	
    	up = !checkAndDraw(e.gc,set,snow) && up;   
    	up = !checkAndDraw(e.gc,set,snowfall) && up;	
    	
    	if(m != null) drawTemp(e.gc,m,up);
    }

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		   checkWidget ();
		   int width = 0, height = 0, border = getBorderWidth ();
		   return new Point (sun.getBounds().width + border * 2, sun.getBounds().height + border * 2 + 30);
	}	

    

}
