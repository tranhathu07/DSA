import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MinPQ<Key> implements Iterable<Key> {
    private int initCapacity;
    private Comparator<Key> comparator;
    private int N = 0;
    private Key[] keys;
    public MinPQ(int initCapacity){
        this.initCapacity = initCapacity;
        this.keys = (Key[]) new Object[initCapacity+1];
    }
    public MinPQ(){
        this(1);
    }
    public MinPQ(int initCapacity, Comparator<Key> comparator){
        this.initCapacity = initCapacity;
        this.comparator = comparator;
        this.keys = (Key[]) new Object[initCapacity+1];
    }
    public MinPQ(Comparator<Key> comparator){
        this.comparator = comparator;
        this.initCapacity = 1;
        this.keys = (Key[]) new Object[initCapacity+1];
    }
    public MinPQ(Key[] keys){
        this.keys = keys;
    }
    public boolean isEmpty(){
        return N==0;
    }
    public int size(){
        return N;
    }
    public Key min(){
        return keys[1];
    }
    public void insert(Key x){
        if(N==keys.length-1) resize(keys.length*2);
        keys[++N] = x;
        swim(N);
    }
    public Key delMin(){
        Key max = keys[1];
        exch(1,N);
        keys[N--] = null;
        sink(1);
        if (N == keys.length/4) resize(keys.length/2);
        return max;
    }
    @Override
    public Iterator<Key> iterator(){
        return new HeapIterator();
    }
    private class HeapIterator implements Iterator<Key>{
        private MinPQ<Key> copy;
        public HeapIterator(){
            if (comparator == null){
                copy = new MinPQ<Key>(size());
            }
            else {
                copy = new MinPQ<Key>(size(), comparator);
            }
            for (int i = 1; i <= N; i++) {
                copy.insert(keys[i]);
            }
        }
        @Override
        public boolean hasNext(){
            return !copy.isEmpty();
        }
        @Override
        public Key next(){
            if (!hasNext()) {
                throw new NoSuchElementException("Priority Queue rỗng!");
            }
            return copy.delMin();
        }
        @Override
        public void remove(){
            throw new UnsupportedOperationException("Phương thức remove() không được hỗ trợ");

        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Key key : this) { // Duyệt các phần tử theo thứ tự tăng dần
            sb.append(key).append(", ");
        }
        if (N > 0) sb.setLength(sb.length() - 2);
        sb.append("]");
        return sb.toString();
    }
    private boolean more(int i, int j){
        if (comparator != null){
            return comparator.compare(keys[i],keys[j]) >0;
        }
        else{
            return ((Comparable<Key>) keys[i]).compareTo(keys[j])>0;
        }
    }
    private void exch(int i,int j){
        Key temp = keys[i];
        keys[i] = keys[j];
        keys[j] = temp;
    }
    private void swim(int k){
        while(k>1 && !more(k,k/2)){
            exch(k,k/2);
            k/=2;
        }
    }
    private void resize(int k){
        Key[] copy = (Key[]) new Object[k];
        for (int i =0;i<=Math.min(k,N);i++){
            copy[i] = keys[i];
        }
        keys = copy;
    }
    private void sink(int k){
        while(2*k <=N){
            int j = 2*k;
            if(j<N && more(j,j+1)) j++;
            if(!more(k,j)) break;
            exch(k,j);
            k=j;
        }
    }
    public static void main(String[] args){
        MinPQ<Integer> pq = new MinPQ<>();
        pq.insert(1);
        pq.insert(7);
        pq.insert(2);
        pq.insert(5);
        System.out.println(pq);
        pq.delMin();
        System.out.println(pq);
        pq.delMin();
        System.out.println(pq);
        pq.delMin();
        System.out.println(pq);

    }

}
