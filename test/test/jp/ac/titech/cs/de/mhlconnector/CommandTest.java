package test.jp.ac.titech.cs.de.mhlconnector;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import jp.ac.titech.cs.de.mhlconnector.Command;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CommandTest {
	@Test
	public void incRequireDataCommandTest() {
		byte[] expect = { (byte) 0x01, (byte) 0x00 };

		for (int i = 0; i < 0x100; i++) {
			Command.incRequireDataCommand();
		}
		byte[] actual = { Command.REQUIRE_DATA[11], Command.REQUIRE_DATA[12] };

		assertThat(actual, is(expect));
	}

	@Test
	public void setRequireNumOfDataTest() {
		byte[] expect = { (byte) 0x01, (byte) 0x00 };

		Command.setRequireNumOfData(256);
		byte[] actual = { Command.REQUIRE_DATA[19], Command.REQUIRE_DATA[20] };

		assertThat(actual, is(expect));
	}
}
