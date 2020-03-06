package FrontEnd.Pascal.parsers;

import java.util.EnumSet;

import FrontEnd.*;
import FrontEnd.Pascal.*;
import Intermediate.*;
import Intermediate.icodeimpl.ICodeNodeTypeImpl;

import static FrontEnd.Pascal.PascalTokenType.*;
import static FrontEnd.Pascal.PascalErrorCode.*;
import static Intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static Intermediate.icodeimpl.ICodeKeyImpl.*;


public class LoopStatementParser extends StatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public LoopStatementParser(PascalParserTD parent)
    {
        super(parent);
    }

    private static final EnumSet<PascalTokenType> OPEN_PAREN_SET =
            ExpressionParser.EXPR_START_SET.clone();
    static {
        OPEN_PAREN_SET.add(LEFT_PAREN);
        OPEN_PAREN_SET.addAll(StatementParser.STMT_FOLLOW_SET);
    }

    private static final EnumSet<PascalTokenType> BAR_SET =
            ExpressionParser.EXPR_START_SET.clone();
    static {
        BAR_SET.add(BAR);
        BAR_SET.addAll(StatementParser.STMT_FOLLOW_SET);
    }

    private static final EnumSet<PascalTokenType> CLOSE_PAREN_SET =
            ExpressionParser.EXPR_START_SET.clone();
    static {
        CLOSE_PAREN_SET.add(RIGHT_PAREN);
        CLOSE_PAREN_SET.addAll(StatementParser.STMT_FOLLOW_SET);
    }

    // Synchronization set for DO.
    private static final EnumSet<PascalTokenType> DO_SET =
            StatementParser.STMT_START_SET.clone();
    static {
        DO_SET.add(DO);
        DO_SET.addAll(StatementParser.STMT_FOLLOW_SET);
    }

    /**
     * Parse the FOR statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
            throws Exception
    {
        token = nextToken();  // consume the LOOP

        // Synchronize at the open paren
        token = synchronize(OPEN_PAREN_SET);
        if (token.getType() == LEFT_PAREN) {
            token = nextToken();  // consume the (
        }
        else {
            errorHandler.flag(token, MISSING_LEFT_PAREN, this);
        }

        Token targetToken = token;

        // Create the loop COMPOUND, LOOP, and TEST nodes.
        ICodeNode compoundNode = ICodeFactory.createICodeNode(COMPOUND);
        ICodeNode loopNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.LOOP);
        ICodeNode testNode = ICodeFactory.createICodeNode(TEST);

        // Parse the embedded initial assignment.
        AssignmentStatementParser assignmentParser =
                new AssignmentStatementParser(this);
        ICodeNode initAssignNode = assignmentParser.parse(token);

        // Set the current line number attribute.
        setLineNumber(initAssignNode, targetToken);

        // The COMPOUND node adopts the initial ASSIGN and the LOOP nodes
        // as its first and second children.
        compoundNode.addChild(initAssignNode);
        compoundNode.addChild(loopNode);

        // Synchronize at the BAR
        token = synchronize(BAR_SET);
        if (token.getType() == BAR) {
            token = nextToken();  // consume the BAR
        }
        else {
            errorHandler.flag(token, MISSING_BAR, this);
        }

        // Parse the embedded expression.
        ExpressionParser expressionParser =
                new ExpressionParser(this);
        ICodeNode test = expressionParser.parse(token);

        // Set the current line number attribute.
        setLineNumber(test, targetToken);

        ICodeNode not = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);
        testNode.addChild(not);
        not.addChild(test);
        loopNode.addChild(testNode);

        // Synchronize at the 2nd BAR
        token = synchronize(BAR_SET);
        if (token.getType() == BAR) {
            token = nextToken();  // consume the BAR
        }
        else {
            errorHandler.flag(token, MISSING_BAR, this);
        }

        // Parse the embedded expression.
        ICodeNode lastAssign = assignmentParser.parse(token);

        // Set the current line number attribute.
        setLineNumber(lastAssign, targetToken);

        // Synchronize at the close paren
        token = synchronize(CLOSE_PAREN_SET);
        if (token.getType() == RIGHT_PAREN) {
            token = nextToken();  // consume the )
        }
        else {
            errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
        }

        // Synchronize at the DO.
        token = synchronize(DO_SET);
        if (token.getType() == DO) {
            token = nextToken();  // consume the DO
        }
        else {
            errorHandler.flag(token, MISSING_DO, this);
        }

        StatementParser statementParser = new StatementParser(this);
        ICodeNode body = statementParser.parse(token);

        // append the body to the loop node
        loopNode.addChild(body);

        // append the last assignment to the loop node (after the body of the loop is added)
        loopNode.addChild(lastAssign);

        return compoundNode;
    }
}