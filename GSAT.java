import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GSAT {
    static ArrayList<ArrayList<String>> clauses = new ArrayList<ArrayList<String>>();
    static int numVariable=0;
    static int numClauses=0;
    static LogicalExpression knowledge_base = new LogicalExpression();

    public static void ReadFile(String filepath){
        try {
            File file=new File(filepath);
            Path path=file.toPath();
            String contentString=Files.readString(path);
            //Split the string by 0
            String[] content=contentString.split("\\s0");
            //print the title 
            String titleString=content[0];
            String[] title=titleString.split("\n");
            for(int i=0;i<title.length;i++) {
                if(title[i].charAt(0)=='p') {
                    String[] p = title[i].split("\\s");
                    numVariable = Integer.parseInt(p[2].trim());
                    numClauses = Integer.parseInt(p[3].trim());
                    System.out.println("Num clauses: " + numClauses + "\nNum Variable: " + numVariable);

                }else if(title[i].charAt(0)!='c') {
                    String[] clause = title[i].trim().split("\\s+");
                    ArrayList<String> list = new ArrayList<String>();
                    for(int j = 0; j < clause.length; j++) {
                        list.add(clause[j].trim());
                    }
                    clauses.add(list);
                }
            }
            for(int i=1;i<content.length;i++) {
                if(content[i].charAt(0)!='c') {
                    String[] clause = content[i].trim().split("\\s+");
                    ArrayList<String> list = new ArrayList<String>();
                    for(int j = 0; j < clause.length; j++) {
                        list.add(clause[j].trim());
                    }
                    clauses.add(list);
                }
            }
            //remove the last empty element in the list
            clauses.remove(clauses.size()-1);


        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    //Generate a random map 
    public static HashMap<String, Boolean> RandomMap(){
        HashMap<String, Boolean> map=new HashMap<String, Boolean>();
        for(int i=1;i<numVariable+1;i++) {
            Boolean b=getRandomBoolean();
            map.put(String.valueOf(i), b);
        }
        return map;
    }
    
    public static Boolean getRandomBoolean() {
        Boolean b=true;
        if(Math.random()>0.5) {
            b=true;
        }else {
            b=false;
        }
        return b;
    }
    
    public static String heuristic(HashMap<String, Boolean> map, LogicalExpression knowledge_base) {
        int max=0;
        String symbolString="";
        for(Map.Entry<String, Boolean> entry: map.entrySet()) {
            HashMap<String, Boolean> map1=flip(map, entry.getKey());
            int num=heuristicVal(map1, knowledge_base);
            if(num>max) {
                max=num;
                symbolString=entry.getKey();
            }
        }
        return symbolString;
    }
    //Get the number of clauses satisfied 
    public static int heuristicVal(HashMap<String, Boolean> map, LogicalExpression knowledge_base) {
        int result=0;
        for(int i=0;i<numClauses;i++) {
            if(TTalgorithm.PLTrue(knowledge_base.getSubexpressions().get(i), map)) {
                result++;
            }
        }
        return result;
    }
    
    public static HashMap<String, Boolean> flip(HashMap<String, Boolean> map, String key){
        HashMap<String,Boolean> newmap = new HashMap<String,Boolean>();
        newmap.putAll(map);
        Boolean b = map.get(key);
        newmap.put(key, !b);
        return newmap;
    }
    //transfer the file to Logical Expression
    public static void fileToLE(ArrayList<ArrayList<String>> clauses) {
        knowledge_base.setConnective("and");
        ArrayList<String> resultStrings=new ArrayList<String>();
        for(int i=0;i<clauses.size();i++) {
            String result="";
            if(clauses.get(i).size()==1) {
                result+=clauses.get(i).get(0);
            }else{
                result+="(or ";
                for(int j=0;j<clauses.get(i).size();j++) {
                    if(clauses.get(i).get(j).contains("-")) { // negate
                        String aString=clauses.get(i).get(j);
                        aString=aString.substring(1);
                        result+="(not "+aString+") ";
                    }
                    else if(clauses.get(i).get(j).length()==1) {
                        result+=clauses.get(i).get(j)+" ";
                    }
                    else {
                        result+=clauses.get(i).get(j)+" ";
                    }
                }
                result+=")";
            }
            resultStrings.add(result);
        }
        for(int i=0;i<resultStrings.size();i++) {
            LogicalExpression subExpression = TTalgorithm.readExpression(resultStrings.get(i));
            knowledge_base.setSubexpression( subExpression );
        }
    }
    
    public static HashMap<String, Boolean> GSATalgorithm(int maxFlips, int maxTries, boolean tracing, LogicalExpression knowledge_base){
        System.out.println("Running GSAT...");
        for(int i = 0; i < maxTries; i++){
            HashMap<String, Boolean> map= RandomMap();
            if(tracing){
                System.out.println("Generated " + i+1 + "th random assignment...");
                System.out.println("The ramdon map is"+map.toString());
            }
            for(int j = 0 ; j < maxFlips; j++){
                if(TTalgorithm.PLTrue(knowledge_base, map)){
                    return map;
                }
                String a = heuristic(map,knowledge_base);
                map = flip(map,a);
                if(tracing){
                    System.out.println("According to heuristic, index " + a + " is flipped.");
                    System.out.println("Model is now: ");
                    System.out.println(map.toString());
                }
            }
        }
        System.out.println("No Satisfying Assignment Found.");
        return null;
    }
    
    public static void run() {
        Scanner scan = new Scanner(System.in);
        System.out.println("What problem do you want to test? (Type number)");
        System.out.println("1: Problem 1\n2: nqueens of n=4\n3: nqueens of n=8\n4: nqueens of n=12\n5: nqueens of n = 16\n6: Quinn.cnf\n7: Aim-50-1_6-yes1-4.cnf\n8: Quit");
        int choice = scan.nextInt();
        while(choice!=8) {
            System.out.println("Please enter the value of MAX-FLIPS.");
            int maxFlip=scan.nextInt();
            System.out.println("Please enter the value of MAX-TRIES.");
            int maxTry=scan.nextInt();
            System.out.println("Do you want the tracing function for the problem? (y/n)");
            String tracingString=scan.next();
            boolean tracing=false;
            if(tracingString.equals("y")) {
                tracing=true;
            }
            HashMap<String, Boolean> result=new HashMap<String, Boolean>();
            if(choice==1) {
                ReadFile("CSC242_Project_2_cnf/problem1.cnf");
                fileToLE(clauses);
                result=GSATalgorithm(maxFlip,maxTry,tracing,knowledge_base);
            }else if(choice==2) {
                ReadFile("CSC242_Project_2_cnf/nqueens_4.cnf");
                fileToLE(clauses);
                result=GSATalgorithm(maxFlip,maxTry,tracing,knowledge_base);
            }else if(choice==3) {
                ReadFile("CSC242_Project_2_cnf/nqueens_8.cnf");
                fileToLE(clauses);
                result=GSATalgorithm(maxFlip,maxTry,tracing,knowledge_base);
            }else if(choice==4) {
                ReadFile("CSC242_Project_2_cnf/nqueens_12.cnf");
                fileToLE(clauses);
                result=GSATalgorithm(maxFlip,maxTry,tracing,knowledge_base);
            }else if(choice==5) {
                ReadFile("CSC242_Project_2_cnf/nqueens_16.cnf");
                fileToLE(clauses);
                result=GSATalgorithm(maxFlip,maxTry,tracing,knowledge_base);
            }else if(choice==6) {
                ReadFile("CSC242_Project_2_cnf/quinn.cnf");
                fileToLE(clauses);
                result=GSATalgorithm(maxFlip,maxTry,tracing,knowledge_base);
            }else if(choice==7) {
                ReadFile("CSC242_Project_2_cnf/aim-50-1_6-yes1-4.cnf");
                fileToLE(clauses);
                result=GSATalgorithm(maxFlip,maxTry,tracing,knowledge_base);
            }else {
                System.out.println("You have quited!");
            }

            if(result==null) {
                System.out.println("Ooooooooops!Sorry!Please try again!");
            }else {
                System.out.println("The result has been found!");
                System.out.println("The truth assignment is "+result);
                break;
            }
            System.out.println("What problem do you want to test? (Type number)");
            System.out.println("1: Problem 1\n2: nqueens of n=4\n3: nqueens of n=8\n4: nqueens of n=12\n5: nqueens of n = 16\n6: Quinn.cnf\n7: Aim-50-1_6-yes1-4.cnf\n8: Quit");
            choice=scan.nextInt();
        }
    }

    public static void main(String[] args) {
        run();
    }

}
