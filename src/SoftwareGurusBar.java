/* File       : SoftwareGurusBar.txt
 * Author     : Brian Fiala
 * Date       : 11/2/14
 * Attribution: some code used with permission from Professor Mousallam,
 *              CS1C Data Structures and Algorithms, Foothill Community College
 * Description: SoftwareGurusBar is a simulation of a bar that determines 
 *              estimated income for a user inputted number of chairs.
 */

import java.util.Scanner;

public class SoftwareGurusBar {
   private int freeChairs;
   private double profit = 0.0;
   private SimulationFramework simulation = new SimulationFramework();
   private int timeOpen = 240;
   
   private int groupsSeated = 0;
   private int groupsTurnedAway = 0;
   private int customersServed = 0;
   private int customersTurnedAway = 0;
   private int[] groupsOfEachSize = new int[5];
   private int[] ordersForEachTime = new int[9];
   private int[] ordersOfEachBeer = new int[3];
   
   private int[] groupSizeProbabilities = {10, 35, 35, 10, 10};
   // groupSizeProbabilities represent weights for groups of 1, 2, 3, 4, or 5
   private int[] beerChoiceProbabilities = {20, 35, 45};
   // beerChoiceProbabilities represent weights for beer choices:
   // domestic, imported, and microbrew
   private int[] orderTimeProbabilities = {30, 20, 10, 10, 5, 5, 5, 3, 2};
   // above represents weights for amount of time it takes a group to order
   
   public static void main(String [] args) {
      SoftwareGurusBar world = new SoftwareGurusBar();
   }
   
   SoftwareGurusBar() {
      Scanner sysInput = new Scanner(System.in);
      System.out.print("Please enter the number of chairs you would like to "
            + "simulate: ");
      int numChairs = sysInput.nextInt();
      System.out.println("\n");
      freeChairs = numChairs;
      int t = randBetween(2, 5);
      while (t < timeOpen) { // simulate 4 hours of bar operation
         // distribution for arrival time interval is uniform
         int groupSize = // ranges from 1 to 5...
               1 + simulation.weightedProbability(groupSizeProbabilities);
         if (groupSize == 0) System.out.println("Error");
         groupsOfEachSize[groupSize-1]++;
         simulation.scheduleEvent(new ArriveEvent(t, groupSize));
         t += randBetween(2, 5); // new group every 2-5 minutes
      }
      simulation.run();
      
      System.out.println("\n\n\nSummary of results:\n");
      System.out.printf("Total profits $%,.2f\n\n", profit);
      
      for (int i = 0; i < groupsOfEachSize.length; i++) {
         System.out.println("There were " + groupsOfEachSize[i]
               + " groups of " + (i+1) + " people.");
      }
      
      System.out.println();
      for (int i = 0; i < ordersForEachTime.length; i++) {
         System.out.println("There were " + ordersForEachTime[i] 
               + " orders at " + (i+2) + " minutes.");
      }
      
      System.out.println();
      System.out.println("There were " + ordersOfEachBeer[0] + " orders for "
            + "domestic beers.");
      System.out.println("There were " + ordersOfEachBeer[1] + " orders for "
            + "imported beers.");
      System.out.println("There were " + ordersOfEachBeer[2] + " orders for "
            + "microbrew beers.");
      
      System.out.println();
      System.out.println("There were " + groupsSeated + " groups totaling "
            + customersServed + " customers served.");
      System.out.println("There were " + groupsTurnedAway 
            + " groups totaling " + customersTurnedAway
            + " customers turned away.\n");
      
      if (customersTurnedAway > 5) {
         System.out.println("The simulation indicates that " + numChairs
               + " is not enough seats for maximal profit.\n");
      }
      else if (customersTurnedAway > 0) {
         System.out.println("The simulation indicates that " + numChairs 
               + " is an approximately optimal number of chairs.\n");
      }
      else {
         System.out.println("The simulation indicates that " + numChairs
            + " may be too many chairs for maximal profit");
      }
      
      sysInput.close();
   } // end SoftwareGurusBar() 
   
   private int randBetween(int low, int high) {
      return low + (int) ((high - low + 1) * Math.random());
   }
   
