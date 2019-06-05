package it.polito.tdp.model;

import java.time.Year;

public class Anno {
	
	private Year anno;

	public Anno(Year anno) {
		super();
		this.anno = anno;
	}

	public Year getAnno() {
		return anno;
	}

	@Override
	public String toString() {
		return "" + anno ;
	}
	
	

}
