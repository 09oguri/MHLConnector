package jp.ac.titech.cs.de.mhlconnector;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MHLLogger {
	private final String logFilePath;
	private final PrintWriter writer;

	public MHLLogger(String logPath) throws IOException {
		this.logFilePath = logPath;
		this.writer = new PrintWriter(new BufferedWriter(new FileWriter(
				this.logFilePath, true)));
	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public void log(long time, ArrayList<Double> powerList) {
		writer.print(time + "¥t");
		for (double power : powerList) {
			writer.print(power + "\t");
		}
		writer.println("");
	}

	public void printlnPower(long time, ArrayList<Double> powerList) {
		System.out.print(time + "¥t");
		for (double power : powerList) {
			System.out.print(power + "\t");
		}
		System.out.println("");
	}
}
