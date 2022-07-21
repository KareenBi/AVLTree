/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */
public class AVLTree {	
	private int size; //no need (size==root.size)
	public IAVLNode root; // pointer for the tree's root 
	public IAVLNode min; // pointer for the tree's min node 
	public IAVLNode max; // pointer for the tree's max node
	
	public AVLTree() { //constructor
		this.size = 0;
		this.root = null;
		this.min = null;
		this.max = null;
	}
	
	//we'll use it in split
	public AVLTree(IAVLNode node) {
		this.size = 1;
		this.root = node;
		root.setParent(null);
	}
	
  /**
   * public boolean empty()
   *
   *   * returns true if and only if the tree is empty
   *
   */
  public boolean empty() {
	  return (this.root == null);
  }

 /**
   * public String search(int k)
   *
   * returns the info of an item with key k if it exists in the tree
   * otherwise, returns null
   */
  public String search(int k){
	IAVLNode node = treePosition(k);
	if(node.getKey() == k) {
		return node.getValue();
	}else{
		return null;
	}
  }
  
  /**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the AVL tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   * promotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
   * returns -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String i) {
	   IAVLNode node = new AVLNode(k,i);
	   node = binaryTreeInsertion(node);
	   if(node == null) {
		   return -1;
	   }
	   if(this.size==1) {
		   //insert to an empty tree
		   return 0;
	   }	   
	  fixExtremumInsert(node);
	  int rebalance_steps = insert_rebalance(node);
	  return rebalance_steps;
   }
   
   //updates tree.min and tree.max
   private void fixExtremumInsert(IAVLNode node) {
	if(node.getKey()>this.max.getKey()) {
		this.max = node;
	}
	if(node.getKey()<this.min.getKey()) {
		this.min = node;
	}
	
}

/**
 * 
 * @param key of the new node
 * @return node if found, else its tree position
 */
   //contains two pointers' one for the node and the second for the node's parent. 
   // for deletion- finds the node and return it
   // for insert- return the parent of the node we want to insert.
   // runs in O(log(n)) as the tree's height  
   IAVLNode treePosition(int k) {
	   IAVLNode current = this.root;
	   IAVLNode prev = this.root;
	   while(current.getKey() != -1){
		   //not an external leaf
		   if(current.getKey() == k) {
			   //node found
			   return current;
		   }if(current.getKey() < k) {
			   prev = current;
			   current = current.getRight();
		   }if(current.getKey() > k) {
			   prev = current;
			   current = current.getLeft();
		   }
	   }
	   return prev;
   }
   
   //inserts a node as without
   // if the tree is empty, insert the node as the root and update is to be the max and min node as well.
   // this function calls to "position tree" which runs in O(logn) as we mentioned above' here the insertion itself runs in O(1). 
   private IAVLNode binaryTreeInsertion(IAVLNode node) {
	   if(this.empty()) { 
		  this.root = node;
		  this.min = node;
		  this.max = node;
		  this.size++;
		  root.setLeft(new AVLNode());
		  root.setRight(new AVLNode());
		  return node;
	  }
	  IAVLNode newParent = treePosition(node.getKey());
	  if(newParent == null || newParent.getKey() == node.getKey()){
		  return null;
	  }
	  else{ 
		  node.setParent(newParent);
		  if(node.getKey()<newParent.getKey()) {
			  newParent.setLeft(node);
		  }else{
			  newParent.setRight(node);
		  }
		  size++;
		  return node;
	  }
   }
   
