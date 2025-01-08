package org.example;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.One2OneChannelInt;
public class Producer implements CSProcess {
    private final One2OneChannelInt[] toBuffers;
    private final One2OneChannelInt fromMP;
    private final int start;

    public Producer(final One2OneChannelInt[] toBuffers, final One2OneChannelInt fromMP, int start){
        this.toBuffers = toBuffers;
        this.fromMP = fromMP;
        this.start = start;
    } // constructor
    public void run() {
//        System.out.println("Producer" + start + " started.");

        int item;
        while (true) {
            // wait for ProducerManager to send bufferIndex
            int bufferIndex = fromMP.in().read();

            if (bufferIndex < 0) { // End signal
                break;
            }



            // Generate and send portions
            item = (int) (Math.random() * 100) + 1 + start;
            toBuffers[bufferIndex].out().write(item);
        }

        // Send -1 to all Buffers
        for (One2OneChannelInt buffer : toBuffers) {
            buffer.out().write(-1);
        }
//        System.out.println("Producer" + start + " ended.");
    }
}