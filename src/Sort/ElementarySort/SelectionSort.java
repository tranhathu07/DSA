public class SelectionSort {

    public void ssort(int[] lst){
        int n = lst.length;
        for(int i = 0; i<n;i++){
            int mini = i;

            for(int j = i+1; j<n; j++) {
                if (lst[j]<lst[mini]){
                    mini=j;
                }

            }
            int temp = lst[i];
            lst[i] = lst[mini];
            lst[mini] = temp;

        }
    }
    public String toString(int[] lst){
        StringBuilder s = new StringBuilder();
        for(int i:lst){
            s.append(i +", ");
        }
        return s.toString();
    }
    public void main(String[] args){
        int[] in = new int[]{1,5,2,9,8};
        ssort(in);
        System.out.println(toString(in));
    }
}
