package org.example;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2OneChannelInt;

public class Buffer implements CSProcess {
    private final One2OneChannelInt[] fromProducers; // Input from Producer
    private final One2OneChannelInt[] toConsumers; // Output to Consumer
    private final One2OneChannelInt[] fromConsumers;
    // The buffer itself
    private final int[] buffer = new int[10];
    // Subscripts for buffer
    int hd = -1;
    int tl = -1;
    public Buffer (
            final One2OneChannelInt[] fromProducers,
            final One2OneChannelInt[] toConsumers,
            final One2OneChannelInt[] fromConsumers) {
        this.fromProducers = fromProducers;
        this.fromConsumers = fromConsumers;
        this.toConsumers = toConsumers;
    } // constructor
    public void run (){
//        System.out.println("Buffer started.");

        final Guard[] guards = new Guard[fromProducers.length+fromConsumers.length];
        for (int i = 0; i < fromProducers.length; i++){
            guards[i] = fromProducers[i].in();
        }
        for (int i = 0; i < fromConsumers.length; i++){
            guards[fromProducers.length+i] = fromConsumers[i].in();
        }
        final Alternative alt = new Alternative(guards);
        int countdown = fromProducers.length  + fromConsumers.length ; // Number of producent processes running
        while (countdown > 0) {
            int index = alt.select();
            if (index >= 0 && index < fromProducers.length) { // A Producer is ready to send
                if (hd < tl + 11){ // Space available
                    int item = fromProducers[index].in().read();

                    if (item < 0)
                        countdown--;
                    else {
                        hd++;
                        buffer[hd%buffer.length] = item;
                    }
                }
            }//if
            if (index >= fromProducers.length && index < fromConsumers.length + fromProducers.length) {
                if (tl < hd) { // Item(s) available
                    if (fromConsumers[index - fromProducers.length].in().read()<0){
                        countdown--;
                    }
                    else {
                        tl++;
                        int item = buffer[tl % buffer.length];
                        toConsumers[index - fromProducers.length].out().write(item); // Send to chosen consumer
                    }
                }
                else if(countdown<=fromConsumers.length){

                    if (fromConsumers[index - fromProducers.length].in().read()<0){
                        countdown--;
                    }
                    else {
                        toConsumers[index - fromProducers.length].out().write(-1);
                    }
                }


            } //if
        } // while
//        System.out.println("Buffer ended.");
    } // run
} // class Buffer
