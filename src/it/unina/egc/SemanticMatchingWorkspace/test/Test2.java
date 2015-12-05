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
import java.util.Vector;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;


public class Test2 
{
	static final String  DBpediaTBOXFile = "../Resources/DBpedia/dbpedia_2014.owl";
	
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
			
			//exploreSuperClasses(c, Integer.MAX_VALUE, v);
			//exploreSubClasses(c, Integer.MAX_VALUE, v);
			//exploreEquivClasses(c, Integer.MAX_VALUE, v);
			holisticallyExploreClass(c, Integer.MAX_VALUE, v);
			
			System.out.println(c.getLocalName() + " --> " + v);
			
			wordArray.add(v);
			
			
			if (((counter++)%10)==0)
				System.out.println("Read " + counter + " classes.");
		}
		
		wordArray.trimToSize();
		
		try
		{
		   ObjectOutputStream oos = new ObjectOutputStream( 
	            new FileOutputStream(new File("export/DBpediaLexicalChainsHolistic.obj")));
	
			oos.writeObject(wordArray);
			// close the writing.
			oos.close();
			
			System.out.println("export/DBpediaLexicalChainsHolistic.obj serialized");
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
			v.add(classLocal);
		
		if (level > 0)
		{
			if (c.hasSubClass())
			{
				exploreSubClasses(c, level, v);
			}
			
			if (c.hasSuperClass())
			{
				exploreSuperClasses(c, level, v);
			}
			
			exploreEquivClasses(c, level, v);
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
