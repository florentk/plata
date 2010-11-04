package fr.inrets.leost.cmo.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import fr.inrets.leost.cmo.dashboard.ClosestCMO;

/**
 * Widget for representing a Alert indicator
 * @author florent kaisser
 * @had 1 - - ClosestCMO
 */
public class AlertWidget extends Canvas implements PaintListener {
	private ClosestCMO closestCMO;
	
	private  Map<Integer, Image> alertImg =  new HashMap<Integer, Image>();	
	
	public AlertWidget(Composite arg0, int arg1, ClosestCMO closestCMO) {
		super(arg0, arg1);
		
		this.closestCMO = closestCMO;
		
        addPaintListener(this);		
	}
	
	/**
	 * set a image associate to a level alert
	 * @param image
	 * @param alert
	 */
	public void setImg(Image image, int alert) {
		alertImg.put(alert, image);
	}	
	
    public void paintControl(PaintEvent e) {
    	Image img= alertImg.get(closestCMO.getDecision());
    	e.gc.drawImage(img , getSize().x/2 - img.getBounds().width / 2,0);
    }

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		
		   checkWidget ();
		   int width = 0, height = 0, border = getBorderWidth ();

		   for (Image img : alertImg.values()){
			   if (width < img.getBounds().width)
				   width = img.getBounds().width;
			   if (height < img.getBounds().height)
				   height = img.getBounds().height;			   
		   }
		   
		   width += border * 2; 
		   height += border * 2;
		   return new Point (width, height);
	}	

    

}
