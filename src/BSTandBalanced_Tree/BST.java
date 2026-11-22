public class BST<Key extends Comparable<Key>,Value> {
    private class Node{
        private Key key;
        private Value val;
        private Node left, right;
        private int count;
        private Node(Key key, Value val){
            this.key = key;
            this.val = val;
        }
    }
    public int size(){
        return size(root);
    }
    private int size(Node x){
        if (x == null) return 0;
        return x.count;
    }
    private Node root;
    public void put(Key key, Value val){
        root = put(root, key,val);
    }
    private Node put(Node x, Key key, Value val){
        if ( x == null) return new Node(key, val);
        int cmp = key.compareTo(x.key);
        if (cmp>0){
            return put (x.right, key, val);
        }
        else if (cmp<0){
            return put(x.left, key,val);

        }
        else{
            x.val = val;
            x.count = 1+ size(x.left) + size(x.right);
            return x;
        }
    }
    public int rank (Key key){
        return rank(root,key);
    }
    private int rank(Node x, Key key){
        if (x==null) return 0;
        int cmp = key.compareTo(x.key);
        if(cmp <0) return rank(x.left, key);
        if (cmp == 0) return size(x.left);
        else return 1+ size(x.left) + rank(x.right,key);
    }
    public void deleteMin(){
        root = deleteMin(root);
    }
    private Node deleteMin(Node x){
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.count = 1+ size(x.left) + size(x.right);
        return x;
    }
    public void delete(Key key){
        root = delete(root, key);
    }
    private Node delete(Node x, Key key){
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if ( cmp <0) x.left = delete(x.left,key);
        if(cmp>0) x.right = delete(x.right,key);
        else{
            if(x.right == null) return x.left;
            if(x.left == null) return x.right;

            Node t = x;
            x = min(t.right);
            x.right = deleteMin(t.right);
            x.left = t.left;
        }
        return x;
    }
    private Node min(Node x){
        if (x.left == null) return x;
        return min(x.left);
    }
    public Key floor(Key key){
        Node x = floor(root,key);
        if (x==null) return null;
        return x.key;
    }
    private Node floor(Node x, Key key){
        if(x==null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp>0) return floor(x.right, key);
        if (cmp<0) {
            Node t = floor(x.left,key);
            if (t==null) return x;
            else return t;
        }
        else return x;
    }

    public Key ceil(Key key){
        Node x = ceil(root, key);
        if ( x==null) return null;
        return x.key;
    }
    private Node ceil(Node x, Key key){
        if ( x == null) return null;
        int cmp = key.compareTo(x.key);
        if ( cmp == 0) return x;
        else if (cmp <0) return ceil(x.left,key);
        else{
            Node t = ceil(x.right, key);
            if (t == null) return x;
            else return t;
        }
    }
    private Value get(Key key){
        Node x = root;
        while(x!= null){
            int cmp = key.compareTo(x.key);
            if (cmp>0) x= x.right;
            else if(cmp<0) x = x.left;
            else return x.val;
        }
        return null;
    }


}

