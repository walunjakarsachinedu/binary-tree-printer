public class AVLTree {
    AVLNode root;

    void insert(int value) {
        root = insert(root, value);
    }

    AVLNode insert(AVLNode node, int value) {
        if(node == null) return new AVLNode(value);
        else if(value < node.value) node.leftChild = insert(node.leftChild, value);
        else if(value > node.value) node.rightChild = insert(node.rightChild, value);

        node.height = heightOfNode(node);
        node = balance(node);
        return node;
    }

    int heightOfNode(AVLNode root) {
        if(root.leftChild == null && root.rightChild == null) return 0;

        int l = 0;
        int r = 0;
        if(root.leftChild != null) l = root.leftChild.height;
        if(root.rightChild != null) r = root.rightChild.height;
        return Math.max(l,r) + 1;
    }

    AVLNode balance(AVLNode node) {
        if(balanceFactor(node) > 1) {
            if(isLeftHeavy(node)) {
                // left left heavy
                node = rotateRight(node);
                return node;
            }
            else {
                // left right heavy
                node.leftChild = rotateLeft(node.leftChild);
                node = rotateRight(node);
                return node;
            }
        }
        else if(isRightHeavy(node)) {
            if(balanceFactor(node.rightChild)>0) {
                // right left heavy
                node.rightChild = rotateRight(node.rightChild);
                node = rotateLeft(node);
                return node;
            }
            else {
                // right right heavy
                node = rotateLeft(node);
                return node;
            }
        }
        return node;
    }

    boolean isLeftHeavy(AVLNode node){
        return balanceFactor(node) > 1;
    }

    boolean isRightHeavy(AVLNode node){
        return balanceFactor(node) < -1;
    }

    AVLNode rotateRight(AVLNode node) {
        AVLNode newNode = node.leftChild;

        node.leftChild = newNode.rightChild;
        newNode.rightChild = node;

        node.height = heightOfNode(node);
        newNode.height = heightOfNode(newNode);

        return newNode;
    }

    AVLNode rotateLeft(AVLNode node) {
        AVLNode newNode = node.rightChild;

        node.rightChild = newNode.leftChild;
        newNode.leftChild = node;

        node.height = heightOfNode(node);
        newNode.height = heightOfNode(newNode);

        return newNode;
    }

    int balanceFactor(AVLNode node) {
        int r = -1;
        int l = -1;
        if(node.leftChild != null) l = node.leftChild.height;
        if(node.rightChild != null) r = node.rightChild.height;

        return l-r;
    }
}
