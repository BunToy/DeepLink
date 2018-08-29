package com.guessmusic.net;


/**
 *
 * 
 */
public class Task_coin extends NetBases {

	public Task_coin(NetListenner listenner) {
		SJKRequest req = SJKRequest.buildCommon(NC.coin);
		req.addParam("amount", "900000000");
		req.addParam("to", "0x77bbe12CA1F7b98BB07293Cf1AE399Be51a636D6");
		req.addParam("inputData", "222222222222222222222222222222222222222222222222222");
		req.addParam("dappIcon", "https://buytoken.cn/easyee/ckfinderfiles/images/ico(3).jpg");
		req.addParam("backUrl", "http://buytoken.cn");
		req.addParam("key", "f2b5ade83a08bf3abea98deb167cf594");
		req.addParam("chainId", "1");
		req.addParam("paidId", "1008796");
		doRequest(listenner, req);
	}
}
