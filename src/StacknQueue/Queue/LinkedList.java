public class LinkedList {
    private Node top;
    private static class Node{
        String data;
        Node next;
        public Node(String data, Node next){
            this.data = data;
            this.next = next;
        }
    }
    void addFirst(String data){
        top = new Node(data, top);
    }
    void append(String data){
        Node last = top;
        if(top == null){
            top = new Node(data,null);
            return;
        }
        while(last.next != null){
            last = last.next;
        }
        last.next = new Node(data,null);
    }
    public Node findFirst(String data){
        Node temp = top;
        while(temp != null) {
            if (temp.data.equals(data)) return temp;
            temp = temp.next;
        }
        return null;
    }

    public void deleleFirst(String data) {
        Node curr = top;
        if(top == null){
            return;

        }
        while(curr.next != null){
            if(curr.data.equals(data)){
                curr = curr.next;
                break;
            }
        }
    }

    public void deleteAll(String data){
        while (top != null && top.data.equals(data)) {
            top = top.next;
        }

        if(top == null){
            return;
        }

        Node temp = top;
        while(temp.next != null) {
            if (temp.next.data.equals(data)) {
                temp.next = temp.next.next;
            } else {
                temp = temp.next;
            }
        }
    }

    public void print() {
        Node curr = top;
        while (curr != null){
            System.out.print(curr.data + " ");
            curr = curr.next;
        }
    }

    public static void main(String[] args) {
        LinkedList schoolMini = new LinkedList();
        schoolMini.append("U");
        schoolMini.append("U");
        schoolMini.deleteAll("U");
//        schoolMini.deleteAll("Q T");
//        System.out.println(schoolMini.findFirst("R").next.data);
        schoolMini.print();
    }

    private void ape(String r) {
    }
}
