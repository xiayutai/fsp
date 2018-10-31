package com.ht.fsp;


import com.ht.fsp.config.BillBatchConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class BillScheduler {
	@Autowired
	private BillBatchConfig billBatchConfig;

	private static final Logger log = LoggerFactory.getLogger(BillScheduler.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	//每隔10秒执行一次
//	@Scheduled(initialDelay=10000, fixedRate = 10000)
	public void fixedBillBatch() {
		log.info("job begin {}", dateFormat.format(new Date()));
		billBatchConfig.run();
		log.info("job end {}", dateFormat.format(new Date()));
	}

	//每天早上10:15分钟执行
//	@Scheduled(cron="0 15 10 ? * *")
	@Scheduled(cron="0 45 16 ? * *")
	public void fixedTimePerDayBillBatch() {
		log.info("job begin {}", dateFormat.format(new Date()));
		billBatchConfig.run();
		log.info("job end {}", dateFormat.format(new Date()));
	}
}
