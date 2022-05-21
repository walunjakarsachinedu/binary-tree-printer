public class Main {
    public static void main(String[] args) {
        TreePrinter treePrinter = new TreePrinter();
        AVLTree avlTree = new AVLTree();
        for(int i=0;i<=6;i++){
            avlTree.insert(i);
        }

        treePrinter.printBST(avlTree.root);
    }

    static int heightOfNode(AVLNode root) {
        if(root.leftChild == null && root.rightChild == null) return 0;

        int l = 0;
        int r = 0;
        if(root.leftChild != null) l = root.leftChild.height;
        if(root.rightChild != null) r = root.rightChild.height;
        return Math.max(l,r)+1;
    }
}
