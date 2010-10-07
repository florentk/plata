package fr.inrets.leost.cmo.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Font;

import com.roots.swtmap.MapWidgetOverlay;

import fr.inrets.leost.cmo.management.CMOTableEntry;

/**
 * Draw a CMO neighborhood
 * @author florent
 *
 */
public final class MapWidgetOverlayCMO extends MapWidgetOverlay{
	private static final Map<Short, Image> cmoimg =  new HashMap<Short, Image>();

	
	private static Font ft = null; 

	/**
	 * set a image representing a CMO
	 * @param image a image representing a CMO
	 */
	public static void setImg(Image image, short cmoType) {
		cmoimg.put(cmoType, image);
	}
	
	
	
	/**
	 * set the text font for the CMO description
	 * @param font text font for the CMO description
	 */
	public static void setFont(Font font) {
		ft = font;
	}	

	public MapWidgetOverlayCMO(double dx, double dy, CMOTableEntry cmo) {
		super(dx, dy, MapWidgetOverlay.REFERENCE_WORLD, cmo);
	}

	/**
	 * @see com.roots.swtmap.MapWidget.MapWidgetOverlay#drawOverlay(org.eclipse.swt.graphics.GC, java.lang.Object, int, int)
	 */
	@Override
	public void drawOverlay(GC gc, Object overlay, int x, int y) {
		CMOTableEntry cmo = (CMOTableEntry)overlay;
		
		Image img = cmoimg.get(cmo.getCmoType());
		
		if(img != null)
			gc.drawImage(img, x -  img.getBounds().width / 2, y - img.getBounds().height);
		
		if(ft != null)
			gc.setFont(ft);
		
		//get the weight text display
		int tw = gc.stringExtent(cmo.getCmoID()).x;

		//draw the text in the center
		gc.drawString(cmo.getCmoID(),x - tw / 2,y,true); 
	}
}
