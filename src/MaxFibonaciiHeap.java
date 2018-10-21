import com.sun.javaws.exceptions.InvalidArgumentException;

public class MaxFibonaciiHeap {
    private Node maxPointer;

    public MaxFibonaciiHeap() {

    }

    public boolean isEmpty() {
        return maxPointer == null;
    }

    public Node insert(String word, long frequency) {
        Node newNode = new Node(word, frequency);
        // if heap is empty
        if (isEmpty()) {
            maxPointer = newNode;
        } else if (maxPointer.getLeft() == null) { // single node in heap
            maxPointer.setLeft(newNode);
            maxPointer.setRight(newNode);
            newNode.setRight(maxPointer);
            newNode.setLeft(maxPointer);
        } else {
            /*compare it with the maxPointer check if it is smaller than min pointer
              make the new insert min pointer otherwise just insert it right after
              the maxPointer node -- creating a link using right and left
             */
            newNode.setLeft(maxPointer);
            newNode.setRight(maxPointer.getLeft());
            maxPointer.setRight(newNode);
            maxPointer.getLeft().setLeft(newNode);
        }

        // update the maxPointer if necessary
        if (frequency > maxPointer.getFrequency()) {
            maxPointer = newNode;
        }
        return newNode;
    }

    public Node getMaxPointer() {
        return maxPointer;
    }


    public void increaseKey(Node node, int newFrequency) throws InvalidArgumentException {

        //validate increase key
        if (node.getFrequency() < newFrequency) {
            throw new InvalidArgumentException(new String[]{"Cannot decrease the node with key " + node.getFrequency() + "to " + newFrequency});
        }
        // if the increase key operations is requested on the root node then we
        // can just update it without violation the heap property, as long as
        // decrease key is valid, that is it is not decreasing instead
        if (node == maxPointer) {
            maxPointer.setFrequency(newFrequency);
        } else if (node.getParent().getFrequency() > newFrequency) {
            // if the new frequency doesn't violate the heap that is it is greater
            // than than the current key value and smaller or equal to its parent
            node.setFrequency(newFrequency);
        } else {
            // this node must be removed from the tree

        }
    }

    @Override
    public String toString() {
        return "MaxFibonaciiHeap{" +
                "maxPointer=" + maxPointer +
                '}';
    }
}
