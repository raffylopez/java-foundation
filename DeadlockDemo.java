package ownclasses;

public class DeadlockDemo {
	/* Set to 1 to force deadlocks all the time! */
	public static int FORCE_TIMINGS = 0;
	
	public static void main(String[] args) throws Exception {
		System.out.println("Begin deadlock demonstration...");
		BankAccount ba = new BankAccount();
		A a = new A(ba);
		B b = new B(ba);
		
		a.cothread = b;
		b.cothread = a;
		
		a.start();
		b.start();
		
		/* Wait for threads to finish before displaying 'Done' */
		a.join();
		b.join();
		System.out.println("Done...");
	}
}

class BankAccount {
	float balance;
	
	/* synchronized prevents multithreading on this method */
	public synchronized void deposit(String label, float amt) {
		System.out.println(label + " performs deposit of " + amt + "... (force 5 secs delay)");
		if (DeadlockDemo.FORCE_TIMINGS == 1) 
			try { Thread.sleep(5000); } catch (Exception e) { }
		
		balance = balance + amt;
		System.out.println(label + " done performing deposit...");
	}
}

class A extends Thread {
	B cothread;
	BankAccount account;
	public A(BankAccount account) {
		this.account = account;
	}
	public void run() {
		if (DeadlockDemo.FORCE_TIMINGS == 1) 
			try { Thread.sleep(1000); } catch (Exception e) { }
		
		System.out.println("A suspends B");
		cothread.suspend();
		System.out.println("A attempts to lock bankaccount (but B's already locked it, and A was supended by A)");
		account.deposit("A", 123);
		System.out.println("A resumes B");
		cothread.resume();
	}
}

class B extends Thread {
	A cothread;
	BankAccount account;
	public B(BankAccount account) {
		this.account = account;
	}
	public void run() {
		System.out.println("B locks bankaccount");
		account.deposit("B", 456);
		if (DeadlockDemo.FORCE_TIMINGS == 1)
			try { Thread.sleep(3000); } catch (Exception e) { }
	}
}

