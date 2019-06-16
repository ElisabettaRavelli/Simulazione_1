package it.polito.tdp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.db.EventsDao;
import it.polito.tdp.model.Evento.TipoEvento;

public class Simulatore {
	//TIPI DI EVENTO DA MODELLARE
	//1.evento criminoso--> la centrale selezionala l'agente più vicino (setta l'agente occupato)
	//2.arriva l'agente--> controlla se l'evento è mal gestito (definisco durata dell'intervento)
	//3.crimine terminato--> libero l'agente
	
	//strutture dati che ci servono
	private Integer malGestiti;
	private Integer N;
	private Integer anno;
	private Integer mese;
	private Integer giorno;
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private PriorityQueue<Evento> queue;
	
	//mappa che contiene il numero del distretto e il numero di agenti liberi
	private Map<Integer, Integer> agenti;
	
	public void init(Integer N, Integer anno, Integer mese, Integer giorno,
			Graph<Integer, DefaultWeightedEdge> grafo ) {
		this.N=N;
		this.anno=anno;
		this.mese=mese;
		this.giorno=giorno;
		this.grafo=grafo;
		
		this.malGestiti=0;
		this.agenti = new HashMap<Integer, Integer>();
		for(Integer d: this.grafo.vertexSet()) {
			this.agenti.put(d, 0);
		}
		
		//Devo scegliere dove sta la centrale
		EventsDao dao = new EventsDao();
		Integer minD = dao.getDistrettoMin(anno);
		this.agenti.put(minD, this.N);
		
		//creo la coda
		this.queue= new PriorityQueue<Evento>();
		
		for(Event e : dao.listAllEventsbyYear(this.anno, this.mese, this.giorno)) {
			queue.add(new Evento(TipoEvento.CRIMINE, e.getReported_date(), e));
		}
		
	}
	public int run() {
		Evento e;
		while((e = queue.poll()) != null) {
			switch(e.getTipo()) {
				
				case CRIMINE:
					System.out.println("NUOVO CRIMINE! " + e.getCrimine().getIncident_id());
					Integer partenza = null;
					partenza = cercaAgente(e.getCrimine().getDistrict_id());
					
					if(partenza != null) {
						//c'è un agente libero
						if(partenza.equals(e.getCrimine().getDistrict_id())) {
							//tempo di arrivo = 0
							System.out.println("AGENTE ARRIVA PER CRIMINE: " + e.getCrimine().getIncident_id());
							Long duration = getDuration(e.getCrimine().getOffense_category_id());
							this.queue.add(new Evento(TipoEvento.GESTITO,
									e.getData().plusSeconds(duration),e.getCrimine()));
							
						} else {
							Double distance = this.grafo.getEdgeWeight(this.grafo.getEdge(partenza, 
									e.getCrimine().getDistrict_id()));
							Long seconds = (long) ((distance * 1000)/(60/3.6));
							this.queue.add(new Evento(TipoEvento.ARRIVA_AGENTE,
									e.getData().plusSeconds(seconds), e.getCrimine()));	
						}
					}else{
						//nessuno libero
						System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " MAL GESTITO!!!!");
						this.malGestiti++;
					}
					break;
					
				case ARRIVA_AGENTE:
					System.out.println("AGENTE ARRIVA PER CRIMINE: " + e.getCrimine().getIncident_id());
					Long duration = getDuration(e.getCrimine().getOffense_category_id());
					this.queue.add(new Evento(TipoEvento.GESTITO,
							e.getData().plusSeconds(duration),e.getCrimine()));
					
					//controllo se il crimine è mal gestito
					if(e.getData().isAfter(e.getCrimine().getReported_date().plusMinutes(15))) {
						System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " MAL GESTITO!!!!");
						this.malGestiti++;
					}
					break;
					
				case GESTITO:
					System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " GESTITO!");
					this.agenti.put(e.getCrimine().getDistrict_id(), 
							this.agenti.get(e.getCrimine().getDistrict_id()) +1);
					break;
			}
		}
		
		System.out.println("TERMINATO!! MAL GESTITI = " + this.malGestiti);
		return this.malGestiti;
	}
	
	private Integer cercaAgente(Integer district_id) {
		Double distanza = Double.MAX_VALUE;
		Integer distretto = null;
		
		for(Integer d : this.agenti.keySet()) {// ciclo su tutti i distretti della mappa 
			
			if(this.agenti.get(d) > 0) {// se nel distretto d ci sono degli liberi
				if(district_id.equals(d)) {// se il distretto d è il distretto del crimine
					distanza = Double.valueOf(0);
					distretto = d;
				}// se il peso dell'arco tra il distretto d e il distretto del crimine è minore di distanza
				//ho un distretto migliore ovvero più vicino al distretto del crimine
				else if(this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d)) < distanza) {
					distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d));
					distretto = d;
				}
			}
		}
		return distretto;
	}

	private Long getDuration(String offense_category_id) {
		if(offense_category_id.equals("all_other_crimes")) {
			Random r = new Random();// mi permette di gestire le probabilità che avvengano determinate cose
			if(r.nextDouble() > 0.5)// 50% di probabilità 
				return Long.valueOf(2*60*60); //Long.valueOf(x) mi permette di trasformare un intero x in un long
			else
				return Long.valueOf(1*60*60);
		} else
			return Long.valueOf(2*60*60);
	}

}
