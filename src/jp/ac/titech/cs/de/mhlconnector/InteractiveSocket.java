package jp.ac.titech.cs.de.mhlconnector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

public class InteractiveSocket {
	private Socket interactiveSocket;
	private InteractiveStream interactiveStream;

	private final String hostname;
	private final int port;
	private final long measurementInterval;
	private final long delayTime;
	private final int maxChannel;
	private final int maxUnit;
	private final long takeInterval;

	public InteractiveSocket(String configFilePath)
			throws UnknownHostException, IOException {

		Properties config = new Properties();
		config.load(new FileInputStream(configFilePath));

		this.hostname = config.getProperty("hilogger.info.hostname");
		this.port = Integer.parseInt(config.getProperty("hilogger.info.port"));
		this.measurementInterval = Long.parseLong(config
				.getProperty("hilogger.info.measurementInterval"));
		this.delayTime = Long.parseLong(config
				.getProperty("hilogger.info.delayTime"));
		this.maxChannel = Integer.parseInt(config
				.getProperty("hilogger.info.maxChannel"));
		this.maxUnit = Integer.parseInt(config
				.getProperty("hilogger.info.maxUnit"));
		this.takeInterval = Long.parseLong(config
				.getProperty("hilogger.info.takeInterval"));

		this.interactiveSocket = new Socket();
		int timeout = (int) takeInterval + 1000;
		this.interactiveSocket.setSoTimeout(timeout);
		this.interactiveSocket.connect(new InetSocketAddress(hostname, port),
				timeout);

		OutputStream out = this.interactiveSocket.getOutputStream();
		InputStream in = this.interactiveSocket.getInputStream();

		// ソケットオープン時の応答処理
		// MemoryHiLoggerからの応答を受け取る
		// 受け取ったデータ自体は特に使わない
		while (in.available() == 0)
			;
		byte[] unused = new byte[in.available()];
		in.read(unused);
		unused = null;

		interactiveStream = new InteractiveStream(in, out);
	}

	public long getDelayTime() {
		return delayTime;
	}

	public int getMaxChannel() {
		return maxChannel;
	}

	public int getMaxUnit() {
		return maxUnit;
	}

	public long getMeasurementInterval() {
		return measurementInterval;
	}

	public long getTakeInterval() {
		return takeInterval;
	}

	public void close() throws IOException {
		if (interactiveSocket != null) {
			interactiveSocket.close();
		}
	}

	public InteractiveStream getInteractiveStream() {
		return interactiveStream;
	}

}
