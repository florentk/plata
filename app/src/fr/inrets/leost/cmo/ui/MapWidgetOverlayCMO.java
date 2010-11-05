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
 * @depend - - - CMOTableEntry
 * @depend - - - CMOImg
 */
public final class MapWidgetOverlayCMO extends MapWidgetOverlay{
	private static CMOImg cmoimg;
	private static Font ft = null; 

	/**
	 * set the text font for the CMO description
	 * @param font text font for the CMO description
	 */
	public static void setFont(Font font) {
		ft = font;
	}	
	
	public static void setCMOImg( CMOImg images) {
		cmoimg = images;
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
		
		Image img = cmoimg.getImg(cmo.getCmoType());
		
		if(img != null)
			gc.drawImage(img, Math.max(x -  img.getBounds().width / 2,0), Math.max(y - img.getBounds().height,0));
		
		if(ft != null)
			gc.setFont(ft);
		
		//get the weight text display
		int tw = gc.stringExtent(cmo.getCmoID()).x;
		
		//draw the text in the center
		gc.drawString(cmo.getCmoID(),Math.max(x - tw / 2, 0),y,true); 
	}
}
