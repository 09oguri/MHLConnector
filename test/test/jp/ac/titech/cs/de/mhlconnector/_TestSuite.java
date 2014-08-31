package test.jp.ac.titech.cs.de.mhlconnector;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CommandTest.class, InteractiveSocketTest.class,
		InteractiveStreamTest.class, MHLConnectorTest.class,
		MHLLoggerTest.class, ResponseTest.class, })
public class _TestSuite {

}
