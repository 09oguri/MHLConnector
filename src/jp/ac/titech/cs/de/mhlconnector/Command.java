package jp.ac.titech.cs.de.mhlconnector;

public class Command {
	// スタート状態とPCのMAC要求コマンド
	public static final byte[] REQUIRE_MAC = { (byte) 0x02, (byte) 0x01,
			(byte) 0x5b, (byte) 0x00, (byte) 0x00 };
	// 記録間隔（高速側）の設定を取得
	public static final byte[] SAMP = { (byte) 0x02, (byte) 0x20, (byte) 0x01,
			(byte) 0x00, (byte) 0x00 };
	// 記録間隔（高速側）を10msに設定
	public static final byte[] SAMP_10ms = { (byte) 0x02, (byte) 0x20,
			(byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00 };
	// 記録間隔（高速側）を50msに設定
	public static final byte[] SAMP_50ms = { (byte) 0x02, (byte) 0x20,
			(byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x02 };
	// 記録間隔（高速側）を100msに設定
	public static final byte[] SAMP_100ms = { (byte) 0x02, (byte) 0x20,
			(byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x03 };
	// スタートコマンド(後ろ6バイトはPCのMACアドレス)
	public static final byte[] START = { (byte) 0x02, (byte) 0x01, (byte) 0x50,
			(byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x1a, (byte) 0x4d,
			(byte) 0x5b, (byte) 0x15, (byte) 0x3c };
	// メモリ内先頭番号、データ数要求コマンド1st（高速）
	public static final byte[] REQUIRE_MEMORY = { (byte) 0x02, (byte) 0x01,
			(byte) 0x53, (byte) 0x00, (byte) 0x00 };
	// 測定状態要求コマンド
	public static final byte[] REQUIRE_STATE = { (byte) 0x02, (byte) 0x01,
			(byte) 0x57, (byte) 0x00, (byte) 0x00 };
	// アプリシステムトリガコマンド
	public static final byte[] SYSTRIGGER = { (byte) 0x02, (byte) 0x01,
			(byte) 0x58, (byte) 0x00, (byte) 0x09, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00 };
	// ストップコマンド
	public static final byte[] STOP = { (byte) 0x02, (byte) 0x01, (byte) 0x51,
			(byte) 0x00, (byte) 0x00 };

	// データ要求コマンド1st（高速）
	public static byte[] REQUIRE_DATA = { (byte) 0x02, (byte) 0x01,
			(byte) 0x55, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 };

	// public static final boolean isStartCommand(byte[] receive) {
	// return compareCommand(receive, START, 3);
	// }
	//
	// public static final boolean isStopCommand(byte[] receive) {
	// return compareCommand(receive, STOP, 3);
	// }
	//
	// public static final boolean isStateCommand(byte[] receive) {
	// return compareCommand(receive, REQUIRE_STATE, 3);
	// }
	//
	// public static final boolean isSysTriggerCommand(byte[] receive) {
	// return compareCommand(receive, SYSTRIGGER, 3);
	// }
	//
	// public static final boolean isDataCommand(byte[] receive) {
	// return compareCommand(receive, REQUIRE_DATA, 3);
	// }
	//
	// private static final boolean compareCommand(byte[] rec, byte[] cmd,
	// int compareLength) {
	// if (compareLength < 1)
	// return false;
	//
	// for (int i = 0; i < compareLength; i++) {
	// if (rec[i] != cmd[i])
	// return false;
	// }
	//
	// return true;
	// }

	// データ要求コマンド1stのサンプリング番号を1つ増やす
	public static void incRequireDataCommand() {
		// サンプリング番号の開始位置
		int sampNumStart = 5;
		// サンプリング番号の終端位置
		int sampNumEnd = 12;
		for (int i = sampNumEnd; i > sampNumStart; i--) {
			if (REQUIRE_DATA[i] == 0xffffffff) {
				REQUIRE_DATA[i] = 0x00000000;
				REQUIRE_DATA[i - 1]++;
				if (REQUIRE_DATA[i - 1] != 0xffffffff) {
					break;
				}
			} else if (REQUIRE_DATA[sampNumStart] == 0xffffffff) {
				return;
			} else {
				REQUIRE_DATA[sampNumEnd]++;
				break;
			}
		}
	}

	// 1回のデータ要求コマンドで取得するデータ数を指定する
	// 現在は1-65534まで
	public static final void setRequireNumOfData(int numOfData) {
		if (numOfData == 0) {
			REQUIRE_DATA[19] = (byte) 0x00;
			REQUIRE_DATA[20] = (byte) 0x01;
		} else {
			REQUIRE_DATA[19] = (byte) (numOfData / 0x100);
			REQUIRE_DATA[20] = (byte) (numOfData % 0x100);
		}
	}
}
