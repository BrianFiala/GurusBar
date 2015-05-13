

public class SimulationFramework {
   
   private int currentTime = 0;
   private FindMin<Event> eventQueue = 
         new MinEventHeap(new DefaultComparator());
   
   public void scheduleEvent(Event newEvent) {
      eventQueue.addElement(newEvent);
   }
   
   public void run() {
      while (!eventQueue.isEmpty()) {
         // remove first element from priority queue (MinEventHeap)
         Event nextEvent = eventQueue.removeFirst();
         currentTime = nextEvent.time;
         nextEvent.processEvent(); // what do you see here??? - polymorphism?
      }
   }
   
   // used to determine a random result given a vector of weights
   public int weightedProbability(int[] weights) {
      int weightSum = 0;
      int randNum;
      int resultIndex;
      for (int i = 0; i < weights.length; i++) {
         weightSum += weights[i];
      }
      
      
      // generate a random number between 1 and the sum of the weights
      randNum = randBetween(1, weightSum);
      // find where in the distribution the random number lies
      weightSum = 0;
      for (resultIndex = 0; resultIndex < weights.length; resultIndex++) {
         weightSum += weights[resultIndex];
         if (randNum <= weightSum) 
            return resultIndex;
      }
      return -1; // shouldn't get here. A -1 return indicates an error
   }
   
   private int randBetween(int low, int high) {
      return low + (int) ((high - low + 1) * Math.random());
   }
   
   public int time() { 
      return currentTime; 
   }
   
} // end class SimulationFramework
