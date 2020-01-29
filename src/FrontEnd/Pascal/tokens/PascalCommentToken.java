package FrontEnd.Pascal.tokens;

import FrontEnd.*;
import FrontEnd.Pascal.*;

import static FrontEnd.Source.EOL;
import static FrontEnd.Source.EOF;
import static FrontEnd.Pascal.PascalTokenType.*;
import static FrontEnd.Pascal.PascalErrorCode.*;


public class PascalCommentToken extends PascalToken
{

    public PascalCommentToken(Source source)
            throws Exception
    {
        super(source);
    }

    protected void extract()
            throws Exception
    {
        StringBuilder textBuffer = new StringBuilder();
        StringBuilder valueBuffer = new StringBuilder();

        char currentChar = nextChar();  // consume initial open {
        textBuffer.append('{');

        // Get characters.
        do {
            // Replace any whitespace character with a blank.
            if (Character.isWhitespace(currentChar)) {
                currentChar = ' ';
            }

            if ((currentChar != '}') && (currentChar != EOF)) {
                textBuffer.append(currentChar);
                valueBuffer.append(currentChar);
                currentChar = nextChar();  // consume character
            }

        } while ((currentChar != '}') && (currentChar != EOF));

        if (currentChar == '}') {
            nextChar();  // consume final }
            textBuffer.append('}');

            type = COMMENT;
            value = valueBuffer.toString();
        }
        else {
            type = ERROR;
            value = UNEXPECTED_EOF;
        }

        text = textBuffer.toString();
    }
}
