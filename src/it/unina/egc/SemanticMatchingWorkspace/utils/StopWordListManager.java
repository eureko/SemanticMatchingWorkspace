package it.unina.egc.SemanticMatchingWorkspace.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class StopWordListManager {
	
		
		static Vector<String> stopWordList = new Vector<String>();
	
		public static void main(String[] args) 
		{
			try (BufferedReader br = new BufferedReader(new FileReader("resources/stop-word-list.txt"))) 
			{
				
				String line;
			    while ((line = br.readLine()) != null) 
			    {
			    	stopWordList.addElement(line);
			    }
			    
				 ObjectOutputStream oos = new ObjectOutputStream( 
                         new FileOutputStream(new File("resources/stop-word-list.obj")));

				  oos.writeObject(stopWordList);
				  // close the writing.
				  oos.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

}
