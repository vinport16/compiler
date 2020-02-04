package FrontEnd.Pascal;

import FrontEnd.*;
import Intermediate.SymTabEntry;
import Intermediate.SymTabFactory;
import static Intermediate.symtabimpl.SymTabKeyImpl.*;

import Message.*;

public class PascalParserTD extends Parser {

    protected static PascalErrorHandler errorHandler = new PascalErrorHandler();

    public PascalParserTD(Scanner scanner){
        super(scanner);
    }

    public void parse() throws Exception {
        Token token;
        long startTime = System.currentTimeMillis();

        try{
            while (! ((token = nextToken()) instanceof EofToken)) {
                TokenType tokenType = token.getType();

                if(tokenType == PascalTokenType.IDENTIFIER){
                    String name = token.getText();
                    SymTabEntry entry = symTabStack.lookup(name);
                    if(entry == null){
                        entry = symTabStack.enterLocal(name);
                    }
                    entry.appendLineNumber(token.getLineNumber());
                }
                else if(tokenType == PascalTokenType.ERROR){
                    errorHandler.flag(token, (PascalErrorCode) token.getValue(), this);
                }
            }

            // send summary message
            float elapsedTime = (System.currentTimeMillis() - startTime)/1000f;
            sendMessage(new Message(MessageType.PARSER_SUMMARY,
                    new Number[] {
                            token.getLineNum(), getErrorCount(), elapsedTime
                    }
            ));
        }
        catch(java.io.IOException ex){
            errorHandler.abortTranslation(PascalErrorCode.IO_ERROR, this);
        }


    }

    public int getErrorCount(){
        return errorHandler.getErrorCount();
    }

}
