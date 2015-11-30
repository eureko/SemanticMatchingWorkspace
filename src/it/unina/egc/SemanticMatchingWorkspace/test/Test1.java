/* 
 * Test1.java
 * Contains two usefull functions: the first one explores the Wordnet hypernyms/hyponims hierarchy starting from a synset passed by parameter 
 * while the second one retrieves all the related of given synset digging into the level passed as innput. 
 * 
 * 
 * Author: Enrico G. Caldarola (enricogiacinto.caldarola@unina.it)
 */

package it.unina.egc.SemanticMatchingWorkspace.test;

import it.unina.egc.SemanticMatchingWorkspace.core.JWIWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
			
			while(wordIterator.hasNext()) 
			{
				IIndexWord indexWord = wordIterator.next();
				List<IWordID> wordIDs = indexWord.getWordIDs();
				
				for (IWordID wid : wordIDs)
				{
					IWord word = jwiwrapper.dictionary.getWord(wid);
					Vector<String> v = new Vector<String>();
					exploreWord(word.getSynset(), Pointer.HYPERNYM, Integer.MAX_VALUE, v);
					//holisticallyExploreWord(word.getSynset(), 2, v);
					//advancedHolisticallyExploreWord(word.getSynset(), 3, v);
					System.out.println(word.getLemma() + " --> " + v);
					
					/*if (word.getLemma().compareTo("anjou")==0)
						System.exit(0);*/
				}
			}
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
	
	static Vector<String> holisticallyExploreWord(ISynset synset, int level, Vector<String> v)
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
		return v;
	}
	
	static Vector<String> advancedHolisticallyExploreWord(ISynset synset, int level, Vector<String> v)
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
				
				for (Map.Entry<IPointer, List <ISynsetID>> entry : relatedMap.entrySet()) {
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
		return v;
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
