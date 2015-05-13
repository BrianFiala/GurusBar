import java.util.Comparator;

// comparator object for values that satisfy comparable interface;
public class DefaultComparator implements Comparator<Event> {
   
   @Override
   // determine order of two Events: 
   // Returns: -1 if left less than right, 0 if equal, 1 otherwise equals
   public int compare(Event left, Event right) {
      return left.compareTo(right);
   }
   
   @Override
   public boolean equals(Object obj) {
      /*    Specified by:
       *       equals in interface java.util.Comparator
       *    Overrides:
       *       equals in class java.lang.Object
       */
      return obj instanceof DefaultComparator;
   }
   
   
}
