package it.polito.tdp.model;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.db.EventsDao;
import it.polito.tdp.db.PesoVertice;

public class Model {
	
	private EventsDao dao;
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private List<Integer> distretti = new LinkedList<>();
	
	public Model() {
		this.dao = new EventsDao();
		this.grafo = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	}

	public List<Integer> getAnni() {
		return this.dao.getAnni();
	}
	
	public void creaGrafo(Integer anno) {
		//aggiungo i vertici 
		this.distretti = dao.getDistretti(anno);
		Graphs.addAllVertices(this.grafo, this.distretti);
		//doppio ciclo for per gli archi
		for(Integer v1: this.grafo.vertexSet()) {
			for(Integer v2: this.grafo.vertexSet()) {
				if(!v1.equals(v2)){
					if(this.grafo.getEdge(v1, v2)==null) {
					
					Double latMediaV1 = dao.getLatMedia(anno, v1); 
					Double lonMediaV1 = dao.getLonMedia(anno, v1); 
					Double latMediaV2 = dao.getLatMedia(anno, v2); 
					Double lonMediaV2 = dao.getLonMedia(anno, v2); 
					
					Double distanzaMedia = LatLngTool.distance(
							new LatLng(latMediaV1, lonMediaV1),
							new LatLng(latMediaV2, lonMediaV2),
							LengthUnit.KILOMETER);
					//aggiungo l'arco con il peso specifico calcolato
					Graphs.addEdge(this.grafo, v1, v2, distanzaMedia);
					
					}
				}
			}
		}
		System.out.println("Grafo creato!");
		System.out.println("#vertici: "+grafo.vertexSet().size());
		System.out.println("#archi: "+grafo.edgeSet().size());
		
	}
	
	public int getNvertici() {
		return this.grafo.vertexSet().size();
	}
	public int getNarchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<PesoVertice> getVicini(Integer distretto){
		List<Integer> neighbors = Graphs.neighborListOf(this.grafo,  distretto);
		List<PesoVertice> vicini = new LinkedList<>();
		
		for(Integer n: neighbors) {
			vicini.add(new PesoVertice(this.grafo.getEdgeWeight(this.grafo.getEdge(distretto, n)), n));
		}
		Collections.sort(vicini);
		return vicini;
	}
	
	public List<Integer> getDistretti(){
		return this.distretti;
	}
	
	public List<Integer> getMesi() {
		List<Integer> mesi = new LinkedList<Integer>();
		for(int i = 1; i<=12; i++)
			mesi.add(i);
		return mesi;
	}
	
	public List<Integer> getGiorni() {
		List<Integer> giorni = new LinkedList<Integer>();
		for(int i = 1; i<=31; i++)
			giorni.add(i);
		return giorni;
	}
	
	public int simula(Integer anno, Integer mese, Integer giorno, Integer N) {
		Simulatore sim = new Simulatore();
		sim.init(N, anno, mese, giorno, grafo);
		return sim.run();
	}
}
