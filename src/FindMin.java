import java.util.Collection;

// interface for a min heap priority queue
public interface FindMin<T> extends Collection<T> {
   
   // add a new value to the collection
   void addElement(T value);
   
   // yields the smallest element in collection
   T getFirst();
   
   // removes and returns the smallest element in collection
   T removeFirst();
}
