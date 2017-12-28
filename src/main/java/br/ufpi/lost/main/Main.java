package br.ufpi.lost.main;

import com.mashape.unirest.http.exceptions.UnirestException;

import br.ufpi.lost.nucleo.TelegramBotSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
    	//token gerado pelo gerenciador de bot (botfather) do telegram
        TelegramBotSession tb = new TelegramBotSession("549301846:AAHNm92gYo-8XZ89kllek-uJhgI4H4KMs6E");
        try {
            tb.run();
        } catch (UnirestException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
			System.out.println(e.getMessage());
		}
    }
}