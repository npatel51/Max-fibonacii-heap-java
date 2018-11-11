import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.xml.internal.bind.marshaller.NoEscapeHandler;

import java.util.*;

public class MaxFibonaciiHeap {
    private Node maxPointer;
    private Node root; //dummy node
    public MaxFibonaciiHeap() {
        root = new Node("",0);
    }

    public Node getRoot(){
        return root;
    }
    public boolean isEmpty() {
        return root.getChild() == null;
    }

    public Node insert(String word, long frequency) {
        Node newNode = new Node(word, frequency);
        insertTopLevel(newNode);
        if (frequency > maxPointer.getFrequency()) {
            maxPointer = newNode;
        }
        return newNode;
    }

    public Node getMaxPointer() {
        return maxPointer;
    }


    public void increaseKey(Node node, long count) throws InvalidArgumentException {
       System.out.println("--------increase key --------------");
        //validate increase key
        if ( count < 0 ) {
            throw new InvalidArgumentException(new String[]{"Cannot decrease the key"});
        }
        System.out.println(node.getParent()+ " \n" +node);
        // if the increase key operations is requested on the root node then we
        // can just update it without violating the heap property
        if (node == maxPointer) {
            maxPointer.setFrequency(count);
        } else if (node.getParent() == root) { // if it has no parent it is similar to the first case
            node.setFrequency(count);
        } else if (node.getParent().getFrequency() >= count + node.getFrequency()) {
            // this doesn't violate the heap property,if the parent is >=
            // the child node with new frequency then simply increase the key
            node.setFrequency(count);
        } else {
            // parent is < new key value of child then this node must be
            // removed from the heap, update the child pointer and the doubly
            // linked list pointer, increase key and put it back into the top
            // level circular linked list, update pointer if necessary

            List<Node> nodeList = new ArrayList<>();
            Node parent = node.getParent();
            Node left,right;

            // increase the key
            node.setFrequency(count);

            Node child = node;
            // propagate until the node is a root node or it has lost the child
            // for the first time
            while( parent!=root && parent.isChildCut()) {
                System.out.println("--------child cut-------------\n"+parent);
                // update child pointer if needed
                // update is only needed if the node to be removed is the child
                if( parent!=root && parent.getChild() ==  child) {
                    //set the child pointer
                    parent.setChild(parent.getChild().getRight());
                }else{
                    // node needs to to be removed from list
                    left = child.getLeft();
                    right = child.getRight();
                    // remove the node from circular doubly linked list
                    if( left == right ){
                        child.setRight(null);
                        child.setLeft(null);
                    }else {
                        child.setRight(right);
                        child.setLeft(left);
                    }
                    child.setLeft(null);
                    child.setRight(null);
                }
                nodeList.add(child);  // to be added to the heap
                child.setParent(null); // set the parent pointer to null
                child = parent;
                parent = parent.getParent();
            }
            // parent has lost the child, set the child cut field to true
            if ( parent != root) {
                parent.setChildCut(true);
                if( parent.getChild() ==  child) {
                    //set the child pointer
                    parent.setChild(parent.getChild().getRight());
                }else{
                    // node needs to to be removed from list
                    left = child.getLeft();
                    right = child.getRight();

                    // remove the node from circular doubly linked list
                    if( left == right ){
                        child.setRight(null);
                        child.setLeft(null);
                    }else {
                        child.setRight(right);
                        child.setLeft(left);
                    }
                    child.setLeft(null);
                    child.setRight(null);
                }
                nodeList.add(child);
                child.setParent(null);
            }

            // add the nodes to the top level doubly circular list
            for( Node n : nodeList ){
                insertTopLevel(n);
            }
        }
        // update max pointer if necessary
        if (maxPointer == null || maxPointer.getFrequency() < node.getFrequency()) {
            maxPointer = node;
        }

    }

