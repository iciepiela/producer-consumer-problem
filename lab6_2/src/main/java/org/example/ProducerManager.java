package org.example;

import org.jcsp.lang.*;

public class ProducerManager implements CSProcess {
    private final One2OneChannelInt[] toProducers;
    private int currentProducerIndex = 0;
    private int currentBufferIndex = 0;
    private final int numBuffers;
    private final int numOperations;

    public ProducerManager(One2OneChannelInt[] toProducers, int numBuffers, int numOperations) {
        this.toProducers = toProducers;
        this.numBuffers = numBuffers;
        this.numOperations = numOperations;
    }

    public void run() {
//        System.out.println("ProducerManager started.");

        for (int i = 0; i<numOperations; i++) {

            // choose producer to send portion to chosen buffer
            toProducers[currentProducerIndex].out().write(currentBufferIndex);
            currentProducerIndex = (currentProducerIndex + 1) % toProducers.length;
            currentBufferIndex = (currentBufferIndex + 1) % numBuffers;
        }

        //send ending signal
        for (One2OneChannelInt producer : toProducers) {
            producer.out().write(-1);
        }
//        System.out.println("ProducerManager ended.");
    }
}
