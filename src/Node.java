public class Node {
    //the number of children(s)
    private int degree;
    //word and its frequency
    private long frequency;
    private String word;
    //pointer to parent node
    private Node parent;
    private Node child;  // pointer to the left most child
    // used for circular doubly linked list
    private Node left;
    private Node right;
    //True if node has lost a child since it became a child of its current parent.
    //Set to false by remove min, which is the only operation that makes one node a child of another.
    private boolean childCut;


    public Node(String word, long frequency) {
        this.frequency = frequency;
        this.word = word;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree+= degree;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency+= frequency;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getChild() {
        return child;
    }

    public void setChild(Node child) {
        this.child = child;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public boolean isChildCut() {
        return childCut;
    }

    public void setChildCut(boolean childCut) {
        this.childCut = childCut;
    }

    @Override
    public String toString() {
        return "Node{" +
                "degree=" + degree +
                ", frequency=" + frequency +
                ", word='" + word + '\'' +
                ", left=" +(left==null?"null":left.getWord()) +
                ", right=" +(right==null?"null":right.getWord()) +
                ", child=" +(child==null?"null":child.getWord()) +
                ", parent=" +(parent==null?"null":parent.getWord()) +
                ", childCut=" + childCut +
                '}';
    }
}

