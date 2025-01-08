package org.example;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.One2OneChannelInt;
public class Consumer implements CSProcess {
    private final One2OneChannelInt[] fromBuffer;
    private final One2OneChannelInt[] toBuffer;
    private final One2OneChannelInt toMK;
    private final One2OneChannelInt fromMK;

    public Consumer(final One2OneChannelInt toMK,
                    final One2OneChannelInt fromMK,
                    final One2OneChannelInt[] fromBuffer,
                    final One2OneChannelInt[] toBuffer){
        this.toMK = toMK;
        this.fromBuffer = fromBuffer;
        this.toBuffer = toBuffer;
        this.fromMK = fromMK;
    } // constructor
    public void run (){
//        System.out.println("Consumer started.");

        int item = 0;
        while (true) {
            toMK.out().write(0); // Request data
//            System.out.println("Consumer requested buffer index.");

            int bufferIndex = fromMK.in().read();
//            System.out.println("Consumer received buffer index: " + bufferIndex);

            if (bufferIndex == -1) {
                break;
            }

                toBuffer[bufferIndex].out().write(0);
                item = fromBuffer[bufferIndex].in().read();
            try {
                int delay = (int) (Math.random() * 3) + 1; // Random delay between 1 and 3 ms
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


//            System.out.println(item);

        } // while
        for (One2OneChannelInt buffer : toBuffer) {
            buffer.out().write(-1);
        }
//        System.out.println("Consumer ended.");
    } // run
} // class Consumer
