import java.util.*;

public class FibonaciiHeap {

    private Node root; // root node pointing to the top level circular loop
    private Node maxPointerNode; // points to node with max value


    public FibonaciiHeap() {
        root = new Node("root", 0);
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        return root.getChild() == null;
    }

    /**
     *
     * @return
     */
    public Node getMaxPointerNode() {
        return maxPointerNode;
    }

    /**
     *
     * @param word
     * @param frequency
     * @return
     */

    public Node insert(String word, long frequency) {
        Node newNode = new Node(word, frequency);
        insert(newNode, root);
        return newNode;
    }

    /**
     * Insert the new node in the circular list, updates the max pointer as needed
     * The new node is inserted as a child of the parent node, if the parent doesn't
     * have a child then just simple add the new node as a child. If there is a
     * child node present then insert the child into the circular doubly list
     * @param newNode - new node to be inserted in the circular list
     * @param parent - parent of the new node
     */
    private void insert(Node newNode, Node parent) {
        if( newNode == null ) return ;

        if (parent.getChild()!=null) {
            Node child = parent.getChild();
            if (child.getLeft() == null) { // single node in a heap
                child.setLeft(newNode);
                child.setRight(newNode);
                newNode.setRight(child);
                newNode.setLeft(child);
            }  else { // insert right after the max pointer, creating a circular doubly linked list
                newNode.setLeft(child);
                newNode.setRight(child.getRight());
                Node right = child.getRight();
                right.setLeft(newNode);
                child.setRight(newNode);
            }
        }
        parent.setDegree(1);       // added one child, update degree
        parent.setChild(newNode);  // set the new node as a child
        parent.setChildCut(false); // set the child cut to false
        newNode.setParent(parent); // set parent for a child node

        // update the max pointer
        if (maxPointerNode == null ) {
            maxPointerNode = parent==root?newNode:parent;
        }else{
            if( newNode.getFrequency() > maxPointerNode.getFrequency())
                maxPointerNode = newNode;
            if( parent.getFrequency() > maxPointerNode.getFrequency())
                maxPointerNode = parent;
        }
    }

    /**
     *
     * @param node
     * @param frequency
     */

    public void increaseKey(Node node, long frequency) {
      //  System.out.println("increase key "+node);
        // two cases
        // I. If updating a max pointer or increasing a key of node that doesn't exceed the parent's frequency
        // then just update the key since heap property will be preserved
        // II. remove it from the heap, perform cascading cut, reinsert it back into the tree
        if (node == maxPointerNode || node.getParent() == root) {
            node.setFrequency(frequency);
        } else if (node.getParent().getFrequency() >= frequency + node.getFrequency()) {
            // if increase key results in a frequency where child node is still smaller/equal than parent
            node.setFrequency(frequency);
        } else {
            // perform cascading cut remove all node that has lost the child before the cut
            node.setFrequency(frequency);
          //  node.setChild(node);
            cascadingCut(node);
        }
        //update max pointer if needed and increase the key
        if (maxPointerNode.getFrequency() < node.getFrequency()) {
            maxPointerNode = node;
        }
    //    print();
    }

    /**
     *
     * @param node
     */
    public void cascadingCut(Node node) {
        Node child = node;
        Node parent = child.getParent();
        List<Node> nodesList = new ArrayList<>();

        // cut until parent is either root or a parent with child cut false is encountered
        while (parent != root && (parent.isChildCut() || nodesList.size()==0)) {
            // remove the child node
            Node left = child.getLeft();
            Node right = child.getRight();

            if (left == right ) {
                if( left != null) {
                    left.setRight(null);
                    right.setLeft(null);
                }
            } else {
                left.setRight(right);
                right.setLeft(left);
            }
            parent.setDegree(-1);     // degree goes down by 1
            parent.setChildCut(true); // losing the child first time

           if( parent.getChild() == child)
               parent.setChild(right);   // set the right sibling as a child

            // reset the child fields
            child.setLeft(null);
            child.setRight(null);
            child.setParent(null);

            // needs to be added back to the tree
            nodesList.add(child);

            // update pointer
            child = parent;
            parent = child.getParent();
        }
        parent.setChildCut(true); // parent must have lost the child
        for (Node n : nodesList) {
            insert(n, root);      // insert it back into heap
        }

    }


