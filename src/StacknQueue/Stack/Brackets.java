class StackLst<Item> {
    private Item[] lst = (Item[]) new Object[10];
    int N = 0;
    public void resize(int n){
        Item[] ne= (Item[]) new Object[n];
        int ln = Math.min(lst.length, n);
        for( int i = 0; i<ln; i++){
            ne[i] = lst[i];
        }
        lst = ne;
    }
    public void push(Item item){
        if(N== lst.length){
            resize(N*2);
        }
        lst[N++] = item;
    }
    public Item pop(){
        if(N>0 &&N == lst.length/4){
            resize(lst.length/2);
        }

        Item data = lst[--N];
        lst[N] = null;
        return data;
    }
    public boolean isEmpty(){
        return N==0;
    }
    public int size(){
        return lst.length;
    }

}
public class Brackets {
    public static boolean isLegal(String s){
        StackLst<String> stack = new StackLst<>();
        String close = ")]}";
        String open = "([{";
        for(String a : s.split("")){
            if(open.contains(a)){
                stack.push(a);
            }
            if(close.contains(a)){
                if(stack.isEmpty()){
                    return false;
                }
                stack.pop();
            }
        }
        return stack.isEmpty();
    }
    public static void main(String[] args){
        String s = "{[({[()]})]{[(){[()]}]}}";
        System.out.println(isLegal(s));
    }
}
