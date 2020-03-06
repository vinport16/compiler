package BackEnd.Interpreter.executors;

import java.util.ArrayList;

import Intermediate.*;
import Intermediate.icodeimpl.*;
import BackEnd.Interpreter.*;

import static Intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static Intermediate.icodeimpl.ICodeKeyImpl.*;
import static BackEnd.Interpreter.RuntimeErrorCode.*;

/**
 * <h1>LoopExecutor</h1>
 *
 * <p>Execute a loop statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class LoopExecutor extends StatementExecutor
{
    /**
     * Constructor.
     * @param the parent executor.
     */
    public LoopExecutor(Executor parent)
    {
        super(parent);
    }

    /**
     * Execute a loop statement.
     * @param node the root node of the statement.
     * @return null.
     */
    public Object execute(ICodeNode node)
    {
        boolean exitLoop = false;
        ICodeNode exprNode = null;
        ArrayList<ICodeNode> loopChildren = node.getChildren();

        ExpressionExecutor expressionExecutor = new ExpressionExecutor(this);
        StatementExecutor statementExecutor = new StatementExecutor(this);

        // Loop until the TEST expression value is true.
        while (!exitLoop) {
            ++executionCount;  // count the loop statement itself

            // Execute the children of the LOOP node.
            for (ICodeNode child : loopChildren) {
                ICodeNodeTypeImpl childType =
                                      (ICodeNodeTypeImpl) child.getType();

                // TEST node?
                if (childType == TEST) {
                    if (exprNode == null) {
                        exprNode = child.getChildren().get(0);
                    }
                    exitLoop = (Boolean) expressionExecutor.execute(exprNode);
                }

                // Statement node.
                else {
                    statementExecutor.execute(child);
                }

                // Exit if the TEST expression value is true,
                if (exitLoop) {
                    break;
                }
            }
        }

        return null;
    }
}
