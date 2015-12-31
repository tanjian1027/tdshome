package com.tangdi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import net.sf.excelutils.ExcelException;
import net.sf.excelutils.ExcelUtils;
import org.dom4j.Element;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;

public class OrderReportPact
{
  @Named("ToReportP")
  public static int ToReportP(@Named("Root") String Root, @Named("FileDemo") String FileDemo, @Named("FilePath") String FilePath, @Named("FILETOPATH") String FILETOPATH, @Named("NameForUser") String NameForUser, @Named("WebPath") String WebPath)
  {
    List reportList = null;
    if (FileDemo.equals("orderdemo2"))
      reportList = iterlist1(Root);
    else if (FileDemo.equals("pactdemo"))
      reportList = setPact(Root);
    else {
      reportList = iterlist2(Root);
    }
    ExcelUtils.addValue("reportList", reportList);

    String tmpPath = OrderReportPact.class.getClassLoader().getResource("")
      .getPath();
    int i = tmpPath.indexOf("classes/");
    String webPath = tmpPath.substring(0, i);
    String config = webPath + "/" + FilePath + "/" + FileDemo + ".xls";
    Log.info("config", new Object[] { config });
    String fileName = NameForUser + System.currentTimeMillis() + ".xls";
    Log.info("----fileName=%s--------", new Object[] { webPath + "/" + FilePath + "/" + fileName + config });
    try {
      ExcelUtils.export(config, new FileOutputStream(webPath + "/" + FILETOPATH + "/" + fileName));
    }
    catch (FileNotFoundException e) {
      Log.info("----fileName=%s--", new Object[] { webPath + "/" + FilePath + "/" + fileName + "没找到" });
      return 1;
    }
    catch (ExcelException e) {
      Log.info("----fileName=%s--", new Object[] { webPath + "/" + FilePath + "/" + fileName + "导入异常" });
      return 2;
    }
    Msg.setTmp("FILENAME", fileName);
    return 0;
  }

  public static List iterlist1(String ROOT) {
    List list = Etf.childs(ROOT);
    Log.info("list的长度=%s", new Object[] { Integer.valueOf(list.size()) });
    List reportList = new ArrayList();
    for (int i = 0; i < list.size(); ++i) {
      Element em = (Element)list.get(i);
      Orders order = new Orders();
      order.setPrdOrdNo((em.element("PRDORDNO") == null) ? "无记录" : em.element("PRDORDNO").getText());
      order.setOrdAmt((em.element("ORDAMT") == null) ? "无记录" : em.element("ORDAMT").getText());
      order.setOrderDate((em.element("ORDERDATE") == null) ? "无记录" : em.element("ORDERDATE").getText());
      order.setOrderTime((em.element("ORDERTIME") == null) ? "无记录" : em.element("ORDERTIME").getText());
      if (em.element("ORDSTATUS") == null) { order.setOrdStatus("无记录");
      } else {
        String sta = em.element("ORDSTATUS").getText();
        Integer a = Integer.valueOf(Integer.parseInt(sta.substring(0, 1)));
        Integer b = Integer.valueOf(Integer.parseInt(sta.substring(1, 2)));
        switch (a.intValue())
        {
        case 0:
          switch (b.intValue())
          {
          case 0:
            order.setOrdStatus("未支付");
            break;
          case 1:
            order.setOrdStatus("支付成功");
            break;
          case 2:
            order.setOrdStatus("支付处理中");
            break;
          case 3:
            order.setOrdStatus("退款审核中");
            break;
          case 4:
            order.setOrdStatus("退款处理中");
            break;
          case 5:
            order.setOrdStatus("退款成功");
            break;
          case 6:
            order.setOrdStatus("退款失败");
            break;
          case 7:
            order.setOrdStatus("撤销审核中");
            break;
          case 8:
            order.setOrdStatus("同意撤销");
            break;
          case 9:
            order.setOrdStatus("撤销成功");
          }

          break;
        case 1:
          switch (b.intValue())
          {
          case 0:
            order.setOrdStatus("撤销失败");
            break;
          case 1:
            order.setOrdStatus("订单作废");
          }

          break;
        case 9:
          order.setOrdStatus("超时");
        }

      }

      order.setTransType((em.element("TRANSTYPE") == null) ? "无记录" : em.element("TRANSTYPE").getText());
      order.setPrdName((em.element("PRDNAME") == null) ? "无记录" : em.element("PRDNAME").getText());
      reportList.add(order);
    }
    return reportList; }

