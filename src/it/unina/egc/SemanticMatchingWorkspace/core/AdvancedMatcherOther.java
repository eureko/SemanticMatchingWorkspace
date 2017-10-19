package it.unina.egc.SemanticMatchingWorkspace.core;

import it.unina.egc.SemanticMatchingWorkspace.utils.JWIWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.POS;

public class AdvancedMatcherOther 
{
	
	static final String  DBpediaResourceFile = "./short-abstracts_en.ttl";
	
	static JWIWrapper jwiwrapper;	
	
	static TreeMap<String, Integer> sidMap = new TreeMap<String, Integer>();
	static TreeMap<String, Integer> resourceMap = new TreeMap<String, Integer>();
	
	static final String regex1 = "[_\\s]";
	static final String regex2 = "(?<!^)(?=[A-Z])";	
	
	static Vector<String> stopWordListVector;
	
	public static void main(String[] args) 
	{
		try
		{
			// Get all synsets from WordNet
			jwiwrapper = new JWIWrapper();
			
			//Read from serialized files
			ObjectInputStream ois = new ObjectInputStream(                                 
			        new FileInputStream(new File("stop-word-list.obj")));
			
			stopWordListVector = (Vector<String>) ois.readObject();
			
			//Exporting synset nodes
			//POS[] pos_values = POS.values();
			int id = 0;
				
			//for (POS pos:pos_values)
			//{
				
			BufferedReader br = new BufferedReader(new FileReader(DBpediaResourceFile));
		    String line;
		    
				Iterator<ISynset> synsetIterator = jwiwrapper.dictionary.getSynsetIterator(POS.NOUN);
				
				while(synsetIterator.hasNext()) 
				{
					ISynset synset = synsetIterator.next();
					sidMap.put(synset.getID().toString(), id);
					id++;
					
					int c = 0;
					
					br.readLine(); //skip comment
					
					 while (((line = br.readLine()) != null)) 
					 {
					        
						String stm1 = line.substring(line.indexOf('<'), line.indexOf('>') + 1);
						String stm2 =  line.substring(line.lastIndexOf('>') + 1, line.length());
						
						
						 
						// String[] statement = line.split("(?=(([^'\"]*['\"]){2})*[^'\"]*$)");
					       // System.out.println(statement[0]);
					       // System.out.println(statement[1]);
					       // System.out.println(statement[2]);
					        
					      stm2 =  stm2.replaceAll("\"", "");
					      stm2 =  stm2.replace("\\", "");
						
						double measure = compareMultiWords(synset.getGloss(), "\\s", stm2, "\\s");
					        
					      if (measure > 0)
					       {   
						        System.out.println(synset + " --> " + synset.getGloss());
						        System.out.println(stm1 + " --> " + stm2);
						        System.out.println("Measure: " + measure);
						        System.out.println("__________________________________");
					       }
					       
					       //c++;
					       //System.out.println(c);
					 }
					 
					
					 
					 System.out.println("reset");
					
					
					
				}
			//}
			
			// Get all DBpedia resources
			
			// Exporting namedClasses
			
			
		   
		   
			
		    br.close();
			
			
			//Matching phase
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static OntModel getOntologyModel(String ontoFile)
	{   
	    OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
	    try 
	    {
	        InputStream in = FileManager.get().open(ontoFile);
	        try 
	        {
	            ontoModel.read(in, null);
	            System.out.println("Ontology " + ontoFile + " loaded.");
	        } 
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
	    } 
	    catch (JenaException je) 
	    {
	        System.err.println("ERROR" + je.getMessage());
	        je.printStackTrace();
	        System.exit(0);
	    }
	    return ontoModel;
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
	
	
	static int contCommonWordNumber(String s1, String regex1, String s2, String regex2)
	{
		String[] words1 = s1.split(regex1);
		String[] words2 = s2.split(regex2); 
		
		
		int match = 0;
		
		
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
		
		return match;
	}
	
	
	/*static boolean isStopWord(String word)
	{
		if (word.compareToIgnoreCase("of") == 0 || 
				word.compareToIgnoreCase("the") == 0 ||
				word.compareToIgnoreCase("a") == 0 ||
				word.compareToIgnoreCase("and") == 0 || 
				word.compareToIgnoreCase("for") == 0 ||
				word.compareToIgnoreCase("is") == 0 ||
				word.compareToIgnoreCase("that") == 0 ||
				word.compareToIgnoreCase("or") == 0)
 				
			return true;
		else
			return false;
	}*/
	
	static boolean isStopWord(String word)
	{
		if (stopWordListVector.contains(word)) 				
			return true;
		else
			return false;
	}
	
	static int getFrequencyRank(IWord word)
	{
		ISynset synset = word.getSynset();
		int rank = 0, i = 0;
		
		List<IWord> wordList = synset.getWords();
		for (IWord w:wordList)
		{
			if (w.equals(word))
				rank = i;
			i++;
		}
		return rank;
	}
	
	static double compareText(String s1, String s2)
	{
		String[] words1 = s1.split("\\s");
		String[] words2 = s2.split("\\s");
		int counter = 0;
		
		for (int i = 0; i < words1.length; i++)
		{
			for (int j = 0; j < words2.length; j++)
			{
				if (words1[i].compareToIgnoreCase(words2[j]) == 0)
					counter++;
			}
		}
		
		return (double)((double)counter/(double)(words1.length + words2.length));
	}
}