  /**
   *  
   * @param inserted node
   * @return total rebalance steps
   */	
   public int insert_rebalance(IAVLNode node) {
	   if(node==this.root) {return 0;}
	   if(node.getParent().hasTwoChildren()) {
		   incrementAllSizes(node);
		   return 0;} //inserting to an unary node
	   int rebalanceSteps = 0;
	   IAVLNode parent;
	   while (node != this.getRoot()) {
		   parent = node.getParent();
		   if(rank_sum(parent) == 1){
			   //case (1,0), (0,1) result of inserting to a leaf or promotion
			   rebalanceSteps++;
			   parent.incrementHeight(+1);
			   parent.updateSize(+1);
		   }
		   else if(rank_diff(parent)==-2 && leftDiff(node)==1){ 
			   //case parent(0,2) node (1,x)
			   rotateRight(parent);
			   incrementAllSizes(node);
			   return rebalanceSteps+1;  //check if it needs to be +2!
		   }
		   else if(rank_diff(parent)==-2 && rightDiff(node)==1){
			   //case parent (0,2) node (x,1)
			   rotateLeft(node);
			   rotateRight(parent);
			   incrementAllSizes(node.getParent());
			   return rebalanceSteps+2;
		   }
		   else if(rank_diff(parent)==2 && rightDiff(node)==1) {
			   //parent (2,0) node (x,1)
			   rotateLeft(parent);
			   incrementAllSizes(node);
			   return rebalanceSteps+1;
		   }
		   else if((rank_diff(parent)==2) && leftDiff(node)==1){
			   //parent (1,3) (2,1)
			   rotateRight(node);
			   rotateLeft(parent);
			   incrementAllSizes(node.getParent());
			   return rebalanceSteps+2;
			   }
		   else {
			   parent.updateSize(+1);
		   }
		   node = parent;
		   }
	   return rebalanceSteps;
   }
 
   /**
    * @param node
    * @return height differene between the node and its rightChild - O(1)
    */
   private int rightDiff(IAVLNode node) {
	   int rightDiff = node.getHeight() - node.getRight().getHeight();
	   return rightDiff;
	   }
   
   /**
    * @param node
    * @return height differene between the node and its leftChild - O(1)
    */
   private int leftDiff(IAVLNode node) {
	   int leftDiff = node.getHeight() - node.getLeft().getHeight(); 
	   return leftDiff;
	   }

   private int rank_diff(IAVLNode node) {
	   return leftDiff(node) - rightDiff(node);
	   }
   
   private int rank_sum(IAVLNode node) {
	   return leftDiff(node) + rightDiff(node);
	   }
   
   // right rotation of oldroot node with his left child called newRoot - O(1)
   private void rotateRight(IAVLNode oldRoot) {
		IAVLNode newRoot = oldRoot.getLeft();
		if(oldRoot != this.getRoot()) {
			if(oldRoot.isRightChild()) {
				oldRoot.getParent().setRight(newRoot);
			}else{
				oldRoot.getParent().setLeft(newRoot);
			}
			newRoot.setParent(oldRoot.getParent());
		}else {
			this.root = newRoot;
		}
		oldRoot.setLeft(newRoot.getRight());
		newRoot.getRight().setParent(oldRoot);
		oldRoot.setParent(newRoot);
		newRoot.setRight(oldRoot);
		
		oldRoot.setHeight(1 + Math.max(oldRoot.getRight().getHeight(), oldRoot.getLeft().getHeight()));
        newRoot.setHeight(1 + Math.max(newRoot.getRight().getHeight(), newRoot.getLeft().getHeight()));
        oldRoot.setSize(oldRoot.getLeft().getSize() + oldRoot.getRight().getSize() + 1);
        newRoot.setSize(newRoot.getLeft().getSize() + newRoot.getRight().getSize() + 1);   
	}
   
    // left rotation of oldRoot node with his right child called newRoot - O(1)
	private void rotateLeft(IAVLNode oldRoot){
		IAVLNode newRoot = oldRoot.getRight();
		if(oldRoot != this.getRoot()) {
			if(oldRoot.isRightChild()) {
				oldRoot.getParent().setRight(newRoot);
			}else{
				oldRoot.getParent().setLeft(newRoot);
			}
			newRoot.setParent(oldRoot.getParent());
		}else {
			this.root = newRoot;
		}
		newRoot.setParent(oldRoot.getParent());
		oldRoot.setRight(newRoot.getLeft());
		newRoot.getLeft().setParent(oldRoot);
		oldRoot.setParent(newRoot);
		newRoot.setLeft(oldRoot);
		oldRoot.setHeight(1 + Math.max(oldRoot.getRight().getHeight(), oldRoot.getLeft().getHeight()));
        newRoot.setHeight(1 + Math.max(newRoot.getRight().getHeight(), newRoot.getLeft().getHeight()));
        oldRoot.setSize(oldRoot.getLeft().getSize() + oldRoot.getRight().getSize() + 1);
        newRoot.setSize(newRoot.getLeft().getSize() + newRoot.getRight().getSize() + 1);
	}
	
	//increments ancestor's sizes after insertion - O(log(n))
	void incrementAllSizes(IAVLNode node) {
		while(node != this.root) {
			node = node.getParent();
			node.updateSize(+1);
		}
	}
  
