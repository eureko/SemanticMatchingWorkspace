/* 
 * Test1.java
 * Contains two usefull functions: the first one explores the Wordnet hypernyms/hyponims hierarchy starting from a synset passed by parameter 
 * while the second one retrieves all the related of given synset digging into the level passed as innput. 
 * 
 * 
 * Author: Enrico G. Caldarola (enricogiacinto.caldarola@unina.it)
 */

package it.unina.egc.SemanticMatchingWorkspace.test;

import it.unina.egc.SemanticMatchingWorkspace.utils.ComparableIWord;
import it.unina.egc.SemanticMatchingWorkspace.utils.JWIWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

public class Test1 
{
	static JWIWrapper jwiwrapper;
	
	public static void main(String[] args)
	{
		try 
		{
			jwiwrapper = new JWIWrapper();
			Iterator<IIndexWord> wordIterator = jwiwrapper.dictionary.getIndexWordIterator(POS.NOUN);
			Vector<Vector<String>> wordArray = new Vector<Vector<String>>();
			
			
			int counter = 0;
			while(wordIterator.hasNext()) 
			{
				IIndexWord indexWord = wordIterator.next();
				List<IWordID> wordIDs = indexWord.getWordIDs();
				
				for (IWordID wid : wordIDs)
				{
					IWord word = jwiwrapper.dictionary.getWord(wid);
					Vector<String> v = new Vector<String>();
					
					//exploreWord(word.getSynset(), Pointer.HYPONYM, Integer.MAX_VALUE, v);
					holisticallyExploreWord(word.getSynset(), 3, v);
					//advancedHolisticallyExploreWord(word.getSynset(), Integer.MAX_VALUE, v);
					//exploreHyperHypo(word.getSynset(), Integer.MAX_VALUE, v);
					//System.out.println(word.getLemma() + " --> " + v);
					
					wordArray.add(v);
					
					if (((counter++)%100)==0)
						System.out.println("Read " + counter + " words.");
					
					/*if (word.getLemma().compareTo("anjou")==0)
						System.exit(0);*/
				}
			}
			
			//Serialize wordMap
			
			wordArray.trimToSize();
			
		   ObjectOutputStream oos = new ObjectOutputStream( 
                   new FileOutputStream(new File("export/WordNetLexicalChainsHolisticLevel3.obj")));

			oos.writeObject(wordArray);
			// close the writing.
			oos.close();
			
			System.out.println("export/WordNetLexicalChainsHolisticLevel3.obj serialized");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void exploreWord(ISynset synset, IPointer p, int level, Vector<String> v)
	{
		if (level >= 0)
		{
			//System.out.println("Start of level: " + (Integer.MAX_VALUE - level) + " Synset: " + synset.getWords());
			
			ArrayList<String> arrayList = getArrayList(synset);
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
	
	static void holisticallyExploreWord(ISynset synset, int level, Vector<String> v)
	{
		if (level >= 0)
		{
			ArrayList<String> arrayList = getArrayList(synset);
			if (!v.containsAll(arrayList))
				v.addAll(arrayList);
			
			List < ISynsetID > related =  synset.getRelatedSynsets();
			int rel_size = related.size();
			
			if (rel_size > 0)
			{
				for( ISynsetID sid : related)
				{
					 ISynset sy = jwiwrapper.dictionary.getSynset(sid);
					 holisticallyExploreWord(sy, --level, v);
					 level++;
				}
			}
		}
	}
	
	static void advancedHolisticallyExploreWord(ISynset synset, int level, Vector<String> v)
	{
		if (level >= 0)
		{
			// get the related according to p pointer
			Map<IPointer, List <ISynsetID>> relatedMap =  synset.getRelatedMap();
			
			int rel_size = relatedMap.size();
			
			if (rel_size > 0)
			{
				String s = relatedMap.toString();
				if (!v.contains(s))
						v.add(s);
				
				for (Map.Entry<IPointer, List <ISynsetID>> entry : relatedMap.entrySet()) 
				{
					IPointer key = entry.getKey();
					List <ISynsetID> list = entry.getValue();
					
					for( ISynsetID sid : list)
					{
						 ISynset sy = jwiwrapper.dictionary.getSynset(sid);
						 advancedHolisticallyExploreWord(sy, --level, v);
						 level++;
					}
				}
			}
		}
	}
	
	static void exploreHyperHypo(ISynset synset, int level, Vector<String> v)
	{
		exploreWord(synset, Pointer.HYPERNYM, level, v);
		exploreWord(synset, Pointer.HYPONYM, level, v);
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
	
	
}
