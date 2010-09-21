
import java.net.Socket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;


public class Gps {



	private BufferedReader gpsBR;



	static private void writeInit(BufferedWriter gpsBW) throws IOException {
		gpsBW.write( "w+r-\n" );
		gpsBW.flush();		
	}

	static private Socket connectToGpsd()throws IOException {
		return  connectToGpsd( InetAddress.getLocalHost(), 2947);	
	}
	
	static private Socket connectToGpsd(InetAddress addr, int port)throws IOException {
		return new Socket( addr, port);	
	}	
	

	public Gps() throws IOException {
		connectGPS();
	}

	private void connectGPS() throws IOException {
		Socket gpsSocket;
		
		gpsSocket = connectToGpsd();
		
		gpsBR = new BufferedReader( new 
				InputStreamReader( gpsSocket.getInputStream()) );
		
		writeInit(new BufferedWriter( new 
				OutputStreamWriter( gpsSocket.getOutputStream() ) ));

	}  	
	
	


}
