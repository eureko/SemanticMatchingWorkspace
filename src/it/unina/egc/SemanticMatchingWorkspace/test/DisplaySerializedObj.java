package it.unina.egc.SemanticMatchingWorkspace.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Vector;

public class DisplaySerializedObj 
{
	static String file1 = "export/WordNetWordLexicalChainsOfAllRelated_level_2.obj";
	
	public static void main(String[] args) 
	{
		try
		{
			System.out.println("Reading serialized files....");
			ObjectInputStream ois1 = new ObjectInputStream(                                
	                new FileInputStream(new File(file1))) ;
			
	       
			Vector<Vector<String>> wordArray1 = (Vector<Vector<String>>)ois1.readObject();
			
			System.out.println("Finished to read serialized files....");
			
			
			
			long startTime = System.currentTimeMillis();
	        
			for (Vector<String> v : wordArray1) 
			{
				System.out.println(v);
			}
			
			ois1.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
