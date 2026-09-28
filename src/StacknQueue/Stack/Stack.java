import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import java.util.Iterator;
import java.util.NoSuchElementException;

class Stack<Item> implements Iterable<Item> {
    private Node<Item>  top;
    private int size = 0;
    private class Node<Item> {
        Item item;
        Node next;

        public Node(Item item, Node next) {
            this.item = item;
            this.next = next;
        }
    }

    public void push(Item item){
        top = new Node(item, top);
        size++;
    }

    public Item pop(){
        Item data = (Item) top.item;
        top = top.next;
        size--;
        return data;
    }
    public boolean isEmpty(){
        return size ==0;
    }
    public int size(){
        return size;
    }
    public String toString(){
        StringBuilder s = new StringBuilder();
        for( Item item:this){
            s.append(item);
            s.append(' ');
        }
        return s.toString();
    }
    public Iterator<Item> iterator(){
        return new LinkedIterator(top);
    }
    private class LinkedIterator implements Iterator<Item>{
        private Node<Item> current;
        public LinkedIterator(Node<Item> first){
            current = first;
        }
        public boolean hasNext(){
            return current != null;
        }
        public Item next(){
            if(!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }
    }
    public static void main(String[] args){
        Stack<String> stack = new Stack<String>();
        while(!StdIn.isEmpty()){
            String item = StdIn.readString();
            if(!item.equals("-")){
                stack.push(item);
            }
            else if (!stack.isEmpty()){
                StdOut.print(stack.pop()+ " ");
            }
        }
        StdOut.println("(" + stack.size()+ "left on stack");
    }
}
