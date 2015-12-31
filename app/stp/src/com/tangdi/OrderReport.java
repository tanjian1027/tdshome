package com.tangdi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import net.sf.excelutils.ExcelException;
import net.sf.excelutils.ExcelUtils;
import org.dom4j.Element;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;
import tangdi.engine.context.Msg;

public class OrderReport
{
  @Named("ToReport")
  public static int ToReport(@Named("Root") String Root, @Named("FileDemo") String FileDemo, @Named("FilePath") String FilePath, @Named("FileToPath") String FileToPath, @Named("NameForUser") String NameForUser, @Named("WebPath") String WebPath)
  {
    List reportList = null;
    if (FileDemo.equals("orderdemo2"))
      reportList = iterlist1(Root);
    else {
      reportList = iterlist2(Root);
    }
    ExcelUtils.addValue("reportList", reportList);
    String config = WebPath + "/" + FilePath + "/" + FileDemo + ".xls";
    Log.info("config", new Object[] { config });
    String fileName = NameForUser + System.currentTimeMillis() + ".xls";
    Log.info("----fileName=%s--------", new Object[] { WebPath + FileToPath + "/" + fileName + config });
    try {
      ExcelUtils.export(config, new FileOutputStream(WebPath + FileToPath + "/" + fileName));
    }
    catch (FileNotFoundException e) {
      Log.info("----fileName=%s--", new Object[] { WebPath + FileToPath + "/" + fileName + "û�ҵ�" });
      return 1;
    }
    catch (ExcelException e) {
      Log.info("----fileName=%s--", new Object[] { WebPath + FileToPath + "/" + fileName + "�����쳣" });
      return 2;
    }
    Msg.setTmp("FILENAME", fileName);
    return 0;
  }

  public static List iterlist1(String ROOT) {
    List list = Etf.childs(ROOT);
    Log.info("list�ĳ���=%s", new Object[] { Integer.valueOf(list.size()) });
    List reportList = new ArrayList();
    for (int i = 0; i < list.size(); ++i) {
      Element em = (Element)list.get(i);
      Orders order = new Orders();
      order.setPrdOrdNo((em.element("PRDORDNO") == null) ? "�޼�¼" : em.element("PRDORDNO").getText());
      order.setOrdAmt((em.element("ORDAMT") == null) ? "�޼�¼" : em.element("ORDAMT").getText());
      order.setOrderDate((em.element("ORDERDATE") == null) ? "�޼�¼" : em.element("ORDERDATE").getText());
      order.setOrderTime((em.element("ORDERTIME") == null) ? "�޼�¼" : em.element("ORDERTIME").getText());
      String flag = em.element("FLAG").getText();
      if (em.element("ORDSTATUS") == null) { order.setOrdStatus("�޼�¼");
      } else {
        String sta = em.element("ORDSTATUS").getText();
        Integer a = Integer.valueOf(Integer.parseInt(sta.substring(0, 1)));
        Integer b = Integer.valueOf(Integer.parseInt(sta.substring(1, 2)));
        if (flag.equals("all")) {
          switch (a.intValue())
          {
          case 0:
            switch (b.intValue())
            {
            case 0:
              order.setOrdStatus("δ֧��");
              break;
            case 1:
              order.setOrdStatus("֧���ɹ�");
              break;
            case 2:
              order.setOrdStatus("֧��������");
            }
          }

        }
        else {
          switch (a.intValue())
          {
          case 0:
            switch (b.intValue())
            {
            case 0:
              order.setOrdStatus("�˿������");
              break;
            case 1:
              order.setOrdStatus("�˿����");
              break;
            case 2:
              order.setOrdStatus("�˿�ɹ�");
              break;
            case 3:
              order.setOrdStatus("�˿�ʧ��");
            }
          }

        }

        switch (a.intValue())
        {
        case 0:
          switch (b.intValue())
          {
          case 7:
            order.setOrdStatus("���������");
            break;
          case 8:
            order.setOrdStatus("ͬ�⳷��");
            break;
          case 9:
            order.setOrdStatus("�����ɹ�");
          }

          break;
        case 1:
          switch (b.intValue())
          {
          case 0:
            order.setOrdStatus("����ʧ��");
            break;
          case 1:
            order.setOrdStatus("��������");
          }

          break;
        case 9:
          order.setOrdStatus("��ʱ");
        }

      }

      order.setTransType((em.element("TRANSTYPE") == null) ? "�޼�¼" : em.element("TRANSTYPE").getText());
      order.setPrdName((em.element("PRDNAME") == null) ? "�޼�¼" : em.element("PRDNAME").getText());
      reportList.add(order);
    }
    return reportList; }

  public static List iterlist2(String ROOT) {
    List list = Etf.childs(ROOT);
    Log.info("list�ĳ���=%s", new Object[] { Integer.valueOf(list.size()) });
    List reportList = new ArrayList();
    for (int i = 0; i < list.size(); ++i) {
      Element em = (Element)list.get(i);
      Users user = new Users();
      user.setPhone((em.element("PHONE") == null) ? "�޼�¼" : em.element("PHONE").getText());
      user.setCusNam((em.element("CUS_NAM") == null) ? "�޼�¼" : em.element("CUS_NAM").getText());
      user.setIdNo((em.element("ID_NO") == null) ? "�޼�¼" : em.element("ID_NO").getText());
      user.setBthDay((em.element("BTH_DAY") == null) ? "�޼�¼" : em.element("BTH_DAY").getText());
      reportList.add(user);
    }
    return reportList;
  }
}