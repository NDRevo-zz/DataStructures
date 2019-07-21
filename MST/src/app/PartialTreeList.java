package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.MinHeap;
import structures.PartialTree;
import structures.Vertex;

/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {
    
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	Node ptr = new Node(tree);
    	if (rear == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    	size++;
    }

    /**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
        PartialTreeList ptl = new PartialTreeList();
       
        for (int i = 0; i < graph.vertices.length; i++) {
            Vertex vertex = graph.vertices[i];
            
            PartialTree pt = new PartialTree(vertex);

            //Gets neighbors and loops through and inserts to partialtree
            Vertex.Neighbor vertexneighbor = vertex.neighbors;       
            while (vertexneighbor != null) {
                Arc arc = new Arc(vertex, vertexneighbor.vertex, vertexneighbor.weight);              
                pt.getArcs().insert(arc);
                vertexneighbor = vertexneighbor.next;
            }
            
            ptl.append(pt);
        }
        
        return ptl;
	}
	
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {
        ArrayList<Arc> arcs = new ArrayList<>();
        while (ptlist.size() > 1) {
        	
            PartialTree pt = ptlist.remove();
            MinHeap<Arc> minHeap = pt.getArcs();
            Arc lowarc = minHeap.deleteMin();
                       
            while (lowarc != null) {
                Vertex v1 = lowarc.getv1(),v2 = lowarc.getv2();

                PartialTree pt2 = ptlist.removeTreeContaining(v1);       
                
                if (pt2 == null) {         	
                    pt2 = ptlist.removeTreeContaining(v2);
                }
                
                if (pt2 != null) {                                          	
                    pt.merge(pt2);
                    arcs.add(lowarc);        
                    ptlist.append(pt); 
                    
                    break;
                }
                
                lowarc = minHeap.deleteMin();
            }
        }
        return arcs;
    }
	
    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    			
    	if (rear == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	PartialTree ret = rear.next.tree;
    	if (rear.next == rear) {
    		rear = null;
    	} else {
    		rear.next = rear.next.next;
    	}
    	size--;
    	return ret;
    		
    }

    /**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex) 
    throws NoSuchElementException {
	    if (rear == null) {
	      throw new NoSuchElementException("Empty Tree");
	     } 
    	
    	PartialTree removedpt = null;   
        Node temp = rear;
        
        do{
           PartialTree tree = temp.tree;
           boolean check = vCheck(tree, vertex);         
           if (check) {
             removedpt = tree;
             removeNode(temp);
             break;
           }
           temp = temp.next;          
        } 
        while (temp != rear);
        
        if (removedpt == null) {
              return null;
        }
        else {
              return removedpt;
        }
      }
    
    private boolean vCheck (PartialTree partialTree, Vertex vertex) {
        Vertex vx = vertex;
        
        while (vx.parent != vx) {         
              vx = vx.parent;
        }
        return vx == partialTree.getRoot();
      }
      
      private void removeNode (Node node) {
          Node prev = node;     
          while (!(prev.next == node)) {
              prev = prev.next;
          } 
          Node next = node.next;
          if (next == node && prev == node) {
              rear = null;
              size--;
          }
          else if (next == prev) {
              if (node == rear) {                      
                   rear = rear.next;
              }
              (node.next).next = node.next;            
              size--;
          }
           
          else {
              if (node == rear) {
                  rear = prev;
              }
              prev.next = next;
              size--;
          }
          node.next = null;
      }
    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}


