static class QueueLinked<T> {
    private Node<T> front;
    private Node<T> back;

    private class Node<T>{
        T data;
        Node<T> next;
        public Node(T data, Node<T> next){
            this.data = data;
            this.next = next;
        }

    }
    public boolean isEmpty(){
        return front == null;
    }
    public void enqueue(T data){

        Node<T> node = new Node<T>(data,null);
        if(isEmpty()){
            back = node;
            front = node;
        }
        else{
            back.next = node;
            back=node;
        }

    }
    public T dequeue(){
        if( isEmpty()){
            throw new NoSuchElementException("Null Queue");
        }
        T item = front.data;
        front = front.next;
        if (isEmpty()){
            back =  null;
        }
        return item;
    }


}
static class QueueArray<T>{
    private T[] lst = (T[]) new Object[10];
    private int N = 0;
    int front = 0;
    int back = 0;
    public boolean isEmpty(){
        return N == 0;
    }
    public void resize(int n){
        T[] new_lst = (T[]) new Object[n];
        int ma = Math.min(n,N);
        for(int i = 0 ;i <ma;i++){
            new_lst[i] = lst[i];
        }
        lst = new_lst;
    }
    public void enqueue(T data){
        if (N == lst.length){
            resize(lst.length*2);
        }
        lst[back] = data;
        back = (back+1) % lst.length;
        N++;
    }
    public T dequeue(){

        T item = lst[front];
        lst[front] = null;
        front = (front+1)%lst.length;
        N--;
        if (N>0 & N == lst.length/4){
            resize(lst.length/2);
        }
        return item;
    }

}
public static void main(String[] args) {
    QueueArray<String> s = new QueueArray<String>();
    s.enqueue("It");
    s.enqueue("is");
    System.out.println(s.dequeue());
    System.out.println(s.dequeue());

}
