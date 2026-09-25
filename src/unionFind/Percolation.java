
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation{
    private boolean[][] matrix;
    private int opened = 0;
    private int n;
    private WeightedQuickUnionUF uf;
    private WeightedQuickUnionUF ufFull;

    public Percolation(int n){
        if (n<=0){
        throw new IllegalArgumentException();
    }
        this.n = n;
        this.uf = new WeightedQuickUnionUF(n*n +2);
        this.ufFull = new WeightedQuickUnionUF(n*n+1);
        this.matrix = new boolean[n][n];

    }
    public void open(int row, int col){

        int[] change_row = {1,-1,0,0};
        int[] change_col = {0,0,1,-1};
        if ( row <=0 || col<=0 || row>n || col>n){
            throw new IllegalArgumentException();

        }
        int i = row-1;
        int j = col-1;
        if (!isOpen(row,col)) {
            matrix[i][j] = true;
            opened++;

            int id = i * n + (j + 1);
            if (i == 0) {
                uf.union(0, id);
                ufFull.union(0,id);
            }
            if (i == n - 1) {
                uf.union(n * n + 1, id);
            }

            for (int k = 0; k < 4; k++) {
                int new_row = i + change_row[k];
                int new_col = j + change_col[k];
                if (new_row >= 0 && new_row < n && new_col >= 0 && new_col < n && matrix[new_row][new_col] == true) {
                    uf.union(id, new_row * n + (new_col + 1));
                    ufFull.union(id, new_row*n + (new_col+1));
                }

            }
        }

    }



    public boolean isOpen(int row, int col){
        if ( col <=0 || row<=0 || col>n || row>n){
            throw new IllegalArgumentException();

        }
        return matrix[row-1][col-1] ==true;
    }
    public boolean isFull(int row, int col){
        if ( col <=0 || row<=0 || col>n || row>n){
            throw new IllegalArgumentException();
        }
        int i = row-1;
        int j = col-1;
        int id = i*n+(j+1);
        return matrix[row-1][col-1] ==true && ufFull.connected(0,id);
    }

    public boolean percolates(){
        return uf.connected(0,n*n+1);
    }
    public int numberOfOpenSites(){
        return opened;
    }

}
