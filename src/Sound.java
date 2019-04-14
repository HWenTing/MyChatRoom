
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;

public class Sound{  
	  AudioClip christmas = loadSound("sounds\\Ã∞≥‘…ﬂ.wav");
	    public static AudioClip loadSound(String filename) {  
	        URL url = null;  
	       try {  
	            url = new URL("file:" + filename);  
	        }   
	        catch (MalformedURLException e) {;}  
	       return JApplet.newAudioClip(url);  
	   }  
	   public void play() {  
	      
	        christmas.loop();  
	   }  
	   public void stop() {  
	     
	        christmas.stop();  
	   }  
	  
}  
