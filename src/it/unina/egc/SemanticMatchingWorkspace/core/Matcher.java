package it.unina.egc.SemanticMatchingWorkspace.core;

import java.awt.SecondaryLoop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.jena.base.Sys;

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
	
	static double firstThreshold = 1.0;
	static double JaccardThreshold = 1.0;
	
	static String file1 = "export/WordNetWordLexicalChainsOfHypernims_level_0.obj";
	static String file2 = "export/DBpediaClassNameLexicalChainsOfSuperClasses_level_0.obj";
	
	public static void main(String[] args) 
	{
		try
		{
			
			FileWriter statisticWirter = new FileWriter("export/matching001.txt");
			
			//FileWriter fileWriter = new FileWriter("export/alignments");
			
			System.out.println("Reading serialized files....");
			ObjectInputStream ois1 = new ObjectInputStream(                                
	                new FileInputStream(new File(file1))) ;
			
			ObjectInputStream ois2 = new ObjectInputStream(                                 
	                new FileInputStream(new File(file2))) ;
	       
			Vector<Vector<String>> wordArray1 = (Vector<Vector<String>>)ois1.readObject();
			Vector<Vector<String>> wordArray2 = (Vector<Vector<String>>)ois2.readObject();
			
			System.out.println("Finished to read serialized files....");
			
			int counter = 0;
			
			long startTime = System.currentTimeMillis();
	        
			statisticWirter.write("Matching results for " + file1 + " matched to " + file2 + "\nThreshold: " + firstThreshold + ":" + JaccardThreshold + "\n");
			for (Vector<String> v1 : wordArray1) 
			{
				for (Vector<String> v2 : wordArray2)
				{
					String indexStr1 = v1.get(0);
					String indexStr2 = v2.get(0);
					
					v1.trimToSize();
					v2.trimToSize();
					
					double measure1 = compareMultiWords(indexStr1, regex1, indexStr2, regex2);
					
					if (measure1 == firstThreshold)
					{
						double measure2 = computeJaccardSimilarity(v1, v2);
						
						System.out.println(indexStr1 + " = (" + measure1 + ", " + measure2 + ") " + indexStr2);
					
						if (measure2 == 0.0)
							zeroMatchingAverage++;
						else if (measure2 < 0.1 && measure2 > 0.0)
							lessThan10MatchingAverage++;
						else if (measure2 < 0.2 && measure2 >= 0.1)
							lessThan20MatchingAverage++;
						else if (measure2 < 0.3 && measure2 >= 0.2)
							lessThan30MatchingAverage++;
						if (measure2 < 0.4 && measure2 >= 0.3)
							lessThan40MatchingAverage++;
						if (measure2 < 0.5 && measure2 >= 0.4)
							lessThan50MatchingAverage++;
						if (measure2 < 0.6 && measure2 >= 0.5)
							lessThan60MatchingAverage++;
						if (measure2 < 0.7 && measure2 >= 0.6)
							lessThan70MatchingAverage++;
						if (measure2 < 0.8 && measure2 >= 0.7)
							lessThan80MatchingAverage++;
						if (measure2 < 0.9 && measure2 >= 0.8)
							lessThan90MatchingAverage++;
						if (measure2 < 1 && measure2 >= 0.9)
							lessThan100MatchingAverage++;
						if (measure2 == 1)
							equal100MatchingAverage++;
					}
				}
				
				//if (counter%1000==0)
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
					//System.out.println("Number of WordNet word analyzed: " + counter);
				//}
				//counter++;
			}
			
			statisticWirter.write("Statistics:\n");
			statisticWirter.write("zeroMatchingAverage, " + zeroMatchingAverage + "\n" +
					"lessThan10MatchingAverage, " + lessThan10MatchingAverage + "\n" + 
					"lessThan20MatchingAverage, " +  lessThan20MatchingAverage + "\n" +
					"lessThan30MatchingAverage, " +  lessThan30MatchingAverage + "\n" +
					"lessThan40MatchingAverage, " +  lessThan40MatchingAverage + "\n" +
					"lessThan50MatchingAverage, " +  lessThan50MatchingAverage + "\n" +
					"lessThan60MatchingAverage, " +  lessThan60MatchingAverage + "\n" +
					"lessThan70MatchingAverage, " +  lessThan70MatchingAverage + "\n" +
					"lessThan80MatchingAverage, " +  lessThan80MatchingAverage + "\n" +
					"lessThan90MatchingAverage, " +  lessThan90MatchingAverage + "\n" +
					"lessThan100MatchingAverage, " + lessThan100MatchingAverage + "\n" +
					"equal100MatchingAverage, " + equal100MatchingAverage + "\n");
			
			statisticWirter.write("Execution Time: " + ((System.currentTimeMillis() - startTime)/1000) + "[s]\n");
			
			//fileWriter.close();
			statisticWirter.flush();
			statisticWirter.close();
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
				if (similarity == JaccardThreshold) 
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
		
		
		if ((words1.length > 1) || (words2.length > 1))
		{
			int match = 0;
			int stopWordCounter1 = 0;
			int stopWordCounter2 = 0;
			//System.out.println(words1.length + " " + words2.length);
			
			
			for (int i = 0; i < words1.length; i++)
			{
				if (isStopWord(words1[i]))
					stopWordCounter1++;
			}
			
			for (int i = 0; i < words2.length; i++)
			{
				if (isStopWord(words2[i]))
					stopWordCounter2++;
			}
			
			for (int i = 0; i < words1.length; i++)
			{
				for (int j = 0; j < words2.length; j++)
				{
					if (!(isStopWord(words1[i]) || isStopWord(words2[j])))
					{
						if (words1[i].compareToIgnoreCase(words2[j])==0)
							match++;
					}
				}
			}
			return (double)(((double)match)/(double)(words1.length + words2.length - stopWordCounter1 - stopWordCounter2 - match));
		}
		else
			return s1.compareToIgnoreCase(s2) == 0 ? 1.0 :0.0; //Exact string matching
		 
		
		
		
			
			
	}
	
	static boolean isStopWord(String word)
	{
		if (word.compareToIgnoreCase("of") == 0 || 
				word.compareToIgnoreCase("the") == 0 ||
				word.compareToIgnoreCase("a") == 0 ||
				word.compareToIgnoreCase("and") == 0 || 
				word.compareToIgnoreCase("for") == 0)
			return true;
		else
			return false;
	}
}
