import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double[] threshold;
    private double mean;
    private double s;
    private int trials;
    private int n;

    public PercolationStats(int n, int trials){
        if ( n<=0 || trials <=0){
            throw new IllegalArgumentException();
        }
        this.trials = trials;
        this.n =n;

        this.threshold = new double[trials];
        for ( int i = 0; i< trials;i++){
            Percolation p = new Percolation(n);
            while (!p.percolates()) {
                int row = StdRandom.uniformInt(1, n+1);
                int col = StdRandom.uniformInt(1,n+1);
                if (!p.isOpen(row,col)){
                    p.open(row,col);
                }
            }
            threshold[i] =  (double) (p.numberOfOpenSites()) /(n*n);
        }
        this.mean =  StdStats.mean(threshold);
        this.s= StdStats.stddev(threshold);
    }
    public double mean(){

        return mean;
    }
    public double stddev(){

        return s;
    }
    public double confidenceLo(){
        return mean - ((1.96)*s)/Math.sqrt(trials);
    }
    public double confidenceHi(){
        return mean + ((1.96)*s)/Math.sqrt(trials);
    }
    public static void main(String[] args){
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        PercolationStats p = new PercolationStats(n,trials);
        System.out.println("mean                    = "+ p.mean());
        System.out.println("stddev                  = "+ p.stddev());
        System.out.println("95% confidence interval = [" + p.confidenceLo() + ", " + p.confidenceHi() + "]");
    }

}

