import java.util.*;

public class TTalgorithm {

    public static List<String> ExtractSymbols(LogicalExpression sentence){
        List<String> resultList=new ArrayList<String>();
        if(sentence.getUniqueSymbol()!=null) {
            resultList.add(sentence.getUniqueSymbol());
        }else {
            for (LogicalExpression child: sentence.getSubexpressions()){
                resultList=combine(resultList,ExtractSymbols(child));
            }
        }
        return resultList;
    }

    public static List<String> combine(List<String> list1, List<String> list2){
        for(String list:list2) {
            if(!list1.contains(list)) {
                list1.add(list);
            }
        }
        return list1;
    }
    
    public static boolean trueInKB(LogicalExpression KB, String symbol) {
        Vector<LogicalExpression> expressions = KB.getSubexpressions();

        for(LogicalExpression subexpression : expressions) {
            String currentSymbol = subexpression.getUniqueSymbol();

            if(currentSymbol != null && currentSymbol.equals(symbol)) {
                return true;
            }
        }

        return false;
    }

    public static boolean falseInKB(LogicalExpression KB, String symbol) {
        Vector<LogicalExpression> expressions = KB.getSubexpressions();

        for(LogicalExpression subexpression : expressions) {
            String currentConnective = subexpression.getConnective();

            if(currentConnective != null && currentConnective.equals("not")) {
                String currentSymbol = subexpression.getSubexpressions().get(0).getUniqueSymbol();
                if(currentSymbol != null && currentSymbol.equals(symbol)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Boolean PLTrue (LogicalExpression sentence, HashMap<String, Boolean> map){
        if(sentence.getUniqueSymbol()!=null) {
            return map.get(sentence.getUniqueSymbol());
        }
        else if(sentence.getConnective().equals("and")) {
            for(LogicalExpression child: sentence.getSubexpressions()) {
                if(PLTrue(child, map)==false) {
                    return false;
                }
            }
            return true;
        }
        else if(sentence.getConnective().equals("or")) {
            for(LogicalExpression child: sentence.getSubexpressions()) {
                if(PLTrue(child, map)) {
                    return true;
                }
            }
            return false;
        }
        else if(sentence.getConnective().equals("if")) {
            LogicalExpression left=sentence.getSubexpressions().get(0);
            LogicalExpression right=sentence.getSubexpressions().get(1);
            if(PLTrue(left, map)==true&&PLTrue(right, map)==false) {
                return false;
            }
            return true;
        }
        else if(sentence.getConnective().equals("iff")) {
            LogicalExpression left=sentence.getSubexpressions().get(0);
            LogicalExpression right=sentence.getSubexpressions().get(1);
            if(PLTrue(left, map)==true&&PLTrue(right, map)==true) {
                return true;
            }else if(PLTrue(left, map)==false&&PLTrue(right, map)==false) {
                return true;
            }
            return false;
        }
        else{
            LogicalExpression child1=sentence.getSubexpressions().get(0);
            if(PLTrue(child1, map)==true) {
                return false;
            }else if(PLTrue(child1, map)==false) {
                return true;
            }
            return true;
        }
    }
    
    public static Boolean TTCheckAll(LogicalExpression KB, LogicalExpression alpha, List<String> symbols, HashMap<String, Boolean> model) {
        if(symbols.isEmpty()) {
            if(PLTrue(KB, model)) {
                return PLTrue(alpha, model);
            }else {
                return true;
            }
        }else {
            String first = symbols.get(0);
            ArrayList<String> rest = new ArrayList<String>(symbols.subList(1, symbols.size()));
            if(trueInKB(KB, first)) {
                return TTCheckAll(KB, alpha, rest, Extend(first, true, model));
            } else if(falseInKB(KB, first)) {
                return TTCheckAll(KB, alpha, rest, Extend(first, false, model));
            } else {
                return TTCheckAll(KB, alpha, rest, Extend(first, true, model)) && TTCheckAll(KB, alpha, rest, Extend(first, false, model));
            }
        }
    }


    public static HashMap<String, Boolean> Extend(String first, Boolean value, HashMap<String, Boolean> model){
        HashMap<String,Boolean> toReturn = new HashMap<String,Boolean>();
        model.put(first,value);

        toReturn.putAll(model);
        return toReturn;
    }
    
    public static Boolean TTEntails(LogicalExpression KB, LogicalExpression alpha) {
        List<String> symbol1=ExtractSymbols(KB);
        List<String> symbol2=ExtractSymbols(alpha);
        List<String> symbols=combine(symbol1, symbol2);
        HashMap<String, Boolean> emptyHashMap=new HashMap<String, Boolean>();
        return TTCheckAll(KB, alpha, symbols, emptyHashMap);
    }
    
    public static LogicalExpression readExpression( String input_string )
    {
        LogicalExpression result = new LogicalExpression();
        //trim the whitespace off
        input_string = input_string.trim();
        if( input_string.startsWith("(") )
        {
            String symbolString = "";
            // remove the '(' from the input string
            symbolString = input_string.substring(1);
            //remove the last ')'
            //it should be at the end
            symbolString = symbolString.substring(0,(symbolString.length()-1));
            symbolString.trim();
            // read the connective into the result LogicalExpression object
            symbolString = result.setConnective(symbolString);
            //read the subexpressions into a vector and call setSubExpressions( Vector );
            result.setSubexpressions(read_subexpressions(symbolString));
        }
        else
        {
            // the next symbol must be a unique symbol
            result.setUniqueSymbol(input_string);
        }
        return result;
    }

    public static Vector<LogicalExpression> read_subexpressions(String input_string){

        Vector<LogicalExpression> symbolList = new Vector<LogicalExpression>();
        LogicalExpression newExpression;// = new LogicalExpression();
        String newSymbol = new String();
        input_string.trim();
        while(input_string.length()>0){
            newExpression = new LogicalExpression();
            if(input_string.startsWith("(")) {
                // find the matching ')'
                int parenCounter = 1;
                int matchingIndex = 1;
                while((parenCounter>0) && (matchingIndex<input_string.length())){
                    if(input_string.charAt(matchingIndex) =='('){
                        parenCounter++;
                    } else if( input_string.charAt(matchingIndex)==')'){
                        parenCounter--;
                    }
                    matchingIndex++;
                }
                // read until the matching ')' into a new string
                newSymbol = input_string.substring(0, matchingIndex);
                // pass that string to readExpression,
                newExpression = readExpression(newSymbol);
                // add the LogicalExpression that it returns to the vector symbolList
                symbolList.add(newExpression);
                // trim the logicalExpression from the input_string for further processing
                input_string = input_string.substring(newSymbol.length(),input_string.length());
            } else {
                if(input_string.contains(" ")){
                    //remove the first string from the string
                    newSymbol = input_string.substring( 0, input_string.indexOf(" "));
                    input_string = input_string.substring((newSymbol.length() + 1), input_string.length());
                } else{
                    newSymbol = input_string;
                    input_string = "";
                }
                newExpression.setUniqueSymbol( newSymbol );
                symbolList.add( newExpression );
            }
            input_string.trim();
            if(input_string.startsWith(" ")){
                //remove the leading whitespace
                input_string = input_string.substring(1);
            }
        }
        return symbolList;
    }
    public static void readValue (String input) {
        Scanner s=new Scanner(System.in);
        if(input.equalsIgnoreCase("a")) {
            System.out.println("Show {P, P-->Q} entails Q");
            LogicalExpression knowledge_base = new LogicalExpression();
            LogicalExpression alpha = new LogicalExpression();
            knowledge_base.setConnective("and");
            LogicalExpression subExpression = readExpression("(if P Q)");
            knowledge_base.setSubexpression( subExpression );
            subExpression = readExpression( "P" );
            knowledge_base.setSubexpression( subExpression );
            alpha = readExpression( "Q" );
            System.out.println("Checking if KB entails Q");
            System.out.println(TTEntails(knowledge_base, alpha));
            System.out.println("----------------------------------");
        }else if(input.equalsIgnoreCase("b")) {
            System.out.println("Welcome to Wumpus World!");
            LogicalExpression knowledge_base = new LogicalExpression();
            LogicalExpression alpha1 = new LogicalExpression();
            knowledge_base.setConnective("and");

            LogicalExpression subExpression = readExpression("(not P11)");
            knowledge_base.setSubexpression( subExpression );

            subExpression = readExpression("(iff B11 (or P12 P21))");
            knowledge_base.setSubexpression( subExpression );

            subExpression = readExpression("(iff B21 (or P11 P22 P31))");
            knowledge_base.setSubexpression( subExpression );

            subExpression = readExpression("(iff B12 (or P11 P22 P13))");
            knowledge_base.setSubexpression( subExpression );

            subExpression = readExpression("(not B11)");
            knowledge_base.setSubexpression( subExpression );

            alpha1 = readExpression("(not P12)");
            System.out.println("Checking if KB entails not P(1,2)");
            System.out.println(TTEntails(knowledge_base, alpha1));

            alpha1 = readExpression("(not P21)");
            System.out.println("Checking if KB entails not P(2,1)");
            System.out.println(TTEntails(knowledge_base, alpha1));

            alpha1 = readExpression("P22");
            System.out.println("Checking if KB entails P(2,2)");
            System.out.println(TTEntails(knowledge_base, alpha1));

            alpha1 = readExpression("(not P22)");
            System.out.println("Checking if KB entails not P(2,2)");
            System.out.println(TTEntails(knowledge_base, alpha1));

            System.out.println("Adding perception B(2,1)");
            subExpression = readExpression("B21");
            knowledge_base.setSubexpression( subExpression );

            alpha1 = readExpression("(or P22 P31)");
            System.out.println("Checking if KB entails P(2,2) or P(3,1)");
            System.out.println(TTEntails(knowledge_base, alpha1));

            alpha1 = readExpression("P22");
            System.out.println("Checking if KB entails P(2,2)");
            System.out.println(TTEntails(knowledge_base, alpha1));

            alpha1 = readExpression("(not P22)");
            System.out.println("Checking if KB entails not P(2,2)");
            System.out.println(TTEntails(knowledge_base, alpha1));

            alpha1 = readExpression("P31");
            System.out.println("Checking if KB entails P(3,1)");
            System.out.println(TTEntails(knowledge_base, alpha1));

            alpha1 = readExpression("(not P31)");
            System.out.println("Checking if KB entails not P(3,1)");
            System.out.println(TTEntails(knowledge_base, alpha1));

            System.out.println("Adding perception not B(1,2)");
            subExpression = readExpression("(not B12)");
            knowledge_base.setSubexpression( subExpression );

            alpha1 = readExpression("(not P22)");
            System.out.println("Checking if KB entails not P(2,2)");
            System.out.println(TTEntails(knowledge_base, alpha1));

            alpha1 = readExpression("P31");
            System.out.println("Checking if KB entails P(3,1)");
            System.out.println(TTEntails(knowledge_base, alpha1));
            System.out.println("----------------------------------");
        }else if(input.equalsIgnoreCase("c")) {
            System.out.println("Russell & Norvig");
            LogicalExpression knowledge_base = new LogicalExpression();
            LogicalExpression alpha2 = new LogicalExpression();
            knowledge_base.setConnective("and");
            System.out.println("The knowledge base is: ");
            System.out.println("Mythical --> Immortal ");
            System.out.println("(not Mythical) --> (not Immortal)  (Mammal)");
            System.out.println("(Immortal or Mammal) --> Horned");
            System.out.println("Horned --> Magical");
            LogicalExpression subExpression = readExpression("(if My Im)");
            knowledge_base.setSubexpression( subExpression );

            subExpression = readExpression("(if (not My) (and (not Im) Ma))");
            knowledge_base.setSubexpression( subExpression );

            subExpression = readExpression("(if (or Im Ma ) Ho)");
            knowledge_base.setSubexpression( subExpression );

            subExpression = readExpression("(if Ho Mag)");
            knowledge_base.setSubexpression( subExpression );

            alpha2 = readExpression("My");
            System.out.println("Checking if KB entails Mythical");
            System.out.println(TTEntails(knowledge_base, alpha2));

            alpha2 = readExpression("Mag");
            System.out.println("Checking if KB entails Magical");
            System.out.println(TTEntails(knowledge_base, alpha2));

            alpha2 = readExpression("Ho");
            System.out.println("Checking if KB entails Horned");
            System.out.println(TTEntails(knowledge_base, alpha2));
            System.out.println("----------------------------------");
        }
    }


    public static void main (String args[]){
        readValue("a");
        readValue("b");
        readValue("c");
    }

}
