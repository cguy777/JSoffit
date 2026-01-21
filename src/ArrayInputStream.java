import java.io.IOException;
import java.io.InputStream;

public class ArrayInputStream extends InputStream {
	byte[] buffer;
	int pos = 0;

	public ArrayInputStream(InputStream is) throws IOException {
		buffer = is.readAllBytes();
	}
	
	@Override
	public int read() throws IOException {
		return pos < buffer.length ? (buffer[pos++] & 0xff) : -1;
	}
	
	public void reset() {
		pos = 0;
	}
}