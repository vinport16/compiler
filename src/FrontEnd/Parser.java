package FrontEnd;
import Intermediate.ICode;
import Intermediate.SymTabFactory;
import Intermediate.SymTabStack;
import Message.*;

public abstract class Parser implements MessageProducer {
    protected Scanner scanner;
    protected ICode iCode;
    protected static SymTabStack symTabStack;
    protected static MessageHandler messageHandler;

    static{
        symTabStack = SymTabFactory.createSymTabStack();
        messageHandler = new MessageHandler();
    }

    protected Parser(Scanner scanner){
        this.scanner = scanner;
        this.iCode = null;
    }

    public abstract void parse() throws Exception;
    public abstract int getErrorCount();

    public Token currentToken(){
        return scanner.currentToken();
    }

    public Token nextToken() throws Exception{
        return scanner.nextToken();
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

    public SymTabStack getSymTabStack(){
        return symTabStack;
    }

    public ICode getICode(){
        return iCode;
    }

}