	  /**
	   * public int delete(int k)
	   *
	   * deletes an item with key k from the binary tree, if it is there;
	   * the tree must remain valid (keep its invariants).
	   * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
	   * demotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
	   * returns -1 if an item with key k was not found in the tree.
	   */
	//O(log(n))
	public int delete(int k) {
		//checks if the tree is empty or the node doesnt exist in tree
		if ((this.root == null) || (this.search(k)== null) ) {
			return -1;
		}
	    if (this.size() == 1) {
	    	//node is the only node in tree
	    	this.min = null;
	        this.max = null;   
	        this.root=null;
	        this.size=0;
	        return 0;
	    } 
	    else {
		    //node exist
	    	if (k == this.min.getKey()) {
	    		this.min = (AVLNode) successor(min);
	        }
	        if (k == this.max.getKey()) {
	        	this.max = (AVLNode) predecessor(max);
	        }
	    }
	    IAVLNode position = treePosition(k);
	    if (position==this.root && this.size<=3) {
	    	//tree size is 3 or less and we need to delete the root
	    	if (position.hasTwoChildren()) {
	    		this.root= position.getRight();
	    		root.setLeft(position.getLeft());
	    		this.root.updateSize(-1);
	    		this.root.incrementHeight(1);	
	    	}
	    	else if (!position.getLeft().isRealNode()) {
	    		this.root= position.getRight();
	    		this.root.updateSize(-1);
	    	}
	    	else {
	    		this.root= position.getLeft();
	    		this.root.updateSize(-1);
	    	}
	    	this.size--;
	    	return 0;
	    }
	    this.size --;
	    IAVLNode parent = position.getParent();
	    if (position.isLeaf()) {
	    	//node is a leaf
	    	deleteLeaf(position);
	    	decrementSize(parent);
	        return (rebalanceDelete(parent));
	    }if (position.isUnaryNode()) {
	    	//node is an unary node
	    	deleteUnaryNode(position);
	    	decrementSize(parent);
	        return (rebalanceDelete(parent));
	    }
	    //deletes if node has two sons
	    IAVLNode nodeToDelete = swap(position);
	    parent = nodeToDelete.getParent();
	    deleteRegularNode(nodeToDelete);
	    decrementSize(parent);
	
		return (rebalanceDelete(parent));
	}
	
	//swaps between node and his successor and update min and max nodes - O(log(n))	
	private IAVLNode swap(IAVLNode node) {
		IAVLNode successor = successor(node);
	    int nodekey = node.getKey();
	    String nodevalue = node.getValue();
	    int nodeSize = node.getSize();
	    node.setKey(successor.getKey());
	    node.setValue(successor.getValue());
	    node.setSize(nodeSize);
	    successor.setKey(nodekey);
	    successor.setValue(nodevalue);
	    successor.setSize(nodeSize);
	    if (node == min) {
	    	min = successor;
	    }
	    if (successor == max) {
	    	max = node;
	    }
	    return successor;
	}
	 
	//deleting node with two children - O(1)
	private void deleteRegularNode(IAVLNode node) {
		if (node.isLeaf()) {
			deleteLeaf(node);
		} 
		else {
			deleteUnaryNode(node);
		}
	}
	
	//delete a leaf - O(1)
	private void deleteLeaf(IAVLNode node) {
		if (node == this.root) {
			this.root = null;
		} 
		else {
			IAVLNode parent = (AVLNode) node.getParent();
			if (parent.getRight() == node) {
				node.getParent().setRight(new AVLNode());
				node.getRight().setParent(parent);
			} 
			else {
				node.getParent().setLeft(new AVLNode());
				
			}
		}
	}
	 
	//delete a unary node - O(1) 
	private void deleteUnaryNode(IAVLNode node) {
		if (node == this.root) {
    		if (node.getRight().isRealNode()) {
    			this.root = node.getRight();
    			this.root.setParent(null);
    		} 
    		else {
    			this.root = node.getLeft();
    			this.root.setParent(null);
    		}
    	} 
    	else {
    		IAVLNode parent =(AVLNode) node.getParent();
    		{
    			if (parent.getRight() == node) {
    				if (node.getRight().isRealNode()) {
    					parent.setRight(node.getRight());
                        node.getRight().setParent(parent);
    				} 
    				else {
    					parent.setRight(node.getLeft());
    					node.getLeft().setParent(parent);
    				}
    			} 
    			else {
    				if (node.getLeft().isRealNode()) {
    					parent.setLeft(node.getLeft());
    					node.getLeft().setParent(parent);
    				} 
    				else {
    					parent.setLeft(node.getRight());
    					node.getRight().setParent(parent);
    				}
    			}
    		}
    	}
    }
	 