   public boolean canSeat(int numberOfPeople) {
      System.out.println("Group of " + numberOfPeople +
            " customers arrives at time " + simulation.time());
      if (numberOfPeople < freeChairs) {
         System.out.println("Group is seated");
         freeChairs -= numberOfPeople;
         groupsSeated++;
         customersServed += numberOfPeople;
         return true;
      } 
      else {
         System.out.println("No Room, Group Leaves");
         groupsTurnedAway++;
         customersTurnedAway += numberOfPeople;
      }
      return false;
   }
   
   private void order(int beerType) {
      System.out.print("Serviced order for ");
      switch (beerType) {
         case 2:
            System.out.print("domestic ");
            break;
         case 3:
            System.out.print("imported ");
            break;
         case 4:
            System.out.print("microbrew ");
            break;
         default:
            System.out.print("ERROR ");
      }
      System.out.println("beer at time " + simulation.time());
      profit += beerType; // conveniently, beerType and profit are the same
      ordersOfEachBeer[beerType-2]++;
   }
   
   private void leave(int numberOfPeople) {
      System.out.println("Group of size " + numberOfPeople +
            " leaves at time " + simulation.time());
      freeChairs += numberOfPeople;
   }
   
   private class ArriveEvent extends Event {
      private int groupSize;
      
      ArriveEvent(int time, int gs) { 
         super(time, "ArriveEvent"); 
         groupSize = gs; 
      }
      
      public void processEvent() {
         if (canSeat(groupSize)) {
            // place an order within 2 & 10 minutes
            
            int orderTime = // ranges from 1 to 5...
                  2 + simulation.weightedProbability(orderTimeProbabilities);
            if (orderTime == 1) System.out.println("Error");
            ordersForEachTime[orderTime-2]++;
            simulation.scheduleEvent (
                  new OrderEvent(time + orderTime, groupSize));
         }
      } // end processEvent()
   } // end inner class ArriveEvent
   
   private class OrderEvent extends Event {
      private int groupSize;
      private int roundsServed = 0;
      
      OrderEvent (int time, int gs) { 
         super(time, "OrderEvent"); 
         groupSize = gs; 
      }
      
      public void processEvent() {
         
         // each member of the group orders a beer (type 1,2,3) 
         int beerChoice = 
               2 + simulation.weightedProbability(beerChoiceProbabilities);;
               for (int i = 0; i < groupSize; i++) {
                  order(beerChoice);
               }
               
               roundsServed++;
               int nextEventTime = time + randBetween(30, 60);
               if (nextEventTime < timeOpen) { // if not closed before they reorder
                  int reorderWeight = 50 - ((roundsServed-1)*10);
                  int[] reorderProbabilities = {100 - reorderWeight, reorderWeight}; 
                  // reorder weight calculated so 50/50 chance of reordering after 
                  // first round, decreasing until all groups stop at 6 rounds
                  int reorder = simulation.weightedProbability(reorderProbabilities);
                  if (reorder == 1) {
                     simulation.scheduleEvent(
                           new OrderEvent(nextEventTime, groupSize));
                  }
               }
               else { // closed or they didn't want to reorder
                  simulation.scheduleEvent(new LeaveEvent(nextEventTime, groupSize));
               }
      } // end processEvent()
   } // end inner class OrderEvent
   
   private class LeaveEvent extends Event {
      private int groupSize;
      
      LeaveEvent(int time, int gs) { 
         super(time, "LeaveEvent"); 
         groupSize = gs; 
      } 
      
      public void processEvent() { 
         leave(groupSize); 
      } 
   } // end inner class LeaveEvent
} // end class SoftwareGurusBar