  public static List iterlist2(String ROOT) {
    List list = Etf.childs(ROOT);
    Log.info("list的长度=%s", new Object[] { Integer.valueOf(list.size()) });
    List reportList = new ArrayList();
    for (int i = 0; i < list.size(); ++i) {
      Element em = (Element)list.get(i);
      Users user = new Users();
      user.setPhone((em.element("PHONE") == null) ? "无记录" : em.element("PHONE").getText());
      user.setCusNam((em.element("CUS_NAM") == null) ? "无记录" : em.element("CUS_NAM").getText());
      user.setIdNo((em.element("ID_NO") == null) ? "无记录" : em.element("ID_NO").getText());
      user.setBthDay((em.element("BTH_DAY") == null) ? "无记录" : em.element("BTH_DAY").getText());
      reportList.add(user);
    }
    return reportList;
  }

  public static List setPact(String ROOT)
  {
    List list = Etf.childs(ROOT);
    Log.info("list的长度=%s", new Object[] { Integer.valueOf(list.size()) });
    List reportList = new ArrayList();
    for (int i = 0; i < list.size(); ++i) {
      Element em = (Element)list.get(i);
      Pact pact = new Pact();
      pact.setSEQNO((em.element("SEQ_NO") == null) ? "无记录" : em.element("SEQ_NO").getText());
      pact.setPACTTYPE((em.element("PACT_TYPE") == null) ? "无记录" : em.element("PACT_TYPE").getText());
      pact.setPACTVERSNO((em.element("PACT_VERS_NO") == null) ? "无记录" : em.element("PACT_VERS_NO").getText());
      pact.setPACTNAME((em.element("PACT_NAME") == null) ? "无记录" : em.element("PACT_NAME").getText());
      pact.setPACTCUSTTYPE((em.element("PACT_CUST_TYPE") == null) ? "无记录" : em.element("PACT_CUST_TYPE").getText());
      pact.setPACTTAKEEFFDATE((em.element("PACT_TAKE_EFF_DATE") == null) ? "无记录" : em.element("PACT_TAKE_EFF_DATE").getText());
      pact.setPACTLOSEEFFDATE((em.element("PACT_LOSE_EFF_DATE") == null) ? "无记录" : em.element("PACT_LOSE_EFF_DATE").getText());
      pact.setPACTCONTENT2((em.element("PACT_CONTENT2") == null) ? "无记录" : em.element("PACT_CONTENT2").getText());
      pact.setPACTSTATUS((em.element("PACT_STATUS") == null) ? "无记录" : em.element("PACT_STATUS").getText());
      pact.setCREOPERID((em.element("CRE_OPER_ID") == null) ? "无记录" : em.element("CRE_OPER_ID").getText());
      pact.setCREDATE((em.element("CRE_DATE") == null) ? "无记录" : em.element("CRE_DATE").getText());
      pact.setCRETIME((em.element("CRE_TIME") == null) ? "无记录" : em.element("CRE_TIME").getText());
      pact.setLSTUPTOPERID((em.element("LST_UPT_OPER_ID") == null) ? "无记录" : em.element("LST_UPT_OPER_ID").getText());
      pact.setLSTUPTDATE((em.element("LST_UPT_DATE") == null) ? "无记录" : em.element("LST_UPT_DATE").getText());
      pact.setLSTUPTTIME((em.element("LST_UPT_TIME") == null) ? "无记录" : em.element("LST_UPT_TIME").getText());
      reportList.add(pact);
    }
    return reportList;
  }
}