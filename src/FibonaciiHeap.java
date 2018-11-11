import java.util.*;

public class FibonaciiHeap {

    private Node root; // root node pointing to the top level circular loop
    private Node maxPointerNode; // points to node with max value

    public FibonaciiHeap() {
        root = new Node("root", 0);
    }

    public boolean isEmpty() {
        return root.getChild() == null;
    }

    public Node getMaxPointerNode() {
        return maxPointerNode;
    }

    public Node insert(String word, long frequency) {
        Node newNode = new Node(word, frequency);
        insert(newNode, root);
        return newNode;
    }

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
        parent.setDegree(1); // added one child
        parent.setChild(newNode);
        parent.setChildCut(false);
        newNode.setParent(parent);
      //  System.out.println("-------------insert new node ----------------");
        //System.out.println("child => "+newNode+ " \npraent=>"+parent);
        if (maxPointerNode == null ) {
            maxPointerNode = parent==root?newNode:parent;
        }else{
            if( newNode.getFrequency() > maxPointerNode.getFrequency())
                maxPointerNode = newNode;
            if( parent.getFrequency() > maxPointerNode.getFrequency())
                maxPointerNode = parent;
        }
    }

    public void increaseKey(Node node, long frequency) {
        // two cases
        // I. If updating a max pointer or increasing a key of node that doesn't exceed the parent then just update the key
        // II. remove it from the heap, perform cascading cut, reinsert it back into the tree
        if (node == maxPointerNode || node.getParent() == root) {
            node.setFrequency(frequency);
        } else if (node.getParent().getFrequency() >= frequency + node.getFrequency()) {
            node.setFrequency(frequency);
        } else {
            // perform cascading cut remove all node that has lost the child before the cut
            node.setFrequency(frequency);
            cascadingCut(node);
        }

        //update max pointer and increase the key
        if (maxPointerNode.getFrequency() < node.getFrequency()) {
            maxPointerNode = node;
        }
    }

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

            parent.setChild(right);
            parent.setDegree(-1);
            parent.setChildCut(true); // losing teh child first time
            parent.setChild(null);

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

        maxPointerNode.setLeft(null);
        maxPointerNode.setRight(null);
        maxPointerNode.setChild(null);
        maxPointerNode.setDegree(-maxPointerNode.getDegree());
        maxPointerNode.setChildCut(false);
        maxPointerNode = null;


        List<Node> trees = new ArrayList<>();
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
        }
        // pairwise combining
        pairWiseCombine();
    }

    public List<String> topKWords(int k){
        List<Node> trees = new ArrayList<>();
        List<String> words = new ArrayList<>();
        while( k-- >  0 ){
          //  System.out.println("max node => " +maxPointerNode);
          //  System.out.println("child node => " +root.getChild());
            trees.add(maxPointerNode);
            removeMax();
       //     print();
        }
        for( Node tree : trees ){
            words.add(tree.getWord());
          insert(tree,root);
        }
        return words;
    }
    private void pairWiseCombine() {
        Node child = root.getChild();
        Node start = child;
        //take all the tree out of the circular list
        if( child  == null ) return ;

        Map<Integer,Node> degreeTable = new HashMap<>();
        List<Node> trees = new ArrayList<>();
        while( child!=null && (child!=start || trees.size()==0) ){
            Node right = child.getRight();
            child.setLeft(null);
            child.setRight(null);
            trees.add(child);
            child = right;
        }
       // System.out.println("------------trees to be combined-------------");
       // System.out.println(trees);
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
                degreeTable.put(tree.getDegree(), tree);
            }
        }
      //  System.out.println("degree table =>"+degreeTable);
        // insert it back into the heap
        // Trees left in the degree table are inserted back into the heap
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
