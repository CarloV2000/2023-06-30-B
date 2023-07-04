package it.polito.tdp.exam.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.exam.db.BaseballDAO;

public class Model {
	
	private List<Team>allTeams;
	private BaseballDAO dao;
	private List<Integer>allAnni;
	private Graph<Integer, DefaultWeightedEdge>grafo;
	private Map<String, Team>nameTeamMap;

	public Model() {
		this.dao = new BaseballDAO();
		this.allTeams = new ArrayList<>(dao.readAllTeams());
		this.allAnni = new ArrayList<>();
		this.nameTeamMap = new HashMap<>();
		for(Team t : allTeams) {
			this.nameTeamMap.put(t.getName(), t);
		}
	}

	public List<Team> getAllTeams() {
		return allTeams;
	}
	
	public String creaGrafo(String teamName) {
		Team t = this.nameTeamMap.get(teamName);
		
		this.grafo = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		this.allAnni = dao.readAllAnni(teamName);
		Graphs.addAllVertices(grafo, this.allAnni);
		
		for(Integer x : allAnni) {
			for(Integer y : allAnni) {
				if(!x.equals(y)) {
					Integer peso = 0; 
					Integer SalarioX = dao.getSalarioNellAnno(x, t);
					Integer SalarioY = dao.getSalarioNellAnno(y, t);
					if(SalarioX >= SalarioY) {
						peso = SalarioX-SalarioY;
					}
					else if(SalarioY > SalarioX) {
						peso = SalarioY-SalarioX;
					}
					Graphs.addEdge(grafo, x, y, peso);
				}
			}
		}
		return "Grafo creato con "+grafo.vertexSet().size()+"vertici e "+grafo.edgeSet().size()+" archi";
	}
	
	public List<CoppiaA>dettagli(Integer a1){
		List<CoppiaA>res = new ArrayList<>();
		List<Integer>adiacenti = new ArrayList<>(Graphs.neighborListOf(grafo, a1));
		for(Integer x : adiacenti) {
			DefaultWeightedEdge e = grafo.getEdge(a1, x);
			Integer peso = (int) grafo.getEdgeWeight(e);
			CoppiaA c = new CoppiaA(a1, x, peso);
			res.add(c);
		}
		Collections.sort(res);
		return res;
	}

	public List<Integer> getAllAnni() {
		return allAnni;
	}

	public Graph<Integer, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	public Map<String, Team> getNameTeamMap() {
		return nameTeamMap;
	}
	
}
