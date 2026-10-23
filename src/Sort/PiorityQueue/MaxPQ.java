import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MaxPQ<Key> implements  Iterable<Key>{
    private int initCapactity=0;
    private Key[] keys;
    int N = 0;
    private Comparator<Key> comparator;

    public MaxPQ(int initCapacity){
        this.initCapactity = initCapacity;
        this.keys= (Key[]) new Object[initCapactity + 1];
    }
    public MaxPQ(){
        this(1);
    }

    public MaxPQ(int initCapactity, Comparator<Key> comparator){
        this.initCapactity = initCapactity;
        this.comparator = comparator;
        this.keys= (Key[]) new Object[initCapactity + 1];
    }
    public MaxPQ(Comparator<Key> comparator){
        this.initCapactity=1;
        this.comparator = comparator;
        this.keys= (Key[]) new Object[initCapactity + 1];

    }
    public MaxPQ(Key[] keys){
        this.keys = keys;
    }

    public boolean isEmpty(){
        return N == 0;
    }

    public int size(){
        return N;
    }
    public Key max(){
        if (isEmpty()) throw new NoSuchElementException("Priority queue rỗng!");
        return keys[1];
    }
    public void insert(Key x){
        if(N ==keys.length-1) resize(keys.length*2);
        keys[++N] = x;
        swim(N);
    }
    public Key delMax(){
        Key maxi = keys[1];
        exch(1,N);
        keys[N--] = null;
        sink(1);

        if(N==keys.length/4) resize(keys.length/2);
        return maxi;

    }
    private boolean less(int i, int j) {
        if (comparator != null) {
            return comparator.compare(keys[i], keys[j]) < 0;
        } else {
            return ((Comparable<Key>) keys[i]).compareTo(keys[j]) < 0;
        }
    }
    private void resize(int k){
        Key[] copy = (Key[]) new Object[k];
        for(int i = 1;i<=Math.min(k,N);i++){
            copy[i] = keys[i];
        }
        keys = copy;
    }
    private void exch(int i, int j){
        Key temp = keys[i];
        keys[i] = keys[j];
        keys[j] = temp;
    }
    private void swim(int k){
        while (k>1 && less(k/2,k)){
            exch(k/2,k);
            k/=2;
        }
    }
    private void sink(int k){
        while (2*k<=N ){
            int j = 2*k;
            if(j<N && less(j,j+1)) j++;
            if(!less(k,j)) break;
            exch(k,j);
            k = j;
        }
    }
    @Override
    public Iterator<Key> iterator() {
        return new HeapIterator();
    }
    private class HeapIterator implements Iterator<Key> {
        // Tạo một bản sao của MaxPQ để thao tác
        private MaxPQ<Key> copy;

        public HeapIterator() {
            // Sao chép dung lượng và comparator (nếu có)
            if (comparator == null) {
                copy = new MaxPQ<Key>(size());
            } else {
                copy = new MaxPQ<Key>(size(), comparator);
            }

            // Sao chép toàn bộ phần tử sang queue tạm
            for (int i = 1; i <= N; i++) {
                copy.insert(keys[i]);
            }
        }

        @Override
        public boolean hasNext() {
            return !copy.isEmpty();
        }

        @Override
        public Key next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Priority Queue rỗng!");
            }
            // Trả về phần tử lớn nhất còn lại trong bản sao
            return copy.delMax();
        }

        @Override
        public void remove() {
            // Theo yêu cầu thiết kế, phương thức này không hỗ trợ
            throw new UnsupportedOperationException("Phương thức remove() không được hỗ trợ");
        }
    }
    public String toString(){
        StringBuilder s = new StringBuilder();
        for (int i = 0;i<keys.length;i++) {
            if (keys[i] != null) {
                s.append(keys[i] + ", ");
            }
        }
        if (keys[keys.length-1] != null) s.append(keys[keys.length-1]);
        return s.toString();
    }
    public static void main(String[] args){
        MaxPQ<Integer> pq = new MaxPQ<>(3);
        pq.insert(1);
        pq.insert(7);
        pq.insert(2);
        pq.insert(5);
        System.out.println(pq);
        pq.delMax();
        System.out.println(pq);
    }

}