	//increaseSize of the node's ancestors - O(log(n))
    private int increaseSize(IAVLNode node, int increase) {
        if (node == this.root && this.size==1) {
        	return 0;
        }
        if (node == this.root) {
        	node.setSize(node.getSize() + increase);
        	return (node.getSize());
        }
        node.setSize(node.getSize() + increase);
        return 1 + increaseSize(node.getParent(), increase);
    }
    //return decreseSize on node and 1, which calls to increaseSize that runs in O(log(n))
    private int decrementSize(IAVLNode node) {
        return decreaseSize(node, 1);
    }
    //increaseSize that runs in O(log(n))
    private int decreaseSize(IAVLNode node, int decrease) {
        return increaseSize(node, -decrease);
    }
    
    /**
     * @param node
     * @return node's successor
     * complexity - O(log(n))
     */
    private IAVLNode successor(IAVLNode node) {
        IAVLNode current = node;
        if (node.getRight().isRealNode()) {
            current = node.getRight();
            while (current.getLeft().isRealNode()) {
                current = current.getLeft();
            }
            return current;
        } 
        else {
            while (current.getParent().getRight() == current) {
                current = current.getParent();
            }
            if (current.getParent() == null) {
            	return current;
            }
            else {
            	return current.getParent();
            }
        }
    }
    
	/**
	 * @param node
	 * @return node's predecessor
	 * complexity - O(log(n))
	 */
	private IAVLNode predecessor(IAVLNode node) {
        IAVLNode current = node;
        if (node.getLeft().isRealNode()) {
        	current = node.getLeft();
            while (current.getRight().isRealNode()) {
            	current = current.getRight();
            }return current;
        }else{
            while (current.getParent().getLeft() == current) {
            	current = current.getParent();
            }if (current.getParent() == null) {
            	return current;
            }else{
            	return current.getParent();
            }
        }
    }
	
	//rebalancing of a tree after deletion - O(log(n))
	private int rebalanceDelete(IAVLNode node) {
		int cnt =0;
		while (node != null && !rankDiffLegal(node)) {
			IAVLNode parent = node.getParent();
			if (rightDiff(node) == 2 && leftDiff(node) == 2) {
				node.setHeight(node.getHeight() - 1);
				cnt+=1;
			}
			if (leftDiff(node) == 3 && rightDiff(node) == 1 && leftDiff(node.getRight())==1 && rightDiff(node.getRight())==1 ) {
				rotateLeft(node);
	            cnt += 3;	   
		    }
			if (leftDiff(node) == 1 && rightDiff(node) == 3 && leftDiff(node.getLeft())==1 && rightDiff(node.getLeft())==1) {
				rotateRight(node);
				cnt +=3;
			}
			if (leftDiff(node) == 3 && rightDiff(node) == 1 && leftDiff(node.getRight())==2 && rightDiff(node.getRight())==1 ) {
				rotateLeft(node);
				cnt +=3;
			}
			if (leftDiff(node) == 1 && rightDiff(node) == 3 && leftDiff(node.getLeft())==1 && rightDiff(node.getLeft())==2) {
				rotateRight(node);
				cnt +=3;
			}
			if (leftDiff(node) == 3 && rightDiff(node) == 1 && leftDiff(node.getRight())==1 && rightDiff(node.getRight())==2 ) {
				rotateRight(node.getRight());
                rotateLeft(node);
                cnt += 6;
			}
			if (leftDiff(node) == 1 && rightDiff(node) == 3 && leftDiff(node.getLeft())== 2 && rightDiff(node.getLeft())==1) {
				rotateLeft(node.getLeft());
                rotateRight(node);
                cnt += 6;
			}
			
			node= parent;	
		}
		return cnt;
	}
	
	/**
	 * @param node
	 * @return True if rankDiff is legal, otherwise False
	 */
    private boolean rankDiffLegal(IAVLNode node) {
        return (leftDiff(node) == 1 && rightDiff(node) == 1) || (leftDiff(node) == 2 && rightDiff(node) == 1) || (leftDiff(node) == 1 && rightDiff(node) == 2);
    }	
			
