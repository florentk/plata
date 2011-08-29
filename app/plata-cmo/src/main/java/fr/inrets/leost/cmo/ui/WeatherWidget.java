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


/**
 * Widget for representing weather icon
 * @author florent kaisser
 */
public class WeatherWidget extends Canvas implements PaintListener {	
	private Image sun,cloud,fog,moon,overcloud,rain,rainfall,snow,snowfall,thunder;
	private Weather w;
	private Font font;
	private int decalage=0;
	
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
    public void checkAndDraw(GC gc, boolean show, Image img){
      if(show) {
            gc.drawImage(img , getSize().x/2 - sun.getBounds().width / 2,0);
      }
    }

	public void updateDecalage() {
    	if (w.validData()){
    		decalage = -100;
    	    if(	w.isThunderstorms() ||
            	w.isFog() ||
            	w.isRain() ||
            	w.isSnow()
            ) decalage += 50; 
            
            if(	w.isThunderstorms() ||
            	w.isFog() ||
            	w.isRainfall() ||
            	w.isSnowfall()
            ) decalage += 50;
        }	
	}
    
    public void drawTemp(GC gc) {
            int x,y;
    		StringBuffer str = new StringBuffer();
			str.append(w.getTemperature() + " °C ");		
			
			if (w.getWindSpeed() != 0) str.append(w.getWindSpeed() + " km/h ");
            gc.setFont(font); 

            x = getSize().x/2 - gc.textExtent(str.toString()).x / 2;
            y = sun.getBounds().height + 2 + decalage;
            
            gc.drawText( str.toString(), x, y); 
    }
	
    public void paintControl(PaintEvent e) { 
    	if (w.validData()){
			updateDecalage();
    	
			checkAndDraw(e.gc,w.isSun(),sun);
			checkAndDraw(e.gc,w.isMoon(),moon);   
	 
			checkAndDraw(e.gc,w.isOvercloud(),overcloud);      	
			checkAndDraw(e.gc,w.isCloud(),cloud); 
	  	
		 	checkAndDraw(e.gc,w.isThunderstorms(), thunder);    	  
			checkAndDraw(e.gc,w.isFog(), fog);

			checkAndDraw(e.gc,w.isRain(),rain);   
			checkAndDraw(e.gc,w.isRainfall(),rainfall);
			
			checkAndDraw(e.gc,w.isSnow(),snow);   
			checkAndDraw(e.gc,w.isSnowfall(),snowfall);	
			
			drawTemp(e.gc);
    	}
    }

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		  updateDecalage();
		   checkWidget ();
		   int width = 0, height = 0, border = getBorderWidth ();
		   return new Point (sun.getBounds().width + border * 2, sun.getBounds().height + border * 2 + 30 + decalage);
	}
}
