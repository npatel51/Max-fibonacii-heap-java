import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;

public class MaxFibonaciiHeap {
    private Node maxPointer;

    public MaxFibonaciiHeap() {

    }

    public boolean isEmpty() {
        return maxPointer == null;
    }

    public Node insert(String word, long frequency) {
        Node newNode = new Node(word, frequency);
        insert(newNode);
        if (frequency > maxPointer.getFrequency()) {
            maxPointer = newNode;
        }
        return newNode;
    }

    public Node getMaxPointer() {
        return maxPointer;
    }


    public void increaseKey(Node node, int count) throws InvalidArgumentException {

        //validate increase key
        if ( count < 0 ) {
            throw new InvalidArgumentException(new String[]{"Cannot decrease the key"});
        }
        // if the increase key operations is requested on the root node then we
        // can just update it without violating the heap property
        if (node == maxPointer) {
            maxPointer.setFrequency(count);
        } else if (node.getParent() == null) { // if it has no parent it is similar to the first case
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
            while( parent!=null && parent.isChildCut()) {
                // update child pointer if needed
                // update is only needed if the node to be removed is the child
                if( parent!=null && parent.getChild() ==  child) {
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
            if ( parent != null) {
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
                insert(n);
                if (maxPointer.getFrequency() < n.getFrequency()) {
                    maxPointer = n;
                }
            }
        }
        // update max pointer if necessary
        if (maxPointer.getFrequency() < node.getFrequency()) {
            maxPointer = node;
        }

    }

    // insert the node on the doubly circular linked list
    public void insert(Node newNode) {
        // if heap is empty
        if (isEmpty()) {
            maxPointer = newNode;
        } else if (maxPointer.getLeft() == null) { // single node in a heap
            maxPointer.setLeft(newNode);
            maxPointer.setRight(newNode);
            newNode.setRight(maxPointer);
            newNode.setLeft(maxPointer);
        } else { // insert right after the max pointer, creating a circular doubly linked list
            newNode.setLeft(maxPointer);
            newNode.setRight(maxPointer.getRight());
            Node right = maxPointer.getRight();
            right.setLeft(newNode);
            maxPointer.setRight(newNode);

        }
    }

    @Override
    public String toString() {
        return "MaxFibonaciiHeap{" +
                "maxPointer=" + maxPointer +
                '}';
    }
}
