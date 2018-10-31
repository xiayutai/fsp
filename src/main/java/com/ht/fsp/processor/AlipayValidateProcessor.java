package com.ht.fsp.processor;

import com.ht.fsp.bean.AlipayTranDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;


public class AlipayValidateProcessor implements ItemProcessor<AlipayTranDO, AlipayTranDO> {
	private static final Logger log = LoggerFactory.getLogger(AlipayValidateProcessor.class);

	@Override
	public AlipayTranDO process(AlipayTranDO alipayTranDO) throws Exception {
		if(Double.parseDouble(alipayTranDO.getAmount()) < 0){
			log.info("validate error: " + alipayTranDO.toString());
			throw new Exception();
		}else{
			return alipayTranDO;
		}
	}
}
