import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Binary {

    public static Set nhiphan(int n){
        Set<String> lst = new HashSet<>();
        StringBuilder s = new StringBuilder();
        dequy( lst,s,1,n);
        dequy(lst,s,0,n);
        return lst;

    }
    public static void dequy(Set lst,StringBuilder s, int add, int n){
        if (s.length() == n) {
            lst.add(s.toString());

        }
        else{
            s.append(add);
            System.out.println(s);
            dequy(lst,s,0,n);

            dequy(lst,s,1,n);
            s.deleteCharAt(s.length()-1);
        }
    }
    public static void main(String[] args){
        int n = 5;
        Set<String> lst = new HashSet<>();
        lst = nhiphan(n);
        System.out.println(lst);
        System.out.println(lst.toArray().length);
    }
}