/***************************** CONSOLE PASTE ***********************************

Please enter the number of chairs you would like to simulate: 188


Group of 3 customers arrives at time 4
Group is seated
Group of 3 customers arrives at time 7
Group is seated
Serviced order for domestic beer at time 9
Serviced order for domestic beer at time 9
Serviced order for domestic beer at time 9
Group of 1 customers arrives at time 11
Group is seated
Serviced order for imported beer at time 13
Serviced order for imported beer at time 13
Serviced order for imported beer at time 13
Group of 2 customers arrives at time 13
Group is seated
Serviced order for microbrew beer at time 15
Group of 3 customers arrives at time 18
Group is seated
Group of 2 customers arrives at time 22
Group is seated
Serviced order for microbrew beer at time 23
Serviced order for microbrew beer at time 23
Serviced order for domestic beer at time 25
Serviced order for domestic beer at time 25
Group of 2 customers arrives at time 27
Group is seated
Serviced order for domestic beer at time 27
Serviced order for domestic beer at time 27
Serviced order for domestic beer at time 27
Serviced order for microbrew beer at time 29
Serviced order for microbrew beer at time 29
Group of 2 customers arrives at time 30
Group is seated
Serviced order for imported beer at time 32
Serviced order for imported beer at time 32
Group of 2 customers arrives at time 33
Group is seated
Serviced order for microbrew beer at time 36
Serviced order for microbrew beer at time 36
Group of 2 customers arrives at time 37
Group is seated
Serviced order for microbrew beer at time 40
Serviced order for microbrew beer at time 40
Group of 3 customers arrives at time 41
Group is seated
Serviced order for domestic beer at time 45
Serviced order for domestic beer at time 45
Serviced order for domestic beer at time 45
Group of 4 customers arrives at time 45
Group is seated
Group of 1 customers arrives at time 47
Group is seated
Group of 3 customers arrives at time 49
Group is seated
Serviced order for microbrew beer at time 51
Group of 2 customers arrives at time 52
Group is seated
Serviced order for imported beer at time 52
Serviced order for imported beer at time 52
Serviced order for imported beer at time 52
Serviced order for imported beer at time 52
Serviced order for imported beer at time 54
Serviced order for imported beer at time 55
Serviced order for imported beer at time 55
Group of 3 customers arrives at time 56
Group is seated
Serviced order for imported beer at time 58
Serviced order for imported beer at time 58
Serviced order for imported beer at time 58
Group of 2 customers arrives at time 58
Group is seated
Serviced order for microbrew beer at time 59
Serviced order for microbrew beer at time 59
Serviced order for microbrew beer at time 59
Serviced order for microbrew beer at time 62
Serviced order for microbrew beer at time 62
Group of 5 customers arrives at time 62
Group is seated
Serviced order for imported beer at time 63
Serviced order for imported beer at time 63
Serviced order for imported beer at time 63
Serviced order for imported beer at time 64
Serviced order for imported beer at time 64
Group of 3 customers arrives at time 66
Group is seated
Serviced order for imported beer at time 68
Serviced order for imported beer at time 68
Serviced order for imported beer at time 68
Group of 1 customers arrives at time 68
Group is seated
Serviced order for imported beer at time 69
Serviced order for imported beer at time 69
Serviced order for imported beer at time 69
Serviced order for imported beer at time 69
Serviced order for imported beer at time 69
Serviced order for imported beer at time 72
Group of 2 customers arrives at time 73
Group is seated
Serviced order for domestic beer at time 73
Serviced order for domestic beer at time 73
Group of 2 customers arrives at time 77
Group is seated
Serviced order for microbrew beer at time 81
Serviced order for microbrew beer at time 81
Serviced order for microbrew beer at time 81
Serviced order for microbrew beer at time 81
Group of 3 customers arrives at time 82
Group is seated
Group of 3 customers arrives at time 84
Group is seated
Serviced order for microbrew beer at time 86
Serviced order for microbrew beer at time 86
Serviced order for microbrew beer at time 86
Serviced order for imported beer at time 86
Serviced order for imported beer at time 86
Serviced order for imported beer at time 86
Group of 2 customers arrives at time 88
Group is seated
Serviced order for domestic beer at time 90
Serviced order for domestic beer at time 90
Group of 2 customers arrives at time 91
Group is seated
Serviced order for imported beer at time 93
Serviced order for imported beer at time 93
Group of 1 customers arrives at time 93
Group is seated
Group of 5 customers arrives at time 95
Group is seated
Serviced order for microbrew beer at time 96
Serviced order for microbrew beer at time 96
Serviced order for microbrew beer at time 96
Serviced order for microbrew beer at time 96
Serviced order for microbrew beer at time 96
Serviced order for microbrew beer at time 96
Serviced order for domestic beer at time 97
Serviced order for domestic beer at time 97
Serviced order for domestic beer at time 97
Serviced order for domestic beer at time 97
Serviced order for domestic beer at time 97
Serviced order for domestic beer at time 97
Serviced order for domestic beer at time 97
Serviced order for imported beer at time 98
Serviced order for imported beer at time 98
Serviced order for imported beer at time 98
Serviced order for microbrew beer at time 99
Serviced order for imported beer at time 99
Serviced order for imported beer at time 99
Group of 3 customers arrives at time 100
Group is seated
Group of 5 customers arrives at time 103
Group is seated
Serviced order for microbrew beer at time 105
Serviced order for microbrew beer at time 105
Serviced order for microbrew beer at time 105
Serviced order for imported beer at time 106
Serviced order for imported beer at time 106
Serviced order for imported beer at time 106
Serviced order for imported beer at time 106
Serviced order for imported beer at time 106
Group of 1 customers arrives at time 108
Group is seated
Group of 1 customers arrives at time 111
Group is seated
Serviced order for imported beer at time 113
Serviced order for imported beer at time 113
Group of 1 customers arrives at time 113
Group is seated
Serviced order for microbrew beer at time 114
Group of 4 customers arrives at time 115
Group is seated
Serviced order for microbrew beer at time 116
Serviced order for microbrew beer at time 117
Serviced order for microbrew beer at time 117
Serviced order for microbrew beer at time 117
Serviced order for microbrew beer at time 117
Group of 2 customers arrives at time 118
Group is seated
Serviced order for microbrew beer at time 119
Serviced order for microbrew beer at time 119
Serviced order for microbrew beer at time 119
Serviced order for imported beer at time 120
Serviced order for imported beer at time 120
Serviced order for microbrew beer at time 120
Serviced order for microbrew beer at time 120
Serviced order for microbrew beer at time 120
Serviced order for microbrew beer at time 120
Serviced order for microbrew beer at time 120
Serviced order for imported beer at time 121
Group of 2 customers arrives at time 121
Group is seated
Group of 3 customers arrives at time 123
Group is seated
Serviced order for domestic beer at time 124
Serviced order for domestic beer at time 124
Serviced order for domestic beer at time 124
Serviced order for domestic beer at time 125
Serviced order for domestic beer at time 125
Serviced order for domestic beer at time 125
Group of 2 customers arrives at time 127
Group is seated
Serviced order for imported beer at time 129
Serviced order for imported beer at time 129
Serviced order for domestic beer at time 129
Serviced order for domestic beer at time 129
Serviced order for microbrew beer at time 130
Serviced order for microbrew beer at time 130
Serviced order for imported beer at time 132
Serviced order for imported beer at time 132
Serviced order for imported beer at time 132
Group of 5 customers arrives at time 132
Group is seated
Serviced order for imported beer at time 136
Serviced order for imported beer at time 136
Group of 2 customers arrives at time 137
Group is seated
Serviced order for microbrew beer at time 139
Serviced order for microbrew beer at time 139
Serviced order for imported beer at time 140
Serviced order for imported beer at time 140
Serviced order for imported beer at time 140
Group of 3 customers arrives at time 141
Group is seated
Serviced order for domestic beer at time 142
Serviced order for domestic beer at time 142
Serviced order for domestic beer at time 142
Serviced order for domestic beer at time 142
Serviced order for domestic beer at time 142
Serviced order for domestic beer at time 143
Serviced order for domestic beer at time 143
Serviced order for domestic beer at time 143
Serviced order for microbrew beer at time 146
Serviced order for microbrew beer at time 146
Serviced order for microbrew beer at time 146
Group of 4 customers arrives at time 146
Group is seated
Group of 3 customers arrives at time 148
Group is seated
Serviced order for imported beer at time 150
Serviced order for imported beer at time 150
Serviced order for imported beer at time 150
Serviced order for imported beer at time 150
Group of 3 customers arrives at time 151
Group is seated
Serviced order for microbrew beer at time 154
Serviced order for microbrew beer at time 154
Serviced order for microbrew beer at time 154
Serviced order for microbrew beer at time 154
Group of 5 customers arrives at time 154
Group is seated
Serviced order for microbrew beer at time 154
Serviced order for microbrew beer at time 154
Serviced order for microbrew beer at time 154
Serviced order for microbrew beer at time 156
Serviced order for microbrew beer at time 156
Serviced order for microbrew beer at time 156
Serviced order for microbrew beer at time 156
Serviced order for microbrew beer at time 156
Group of 3 customers arrives at time 157
Group is seated
Serviced order for domestic beer at time 157
Serviced order for domestic beer at time 157
Serviced order for domestic beer at time 157
Group of 4 customers arrives at time 159
Group is seated
Serviced order for microbrew beer at time 161
Serviced order for microbrew beer at time 161
Serviced order for microbrew beer at time 161
Serviced order for microbrew beer at time 161
Serviced order for imported beer at time 161
Serviced order for imported beer at time 161
Serviced order for imported beer at time 161
Group of 2 customers arrives at time 162
Group is seated
Serviced order for domestic beer at time 163
Serviced order for domestic beer at time 163
Serviced order for domestic beer at time 163
Group of 3 customers arrives at time 164
Group is seated
Serviced order for domestic beer at time 164
Serviced order for microbrew beer at time 164
Serviced order for microbrew beer at time 164
Serviced order for domestic beer at time 165
Serviced order for domestic beer at time 165
Serviced order for imported beer at time 167
Serviced order for imported beer at time 167
Serviced order for imported beer at time 167
Group of 2 customers arrives at time 169
Group is seated
Serviced order for imported beer at time 172
Serviced order for imported beer at time 172
Group of 2 customers arrives at time 172
Group is seated
Serviced order for microbrew beer at time 174
Serviced order for microbrew beer at time 174
Group of 1 customers arrives at time 177
Group is seated
Serviced order for domestic beer at time 179
Serviced order for domestic beer at time 179
Group of 3 customers arrives at time 180
Group is seated
Serviced order for microbrew beer at time 180
Serviced order for microbrew beer at time 182
Serviced order for microbrew beer at time 182
Serviced order for microbrew beer at time 182
Group of 2 customers arrives at time 184
Group is seated
Serviced order for imported beer at time 185
Serviced order for imported beer at time 185
Serviced order for imported beer at time 185
Serviced order for imported beer at time 185
Serviced order for imported beer at time 185
Group of 4 customers arrives at time 186
Group is seated
Serviced order for imported beer at time 187
Serviced order for imported beer at time 187
Serviced order for imported beer at time 188
Serviced order for imported beer at time 188
Serviced order for imported beer at time 188
Serviced order for imported beer at time 188
Group of 1 customers arrives at time 190
Group is seated
Serviced order for domestic beer at time 194
Serviced order for microbrew beer at time 194
Serviced order for microbrew beer at time 194
Serviced order for microbrew beer at time 194
Serviced order for microbrew beer at time 194
Group of 2 customers arrives at time 195
Group is seated
Serviced order for microbrew beer at time 197
Serviced order for microbrew beer at time 197
Group of 2 customers arrives at time 199
Group is seated
Serviced order for domestic beer at time 201
Serviced order for domestic beer at time 201
Group of 2 customers arrives at time 201
Group is seated
Serviced order for microbrew beer at time 202
Serviced order for microbrew beer at time 202
Serviced order for microbrew beer at time 202
Serviced order for microbrew beer at time 202
Group of 3 customers arrives at time 203
Group is seated
Serviced order for microbrew beer at time 205
Serviced order for microbrew beer at time 205
Group of 5 customers arrives at time 205
Group is seated
Serviced order for imported beer at time 205
Serviced order for microbrew beer at time 207
Serviced order for microbrew beer at time 207
Serviced order for microbrew beer at time 207
Serviced order for microbrew beer at time 208
Serviced order for microbrew beer at time 208
Serviced order for microbrew beer at time 208
Serviced order for microbrew beer at time 208
Serviced order for microbrew beer at time 208
Serviced order for microbrew beer at time 209
Serviced order for microbrew beer at time 209
Serviced order for microbrew beer at time 209
Serviced order for imported beer at time 209
Serviced order for imported beer at time 209
Group of 4 customers arrives at time 209
Group is seated
Serviced order for microbrew beer at time 211
Serviced order for microbrew beer at time 211
Serviced order for microbrew beer at time 211
Serviced order for microbrew beer at time 211
Serviced order for microbrew beer at time 211
Serviced order for microbrew beer at time 211
Serviced order for microbrew beer at time 211
Group of 4 customers arrives at time 213
Group is seated
Group of 2 customers arrives at time 216
Group is seated
Serviced order for domestic beer at time 216
Serviced order for domestic beer at time 216
Serviced order for domestic beer at time 216
Serviced order for domestic beer at time 216
Serviced order for domestic beer at time 216
Group of 2 customers arrives at time 218
Group is seated
Serviced order for imported beer at time 218
Serviced order for imported beer at time 218
Serviced order for microbrew beer at time 220
Serviced order for microbrew beer at time 220
Serviced order for microbrew beer at time 221
Serviced order for microbrew beer at time 221
Serviced order for microbrew beer at time 221
Group of 3 customers arrives at time 221
Group is seated
Serviced order for domestic beer at time 221
Serviced order for domestic beer at time 221
Serviced order for microbrew beer at time 223
Serviced order for microbrew beer at time 223
Serviced order for microbrew beer at time 223
Serviced order for microbrew beer at time 223
Serviced order for domestic beer at time 224
Serviced order for domestic beer at time 224
Group of 3 customers arrives at time 224
Group is seated
Serviced order for microbrew beer at time 226
Serviced order for microbrew beer at time 226
Serviced order for microbrew beer at time 226
Serviced order for imported beer at time 227
Serviced order for imported beer at time 227
Serviced order for imported beer at time 227
Group of 3 customers arrives at time 228
Group is seated
Serviced order for imported beer at time 230
Serviced order for imported beer at time 230
Serviced order for imported beer at time 230
Serviced order for microbrew beer at time 230
Serviced order for microbrew beer at time 230
Group of 3 customers arrives at time 232
Group is seated
Serviced order for microbrew beer at time 234
Serviced order for microbrew beer at time 234
Serviced order for microbrew beer at time 234
Group of 3 customers arrives at time 234
Group is seated
Group of 5 customers arrives at time 239
No Room, Group Leaves
Serviced order for microbrew beer at time 242
Serviced order for microbrew beer at time 242
Serviced order for microbrew beer at time 242
Group of size 5 leaves at time 243
Group of size 1 leaves at time 244
Group of size 2 leaves at time 245
Group of size 4 leaves at time 246
Group of size 5 leaves at time 246
Group of size 2 leaves at time 248
Group of size 2 leaves at time 249
Group of size 2 leaves at time 249
Group of size 4 leaves at time 250
Group of size 4 leaves at time 255
Group of size 2 leaves at time 256
Group of size 3 leaves at time 256
Group of size 3 leaves at time 256
Group of size 2 leaves at time 258
Group of size 3 leaves at time 260
Group of size 3 leaves at time 262
Group of size 4 leaves at time 262
Group of size 3 leaves at time 262
Group of size 3 leaves at time 266
Group of size 2 leaves at time 267
Group of size 5 leaves at time 272
Group of size 3 leaves at time 277
Group of size 2 leaves at time 277
Group of size 2 leaves at time 278
Group of size 3 leaves at time 284
Group of size 3 leaves at time 294



Summary of results:

Total profits $909.00

There were 9 groups of 1 people.
There were 25 groups of 2 people.
There were 23 groups of 3 people.
There were 7 groups of 4 people.
There were 7 groups of 5 people.

There were 24 orders at 2 minutes.
There were 11 orders at 3 minutes.
There were 9 orders at 4 minutes.
There were 4 orders at 5 minutes.
There were 8 orders at 6 minutes.
There were 3 orders at 7 minutes.
There were 6 orders at 8 minutes.
There were 1 orders at 9 minutes.
There were 4 orders at 10 minutes.

There were 61 orders for domestic beers.
There were 93 orders for imported beers.
There were 127 orders for microbrew beers.

There were 70 groups totaling 186 customers served.
There were 1 groups totaling 5 customers turned away.

The simulation indicates that 188 is an approximately optimal number of chairs.

***************************** END CONSOLE PASTE *******************************/
