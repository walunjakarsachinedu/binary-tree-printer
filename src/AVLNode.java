public class AVLNode {
    AVLNode leftChild;
    AVLNode rightChild;
    Integer value;
    Integer height;

    public AVLNode(Integer value){
        this.value = value;
        height = 0;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
