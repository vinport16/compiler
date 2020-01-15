package FrontEnd.Pascal;

import FrontEnd.*;
import Message.*;

public class PascalParserTD extends Parser {

    public PascalParserTD(Scanner scanner){
        super(scanner);
    }

    public void parse() throws Exception {
        Token token;
        long startTime = System.currentTimeMillis();

        while (! ((token = nextToken()) instanceof EofToken)) {}

        float elapsedTime = (System.currentTimeMillis() - startTime)/1000f;
        sendMessage(new Message(MessageType.PARSER_SUMMARY,
                new Number[] {
                        token.getLineNum(), getErrorCount(), elapsedTime
                }
        ));
    }

    public int getErrorCount(){
        return 0;
    }

}
