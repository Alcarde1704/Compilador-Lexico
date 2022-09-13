package br.com.diogo.compile.lexico;
    

public class Token {

    


    private enumTokens type;
    private String text;

    public Token(enumTokens type, String text){
        super();
        this.type = type;
        this.text = text;

    }

    public Token(){
        super();
    }

    public enumTokens getType() {
        return type;
    }

    public void setType(enumTokens type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Token [type=" + type+ ", text= " + text +"]";
    }
}
