import java.util.*;

public class FibonaciiHeap {

    private Node root;      // root node pointing to the top level circular loop
    private Node maxNode;   // points to node with max value
    Set<Node> set = new HashSet<>();
    /**
     * Constructor sets the root node
     */
    public FibonaciiHeap() {
        root = new Node("root", 0);
    }

    /**
     * If the heap doesn't have a node, i.e. the root node doesn't have a child
     * @return - true if the heap is empty, false otherwise
     */
    public boolean isEmpty() {
        return root.getChild() == null;
    }
    /**
     * The max pointer node is the node with highest frequency
     * @return - max pointer node
     */
    public Node getmaxNode() {
        return maxNode;
    }
    /**
     * Insert a node into the heap
     *
     * @param word      - word to be inserted
     * @param frequency - frequency of the word
     * @return - Node representing the word and frequency
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
     *
     * @param newNode - new node to be inserted in the circular list
     * @param parent  - parent of the new node
     */
    private void insert(Node newNode, Node parent) {
        if (newNode == null){
            return;
        }
        if (parent.getChild() != null) {
            Node child = parent.getChild();
            if (child.getLeft() == null) { // single node in a heap
                child.setLeft(newNode);
                child.setRight(newNode);
                newNode.setRight(child);
                newNode.setLeft(child);
            } else { // insert right after the max pointer, creating a circular doubly linked list
                newNode.setLeft(child);
                newNode.setRight(child.getRight());
                Node right = child.getRight();
                right.setLeft(newNode);
                child.setRight(newNode);
            }
        }

        parent.incrementDegree();  // added one child, update degree
        parent.setChild(newNode);  // set the new node as a child
        parent.setChildCut(false); // set the child cut to false
        newNode.setParent(parent); // set parent for a child node

        // update the max pointer
        if( root == parent) {
            if (maxNode == null) {
                maxNode = newNode;
            } else {
                if (newNode.getFrequency() > maxNode.getFrequency())
                    maxNode = newNode;
            }
        }
        //  update the max node if the max node has become the child
        if( root!=parent && maxNode == newNode ){
            maxNode =parent;
        }
    }

    /**
     * increase key operation allows to increase the frequency by a count
     *
     * @param node      - node whose frequency needs to be increased
     * @param frequency - count is increased by this frequency
     */

    public void increaseKey(Node node, long frequency) {
        // I. If updating a max pointer or increasing a key of node that doesn't exceed the parent's frequency
        // then just update the key since heap property will be preserved
        // II. remove it from the heap, perform cascading cut, reinsert it back into the tree
        if (node == maxNode || node.getParent() == root) {
            node.setFrequency(frequency);
        } else if (node.getParent().getFrequency() >= frequency + node.getFrequency()) {
            // if increase key results in a frequency where child node is still smaller/equal than parent
            node.setFrequency(frequency);
        } else {
            // perform cascading cut remove all node that has lost the child before the cut
            node.setFrequency(frequency);
            cascadingCut(node);
        }
        //update max pointer
        if (maxNode.getFrequency() < node.getFrequency()) {
            maxNode = node;
        }
    }

    /**
     * Cascading cut is triggered whenever a heap property is violated due to the
     * increase key operation, cascading cut removes a tree until a node with child
     * cut false is encountered or a root node. Cascading cut removes a node and sets
     * its parent child-cut to true and inserts all the nodes into the top-level list.
     *
     * @param node - node that triggered cascading cut
     */
    public void cascadingCut(Node node) {
        Node child = node;
        node.setChildCut(true);
        Node parent = child.getParent();
        boolean childCut = child.getChildCut();
        // cut until parent is either root or a parent with child cut false is encountered
        while (parent != root && childCut ) {
            // remove the child node
            Node left = child.getLeft();
            Node right = child.getRight();
            if (left == right) {
                if (left != null) {
                    left.setRight(null);
                    right.setLeft(null);
                }
            } else {
                left.setRight(right);
                right.setLeft(left);
            }
            childCut = parent.getChildCut();
            parent.decrementDegree();     // degree goes down by 1
            parent.setChildCut(true); // losing the child first time

            if (parent.getChild() == child)
                    parent.setChild(right);   // set the right sibling as a child

            // reset the child fields
            child.setLeft(null);
            child.setRight(null);
            child.setParent(root);

            // needs to be added back to the tree
            insert(child,root);
            // keep moving up in the tree
            child = parent;
            parent = child.getParent();
        }
        parent.setChildCut(true); // parent must have lost the child

    }

