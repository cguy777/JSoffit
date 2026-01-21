import java.io.IOException;
import java.io.OutputStream;

public class ArrayOutputStream extends OutputStream {
	byte[] buffer;
	int pos = 0;
	int mark = 0;
	
	public ArrayOutputStream(int bufferSize) {
		buffer = new byte[bufferSize];
	}

	@Override
	public void write(int b) throws IOException {
		buffer[pos] = (byte) b;
		pos++;
	}
	
	@Override
	public void write(byte[] b) {
		System.arraycopy(b, 0, buffer, pos, b.length);
		pos += b.length;
	}
	
	@Override
	public void write(byte[] b, int off, int len) {
		System.arraycopy(b, 0, buffer, pos, len);
		pos += len;
	}
	
	public void reset() {
		pos = 0;
		mark = 0;
	}
	
	public void resetMark() {
		mark = 0;
	}
	
	public void mark() {
		mark = pos;
	}
	
	public void goToMark() {
		pos = mark;
	}
	
	public void pipeToOutputStream(OutputStream os) throws IOException {
		os.write(buffer, 0, pos);
	}
	
	public byte[] getWrittenBytes() {
		byte[] copy = new byte[pos];
		System.arraycopy(buffer, 0, copy, 0, pos);
		return copy;
	}
}
