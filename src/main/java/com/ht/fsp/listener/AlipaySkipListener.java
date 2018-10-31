package com.ht.fsp.listener;

import com.ht.fsp.bean.AlipayTranDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Service;


@Service
public class AlipaySkipListener implements SkipListener<AlipayTranDO, AlipayTranDO> {
	private static final Logger log = LoggerFactory.getLogger(AlipaySkipListener.class);

	@Override
	public void onSkipInProcess(AlipayTranDO alipayTranDO, Throwable throwable) {
		log.info("AlipayTran was skipped in process: "+alipayTranDO);
	}
	@Override
	public void onSkipInRead(Throwable arg0) {
	}
	@Override
	public void onSkipInWrite(AlipayTranDO alipayTranDO, Throwable throwable) {
		log.info("AlipayTran was skipped in process: "+alipayTranDO);
	}

}