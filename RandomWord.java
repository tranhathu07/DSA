import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;


public class RandomWord {
    public static void main(String[] args) {

        int count = 0;
        String winner = "";
        while (!StdIn.isEmpty()) {
            String word = StdIn.readString();
            count++;
            if (StdRandom.bernoulli(1.0/count)) {
                winner = word;
            }
        }
        StdOut.println(winner);
    }
}
