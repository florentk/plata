package com.roots.swtmap;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

public final class MapWidgetOverlayImage extends MapWidgetOverlay{

	private Image img;

	public Image getImg() {
		return img;
	}

	public MapWidgetOverlayImage(double dx, double dy, int reference,Image img) {
		super(dx, dy, reference, img);
		this.img = img;
	}

	/**(non-Javadoc)
	 * @see com.roots.swtmap.MapWidget.MapWidgetOverlay#drawOverlay(org.eclipse.swt.graphics.GC, java.lang.Object, int, int)
	 */
	@Override
	public void drawOverlay(GC gc, Object overlay, int x, int y) {
		Image img = (Image)overlay;
		gc.drawImage(img, x -  img.getBounds().width / 2, y - img.getBounds().height / 2);
	}
	
	
}