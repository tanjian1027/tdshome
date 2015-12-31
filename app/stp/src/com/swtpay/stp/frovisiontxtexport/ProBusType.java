package com.swtpay.stp.frovisiontxtexport;

public enum ProBusType {
	 
	 PBT_01("01", "本期反映，本期入金"),
	 PBT_02("02", "本期反映，未入金"),
	 PBT_03("03", "前期反映，本期入金"),
	 PBT_04("04", "系统未反映，本期入金"),
	 PBT_05("05", "本期反映，前期入金"),
	 PBT_06("06", "本期反映，本期入金-押金"),
	 PBT_07("07", "本期反映，未入金-押金"),
	 PBT_08("08", "前期反映，本期入金-押金"),
	 PBT_09("09", "本期反映，前期入金-押金"),
	 PBT_11("11", "前期入金系统未反映，本期退回"),
	 PBT_31("31", "本期反映，本期出金"),
	 PBT_32("32", "本期反映，未出金"),
	 PBT_33("33", "前期反映，本期出金"),
	 PBT_34("34", "系统未反映，本期出金"),
	 PBT_35("35", "本期反映，前期出金"),
	 PBT_37("37", "前期出金系统未反映，本期返回款项"),
	 PBT_51("51", "内部账户转账");
	 private final String code;
	 private final String msg;

	 ProBusType(String code, String msg) {
		this.code = code;
		this.msg = msg;
	 }
	 public String getCode()
     {
        return code;
     }

     public String getMsg()
     {
        return msg;
     }
     public static boolean isSignType(String code)
     {
         for (ProBusType s : ProBusType.values())
         {
             if (s.getCode().equals(code))
             {
                 return true;
             }
         }
         return false;
     }
}
