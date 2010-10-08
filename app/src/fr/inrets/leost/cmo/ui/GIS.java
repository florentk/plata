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
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;

import com.roots.swtmap.MapWidget;
import com.roots.swtmap.MapWidgetOverlayImage;
import com.roots.swtmap.MapWidget.PointD;

import fr.inrets.leost.cmo.beaconning.BeaconRecv;
import fr.inrets.leost.cmo.beaconning.BeaconRecvFake;
import fr.inrets.leost.cmo.dashboard.*;
import fr.inrets.leost.cmo.management.CMOManagement;
import fr.inrets.leost.cmo.management.CMOTableEntry;
import fr.inrets.leost.cmo.management.CMOTableListener;

import fr.inrets.leost.cmo.beaconning.packet.*;

import fr.inrets.leost.geolocation.Geolocation;
import fr.inrets.leost.geolocation.GeolocationListener;
import fr.inrets.leost.geolocation.Gps;
import fr.inrets.leost.geolocation.WGS84;

/**
 * show a map with associated information from Geolocation and CMOMangement
 * @author florent kaisser
 * @has 1 - - Geolocation
 * @has 1 - - CMOManagement
 * @has 1 - - Dashboard
 * @has 1 - - AlertWidget
 * @has 1 - - MapWidget
 * @depend 1 - - StoppingDistance
 * @depend 1 - - BrakingDistance
 * @depend 1 - - ClosestCMO
 * @depend 1 - - Alert
 * @depend 2 - - MapWidgetOverlayImage
 * @depend - - - CMOTableEntry
 * 
 * @has 0..* - - MapWidgetOverlayCMO
 */
public class GIS extends Composite  implements DashboardListener, CMOTableListener, GeolocationListener  {
	
	private static final PointD home = new PointD(3.13252, 50.60689);
	private static int defaultZoom = 16;

	private Geolocation geo;
	private CMOManagement cmoMgt;
	private Dashboard dashboard;	
	private AlertWidget alert;
	
	private MapWidget map;
	private Table tableInfo;
	private Table tableCMO;	
	private Display display;
	private ExpandItem expandItemCMOTable;
	
	private boolean stop=false;

	private Map<String, MapWidgetOverlayCMO> neighborhood  =   new HashMap<String, MapWidgetOverlayCMO>();

	
	public Image loadCarImage()   {
		return new Image(display,  getClass().getResourceAsStream("resources/twingo.png"));
	}
	
	public Image loadMyCarImage()   {
		return new Image(display,  getClass().getResourceAsStream("resources/twingo_green.png"));
	}	
	
	public Image loadNeighborhoodCarImage()   {
		return new Image(display,  getClass().getResourceAsStream("resources/twingo_red.png"));
	}		
	
	public Image loadHomeImage()  {
		return new Image(display, getClass().getResourceAsStream("resources/home.png"));
	}	
	
	public Image loadSemaphoreGreen()  {
		return new Image(display, getClass().getResourceAsStream("resources/feux_vert.png"));
	}		
	
	public Image loadSemaphoreOrange()  {
		return new Image(display, getClass().getResourceAsStream("resources/feux_orange.png"));
	}		
	
	public Image loadSemaphoreRed()  {
		return new Image(display, getClass().getResourceAsStream("resources/feux_rouge.png"));
	}			
	
    //////////////////////////////
    // init the dashboard 
	private Dashboard initDashboard(Composite parent){

		Dashboard db = new Dashboard();
		
		//link the  dashboard with the geolocation system
		geo.addPositionListener(db);
		
		//link the  dashboard with the CMO Mangement		
		cmoMgt.addListener(db);
		
		//alow receive the dashboardUpdate
		db.addListener(this);

		//add indicator
		StoppingDistance sDistance = new StoppingDistance(geo);
		BrakingDistance bDistance = new BrakingDistance(geo);		
		ClosestCMO closestCMO = new ClosestCMO(geo, cmoMgt);
		Alert dbAlert=new Alert(geo, closestCMO, sDistance, bDistance);
		
		db.addIndicator(new Position(geo));
		db.addIndicator(new Speed(geo));
		db.addIndicator(new Track(geo));
		db.addIndicator(bDistance);
		db.addIndicator(sDistance);
		db.addIndicator(closestCMO);   
		db.addIndicator(dbAlert);     	
        
		alert = new AlertWidget(parent, SWT.NONE, dbAlert);
		alert.setImg(loadSemaphoreGreen(), 0);
		alert.setImg(loadSemaphoreOrange(), 1);
		alert.setImg(loadSemaphoreRed(), 2);
		
		
		
        return db;
	}
	
