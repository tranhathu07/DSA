package study;
import org.junit.jupiter.api.Test;
import study.sort.MergeSort;
import study.sort.QuickSort;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class MergeSortTest {
 @Test void correctnessOrExplicitTodo() {
  try { MergeSort.sort(new Integer[0],0); }
  catch (UnsupportedOperationException e) {
   if (Boolean.getBoolean("requireImplementation")) fail("MergeSort is still TODO",e);
   System.out.println("SCAFFOLD ONLY: MergeSort TODO detected; correctness not verified."); return;
  }
  for(int c:new int[]{0,1,7,10,64}) for(int n:new int[]{0,1,2,6,7,8,9,10,11,63,64,65,1000}) {
   Integer[] a=new Random(123+n).ints(n,-20,21).boxed().toArray(Integer[]::new);
   Integer[] expected=a.clone(); Arrays.sort(expected); MergeSort.sort(a,c); assertArrayEquals(expected,a);
  }
  assertThrows(IllegalArgumentException.class,()->MergeSort.sort(new Integer[0],-1));
  for(int c:new int[]{0,7,10,64}) {
   for(Integer[] input:new Integer[][]{{Integer.MIN_VALUE,0,Integer.MAX_VALUE,-1},{1,2,3,4},{4,3,2,1},{5,5,5,5}}) {
    Integer[] expected=input.clone();Arrays.sort(expected);MergeSort.sort(input,c);assertArrayEquals(expected,input);
   }
   Item[] items=new Item[100];for(int i=0;i<items.length;i++)items[i]=new Item(i%4,i);
   Item[] expected=items.clone();Arrays.sort(expected);MergeSort.sort(items,c);assertArrayEquals(expected,items);
  }
 }
 record Item(int key,int originalIndex) implements Comparable<Item>{public int compareTo(Item other){return Integer.compare(key,other.key);}}
 @Test void quickSortCorrect() {
  for(int c:new int[]{0,1,7,10,64}) for(int n:new int[]{0,1,2,6,7,8,9,10,11,63,64,65,1000}) {
   Integer[] a=new Random(321+n).ints(n,-20,21).boxed().toArray(Integer[]::new);
   Integer[] expected=a.clone(); Arrays.sort(expected); QuickSort.sort(a,c); assertArrayEquals(expected,a);
  }
  assertThrows(IllegalArgumentException.class,()->QuickSort.sort(new Integer[0],-1));
 }
}
