package cillian.cexstockwatch;

/**
 * Created by Cillian on 17/08/2016.
 */
public class LoginCounter implements Runnable {

    public boolean timeElapsed = false;

    public LoginCounter() {
    }

    public void run() {
        try {
            Thread.sleep(5000);                 //1000 milliseconds is one second.
            timeElapsed = true;
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
