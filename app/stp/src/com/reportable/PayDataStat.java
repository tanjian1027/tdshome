package com.reportable;

import net.tangdi.rpt.TdExpBasicFunctions;

import tangdi.annotations.Code;
import tangdi.atc.Atc;
import tangdi.engine.DB;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.web.TdWebConstants;

@DB
public class PayDataStat {
    /**
     * 支付机构互联网支付业务指标统计表
     * 
     * @throws Exception
     */
    @Code("RPT001")
    public void payDataStat() throws Exception {
        String yyyymm = Etf.getChildValue("txn_Date");
        if(yyyymm==null){
            yyyymm = TdExpBasicFunctions.GETDATETIME("yyyyMM");
            Etf.setChildValue("txn_Date", yyyymm);
        }
        Etf.setChildValue("yyyy", yyyymm.substring(0, 4));
        Etf.setChildValue("mm", yyyymm.substring(4));
        // 网银支付
        String sql = "select to_char(nvl(sum(1),0)/10000,'FM999990.0999') wyzf_cnt,to_char(nvl(sum(t.txamt),0)/1000000,'FM9999,999,999,990.099999') wyzf_amt from STPPAYINF t where t.actdat like '${txn_Date}%' and t.paytype='01' and (t.ordstatus='01' or t.ordstatus='06') and t.prdordtype!='1'";
        Log.info("sql=%s", sql);
        int result = Atc.ReadRecord(sql);
        ////支付方式
        // 快捷支付
        sql = "select to_char(nvl(sum(1),0)/10000,'FM999990.0999') kjzf_cnt,to_char(nvl(sum(t.txamt),0)/1000000,'FM9999,999,999,990.099999') kjzf_amt from STPPAYINF t where t.actdat like '${txn_Date}%' and t.paytype='03' and (t.ordstatus='01' or t.ordstatus='06') and t.prdordtype!='1'";
        result = Atc.ReadRecord(sql);
        // 快捷支付(关联银行卡数量)
        //sql = "select to_char(nvl(count(distinct(t.credno)),0),'FM999990.0999') kjzf_card_cnt from STPPAYINF t where t.actdat like '${txn_Date}%' and t.paytype='03' and (t.ordstatus='01' or t.ordstatus='06') and t.prdordtype!='1'";
        //result = Atc.ReadRecord(sql);
        // 支付账户支付
        sql = "select to_char(nvl(sum(1),0)/10000,'FM999990.0999') zfzhzf_cnt,to_char(nvl(sum(t.txamt),0)/1000000,'FM9999,999,999,990.099999') zfzhzf_amt from STPPAYINF t where t.actdat like '${txn_Date}%' and t.paytype='00' and (t.ordstatus='01' or t.ordstatus='06')";
        result = Atc.ReadRecord(sql);
        
        ////支付账户充值
        // 银行账户充值
        sql = "select to_char(nvl(sum(1),0)/10000,'FM999990.0999') yhzhcz_cnt,to_char(nvl(sum(t.txamt),0)/1000000,'FM9999,999,999,990.099999') yhzhcz_amt from STPPAYINF t where t.actdat like '${txn_Date}%' and (t.paytype='01' or t.paytype='03') and (t.ordstatus='01' or t.ordstatus='06') and t.prdordtype='1'";
        result = Atc.ReadRecord(sql);
        // 预付卡充值
        sql = "select to_char(nvl(sum(1),0)/10000,'FM999990.0999') yfkcz_cnt,to_char(nvl(sum(t.txamt),0)/1000000,'FM9999,999,999,990.099999') yfkcz_amt from STPPAYINF t where t.actdat like '${txn_Date}%' and (t.paytype='12' or t.paytype='13') and (t.ordstatus='01' or t.ordstatus='06') and t.prdordtype='1'";
        result = Atc.ReadRecord(sql);
        // 支付账户充值：其他
        sql = "select to_char(nvl(sum(1),0)/10000,'FM999990.0999') qtcz_cnt,to_char(nvl(sum(t.txamt),0)/1000000,'FM9999,999,999,990.099999') qtcz_amt from STPPAYINF t where t.actdat like '${txn_Date}%' and (t.paytype='02' or t.paytype='04') and (t.ordstatus='01' or t.ordstatus='06') and t.prdordtype='1'";
        result = Atc.ReadRecord(sql);
        
        ////转账和回提
        // 银行账户转出
        Etf.setChildValue("yhzhzc_cnt", "0.0");
        Etf.setChildValue("yhzhzc_amt", "0.0");
        // 支付账户转出
        sql = "select to_char(nvl(sum(1),0)/10000,'FM999990.0999') zfzhzc_cnt,to_char(nvl(sum(t.txamt),0)/1000000,'FM9999,999,999,990.099999') zfzhzc_amt from STPTRAINF t where t.tratime like '${txn_Date}%' and  t.ordstatus='01'";
        result = Atc.ReadRecord(sql);
        // 回提
        sql = "select to_char(nvl(sum(1),0)/10000,'FM999990.0999') ht_cnt,to_char(nvl(sum(t.txamt),0)/1000000,'FM9999,999,999,990.099999') ht_amt from STPCASINF t where t.actdat like '${txn_Date}%' and (t.ordstatus='02' or t.ordstatus='05')";
        result = Atc.ReadRecord(sql);
        
        ////业务统计
        // 消费业务
        sql = "select to_char(nvl(sum(1),0)/10000,'FM999990.0999') xfyw_cnt,to_char(nvl(sum(t.txamt),0)/1000000,'FM9999,999,999,990.099999') xfyw_amt from STPPAYINF t where t.actdat like '${txn_Date}%' and (t.ordstatus='01' or t.ordstatus='06') and (t.prdordtype='0' or t.prdordtype='2')";
        result = Atc.ReadRecord(sql);
        // 金融理财业务
        Etf.setChildValue("jrlcyw_cnt", "0.0");
        Etf.setChildValue("jrlcyw_amt", "0.0");
        // 自助支付业务
        Etf.setChildValue("zzzfyw_cnt", "0.0");
        Etf.setChildValue("zzzfyw_amt", "0.0");
        // B2B业务
        Etf.setChildValue("b2byw_cnt", "0.0");
        Etf.setChildValue("b2byw_amt", "0.0");
        // 虚拟产品
        Etf.setChildValue("xncp_cnt", "0.0");
        Etf.setChildValue("xncp_amt", "0.0");
        // 其他
        Etf.setChildValue("qtxf_cnt", "0.0");
        Etf.setChildValue("qtxf_amt", "0.0");
        
        ////支付账户
        // 单位实名账户个数
        Etf.setChildValue("smzhdw_cnt", "0");
        // 个人实名账户个数
        sql = "select count(s.cust_id) smzhgr_cnt from stpusrinf s, STPCUSINF t where t.cust_type = '0' and s.usr_status = '2' and s.cust_id = t.cust_id and t.cust_reg_date like '${txn_Date}%'";
        result = Atc.ReadRecord(sql);
        // 单位非实名账户个数
        Etf.setChildValue("fsmzhdw_cnt", "0");
        // 个人非实名账户个数
        sql = "select count(s.cust_id) fsmzhgr_cnt from stpusrinf s, STPCUSINF t where t.cust_type = '0' and s.usr_status != '2' and s.cust_id = t.cust_id and t.cust_reg_date like '${txn_Date}%'";
        result = Atc.ReadRecord(sql);
        
        if (result == 0) {
            Etf.setChildValue("RspCod", "000000");
            Etf.setChildValue("RspMsg", "查询成功！");
        } else if (result == -1) {
            Etf.setChildValue("RspCod", "200001");
            Etf.setChildValue("RspMsg", "系统错误，请联系管理员！");
            Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME, "/ajaxrequest.jsp");
            return;
        } else {
            Etf.setChildValue("RspCod", "200005");
            Etf.setChildValue("RspMsg", "查询失败！");
        }

        Etf.setChildValue(TdWebConstants.WEB_FORWARD_NAME, "/WEB-INF/html/reportable/payDataStat.jsp");
    }

}