    // insert the node on the doubly circular linked list
    public void insertTopLevel(Node newNode) {
        if( newNode == null ) return ;
        // if heap is empty
        Node child = root.getChild();
        if (isEmpty()) {
            maxPointer = newNode;
        } else if (child.getLeft() == null) { // single node in a heap
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
        System.out.println("--------inserting node --------------\n"+newNode);

        // System.out.println("inserted " +newNode);
        // the last one inserted becomes the child of the parent node
        newNode.setParent(root);
        root.setChild(newNode);

        if( maxPointer == null || maxPointer.getFrequency() < newNode.getFrequency()){
            maxPointer = newNode;
        }
    }
    private void insert(Node newNode, Node rootNode){
        if( newNode == null ) return ;
        // if heap is empty
        Node child = rootNode.getChild();
        if ( child != null) {
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
    }

    public void removeMax(){
        Node child = maxPointer.getChild();
        Node left = maxPointer.getLeft();
        Node right = maxPointer.getRight();

        //if has children add it to the top level list
        if( child!=null ) {
            Node childLeft = child.getLeft();
            if (childLeft!=null && left != null && right != null){
                child.setLeft(left);
                childLeft.setRight(right);
                left.setRight(child);
                right.setLeft(childLeft);
            }else if(childLeft ==null && left!=null && right!=null) {
                child.setLeft(left);
                child.setRight(right);
                left.setRight(child);
                right.setLeft(child);
            }
            root.setChild(child);
        }else {

            if (right == null && left == null) {
                root.setChild(child);
            } else if (left == right) {
                left.setLeft(null);
                right.setRight(null);
            } else {
                left.setRight(right);
                right.setLeft(left);
            }
        }

        maxPointer.setDegree(0);
        maxPointer.setChild(null);
        maxPointer.setRight(null);
        maxPointer.setLeft(null);
        maxPointer = null;

        // pairwise-combining of equal degree
        Map<Integer,Node> degreeTable = new HashMap<>();
        Node temp = root.getChild();
        System.out.println("child " +temp);
        Set<Node> visited = new HashSet<>();
        while(temp!=null && visited.add(temp)){
            right = temp.getRight();
            if(!degreeTable.containsKey(temp.getDegree()))
                degreeTable.put(temp.getDegree(),temp);
            else{
                Node a = temp;
                Node b = degreeTable.get(a.getDegree());
               // System.out.println(a+ "  "+b);
                System.out.println(degreeTable);
                while( a!=null && b!=null && a!=b) {
                    degreeTable.remove(a.getDegree());
                    degreeTable.remove(b.getDegree());
                    if (a.getFrequency() > b.getFrequency()) {
                        a = meld(b, a);
                        b = degreeTable.getOrDefault(a.getDegree(), null);
                        degreeTable.put(a.getDegree(),a);
                    } else {
                        b = meld(a, b);
                        a = degreeTable.getOrDefault(b.getDegree(), null);
                        degreeTable.put(b.getDegree(),b);
                    }

                }

            }
            if( maxPointer == null || maxPointer.getFrequency() < temp.getFrequency()){
                maxPointer = temp;
            }
            temp = right;
        }

        System.out.println("-----------------after remove----------- ");
        temp = getMaxPointer();
        Set<Node> set = new HashSet<>();
        while(set.add(temp) && temp!=null){
            System.out.println(temp);
            temp = temp.getRight();
        }
        System.out.println();
    }

    public Node meld(Node a, Node b){
        //remove pointers pointing to a
        Node left = a.getLeft();
        Node right = a.getRight();
        a.setLeft(null);
        a.setRight(null);
        if( left!=null && right !=null) {
            if (left == right) {
                left.setRight(null);
                right.setLeft(null);
            } else {
                left.setRight(right);
                right.setLeft(left);
            }
        }
        Node child = b.getChild();
        if( child != null  && left!=null && right!=null) {
            if (child.getRight() == null) {
                child.setLeft(a);
                child.setRight(a);
                a.setRight(child);
                a.setLeft(child);
            } else { // insert right before the child, creating a circular doubly linked list
                a.setRight(child);
                a.setLeft(child.getLeft());
                left = child.getLeft();
                left.setRight(a);
                child.setLeft(a);
            }
        }
       // System.out.println(a+ " \n "+child+"\n");
        b.setParent(root);
        a.setParent(b);
        a.setChildCut(false);
        b.setDegree(1);
        b.setChild(a);

        if( maxPointer == null || maxPointer.getFrequency() < b.getFrequency())
            maxPointer = b;

        return b;
    }

    public List<String> topK(int k){
        List<Node> res = new ArrayList<>();
        // remove top K from the heap
        while( k-- > 0) {
            res.add(maxPointer);
            removeMax();
        }
        List<String> topK = new ArrayList<>();
        for(Node n : res ) {
            topK.add(n.getWord());
            //insert the top K back into the heap
            n.setDegree(-n.getDegree()); //reset the degree
            insertTopLevel(n);
        }

        return topK;
    }


   /* public List<String> topK(int k){
        List<Node> res = new ArrayList<>();
        // remove top K from the heap
        while( k-- > 0) {
            res.add(maxPointer);
            removeMax();
        }
        List<String> topK = new ArrayList<>();
        for(Node n : res ) {s
            topK.add(n.getWord());
            //insert the top K back into the heap
     //       insert(n);
        }

        return topK;
    }

    public void removeMax() {
        if( isEmpty() ) return ;
        Node child = maxPointer.getChild(); // if the node has children add it to the heap
        // remove max pointer from circular list
        Node left = maxPointer.getLeft();
        Node right = maxPointer.getRight();
        System.out.println("max pointer " +maxPointer+" child "+child);
        maxPointer = null;
        if( root.getChild() == maxPointer ){
            root.setChild(null);
        }
        //remove the max pointer from the top level doubly list
        if( left  == null && right == null ) {

        }else if(  left  == right ){
            left.setRight(null);
            right.setLeft(null);
        }else {
            left.setRight(right);
            right.setLeft(left);
        }
        if( child != null){
            left = child.getLeft();
            right = child.getRight();
            insert(child);
            Node temp = right;
            if( left  == null && right == null ) {

            }else if(  left  == right ){
                left.setRight(null);
                right.setLeft(null);
            }else {
                left.setRight(right);
                right.setLeft(left);
            }
            while(temp!=null){
                left = temp.getLeft();
                right = temp.getRight();
                if( left  == null && right == null ) {

                }else if( left == right ){
                    left.setRight(null);
                    right.setLeft(null);
                }else {
                    left.setRight(right);
                    right.setLeft(left);
                }
                insert(temp);
                temp = right;
                System.out.println(temp);
            }
        }
        // pairwise-combining of equal degree
        Map<Integer,Node> degreeTable = new HashMap<>();
        Node temp = root.getChild();
        System.out.println("child" +temp);
        Set<Node> visited = new HashSet<>();
        while(visited.add(temp)){
            right = temp.getRight();
            System.out.println(degreeTable);
            if(!degreeTable.containsKey(temp.getDegree()))
               degreeTable.put(temp.getDegree(),temp);
            else{
                Node a = temp;
                Node b = degreeTable.get(a.getDegree());
                while( a!=null && b!=null) {
                    if (a.getFrequency() > b.getFrequency()) {
                        a = meld(b, a);
                        degreeTable.remove(b.getDegree());
                        b = degreeTable.getOrDefault(a.getDegree(),null);
                    } else {
                        b = meld(a, b);
                        degreeTable.remove(a.getDegree());
                        a = degreeTable.getOrDefault(b.getDegree(),null);
                    }
                }
                if( a!= null)
                    degreeTable.put(a.getDegree(),a);
                else
                    degreeTable.put(b.getDegree(),b);

            }
            if( maxPointer == null || maxPointer.getFrequency() < temp.getFrequency()){
                maxPointer = temp;
            }
            temp = right;
        }

        System.out.println("after pass 1");

        Node t = root.getChild();
        Set<Node> set = new HashSet<>();
        while(set.add(t)){
            System.out.println(t);
            t = t.getRight();
        }


    }

    public Node meld(Node a, Node b){
        //remove pointers pointing to a
        Node left = a.getLeft();
        Node right = a.getRight();
        a.setLeft(null);
        a.setRight(null);
        if(  left  == right ){
            left.setRight(null);
            right.setLeft(null);
        }else {
            left.setRight(right);
            right.setLeft(left);
        }

        Node child = b.getChild();
        if( child == null ) {
            b.setChild(a);
        }else if( child.getRight() == null){
            child.setLeft(a);
            child.setRight(a);
            a.setRight(child);
            a.setLeft(child);
        } else { // insert right before the child, creating a circular doubly linked list
            a.setRight(child);
            a.setLeft(child.getLeft());
            left = child.getLeft();
            left.setRight(a);
            child.setLeft(a);
        }
        System.out.println(a+ " \n "+child+"\n");
        a.setParent(b);
        a.setChildCut(true);
        b.setDegree(1);
        b.setChild(a);
        return b;
    }*/


    @Override
    public String toString() {
        return "MaxFibonaciiHeap{" +
                "maxPointer=" + maxPointer +
                '}';
    }
}
