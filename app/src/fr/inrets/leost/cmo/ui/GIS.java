package fr.inrets.leost.cmo.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.roots.swtmap.MapWidget;
import com.roots.swtmap.MapWidget.PointD;

import fr.inrets.leost.cmo.dashboard.*;
import fr.inrets.leost.geolocation.Gps;

public class GIS extends Composite  implements DashboardListener  {

	private SashForm sashForm;
	private MapWidget mapWidget;
	private Dashboard dashboard;
	private Table table;
	
	public GIS(Dashboard dashboard, Composite parent, int style){
		super(parent, style);
		
		this.dashboard = dashboard;

        setLayout(new FillLayout());
        
        sashForm = new SashForm(this, SWT.HORIZONTAL);
        sashForm.setLayout(new FillLayout());

        table = new Table(sashForm, SWT.FULL_SELECTION  | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        mapWidget = new MapWidget(sashForm, SWT.NONE, MapWidget.computePosition(new PointD(3.12780, 50.61164),16),16);
        
        sashForm.setWeights(new int[] { 75, 200 });

	}
	
	public static void startGIS(){
	      Display display = new Display ();
	      Shell shell = new Shell(display);
	      shell.setText("GPS Monitor");
	      shell.setSize(1245, 700);
	      shell.setLocation(30, 10);
	      shell.setLayout (new FillLayout());
	      
	      new GIS(null, shell, SWT.NONE);
	      
	      shell.open ();
	      while (!shell.isDisposed ()) {
	          if (!display.readAndDispatch ()) display.sleep ();
	      }
	      display.dispose ();	
	}
	
    public static void main (String [] args) throws Exception {

    	
    	startGIS();
      
    }	

}
