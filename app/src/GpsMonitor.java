
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.roots.swtmap.MapWidget;


public class GpsMonitor {
	static MapWidget map;
	static Display display;
	static Gps gps;
	static Image car;
	
	public static Point WGS84toPoint(WGS84 position,int z){
		return new Point(MapWidget.lon2position(position.longitude(), z), MapWidget.lat2position(position.latitude(), z));
	}
	
	public static Image loadCarImage(){
		return new Image(display, "res/twingo.png");
	}

    public static void main (String [] args) throws Exception {

      
      display = new Display ();
      Shell shell = new Shell(display);
      shell.setText("GPS Monitor");
      shell.setSize(600, 710);
      shell.setLocation(10, 10);
      shell.setLayout (new FillLayout());
      
      
      map = new MapWidget(shell, SWT.NONE);
      map.addOverImage( map.new OverImage(0, 0, loadCarImage()) ); 

    		  
      gps = new Gps();
      gps.addPositionListener(new GpsListener() {

    	  public void positionChanged(WGS84 position) {
    		  display.syncExec(
				  new Runnable(){
				      public void run(){
				    	  map.setCenterPosition( WGS84toPoint(gps.getCurrentPos(), map.getZoom()) );
				    	  //map.setCenterPosition(new Point(45,78));
				    	  map.redraw();
				      }
				    }  
    		  );
    		 
    	  }

      });
      
      
      shell.open ();
      while (!shell.isDisposed ()) {
          if (!display.readAndDispatch ()) display.sleep ();
      }
      display.dispose ();
      
      
  }
	
	
}