    /**
     *
     */
    public void removeMax() {
        if( maxPointerNode == null ) return ;
        //insert a child into the top-level list
        Node child = maxPointerNode.getChild();
        Node left = maxPointerNode.getLeft();
        Node right = maxPointerNode.getRight();
        if( root.getChild() == maxPointerNode ){
            root.setChild(right); // right becomes the child
        }
        // remove from top-level link
        if (left == right) {
            if( left != null) {
                left.setRight(null);
                right.setLeft(null);
            }
        } else {
            left.setRight(right);
            right.setLeft(left);
        }

        // reset the max pointer node
        maxPointerNode.setLeft(null);
        maxPointerNode.setRight(null);
        maxPointerNode.setChild(null);
        maxPointerNode.setDegree(-maxPointerNode.getDegree());
        maxPointerNode.setChildCut(false);
        maxPointerNode = null;

        // removes all the nodes from the circular list and adds it to a list
       /* List<Node> trees = new ArrayList<>();
        Node start = child;
        while( child!=null && (child!=start || trees.size()==0) ){
            right = child.getRight();
            child.setLeft(null);
            child.setRight(null);
            trees.add(child);
            child = right;
        }

        for( Node tree : trees ){
            insert(tree,root);
        }*/
        // insert them back into the heap in the top level circular lis
        merge(child,root.getChild());

        // perform a pairwise combine
        pairWiseCombine();
    }

    /**
     * Merge two circular list
     * @param list1
     * @param list2
     * Time complexity O(1)
     */
    private void merge(Node list1, Node list2) {
        if( list1 == null ) return ;
        if( list2 == null ){
            root.setChild(list1);
        }else{
            Node left = list1.getLeft();
            Node rLeft = list2.getLeft();
            if( left == null ){
                insert(list1,root);
            }else if(rLeft == null ){
                list2.setLeft(left);
                left.setRight(list2);
                list2.setRight(list1);
                list1.setLeft(list2);
            }else{
                left.setRight(list2);
                list2.setLeft(left);
                list1.setLeft(rLeft);
                rLeft.setRight(list1);
            }
        }
    }

    /**
     *
     * @param k
     * @return
     */
    public List<Node> topKWords(int k){
        List<Node> trees = new ArrayList<>();
        List<Node> words = new ArrayList<>();
        // remove the top K words from teh heap
        while( k-- >  0 ){
            trees.add(maxPointerNode);
            removeMax();
        //    print();
        }
        // insert the nodes back into the heap
        for( Node tree : trees ){
            words.add(tree);
            insert(tree,root);
        }
        return words;
    }

    /**
     *
     */
    private void pairWiseCombine() {
        Node child = root.getChild();
        Node start = child;
        //take all the tree out of the circular list
        if( child  == null ) return ;

        Map<Integer,Node> degreeTable = new HashMap<>();
        List<Node> trees = new ArrayList<>();

        // remove all trees from the circular list, not required but
        // doing this makes is much easier to perform the operations below
        while( child!=null && (child!=start || trees.size()==0) ){
            Node right = child.getRight();
            child.setLeft(null);
            child.setRight(null);
            child.setParent(null);
            trees.add(child);
            child = right;
        }
        root.setChild(null); // all trees are removed from the top list

        // combine tree with equal degree

        for(int i=0;i<trees.size();++i) {
            Node tree = trees.get(i);
            if( degreeTable.containsKey(tree.getDegree())) {
                while (degreeTable.containsKey(tree.getDegree())) {
                    Node temp = degreeTable.get(tree.getDegree());
                    degreeTable.remove(tree.getDegree());
                    if( temp.getFrequency() > tree.getFrequency() ){
                        insert(tree,temp); // temp becomes the parent of tree
                        tree = temp;
                    }else{
                        insert(temp,tree); // tree becomes the parent of temp
                    }
                }
                degreeTable.put(tree.getDegree(),tree); // insert new tree into degree table
            }else {
                degreeTable.put(tree.getDegree(), tree);// not seen the degree before
            }
        }
        // trees in the degree table are inserted back into the heap
        for( int key : degreeTable.keySet()){
            insert(degreeTable.get(key),root);
        }
    }

    //print the top level list
    public void print() {
        System.out.println("-------printing top level circular list ----------------");
        Node temp = root.getChild();
        Set<Node> set = new HashSet<>();
        while (temp != null && set.add(temp)) {
            System.out.println(temp);
            temp = temp.getRight();
        }
    }
}
