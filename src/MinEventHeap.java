import java.util.Collection;
import java.util.Iterator;

// heap array priority queue of events with most recent at top
public class MinEventHeap implements FindMin<Event> {
   
   private DefaultComparator eventComparator;
   private Event[] eventArray;
   private int numEvents;
   private int size = 121; // max 120 events (a group arrives every 2 mins)
   
   public MinEventHeap(DefaultComparator defaultComparator) {
      eventComparator = defaultComparator;
      numEvents = 0;
      eventArray = new Event[size];
   }
   
   public void addElement(Event newEvent) {
      eventArray[++numEvents] = newEvent;
      swim(numEvents);
   }
   
   // returns but does not remove the first Event in the Queue
   public Event getFirst() {
      return eventArray[1]; 
   }
   
   // removes and returns the next Event to occur, then reorders the Events to 
   // maintain virtual binary tree heap priority queue
   public Event removeFirst() {
      exch(1, numEvents);
      sink(1, numEvents-1);
      return eventArray[numEvents--];
   }
   
   // helper method, raises Event at [k] to the right spot in the heap
   private void swim(int k) {
      while (k > 1 && !less(k/2, k)) {
         exch(k, k/2); // parent of node k is at k/2
         k /= 2;
      }
   }
   
   // helper method, lowers Event at [k] to the right spot in the heap
   private void sink(int k, int N) {
      while (2*k <= N) {
         int j = 2*k;
         if (j < N && !less(j, j+1)) j++;
         if (less(k, j)) break;
         exch(k, j);
         k = j;
      }
   }
   
   // helper method, compares Event at [i] to Event at [j]
   private boolean less(int i, int j) {
      return eventComparator.compare(eventArray[i], eventArray[j]) < 0;
   }
   
   // helper method, swaps Event at [i] to Event at [j]
   private void exch(int i, int j) {
      Event temp = eventArray[i];
      eventArray[i] = eventArray[j];
      eventArray[j] = temp;
   }
   
   @Override
   // returns num of event in priority queue
   public int size() {
      return numEvents;
   }
   
   @Override
   // true if there are no events in the priority queue
   public boolean isEmpty() {
      return numEvents == 0;
   }
   
   @Override
   public boolean contains(Object o) {
      // TODO Auto-generated method stub
      return false;
   }
   @Override
   public boolean add(Event e) {
      addElement(e);
      return true;
   }
   
   @Override
   public boolean remove(Object o) {
      removeFirst();
      return true;
   }
   
   @Override
   // resets the priority queue to initial conditions
   public void clear() {
      numEvents = 0;
      eventArray = new Event[size];
   }
   
   @Override
   // unimplemented
   public boolean containsAll(Collection<?> c) {
      // TODO Auto-generated method stub
      return false;
   }
   
   @Override
   // unimplemented
   public Iterator<Event> iterator() {
      // TODO Auto-generated method stub
      return null;
   }
   
   @Override
   // unimplemented
   public Object[] toArray() {
      // TODO Auto-generated method stub
      return null;
   }
   
   @Override
   // unimplemented
   public Object[] toArray(Object[] a) {
      // TODO Auto-generated method stub
      return null;
   }
   
   @Override
   // unimplemented
   public boolean addAll(Collection<? extends Event> c) {
      // TODO Auto-generated method stub
      return false;
   }
   
   @Override
   // unimplemented
   public boolean removeAll(Collection<?> c) {
      // TODO Auto-generated method stub
      return false;
   }
   
   @Override
   // unimplemented
   public boolean retainAll(Collection<?> c) {
      // TODO Auto-generated method stub
      return false;
   }
   
}
