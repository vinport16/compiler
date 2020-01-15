package FrontEnd;

public class Token {

    protected TokenType type;
    protected String text;
    protected Object value;
    protected Source source;
    private int lineNum;
    private int position;

    public Token(Source source) throws Exception{
        this.source = source;
        this.lineNum = source.getLineNum();
        this.position = source.getPosition();

        extract();
    }

    protected void extract() throws Exception {
        text = Character.toString(currentChar());
        value = null;
        nextChar(); // consumes current character
    }

    protected char currentChar() throws Exception {
        return source.currentChar();
    }

    protected char nextChar() throws Exception {
        return source.nextChar();
    }

    protected char peekChar() throws Exception {
        return source.peekChar();
    }

    public int getLineNum(){
        return lineNum;
    }
}
