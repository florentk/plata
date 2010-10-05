package fr.inrets.leost.cmo.ui;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Font;

import com.roots.swtmap.MapWidgetOverlay;

import fr.inrets.leost.cmo.management.CMOTableEntry;

public final class MapWidgetOverlayCMO extends MapWidgetOverlay{

	private static Image img = null;
	private static Font ft = null; 

	public static void setImg(Image image) {
		img = image;
	}
	
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
		
		if(img != null)
			gc.drawImage(img, x -  img.getBounds().width / 2, y - img.getBounds().height);
		
		if(ft != null)
			gc.setFont(ft);
		
		int tw = gc.stringExtent(cmo.getCmoID()).x;

		gc.drawString(cmo.getCmoID(),x - tw / 2,y,true); 
	}
}
