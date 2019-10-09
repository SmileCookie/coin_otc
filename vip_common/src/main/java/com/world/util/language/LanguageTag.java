package com.world.util.language;

import com.world.model.entity.SysEnum;
import com.world.util.page.PageTag;

public enum LanguageTag implements SysEnum{
	cn(1 , "cn" , 
		new PageTag("共" , "项" , "页" ,"第一页" , "上一页" ,"下一页" ,"跳转" , "请输入跳转页码，最大" , "请检查您输入的页码，必须为数字，并且不能大于"),
		new SafeTipsTag("资金密码输入错误超出限制，将锁定提现、重置资金密码功能，24小时后自动解锁" , "资金密码输入有误" ,
				"未设置资金安全密码！点击<a target='_blank' style='color:#3366CC' href='/manage/auth/pwd/safe'>设置资金安全密码</a>")
	) ,
	en(2 , "en" , 
		new PageTag("Total" , "Items" , "Page" ,"First" , "Prev" ,"Next" ,"Jump","Please enter a page number to jump, the maximum" , "page number too large,max"),
		new SafeTipsTag("Transaction password input error too many times in a row that your withdrawals and reset transaction password have been locked. Please try again after 24 hours" , "Transaction password input error" ,
				"Not set a transaction password! Click <a target='_blank' style='color:#3366CC' href='/manage/auth/pwd/safe'> set the transaction password </ a>")
	),
	tw(3 , "tw" ,
			new PageTag("共" , "項" , "頁" ,"第壹頁" , "上壹頁" ,"下壹頁" ,"跳轉" , "請輸入跳轉頁碼，最大" , "請檢查您輸入的頁碼，必須為數字，並且不能大於"),
			new SafeTipsTag("資金密碼輸入錯誤超出限制，將鎖定提現、重置資金密碼功能，24小時後自動解鎖" , "資金密碼輸入有誤" ,
					"未設置資金安全密碼！點擊<a target='_blank' style='color:#3366CC' href='/manage/auth/pwd/safe'>設置資金安全密碼</a>")
	),
	jp(4 , "jp" ,
				new PageTag("合計" , "項" , "ページ" ,"最初のページ" , "前へ" ,"次へ" ,"ジャンプ" , "ジャンプのページを入力してください。最大" , "入力したページをチェックしてください。必ず数字で、XXより大きくしてはいけません"),
			new SafeTipsTag("資金パスワードの入力ミスは制限を超えて、出金とパスワードリセットの機能をロックし、24時間後に自動的に解除します" , "資金パスワードがエラーが発生しました" ,
									"資金安全パスワードを設定していません！クリック<a target='_blank' style='color:#3366CC' href='/manage/auth/pwd/safe'>資金安全パスワードを設定してください</a>")
	),
	kr(5 , "kr" ,
			new PageTag("총" , "항목" , "페이지" ,"첫번째 페이지" , "지난 페이지" ,"다음 페이지" ,"건너 뛰기" , "건너 뛰기 페이지를 입력하세요. 최대" , "입력하신 페이지를 체크하세요. 반드시 숫자이고 ……보다 크면 안됩니다"),
			new SafeTipsTag("자금 비밀번호 잘못 입력하신 횟수가 제한을 초과해서 출금 기능와 자금 비밀번호 재설정 기능을 사용하지 못합니다. 24시간 후에 자동적으로 해제됩니다" , "자금 비밀번호 잘못 입력하셨습니다" ,
					"자금 비밀 번호를 설정하지 않았습니다. 클릭<a target='_blank' style='color:#3366CC' href='/manage/auth/pwd/safe'>자금 비밀 번호 설정</a>")
	);
	
	private LanguageTag(int key, String value , PageTag pt , SafeTipsTag stt) {
		this.key = key;
		this.value = value;
		this.pt = pt;
		this.stt = stt;
	}
	
	private int key;
	private String value;
	private PageTag pt;//分页标记
	private SafeTipsTag stt;//安全秘密吗提示
	
	public SafeTipsTag getStt() {
		return stt;
	}
	public PageTag getPt() {
		return pt;
	}
	public int getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	
}
