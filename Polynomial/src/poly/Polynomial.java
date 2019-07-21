package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */

public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	
	
	
	
	public static Node add(Node poly1, Node poly2) {
		Node result = null;
		 if(poly1 == null) {
			 return poly2;
		 }
		 else if(poly2 == null) {
			 return poly1;
		 }
		 while (poly1 != null &&  poly2 != null) {
			 
			if(poly2.next == null && poly1.next == null && poly2.term.degree == poly1.term.degree) {
				if(poly1.term.coeff + poly2.term.coeff == 0) {
					poly1 = poly1.next;
	            	poly2 = poly2.next;
				}
				else {
					result.next = new Node(poly1.term.coeff + poly2.term.coeff,poly2.term.degree,result.next);
					poly1 = poly1.next;
					poly2 = poly2.next;
				}
					
				 
			 }
					
			else if(result == null) {
	       		if(poly1.term.degree > poly2.term.degree) {
	       			result = new Node(poly2.term.coeff,poly2.term.degree,result); 
	       			
		             
	     
	            
	           }
	           else if(poly2.term.degree > poly1.term.degree) {
	        	   result= new Node(poly1.term.coeff,poly1.term.degree,result);
	        	  
		           
	        	
	            }
	            else{
	            	if(poly1.term.coeff + poly2.term.coeff == 0) {
	            		poly1 = poly1.next;
	                	poly2 = poly2.next;
	    			}
	            	else
	            	 result = new Node(poly1.term.coeff + poly2.term.coeff,poly2.term.degree,null); 	
	            }
	       		
	       	 }	
	       	 else{ 
	       		if(poly1.term.degree > poly2.term.degree) {           
		             result.next = new Node(poly2.term.coeff,poly2.term.degree,result.next);      
		             poly2 = poly2.next;
	             }
	            else if(poly2.term.degree > poly1.term.degree) {  
	            	result.next = new Node(poly1.term.coeff,poly1.term.degree,result.next);  
		             poly1 = poly1.next;
	            }
	            else if(poly2.term.degree == poly1.term.degree){
	            	if(poly1.term.coeff + poly2.term.coeff == 0) {
	    				poly1 = poly1.next;
	                	poly2 = poly2.next;
	    			}
	            	else {
	            		result.next = new Node(poly1.term.coeff + poly2.term.coeff,poly2.term.degree,result.next);
	   	             poly1 = poly1.next;
	   	             poly2 = poly2.next; 
	            		
	            	}
			                   
	            }
	
	       	  }
	        }  
	 
	 
	   while(poly1 == null && poly2 != null){
			  result.next = new Node(poly2.term.coeff,poly2.term.degree,result.next);  
			  poly2 = poly2.next;
					 
			}
	   while(poly2 == null && poly1 !=null) {
		 result.next = new Node(poly1.term.coeff,poly1.term.degree,result.next); 
		 poly1 = poly1.next;			 
	   }

       if(result ==null) {
    	   return result;
       }
       Node prev = null; 
       Node current = result.next; 
       Node next = null; 
       while (current != null) { 
           next = current.next; 
           current.next = prev; 
           prev = current; 
           current = next; 
       } 
       result.next = prev; 

      
       
       return result.next;

	}
	
	
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		Node revert = poly2;
		Node holder = null;
		Node total = null;

		while(poly1!=null){
			while(poly2!=null){
				holder = new Node(poly1.term.coeff*poly2.term.coeff,poly1.term.degree+poly2.term.degree, null);
				total = add(holder, total);
				poly2=poly2.next;
			}
			poly1 = poly1.next;
			poly2 = revert;
		}

		return total;
	
	}
	
	
		
	
	public static float evaluate(Node poly, float x) {
		float total = 0;
		if(x != 0) {
			for(Node ptr = poly; ptr !=null; ptr=ptr.next) {
				total += ptr.term.coeff * (Math.pow(x,ptr.term.degree));
			}
			return total;
		}
		return 0;
		
	}
	
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
