import java.util.Date;

import static java.lang.Double.doubleToLongBits;

public class HashCode {
    public final class Integer{
        private int value;
        public int hashCode(){
            return value;
        }
    }
    public static final class Double{
        private double value;
        public Double(double value){
            this.value =value;
        }

        public int hashCode(){
            long bits = doubleToLongBits(value);
            return (int) (bits ^ (bits >>> 32));
        }
    }
    public static final class Boolean{
        private boolean value;
        public int hashCode(){
            if (value) return 1231;
            else return 1237;
        }
    }
    public final class String{
        private int hash = 0;
        private char[] s;
        public int hashCode(){
            int h = hash;
            if (h!= 0) return h;
            for (int i =0;i< s.length;i++){
                h = s[i] + (31*hash);

            }
            hash = h;
            return h;
        }
    }
    public final class Transaction implements Comparable<Transaction>{
        private final String who;
        private final Date when;
        private final double amount;
        public Transaction(String who, Date when, double amount){
            this.who = who;
            this.when =when;
            this.amount = amount;
        }
        public int hashCode(){
            int hash = 17 ;//Nonzero
            hash = 31*hash + who.hashCode();
            hash = 31*hash+when.hashCode();
            hash = 31*hash + new Double (amount).hashCode();
            return hash;
        }

        @Override
        public int compareTo(Transaction transaction) {
            return 0;
        }
    }
    private class Key{
        private int M;

    }
    private int hash(Key key){
        return (key.hashCode()&0x7fffffff) %key.M;
    }
}
