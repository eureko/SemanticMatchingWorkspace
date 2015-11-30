/* 
 * Test2.java
 * Contains two usefull functions: the first one explores the DBpedia TBOX classes hierarchy starting from a synset passed by parameter 
 * while the second one retrieves all the related of given synset digging into the level passed as innput. 
 * 
 * 
 * Author: Enrico G. Caldarola (enricogiacinto.caldarola@unina.it)
 */

package it.unina.egc.SemanticMatchingWorkspace.test;

import java.io.InputStream;
import java.util.Vector;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL;


public class Test2 
{
	static final String  DBpediaTBOXFile = "../Resources/DBpedia/dbpedia_2014.owl";
	
	public static void main(String[] args) 
	{
		OntModel ontoModel = getOntologyModel(DBpediaTBOXFile);
		ontoModel.setStrictMode(false);
		ExtendedIterator<OntClass> classesIter = ontoModel. listNamedClasses();

		Vector<String> v;
		
		while(classesIter.hasNext())
		{
			OntClass c = classesIter.next();
			
			v = exploreSuperClasses(c, Integer.MAX_VALUE);
			//v = exploreSubClasses(c, Integer.MAX_VALUE);
			//v = holisticallyExploreClass(c, 1);
			//v = exploreAllSuperClasses(c);
			
			System.out.println(c.getLocalName() + " --> " + v);
		}
	}
	
	
	static Vector<String> exploreAllSuperClasses(OntClass c)
	{
		Vector<String> v = new Vector<String>();
		
		if (c.hasSuperClass())
		{
			ExtendedIterator<OntClass> superClassIter = c.listSuperClasses();
			while (superClassIter.hasNext())
			{
				v.add(superClassIter.next().getLocalName());
			}
			
		}
		
		return v;
	}
	
	static Vector<String> exploreSuperClasses(OntClass c, int level)
	{
		Vector<String> v = new Vector<String>();
		
		if (level > 0)
		{
			if (c.hasSuperClass())
			{
				ExtendedIterator<OntClass> superClassIter = c.listSuperClasses(true);
				
				while (superClassIter.hasNext())
				{
					OntClass superClass = superClassIter.next();
					v.add(superClass.getLocalName());
					v = exploreSuperClasses(superClass, --level);
					
				}
				
				v.add(c.getLocalName());
			}
			else
				v.add(OWL.Thing.getLocalName());
		}
		
		return v;
	}
	
	static Vector<String> exploreSubClasses(OntClass c, int level)
	{
		Vector<String> v = new Vector<String>();
		
		if (level > 0)
		{
			if (c.hasSubClass())
			{
				OntClass subClass = c.getSubClass();
				v = exploreSubClasses(subClass, --level);
				v.add(c.getLocalName());
			}
		}
		
		return v;
	}
	
	static Vector<String> holisticallyExploreClass(OntClass c, int level)
	{
		Vector<String> v = new Vector<String>();
		
		if (level > 0)
		{
			if (c.hasSubClass())
			{
				OntClass subClass = c.getSubClass();
				v.add(subClass.getLocalName());
				holisticallyExploreClass(subClass, --level);
			}
			
			if (c.hasSuperClass())
			{
				OntClass superClass = c.getSuperClass();
				v.add(superClass.getLocalName());
				holisticallyExploreClass(superClass, --level);
			}
			
				OntClass equivClass = c.getEquivalentClass();
				if (equivClass != null)
				{
					v.add(equivClass.getLocalName());
					holisticallyExploreClass(equivClass, --level);
				}
		}
		
		return v;
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




