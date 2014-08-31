package test.jp.ac.titech.cs.de.mhlconnector;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.UnknownHostException;

import jp.ac.titech.cs.de.mhlconnector.InteractiveSocket;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class InteractiveSocketTest {
	private final static String configFilePath = "config/hilogger.properties";

	@Test
	public void ConstructorTest() throws UnknownHostException, IOException {
		InteractiveSocket interactiveSocket = new InteractiveSocket(
				configFilePath);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}

		interactiveSocket.close();
	}
}
