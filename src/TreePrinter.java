import java.util.ArrayList;
import java.util.Arrays;

public class TreePrinter {

    static ArrayList<Object> preparingDataForPrinting(AVLNode root) {
        ArrayList<ArrayList<String>> edges = new ArrayList<>();
        ArrayList<ArrayList<String>> values = new ArrayList<>();
        ArrayList<ArrayList<AVLNode>> nodes = new ArrayList<>();
        ArrayList<ArrayList<Integer>> childCount = new ArrayList<>();

        nodes.add(new ArrayList<>(Arrays.asList(root)));

        while(true){
            ArrayList<AVLNode> lastLevel = nodes.get(nodes.size()-1);

            ArrayList<AVLNode> nextLevel = new ArrayList<>();
            ArrayList<String> levelEdges = new ArrayList<>();
            ArrayList<String> levelValues = new ArrayList<>();
            ArrayList<Integer> levelChildCount = new ArrayList<>();

            for(var node : lastLevel) {
                int count = 0;
                if(node.leftChild != null) {
                    nextLevel.add(node.leftChild);
                    levelEdges.add("/");
                    ++count;
                }
                if(node.rightChild != null) {
                    nextLevel.add(node.rightChild);
                    levelEdges.add("\\");
                    ++count;
                }
                levelValues.add(String.valueOf(node.value));
                levelChildCount.add(count);
            }

            values.add(levelValues);
            childCount.add(levelChildCount);
            if(nextLevel.size() == 0) break;

            nodes.add(nextLevel);
            edges.add(levelEdges);
        }

        ArrayList<Object> output = new ArrayList<>();
        output.add(edges);
        output.add(values);
        output.add(childCount);

        return output;
    }

    static void printBST(ArrayList<ArrayList<String>> values, ArrayList<ArrayList<String>> edges) {
        int v=0;
        int e=0;
        while(v< values.size() || e<edges.size()){
            if(v<values.size()) {
                for(var i:values.get(v)) System.out.print(i);
                System.out.println();
            }
            if(e<edges.size()) {
                for(var i:edges.get(e)) System.out.print(i);
                System.out.println();
            }
            ++v;
            ++e;
        }
    }

    void printBST(AVLNode root) {
        ArrayList<Object> input = preparingDataForPrinting(root);
        ArrayList<ArrayList<String>> edges = (ArrayList<ArrayList<String>>) input.get(0);
        ArrayList<ArrayList<String>> values = (ArrayList<ArrayList<String>>) input.get(1);
        ArrayList<ArrayList<Integer>> childCount = (ArrayList<ArrayList<Integer>>) input.get(2);

        processEdges(edges, childCount);
        placeNodes(values, edges, childCount);
        putSpaceForValue(values, edges,childCount);

        printBST(values, edges);
    }

    void processEdges(ArrayList<ArrayList<String>> edges, ArrayList<ArrayList<Integer>> childCount) {
        processEdges(edges, 0, childCount);
    }

    void processEdges(ArrayList<ArrayList<String>> edges, Integer level, ArrayList<ArrayList<Integer>> childCount) {

        // adjustment of edge at level 0
        if(edges.size()>0) {
            var level0 = edges.get(0);
            for(int m=0;m<level0.size();m++){
                if(level0.get(m) == "/" || level0.get(m) == "\\") level0.add(m++," ");
            }
        } else  return;

        // process edges from top to bottom
        int j = (level!=null) ? level : 0;
        for(;j+1<edges.size();j++) {
            boolean isParentChange = arrangeLevelEdges(j,edges, childCount);
            if(isParentChange) {
                //process edges from current to top
                for(int i=j-1;i>=0;i--){
                    arrangeLevelEdges(i,edges, childCount);
                }
            }
        }
        return;
    }

