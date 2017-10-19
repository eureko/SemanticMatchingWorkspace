package it.unina.egc.SemanticMatchingWorkspace.exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.TreeMap;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

public class DBpediaExporter 
{
	static final String  DBpediaTBOXFile = "../Resources/DBpedia/dbpedia_2015-04.owl";
	static TreeMap<String, Integer> classesMap = new TreeMap<String, Integer>();
	
	static String objFileName = "export/indexes/DBpediaClassesIndex.obj";
	
	public static void main(String[] args) 
	{
		
		try
		{
			// Exporting namedClasses
			FileWriter fileWriterClasses = new FileWriter("DBpedia_namedClasses_V2.csv");
			fileWriterClasses.write ("id,URI,localName,comment\n");		
			
			int namedClassId = 0;
			OntModel ontoModel = getOntologyModel(DBpediaTBOXFile);
			ontoModel.setStrictMode(false);
			ExtendedIterator<OntClass> classesIter = ontoModel.listNamedClasses();
			
			while(classesIter.hasNext())
			{
				OntClass c = classesIter.next();
				classesMap.put(c.toString(), namedClassId);
				String comment = c.getComment("EN");
				fileWriterClasses.write(namedClassId + "," + c.toString() + "," + c.getLocalName() + "," + 
						(comment == null ?"no comment":	"\"" + comment.replaceAll("\"", "\"\"") + "\"")  + "\n");
				namedClassId++;
			}
			
			
			fileWriterClasses.flush();
			fileWriterClasses.close();
			
			// Exporting semantic relations
			
			FileWriter fileWriterProp = new FileWriter("DBpedia_namedClasses_Prop_V1.csv");
			fileWriterProp.write ("Prop,Src,Dest\n");
			
			
			classesIter = ontoModel.listNamedClasses();
			
			while(classesIter.hasNext())
			{
				OntClass c = classesIter.next();
				
				StmtIterator stmtIter = c.listProperties();
				
				while(stmtIter.hasNext())
				{
					Statement s = stmtIter.next();
					Property p = s.getPredicate();
					RDFNode rdfNode = s.getObject();
					
					
					System.out.println(c + "," + p + "," + rdfNode);
					if (rdfNode.isURIResource())
					{
						if (classesMap.containsKey(rdfNode.toString()))
							fileWriterProp.write (p.getLocalName() + "," + classesMap.get(c.toString()).intValue() + "," +  classesMap.get(rdfNode.toString()).intValue() + "\n");
					}
				}
			}
			
			fileWriterProp.flush();
			fileWriterProp.close();
			
			
			//Serialize
			
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(objFileName)));

			oos.writeObject(classesMap);
			// close the writing.
			oos.close();
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
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
