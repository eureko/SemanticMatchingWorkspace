package it.unina.egc.SemanticMatchingWorkspace.test;

import it.unina.egc.SemanticMatchingWorkspace.core.JWIWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.mit.jwi.item.IIndexWord;
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
				
				Vector<String> v;
				
				for (IWordID wid : wordIDs)
				{
					IWord word = jwiwrapper.dictionary.getWord(wid);
					v = exploreWord(word.getSynset(), Pointer.HYPERNYM, Integer.MAX_VALUE);
					//v = holisticallyExploreWord(word.getSynset(), 10);
					
					
					System.out.println(word.getLemma() + " --> " + v);
				}
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static Vector<String> exploreWord(ISynset synset, Pointer p, int level)
	{
		Vector<String> v = new Vector<String>();
		if (level > 0)
		{
			// get the related according to p pointer
			List < ISynsetID > related =  synset.getRelatedSynsets(p);
			
			
			for( ISynsetID sid : related)
			{
				 ISynset sy = jwiwrapper.dictionary.getSynset(sid);
				 	v =  exploreWord(sy, p, --level);
				 	v.addAll(getArrayList(sy));
				
			}
		}
		return v;
	}
	
	static Vector<String> holisticallyExploreWord(ISynset synset, int level)
	{
		Vector<String> v = new Vector<String>();
		if (level > 0)
		{
			// get the related according to p pointer
			List < ISynsetID > related =  synset.getRelatedSynsets();
			
			for( ISynsetID sid : related)
			{
				 ISynset sy = jwiwrapper.dictionary.getSynset(sid);
				 v.addAll(getArrayList(sy));
				 holisticallyExploreWord(sy, --level);
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
	

	//========== LEGACY CODE ==========
	/*static Vector<String> exploreWord(IWord word, Pointer p, int level)
	{
		Vector<String> v = new Vector<String>();
		if (level > 0)
		{
			ISynset synset = word.getSynset();
			
			// get the related according to p pointer
			List < ISynsetID > related =  p!= null ? synset.getRelatedSynsets(p) : synset.getRelatedSynsets();
			
			List <IWord > words;
			for( ISynsetID sid : related)
			{
				words = jwiwrapper.dictionary.getSynset(sid).getWords();
				for( Iterator <IWord > iter = words.iterator(); iter.hasNext();)
				{
					IWord w = iter.next();
					v =  exploreWord(w, p, --level);
					v.add(w.getLemma());
				}
			}
		}
		return v;
	}*/
}




