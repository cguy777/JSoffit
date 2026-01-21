package fibrous.profiling;

public class Profiler {
    public static Profiler[] profilers;
    public static int nextSlot = 0;
    public static boolean enabled = false;
    public static boolean completed = false;

    public final static int MAX_PROFILERS = 4096;

    public static long wholeProgramTSCStart;
    public static long wholeProgramTSCStop;

    long tscStart;
    long tscStop;

    private Profiler() {
        if(!enabled)
            return;

        if(nextSlot == MAX_PROFILERS)
            throw new RuntimeException("Exceeded maximum number of profilers");

        profilers[nextSlot] = this;
        nextSlot++;
        tscStart = TimingUtils.RDTSC();
    }

    public void stop() {
    	if(enabled)
    		tscStop = TimingUtils.RDTSC();
    }

    public long getTotalCounts() {
        return tscStop - tscStart;
    }

    public static void Init(boolean warmUp) {
    	enabled = true;
        TimingUtils.Init();
        if(warmUp)
            TimingUtils.WarmUp();
        profilers = new Profiler[4096];
    }
    
    public static void reset() {
    	nextSlot = 0;
    }

    public static void startWholeProgramTimer() {
    	if(enabled)
    		wholeProgramTSCStart = TimingUtils.RDTSC();
    }

    public static void stopWholeProgramTimer() {
    	if(enabled) {
	        wholeProgramTSCStop = TimingUtils.RDTSC();
	        completed = true;
    	}
    }

    public static long getWholeProgramCounts() {
        return wholeProgramTSCStop - wholeProgramTSCStart;
    }
    
    public static long RDTSC() {
    	return TimingUtils.RDTSC();
    }

    public static double getTimeFraction(Profiler p) {
        if(!completed)
            throw new RuntimeException("Profiler.stopWholeProgramTimer() was never called");

        return (double) p.getTotalCounts() / (double) getWholeProgramCounts();
    }

    public static Profiler startProfiler() {
        Profiler profiler = new Profiler();
        return profiler;
    }
}