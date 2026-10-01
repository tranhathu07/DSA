public class InsertionSort {
    public void isort(int[] lst){
        int n = lst.length;
        for(int i =0; i<n; i++){
            for(int j = i-1; j>0;j--){
                if(lst[i]<lst[j]){
                    int temp = lst[i];
                    lst[i] = lst[j];
                    lst[j] = temp;
                }
            }
        }
    }


    public String toString(int[] lst){
        StringBuilder s = new StringBuilder();
        for(int i=0;i<lst.length -1;i++){
            s.append(lst[i]+", ");
        }
        s.append(lst[lst.length-1]);
        return s.toString();
    }

    public void main(String[] args){
        int[] in = new int[]{1,5,2,9,8};
        isort(in);
        System.out.println(toString(in));
    }
}
