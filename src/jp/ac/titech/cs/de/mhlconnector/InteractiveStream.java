package jp.ac.titech.cs.de.mhlconnector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InteractiveStream {
	private final InputStream in;
	private final OutputStream out;

	public InteractiveStream(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}

	public void close() throws IOException {
		if (in != null) {
			in.close();
		}
		if (out != null) {
			out.close();
		}
	}

	public byte[] receiveCommand() throws IOException {
		while (in.available() == 0)
			;
		byte[] receive = new byte[in.available()];
		in.read(receive);
		return receive;
	}

	public void sendCommand(byte[] command) throws IOException {
		out.write(command);
		out.flush();
	}
}
