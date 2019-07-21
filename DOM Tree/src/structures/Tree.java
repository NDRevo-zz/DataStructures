package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		root = recursion();
	}
	private TagNode recursion(){
		if (sc.hasNextLine()) {
			String current = sc.nextLine();
	
			//Closing tag
			if (current.length() > 1 && current.charAt(1) == '/' ) { 
				return null;
			} 
			else if (current.charAt(0) == '<'){ //If child
				TagNode newTag = new TagNode(removeBracket(current), null, null);
				newTag.firstChild = recursion(); //Recurses until no more brackets, then goes to sibling
				newTag.sibling = recursion();
				return newTag;
			} 
			else { //If sibling
				//No removeBracket beacuse there  isnt one
				TagNode newTag = new TagNode(current, null, null);
				newTag.sibling = recursion();
				return newTag;
			}
		} 
		else{
			return null;
		}		
	}
	
	private String removeBracket(String s) {
		for(int i = 0; i < s.length();i++) {
			if(s.charAt(i) == '<') {
				s = s.substring(1,s.length());				
			}
			if(s.charAt(i) == '>') {
				s = s.substring(0,s.length()-1);
			}			
		}
		return s;		
	}
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		if(oldTag.equals(newTag)){ 
			return;
		}
		recursionReplace(oldTag,newTag,root);
	}
	private void recursionReplace(String oldTag,String newTag,TagNode root) {
		if(root == null) {
			return;
		}
		if(root.tag.equals(oldTag)) {
			root.tag=newTag;
		}
		recursionReplace(oldTag,newTag,root.firstChild);
		recursionReplace(oldTag,newTag,root.sibling);
	}

	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row){
		boldRecursion(row, root);
	}
	private void boldRecursion(int row, TagNode root){
		if (root == null) {
			return; 
		}
		if (root.tag.equals("table")) { // Needs to find where table begins and then each child will be a row
			TagNode tr = root.firstChild;
			for (int count = 0; count < row -1; count ++) { //row-1 because root.firstChild is already the first one
				if (tr.sibling != null) 
					tr = tr.sibling;
			}
			TagNode td = tr.firstChild; 
			while (td != null) {
				TagNode toBold = new TagNode("b", td.firstChild, null);
				td.firstChild = toBold;
				td = td.sibling;
			}
		}
		boldRecursion(row, root.firstChild);
		boldRecursion(row, root.sibling);
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		removeRecursion(tag, root);
	}
	private TagNode removeRecursion(String tag, TagNode root){
		if(root == null){
			return null;
		}
		
		TagNode firstchild = removeRecursion(tag, root.firstChild);
		TagNode sibling = removeRecursion(tag, root.sibling);
		
		if(tag.equals(root.tag)){
			
			//If ol/ul and removal of inner li if any
			if(tag.equals("ol") || tag.equals("ul")){
				TagNode curr = root.firstChild;
				for(curr = root.firstChild; curr != null; curr = curr.sibling){ //Traverse to find list
					if(curr.tag.equals("li")){
						curr.tag = "p";
					}
				}
			}
			
			
			//Takes previous tag before removal tag and makes it the sibling of the removal tag 
			TagNode front;
			TagNode prev = null;
			for(front = firstchild; front != null; front = front.sibling){
				prev = front;
			}
			prev.sibling = sibling;
			return firstchild;
		} 
		else{
			root.sibling = sibling;
			root.firstChild = firstchild;
			return root;
		}
		
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		if(word.equals(null)) return;
		if(tag.equals("em") || tag.equals("b")) {
			addRecursion(word.toLowerCase(),tag,root);
		}
	}
	private void addRecursion(String word, String tag, TagNode root) {
		
		if(root == null)return;
		if(root.tag.equals("p")) {
			TagNode start = root.firstChild;
			if(start.tag.contains(word)) {
				String begin = start.tag.substring(0,start.tag.indexOf(word));				
				String end = start.tag.substring(start.tag.indexOf(word)+word.length(),start.tag.length());
				TagNode last = new TagNode(end,null,null);

		}
			
		}
		
		
		
		addRecursion(word,tag,root.firstChild);
		addRecursion(word,tag,root.sibling);
		 
		
	}
	
	
	
	
	
	
	
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
