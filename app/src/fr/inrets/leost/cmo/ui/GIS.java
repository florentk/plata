package fr.inrets.leost.cmo.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.roots.swtmap.MapWidget;
import com.roots.swtmap.MapWidgetOverlayImage;
import com.roots.swtmap.MapWidget.PointD;

import fr.inrets.leost.cmo.beaconning.BeaconRecv;
import fr.inrets.leost.cmo.beaconning.BeaconRecvFake;
import fr.inrets.leost.cmo.dashboard.*;
import fr.inrets.leost.cmo.management.CMOManagement;
import fr.inrets.leost.cmo.management.CMOTable;
import fr.inrets.leost.cmo.management.CMOTableEntry;
import fr.inrets.leost.cmo.management.CMOTableListener;

import fr.inrets.leost.cmo.beaconning.packet.*;

import fr.inrets.leost.geolocation.Geolocation;
import fr.inrets.leost.geolocation.GeolocationListener;
import fr.inrets.leost.geolocation.Gps;
import fr.inrets.leost.geolocation.WGS84;

public class GIS extends Composite  implements DashboardListener, CMOTableListener, GeolocationListener  {
	
	private static final PointD home = new PointD(3.13252, 50.60689);
	private static int defaultZoom = 16;
	
	private SashForm sashForm;
	private MapWidget map;
	private Dashboard dashboard;
	private Table table;
	private Display display;
	private Geolocation geo;
	private CMOManagement cmoMgt;

	private Map<String, MapWidgetOverlayCMO> neighborhood  =   new HashMap<String, MapWidgetOverlayCMO>();

	
	
	public Image loadCarImage()   {
		return new Image(display,  getClass().getResourceAsStream("resources/twingo.png"));
	}
	
	public Image loadHomeImage()  {
		return new Image(display, getClass().getResourceAsStream("resources/home.png"));
	}		
	
    //////////////////////////////
    // init the dashboard 
	private void initDashboard(){

		dashboard = new Dashboard();
		
		//link the  dashboard with the geolocation system
		geo.addPositionListener(dashboard);
		
		//link the  dashboard with the CMO Mangement		
		cmoMgt.addListener(dashboard);
		
		//alow receive the dashboardUpdate
		dashboard.addListener(this);
		
		//add indicator
		StoppingDistance sDistance = new StoppingDistance(geo);
		BrakingDistance bDistance = new BrakingDistance(geo);		
		ClosestCMO closestCMO = new ClosestCMO(geo, cmoMgt);
        dashboard.addIndicator(new Position(geo));
        dashboard.addIndicator(new Speed(geo));
        dashboard.addIndicator(new Track(geo));
        dashboard.addIndicator(bDistance);
        dashboard.addIndicator(sDistance);
        dashboard.addIndicator(closestCMO);   
        dashboard.addIndicator(new Hazard(geo, closestCMO, sDistance, bDistance));     	
	}
	
