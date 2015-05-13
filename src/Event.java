public abstract class Event implements Comparable<Object> {
   
   public final int time;
   public final String eventType;
   
   public Event(int t, String evnt) { 
      time = t; 
      eventType = evnt;
   }
   
   abstract void processEvent();
   
   public int compareTo(Object o) {
      Event right = (Event) o;
      if (time < right.time) return -1;
      if (time == right.time) return 0;
      return 1;
   } 
}