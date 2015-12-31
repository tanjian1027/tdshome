package com.tangdi.risk.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.tangdi.risk.rule.SerialRecord;

/**
 * 按卡bin排序
 * @author gengzhe
 *
 * @param <T>
 */
public class Mycomparator2<T> implements Comparator<T> {
	
	@Override
	public int compare(Object o1, Object o2) {
		 
		SerialRecord u1 = (SerialRecord) o1;
		SerialRecord u2 = (SerialRecord) o2;
		//交易登记时间排序
		if (Long.valueOf(u1.getBANK_DEP_NO()) > Long.valueOf(u2.getBANK_DEP_NO()) 
			&& Long.valueOf(u1.getBANK_CARD_LEN()) > Long.valueOf(u2.getBANK_CARD_LEN())
			&&Long.valueOf(u1.getBANK_SIGN_VAL()) > Long.valueOf(u2.getBANK_SIGN_VAL()))
			return 1;
		else
			return 0;
	}
}
