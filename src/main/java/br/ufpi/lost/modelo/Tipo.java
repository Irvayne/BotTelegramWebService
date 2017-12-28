package br.ufpi.lost.modelo;

public class Tipo {
	
	private String nome;
	private String token;
	
	public Tipo(String nome, String token) {
		super();
		this.nome = nome;
		this.token = token;
	}

	public String getNome() {
		return nome;
	}

	public String getToken() {
		return token;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
