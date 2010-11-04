package fr.inrets.leost.cmo.ui;

import java.util.HashMap;

import org.eclipse.swt.graphics.Image;

public class CMOImg extends HashMap<Short, Image> {

	/**
	 * set a image representing a CMO
	 * @param image a image representing a CMO
	 */
	public void setImg(Image image, short cmoType) {
		put(cmoType, image);
	}	
	
	public Image getImg(short cmoType) {
		return get(cmoType);
	}
	
}
