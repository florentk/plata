/*
 * $HeadURL$
 *
 * (c)2010 Stepan Rutz, Licensed under LGPL License
 */

package com.roots.swtmap;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.roots.swtmap.MapWidget.PointD;
import com.roots.swtmap.MapWidget.TileServer;

/**
 * @author stepan.rutz@gmx.de
 * @version $Revision$
 */
public class MapBrowser extends Composite {
    private SashForm sashForm;
    private PageContainer pageContainer;
    private MapWidget mapWidget;
    private SearchPage searchPage;
    private ResultsPage resultsPage;
    private InfoPage infoPage;

    public MapBrowser(Composite parent, int style) {
        super(parent, style);
        
        setLayout(new FillLayout());
        
        sashForm = new SashForm(this, SWT.HORIZONTAL);
        sashForm.setLayout(new FillLayout());

        pageContainer = new PageContainer(sashForm, SWT.NONE);
        mapWidget = new MapWidget(sashForm, SWT.NONE, MapWidget.computePosition(new PointD(3.12780, 50.61164),16),16);
   
        mapWidget.setTileServer(new TileServer("file://" + "/var/cache/osm/tiles/", 18));
        
        sashForm.setWeights(new int[] { 100, 200 });
         
        searchPage = new SearchPage(this);
        resultsPage = new ResultsPage(this);
        infoPage = new InfoPage(this);
        pageContainer.setPages(searchPage, resultsPage, infoPage);
        pageContainer.showPage(0);
        
        mapWidget.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                infoPage.updateInfos();
            }
        });
        mapWidget.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                infoPage.updateInfos();
            }
        });
        mapWidget.addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(MouseEvent e) {
                infoPage.updateInfos();
            }
        });
    }
    
    public MapWidget getMapWidget() {
        return mapWidget;
    }
    
    public SearchPage getSearchPage() {
        return searchPage;
    }
    
    public InfoPage getInfoPage() {
        return infoPage;
    }
    
    public ResultsPage getResultsPage() {
        return resultsPage;
    }
    
    public PageContainer getPageContainer() {
        return pageContainer;
    }
    
    private void createMenu(Shell shell) {
        Menu bar = new Menu (shell, SWT.BAR);
        shell.setMenuBar (bar);
        MenuItem fileItem = new MenuItem (bar, SWT.CASCADE);
        fileItem.setText ("&File");
        Menu submenu = new Menu (shell, SWT.DROP_DOWN);
        fileItem.setMenu (submenu);
        MenuItem item = new MenuItem (submenu, SWT.PUSH);
        item.addListener (SWT.Selection, new Listener () {
            public void handleEvent (Event e) {
                Runtime.getRuntime().halt(0);
            }
        });
        item.setText ("E&xit\tCtrl+W");
        item.setAccelerator(SWT.MOD1 + 'W');
    }
    
    public static void main (String [] args) throws Exception {
        Display display = new Display ();
        Shell shell = new Shell(display);
        shell.setText("Map Widget - SWT Native Map Browsing, Map data from openstreetmap.org");
        shell.setSize(600, 710);
        shell.setLocation(10, 10);
        shell.setLayout (new FillLayout());

        new MapBrowser(shell, SWT.NONE);
        shell.open ();
        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }
        display.dispose ();
    }

}


