package br.com.diogo.compile.main;

import br.com.diogo.compile.exceptions.IsiLexicalException;
import br.com.diogo.compile.lexico.IsiScanner;
import br.com.diogo.compile.lexico.Token;

public class MainClass {
    public static void main(String[] args) {

        try {
            IsiScanner sc = new IsiScanner("input.isi");
            Token token = null;
            do {
                token = sc.nextToken();
                if(token != null){
                    System.out.println(token);
                }
            } while (token != null);
        } catch (IsiLexicalException ex){
            System.out.println("Lexical ERROR " + ex.getMessage());
        }

    }
}
