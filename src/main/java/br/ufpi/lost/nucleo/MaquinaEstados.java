package br.ufpi.lost.nucleo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import br.ufpi.lost.modelo.Estrutura;
import br.ufpi.lost.modelo.Tipo;

public class MaquinaEstados {
	
	 private final static String url_credenciados = "https://#########################################";
	 private final static String url_redes = "https://#########################################";
	 private final static String url_municipios = "https://#########################################";

	/**
	 * Processa a maquina de estados da aplicacao.
	 * Foram definidos 2 estados. No primeiro o usuario deve informar o nome do municipio. No segundo ele deve informar 
	 * as opcoes relacionadas ao tipo de credenciamento.
	 * @param session - objeto que mantem os usuarios na sessao
	 * @param chat_id - id do chat para salvar na sessao
	 * @param conteudo_msg - conteudo da mensagem
	 * @param nome_usuario - nome do usuario da mensagem
	 * @param nome_municipio - nome do municipio caso seja o primeiro estado 
	 * @return - texto com a resposta para enviar ao usuario
	 * @throws IOException
	 */
	public static String processar(Map<Integer, Estrutura> session, int chat_id, String conteudo_msg, String nome_usuario, String nome_municipio) throws IOException {

		Estrutura estrutura = session.get(chat_id);
		String resposta = "";
		
		if(estrutura != null) {
			if(conteudo_msg.equals("0")) {
				session.remove(chat_id);
				resposta +="Olá "+nome_usuario+",\nDigite um município:";
			}else {

				try {
					int parseInt = Integer.parseInt(conteudo_msg);
					String token = estrutura.getTokens().get(parseInt - 1).getToken();
					//requisicao passando o token e o id do municipio
					
					resposta += buscarCredenciadosPorMunicipioERede(estrutura.getIdMunicipio(), token);
					String resp = "\n\nLegal "+nome_usuario+", agora escolha a opção para "+estrutura.getNomeMunicipio()+":\n";
					
					for (int i = 0; i < estrutura.getTokens().size(); i++) {
						resp += i+1 +" - "+estrutura.getTokens().get(i).getNome()+"\n";
					}
					
					resposta += resp +"0 - Voltar";
				
				}catch (Exception e) {
					System.out.println(e.getMessage());
					resposta += nome_usuario+" você digitou um valor INVÁLIDO!!!\n"
							+ "Preste atenção nas opções disponíveis";

					String resp = "\n\n"+nome_usuario+", essas são as opções disponíveis para "+estrutura.getNomeMunicipio()+":\n";
					for (int i = 0; i < estrutura.getTokens().size(); i++) {
						resp += i+1 +" - "+estrutura.getTokens().get(i).getNome()+"\n";
					}
					resposta += resp +"0 - Voltar";

				}


			}
		} else {

			if(conteudo_msg.equals("/start")) {
				resposta +="Olá "+nome_usuario+", eu sou o bot da Infoway. Estou aqui para ajudar!\n"
						+ "Por favor, escolha um município:";

				return resposta;
			}

			estrutura = gerarObjetoEstrutura(conteudo_msg);

			if(estrutura != null) {
				estrutura.setNomeMunicipio(nome_municipio);
				session.put(chat_id, estrutura);
				String resp = "Muito bem "+nome_usuario+", escolha a opção que você deseja consultar para "+estrutura.getNomeMunicipio()+":\n";
				for (int i = 0; i < estrutura.getTokens().size(); i++) {
					resp += i+1 +" - "+estrutura.getTokens().get(i).getNome()+"\n";
				}
				resposta += resp+"0 - Voltar";
			}else {
				resposta += "Amigo "+nome_usuario+", o município '"+nome_municipio+"' não foi identificado em nosso sistema!\n"
						+ "\nPor favor, digite novamente:";
			}

		}


		return resposta;
	}

	/**
	 * Busca os credenciados de uma determinado municipio e tipo de rede
	 * @param idMunicipio - Id do municipio
	 * @param tokenRede - token com o nome da rede
	 * @return - texto com a lista de credenciados
	 * @throws IOException
	 */
	private static String buscarCredenciadosPorMunicipioERede(int idMunicipio, String tokenRede) throws IOException {
		String url = url_credenciados + "&nome_rede="+tokenRede+"&municipio="+idMunicipio;

		StringBuffer response = realizaRequisicaoParaWebService(url);

		String resposta = " -  Rede Credenciada\n\n";

		JSONArray responses = new JSONArray(response.toString());
		
		for(int i = 0; i < responses.length(); i++) {
			JSONArray object = (JSONArray) responses.get(0);

			JSONObject object2 = (JSONObject) object.get(1);

			for (Object object3 : object2.keySet()) {
				Object object4 = object2.get((String) object3);


				JSONArray object5 = (JSONArray) object4;

				for(int j = 0; j< object5.length(); j++) {
					JSONObject object6 = (JSONObject) ((JSONArray) object4).get(j);

					String name = (String) object6.get("name");
					String phone = (String) object6.get("phone");


					resposta += "Especialidade: "+object3.toString()+"\nNome: "+name+"\nTelefone:"+phone+"\n\n";
				}

			}

		}
		return resposta;
	}
	/**
	 * Realiza requisicao para listar todos os tipos de redes de um determinado municipio
	 * @param municipio - nome do municipio
	 * @return - objeto estrutura
	 * @throws IOException
	 */
	private static Estrutura gerarObjetoEstrutura(String municipio) throws IOException {
		String url = url_redes;
		
		Estrutura estrutura = null;
		ArrayList<Tipo> tokens = new ArrayList<Tipo>();

		StringBuffer response = realizaRequisicaoParaWebService(url);

		JSONArray responses = new JSONArray(response.toString());
		for(int i = 0; i < responses.length(); i++) {
			JSONObject message = responses
					.getJSONObject(i);
			String token = message.getString("token");
			String name = message.getString("name");

			Map<Integer, String> municipios = buscarMunicipioPorTokenRede(token);

			for (Integer integer : municipios.keySet()) {
				String nomeMunicipio = municipios.get(integer).replaceAll(" ","").toLowerCase();
				if(nomeMunicipio.equals(municipio)) {
					estrutura = new Estrutura(nomeMunicipio, integer);
					tokens.add(new Tipo(name, token));
				}
			}
		}
		
		if(estrutura != null)
			estrutura.setTokens(tokens);
		
		return estrutura;
	}

	/**
	 * Busca todos os municipios de um determinado token que representa um tipo de rede
	 * @param token - token da rede
	 * @return - um hash map com o id do municipio e o nome do municipio
	 * @throws IOException
	 */
	private static Map<Integer, String> buscarMunicipioPorTokenRede(String token) throws IOException { 
		String url = url_municipios + "&nome_rede="+token;
		HashMap<Integer,String> map = new HashMap<Integer, String>();
		
		StringBuffer response = realizaRequisicaoParaWebService(url);
		

		JSONArray responses = new JSONArray(response.toString());
		for(int i = 0; i < responses.length(); i++) {
			JSONObject message = responses.getJSONObject(i);
			
			String nomeMunicipio = message.getString("description");
			int idMunicipio = message.getInt("value");
			
			map.put(idMunicipio, nomeMunicipio);
		}
		return map;
	}
	
	/**
	 * Realiza a requisicao para o web service
	 * @param url - url para requisicao
	 * @return
	 * @throws IOException
	 */
	public static StringBuffer realizaRequisicaoParaWebService(String url) throws IOException {
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		return response;
	}

}