    /**
     * Remove max is a key operation of a Fibonacci heap, remove max simply removes
     * the node with a maximum frequency see so far.
     */
    public void removeMax() {
        if (maxNode == null) return;
        //insert a child into the top-level list
        Node child = maxNode.getChild();
        Node left = maxNode.getLeft();
        Node right = maxNode.getRight();

        // remove from top-level link
        if (left == right) {
            if (left != null) {
                left.setRight(null);
                right.setLeft(null);
            }
        } else {
            left.setRight(right);
            right.setLeft(left);
        }

        if(maxNode == root.getChild())
            root.setChild(right);

        // reset the max pointer node
        maxNode.reset();
        maxNode = null;
        // insert them back into the heap in the top level circular lis
        List<Node> trees = new ArrayList<>();
        Node start = child;
        while( child!=null && (child!=start || trees.size()==0) ){
            right  = child.getRight();
            trees.add(child);
            child = right;
        }

        for( Node tree : trees ){
            tree.setLeft(null);
            tree.setRight(null);
            insert(tree,root);
        }
        // perform a pairwise combine
        pairWiseCombine();
    }


    /**
     * Top k words are removed the heap and returned as list,
     * After removal the nodes are re-inserted back into the heap
     * @param k - top k keyword
     * @return - top k words are returned,if two words have same frequency any word will
     * returned in no particular order
     */
    public List<String> topKWords(int k) {
        List<Node> trees = new ArrayList<>();
        List<String> words = new ArrayList<>();

        // remove the top K words from teh heap
        while (!isEmpty() && k-- > 0) {
            trees.add(maxNode);
            words.add(maxNode.getWord());
            removeMax();
        }

        // insert the nodes back into the heap
        for (Node tree : trees) {
            tree.setRight(null);
            tree.setLeft(null);
            insert(tree, root);
        }
        return words;
    }
    /**
     * Combines trees with equal degree
     */
    private void pairWiseCombine() {
        Node child = root.getChild();
        Node start = child;
        if (child == null) return;
        Map<Integer, Node> degreeTable = new HashMap<>();
        List<Node> trees = new ArrayList<>();
        /*
         remove all trees from the circular list, this is essential since after the
         remove max the parent pointer of each node must be set to root, so either way
         we would have to traverse the list in order to set the parent field, the time
         complexity remains the same, this makes it much simpler to do pairwise operations
        */
        while (child != null && (child != start || trees.size() == 0)) {
            Node right = child.getRight();
            child.setLeft(null);
            child.setRight(null);
            child.setParent(null);
            trees.add(child);
            child = right;
        }
        root.setChild(null); // all trees are removed from the top list
        // combine tree with equal degree
        for (int i = 0; i < trees.size(); ++i) {
            Node tree = trees.get(i);
            while (degreeTable.containsKey(tree.getDegree())) {
                Node temp = degreeTable.get(tree.getDegree());
                degreeTable.remove(tree.getDegree());
                if (temp.getFrequency() >= tree.getFrequency()) {
                    insert(tree, temp); // temp becomes the parent of tree
                    tree = temp;
                } else {
                    insert(temp, tree); // tree becomes the parent of temp
                }
            }
            degreeTable.put(tree.getDegree(), tree);  // insert new tree into degree table
        }
        // trees in the degree table are inserted back into the heap
        for (int key : degreeTable.keySet()) {
            insert(degreeTable.get(key), root);
        }
    }
}
