package it.polito.tdp.db;

public class PesoVertice implements Comparable<PesoVertice> {
	
	private Double distanza;
	private Integer v;
	
	
	public PesoVertice(Double distanza, Integer v) {
		super();
		this.distanza = distanza;
		this.v = v;
	}
	public Double getDistanza() {
		return distanza;
	}
	public void setDistanza(Double distanza) {
		this.distanza = distanza;
	}
	public Integer getV() {
		return v;
	}
	public void setV(Integer v) {
		this.v = v;
	}
	@Override
	public int compareTo(PesoVertice o) {
		//utilizzo il metodo compareto implementato nella classe di java Double
		return this.distanza.compareTo(o.getDistanza());
	}
	
	
	
}
