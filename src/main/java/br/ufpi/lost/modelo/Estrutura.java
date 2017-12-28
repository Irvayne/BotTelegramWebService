package br.ufpi.lost.modelo;

import java.util.ArrayList;
import java.util.List;

public class Estrutura {
	
	private String nomeMunicipio;
	private String municipio;
	private int idMunicipio;
	private List<Tipo> tokens;
	
	public Estrutura(String municipio, int idMunicipio) {
		this.municipio = municipio;
		this.idMunicipio = idMunicipio;
		this.tokens = new ArrayList<Tipo>();
	}
	
	public String getMunicipio() {
		return municipio;
	}
	public int getIdMunicipio() {
		return idMunicipio;
	}
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}
	public void setIdMunicipio(int idMunicipio) {
		this.idMunicipio = idMunicipio;
	}
	public List<Tipo> getTokens() {
		return tokens;
	}
	public void setTokens(List<Tipo> tokens) {
		this.tokens = tokens;
	}

	public String getNomeMunicipio() {
		return nomeMunicipio;
	}

	public void setNomeMunicipio(String nomeMunicipio) {
		this.nomeMunicipio = nomeMunicipio;
	}

}
