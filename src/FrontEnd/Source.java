package FrontEnd;

import Message.*;
import java.io.BufferedReader;
import java.io.IOException;

public class Source {
    public static final char EOL = '\n';
    public static final char EOF = (char) 0;

    private static MessageHandler messageHandler;
    private BufferedReader reader;
    private String line;
    private int lineNum;
    private int currentPos;

    static{
        messageHandler = new MessageHandler();
    }

    public Source(BufferedReader reader) throws IOException {
        this.lineNum = 0;
        this.currentPos = -2; // the book said to do this we'll check back on it later
        this.reader = reader;
    }

    protected int getLineNum(){
        return lineNum;
    }
    protected int getPosition(){
        return currentPos;
    }

    public char currentChar()
            throws Exception
    {
        // First time?
        if (currentPos == -2) {
            readLine();
            return nextChar();
        }

        // At end of file?
        else if (line == null) {
            return EOF;
        }

        // At end of line?
        else if ((currentPos == -1) || (currentPos == line.length())) {
            return EOL;
        }

        // Need to read the next line?
        else if (currentPos > line.length()) {
            readLine();
            return nextChar();
        }

        // Return the character at the current position.
        else {
            return line.charAt(currentPos);
        }
    }

    public char nextChar() throws Exception {
        ++currentPos;
        return currentChar();
    }

    public char peekChar() throws Exception {
        currentChar();
        if (line == null){
            return EOF;
        }

        int nextPos = currentPos + 1;
        return nextPos < line.length() ? line.charAt(nextPos) : EOL;
    }

    private void readLine() throws IOException {
        line = reader.readLine();
        currentPos = -1;
        if (line != null){
            ++ lineNum;
        }

        if (line != null){
            sendMessage(new Message(MessageType.SOURCE_LINE, new Object[]{lineNum, line}));
        }
    }

    public void close() throws Exception {
        if (reader != null){
            try{
                reader.close();
            }
            catch (IOException ex){
                ex.printStackTrace();
                throw ex;
            }
        }
    }

    public void addMessageListener(MessageListener listener){
        messageHandler.addListener(listener);
    }

    public void removeMessageListener(MessageListener listener){
        messageHandler.removeListener(listener);
    }

    public void sendMessage(Message message){
        messageHandler.sendMessage(message);
    }
}
