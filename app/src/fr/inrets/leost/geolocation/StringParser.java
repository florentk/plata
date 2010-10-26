package fr.inrets.leost.geolocation;

import java.util.Locale;
import java.util.Scanner;

import java.util.regex.MatchResult;

public class StringParser {
	
	
	public static final byte INDETERMINATE = 0;	
	public static final byte LONGITUDE = 1;
	public static final byte LATITUDE = 2;	
	
	private Double value;
	private byte type;
	
	public StringParser(String pos){
		parse(pos);
	}
	
	// 50°37'57.41"N
	public void parse(String pos) {
		pos.trim();
		
	   /*  Scanner s = new Scanner(pos);
	     
	     s.useLocale(Locale.US);
	     
	     s.useDelimiter("°");
	     System.out.println(s.nextInt());    
	     
	     s.useDelimiter("\\d+");s.next();  
	     
	     s.useDelimiter("'");
	     System.out.println(s.nextInt());   
	     
	     s.useDelimiter("[0-9.]+");s.next();  
	     
	     s.useDelimiter("\"");
	     System.out.println(s.nextDouble());   */
		
		
		
	     Scanner s = new Scanner(pos);
	     s.useLocale(Locale.US);
	     s.findInLine("(\\d+)°(\\d+)'([0-9.]+)\"([NSEWO])");
	     MatchResult result = s.match();
	     
	     if(result.groupCount() != 4) throw new IllegalArgumentException();

	     double deg = Integer.parseInt(result.group(1)) ;
	     double min = Integer.parseInt(result.group(2)) ;	     
	     double sec = Double.parseDouble(result.group(3)) ;	     
	     char t = result.group(4).charAt(0) ;	  
	     
	     value = deg + min/60.0 + sec/3600.0;
	     
	     switch(t){
	     case 'S':
	    	 deg*=-1.0;
	     case 'N':
	    	 type = LATITUDE;
	    	 break;
	     case 'E':
	    	 deg*=-1.0;	    	 
	     case 'O':	    	 
	     case 'W':
	    	 type = LONGITUDE;
	    	 break;
 
	     }
	     
	     
	     
	      
	     s.close(); 		
		
	}

	public Double getValue() {
		return value;
	}


	public byte getType() {
		return type;
	}

	
	
	
	
}
