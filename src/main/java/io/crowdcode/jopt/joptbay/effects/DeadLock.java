package io.crowdcode.jopt.joptbay.effects;

/**
 * @author Ingo Dueppe (CROWDCODE)
 */
public class DeadLock {

	private Object monitorA = new Object();
	private Object monitorB = new Object();

	private volatile boolean stepA = false;
	private volatile boolean stepB = false;

	public static void main(String[] args) {
		new DeadLock().buildDeadlock();
	}

	public void aThenB() {
		synchronized (monitorA) {
			System.out.println("A got a");
			stepA = true;
			while (!stepB) {
				Thread.yield();
			}
			System.out.println("A trying b");
			synchronized (monitorB) {
				System.out.println("Got It!");
			}
		}
	}

	public void bThenA() {
		synchronized (monitorB) {
			System.out.println("B got b");
			stepB = true;
			System.out.println("B trying a");
			synchronized (monitorA) {
				System.out.println("Got It!");
			}
		}
	}

	public DeadLock buildDeadlock() {
		ThreadGroup group = new ThreadGroup("Deadlock-Simulation");
		Thread threadA = new Thread(group, this::aThenB, "AthenB");
		Thread threadB = new Thread(group, this::bThenA, "BthenA");

		threadA.start();
		Thread.yield();
		while (!stepA) {
			System.out.println("Main waiting for stepA");
			Thread.yield();
		}
		System.out.println("Main starting B");
		threadB.start();
		while (!stepB) {

			Thread.yield();
		}
		Thread.yield();
		return this;
	}


}