    /**
     * public String min()
     *
     * Returns the info of the item with the smallest key in the tree,
     * or null if the tree is empty
     */
    public String min(){
    	if (min== null) {
    		return null;
    		}
    	return min.getValue();
    	}

    /**
     * public String max()
     *
     * Returns the info of the item with the largest key in the tree,
     * or null if the tree is empty
     */
    public String max(){
    	if (max== null) {
    		return null;
    	}
    	return max.getValue();
    }

	  /**
	   * public int[] keysToArray()
	   *
	   * Returns a sorted array which contains all keys in the tree,
	   * or an empty array if the tree is empty.
	   * complexity O(n)
	   */
	  public int[] keysToArray(){
	        int[] keys = new int[this.size];
	        IAVLNode[] nodes = inOrder(this.root);
	        for(int i=0; i<this.size; i++) {
	        	keys[i] = nodes[i].getKey();
	        }
	        return keys;
	  }
	  
	  /**
	   * public String[] infoToArray()
	   *
	   * Returns an array which contains all info in the tree,
	   * sorted by their respective keys,
	   * or an empty array if the tree is empty.
	   * complexity O(n)
	   */
	  public String[] infoToArray(){
		  String[] info = new String[this.size];
		  IAVLNode[] nodes = inOrder(this.root);
		  for(int i=0; i<this.size; i++) {
			  info[i] = nodes[i].getValue();
			  }
		  return info;                   
	  }
	  
	  /**
	   * 
	   * @param root
	   * @return array of tree nodes in order
	   * complexity O(n)
	   */	  
	  private IAVLNode[] inOrder(IAVLNode root) {
		if(this.empty()) {
			return new IAVLNode[0];
		}else {
			IAVLNode[] arr = new IAVLNode[this.size];
			arr = inOrderRec(arr, root, 0);
			return arr;
		}
		  
	  }
	  
	  //O(n)
	  private IAVLNode[] inOrderRec(IAVLNode[] arr, IAVLNode node, int i) {
		  if(node.getLeft().isRealNode()) {
			  inOrderRec(arr, node.getLeft(), i);
		  }int leftSubtree = node.getLeft().getSize();
		  arr[i + leftSubtree] = node;
		  if(node.getRight().isRealNode()) {
			  inOrderRec(arr, node.getRight(), i+leftSubtree+1);
		  }return arr;
	  } 

	   /**
	    * public int size()
	    *
	    * Returns the number of nodes in the tree.
	    *
	    * precondition: none
	    * postcondition: none
	    */
	   public int size(){
		   return(this.size);
	   }
	   
	     /**
	    * public int getRoot()
	    *
	    * Returns the root AVL node, or null if the tree is empty
	    *
	    * precondition: none
	    * postcondition: none
	    */
	   
	   public IAVLNode getRoot(){
		  return(this.root);
	   }
	   
	     /**
	    * public string split(int x)
	    *
	    * splits the tree into 2 trees according to the key x. 
	    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
		  * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
	    * postcondition: none
	    */   
	   public AVLTree[] split(int x){
		   IAVLNode node = treePosition(x);
		   AVLTree t1 = new AVLTree(); //keys(t1) < x
		   AVLTree t2 = new AVLTree(); //keys(t2) > x
		   t1.root = node.getLeft();
		   t1.min = this.min;
		   t1.max = predecessor(node);
		   t1.size = node.getLeft().getSize();
		   t2.root = node.getRight();
		   t2.max = this.max;
		   t2.min = successor(node);
		   t2.size = node.getRight().getSize();
		   IAVLNode parent;
		   AVLTree otherTree = new AVLTree();
		   while(node!=this.root) {
			   parent = node.getParent();
			   if(node.isRightChild()) {
				   otherTree.root = parent.getLeft();
				   otherTree.size = parent.getLeft().getSize();
				   otherTree.min = t1.min;
				   otherTree.max = t1.max;
				   t1.join(parent, otherTree);
			   }else{
				   otherTree.root = parent.getRight();
				   otherTree.size = parent.getRight().getSize();
				   otherTree.min = t2.min;
				   otherTree.max = t2.max;
				   t2.join(parent, otherTree);
			   }node = parent;
		   }AVLTree[] resultTrees = {t1, t2};
		   return resultTrees; 
	   }
	   
