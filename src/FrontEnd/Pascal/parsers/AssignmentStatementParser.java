package FrontEnd.Pascal.parsers;

import FrontEnd.*;
import FrontEnd.Pascal.*;
import Intermediate.*;
import java.util.EnumSet;


import static FrontEnd.Pascal.PascalTokenType.*;
import static FrontEnd.Pascal.PascalErrorCode.*;
import static Intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static Intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>AssignmentStatementParser</h1>
 *
 * <p>Parse a Pascal assignment statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class AssignmentStatementParser extends StatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public AssignmentStatementParser(PascalParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for the := token.
    private static final EnumSet<PascalTokenType> COLON_EQUALS_SET =
            ExpressionParser.EXPR_START_SET.clone();
    static {
        COLON_EQUALS_SET.add(COLON_EQUALS);
        COLON_EQUALS_SET.addAll(StatementParser.STMT_FOLLOW_SET);
    }

    /**
     * Parse an assignment statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
            throws Exception
    {
        // Create the ASSIGN node.
        ICodeNode assignNode = ICodeFactory.createICodeNode(ASSIGN);

        // Look up the target identifer in the symbol table stack.
        // Enter the identifier into the table if it's not found.
        String targetName = token.getText();
        SymTabEntry targetId = symTabStack.lookup(targetName);
        if (targetId == null) {
            targetId = symTabStack.enterLocal(targetName);
        }
        targetId.appendLineNumber(token.getLineNumber());

        token = nextToken();  // consume the identifier token

        // Create the variable node and set its name attribute.
        ICodeNode variableNode = ICodeFactory.createICodeNode(VARIABLE);
        variableNode.setAttribute(ID, targetId);

        // The ASSIGN node adopts the variable node as its first child.
        assignNode.addChild(variableNode);

        // Synchronize on the := token.
        token = synchronize(COLON_EQUALS_SET);
        if (token.getType() == COLON_EQUALS) {
            token = nextToken();  // consume the :=
        }
        else {
            errorHandler.flag(token, MISSING_COLON_EQUALS, this);
        }

        // Parse the expression.  The ASSIGN node adopts the expression's
        // node as its second child.
        ExpressionParser expressionParser = new ExpressionParser(this);
        assignNode.addChild(expressionParser.parse(token));

        return assignNode;
    }
}
