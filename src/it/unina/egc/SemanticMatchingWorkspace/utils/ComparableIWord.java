package it.unina.egc.SemanticMatchingWorkspace.utils;

import java.io.Serializable;

import edu.mit.jwi.item.IWordID;

public class ComparableIWord implements Comparable<ComparableIWord>, Serializable
{
	/**
	 *  
	 *	
	 * 
	 */
	
	
	private static final long serialVersionUID = 1L;
	IWordID iWordID;
	
	public ComparableIWord(ComparableIWord iWordID) {
		// TODO Auto-generated constructor stub
	}
	
	public ComparableIWord(IWordID iWordID) {
		// TODO Auto-generated constructor stub
		this.iWordID = iWordID;
	}

	@Override
	public int compareTo(ComparableIWord o) {
		// TODO Auto-generated method stub
		return iWordID.toString().compareTo(o.iWordID.toString());
	}



}