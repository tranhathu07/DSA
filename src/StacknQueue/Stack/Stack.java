

static class Stack_LinkList {
    private Node top;

    public void push(String data) {
        top = new Node(data, top);

    }

    public String pop() {
        if ( isEmpty()){
            throw new EmptyStackException();
        }
        String item = top.data;
        top = top.next;
        return item;
    }
    public boolean isEmpty(){
        return top == null;
    }

    private class Node {
        String data;
        Node next;

        public Node(String data, Node next) {
            this.data = data;
            this.next = next;
        }
    }
}

static class Stack_Array{
    private int N=0;
    private String[] lst = new String[1];
    public void push(String data){
        if ( N == lst.length){
            resize(lst.length*2);
        }
        lst[N++] = data;

    }
    public void resize(int n){
        String[] new_lst = new String[n];
        int ma = Math.min(n,N);
        for(int i = 0 ;i <ma;i++){
            new_lst[i] = lst[i];
        }
        lst = new_lst;
    }

    public String pop() {
        if(isEmpty()){
            throw new EmptyStackException();
        }
        String item = lst[--N];
        lst[N] = null;
        if (N>0 & N == lst.length/4){
            resize(lst.length/2);
        }
        return item;

    }
    public boolean isEmpty(){
        return N ==0;
    }
}
public static void main(String[] args) {
    Stack_Array s = new Stack_Array();
    s.push("It");
    s.push("is");
    System.out.println(s.pop());
    System.out.println(s.pop());

}

