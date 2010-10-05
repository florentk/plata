package com.roots.swtmap;

import org.eclipse.swt.graphics.GC;

/**
 * abstract class for add a overlay on the map
 * The  drawOverlay method is call for paint 
 * the overlay.
 * @author florent
 *
 */
public abstract class MapWidgetOverlay{
	
	public final static int REFERENCE_CENTER_WIDGET = 0;
	public final static int REFERENCE_WIDGET = 1;    
	public final static int REFERENCE_WORLD = 2;        	
	
	private double dx, dy;
	private int reference;
	private Object overlay;
	
	
	abstract public void drawOverlay(GC gc, Object overlay, int x, int y);

	/**
	 * 
	 * @param dx x position use reference point
	 * @param dy y position use reference point
	 * @param reference reference point
	 * @param overlay a use object data
	 */
	public MapWidgetOverlay(double dx, double dy, int reference, Object overlay) {
		super();
		this.dx = dx;
		this.dy = dy;
		this.reference = reference;
		this.overlay = overlay;
	}


	/**
	 * @param dx the dx to set
	 */
	public void setDx(double dx) {
		this.dx = dx;
	}

	/**
	 * @param dy the dy to set
	 */
	public void setDy(double dy) {
		this.dy = dy;
	}

	/**
	 * @return the dx
	 */
	public double getDx() {
		return dx;
	}


	/**
	 * @return the dy
	 */
	public double getDy() {
		return dy;
	}


	/**
	 * @return the reference
	 */
	public int getReference() {
		return reference;
	}


	/**
	 * @return the overlay
	 */
	public Object getOverlay() {
		return overlay;
	}
	
	
}