    private boolean arrangeLevelEdges(int j, ArrayList<ArrayList<String>> edges, ArrayList<ArrayList<Integer>> childCount) {
        // only process j & j+1 level
        boolean isParentChange = false;
        // j denotes level of parent
        // if shifting done in parent level : this function will make recursive call to upper level
        ArrayList<String> parents = edges.get(j);
        ArrayList<Integer> childrenCount = childCount.get(j+1);
        int parent_number = 0;
        // iterating through each parent
        for(int i=0; i<parents.size(); i++) {
            if(parents.get(i) == "/" || parents.get(i) == "\\") {
                ++parent_number;
                if(j+1 < edges.size() && childrenCount.get(parent_number-1) > 0) {
                    ArrayList<String> children = edges.get(j+1);
                    Integer l,r;
                    l = r = null;
                    int child_number = 0;
                    // boolean isForwardslash = false;
                    int before_child_count = 0;
                    for(int t=0;t<parent_number-1;t++){
                        before_child_count += childrenCount.get(t);
                    }
                    ++before_child_count;
                    // find left and right child of current parent
                    for(int c=0;c< children.size();c++) {
                        if(children.get(c) == "/" || children.get(c) == "\\") ++child_number;
                        if(child_number == before_child_count) {
                            if(children.get(c) == "\\") {
                                r = c;
                                break;
                            }
                            else if(children.get(c) == "/") {
                                l = c;
                                if(childrenCount.get(parent_number-1) > 1)
                                    // look for right child
                                    for(;++c<children.size();) {
                                        if(children.get(c) == "/") break;
                                        if(children.get(c) == "\\") {
                                            r = c;
                                            break;
                                        }
                                    }
                                break;
                            }
                        }
                    }


                    // required position shifting of children
                    if(r!=null && (r==0 || (r>0 && children.get(r-1) != " "))) children.add(r++, " ");

                    if(l!=null) {
                        if(l==0) {
                            children.add(l++, " ");
                            if(r!=null) ++r;
                        }
                        else if(l>0 && children.get(l-1) != " ") {
                            Integer backslash = null;
                            for(int b=l-1;b>=0;b--){
                                if(children.get(b)=="\\") {
                                    backslash = b+1;
                                    break;
                                }
                                else if(children.get(b)=="/") break;
                            }
                            if(backslash!=null) {
                                children.add(l++," ");
                                children.add(l++," ");
                                children.add(l++," ");
                                if(r!=null) r+=3;
                            }
                            children.add(l++," ");
                            if(r!=null) ++r;
                        }
                    }

                    // find expected position of parent
                    Integer l1=l;
                    Integer r1=r;
                    if(l1==null && r1!=null) l1=r1-2;
                    if(l1!=null && r1==null) r1=l1+2;
                    int expected_pos = (r1-l1)/2 + l1;
                    if(parents.get(i) == "/") ++expected_pos;
                    else --expected_pos;


                    //shifting position of parent
                    if(expected_pos > i) {
                        isParentChange = true;
                        int dx = expected_pos - i;
                        while(dx!=0){
                            parents.add(i++," ");
                            --dx;
                        }
                    }
                    //shifting position of child
                    else if(expected_pos < i) {
                        // shift child
                        if(l==null) l = r;
                        if(l!=null) {
                            int dx = i-expected_pos;
                            while(dx!=0) {
                                children.add(l," ");
                                --dx;
                            }
                        }
                    }
                }
            }
        }

        // correcting spacing between consecutive parent node and leaf node
        var children = edges.get(j+1);
        int parent_index = 0;
        Integer preParent = null;
        for(int i=0;i<parents.size();i++){
            if(parents.get(i) == "/" || parents.get(i) == "\\") {
                if(childrenCount.get(parent_index)==0 && preParent != null && childrenCount.get(parent_index-1)>0){ // node is leaf && has preParent
                    //find previous parent right child position
                    int child_number = 0;
                    for(int c=0;c<parent_index;c++) {
                        child_number += childrenCount.get(c);
                    }
                    int count_edges = 0;
                    Integer rightChildPos = null;
                    for(int c=0;c<children.size();c++){
                        if(children.get(c) == "/" || children.get(c) == "\\") {
                            ++count_edges;
                            if(count_edges==child_number) {
                                if(children.get(c) != "/") rightChildPos = c;
                                break;
                            }
                        }
                    }
                    if(rightChildPos != null) { // right parent position should be greater than right child
                        if(rightChildPos >= i || (parents.get(i) == "/" && (i - rightChildPos) < 2)) {
                            int space = 2;
                            if(parents.get(i) == "/") space = 4;
                            while(i-rightChildPos != space) {
                                parents.add(i++," ");
                            }
                        }
                    }
                }
                ++parent_index;
                preParent = i;
            }

        }

        return isParentChange;
    }