	//////////////////////////////
	// init the table info (indicators)
	private static Table initTableInfo(Composite parent, Dashboard db){
	

		Table table = new Table(parent, SWT.FULL_SELECTION  | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2 , 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setText("Name");
		column1.setWidth(160);
		TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setText("Value");
		column2.setWidth(160);

		for (Indicator id : db.getIndicators())
			new TableItem(table, SWT.NONE).setText(0, id.name());
		
		return table;
	}
	
	//////////////////////////////
	// init the cmo table 
	private static Table initTableCMO(Composite parent){

		Table table = new Table(parent, SWT.FULL_SELECTION  | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2 , 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText("ID");
		column.setWidth(100);
		
		column = new TableColumn(table, SWT.NONE);
		column.setText("Type");
		column.setWidth(70);
		
		column = new TableColumn(table, SWT.NONE);
		column.setText("Position");
		column.setWidth(150);	
	
		column = new TableColumn(table, SWT.NONE);
		column.setText("Speed");
		column.setWidth(50);	
		
		column = new TableColumn(table, SWT.NONE);
		column.setText("Track");
		column.setWidth(50);			
		
		return table;
	}	
	
	//tableCMO
	
    //////////////////////////////
    // init the map 
	void initMap(Composite parent){
		map = new MapWidget(parent, SWT.NONE, MapWidget.computePosition(home,defaultZoom),defaultZoom);

		MapWidgetOverlayCMO.setImg(loadNeighborhoodCarImage(),CMOHeader.CMO_TYPE_CAR);
		MapWidgetOverlayCMO.setImg(loadCarImage(),(short)-1);		
		MapWidgetOverlayCMO.setFont(new Font(display,"Arial",14,SWT.BOLD));	
		
		map.addOverlay( 
				new MapWidgetOverlayImage ( 0, 0, 
						MapWidgetOverlayImage.REFERENCE_CENTER_WIDGET ,
						loadMyCarImage()) );

		map.addOverlay( 
				new MapWidgetOverlayImage ( home.x, home.y, 
						MapWidgetOverlayImage.REFERENCE_WORLD,
						loadHomeImage()) );	
	}
	
	//associate a control to a expand bar
	private ExpandItem associateToExpandBar(ExpandBar bar, int id,Composite parent, String text, boolean expand){
    	ExpandItem item = new ExpandItem (bar, SWT.NONE, id);
    	item.setText(text);
    	item.setHeight(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
    	item.setControl(parent);
    	item.setExpanded(expand);
    	
    	return item;
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
        SashForm sashForm = new SashForm(this, SWT.HORIZONTAL);
        sashForm.setLayout(new FillLayout());
        
        //create the left expandbar
        ExpandBar bar = new ExpandBar (sashForm, SWT.V_SCROLL);
        

  
        //create the dashboard and the alert icon
        dashboard = initDashboard(bar);
        associateToExpandBar(bar, 0, alert,"Alert",true).setHeight(200);
        
        //create the information table
        tableInfo = initTableInfo(bar,dashboard);
    	associateToExpandBar(bar, 1, tableInfo,"Informations",false);
  
    	//create the CMO table
        tableCMO = initTableCMO(bar);
    	expandItemCMOTable = associateToExpandBar(bar, 2, tableCMO,"Neighborhood",false);
    	
    	//adapt the Expand item to size of table
    	tableCMO.addListener(SWT.MeasureItem, new Listener() {
    		public void handleEvent(Event event) {
    			Point size = tableCMO.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    			if (expandItemCMOTable.getHeight() != size.y) {
    				expandItemCMOTable.setHeight(size.y);
    			}
    		}
    	});
    	
    	
    	
        
        //create the map
        initMap(sashForm);  

        sashForm.setWeights(new int[] { 35, 65 });
        
	}
	
	public String[] getTextsTableItemCMO(CMOTableEntry entry){
		return new String[] {
				entry.getCmoID(),  
				CMOHeader.typeToString(entry.getCmoType()),
				new WGS84(entry.getLongitude(), entry.getLatitude(), entry.getAltitude()).toString(),
				String.format("%01.1f km/h", entry.getSpeed()) ,
				String.format("%01.0f°", entry.getTrack())};
	}

	public void updateTableCMO(){
		//create a hashtable for associate a primary key with the TableItems
		Map<String,TableItem> entryInTheWidgetTable = new HashMap<String,TableItem>(tableCMO.getItemCount());
		for (TableItem i : tableCMO.getItems())
			entryInTheWidgetTable.put( ((CMOTableEntry)i.getData()).getCmoID(), i);
		
		//add and update the table entry
		for (CMOTableEntry entry : cmoMgt.getTable()){
			TableItem i;
			
			if(entryInTheWidgetTable.containsKey(entry.getCmoID())){
				i = entryInTheWidgetTable.get(entry.getCmoID());
			}else{
				i = new TableItem(tableCMO, SWT.NONE);
			}
			
			i.setText(getTextsTableItemCMO(entry));
			i.setData(entry);			
		}
		
		
		//remove the table entry
		int pos=0;
		for (TableItem i : tableCMO.getItems()){
			String id = ((CMOTableEntry)i.getData()).getCmoID();
			//System.out.println("Check " + id + " entry " + (n++));
			if(! cmoMgt.cmoInTable(id)){
				tableCMO.remove(pos);
				pos--;
			}
				
			pos++;
		}
	}
	
	/**
	 * update the table and the map position
	 */
	public void dashboardUpdate(){
		if (display.isDisposed()) return;
		
		//see http://www.eclipse.org/swt/faq.php#uithread
		if(!stop)display.syncExec(
				new Runnable(){
					public void run(){
						
						if(stop) return;

						//update the table item
						if (tableInfo != null){
							int i=0;
							for (Indicator id : dashboard.getIndicators())
								tableInfo.getItem(i++).setText(1, id.toString());
							alert.redraw();
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
		
		if (display.isDisposed()) return;
		
		//see http://www.eclipse.org/swt/faq.php#uithread
		if(!stop)display.syncExec(
				new Runnable(){
					public void run(){
						if(stop) return;
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
		if(!stop)display.syncExec(new Runnable(){public void run(){if(stop) return;map.redraw();updateTableCMO();}});
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
		if(!stop)display.syncExec(new Runnable(){public void run(){if(stop) return;map.redraw();updateTableCMO();}});
	}

	/** 
	 * @see fr.inrets.leost.cmo.management.CMOTableListener#tableCMORemoved(java.lang.String, fr.inrets.leost.cmo.management.CMOTable)
	 */
	@Override
	public void tableCMORemoved(CMOTableEntry entry) {
		map.removeOverlay(neighborhood.get(entry.getCmoID()));
		neighborhood.remove(entry.getCmoID());
		
		//see http://www.eclipse.org/swt/faq.php#uithread
		if(!stop)display.syncExec(new Runnable(){public void run(){if(stop) return;map.redraw();updateTableCMO();}});
	}
	
	public void dispose(){
		stop = true;
		alert.dispose();
		
		map.dispose();
		tableInfo.dispose();
		tableCMO.dispose();
		display.dispose();
		expandItemCMOTable.dispose();
		
	}

	/**
	 * show the GIS window
	 * @param strDevice device name for intercept the neighborhood CMO beacon
	 * @throws IOException Gps reader problem
	 * @throws SecurityException illegal thread interrupt
	 */
	public static void startGIS(String strDevice) throws IOException,SecurityException{
		
		//create the parent window
		Display display = new Display ();
		Shell shell = new Shell(display);
		shell.setText("Global Information System");
		shell.setSize(1245, 700);
		shell.setLocation(30, 10);
		shell.setLayout (new FillLayout());


		//create the beacon receiver
		BeaconRecv recv = BeaconRecv.loopPacketFromDevice(strDevice);
		
		if(recv==null)System.exit(1);
		
		//BeaconRecv recv = BeaconRecv.loopPacketFromFile(strDevice);

		/*BeaconRecvFake recv = new BeaconRecvFake();

			recv.addFixedCMO(new CMOState(
					new CMOHeader((byte)100, 0, 5000, "CC",CMOHeader.CMO_TYPE_SPOT ),
					3.13061f,
					50.61789f,
					0.0f,
					1.0f,
					0.0f));

		recv.addFixedCMO(new CMOState(
					new CMOHeader((byte)100, 0, 5000, "AZ-197-UY",CMOHeader.CMO_TYPE_CAR ),
					3.12586784363f,
					50.6021995544f,
					0.0f,
					1.0f,
					0.0f));*/
		
		

		//create the GPS
		Geolocation gps = new Gps();
		
		//create the CMO management
		CMOManagement cmoMgt = new CMOManagement();

		//link the CMO Management with the beaconning receiver
		recv.addListener(cmoMgt);

		//create the GIS window
		GIS gis= new GIS(display, gps, cmoMgt, shell, SWT.NONE);

		//start the beaconning receiver
		recv.start();

		//start the GPS
		gps.start();	      

		//show the window
		shell.open ();

		//event loop
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}


		//clean
		gps.interrupt();
		recv.interrupt();
		
		gis.dispose();
		
		display.dispose ();	

	}
	
	public static void main (String [] args) throws Exception {

		if(args.length<1){
			System.out.println("Not enough arguments");
			System.out.println("Usage : java GIS <device>");			
			System.exit(1);
		}	

		startGIS(args[0]); 
		System.exit(0);
	}
}
