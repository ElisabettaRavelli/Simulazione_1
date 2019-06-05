package it.polito.tdp.model;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.db.EventsDao;

public class Model {
	
	private EventsDao dao;
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private List<Integer> distretti;
	
	public Model() {
		this.dao = new EventsDao();
	}

	public List<Anno> getAnni() {
		return this.dao.getAnni();
	}
	
	public void creaGrafo() {
		this.grafo = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.distretti = dao.getDistretti();
		Graphs.addAllVertices(this.grafo, this.distretti);
	}
	
}
