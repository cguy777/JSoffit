import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import fibrous.profiling.Profiler;
import fibrous.soffit.SoffitException;
import fibrous.soffit.SoffitObject;
import fibrous.soffit.SoffitUtil;

/* NUMBERS BELOW ARE RDTSCP CLICKS
 * 
 * Starting at:
 * read:  20774988
 * write: 3949995
 * 
 * Modified input to include escape sequences:
 * InputHash:  vmuyZ/Iiy8jAt68PBN1Fug==
 * OutputHash: vmuyZ/Iiy8jAt68PBN1Fug==
 * 
 * Now at:
 * read:  3012976
 * write: 1573216
 */

public class SoffitTest {
	public static void main(String[]args) throws SoffitException, IOException, InterruptedException, NoSuchAlgorithmException {
		
		Profiler.Init(false);
		
		var fis = new FileInputStream("InputSoffit.soffit");
		var ais = new ArrayInputStream(fis);
		fis.close();
		SoffitObject s_obj = null;
		
		MessageDigest digest = MessageDigest.getInstance("MD5");
		
		Thread.sleep(250);
		
		long bestRead = Long.MAX_VALUE;
		for(int i = 0; i < 3000; i++) {
			ais.reset();
			
			long start = Profiler.RDTSC();
			s_obj = SoffitUtil.ReadStream(ais);
			long end = Profiler.RDTSC();
			
			long total = end - start;
			if(total < bestRead)
				bestRead = total;
		}
		
		
		var fos = new FileOutputStream("OutputSoffit.soffit");
		var aos = new ArrayOutputStream(1024 * 1024);
		
		Thread.sleep(250);
		
		long bestWrite = Long.MAX_VALUE;
		for(int i = 0; i < 3000; i++) {
			aos.reset();
			
			long start = Profiler.RDTSC();
			SoffitUtil.WriteStream(s_obj, aos);
			long end = Profiler.RDTSC();
			
			long total = end - start;
			if(total < bestWrite)
				bestWrite = total;
		}
		
		aos.pipeToOutputStream(fos);
		fos.close();
	
		System.out.println("read:  " + bestRead);
		System.out.println("write: " + bestWrite);
		//System.out.println("write: " + writeProfiler.getTotalCounts());
		
		
		//We re-read the output to eliminate any issues with files changing due to comments, whitespace, CRLF, etc from the original read.
		//This also allows us to generally verify parsing is working correctly.
		var verifyIS = new FileInputStream("OutputSoffit.soffit");
		var bis = new BufferedInputStream(verifyIS);
		
		byte[] inputHash = digest.digest(bis.readAllBytes());
		bis.close();
		byte[] outputHash = digest.digest(aos.getWrittenBytes());
		
		String inputHashString = Base64.getEncoder().encodeToString(inputHash);
		String outputHashString = Base64.getEncoder().encodeToString(outputHash);
		
		System.out.println("InputHash:  " + inputHashString);
		System.out.println("OutputHash: " + outputHashString);
	}
}
