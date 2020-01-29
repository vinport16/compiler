package FrontEnd.Pascal;

import FrontEnd.*;
import FrontEnd.Pascal.tokens.*;
import static FrontEnd.Source.EOF;

public class PascalScanner extends Scanner {

    public PascalScanner(Source source){
        super(source);
    }

    protected Token extractToken() throws Exception {

        skipWhiteSpace();

        Token token;
        char currentChar = currentChar();

        // construct the next token
        if (currentChar == EOF){
            token = new EofToken(source);
        }
        else if(Character.isLetter(currentChar)){
            token = new PascalWordToken(source);
        }
        else if(Character.isDigit(currentChar)){
            token = new PascalNumberToken(source);
        }
        else if(currentChar == '\''){
            token = new PascalStringToken(source);
        }
        else if(currentChar == '{'){
            token = new PascalCommentToken(source);
        }
        else if(PascalTokenType.SPECIAL_SYMBOLS.containsKey(Character.toString(currentChar))){
            token = new PascalSpecialSymbolToken(source);
        }
        else{
            token = new PascalErrorToken(source, PascalErrorCode.INVALID_CHARACTER, Character.toString(currentChar));
            nextChar();
        }

        return token;
    }

    private void skipWhiteSpace() throws Exception {
        char currentChar = currentChar();
        while (Character.isWhitespace(currentChar)){
            currentChar = nextChar();
        }
    }
}
