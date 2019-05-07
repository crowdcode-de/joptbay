package io.crowdcode.jopt.joptbay.effects;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MemoryLeak {

    private volatile boolean stopLeak;

    private volatile Thread workerThread;

    private volatile Set<HugeWrapper.Node> nodes = new CopyOnWriteArraySet<>();

    public static void main(String[] args) throws InterruptedException {
        MemoryLeak ml = new MemoryLeak();

        ml.startMemoryLeaking();

        ml.workerThread.join();
    }

    public void startMemoryLeaking() {
        stopLeak = false;

        workerThread = new Thread(() -> {
            try {
                while (!stopLeak) {
                    TimeUnit.MILLISECONDS.sleep(100);
                    HugeWrapper.Node n = new HugeWrapper().buildNode();
                    nodes.add(n);
                    System.out.println("number of nodes is " + nodes.size());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        workerThread.start();
    }

    public void stopMemoryLeaking() {
        stopLeak = true;
        workerThread = null;
    }

    private static class HugeWrapper {

        private byte[] x = new byte[512 * 1024];

        public Node buildNode() {
            return new Node();
        }

        public class Node {
            private String name;

            public byte[] retrieve() {
                return HugeWrapper.this.x;
            }
        }
    }


}
