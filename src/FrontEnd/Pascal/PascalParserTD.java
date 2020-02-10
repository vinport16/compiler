package FrontEnd.Pascal;

import FrontEnd.*;
import Intermediate.*;
import FrontEnd.Pascal.parsers.*;
import FrontEnd.Pascal.PascalErrorCode;
import static Intermediate.symtabimpl.SymTabKeyImpl.*;

import Message.*;

public class PascalParserTD extends Parser {

    protected static PascalErrorHandler errorHandler = new PascalErrorHandler();

    /**
     * Constructor.
     * @param scanner the scanner to be used with this parser.
     */
    public PascalParserTD(Scanner scanner)
    {
        super(scanner);
    }

    /**
     * Constructor for subclasses.
     * @param parent the parent parser.
     */
    public PascalParserTD(PascalParserTD parent)
    {
        super(parent.getScanner());
    }

    /**
     * Getter.
     * @return the error handler.
     */
    public PascalErrorHandler getErrorHandler()
    {
        return errorHandler;
    }

    /**
     * Parse a Pascal source program and generate the symbol table
     * and the intermediate code.
     * @throws Exception if an error occurred.
     */
    public void parse()
            throws Exception
    {
        long startTime = System.currentTimeMillis();
        iCode = ICodeFactory.createICode();

        try {
            Token token = nextToken();
            ICodeNode rootNode = null;

            // Look for the BEGIN token to parse a compound statement.
            if (token.getType() == PascalTokenType.BEGIN) {
                StatementParser statementParser = new StatementParser(this);
                rootNode = statementParser.parse(token);
                token = currentToken();
            }
            else {
                errorHandler.flag(token, PascalErrorCode.UNEXPECTED_TOKEN, this);
            }

            // Look for the final period.
            if (token.getType() != PascalTokenType.DOT) {
                errorHandler.flag(token, PascalErrorCode.MISSING_PERIOD, this);
            }
            token = currentToken();

            // Set the parse tree root node.
            if (rootNode != null) {
                iCode.setRoot(rootNode);
            }

            // Send the parser summary message.
            float elapsedTime = (System.currentTimeMillis() - startTime)/1000f;
            sendMessage(new Message(MessageType.PARSER_SUMMARY,
                    new Number[] {token.getLineNumber(),
                            getErrorCount(),
                            elapsedTime}));
        }
        catch (java.io.IOException ex) {
            errorHandler.abortTranslation(PascalErrorCode.IO_ERROR, this);
        }
    }

    public int getErrorCount(){
        return errorHandler.getErrorCount();
    }

}
