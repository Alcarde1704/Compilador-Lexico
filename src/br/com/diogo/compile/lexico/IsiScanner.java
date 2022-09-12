package br.com.diogo.compile.lexico;

import br.com.diogo.compile.exceptions.IsiLexicalException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IsiScanner {

    private char[] content;
    private int    estado;
    private int    pos;

    private int initialSize = 16;

    private double loadFactor = 0.75;

    double sizeToRehash = initialSize * loadFactor;

    private Map<Integer, String> example = new HashMap<Integer, String>();

    public void setExample(Map<Integer, String> example) {
        this.example = example;
    }

    public Map<Integer, String> getExample() {
        return example;
    }

    ArrayList<String> listaSimbolos = new ArrayList<>();
    public IsiScanner(String filename){

        try {
            String txtConteudo = "";
            String linha;

            BufferedReader br = new BufferedReader(new FileReader(filename));

            while ((linha = br.readLine()) != null){
                txtConteudo += linha;
                txtConteudo += "\n";
            }

            System.out.println("DEBUG -------");
            System.out.println(txtConteudo);
            System.out.println("-------------");
            content = txtConteudo.toCharArray();
            pos = 0;
            System.out.println(content);

            example.put(0, new String("if"));
            example.put(1, new String("else"));
            example.put(2, new String("while"));
            example.put(3, new String("for"));
            example.put(4, new String("do"));
            example.put(5, new String("String"));


        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public Token nextToken(){

        char currentChar;
        Token token;
        String term = "";
        Map<Integer, String> values = getExample();
        estado = 0;
        while (true) {
            if (isEOF()){
                return null;
            }
            currentChar = nextChar();
            switch (estado){
                case 0:
                    term += q0(currentChar, term);
                    break;
                case 1:
                    if (isChar(currentChar) || isDigit(currentChar) ){
                        term += currentChar;
                        estado = 1;
                    } else if (isSpace(currentChar) || isOperator(currentChar) || isPonctuation(currentChar)){
                        back();
                        estado = 2;
                    } else {
                        throw new IsiLexicalException("Malformed Identifier: " + term);
                    }
                    break;
                case 2:
                    boolean verificaPalavraReservada = false;

                    for (Integer key : values.keySet()){
                        String value = values.get(key);
                        System.out.println(value);
                        System.out.println(term);
                        if (term.equals(value)){
                            verificaPalavraReservada = true;
                            break;
                        }
                    }

                    if (verificaPalavraReservada){
                        System.out.println(term);
                        back();
                        token = new Token();
                        token.setType(Token.TK_RESERVED_WORD);
                        token.setText(term);
                        return token;
                    } else {
                        System.out.println(term);
                        back();
                        token = new Token();
                        token.setType(Token.TK_IDENTIFIER);
                        token.setText(term);
                        listaSimbolos.add(term);
                        return token;

                    }

                case 3:
                    if (isDigit(currentChar)){
                        term += currentChar;
                        estado = 3;
                    } else if (!isChar(currentChar)){
                        estado = 4;
                    } else {
                        throw new IsiLexicalException("Unrecognized Number");
                    }
                    break;
                case 4:


                    token = new Token();
                    token.setType(Token.TK_NUMBER);
                    token.setText(term);
                    back();
                    return token;
                case 5:

                    if (!isOperator(currentChar)){
                        back();
                        token = new Token();
                        token.setType(Token.TK_OPERATOR);
                        token.setText(term);
                        return token;
                    } else if (isOperator(currentChar)){
                        token = new Token();
                        term += currentChar;
                        token.setType(Token.TK_OPERATOR);
                        token.setText(term);
                        return token;
                    }

                case 6:
                    term += currentChar;
                    token = new Token();
                    token.setType(Token.TK_PONCTUATION);
                    token.setText(term);
                    return token;



            }


        }




    }

    private boolean isPonctuation(char c){
        return c == ';';
    }
    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    private boolean isChar(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isOperator(char c){
        return c == '>' || c == '<' || c == '=' || c == '!';
    }

    private boolean isSpace(char c){
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    private char nextChar(){
        return content[pos++];
    }

    private boolean isEOF(){
        return pos == content.length;
    }

    private void back(){
        pos--;
    }

    private String q0(char currentChar, String term){
        String termNovo = "";
        if (isChar(currentChar)){
            estado = 1;
            termNovo += currentChar;

        } else if (isDigit(currentChar)) {
            estado = 3;
            termNovo += currentChar;

        } else if (isSpace(currentChar)) {
            estado = 0;


        } else if (isOperator(currentChar)) {

            estado = 5;
            termNovo += currentChar;

        } else if (isPonctuation(currentChar)) {
            estado = 6;
            termNovo += currentChar;
        } else {
            throw new IsiLexicalException("Unrecognized SYMBOL");
        }
        return termNovo;
    }

}
