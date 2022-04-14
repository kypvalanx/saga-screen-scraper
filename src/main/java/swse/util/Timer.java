package swse.util;

public class Timer {

    private final long start;

    public Timer(){
        start = System.currentTimeMillis();
    }

    public long end() {
        return System.currentTimeMillis() - start;
    }
}
