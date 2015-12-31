package com.tangdi.risk.common;

import java.util.Comparator;

import com.tangdi.risk.rule.SerialRecord;

/**
 * 按时间排序
 * @author gengzhe
 *
 * @param <T>
 */
public class Mycomparator<T> implements Comparator<T> {
	
	@Override
	public int compare(Object o1, Object o2) {
		 
		SerialRecord u1 = (SerialRecord) o1;
		SerialRecord u2 = (SerialRecord) o2;
		//交易登记时间排序
		if (Long.valueOf(u1.getREGDT_TIME()) > Long.valueOf(u2.getREGDT_TIME()))
			return 1;
		else
			return 0;
	}
	
}
