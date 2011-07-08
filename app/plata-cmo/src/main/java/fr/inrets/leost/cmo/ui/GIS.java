package fr.inrets.leost.cmo.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.net.InetAddress;
import java.net.UnknownHostException;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;


import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Button;
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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

import com.roots.swtmap.MapWidget;
import com.roots.swtmap.MapWidgetOverlayImage;
import com.roots.swtmap.MapWidget.PointD;
import com.roots.swtmap.MapWidget.TileServer;

import fr.inrets.leost.cmo.beaconning.BeaconForward;
import fr.inrets.leost.cmo.beaconning.BeaconGenerator;
import fr.inrets.leost.cmo.beaconning.BeaconRecv;
import fr.inrets.leost.cmo.beaconning.BeaconRecvEthernet;
import fr.inrets.leost.cmo.beaconning.BeaconRecvFake;
import fr.inrets.leost.cmo.beaconning.BeaconRecvListener;
import fr.inrets.leost.cmo.dashboard.*;
import fr.inrets.leost.cmo.management.CMOManagement;
import fr.inrets.leost.cmo.management.CMOTableEntry;
import fr.inrets.leost.cmo.management.CMOTableListener;
import fr.inrets.leost.cmo.utils.PcapsTool;
import fr.inrets.leost.cmo.beaconning.packet.*;

import fr.inrets.leost.geolocation.Fixe;
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
 * @has 1 - - CMOImg
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
	
	/** init the logger */
	private static Logger logger = Logger.getLogger(GIS.class);		

	private Geolocation geo;
	private CMOManagement cmoMgt;
	private Dashboard dashboard;	
	private AlertWidget alert;
	private ClosestCMOWidget wClosestCMO;
	
	private MapWidget map;
	private Table tableInfo;
	private Table tableCMO;	
	private Display display;
	private ExpandItem expandItemCMOTable;
	private MapWidgetOverlayImage myCar;
	
	private static final CMOImg cmoImg = new CMOImg();
	
	private boolean syncOnExternalEvent = true;
	private boolean mapCenter = true;	
	private boolean extrapolePosition = true;		
	private Timer timerUpdate = new Timer();
	private int updateInterval = 100;
	private Spinner wUpdateInterval;
	
	private boolean stop=false;
	
	
	
	
