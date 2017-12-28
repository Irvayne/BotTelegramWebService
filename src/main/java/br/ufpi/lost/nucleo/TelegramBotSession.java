package br.ufpi.lost.nucleo;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import br.ufpi.lost.modelo.Estrutura;

public class TelegramBotSession {

    private final String endpoint = "https://api.telegram.org/";
    private final String token;
    private Map<Integer, Estrutura> session;

    public TelegramBotSession(String token) {
        this.token = token;
        this.session = new HashMap<Integer, Estrutura>();
    }

    public HttpResponse<JsonNode> sendMessage(Integer chatId, String text) throws UnirestException {
        return Unirest.post(endpoint + "bot" + token + "/sendMessage")
                .field("chat_id", chatId)
                .field("text", text)
                .asJson();
        
    }

    public HttpResponse<JsonNode> getUpdates(Integer offset) throws UnirestException {
        return Unirest.post(endpoint + "bot" + token + "/getUpdates")
        		.field("offset", offset)
                .asJson();
    }

    public void run() throws UnirestException, IOException {
        int last_update_id = 0; // controle das mensagens processadas
        HttpResponse<JsonNode> response;
        while (true) {
            response = getUpdates(last_update_id++);
            if (response.getStatus() == 200) {
                JSONArray responses = response.getBody().getObject().getJSONArray("result");
                if (responses.isNull(0)) {
                    continue;
                } else {
                    last_update_id = responses
                            .getJSONObject(responses.length() - 1)
                            .getInt("update_id") + 1;
                }

                for (int i = 0; i < responses.length(); i++) {
                	//mensagem enviada pelo chat
                    JSONObject message = responses
                            .getJSONObject(i)
                            .getJSONObject("message");
                    int chat_id = message
                            .getJSONObject("chat")
                            .getInt("id");
                    String first_name = message
                    		.getJSONObject("chat")
                            .getString("first_name");
                    String texto = message
                            .getString("text");

                    Date data = new Date();
                    //log para saber o horario da mensagem enviada, quem enviou e o conteudo da mensagem
                    System.out.println(data +" - "+first_name + " - " +chat_id+" - "+texto);
                    
                    //remove os espacos em branco e deixa tudo minusculo. Isso foi realizado para melhorar na busca por uma cidade 
                    //no web service
                    texto = texto.replaceAll(" ","").toLowerCase();
                    
                    String resposta = "";
                    resposta += MaquinaEstados.processar(session, chat_id, texto, first_name, message.getString("text"));
                    
                    //mensagem a ser enviada
                    sendMessage(chat_id, resposta);
                }
            }
        }
    }

	public Map<Integer, Estrutura> getSession() {
		return session;
	}

	public void setSession(Map<Integer, Estrutura> session) {
		this.session = session;
	}
}