package it.unina.egc.SemanticMatchingWorkspace.test;

import java.io.InputStream;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

public class DBpediaAnalyzer 
{
	static final String  DBpediaTBOXFile = "../Resources/DBpedia/dbpedia_2014.owl";
	
	public static void main(String[] args) 
	{
		
		int namedClassCounter = 0;
		OntModel ontoModel = getOntologyModel(DBpediaTBOXFile);
		ontoModel.setStrictMode(false);
		ExtendedIterator<OntClass> classesIter = ontoModel.listNamedClasses();
		
		while(classesIter.hasNext())
		{
			classesIter.next();
			namedClassCounter++;
		}
		System.out.println("Named Classes count: " + namedClassCounter);
		
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
