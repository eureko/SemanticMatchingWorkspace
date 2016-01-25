package it.unina.egc.SemanticMatchingWorkspace.test;

import it.unina.egc.SemanticMatchingWorkspace.utils.JWIWrapper;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISenseEntry;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

public class WordNetAnalyzer 
{
	static JWIWrapper jwiwrapper;
	public static int synsetLevel = 0;
	
	public static void main(String[] args) 
	{
		try
		{
			jwiwrapper = new JWIWrapper();
			
			Iterator<IIndexWord> nounIterator = jwiwrapper.dictionary.getIndexWordIterator(POS.NOUN);
			Iterator<IIndexWord> verbIterator = jwiwrapper.dictionary.getIndexWordIterator(POS.VERB);
			Iterator<IIndexWord> adverbIterator = jwiwrapper.dictionary.getIndexWordIterator(POS.ADVERB);
			Iterator<IIndexWord> adjectiveIterator = jwiwrapper.dictionary.getIndexWordIterator(POS.ADJECTIVE);
			
			int noun_counter = 0;
			int noun_index_counter = 0;
			while(nounIterator.hasNext())
			{
				IIndexWord indexWord = nounIterator.next();
				List<IWordID> wordIDs = indexWord.getWordIDs();
				noun_index_counter++;
				
				for (IWordID wid : wordIDs)
				{
					//IWord word = jwiwrapper.dictionary.getWord(wid);
					noun_counter++;
				}
			}
			
			int verb_counter = 0;
			int verb_index_counter = 0;
			while(verbIterator.hasNext())
			{
				IIndexWord indexWord = verbIterator.next();
				List<IWordID> wordIDs = indexWord.getWordIDs();
				
				verb_index_counter++;
				
				for (IWordID wid : wordIDs)
				{
					//IWord word = jwiwrapper.dictionary.getWord(wid);
					verb_counter++;
				}
			}
			
			int adverb_counter = 0;
			
			int adverb_index_counter = 0;
			while(adverbIterator.hasNext())
			{
				IIndexWord indexWord = adverbIterator.next();
				List<IWordID> wordIDs = indexWord.getWordIDs();
				
				System.out.println(indexWord.getLemma() + ": " + indexWord.getTagSenseCount());
				adverb_index_counter++;
				
				for (IWordID wid : wordIDs)
				{
					IWord word = jwiwrapper.dictionary.getWord(wid);
					adverb_counter++;
				}
			}
			
			int adjective_counter = 0;
			int adjective_index_counter = 0;
			while(adjectiveIterator.hasNext())
			{
				IIndexWord indexWord = adjectiveIterator.next();
				List<IWordID> wordIDs = indexWord.getWordIDs();
				
				adjective_index_counter++;
				
				for (IWordID wid : wordIDs)
				{
					//IWord word = jwiwrapper.dictionary.getWord(wid);
					adjective_counter++;
				}
			}
			
			Iterator<ISenseEntry> senseIter = jwiwrapper.dictionary.getSenseEntryIterator();
			
			
			
			int sense_count = 0;
			
			while(senseIter.hasNext())
			{
				ISenseEntry sense = senseIter.next();
				//System.out.println(sense.getSenseKey().getLemma());
//				System.out.println(sense);
				sense_count++;
			}
			
			Iterator<ISynset> nounISynsetIter = jwiwrapper.dictionary.getSynsetIterator(POS.NOUN);
			
			int noun_synset_count = 0;
			while (nounISynsetIter.hasNext())
			{
				ISynset synset = nounISynsetIter.next();
				
				
				
				
				noun_synset_count++;
			}
			
			Iterator<ISynset> verbISynsetIter = jwiwrapper.dictionary.getSynsetIterator(POS.VERB);
			
			int verb_synset_count = 0;
			while (verbISynsetIter.hasNext())
			{
				verbISynsetIter.next();
				verb_synset_count++;
			}
			
			Iterator<ISynset> adverbISynsetIter = jwiwrapper.dictionary.getSynsetIterator(POS.ADVERB);
			
			int adverb_synset_count = 0;
			while (adverbISynsetIter.hasNext())
			{
				adverbISynsetIter.next();
				adverb_synset_count++;
			}
			
			Iterator<ISynset> adjectiveISynsetIter = jwiwrapper.dictionary.getSynsetIterator(POS.ADJECTIVE);
			
			int adjective_synset_count = 0;
			while (adjectiveISynsetIter.hasNext())
			{
				adjectiveISynsetIter.next();
				adjective_synset_count++;
			}
			
			
			
			System.out.println("Wordnet dictionary version: " + jwiwrapper.dictionary.getVersion());
			System.out.println("IWord Noun count: " + noun_counter + ". IndexWord Noun count: " + noun_index_counter);
			System.out.println("IWord Verb count: " + verb_counter + ". IndexWord Verb count: " + verb_index_counter);
			System.out.println("IWord Adverb count: " + adverb_counter + ". IndexWord Adverb count: " + adverb_index_counter);
			System.out.println("IWord Adjective count: " + adjective_counter + ". IndexWord Adjective count: " + adjective_index_counter);
			System.out.println("IWord total count: " + (noun_counter + verb_counter + adverb_counter + adjective_counter) + ". IndexWord total count: " + (noun_index_counter + verb_index_counter +  adverb_index_counter + adjective_index_counter));
			System.out.println("ISense count: " + sense_count);
			System.out.println("Noun synset count: " + noun_synset_count);
			System.out.println("Verb synset count: " + verb_synset_count);
			System.out.println("Adverb synset count: " + adverb_synset_count);
			System.out.println("Adjective synset count: " + adjective_synset_count);
			System.out.println("Synset total count: " + (noun_synset_count + verb_synset_count + adverb_synset_count + adjective_synset_count));
			
			
		}
		catch(Exception ex)
		{
			
		}
	}
	
	
	
	
}
