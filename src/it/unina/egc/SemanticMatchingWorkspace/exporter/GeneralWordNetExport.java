package it.unina.egc.SemanticMatchingWorkspace.exporter;

import it.unina.egc.SemanticMatchingWorkspace.utils.JWIWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

public class GeneralWordNetExport 
{
	static JWIWrapper jwiwrapper;	
	static TreeMap<String, Integer> widMap = new TreeMap<String, Integer>();
	
	static String objFileName = "export/indexes/WordNetWIDIndex.obj";
	
	public static void main(String[] args) 
	{
		try 
		{
			jwiwrapper = new JWIWrapper();
			
			// Exporting wordsenses nodes
			FileWriter fileWriterWordSenses = new FileWriter("WordNet_WordSenses_V1.csv");
			fileWriterWordSenses.write ("id,WID,POS,word,gloss\n");
			
			POS[] pos_values = POS.values();
			int id = 0;
			
			for (POS pos:pos_values)
			{
				Iterator<IIndexWord> wordIterator = jwiwrapper.dictionary.getIndexWordIterator(pos);
				
				while(wordIterator.hasNext()) 
				{
					IIndexWord indexWord = wordIterator.next();
					List<IWordID> wordIDs = indexWord.getWordIDs();
					
					for (IWordID wid : wordIDs)
					{
						IWord word = jwiwrapper.dictionary.getWord(wid);
						//System.out.println("Compare: " + word.getID().toString() + " " + wid.toString());
						fileWriterWordSenses.write(getLine(word, id) + "\n");
						widMap.put(word.getID().toString(), id);
						id++;
					}
				}
			}
			
			fileWriterWordSenses.flush();
			fileWriterWordSenses.close();
			
			// Exporting semantic relations
			
			FileWriter fileWriterProp = new FileWriter("WordNet_Prop_V1.csv");
			fileWriterProp.write ("Prop,Src,Dest\n");
			
			for (POS pos:pos_values)
			{
				Iterator<IIndexWord> wordIterator = jwiwrapper.dictionary.getIndexWordIterator(pos);
				
				while(wordIterator.hasNext()) 
				{
					IIndexWord indexWord = wordIterator.next();
					List<IWordID> wordIDs = indexWord.getWordIDs();
					
					for (IWordID wid : wordIDs)
					{
						IWord word = jwiwrapper.dictionary.getWord(wid);
						Map<IPointer, List<ISynsetID>> map = word.getSynset().getRelatedMap();
						
						for (Map.Entry<IPointer, List<ISynsetID>> entry : map.entrySet()) 
						{
							IPointer ipointer = entry.getKey();
							List<ISynsetID> synList = entry.getValue();
							
							Iterator<ISynsetID> iterator = synList.iterator();
							while (iterator.hasNext()) 
							{
								ISynsetID synsetID = iterator.next();
								List<IWord> targetWordsList = jwiwrapper.dictionary.getSynset(synsetID).getWords();
								
								for (IWord tword:targetWordsList)
								{
									try
									{
										
										fileWriterProp.write (ipointer.getName() + "," + widMap.get(word.getID().toString()).intValue() + "," + widMap.get(tword.getID().toString()).intValue() +"\n");
									}
									catch(Exception ex)
									{
										System.out.println(word.getID().toString() + " " + tword.getID().toString());
										ex.printStackTrace();
									}
								}
								
							}
						}  
						
					}
				}
			}
			
					 
			fileWriterProp.flush();
			fileWriterProp.close();
			
			//Serialize
			
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(objFileName)));

			oos.writeObject(widMap);
			// close the writing.
			oos.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	static String getLine(IWord word, int id)
	{
		String str = "" +  id + ",";
		str += word.getID().toString() + ",";
		str += word.getPOS().name() + ",";
		str += word.getLemma() + ",";
		str += "\"" + word.getSynset().getGloss().replaceAll("\"", "\"\"") + "\"";
		
		return str;
	}
}
