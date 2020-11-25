package primitive.thread;

/**
 * Interrupt threads -> for example making I/O operations and the user stop the operation via UI ...
 * we have to terminate the thread
 * <p>
 * boolean Thread.isInterrupted() -> check whether is it interrupted boolean interrupted() -> checks
 * + interrupt the thread !!!
 * <p>
 * Terminate a thread -> volatile boolean flags !!!
 * <p>
 * Thread states:
 * <p>
 * 1.) RUNNABLE: if we create a new thread + call start() method The run() method can be called...
 * new MyThread().start();
 * <p>
 * 2.) BLOCKED: if it is waiting for an object's monitor safety.lock - waiting to enter a
 * synchronized block, like synchronized(new Object()) { } - after wait(): waiting for the monitor
 * safety.lock to be free
 * <p>
 * 3.) WAITING: when we call wait() on a thread ... it is going to loose the monitor safety.lock and
 * wait for notify() notifyAll()
 * <p>
 * 4.) TERMINATED: when the run() method is over ... We can check it with isAlive() method
 */

class TerminalThreadWorker extends Thread {

  // use volatile variables to terminate thread
  private volatile Thread thread = this;

  public void finish() {
    this.thread = null;
  }

  @Override
  public void run() {
    while (this.thread == this) {
      System.out.println("Thread is running...");
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}

public class TerminateThread {

  public static void main(String[] args) {

    TerminalThreadWorker worker = new Worker();
    worker.start();

    try {
      Thread.sleep(4000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    worker.finish();
  }
}
