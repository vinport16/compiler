package FrontEnd.Pascal;

import FrontEnd.*;
import static FrontEnd.Source.EOF;

public class PascalScanner extends Scanner {

    public PascalScanner(Source source){
        super(source);
    }

    protected Token extractToken() throws Exception {
        Token token;
        char currentChar = currentChar();

        // construct the next token
        if (currentChar == EOF){
            token = new EofToken(source);
        }
        else{
            token = new Token(source);
        }

        return token;
    }
}
