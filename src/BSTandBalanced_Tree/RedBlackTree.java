public class RedBlackTree<Key extends Comparable<Key>,Value>{
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private class Node{
        Key key;
        Value val;
        Node left,right;
        boolean color;
        public Node(Key key, Value val,boolean color){
            this.key = key;
            this.val=val;
            this.color = color;
        }
    }
    private boolean isRed(Node x){
        if (x== null) return false;
        return x.color == RED;
    }
    private Node rotateLeft(Node h){
        Node x = h.right;
        h.right = x.left;
        x.left=h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    private Node rotateRight(Node h){
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }
    private void flipColors(Node h){
        h.color = RED;
        h.right.color = BLACK;
        h.left.color = BLACK;
    }
    private Node put(Node h, Key key, Value val){
        if (h== null) return new Node(key,val, RED);
        int cmp = key.compareTo(h.key);
        if(cmp>0) h.right = put(h.right,key,val);
        else if (cmp<0) h.left = put(h.left,key,val);
        else{
            h.val = val;
        }
        if(isRed(h.right) && !isRed(h.left)) h =rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.right) && isRed(h.left)) flipColors(h);
        return h;
    }

    private Node four(Node h, Key key, Value val){
        if (h==null) return new Node(key,val,RED);
        if (isRed(h.left) &&isRed(h.right)) flipColors(h);
        int cmp = key.compareTo(h.key);
        if(cmp>0) h.right = four(h.right,key,val);
        else if(cmp<0) h.left = four(h.left,key,val);
        else{
            h.val = val;
        }
        if(isRed(h.right) && !isRed(h.left)) rotateLeft(h);
        if(isRed(h.left) && isRed(h.left.left)) rotateRight(h);
        return h;
    }
    public void put(Key key, Value val){
        Node root = null;
        root = four(root, key, val);
        root.color = BLACK;
    }

    public Node delMin(Node h){
        if (h.left == null) return null;
        if(!isRed(h.left) && !isRed(h.left.left)){
            h = movedLeft(h);
        }
        h.left = delMin(h.left);
        return balanced(h);
    }
    private Node movedLeft(Node x){
        flipColors(x);
        if(isRed(x.right.left)){
            x.right = rotateRight(x.right);
            x = rotateLeft(x);
            flipColors(x);

        }
        return x;
    }
    private  Node balanced(Node h){
        if(!isRed(h.left) && isRed(h.right)) h=rotateLeft(h);
        if(isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if(isRed(h.left) && isRed(h.right)) flipColors(h);
        return h;

    }
    private Node moveRight(Node x){
        flipColors(x);
        if(isRed(x.left.left)){

            x=rotateRight((x));
            flipColors(x);
        }
        return x;
    }
    private Node delete(Node h,Key key){

        int cmp = key.compareTo(h.key);

        if(cmp<0) {
            if (!isRed(h.left) && !isRed(h.left.left)) {
                h = movedLeft(h);
            }
            h.left = delete(h.left, key);
        }
        else{
            if (isRed(h.left)) h = rotateRight(h);
            if(cmp == 0 && h.right == null) return null;
            if (!isRed(h.right) && !isRed(h.right.left)) {
                h = moveRight(h);
            }
            if(cmp ==0){
                Node x = min(h.right);
                h.key= x.key;
                h.val = x.val;
                h.right = delMin(h.right);
            }
            else {
                h.right = delete(h.right,key);
            }
        }
        h=balanced(h);
        return h;
    }
    private Node min(Node x){
        while (x.left != null){
            x = x.left;
        }
        return x;
    }
}
