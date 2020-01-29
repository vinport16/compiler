package FrontEnd.Pascal;

import FrontEnd.*;
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

                if(tokenType != PascalTokenType.ERROR){
                    sendMessage(new Message(MessageType.TOKEN,
                                            new Object[] {token.getLineNum(),
                                                          token.getPosition(),
                                                          tokenType,
                                                          token.getText(),
                                                          token.getValue()
                                            }));
                }
                else{
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
