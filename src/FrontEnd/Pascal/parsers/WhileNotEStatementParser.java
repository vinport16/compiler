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

/**
 * <h1>WhileStatementParser</h1>
 *
 * <p>Parse a Pascal WHILE statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class WhileNotEStatementParser extends StatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public WhileNotEStatementParser(PascalParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for DO.
    private static final EnumSet<PascalTokenType> DO_SET =
            StatementParser.STMT_START_SET.clone();
    static {
        DO_SET.add(DO);
        DO_SET.addAll(StatementParser.STMT_FOLLOW_SET);
    }

    /**
     * Parse a WHILE statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
            throws Exception
    {
        token = nextToken();  // consume the WhileNotE
        // next token. TODO
        token = nextToken();  // consume the (

        ICodeNode parent = ICodeFactory.createICodeNode(COMPOUND);
        ICodeNode initPreviousValue = ICodeFactory.createICodeNode(ASSIGN);
        ICodeNode previousValue = ICodeFactory.createICodeNode(VARIABLE);
        previousValue.setAttribute(ID, "previousValue");

        SymTabEntry targetId = symTabStack.lookup("previousValue");
        if (targetId == null) {
            targetId = symTabStack.enterLocal("previousValue");
        }
        previousValue.setAttribute(ID, targetId);

        ICodeNode multiply = ICodeFactory.createICodeNode(MULTIPLY);
        ICodeNode greaterNode = ICodeFactory.createICodeNode(GT);

        //assign is first child of parent
        parent.addChild(initPreviousValue);
        initPreviousValue.addChild(previousValue);
        // add expression when we parse it

        // Create LOOP, TEST nodes.
        ICodeNode loopNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.LOOP);
        ICodeNode breakNode = ICodeFactory.createICodeNode(TEST);

        //LOOP is second child of parent
        parent.addChild(loopNode);

        // The LOOP node adopts the TEST node as its first child.
        // The TEST node adopts the NOT node as its only child.
        loopNode.addChild(breakNode);
        breakNode.addChild(greaterNode);

        // Parse the expression.
        ExpressionParser expressionParser = new ExpressionParser(this);
        ICodeNode expression = expressionParser.parse(token);

        // add expression to initPreviousValue
        initPreviousValue.addChild(expression);

        // loop tests a < pv * pv
        greaterNode.addChild(expression);
        greaterNode.addChild(multiply);
        multiply.addChild(previousValue);
        multiply.addChild(previousValue);

        // update previousValue at start of every loop
        loopNode.addChild(initPreviousValue);

        // next token. TODO
        token = nextToken();  // consume the )

        // Parse the statement.
        // The LOOP node adopts the statement subtree as its third child.
        StatementParser statementParser = new StatementParser(this);

        loopNode.addChild(statementParser.parse(token));


        return parent;
    }
}