	//////////////////////////////
	// init the table 
	void initTable(){
		
		initDashboard();

		table = new Table(sashForm, SWT.FULL_SELECTION  | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2 , 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setText("Name");
		column1.setWidth(160);
		TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setText("Value");
		column2.setWidth(160);

		for (Indicator id : dashboard.getIndicators())
			new TableItem(table, SWT.NONE).setText(0, id.name());
	}
	
    //////////////////////////////
    // init the map 
	void initMap(){
		map = new MapWidget(sashForm, SWT.NONE, MapWidget.computePosition(home,defaultZoom),defaultZoom);

		MapWidgetOverlayCMO.setImg(loadCarImage());
		MapWidgetOverlayCMO.setFont(new Font(display,"Arial",14,SWT.BOLD));	
		
		map.addOverlay( 
				new MapWidgetOverlayImage ( 0, 0, 
						MapWidgetOverlayImage.REFERENCE_CENTER_WIDGET ,
						loadCarImage()) );

		map.addOverlay( 
				new MapWidgetOverlayImage ( home.x, home.y, 
						MapWidgetOverlayImage.REFERENCE_WORLD,
						loadHomeImage()) );	
	}
	
	public GIS(Display display, Geolocation geo, CMOManagement cmoMgt, Composite parent, int style){
		super(parent, style);
		
		this.display = display;
		this.geo = geo;
		this.cmoMgt = cmoMgt;
		
		geo.addPositionListener(this);
		cmoMgt.addListener(this);
		
        setLayout(new FillLayout());
        
        //create a Sash
        sashForm = new SashForm(this, SWT.HORIZONTAL);
        sashForm.setLayout(new FillLayout());
  
        initTable();
        initMap();  

        sashForm.setWeights(new int[] { 30, 70 });
        
	}
	
	/**
	 * update the table and the map position
	 */
	public void dashboardUpdate(){
		//see http://www.eclipse.org/swt/faq.php#uithread
		display.syncExec(
				new Runnable(){
					public void run(){

						//update the table item
						if (table != null){
							int i=0;
							for (Indicator id : dashboard.getIndicators())
								table.getItem(i++).setText(1, id.toString());
						}
					}
				}  
		);
	}	
	



	/** 
	 * @see fr.inrets.leost.geolocation.GeolocationListener#positionChanged(fr.inrets.leost.geolocation.WGS84, java.lang.Double, java.lang.Double)
	 */
	@Override
	public void positionChanged(WGS84 position, Double speed, Double track) {
		//see http://www.eclipse.org/swt/faq.php#uithread
		display.syncExec(
				new Runnable(){
					public void run(){
						//compute the current position in pixel
						Point currentPos = new Point(
								MapWidget.lon2position(geo.getCurrentPos().longitude(), map.getZoom()), 
								MapWidget.lat2position(geo.getCurrentPos().latitude(), map.getZoom())
						);

						//center the map on current position
						map.setCenterPosition( currentPos );
						map.redraw();	
					}
				}

		);
	}

	/** 
	 * @see fr.inrets.leost.cmo.management.CMOTableListener#tableChanged(java.lang.String, fr.inrets.leost.cmo.management.CMOTable)
	 */
	@Override
	public void tableChanged(CMOTableEntry entry) {
		MapWidgetOverlayCMO over = neighborhood.get(entry.getCmoID());
		
		if(over==null){
			tableCMOAdded(entry);
			return;
		}
		
		over.setDx(entry.getLongitude());
		over.setDy(entry.getLatitude());
		
		//see http://www.eclipse.org/swt/faq.php#uithread
		display.syncExec(new Runnable(){public void run(){map.redraw();}});
	}

	/**
	 * @see fr.inrets.leost.cmo.management.CMOTableListener#tableCMOAdded(java.lang.String, fr.inrets.leost.cmo.management.CMOTable)
	 */
	@Override
	public void tableCMOAdded(CMOTableEntry entry) {

		MapWidgetOverlayCMO over =  new MapWidgetOverlayCMO(entry.getLongitude(), entry.getLatitude(), entry);
		map.addOverlay(over);
		neighborhood.put(entry.getCmoID(),over);
		
		//see http://www.eclipse.org/swt/faq.php#uithread
		display.syncExec(new Runnable(){public void run(){map.redraw();}});
	}

	/** 
	 * @see fr.inrets.leost.cmo.management.CMOTableListener#tableCMORemoved(java.lang.String, fr.inrets.leost.cmo.management.CMOTable)
	 */
	@Override
	public void tableCMORemoved(CMOTableEntry entry) {
		map.removeOverlay(neighborhood.get(entry.getCmoID()));
		neighborhood.remove(entry.getCmoID());
		
		//see http://www.eclipse.org/swt/faq.php#uithread
		display.syncExec(new Runnable(){public void run(){map.redraw();}});
	}

	public static void startGIS(String strDevice) throws IOException,SecurityException{
	      Display display = new Display ();
	      Shell shell = new Shell(display);
	      shell.setText("GPS Monitor");
	      shell.setSize(1245, 700);
	      shell.setLocation(30, 10);
	      shell.setLayout (new FillLayout());
	      

	      
			BeaconRecv recv = BeaconRecv.loopPacketFromDevice(strDevice);
			
	      /*BeaconRecvFake recv = new BeaconRecvFake();
	      
			recv.addFixedCMO(new CMOState(
					new CMOHeader((byte)100, 0, 5000, "CC",CMOHeader.CMO_TYPE_SPOT ),
					3.13061f,
					50.61789f,
					0.0f,
					1.0f,
					0.0f));*/

			/*recv.addFixedCMO(new CMOState(
					new CMOHeader((byte)100, 0, 5000, "AZ-197-UY",CMOHeader.CMO_TYPE_CAR ),
					3.12586784363f,
					50.6021995544f,
					0.0f,
					1.0f,
					0.0f));*/
					
	        Geolocation gps = new Gps();
			CMOManagement cmoMgt = new CMOManagement();
			
			//link the CMO Management with the beaconning receiver
			recv.addListener(cmoMgt);
			


	      
	      new GIS(display, gps, cmoMgt, shell, SWT.NONE);
	      
		  //start the beaconning receiver
		  recv.start();
	      gps.start();	      
	      
	      shell.open ();
	      while (!shell.isDisposed ()) {
	          if (!display.readAndDispatch ()) display.sleep ();
	      }
	      
	      gps.interrupt();
	      recv.interrupt();
	      
	      display.dispose ();	
	      

	}
	
    public static void main (String [] args) throws Exception {startGIS(args[0]);}	
}
