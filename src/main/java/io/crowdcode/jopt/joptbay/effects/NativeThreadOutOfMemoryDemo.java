package io.crowdcode.jopt.joptbay.effects;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class NativeThreadOutOfMemoryDemo {

    private static AtomicLong summary = new AtomicLong();

    public static void main(String[] args) throws InterruptedException {

        ThreadGroup group = new ThreadGroup("thread-stack");

        for (int i = 1; i < 10_000; i++) {
//            TimeUnit.SECONDS.sleep(1);

            System.out.println("Add Thread " + i);
            new Thread(group, "Thread-" + i) {
                @Override
                public void run() {
                    long value = 0;
                    while (true) {
                        try {
                            recursiveSleep(1_000); // kill it with 1_000_000
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        value = increaseAmount();
                    }
                }
            }.start();
        }

    }

    public static void recursiveSleep(int deep) throws InterruptedException {
        long a = 1_000_000_000 + deep;
        if (deep > 0) {
            recursiveSleep(--deep);
        } else {
            TimeUnit.SECONDS.sleep(30);
        }
    }

    public static synchronized long increaseAmount()  {
        long value = summary.incrementAndGet();
        if (value % 1_000_000 == 0) {
            System.out.println(value);
        }
        return value;
    }

}
