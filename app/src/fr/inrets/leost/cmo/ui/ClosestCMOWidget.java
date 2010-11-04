package fr.inrets.leost.cmo.ui;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;


import fr.inrets.leost.cmo.dashboard.ClosestCMO;
import fr.inrets.leost.cmo.management.CMOTableEntry;

public class ClosestCMOWidget extends Canvas implements PaintListener{
	private ClosestCMO closestCMO;
	
	private static Font ft = null; 
	

	

	public ClosestCMOWidget(Composite parent, int arg1, ClosestCMO closestCMO) {
		super(parent, arg1);
		this.closestCMO = closestCMO;
		
		FillLayout l = new FillLayout();
		l.type = SWT.VERTICAL;
		setLayout(l);

		//marge = l.marginHeight;
		
		addPaintListener(this);
	}

	@Override
	public void paintControl(PaintEvent p) {
		CMOTableEntry cmo = closestCMO.getClosestCMO();
		
		if (cmo != null){
			//get the weight text display
			Point tsId = p.gc.stringExtent(cmo.getCmoID());
			String sDist = String.format("%01.1f m", closestCMO.getDistance() );
			Point tsDist = p.gc.stringExtent(sDist);			
			
			int marge = (p.height - tsDist.y - tsId.y) / 3;
			
			p.gc.drawString(cmo.getCmoID(), (p.width - tsId.x)/2 ,marge,true); 
			p.gc.drawString(sDist, (p.width - tsDist.x)/2 ,2*marge + tsId.y,true); 		
		}
	}

	

	
}
