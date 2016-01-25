package it.unina.egc.SemanticMatchingWorkspace.core;

import it.unina.egc.SemanticMatchingWorkspace.utils.JWIWrapper;

import java.io.FileWriter;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

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

public class AdvancedMatcherV2 
{
	
	static final String  DBpediaTBOXFile = "../Resources/DBpedia/dbpedia_2014.owl";
	
	static JWIWrapper jwiwrapper;	
	
	static TreeMap<String, Integer> sidMap = new TreeMap<String, Integer>();
	static TreeMap<String, Integer> classesMap = new TreeMap<String, Integer>();
	
	static final String regex1 = "[_\\s]";
	static final String regex2 = "(?<!^)(?=[A-Z])";	
	
	public static void main(String[] args) 
	{
		
		
		try
		{
			// Get all synsets from WordNet
			
			jwiwrapper = new JWIWrapper();
			
			//Exporting synset nodes
			//POS[] pos_values = POS.values();
			int id = 0;
				
			//for (POS pos:pos_values)
			//{
				Iterator<ISynset> synsetIterator = jwiwrapper.dictionary.getSynsetIterator(POS.NOUN);
				
				while(synsetIterator.hasNext()) 
				{
					ISynset synset = synsetIterator.next();
					sidMap.put(synset.getID().toString(), id);
					id++;
				}
			//}
			
			// Get all DBpedia TBOX classes
			
			// Exporting namedClasses
			
			int namedClassId = 0;
			OntModel ontoModel = getOntologyModel(DBpediaTBOXFile);
			ontoModel.setStrictMode(false);
			ExtendedIterator<OntClass> classesIter = ontoModel.listNamedClasses();
			
			while(classesIter.hasNext())
			{
				OntClass c = classesIter.next();
				classesMap.put(c.toString(), namedClassId);
				namedClassId++;
			}
			
			//Matching phase
			
			FileWriter alignmentsWriter = new FileWriter("export/alignments/alignmentV1.0.csv");
			alignmentsWriter.write("SynsetId,frequency,classId\n");
			
			Iterator<ISynset> synsetIterator2 = jwiwrapper.dictionary.getSynsetIterator(POS.NOUN);
			
			while(synsetIterator2.hasNext())
			{
				ISynset synset = synsetIterator2.next(); 
				List<IWord> words = synset.getWords();
				
				for (IWord w:words)
				{
					String wordLemma = w.getLemma();
					
					Iterator<String> classIter = classesMap.keySet().iterator();
					
					while(classIter.hasNext())
					{
						String classURI = classIter.next();
						OntClass c = ontoModel.getOntClass(classURI);
						
						String classLocalName = c.getLocalName();
						
						double measure1 = compareMultiWords(wordLemma, regex1, classLocalName, regex2);
						
						if (measure1 == 1.0)
						{
							System.out.println(synset.toString() + "<>" + c.toString());
							
							alignmentsWriter.write(
									sidMap.get(synset.getID().toString()) + "," +
									(getFrequencyRank(w) + 1) + "," + 
									classesMap.get(classURI) + "\n");
						}
					}
				}
			}
			
			alignmentsWriter.flush();
			alignmentsWriter.close();
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
	
}