///////////////////////////////////////////////////////////////////
// Windows builder
	
	private Map<String, MapWidgetOverlayCMO> neighborhood  =   new HashMap<String, MapWidgetOverlayCMO>();


	private Image loadCarImage()   {
		return new Image(display,  getClass().getResourceAsStream("resources/twingo.png"));
	}
	private Image loadMyCarImage()   {
		return new Image(display,  getClass().getResourceAsStream("resources/twingo_green.png"));
	}	
	private Image loadNeighborhoodCarImage()   {
		return new Image(display,  getClass().getResourceAsStream("resources/twingo_red.png"));
	}		
	private Image loadHomeImage()  {
		return new Image(display, getClass().getResourceAsStream("resources/home.png"));
	}	
	private Image loadSemaphoreGreen()  {
		return new Image(display, getClass().getResourceAsStream("resources/feux_vert.png"));
	}		
	private Image loadSemaphoreOrange()  {
		return new Image(display, getClass().getResourceAsStream("resources/feux_orange.png"));
	}	
	private Image loadSemaphoreRed()  {
		return new Image(display, getClass().getResourceAsStream("resources/feux_rouge.png"));
	}	
	private Image loadBikeImage()  {
		return new Image(display, getClass().getResourceAsStream("resources/bike.png"));
	}	
	private Image loadBusImage()  {
		return new Image(display, getClass().getResourceAsStream("resources/bus.png"));
	}	
	private Image loadMotorbikeImage()  {
		return new Image(display, getClass().getResourceAsStream("resources/motorbike.png"));
	}	
	private Image loadSpotImage()  {
		return new Image(display, getClass().getResourceAsStream("resources/ap.png"));
	}	
	private Image loadTruckImage()  {
		return new Image(display, getClass().getResourceAsStream("resources/truck.png"));
	}	
	private Image loadWalkerImage()  {
		return new Image(display, getClass().getResourceAsStream("resources/walker.png"));
	}	
	
	
	
	
    //////////////////////////////
    // init the options panel 
	private Composite initOptions(Composite parent){
		Composite c= new Composite(parent, SWT.NONE);
		
		c.setLayout (new FillLayout(SWT.VERTICAL));
		
		Button b = new Button(c, SWT.CHECK); 
		b.setText("Sync on external event");
		b.setSelection(syncOnExternalEvent);
		b.addListener(SWT.Selection, new Listener() {
		    public void handleEvent(Event event) {
		    	syncOnExternalEvent = ((Button)event.widget).getSelection();
		    	
		}});
		
		
		Composite cUpdate = new Composite(c, SWT.NONE);
		RowLayout rl = new RowLayout(SWT.HORIZONTAL);
		rl.marginLeft = 0;
		rl.center = true;
		cUpdate.setLayout (rl);
		
		b = new Button(cUpdate, SWT.CHECK); 
		b.setText("Refresh update interval : ");
		b.setSelection(timerUpdate != null);
		b.addListener(SWT.Selection, new Listener() {
		    public void handleEvent(Event event) {
		    	boolean enable = ((Button)event.widget).getSelection();
		    	enableTimer (enable);
		    	wUpdateInterval.setEnabled(enable);
		}});		
		
		wUpdateInterval = new Spinner (cUpdate, SWT.BORDER);
		wUpdateInterval.setMinimum(10);
		wUpdateInterval.setMaximum(10000);
		wUpdateInterval.setSelection(100);
		wUpdateInterval.setIncrement(10);
		wUpdateInterval.setPageIncrement(100);
		wUpdateInterval.pack();
		wUpdateInterval.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {	
				updateInterval = Integer.parseInt(
						wUpdateInterval.getText());
				enableTimer(true);
			}
		});
		

		b = new Button(c, SWT.CHECK); 
		b.setText("Extrapolate current position");
		b.setSelection(extrapolePosition);
		b.addListener(SWT.Selection, new Listener() {
		    public void handleEvent(Event event) {
		    	extrapolePosition =  ((Button)event.widget).getSelection();
		}});
		
		
		b = new Button(c, SWT.CHECK); 
		b.setText("Center the map on current position");
		b.setSelection(mapCenter);
		b.addListener(SWT.Selection, new Listener() {
		    public void handleEvent(Event event) {
				mapCenter =  ((Button)event.widget).getSelection();
		}});
		


		

		return c;
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
	
	
    //////////////////////////////
    // init the map 
	private void initMap(Composite parent){
		
		//map = new MapWidget(parent, SWT.NONE, MapWidget.computePosition(home,defaultZoom),defaultZoom);
		map = new MapWidget(parent, SWT.NONE, MapWidget.computePosition(new PointD(3.12780, 50.61164),16),16);

		cmoImg.setImg(loadNeighborhoodCarImage(),CMOHeader.CMO_TYPE_CAR);
		cmoImg.setImg(loadBikeImage(),CMOHeader.CMO_TYPE_BIKE);		
		cmoImg.setImg(loadBusImage(),CMOHeader.CMO_TYPE_BUS);
		cmoImg.setImg(loadMotorbikeImage(),CMOHeader.CMO_TYPE_MOTORBIKE);
		cmoImg.setImg(loadSpotImage(),CMOHeader.CMO_TYPE_SPOT);
		cmoImg.setImg(loadTruckImage(),CMOHeader.CMO_TYPE_TRUCK);
		cmoImg.setImg(loadWalkerImage(),CMOHeader.CMO_TYPE_WALKER);
		cmoImg.setImg(loadCarImage(),(short)-1);	
		
		MapWidgetOverlayCMO.setCMOImg(cmoImg);
		MapWidgetOverlayCMO.setFont(new Font(display,"Arial",14,SWT.BOLD));	
		
		map.addOverlay( 
				myCar = new MapWidgetOverlayImage ( 0, 0, 
						MapWidgetOverlayImage.REFERENCE_WORLD ,
						loadMyCarImage()) );

		map.addOverlay( 
				new MapWidgetOverlayImage ( home.x, home.y, 
						MapWidgetOverlayImage.REFERENCE_WORLD,
						loadHomeImage()) );	
		
		map.setTileServer(new TileServer("file://" + "/var/cache/osm/tiles/", 18));
		
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
	
	private void enableTimer(boolean enable){
		
		if(timerUpdate != null){
			timerUpdate.cancel();
			timerUpdate = null;
		}
		
		if(enable){
			timerUpdate = new Timer();
			timerUpdate.schedule(
	        		new TimerTask(){
	
						public void run() {
							updateAsyncAll();
						}
	        		}
	        ,0, updateInterval);
		}
	}		
	
	public GIS(Display display, Geolocation geo, CMOManagement cmoMgt, Dashboard db, ClosestCMO closestCMO, Composite parent, int style){
		super(parent, style);
		
		this.display = display;
		this.geo = geo;
		this.cmoMgt = cmoMgt;
        this.dashboard = db;
		
		geo.addPositionListener(this);
		cmoMgt.addListener(this);
		
        setLayout(new FillLayout());
        
        //create a Sash
        SashForm sashForm = new SashForm(this, SWT.HORIZONTAL);
        sashForm.setLayout(new FillLayout());
        
        //create the left expandbar
        ExpandBar bar = new ExpandBar (sashForm, SWT.V_SCROLL);
        
        
        //create alert information
        Composite alertInfo = new Composite(bar, SWT.NONE);
        
        GridLayout l = new GridLayout();   
        l.numColumns = 2;
        l.marginRight = 20;
        alertInfo.setLayout(l);    

		//create closestCMO textuel informations
        wClosestCMO = new ClosestCMOWidget(alertInfo, SWT.PUSH, closestCMO);
        wClosestCMO.setFont(new Font(display,"Arial",32,SWT.BOLD));  
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        wClosestCMO.setLayoutData(gridData);      


  
        
        //create the alert icon
		alert = new AlertWidget(alertInfo, SWT.PUSH, closestCMO);
		alert.setImg(loadSemaphoreGreen(), 0);
		alert.setImg(loadSemaphoreOrange(), 1);
		alert.setImg(loadSemaphoreRed(), 2);


        associateToExpandBar(bar, 0, alertInfo,"Alert",true).setHeight(210);
        
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
    	
    	//create the options panel
    	associateToExpandBar(bar, 3, initOptions(bar),"Options",false);
    	
        //create the map
        initMap(sashForm);  

        sashForm.setWeights(new int[] { 35, 65 });
        
        //init the timer
        if(timerUpdate != null)
        	enableTimer(true);
  
	}
	
	//clean
	public void dispose(){
		stop = true;
		alert.dispose();
		map.dispose();
		tableInfo.dispose();
		tableCMO.dispose();
		display.dispose();
		expandItemCMOTable.dispose();
	}	
//
// end of window builder
///////////////////////////////////////////////////
	
	
	
	
//////////////////////////////////////////////////
// windows update
	private String[] getTextsTableItemCMO(CMOTableEntry entry){
		return new String[] {
				entry.getCmoID(),  
				CMOHeader.typeToString(entry.getCmoType()),
				new WGS84(entry.getLongitude(), entry.getLatitude(), entry.getAltitude()).toString(),
				String.format("%01.1f km/h", entry.getSpeed() * 3.6) ,
				String.format("%01.0f°", entry.getTrack())};
	}
	
	private void updateTableCMO(){
		//create a hashmap for associate a primary key with the TableItems
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
	
	private void updateTableInfo(){
		int i=0;
		for (Indicator id : dashboard.getIndicators())
			tableInfo.getItem(i++).setText(1, id.toString());
	}
	
	private void updateNeighborhood(){
		for (CMOTableEntry entry : cmoMgt.getTable()){
			MapWidgetOverlayCMO over = neighborhood.get(entry.getCmoID());
			if(over!=null){
				over.setDx(entry.getLongitude());
				over.setDy(entry.getLatitude());
			}
		}
	}
	
	private void updateAll(){
		if(stop) return;
		
		if(geo.isReady()){
		
			//discret position
			WGS84 discretPos = geo.getLastPos();
			
			//extrapolate continue position
			WGS84 currentPos = geo.getCurrentPos();
			
			
			//last pos in pixel
			Point lastPos;
	
			if(extrapolePosition){
				//use the extrapolate position
				myCar.setDx(currentPos.longitude());
				myCar.setDy(currentPos.latitude());	
				
				//compute the current position in pixel
			   /* lastPos = new Point(
						MapWidget.lon2position(currentPos.longitude(), map.getZoom()), 
						MapWidget.lat2position(currentPos.latitude(), map.getZoom())
				);*/
			}else{
				myCar.setDx(discretPos.longitude());
				myCar.setDy(discretPos.latitude());
				
				//compute the current position in pixel
	
			}
			
			lastPos = new Point(
					MapWidget.lon2position(discretPos.longitude(), map.getZoom()), 
					MapWidget.lat2position(discretPos.latitude(), map.getZoom())
			);
			
			if(mapCenter)
				map.setCenterPosition( lastPos );
		
		}
		
		updateTableCMO();
		updateTableInfo();
		updateNeighborhood();
		map.redraw();
		alert.redraw();
		wClosestCMO.redraw();
	}
	
	class UpdateAll implements Runnable{

		@Override
		public void run() {
			updateAll();
		}
	}
	
	private void updateAsyncAll(){
		if(!stop && !display.isDisposed())
			//see http://www.eclipse.org/swt/faq.php#uithread
			display.syncExec(new UpdateAll());
	}
	
	private void updateOnExternalEvent(){
		if(syncOnExternalEvent)
			updateAsyncAll();
	}
// end of window update
//////////////////////////////////////////////////
	
	
//////////////////////////////////////////////////
// process events
//   connect event on window update method

	/**
	 * update the table and the map position
	 */
	public void dashboardUpdate(){
		if (display.isDisposed()) return;
		
		//update the table item
		if (tableInfo != null){
			updateOnExternalEvent();
		}
	}	
	
	/** 
	 * @see fr.inrets.leost.geolocation.GeolocationListener#positionChanged(fr.inrets.leost.geolocation.WGS84, java.lang.Double, java.lang.Double)
	 */
	@Override
	public void positionChanged(WGS84 position, Double speed, Double track) {
		updateOnExternalEvent();
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
		
		
		updateOnExternalEvent();
	}

	/**
	 * @see fr.inrets.leost.cmo.management.CMOTableListener#tableCMOAdded(java.lang.String, fr.inrets.leost.cmo.management.CMOTable)
	 */
	@Override
	public void tableCMOAdded(CMOTableEntry entry) {

		MapWidgetOverlayCMO over =  new MapWidgetOverlayCMO(entry.getLongitude(), entry.getLatitude(), entry);
		
		
		synchronized (neighborhood) {
			map.addOverlay(over);
			neighborhood.put(entry.getCmoID(),over);			
		}

		
		updateOnExternalEvent();
	}

	/** 
	 * @see fr.inrets.leost.cmo.management.CMOTableListener#tableCMORemoved(java.lang.String, fr.inrets.leost.cmo.management.CMOTable)
	 */
	@Override
	public void tableCMORemoved(CMOTableEntry entry) {
		
		synchronized (neighborhood) {
			map.removeOverlay(neighborhood.get(entry.getCmoID()));
			neighborhood.remove(entry.getCmoID());
		}
		
		updateOnExternalEvent();
	}

// end of process event
//////////////////////////////////////////////////

	

	
	
	
	
	
	
	
	
/////////////////////////////////////////////////////////////////////////
// entry point and associate statics methods	

	//record for Gis option
	public static final class GisOptions{
		public String strInterface = "fake"; //network interface  (default : lo)
		
		public boolean gen = true; //run beacon generator generator
		public boolean fwd = true; //run beacon forwarder

		public String cmoId; //CMO id (default : hostname)
		public short cmoType = CMOHeader.CMO_TYPE_CAR; //type of CMO
		public int beaconInterval = 500; //send interval of beacon in ms = t
		public WGS84 fixedPosition = null; //fixed position instead GPS
		
		public boolean gui = true; //start a gui (default : true)
		public boolean verbose = false; //verbose mode 
		
		public GisOptions(){
			try{
				cmoId = InetAddress.getLocalHost().getHostName();
			}catch (UnknownHostException e){
				cmoId = "N/A";
			}
		}
		
		/**
		 * determine forwarder and generator according to cmo type
		 */
		public void setAutoFwdGen(){

			switch(cmoType){
				case CMOHeader.CMO_TYPE_CAR:
				case CMOHeader.CMO_TYPE_TRUCK:
				case CMOHeader.CMO_TYPE_BUS:
				case CMOHeader.CMO_TYPE_MOTORBIKE:
					gen = true; fwd = true; break;
					
				case CMOHeader.CMO_TYPE_WALKER :
				case CMOHeader.CMO_TYPE_BIKE :
					gen = true; fwd = false; break;
					
				case CMOHeader.CMO_TYPE_SPOT :
					gen = true; fwd = true; break;

			}

		}

		@Override
		public String toString() {
			return "GisOptions [beaconInterval=" + beaconInterval + ", cmoId="
					+ cmoId + ", cmoType=" + cmoType + ", fwd=" + fwd
					+ ", gen=" + gen + ", gui=" + gui + ", strInterface="
					+ strInterface + "]";
		}
		

		
	}
	
	/**
	 * parse the arguments command line
	 * @param args
	 * @return the options
	 */
	public static GisOptions parseArgs(String[] args){
		GisOptions opt = new GisOptions();
		
		Options options = new Options();
		options.addOption("i","interface", true, "network interface");
		options.addOption("g","generator", false, "run beacon generator (with manual option)");
		options.addOption("f", "forwarder", false, "run beacon forwarder (with manual option)");
		options.addOption("m","manual", false, "don't determine generator and forwarder with CMO type");
		options.addOption("n", "id", true, "cmo id");
		options.addOption("t", "type", true, "cmo type");
		options.addOption("b", "beacon-inter", true, "interval between beacon sending (millisecond)");
		options.addOption("d", "daemon", false, "run without GUI");
		options.addOption("p", "fixed-position", true, "fixed position instead GPS. The arg is form 50°37'57.41\"N 3°5'8.26\"E");		
		options.addOption("v", "verbose", false, "write the dashboard in std out");		
		
		
		options.addOption("h", "help", false, "show help");
		
		try{
			CommandLine cmd = new GnuParser().parse( options, args);
			
			if(cmd.hasOption("h")){
				new HelpFormatter().printHelp( "gis", options );
				System.exit(0);
			}
			
			try{
				opt.cmoId = cmd.getOptionValue("n", InetAddress.getLocalHost().getHostName());
			}catch (UnknownHostException e){}		
			
			opt.strInterface = cmd.getOptionValue("i", "fake");
			opt.gen = cmd.hasOption("g");
			opt.fwd = cmd.hasOption("f");
			opt.cmoType = CMOHeader.typeFromString(cmd.getOptionValue("t", "car"));
			
		    if(opt.cmoType==-1){		    
		    	System.out.println("The CMO type " + cmd.getOptionValue("t", "car")  + " doesn't exist");
		    	System.out.println("\tCMO type available " + CMOHeader.getTypeAvailable());
		    	System.exit(1);
		    }
			
			opt.beaconInterval = Integer.parseInt(cmd.getOptionValue("b", "500"));
			opt.gui = ! cmd.hasOption("d");
			opt.verbose = cmd.hasOption("v");
			
			if(cmd.hasOption("p"))
				opt.fixedPosition = new WGS84(cmd.getOptionValue("p", "50°37'57.41\"N 3°5'8.26\"E"));
					
			if(!cmd.hasOption("m"))
				opt.setAutoFwdGen();
			
		}catch (ParseException e){
			System.err.println(e.getMessage());
			new HelpFormatter().printHelp( "gis", options );
			System.exit(1);
		}

		return opt;
	}
	
	private static BeaconRecv createBeaconRecvFake(){

			BeaconRecvFake recvf = new BeaconRecvFake();		
			recvf.addFixedCMO(new CMOState(
					new CMOHeader((byte)100, 0, 5000, "test_spot       ",CMOHeader.CMO_TYPE_SPOT ),
					3.13061f,
					50.61789f,
					0.0f,
					1.0f,
					0.0f,0));

			recvf.addFixedCMO(new CMOState(
						new CMOHeader((byte)100, 0, 5000, "test_car        ",CMOHeader.CMO_TYPE_CAR ),
						3.12586784363f,
						50.6021995544f,
						0.0f,
						1.0f,
						0.0f,0));
			
			return recvf;

	
	}
	
	private static void log(String module, String message){
		System.out.println((new Date()).getTime() + " ["  + module + "] " + message);
		logger.info(module + " " + message);
	}

	/**
	 * start GIS
	 * @param opt options
	 * @throws IOException Gps reader problem
	 * @throws SecurityException illegal thread interrupt
	 */
	public static void startGIS(GisOptions opt) throws IOException,SecurityException, InterruptedException{
		//System.out.println(opt);
		NetworkInterface device = null;
		BeaconRecv recv = null;
		
		//point separator for reel number
		java.util.Locale.setDefault(java.util.Locale.US);
		

		//init logger
		org.apache.log4j.PropertyConfigurator.configure("log.config");


		//if fake, create a fake recv
		if(opt.strInterface.compareToIgnoreCase("fake")==0){
			recv = createBeaconRecvFake();
		}
		else
		{
			//else create a recv with Ethernet
			device = PcapsTool.toNetworkInterface(opt.strInterface);
		
		    if(device==null){
		    	System.err.println("The interface " + opt.strInterface + " doesn't exist");
		    	PcapsTool.printDevice();
		    	return;
		    }
		    
		    //create the Ethernet beacon receiver
			recv = new BeaconRecvEthernet(JpcapCaptor.openDevice(device, 2000, false, 20), opt.cmoId);

		}
		
		Geolocation loc = null;
		
		if(opt.fixedPosition==null)
			loc = new Gps();
		else
			loc = new Fixe(opt.fixedPosition, 0.0, 0.0);
		
		


		//run generator and forwarder, if needed
		BeaconGenerator gen = null;
		BeaconForward fwd = null;
		
		if((device != null) && (opt.gen || opt.fwd)){
			JpcapSender sender = JpcapSender.openDevice(device);
			
			if(opt.gen){
				gen = new BeaconGenerator(sender, loc, opt.cmoId, opt.cmoType, opt.beaconInterval);
				gen.start();
				if (opt.verbose) log("Init","Run generator");
			}
			if(opt.fwd){
				fwd = new BeaconForward(sender);
				recv.addListener(fwd);
				if (opt.verbose) log("Init","Run forwarder");
			}
		}

		GIS gis = null;
		Shell shell = null;
		Display display = null;

		
		if (opt.gui ||opt.verbose){
			
			//init the dashboard
			final Dashboard db = new Dashboard();
			
			//create the CMO management
			final CMOManagement cmoMgt = new CMOManagement();

			//link the CMO Management with the beaconing receiver
			recv.addListener(cmoMgt);
			
			//link the  dashboard with the geolocation system
			loc.addPositionListener(db);
			
			//link the  dashboard with the CMO Mangement		
			cmoMgt.addListener(db);
			

			db.addIndicator(new Position(loc));
			db.addIndicator(new Speed(loc));
			db.addIndicator(new Track(loc));
			db.addIndicator(new BrakingDistance(loc));
			db.addIndicator(new StoppingDistance(loc));
			
			ClosestCMO closestCMO =  new ClosestCMO(loc, cmoMgt);
			db.addIndicator(closestCMO);    
			
			
			//init the gui
			if(opt.gui){
				//create the parent window
				display = new Display ();
				
				shell = new Shell(display);
				shell.setText("Global Information System");
				shell.setSize(1245, 700);
				shell.setLocation(30, 10);
				shell.setLayout (new FillLayout());

				//create the GIS window
				gis= new GIS(display, loc, cmoMgt, db, closestCMO,  shell, SWT.NONE);
				
				//alow receive the dashboardUpdate
				db.addListener(gis);
			}
			
			//show verbose information
			if(opt.verbose){
				/*System.out.print("#");
				for (Indicator id : db.getIndicators())
					System.out.print(id.name()+";");		
				System.out.println();	*/
				
				//event when dashboard updated
				db.addListener(new DashboardListener() {
					public void dashboardUpdate(){
						//System.out.print("\033[2J");
						log("Dashboard" , db.toString());
					}
				});

				recv.addListener(new BeaconRecvListener() {
					public void cmoStatChanged(CMOState stat) {
						log("Receive packet",stat.getCmoID() + ";" + stat.getSeq() + ";" + stat.getTTL());
					}
				});

			}
		}

		//start the beaconning receiver
		recv.start();

		//start the GPS
		loc.start();	  

		if(opt.gui){
			//show the window
			shell.open ();
	
			//event loop
			while (!shell.isDisposed ()) {
				if (!display.readAndDispatch ()) {
					display.sleep ();
				}
			}
			
			loc.interrupt();
			recv.interrupt();
			
			if(gen !=null) gen.interrupt();
		}else{
			loc.join();
			recv.join();
			if(gen !=null) gen.join();	
		}
		
	


		loc.dispose();
		
		if(gis != null){
			gis.dispose();
			display.dispose ();	
		}

	}
	
	public static void main (String [] args) throws Exception {
		startGIS(parseArgs(args)); 
		System.exit(0);
	}
}