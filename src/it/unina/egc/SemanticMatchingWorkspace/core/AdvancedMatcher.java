package it.unina.egc.SemanticMatchingWorkspace.core;

import it.unina.egc.SemanticMatchingWorkspace.utils.JWIWrapper;

import java.awt.SecondaryLoop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.jena.base.Sys;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

public class AdvancedMatcher 
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
	
	//static String file1 = "export/WordNetWordLexicalChainsOfHypernims_level_0.obj";
	//static String file2 = "export/DBpediaClassNameLexicalChainsOfSuperClasses_level_0.obj";
	
	static String wordnetIndexObjFileName = "export/indexes/WordNetWIDIndex.obj";
	static String dbpediaIndexObjFileName = "export/indexes/DBpediaClassesIndex.obj";
	
	static TreeMap<String, Integer> widMap;// = new TreeMap<String, Integer>();
	static TreeMap<String, Integer> classesMap;
	
	static TreeMap<Integer, Vector<String>> wordNetLexicalChainsMap = new TreeMap<Integer, Vector<String>>();
	static TreeMap<Integer, Vector<String>> DBpediaLexicalChainsMap = new TreeMap<Integer, Vector<String>>();
	
	static JWIWrapper jwiwrapper;
	static final String  DBpediaTBOXFile = "../Resources/DBpedia/dbpedia_2014.owl";
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) 
	{
		try
		{
			long startTime = System.currentTimeMillis();
			// Read WordNet index file
			System.out.println("Read WordNet index file from: " + wordnetIndexObjFileName + " " + (startTime/1000));
			
			ObjectInputStream wordNetIndexFileStream = new ObjectInputStream(new FileInputStream(new File(wordnetIndexObjFileName)));
			widMap = (TreeMap<String, Integer>)wordNetIndexFileStream.readObject();
			wordNetIndexFileStream.close();
			
			jwiwrapper = new JWIWrapper();
			
			// Read DBpedia index file
			System.out.println("Read DBpedia index file from: " + dbpediaIndexObjFileName + ". " + (System.currentTimeMillis() - startTime)/1000 + " [s]");
			
			ObjectInputStream dbpediaIndexFileStream = new ObjectInputStream(new FileInputStream(new File(dbpediaIndexObjFileName)));
			classesMap = (TreeMap<String, Integer>)dbpediaIndexFileStream.readObject();
			dbpediaIndexFileStream.close();
			
			// Create lexical chains map for WordNet
			System.out.println("Create lexical chains map for WordNet" + ". " + (System.currentTimeMillis() - startTime)/1000 + " [s]");
			
			Iterator<IIndexWord> wordIterator = jwiwrapper.dictionary.getIndexWordIterator(POS.NOUN);
			
			while(wordIterator.hasNext()) 
			{
				IIndexWord indexWord = wordIterator.next();
				List<IWordID> wordIDs = indexWord.getWordIDs();
				
				for (IWordID wid : wordIDs)
				{
					IWord word = jwiwrapper.dictionary.getWord(wid);
					Vector<String> v = new Vector<String>();
					
					exploreWord(word.getSynset(), Pointer.HYPERNYM, 1, v);
					try
					{
						wordNetLexicalChainsMap.put(widMap.get(word.getID().toString()), v);
					}
					catch(Exception ex)
					{
						System.out.println(word.getID().toString());
						ex.printStackTrace();
					}
				}
			}
			
			// Create lexical chains map for DBpedia
			System.out.println("Create lexical chains map for DBpedia" + ". " + (System.currentTimeMillis() - startTime)/1000 + " [s]");
			OntModel ontoModel = getOntologyModel(DBpediaTBOXFile);
			ontoModel.setStrictMode(false);
			ExtendedIterator<OntClass> classesIter = ontoModel. listNamedClasses();
			
			while(classesIter.hasNext())
			{
				OntClass c = classesIter.next();
				Vector<String> v = new Vector<String>();
				exploreSuperClasses(c, 1, v);
				DBpediaLexicalChainsMap.put(classesMap.get(c.toString()), v);
			}
			
					
			// Matching phase
			System.out.println("Start matching phase... "  + (System.currentTimeMillis() - startTime)/1000 + " [s]");
			
			// Open alignment writer
			FileWriter alignmentsWriter = new FileWriter("export/alignments/alignmentV0.1.csv");
			
			alignmentsWriter.write("word_id,class_id,stringSim,semanticSim\n");
			//alignmentsWriter.write("#Matching results for " + file1 + " matched to " + file2 + ". Threshold: " + firstThreshold + ":" + JaccardThreshold + "\n");
						
			int counter = 0;
			
			wordIterator = jwiwrapper.dictionary.getIndexWordIterator(POS.NOUN);
			
			System.out.println("Start alignment...");
			while(wordIterator.hasNext()) 
			{
				IIndexWord indexWord = wordIterator.next();
				List<IWordID> wordIDs = indexWord.getWordIDs();
				
				for (IWordID wid : wordIDs)
				{
					IWord word = jwiwrapper.dictionary.getWord(wid);
					int word_id = widMap.get(word.getID().toString()).intValue();
					Vector<String> v1 = wordNetLexicalChainsMap.get(word_id);
					
					classesIter = ontoModel. listNamedClasses();
					
					while(classesIter.hasNext())
					{
						OntClass c = classesIter.next();
						int class_id = classesMap.get(c.toString()).intValue();
						Vector<String> v2 = DBpediaLexicalChainsMap.get(class_id);
						
						// Perform matching
						
						String indexStr1 = word.getLemma();
						String indexStr2 = c.getLocalName();
						
						v1.trimToSize();
						v2.trimToSize();
						
						double measure1 = compareMultiWords(indexStr1, regex1, indexStr2, regex2);
						
						if (measure1 == firstThreshold)
						{
							System.out.println("Line counter: " + ++counter);
							double measure2 = computeJaccardSimilarity(v1, v2);
							
							//alignmentsWriter.write(word.getID().toString() + "," + c.toString() + "," + word_id + "," + class_id + ", " + measure1 + ", " + measure2 + "\n");
							alignmentsWriter.write(word_id + "," + class_id + "," + measure1 + "," + measure2 + "\n");
						}						
					}
				}
			}
			
			System.out.println("Execution Time: " + ((System.currentTimeMillis() - startTime)/1000) + "[s]");
			
			//fileWriter.close();
			alignmentsWriter.flush();
			alignmentsWriter.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
		
	static double computeJaccardSimilarity(Vector<String> v1, Vector<String> v2)
	{
		int equalCount = 0;
		
		// Jaccard similarity
		//int counter = 0;
		
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
	
	// lexical chains explorers
	
	static void exploreWord(ISynset synset, IPointer p, int level, Vector<String> v)
	{
		if (level >= 0)
		{
			//System.out.println("Start of level: " + (Integer.MAX_VALUE - level) + " Synset: " + synset.getWords());
			
			ArrayList<String> arrayList = getArrayList(synset); //Get the synonims ring
			if (!v.containsAll(arrayList))
				v.addAll(arrayList);
			
			//System.out.println("Vector: " + v.toString());
			
			// get the related according to p pointer
			List < ISynsetID > related =  synset.getRelatedSynsets(p);
			int rel_size = related.size();
			
			/*if (rel_size > 1)
				System.out.println(" **************************  Related Size: " + related.size());
			else
				System.out.println("Related Size: " + related.size());*/
			
			if (rel_size > 0)
			{
				for(ISynsetID sid : related)
				{
					 ISynset sy = jwiwrapper.dictionary.getSynset(sid);
					 exploreWord(sy, p, --level, v);
					 level++;
				}
			}
			//System.out.println("End of Level " + (Integer.MAX_VALUE - level));
		}
	}
	
	static void exploreHyperHypo(ISynset synset, int level, Vector<String> v)
	{
		exploreWord(synset, Pointer.HYPERNYM, level, v);
		exploreWord(synset, Pointer.HYPONYM, level, v);
	}
	
	static void exploreSuperClasses(OntClass c, int level, Vector<String> v)
	{
		String classLocal = c.getLocalName();
		if (!v.contains(classLocal))
			v.add(classLocal);
		
		if (level > 0)
		{
			if (c.hasSuperClass())
			{
				ExtendedIterator<OntClass> superClassIter = c.listSuperClasses(true);
				
				while (superClassIter.hasNext())
				{
					OntClass superClass = superClassIter.next();
					exploreSuperClasses(superClass, --level, v);
				}
			}
		}
	}
	
	static ArrayList<String> getArrayList(ISynset s)
	{
		ArrayList<String> arrayList = new ArrayList<String>();
		
		List <IWord > words = s.getWords();
		for( Iterator <IWord > iter = words.iterator(); iter.hasNext();)
		{
			arrayList.add(iter.next().getLemma());
		}
		
		return arrayList;
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
}