	   /**
	    * public join(IAVLNode x, AVLTree t)
	    *
	    * joins t and x with the tree. 	
	    * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
		  * precondition: keys(x,t) < keys() or keys(x,t) > keys(). t/tree might be empty (rank = -1).
	    * postcondition: none
	    */   
	   public int join(IAVLNode x, AVLTree t){
		   if(t.empty() && this.empty()) {
			   x.setHeight(0);
			   x.setSize(1);
			   this.root = x;
			   this.min = x;
			   this.max = x;
			   this.size++;
			   return (1);
		   }else if(t.empty() && !this.empty()) {
			   this.insert(x.getKey(), x.getValue());
			   return (this.getRoot().getHeight() + 1);
		   }else if(!t.empty() && this.empty()) {
			   t.insert(x.getKey(), x.getValue());
			   this.max = t.max;
			   this.min = t.min;
			   this.size = t.size;
			   this.root = t.root;
			   return (t.getRoot().getHeight() + 1);
		   }//both trees aren't empty
		   int thisHeight = this.getRoot().getHeight();
		   int otherHeight = t.getRoot().getHeight();
		   if(thisHeight == otherHeight) {
			   joinEqualHeight(x, t);
		   }else if(x.getKey() > this.getRoot().getKey()) {
			   //this tree is leftSubtree of x
			   joinBiggerKeyTree(x, t);
			   this.root.incrementHeight(+1);
		   }else {
			   //this tree is rightSubtree of x
			    joinSmallerKeyTree(x, t);
			    this.root.incrementHeight(+1);
		   }return (Math.abs(thisHeight + otherHeight) + 1);
	   }

	    /**
	     * @param1 leftTree is a subtree with value smaller than x.getKey
	     * @param2 rightTree is a subtere with values bigger than x.getKey
	     * @param3 x a node for joining the two subtrees
	     * 
	     * the function joins leftTree and rightTree have equal heights
	     * complexity - O(log(n))
	     */
		private void joinEqualHeight(IAVLNode x, AVLTree t) {
			if(t.getRoot().getKey()<this.getRoot().getKey()) {
				//t < x < this
				x.setLeft(t.getRoot());
				x.setRight(this.getRoot());
				t.getRoot().setParent(x);
				this.getRoot().setParent(x);
				this.root =  x;
				this.size = this.size + t.size + 1;
				this.min = t.min;
			}else if(t.getRoot().getKey()>this.getRoot().getKey()) {
				//this < x < t
				x.setLeft(this.getRoot());
				x.setRight(t.getRoot());
				this.getRoot().setParent(x);
				t.getRoot().setParent(x);
				this.root = x;
				this.size = this.size + t.size + 1;
				this.max = t.max;
			}x.setHeight(x.getLeft().getHeight() + 1);
			x.setSize(x.getLeft().getSize() + x.getRight().getSize()+1);
	}
		
		/**
		 * 
		 * @param x - node to join
		 * @param other - other AVLTree to join
		 * 
		 * this AVLTree has smaller values than other
		 */
		private void joinBiggerKeyTree(IAVLNode x, AVLTree other) {
			this.max = other.max;
	        this.size = this.size + other.size() + 1;
	        if (other.getRoot().getHeight() > this.getRoot().getHeight()) { 
	        	//rightSubtree(other) has higher Height
	        	int k = this.root.getHeight();
	            IAVLNode node = other.getRoot();
	            while (node.getHeight() > k) {
	                node = node.getLeft();
	            }rightHigher_other(node, x, other);
	        } else { 
	        	//leftSubtree(this) has higher Height
	            IAVLNode node = this.getRoot();
	            while (node.getHeight() > other.getRoot().getHeight()) {
	                node = node.getRight();
	            }
	            rightLower_other(node, x, other);
	        }
	    }

		
		/**
		 * 
		 * @param x - node to join
		 * @param other - AVLTree to join
		 * this AVLTree has bigger values than other AVLTree
		 */
		private void joinSmallerKeyTree(IAVLNode x, AVLTree other) {
	        int cost = Math.abs(this.getRoot().getHeight() - other.getRoot().getHeight()) + 1;
	        if (other.getRoot().getHeight() < this.getRoot().getHeight()) { 
	        	//rightSubtree(this) is higher than leftSubtree(other)
	            IAVLNode node = this.getRoot();
	            while (node.getHeight() > other.getRoot().getHeight()) {
	                node = node.getLeft();
	            }
	            rightHigher_this(node, x, other);
	        } else { 
	        	//rightSubtree(this) is lwoer than leftSubtree(other)
	            IAVLNode node = other.getRoot();
	            while (node.getHeight() > this.getRoot().getHeight()) {
	                node = node.getRight();
	            }rightLower_this(node, x, other);
	        }
	        this.min = other.min;
	        this.size = this.size + other.size() + 1;
	    }

