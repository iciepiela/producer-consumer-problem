package org.example;

import org.jcsp.lang.*;

import java.io.FileWriter;
import java.io.IOException;

/** Main program class for Producer/Consumer example.
 * Sets up channels, creates processes then
 * executes them in parallel, using JCSP.
 */
public final class Main

{
    private static final boolean RUN_TESTS = false;

    public static void main(String[] args) {
        if (RUN_TESTS) {
            // Test execution time based on the number of buffers
            try (FileWriter csvWriter = new FileWriter("execution_times_buffers_slowc.csv")) {
                csvWriter.append("NumBuffers,ExecutionTimeMillis\n");

                for (int numBuffers = 10; numBuffers <= 300; numBuffers += 10) {
                    long totalTime = 0;

                    for (int i = 0; i < 5; i++) { // Repeat 5 times
                        long startTime = System.currentTimeMillis();
                        new Main(25, 25, numBuffers, 1000);
                        long endTime = System.currentTimeMillis();
                        totalTime += (endTime - startTime);
                    }

                    long averageTime = totalTime / 5;
                    csvWriter.append(numBuffers + "," + averageTime + "\n");
                    System.out.println("Buffers: " + numBuffers + " -> Avg Time: " + averageTime + " ms");
                }

                System.out.println("Execution times saved to execution_times_buffers.csv");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Test execution time based on the number of producers
            try (FileWriter csvWriterProducers = new FileWriter("execution_times_producers_slowc.csv")) {
                csvWriterProducers.append("NumProducers,ExecutionTimeMillis\n");

                for (int numProducers = 10; numProducers <= 300; numProducers += 10) {
                    long totalTime = 0;

                    for (int i = 0; i < 5; i++) { // Repeat 5 times
                        long startTime = System.currentTimeMillis();
                        new Main(numProducers, 25, 25, 1000);
                        long endTime = System.currentTimeMillis();
                        totalTime += (endTime - startTime);
                    }

                    long averageTime = totalTime / 5;
                    csvWriterProducers.append(numProducers + "," + averageTime + "\n");
                    System.out.println("Producers: " + numProducers + " -> Avg Time: " + averageTime + " ms");
                }

                System.out.println("Execution times saved to execution_times_producers.csv");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Test execution time based on the number of consumers
            try (FileWriter csvWriterConsumers = new FileWriter("execution_times_consumers_slowc.csv")) {
                csvWriterConsumers.append("NumConsumers,ExecutionTimeMillis\n");

                for (int numConsumers = 10; numConsumers <= 300; numConsumers += 10) {
                    long totalTime = 0;

                    for (int i = 0; i < 5; i++) { // Repeat 5 times
                        long startTime = System.currentTimeMillis();
                        new Main(25, numConsumers, 25, 1000);
                        long endTime = System.currentTimeMillis();
                        totalTime += (endTime - startTime);
                    }

                    long averageTime = totalTime / 5;
                    csvWriterConsumers.append(numConsumers + "," + averageTime + "\n");
                    System.out.println("Consumers: " + numConsumers + " -> Avg Time: " + averageTime + " ms");
                }

                System.out.println("Execution times saved to execution_times_consumers.csv");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Test execution time based on both consumers and producers
            try (FileWriter csvWriterConsumersProducers = new FileWriter("execution_times_consumers_producers_slowc.csv")) {
                csvWriterConsumersProducers.append("NumConsumers,ExecutionTimeMillis\n");

                for (int numConsumers = 10; numConsumers <= 300; numConsumers += 10) {
                    long totalTime = 0;

                    for (int i = 0; i < 5; i++) { // Repeat 5 times
                        long startTime = System.currentTimeMillis();
                        new Main(numConsumers, numConsumers, 25, 1000);
                        long endTime = System.currentTimeMillis();
                        totalTime += (endTime - startTime);
                    }

                    long averageTime = totalTime / 5;
                    csvWriterConsumersProducers.append(numConsumers + "," + averageTime + "\n");
                    System.out.println("Consumers and Producers: " + numConsumers + " -> Avg Time: " + averageTime + " ms");
                }

                System.out.println("Execution times saved to execution_times_consumers_producers.csv");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{

            long startTime = System.currentTimeMillis();

            new Main(100, 100, 10, 50000); // Fixed consumers and buffers

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            System.out.println("Consumers: " + 100 + " -> Time: " + executionTime + " ms");
//            new PCMain2(3, 50, 5, 1000);
        }
    }

    public Main(int numProducers, int numConsumers, int numBuffers, int numOperations){

        final One2OneChannelInt[] mpToProducers = new One2OneChannelInt[numProducers];
        for (int i = 0; i < numProducers; i++) {
            mpToProducers[i] = Channel.one2oneInt();
        }

        final One2OneChannelInt[] consumersToMk = new One2OneChannelInt[numConsumers];
        for (int i = 0; i < numConsumers; i++) {
            consumersToMk[i] = Channel.one2oneInt();
        }


        final One2OneChannelInt[] mkToConsumers = new One2OneChannelInt[numConsumers];
        for (int i = 0; i < numConsumers; i++) {
            mkToConsumers[i] = Channel.one2oneInt();
        }


        final One2OneChannelInt[][] producersToBuffer = new One2OneChannelInt[numBuffers][];
        final One2OneChannelInt[][] bufferToConsumers = new One2OneChannelInt[numBuffers][];
        final One2OneChannelInt[][] consumersToBuffer = new One2OneChannelInt[numBuffers][];

        for (int i = 0; i < numBuffers; i++) {

            producersToBuffer[i] = new One2OneChannelInt[numProducers];
            bufferToConsumers[i] = new One2OneChannelInt[numConsumers];
            consumersToBuffer[i] = new One2OneChannelInt[numConsumers];
            for (int j = 0; j < numProducers; j++) {
                producersToBuffer[i][j] = Channel.one2oneInt();
            }
            for (int j = 0; j < numConsumers; j++) {
                bufferToConsumers[i][j] = Channel.one2oneInt();
                consumersToBuffer[i][j] = Channel.one2oneInt();
            }
        }

        CSProcess[] procList = new CSProcess[numProducers + numConsumers + numBuffers +2];

        // Add producers
        for (int i = 0; i < numProducers; i++) {
            One2OneChannelInt[] producerBufferChannels = new One2OneChannelInt[numBuffers];
            for (int j = 0; j < numBuffers; j++) {
                producerBufferChannels[j] = producersToBuffer[j][i];
            }
            procList[i] = new Producer(producerBufferChannels, mpToProducers[i], i * 100);
        }

        // Add buffers
        for (int i = 0; i < numBuffers; i++) {
            procList[numProducers + i] = new Buffer(producersToBuffer[i], bufferToConsumers[i],consumersToBuffer[i]);
        }

        // Add consumers
        for (int i = 0; i < numConsumers; i++) {
            One2OneChannelInt[] bufferConsumersChannels = new One2OneChannelInt[numBuffers];
            One2OneChannelInt[] consumerBuffersChannels = new One2OneChannelInt[numBuffers];
            for (int j = 0; j < numBuffers; j++) {
                bufferConsumersChannels[j] = bufferToConsumers[j][i];
                consumerBuffersChannels[j] = consumersToBuffer[j][i];
            }
            procList[numProducers + numBuffers + i] = new Consumer(consumersToMk[i], mkToConsumers[i], bufferConsumersChannels,consumerBuffersChannels);
        }
        //Add Producer Manager
        procList[numProducers + numBuffers + numConsumers] = new ProducerManager(mpToProducers,numBuffers,numOperations);

        //Add Consumer Manager
        procList[numProducers + numBuffers + numConsumers+1] = new ConsumerManager(consumersToMk,mkToConsumers,numBuffers,numOperations);

        Parallel par = new Parallel(procList); // PAR construct
        par.run(); // Execute processes in parallel
    } // PCMain constructor
} // class PCMain2