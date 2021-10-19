import java.util.Enumeration;
import java.util.Vector;

public class LogicalExpression {
    String symbol = null; 	// null if sentence is a more complex expression
    String connective = null; 		// null if sentence is a _UNIQUE_ symbol
    Vector<LogicalExpression> subexpressions = null;   // a vector of LogicalExpressions ( basically a vector of unique symbols and subexpressions)

    //constructor
    public LogicalExpression()
    {
        this.subexpressions = new Vector<LogicalExpression>();
    }

    public LogicalExpression(LogicalExpression oldExpression) {
        if(oldExpression.getUniqueSymbol() == null) {
            this.symbol = oldExpression.getUniqueSymbol();
        } else {
            this.connective = oldExpression.getConnective();
            for(Enumeration e = oldExpression.getSubexpressions().elements(); e.hasMoreElements();) {
                LogicalExpression nextExpression = (LogicalExpression)e.nextElement();
                this.subexpressions.add(nextExpression);
            }
        }
    }

    public void setUniqueSymbol(String newSymbol) {
        newSymbol.trim();
        this.symbol = newSymbol;
    }

    public String setConnective(String input) {
        String connect;
        input.trim();
        if(input.startsWith("(")){
            input=input.substring(input.indexOf('('),input.length());
            input.trim();
        }
        if(input.contains( " " )) {
            connect=input.substring(0,input.indexOf(" ")) ;
            input=input.substring((connect.length() + 1),input.length());
        }else {
            connect = input;
            input = "";
        }
        this.connective = connect;
        return input;
    }

    public void setSubexpression( LogicalExpression newSub ){
        this.subexpressions.add(newSub);
    }

    public void setSubexpressions( Vector<LogicalExpression> symbols ){
        this.subexpressions = symbols;
    }

    public String getUniqueSymbol(){
        return this.symbol;
    }

    public String getConnective(){
        return this.connective;
    }
    public Vector<LogicalExpression> getSubexpressions(){
        return this.subexpressions;
    }
}
