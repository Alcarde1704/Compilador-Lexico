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

            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                while ((linha = br.readLine()) != null){
                    txtConteudo += linha;
                    txtConteudo += "\n";
                }
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
            example.put(6, new String("null"));
            example.put(7, new String("int"));
            example.put(9, new String("float"));
            example.put(10, new String("double"));
            example.put(11, new String("boolean"));
            example.put(12, new String("true"));
            example.put(13, new String("false"));

            


        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public Token nextToken(){

        char currentChar;
        String term = "";
        
        estado = 0;
        while (true) {
            if (isEOF()){
                return null;
            }
            currentChar = nextChar();
            switch (estado){
                case 0:
                    term += q0(currentChar);
                    break;
                case 1:
                    term += q1(currentChar);
                    break;
                case 2:
                    return q2(term);
                    
                case 3:
                    term += q3(currentChar);
                    break;
                case 4:

                    return q4(term);
                    
                case 5:

                    term += q5(currentChar);
                    break;
                case 6:
                    return q6(term);
                case 7:
                    return q7(term);
                case 8:
                    return q8(term);



            }


        }




    }

    public ArrayList<String> listaSimbolos(){

        ArrayList<String> listaSimbolos = this.listaSimbolos;

        return listaSimbolos;

    }




    private boolean isGroup(char c){
        return c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}';
    }

    private boolean isPonctuation(char c){
        return c == ';' || c == '.' || c == ',' || c == ':' || c == '?';
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










    private String q0(char currentChar){
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
        } else if(isGroup(currentChar)) {
            estado = 8;
            termNovo += currentChar;
        } else {
            throw new IsiLexicalException("Unrecognized SYMBOL: ");
        }
        return termNovo;
    }


    private String q1(char currentChar){
        String termNovo = "";

        if (isChar(currentChar) || isDigit(currentChar) ){
            termNovo += currentChar;
            estado = 1;
        } else if (isSpace(currentChar) || isOperator(currentChar) || isPonctuation(currentChar)){
            back();
            estado = 2;
        } else {
            throw new IsiLexicalException("Malformed Identifier: ");
        }

        return termNovo;

    }

    private Token q2(String term){
        Token token;

        Map<Integer, String> values = getExample();
        ArrayList<String> listaSimbolos = listaSimbolos();

        boolean verificaPalavraReservada = false;
        boolean verificaPalavraReservadaEscritaErrada = false;
        boolean verificaListaSimbolos = false;

        if(values.containsValue(term)){
            verificaPalavraReservada = true;        
        }

        if(listaSimbolos.contains(term)){
            verificaListaSimbolos = true;
        }

        for(Integer key : values.keySet()){
            String value = values.get(key);
            if(value.equalsIgnoreCase(term)){
                verificaPalavraReservadaEscritaErrada = true;
                break;
            }
        }

        if (verificaPalavraReservada){
            
            if(term.equals("if")){
                back();
                token = new Token();
                token.setType(enumTokens.TK_RESERVED_WORD_IF);
                token.setText(term);
                return token;
            } else if(term.equals("else")){
                back();
                token = new Token();
                token.setType(enumTokens.TK_RESERVED_WORD_ELSE);
                token.setText(term);
                return token;
            } else if(term.equals("while")) {
                back();
                token = new Token();
                token.setType(enumTokens.TK_RESERVED_WORD_WHILE);
                token.setText(term);
                return token;
            } else if(term.equals("for")) {
                back();
                token = new Token();
                token.setType(enumTokens.TK_RESERVED_WORD_FOR);
                token.setText(term);
                return token;
            } else if(term.equals("do")) {
                back();
                token = new Token();
                token.setType(enumTokens.TK_RESERVED_WORD_DO);
                token.setText(term);
                return token;
            } else if(term.equals("String")) {
                back();
                token = new Token();
                token.setType(enumTokens.TK_RESERVED_WORD_STRING);
                token.setText(term);
                return token;
            } else if(term.equals("null")) {
                back();
                token = new Token();
                token.setType(enumTokens.TK_RESERVED_WORD_NULL);
                token.setText(term);
                return token;
            } else if(term.equals("int")) {
                back();
                token = new Token();
                token.setType(enumTokens.TK_RESERVED_WORD_INT);
                token.setText(term);
                return token;
            } else if(term.equals("float")) {
                back();
                token = new Token();
                token.setType(enumTokens.TK_RESERVED_WORD_FLOAT);
                token.setText(term);
                return token;
            } else if(term.equals("double")) {
                back();
                token = new Token();
                token.setType(enumTokens.TK_RESERVED_WORD_DOUBLE);
                token.setText(term);
                return token;
            } else if(term.equals("boolean")) {
                back();
                token = new Token();
                token.setType(enumTokens.TK_RESERVED_WORD_BOOLEAN);
                token.setText(term);
                return token;
            } else if(term.equals("true")) {
                back();
                token = new Token();
                token.setType(enumTokens.TK_RESERVED_WORD_TRUE);
                token.setText(term);
                return token;
            } else {
                back();
                token = new Token();
                token.setType(enumTokens.TK_RESERVED_WORD_FALSE);
                token.setText(term);
                return token;
            } 

        } else if(!verificaListaSimbolos && !verificaPalavraReservadaEscritaErrada){
            back();
            token = new Token();
            token.setType(enumTokens.TK_IDENTIFIER);
            token.setText(term);
            listaSimbolos.add(term);
            return token;

        } else {
            throw new IsiLexicalException("Identificador já utilizado ou escrito de forma errada");
        }


    }

    private String q3(char currentChar){
        String term = "";
        if (isDigit(currentChar)){
            term += currentChar;
            estado = 3;
        } else if (!isChar(currentChar)){
            back();
            estado = 4;
        } else {
            throw new IsiLexicalException("Unrecognized Number");
        }
        return term;
    }

    private Token q4(String term){

        Token token = new Token();
        token.setType(enumTokens.TK_NUMBER);
        token.setText(term);
        back();
        return token;
    }

    private String q5(char currentChar){

        


        String term = "";
        if (isOperator(currentChar)){
            term += currentChar;
            estado = 5;
        } else if ( isChar(currentChar) || isDigit(currentChar) || isSpace(currentChar)) {
            back();
            estado = 7;
        } else {
            throw new IsiLexicalException("Unregconized Operator");
        }
        return term;


        

        
    }

    private Token q6(String term){

        Token token;

        if(term.equals(";")){
            token = new Token();
            token.setType(enumTokens.TK_PONTO_VIRGULA);
            token.setText(term);
            return token;
        } else if (term.equals("?")){
            token = new Token();
            token.setType(enumTokens.TK_INTERROGACAO);
            token.setText(term);
            return token;
        } else if (term.equals(".")){
            token = new Token();
            token.setType(enumTokens.TK_PONTO);
            token.setText(term);
            return token;
        } else if(term.equals(",")) {
            token = new Token();
            token.setType(enumTokens.TK_VIRGULA);
            token.setText(term);
            return token;
        } else {
            token = new Token();
            token.setType(enumTokens.TK_DOIS_PONTOS);
            token.setText(term);
            return token;
        }
        
    }

    private Token q7(String term){

        if(term.equals("=")){
            Token token = new Token();
            token.setType(enumTokens.TK_ASSIGN);
            token.setText(term);
            back();
            return token;
        } else {
            Token token = new Token();
            token.setType(enumTokens.TK_OPERATOR);
            token.setText(term);
            back();
            return token;
        }
        
        

        

        
    }

    private Token q8(String term){

        Token token;
        
        if( term.equals("(") || term.equals(")") ){
            back();
            token = new Token();
            token.setType(enumTokens.TK_PARENTESES);
            token.setText(term);
            return token;
        } else if(term.equals("[") || term.equals("]")){
            back();
            token = new Token();
            token.setType(enumTokens.TK_COLCHETES);
            token.setText(term);
            return token;
        } else {
            back();
            token = new Token();
            token.setType(enumTokens.TK_CHAVES);
            token.setText(term);
            return token;
        }
        
    }
    

}
