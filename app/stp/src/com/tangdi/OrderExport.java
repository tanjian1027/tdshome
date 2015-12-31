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

public class OrderExport
{
  @Named("ToExport")
  public static int ToExport(@Named("Root") String Root, @Named("FileDemo") String FileDemo, @Named("FilePath") String FilePath, @Named("FILETOPATH") String FILETOPATH, @Named("NameForUser") String NameForUser, @Named("WebPath") String WebPath)
  {
    List reportList = null;

    reportList = iterlist1(Root);

    ExcelUtils.addValue("reportList", reportList);

    String tmpPath = OrderExport.class.getClassLoader().getResource("").getPath();
    int i = tmpPath.indexOf("classes/");
    String webPath = tmpPath.substring(0, i);
    String config = webPath + "/" + FilePath + "/" + FileDemo + ".xls";
    Log.info("template：%s", new Object[] { config });
    String fileName = NameForUser + System.currentTimeMillis() + ".xls";
    Log.info("----fileName=%s--------", new Object[] { webPath + "/" + FILETOPATH + "/" + fileName });
    try {
      ExcelUtils.export(config, new FileOutputStream(webPath + "/" + FILETOPATH + "/" + fileName));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      Log.info("----fileName=%s--", new Object[] { webPath + "/" + FILETOPATH + "/" + fileName + "没找到" });
      return 1;
    } catch (ExcelException e) {
      e.printStackTrace();
      Log.info("----fileName=%s--", new Object[] { webPath + "/" + FILETOPATH + "/" + fileName + "导入异常" });
      return 2;
    }
    Msg.setTmp("FILENAME", fileName);
    return 0; }

  public static List iterlist1(String ROOT) {
    List list = Etf.childs(ROOT);
    Log.info("list的长度=%s", new Object[] { Integer.valueOf(list.size()) });
    List reportList = new ArrayList();
    for (int i = 0; i < list.size(); ++i) {
      Element em = (Element)list.get(i);
      OrderInfo order = new OrderInfo();
      order.setPrdOrdNo((em.element("PRDORDNO") == null) ? "" : em.element("PRDORDNO").getText());
      order.setMerNo((em.element("MERNO") == null) ? "" : em.element("MERNO").getText());
      order.setOrdAmt((em.element("ORDAMT") == null) ? "" : em.element("ORDAMT").getText());
      order.setOrderTime((em.element("ORDERTIME") == null) ? "" : em.element("ORDERTIME").getText());
      order.setCustName((em.element("CUSTNAME") == null) ? "" : em.element("CUSTNAME").getText());

      order.setPayType((em.element("PAYTYPE") == null) ? "" : em.element("PAYTYPE").getText());
      order.setBuyCount((em.element("BUYCOUNT") == null) ? "" : em.element("BUYCOUNT").getText());
      order.setFee((em.element("FEE") == null) ? "" : em.element("FEE").getText());
      if (em.element("ORDSTATUS") == null) { order.setOrdStatus("");
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

      order.setPrdName((em.element("PRDNAME") == null) ? "" : em.element("PRDNAME").getText());
      order.setActDat((em.element("ACTDAT") == null) ? "" : em.element("ACTDAT").getText());
      reportList.add(order);
    }
    return reportList;
  }
}