    void placeNodes(ArrayList<ArrayList<String>> values, ArrayList<ArrayList<String>> edges, ArrayList<ArrayList<Integer>> childCount) {
        if(edges.size() < 1) return;
        preProcessValuesForPlacingNode(values, edges, childCount);
        //handling only level 1
        var level0 = edges.get(0);
        Integer left = null;
        Integer right = null;
        for(int i=0;i< level0.size();i++){
            if(level0.get(i)=="/") {
                left = i;
            }
            else if(level0.get(i)=="\\") {
                right = i;
            }
        }
        if(left==null && right!=null) left=right-2;
        if(left!=null && right==null) right=left+2;
        int expected_pos = (right-left)/2 + left;
        while(expected_pos!=0) {
            values.get(0).add(0," ");
            --expected_pos;
        }

        // process all level greater than 1
        for(int j=0;j<edges.size();j++){
            var level = edges.get(j);
            var nodes = values.get(j+1);
            int count=0;

            ArrayList<Integer> extraSpace = new ArrayList<>(nodes.size());
            for(int i=0;i<level.size();i++){
                if(level.get(i)=="/"){
                    if(nodes.get(count)!=" ") {
                        // move value at count to position i-1
                        int dx = i - 1 - count;
                        for(var space:extraSpace) dx -= space;
                        while(dx!=0){
                            nodes.add(count++, " ");
                            --dx;
                        }
                    }

                    extraSpace.add(nodes.get(count).length()-1);
                    ++count;
                }
                else if(level.get(i)=="\\"){
                    var v = nodes.get(count);
                    int km=0;
                    if(nodes.get(count)!=" ") {
                        // move value at count to position i+1
                        int dx = i + 1 - count;
                        for(var space:extraSpace) dx -= space;

                        while(dx!=0){
                            nodes.add(count++, " ");
                            --dx;
                        }
                    }

                    extraSpace.add(nodes.get(count).length()-1);
                    ++count;
                }
            }
        }
        return;
    }

    // adjustment for edge when node have length is greater than 1
    private void preProcessValuesForPlacingNode(ArrayList<ArrayList<String>> values, ArrayList<ArrayList<String>> edges, ArrayList<ArrayList<Integer>> childCount) {
        for(int j=0;j+1<edges.size();j++){
            var levelEdges = edges.get(j);
            var levelValues = values.get(j+1);
            int edge_count = 0;
            for(int i=0;i<levelEdges.size();i++) {
                if(levelEdges.get(i)=="/" || levelEdges.get(i)=="\\"){
                    ++edge_count;
                    if(levelValues.get(edge_count-1).length()>1) {
                        int dx=levelValues.get(edge_count-1).length()-1;
                        while(dx!=0){
                            levelEdges.add(1+i++," ");
                            --dx;
                        }
                    }
                }
            }
            processEdges(edges, childCount);
        }
    }


    void putSpaceForValue(ArrayList<ArrayList<String>> values, ArrayList<ArrayList<String>> edges, ArrayList<ArrayList<Integer>> childCounts) {
        makeSizeEqual(edges,values);

        for(int j=0;j<edges.size();j++) {
            var levelEdges = edges.get(j);
            var levelValues = values.get(j);
            int e = -1;
            int valueVisualPos = 0; // value visual position
            int parentCount = 0; // count of value
            ArrayList<Integer> levelChildCount = childCounts.get(j);
            for(int i=0;i<levelValues.size();i++) { // traversing through all values
                if(levelValues.get(i) != " " && levelValues.get(i) != "_") {
                    if(levelChildCount.get(parentCount)>0){ // found parent

                        for(++e;e < levelEdges.size();e++) { // finding 1st child
                            if(levelEdges.get(e) == "/") {
                                int space = valueVisualPos - e - 1;
                                while(space-- != 0) {
                                    levelValues.set(i-space-1,"_");
                                }
                                break;
                            }
                            else if(levelEdges.get(e) == "\\") {
                                int space = e - valueVisualPos - 1 - (levelValues.get(i).length() - 1);
                                if(space > 0)
                                    while(space-- != 0)
                                        levelValues.set(i+space+1,"_");
                                break;
                            }
                        }
                        if(levelChildCount.get(parentCount) > 1) { // finding 2nd child if exists
                            for(++e;e < levelEdges.size();e++) {
                                if(levelEdges.get(e) == "\\") {
                                    int space = e - valueVisualPos - 1 - (levelValues.get(i).length() - 1);
                                    if(space > 0)
                                        while(space-- != 0)
                                            levelValues.set(i+space+1,"_");
                                    break;
                                }
                            }
                        }
                    }
                    parentCount++;
                    valueVisualPos += levelValues.get(i).length();
                } else ++valueVisualPos;
            }
        }
    }

    private void makeSizeEqual(ArrayList<ArrayList<String>> edges, ArrayList<ArrayList<String>> values) {
        for(int j=0;j<edges.size();j++) {
            if(edges.get(j).size() > values.get(j).size())
                while (edges.get(j).size() - values.get(j).size() != 0)
                    values.get(j).add(" ");
            else
                while (values.get(j).size() - edges.get(j).size() != 0)
                    edges.get(j).add(" ");
        }
    }
}
