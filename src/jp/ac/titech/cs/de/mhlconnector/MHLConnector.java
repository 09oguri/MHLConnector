package jp.ac.titech.cs.de.mhlconnector;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MHLConnector {
	private final InteractiveSocket interactiveSocket;
	private final InteractiveStream interactiveStream;
	private final MHLLogger mhlLogger;
	private boolean isStopped;

	public MHLConnector(String configFilePath, String logFilePath)
			throws UnknownHostException, IOException {
		this.interactiveSocket = new InteractiveSocket(configFilePath);
		this.interactiveStream = this.interactiveSocket.getInteractiveStream();
		this.mhlLogger = new MHLLogger(logFilePath);
		this.isStopped = false;
	}

	private ArrayList<Double> calcPower() throws IOException {
		Response res = getVolts();

		final int MAX_CH = interactiveSocket.getMaxChannel();
		final int MAX_UNIT = interactiveSocket.getMaxUnit();
		ArrayList<Double> powerList = new ArrayList<Double>();

		for (int unit = 1; unit < MAX_UNIT + 1; unit++) {
			for (int ch = 1; ch < MAX_CH + 1; ch += 2) {
				double volt5 = res.getVolt(unit, ch);
				double volt12 = res.getVolt(unit, ch + 1);
				double power = Math.abs(volt5 * 50 + volt12 * 120);
				powerList.add(power);
			}
		}

		return powerList;
	}

	// MemoryHiLoggerから電圧データを受け取る
	private Response getVolts() throws IOException {
		byte[] receive = interactiveStream.receiveCommand();
		Command.incRequireDataCommand();

		return new Response(receive);
	}

	public void start() throws UnknownHostException, IOException {
		long sumNumOfData = 0L;
		long numOfData = interactiveSocket.getTakeInterval()
				/ interactiveSocket.getMeasurementInterval();

		startMeasurement();

		while (!isStopped) {
			long before = System.currentTimeMillis();

			interactiveStream.sendCommand(Command.REQUIRE_DATA);
			byte[] receive = interactiveStream.receiveCommand();
			Response response = new Response(receive);

			for (int i = 0; i < numOfData; i++) {
				ArrayList<Double> powerList = calcPower();
				mhlLogger.printlnPower(before, powerList);
			}
			sumNumOfData += numOfData;

			long after = System.currentTimeMillis();

			// 遅延解消
			try {
				// メモリ内データがなくなるのを防ぐために1秒は必ず遅れる
				long delay = interactiveSocket.getDelayTime();
				if (response.getNumOfData() < sumNumOfData + numOfData) {
					Thread.sleep(delay);
				} else {
					delay = delay - (after - before);
					if (delay > 0) {
						Thread.sleep(delay);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		interactiveSocket.close();
	}

	private void startMeasurement() throws IOException {
		// データの測定間隔を設定
		long measurementInterval = interactiveSocket.getMeasurementInterval();
		if (measurementInterval == 10L) {
			interactiveStream.sendCommand(Command.SAMP_10ms);
		} else if (measurementInterval == 50L) {
			interactiveStream.sendCommand(Command.SAMP_50ms);
		} else {
			interactiveStream.sendCommand(Command.SAMP_100ms);
		}
		interactiveStream.receiveCommand();

		// 測定開始
		interactiveStream.sendCommand(Command.START);
		interactiveStream.receiveCommand();

		// メモリハイロガーの状態が変化するのを待つ
		waitStateChange(State.AVAILABLE);

		// システムトリガー
		interactiveStream.sendCommand(Command.SYSTRIGGER);
		interactiveStream.receiveCommand();
		waitStateChange(State.SYSTRIGGER);

		// データ要求
		interactiveStream.sendCommand(Command.REQUIRE_DATA);
		interactiveStream.receiveCommand();

		// 1回のtakeで取得するデータ数を設定
		long takeInterval = interactiveSocket.getTakeInterval();
		int numOfData = (int) (takeInterval / measurementInterval);
		Command.setRequireNumOfData(numOfData);
	}

	public void stop() {
		isStopped = true;
	}

	// メモリハイロガーの状態が引数で与えられた状態に遷移するまで待つ
	private void waitStateChange(byte expectedState) throws IOException {
		byte state = (byte) 0xff;
		while (state != expectedState) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(0);
			}

			interactiveStream.sendCommand(Command.REQUIRE_STATE);
			byte[] receive = interactiveStream.receiveCommand();

			Response response = new Response(receive);
			state = response.getState();
		}
	}

}
