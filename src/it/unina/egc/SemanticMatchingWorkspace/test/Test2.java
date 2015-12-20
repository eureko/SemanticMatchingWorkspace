/* 
 * Test2.java
 * Contains useful functions to navigate the DBpedia Schema ontology and store the explored class local names in a vector
 * passed as input parameter
 * 
 * 
 * Author: Enrico G. Caldarola (enricogiacinto.caldarola@unina.it)
 */

package it.unina.egc.SemanticMatchingWorkspace.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;


public class Test2 
{
	static final String  DBpediaTBOXFile = "../Resources/DBpedia/dbpedia_2014.owl";
	
	static final String objFileName = "export/DBpediaClassNameLexicalChainsOfSomeRelatedClasses_level_10.obj";
	
	public static void main(String[] args) 
	{
		OntModel ontoModel = getOntologyModel(DBpediaTBOXFile);
		ontoModel.setStrictMode(false);
		ExtendedIterator<OntClass> classesIter = ontoModel. listNamedClasses();
		
		Vector<Vector<String>> wordArray = new Vector<Vector<String>>();
		
		
		int counter = 0;
		while(classesIter.hasNext())
		{
			OntClass c = classesIter.next();
			Vector<String> v = new Vector<String>();
			
			//exploreSuperClasses(c, 0, v);
			//exploreSubClasses(c, Integer.MAX_VALUE, v);
			//exploreSuperSubClasses(c, Integer.MAX_VALUE, v);
			//exploreEquivClasses(c, Integer.MAX_VALUE, v);
			holisticallyExploreClass(c, 10, v);
			
			//System.out.println(c.getLocalName() + " --> " + v);
			
			wordArray.add(v);
			
			
			if (((counter++)%10)==0)
				System.out.println("Read " + counter + " classes.");
		}
		
		wordArray.trimToSize();
		
		try
		{
		   ObjectOutputStream oos = new ObjectOutputStream( 
	            new FileOutputStream(new File(objFileName)));
	
			oos.writeObject(wordArray);
			// close the writing.
			oos.close();
			
			System.out.println(objFileName);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
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
	
	static void exploreSubClasses(OntClass c, int level, Vector<String> v)
	{
		String classLocal = c.getLocalName();
		if (!v.contains(classLocal))
			v.add(classLocal);
		
		if (level > 0)
		{
			if (c.hasSubClass())
			{
				ExtendedIterator<OntClass> subClassIter = c.listSubClasses(true);
				
				while (subClassIter.hasNext())
				{
					OntClass nextClass = subClassIter.next();
					exploreSubClasses(nextClass, --level, v);
				}
			}
		}
	}
	
	static void exploreSuperSubClasses(OntClass c, int level, Vector<String> v)
	{
		exploreSubClasses(c, level, v);
		exploreSuperClasses(c, level, v);
	}
	
	static void exploreEquivClasses(OntClass c, int level, Vector<String> v)
	{
		String classLocal = c.getLocalName();
		if (!v.contains(classLocal))
			v.add(classLocal);
		
		if (level > 0)
		{
			ExtendedIterator<OntClass> equivClassIter = c.listEquivalentClasses();
			while (equivClassIter.hasNext())
			{
				OntClass nextClass = equivClassIter.next();
				exploreEquivClasses(nextClass, level, v);
			}
		}
	}
	
	static void holisticallyExploreClass(OntClass c, int level, Vector<String> v)
	{
		String classLocal = c.getLocalName();
		if (!v.contains(classLocal))
		{
			v.add(classLocal);
	    
			if (level > 0)
			{
				Set<OntClass> set = new HashSet<OntClass>();
				
				if (c.hasSuperClass())
				{
					ExtendedIterator<OntClass> superClassIter = c.listSuperClasses(true);
					set.addAll(superClassIter.toSet());
				}
				
				if (c.hasSubClass())
				{
					ExtendedIterator<OntClass> subClassIter = c.listSubClasses(true);
					set.addAll(subClassIter.toSet());
				}
				
				ExtendedIterator<OntClass> equivClassIter = c.listEquivalentClasses();
				set.addAll(equivClassIter.toSet());
				
			    List < OntClass > related  = new ArrayList<OntClass>(set);
			    
				for(OntClass currentClass : related)
				{
					holisticallyExploreClass(currentClass, --level, v);
					 level++;
				}
			}
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
}
