package fibrous.profiling;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

public class TimingUtils {

    static MemorySegment rdtscSymbol;
    static MethodHandle rdtscHandle;

    public static void Init() {
        System.loadLibrary("timing_utils");
        Linker linker = Linker.nativeLinker();
        //Arena arena = Arena.ofConfined();
        //SymbolLookup timingUtilSymbolLookup = SymbolLookup.libraryLookup("timing_util", arena);
        SymbolLookup timingUtilSymbolLookup = SymbolLookup.loaderLookup();
        rdtscSymbol = timingUtilSymbolLookup.find("RDTSC").orElseThrow();
        rdtscHandle = linker.downcallHandle(rdtscSymbol, FunctionDescriptor.of(ValueLayout.JAVA_LONG));
    }

    /**
     * Optional call to help the JIT compiler see some calls and jump-start the analysis/compilation process.
     * This does seem to help reduce the return time on the first couple of calls from this class.
     */
    @SuppressWarnings("unused")
    public static void WarmUp() {
        //Hotspot warm up
        long l1;
        long l2;
        long l3 = 0;
        for(int i = 0; i < 1000; i++) {
            l1 = RDTSC();
            l2 = RDTSC();
            l3 += l1 - l2;
        }
    }

    /**
     * Calls __rdtscp and simply returns the tsc value
     */
    public static long RDTSC() {
    	try {
    		long rdtsc = (long) rdtscHandle.invoke();
    		return rdtsc;
    	} catch (Throwable e) {
    		throw new RuntimeException(e);
    	}
    }
}