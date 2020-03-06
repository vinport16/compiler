package FrontEnd.Pascal.parsers;

import java.util.EnumSet;

import FrontEnd.*;
import FrontEnd.Pascal.*;
import Intermediate.*;
import Intermediate.icodeimpl.*;

import static FrontEnd.Pascal.PascalTokenType.*;
import static FrontEnd.Pascal.PascalErrorCode.*;
import static Intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static Intermediate.icodeimpl.ICodeKeyImpl.*;


public class WhenStatementParser extends StatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public WhenStatementParser(PascalParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for LESSTHAN0.
    private static final EnumSet<PascalTokenType> LESS_SET =
            StatementParser.STMT_START_SET.clone();
    static {
        LESS_SET.add(LESSTHAN0);
        LESS_SET.addAll(StatementParser.STMT_FOLLOW_SET);
    }

    // Synchronization set for EQUAL0.
    private static final EnumSet<PascalTokenType> EQ_SET =
            StatementParser.STMT_START_SET.clone();
    static {
        EQ_SET.add(EQUAL0);
        EQ_SET.addAll(StatementParser.STMT_FOLLOW_SET);
    }

    // Synchronization set for GREATERTHAN0.
    private static final EnumSet<PascalTokenType> GREATER_SET =
            StatementParser.STMT_START_SET.clone();
    static {
        GREATER_SET.add(GREATERTHAN0);
        GREATER_SET.addAll(StatementParser.STMT_FOLLOW_SET);
    }

    /**
     * Parse a WHEN statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
            throws Exception
    {
        token = nextToken();  // consume the WHEN

        // Create an IF node.
        ICodeNode ifNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.IF);

        // Parse the expression.
        ExpressionParser expressionParser = new ExpressionParser(this);
        ICodeNode expression = expressionParser.parse(token);
        token = currentToken();

        // Create a Node with value 0
        ICodeNode zero = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.INTEGER_CONSTANT);
        zero.setAttribute(VALUE, 0);

        // If less than 0
        ICodeNode lessThan0 = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.LT);
        lessThan0.addChild(expression);
        lessThan0.addChild(zero);
        ifNode.addChild(lessThan0);

        // Synchronize at the LESSTHAN0.
        token = synchronize(LESS_SET);
        if (token.getType() == LESSTHAN0) {
            token = nextToken();  // consume the THEN
        }
        else {
            errorHandler.flag(token, MISSING_LESSTHAN0, this);
        }

        // Parse the LESSTHAN0 statement.
        // The if2 node adopts the statement subtree as its first child.
        StatementParser statementParser = new StatementParser(this);
        ifNode.addChild(statementParser.parse(token));
        token = currentToken();

        token = nextToken();  // consume the ';'
        // Synchronize at the EQUAL0.
        token = synchronize(EQ_SET);
        if (token.getType() == EQUAL0) {
            token = nextToken();  // consume the THEN
        }
        else {
            errorHandler.flag(token, MISSING_EQUAL0, this);
        }

        // Create a child If statement to add to the first If's Else (third child)
        ICodeNode if2 = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.IF);
        ifNode.addChild(if2);

        // Create the equal zero test
        ICodeNode equal0 = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.EQ);
        equal0.addChild(expression);
        equal0.addChild(zero);
        if2.addChild(equal0);

        // Parse the EQUAL0 statement.
        // The IF2 node adopts the statement subtree as its first child.
        StatementParser statementParser2 = new StatementParser(this);
        if2.addChild(statementParser2.parse(token));
        token = currentToken();

        token = nextToken();  // consume the ';'
        // Synchronize at the GRETERTHAN0.
        token = synchronize(GREATER_SET);
        if (token.getType() == GREATERTHAN0) {
            token = nextToken();  // consume the GREATERTHAN0
        }
        else {
            errorHandler.flag(token, MISSING_GREATERTHAN0, this);
        }

        // Parse the GREATERTHAN0 statement.
        // The IF2 node adopts the statement subtree as its second child.
        StatementParser statementParser3 = new StatementParser(this);
        if2.addChild(statementParser3.parse(token));
        token = currentToken();

        return ifNode;
    }
}
