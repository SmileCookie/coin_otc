package com.world.model.enums;

		import com.world.model.entity.SysEnum;

public enum CoinDownloadStatus implements SysEnum{
	//0提交   1失败  2成功  3取消  5已确认 7等待确认和打币中之间的中间状态
	COMMITED(0, "等待确认"),
	FAIL(1, "失败"),
	SUCCESS(2, "成功"),
	CANCEL(3, "已取消"),
	CONFIRMED(5, "打币中"),
	COMMITED_NEXT(7, "等待确认");

	private CoinDownloadStatus(int key, String value) {
		this.key = key;
		this.value = value;
	}

	private int key;
	private String value;

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
