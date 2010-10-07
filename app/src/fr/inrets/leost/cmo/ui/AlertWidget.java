package fr.inrets.leost.cmo.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.roots.swtmap.MapWidget;

import fr.inrets.leost.cmo.dashboard.Alert;

public class AlertWidget extends Canvas {

	private Alert dbAlert;
	
	private  Map<Integer, Image> alertImg =  new HashMap<Integer, Image>();	
		
	
	public AlertWidget(Composite arg0, int arg1, Alert dbAlert) {
		super(arg0, arg1);
		
		this.dbAlert = dbAlert;
		
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
            	AlertWidget.this.paintControl(e);
            }
        });		
	}
	
	public void setImg(Image image, int alert) {
		alertImg.put(alert, image);
	}	
	
    private void paintControl(PaintEvent e) {
    	Image img= alertImg.get(dbAlert.getDecision());
    	e.gc.drawImage(img , getSize().x/2 - img.getBounds().width / 2,0);
    }	



}
