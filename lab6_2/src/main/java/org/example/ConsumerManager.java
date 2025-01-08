package org.example;

import org.jcsp.lang.*;

public class ConsumerManager implements CSProcess {
    private final One2OneChannelInt[] fromConsumers;
    private final One2OneChannelInt[] toConsumers;
    private int currentBufferIndex = 0;
    private final int numBuffers;
    private final int numOperations;

    public ConsumerManager(
            One2OneChannelInt[] fromConsumers,
            One2OneChannelInt[] toConsumers,
            int numBuffers, int numOperations) {
        this.fromConsumers = fromConsumers;
        this.toConsumers = toConsumers;
        this.numBuffers = numBuffers;
        this.numOperations = numOperations;
    }

    public void run() {
//        System.out.println("ConsumerManager started.");
        final Guard[] guards = new Guard[fromConsumers.length];
        int countdown = fromConsumers.length;

        for (int i = 0; i < fromConsumers.length; i++) {
            guards[i] = fromConsumers[i].in();
        }

        final Alternative alt = new Alternative(guards);
        for (int i = 0; i < numOperations; i++) {
            int consumerIndex = alt.select();
            fromConsumers[consumerIndex].in().read();

//            System.out.println("Selected consumer: " + consumerIndex);

            toConsumers[consumerIndex].out().write(currentBufferIndex);
//            System.out.println("Sent buffer index: " + currentBufferIndex + " to consumer: " + consumerIndex);

            currentBufferIndex = (currentBufferIndex + 1) % numBuffers;
        }
        while(countdown>0) {
            int consumerIndex = alt.select();
            fromConsumers[consumerIndex].in().read();

            toConsumers[consumerIndex].out().write(-1);
            countdown--;
        }
//        System.out.println("ConsumerManager ended.");
    }
}

