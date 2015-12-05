package it.unina.egc.SemanticMatchingWorkspace.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.jena.ext.com.google.common.base.Equivalence;
import org.apache.log4j.net.ZeroConfSupport;

import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWordID;

public class Matcher 
{
		
	static final String regex1 = "[_\\s]";
	static final String regex2 = "(?<!^)(?=[A-Z])";	
	
	TreeMap<String, Integer> statistics = new TreeMap<String, Integer>();
	
	static int zeroMatchingAverage = 0;
	static int lessThan10MatchingAverage = 0;
	static int lessThan20MatchingAverage = 0;
	static int lessThan30MatchingAverage = 0;
	static int lessThan40MatchingAverage = 0;
	static int lessThan50MatchingAverage = 0;
	static int lessThan60MatchingAverage = 0;
	static int lessThan70MatchingAverage = 0;
	static int lessThan80MatchingAverage = 0;
	static int lessThan90MatchingAverage = 0;
	static int lessThan100MatchingAverage = 0;
	static int equal100MatchingAverage = 0;
	
	
	
	
	public static void main(String[] args) 
	{
		try
		{
			
			FileWriter fileWriter = new FileWriter("export/alignments");
			
				
			
			System.out.println("Reading serialized files....");
			ObjectInputStream ois1 = new ObjectInputStream(                                 
	                new FileInputStream(new File("export/WordNetLexicalChainsHyperMAX.obj"))) ;
			
			ObjectInputStream ois2 = new ObjectInputStream(                                 
	                new FileInputStream(new File("export/DBpediaLexicalChainsSuperClasses.obj"))) ;
	       
			Vector<Vector<String>> wordArray1 = (Vector<Vector<String>>)ois1.readObject();
			Vector<Vector<String>> wordArray2 = (Vector<Vector<String>>)ois2.readObject();
			
			System.out.println("Finished to read serialized files....");
			
			int counter = 0;
	        
			for (Vector<String> v1 : wordArray1) 
			{
				for (Vector<String> v2 : wordArray2)
				{
					String indexStr1 = v1.get(0);
					String indexStr2 = v2.get(0);
					
					v1.trimToSize();
					v2.trimToSize();
					
					//double measure = computeSimilarity2(v1, v2);
					
					if (compareMultiWords(indexStr1, regex1, indexStr2, regex2) == 1.0)
					{
						double measure = computeJaccardSimilarity(v1, v2);
						//System.out.println(indexStr1 + " = (" + measure + ") " + indexStr2);
						fileWriter.write(indexStr1 + " = (" + measure + ") " + indexStr2 + "\n");
					
						if (measure == 0.0)
							zeroMatchingAverage++;
						else if (measure < 0.1 && measure > 0.0)
							lessThan10MatchingAverage++;
						else if (measure < 0.2 && measure >= 0.1)
							lessThan20MatchingAverage++;
						else if (measure < 0.3 && measure >= 0.2)
							lessThan30MatchingAverage++;
						if (measure < 0.4 && measure >= 0.3)
							lessThan40MatchingAverage++;
						if (measure < 0.5 && measure >= 0.4)
							lessThan50MatchingAverage++;
						if (measure < 0.6 && measure >= 0.5)
							lessThan60MatchingAverage++;
						if (measure < 0.7 && measure >= 0.6)
							lessThan70MatchingAverage++;
						if (measure < 0.8 && measure >= 0.7)
							lessThan80MatchingAverage++;
						if (measure < 0.9 && measure >= 0.8)
							lessThan90MatchingAverage++;
						if (measure < 1 && measure >= 0.9)
							lessThan100MatchingAverage++;
						if (measure == 1)
							equal100MatchingAverage++;
						
						//if (((++counter)%100000)==0)
						//{
						
							/*System.out.println("zeroMatchingAverage: " + zeroMatchingAverage + "\n" +
							"lessThan10MatchingAverage: " + lessThan10MatchingAverage + "\n" + 
							"lessThan20MatchingAverage: " +  lessThan20MatchingAverage + "\n" +
							"lessThan30MatchingAverage: " +  lessThan30MatchingAverage + "\n" +
							"lessThan40MatchingAverage: " +  lessThan40MatchingAverage + "\n" +
							"lessThan50MatchingAverage: " +  lessThan50MatchingAverage + "\n" +
							"lessThan60MatchingAverage: " +  lessThan60MatchingAverage + "\n" +
							"lessThan70MatchingAverage: " +  lessThan70MatchingAverage + "\n" +
							"lessThan80MatchingAverage: " +  lessThan80MatchingAverage + "\n" +
							"lessThan90MatchingAverage: " +  lessThan90MatchingAverage + "\n" +
							"lessThan100MatchingAverage: " + lessThan100MatchingAverage + "\n" +
							"equal100MatchingAverage: " + equal100MatchingAverage);*/
						//}
					}
					
				}
				
				//System.out.flush();
				fileWriter.flush();
				
			}
			
			fileWriter.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	static double computeSimilarity(Vector<String> v1, Vector<String> v2)
	{
		double similarity = 0.0;
		
		// Jaccard similarity
		int counter = 0;
		
		for (String s1:v1)
		{
			for (String s2:v2)
			{
				similarity =+ compareMultiWords(s1, regex1, s2, regex2);
				counter++;
			}
		}
		
		return (similarity/counter);
	}
	
	static double computeSimilarity2(Vector<String> v1, Vector<String> v2)
	{
		double similarity = 0.0;
		
		// Jaccard similarity
		int counter = 0;
		
		for (String s1:v1)
		{
			for (String s2:v2)
			{
				similarity = compareMultiWords(s1, regex1, s2, regex2);
				if (similarity>0) counter++;
			}
		}
		
		return (similarity);
	}
	
	static int computeSimilarity3(Vector<String> v1, Vector<String> v2)
	{
		int sim_count = 0;
		
		// Jaccard similarity
		int counter = 0;
		
		for (String s1:v1)
		{
			for (String s2:v2)
			{
				double similarity = compareMultiWords(s1, regex1, s2, regex2);
				if (similarity == 1) sim_count++;
			}
		}
		
		return sim_count;
	}
	
	static double computeJaccardSimilarity(Vector<String> v1, Vector<String> v2)
	{
		int equalCount = 0;
		
		// Jaccard similarity
		int counter = 0;
		
		for (String s1:v1)
		{
			for (String s2:v2)
			{
				double similarity = compareMultiWords(s1, regex1, s2, regex2);
				if (similarity == 1) 
					equalCount++;
			}
		}
		
		int v1Size = v1.size();
		int v2Size = v2.size();
		
		return (double)equalCount/((double)(v1Size + v2Size - equalCount));
	}
	
	
	static double compareMultiWords(String s1, String regex1, String s2, String regex2)
	{
		String[] words1 = s1.split(regex1);
		String[] words2 = s2.split(regex2); 
		
		int counter = 0;
		int adjustingVar = 0;
		//System.out.println(words1.length + " " + words2.length);
		
		for (int i = 0; i < words1.length; i++)
		{
			if (!isStopWord(words1[i]))
			{
				for (int j = 0; j < words2.length; j++)
				{
					if (!isStopWord(words1[i]))
					{
						if (words1[i].compareToIgnoreCase(words2[j])==0)
							counter++;
					}
					else
						adjustingVar++;
				}
			}
			else
				adjustingVar++;
		}
		
		return (double)(2*((double)counter/(words1.length + words2.length - adjustingVar)));
	}
	
	static boolean isStopWord(String word)
	{
		if (word.compareToIgnoreCase("of") == 0 || 
				word.compareToIgnoreCase("the") == 0 ||
				word.compareToIgnoreCase("a") == 0)
			return true;
		else
			return false;
	}
}
