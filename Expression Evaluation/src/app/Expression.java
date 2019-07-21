package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	expr = expr.replaceAll(" ", ""); //removes all spaces
		String delim = delims + "1234567890"; //symbols and numbers get "ignored"
		int i;
		for (i = 0; i < expr.length(); i++) {
			if (delim.indexOf(expr.charAt(i)) < 0) { // if the char is not a delimiter
				String name = ""; //refreshes
				boolean index = false;
				int j;
				for (j = i; j < expr.length(); j++) { // continues till end of variable/array name					
					if (delim.indexOf(expr.charAt(j)) >= 0) { // if it is a delim
						if (expr.charAt(j) == '[') { // stores the location of the last 
							index = true;
						} 
						i += name.length();
						break;
					}
					else {
						name += expr.charAt(j);
					
					}
				}
				i=j; // this somehow solved my problem, i dont even want to trace through this shit (it basically sets i to where the variable finishes off for me to remember)
				if (index) { //if it is a delim
					Array arr = new Array(name);
					arrays.add(arr);
					System.out.println(arr);
				} else {
					Variable var = new Variable(name);
					vars.add(var);					
					System.out.println(var);
				}
			}

		}
    }
    
   
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	// Remove all whitespaces
        String newExpr = "";

        //Individual characters array
        for (char token : expr.toCharArray())
            if (token != ' ') {
            	newExpr += token;
            }

        expr = newExpr;

        // Adds space so no condition is needed for last character
        expr += " ";

        //Result of seperation for tokenizing
        String formattedString = "";

        //Delims
        String[] stringsToCheckDiff = new String[]{"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ","0123456789","()[]","+-/*"};

        // Loop to add whitespaces between tokens
        for( int i = 0; i < expr.length()-1; i++ ) { // expr.length()-1 because of that extra space
            formattedString += expr.charAt(i);
            char token = expr.charAt(i);
            char afterToken = expr.charAt(i+1);
            boolean differs = false;

            for( int j = 0; j < stringsToCheckDiff.length; j++ ) {
                if( stringsToCheckDiff[j].indexOf(token) >= 0 )
                    differs = stringsToCheckDiff[j].indexOf(token) >= 0 && !(stringsToCheckDiff[j].indexOf(afterToken) >= 0);
            }

            if( !differs ) {
                differs = stringsToCheckDiff[2].indexOf(token) >= 0;
            }

            if( differs )
                formattedString += ' ';
        }
        expr = formattedString;
        
        
        
        
        
        
    	
        ArrayList<String> stringArr = new ArrayList<String>();
        String token = "";
        
        
        //removes spaces and finds the white spaces
        for( int i = 0; i < expr.length(); i++ ) {
            if( expr.charAt(i) == ' ' ) {
                stringArr.add(token);
                token = "";
            } 
            else {
                token += expr.charAt(i);
            }
        }
        
        //String to literals (# values etc.
        for( int i = 0; i < stringArr.size(); i++ ) {
            try {
                stringArr.set( i, "" + Float.parseFloat( stringArr.get(i) ) );
            } catch( NumberFormatException nfe ) {
                String name = stringArr.get(i);
                for( Array arr : arrays ) {
                    if( arr.name.equals( name ) && stringArr.get(i+1).equals("[")) {
                        // turn array list into string and pass to first eval
                        String toPass = "";
                        int j;
                        for( j = i+2; !stringArr.get(j).equals("]"); j++ )
                            toPass += stringArr.get(j);
                        int index = Math.round( evaluate(toPass, vars, arrays) );
                        stringArr.set( i, "" + arr.values[index] );
                        int k;
                        for( k = i+1; !stringArr.get(k).equals("]"); )
                            stringArr.remove(k);
                        stringArr.remove(k);
                        name = "";
                    }
                }
                for( Variable var : vars ) {
                    if( var.name.equals(name) )
                        stringArr.set( i, "" + var.value );
                }
            }
        }
        
        
        
        stringArr.add( 0, "(" );
        stringArr.add( ")" );

        return evaluateClean( stringArr,0, stringArr.size()-1 );
    }

    private static float evaluateClean(ArrayList<String> stringArr, int start, int end ) {
    	
        if( stringArr.size() == 1 )
            return Float.parseFloat( stringArr.get(0) );

        if( isBaseCase( new ArrayList<String>(stringArr.subList( start, end )) ) && start < end ) {
            if ( new ArrayList<String>(stringArr.subList(start,end)).contains("*") || new ArrayList<String>(stringArr.subList(start,end)).contains("/")) {
                for (int i = start; i < end; i++) {
                    String token = stringArr.get(i);
                    if ( token.equals("*") || token.equals("/") ) {
                        float answer = -1;
                        if (token.equals("*")) {
                            answer = Float.parseFloat(stringArr.get(i - 1)) * Float.parseFloat(stringArr.get(i + 1));
                        } else if (token.equals("/")) {
                            answer = Float.parseFloat(stringArr.get(i - 1)) / Float.parseFloat(stringArr.get(i + 1));
                        }
                        stringArr.set(i, "" + answer);
                        stringArr.remove(i + 1);
                        stringArr.remove(i - 1);
                        break;
                    }
                }
            }
            else if( new ArrayList<String>(stringArr.subList(start,end)).contains("+") || new ArrayList<String>(stringArr.subList(start,end)).contains("-") ){
                for( int i = start; i < end; i++ ) {
                    String token = stringArr.get(i);
                    if ( token.equals("+") || token.equals("-") ) {
                        float answer = -1;
                        if (token.equals("-")) {
                            answer = Float.parseFloat(stringArr.get(i - 1)) - Float.parseFloat(stringArr.get(i + 1));
                        } else if (token.equals("+")) {
                            answer = Float.parseFloat(stringArr.get(i - 1)) + Float.parseFloat(stringArr.get(i + 1));
                        }
                        stringArr.set(i, "" + answer);
                        stringArr.remove(i + 1);
                        stringArr.remove(i - 1);
                        break;
                    }
                }
            }
        } 
        else if( isBaseCase( new ArrayList<String>(stringArr.subList( start, end )) ) && start == end ) {
            // remove surrounding parentheses and then proccessed to do whats in the inside
            stringArr.remove(start - 1);
            stringArr.remove( end );
        }

        for( int i = 0; i < stringArr.size(); i++ ) {
            String token = stringArr.get(i);
            if( token.equals(")") ) {
                String delim = token;
                end = i-1;
                int j;
                //goes through loop until it see delim
                for( j = i; !stringArr.get(j).equals(endDelim(delim)); j-- ){
                	
                }
                start = j+1;
                break;
            }
        }
        
        return evaluateClean( stringArr, start, end );
    }


    //if its the start of an array or in parentheses
    private static boolean isBaseCase( ArrayList<String> stringArr ) {
        for( int i = 0; i < stringArr.size(); i++ ) {
            String token = stringArr.get(i);
            if( token.equals("(") || token.equals("[") ) {
            	return false;
            }              
        }
        return true;
    }
    
    
    private static String endDelim( String delim ) {     
        if(delim.equals("]")) {
        	return "[";        	
        }
        if(delim.equals(")")) {
        	return "(";        	
        }
        return "{";        	
        
        
    }
       

}