		/**
		 * 
		 * @param node - a node from thisTree that we need to insert in
		 * @param x - new node to insert
		 * @param other - AVLTree to be joined to this
		 * rightSubtree(other) has higher Height, leftSubtree(this) has smaller height
		 */
		private void rightHigher_other(IAVLNode node, IAVLNode x, AVLTree other) {
			x.setRight(node);
            x.setLeft(this.getRoot());
            x.setParent(node.getParent());
            this.getRoot().setParent(x);
            IAVLNode parent = node.getParent();
            node.setParent(x);
            x.setSize(this.getRoot().getSize() + node.getSize() + 1);
            x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));
            if (parent == null) {
                this.root = x;
            }else{
                this.root = other.getRoot();
                parent.setLeft(x);
                while(node!=this.root) {
                	node.updateSize(+1);
                	node = node.getParent();
                }node.updateSize(+1);
                insert_rebalance(x);
            }
		}
		
		/**
		 * 
		 * @param node - a node from thisTree that we need to insert in
		 * @param x - new node to insert
		 * @param other - AVLTree to be joined to this
		 * rightSubtree(other) has lower Height, leftSubtree(this) has higher height
		 */
		private void rightLower_other(IAVLNode node, IAVLNode x, AVLTree other) {
			x.setLeft(node);
			System.out.println(node.getKey());
            x.setRight(other.getRoot());
            x.setParent(node.getParent());
            other.getRoot().setParent(x);
            IAVLNode parent = node.getParent();
            node.setParent(x);
            x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));
            x.setSize(other.getRoot().getSize() + node.getSize() + 1);
            if (parent == null) {
                this.root = x;
            } else {
                parent.setRight(x);
                while(node!=this.root) {
                	node.updateSize(+1);
                	node = node.getParent();
                }node.updateSize(+1);
                insert_rebalance(x);
            }
		}
		
		/**
		 * 
		 * @param node - a node from thisTree that we need to insert in
		 * @param x - new node to insert
		 * @param other - AVLTree to be joined to this
		 * rightSubtree(other) has lower Height, leftSubtree(this) has higher height
		 */
		private void rightHigher_this(IAVLNode node,IAVLNode x, AVLTree other) {
			x.setRight(node);
			x.setLeft(other.getRoot());
			x.setParent(node.getParent());
			other.getRoot().setParent(x);
			IAVLNode parent = node.getParent();
			node.setParent(x);
			x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));
			x.setSize(other.getRoot().getSize() + node.getSize() + 1);
			if (node == this.getRoot()) {
				this.root = x;
			} else {
				parent.setLeft(x);
				while(node!=this.root) {
					node.updateSize(+1);
					node = node.getParent();
				}node.updateSize(+1);
				insert_rebalance(x);
				}
			}
		
		/**
		 * 
		 * @param node - 
		 * @param x - a node to insert
		 * @param other - will be leftSubtree, and has higher rank
		 */
		private void rightLower_this(IAVLNode node, IAVLNode x, AVLTree other) {
			x.setRight(this.getRoot());
            x.setLeft(node);
            x.setParent(node.getParent());
            this.getRoot().setParent(x);
            IAVLNode parent = node.getParent();
            node.setParent(x);
            x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));
            x.setSize(this.getRoot().getSize() + node.getSize() + 1);
            if (node == other.getRoot()) {
                this.root = x;
            } else {
                this.root = other.getRoot();
                parent.setRight(x);
                while(node!=this.root) {
                	node.updateSize(+1);
                	node = node.getParent();
                }node.updateSize(+1);
                insert_rebalance(x);
            }
		}
		
		/**
		 * @param1 node x that we joined with
		 * @param2 k, the height of the lower subtree
		 * 
		 * updates ancestor's heights after inserting the node x
		 */
		private void fixRankAfterInsert(IAVLNode x, int k) {
			x.setHeight(Math.max(x.getLeft().getHeight(), x.getRight().getHeight())+1);
			if(x.getParent()!=null && x.getParent().getHeight()==k+1) {
				return;
			}else {
				insert_rebalance(x);
			}
		}

		public interface IAVLNode{	
			public int getKey();
			public void setKey(int k); 
			public void setValue(String value);  
			public boolean isRightChild(); 
			public boolean hasTwoChildren(); 
			public void decrementHeight(int i);
			public void incrementHeight(int i);
			public void updateSize(int i);
			public int getSize();  
			public void setSize(int i);
			public String getValue();
			public void setLeft(IAVLNode node); 
			public IAVLNode getLeft();
			public void setRight(IAVLNode node); 
			public IAVLNode getRight(); 
			public void setParent(IAVLNode node); 
			public IAVLNode getParent(); 
			public boolean isRealNode(); 
	    	public void setHeight(int height);
	    	public int getHeight(); 
	    	public boolean isLeaf();
	    	public boolean isUnaryNode();
		}

	   /**
	   * public class AVLNode
	   *
	   * If you wish to implement classes other than AVLTree
	   * (for example AVLNode), do it in this file, not in 
	   * another file.
	   * This class can and must be modified.
	   * (It must implement IAVLNode)
	   */
		public class AVLNode implements IAVLNode{
		  	private int key;
		  	private String info;
		  	private int height;
		  	private int size;
		  	private IAVLNode right;
		  	private IAVLNode left;
		  	private IAVLNode parent;
		  	
		  	
		  	public AVLNode(int key, String info){ //regular node constructor
		  		this.key = key;
		  		this.info = info;
		  		this.height = 0;
		  		this.size = 1;
		  		this.parent = null;
		  		this.right = new AVLNode();
		  		this.left = new AVLNode();
		  		this.right.setParent(this);
		  		this.left.setParent(this);
		  	}
		  	
		  	public AVLNode(){ 
		  		//virtual node constructor
		  		this.key = -1;
		  		this.info = null;
		  		this.height = -1;
		  		this.size = 0;
		  	}
		  
		  	// returns node's key
			public int getKey(){
				return (this.key);
			}
			//return node's value
			public String getValue(){
				return(this.info);
			}
			//sets left child of node
			public void setLeft(IAVLNode node){
				this.left = node;
				return;
			}
			//returns left child of node
			public IAVLNode getLeft(){
				return this.left;
			}
			//set right child of node
			public void setRight(IAVLNode node){
				this.right = node;
				return;
			}
			//returns right child of node
			public IAVLNode getRight(){
				return this.right;
			}
			
			//set parent of node
			public void setParent(IAVLNode node){
				this.parent = node;
				return;
			}
			//return parent of node
			public IAVLNode getParent(){
				return this.parent;
			}
			
			// Returns True if this is a non-virtual AVL node
			public boolean isRealNode(){
				return(this.key != -1);
			}
			//set height of node
			public void setHeight(int height){
				this.height = height;
				return; 
			}
			//return height of node
			public int getHeight(){
				return this.height;
			}
			//set size of node
			public void setSize(int i) {
				this.size=i;
				return;
			}
			//return size of node
			public int getSize() {
				return this.size;
			}
			//increment node's size by d
			public void updateSize(int d) {
				this.size += d;
				return;
			}
			//return TRUE if node has two children
			public boolean hasTwoChildren() {
				if(this.getLeft().getKey()!=-1 && this.getRight().getKey()!=-1) {
					return true;
				}
				return false;
			}
			//decrement node's height by i
			public void decrementHeight(int i) {
				this.height-=i;	
			}
			//increment node's height by i
			public void incrementHeight(int i) {
				this.height+=i;	
			}
			
			//return TRUE if node isRightChild
			public boolean isRightChild() {
				if(this.getParent().getRight() == this) {
					return true;
				}else{
					return false;
				}
			}
			//return TRUE if node is a leaf
			public boolean isLeaf() {
				return this.getLeft().getHeight() == -1 && this.getRight().getHeight() == -1;
			}
			//return TRUE if node is unary node--has one child
	        public boolean isUnaryNode() {
	            return (this.getRight().isRealNode() && !this.getLeft().isRealNode()) || ((!this.getRight().isRealNode() && this.getLeft().isRealNode()));
	        }
	        //sets key of node
	        public void setKey(int k) {
	            this.key = k;
	        }
	        //sets value of node
	        public void setValue(String value) {
	            this.info = value;
	        }
		}  
	}
