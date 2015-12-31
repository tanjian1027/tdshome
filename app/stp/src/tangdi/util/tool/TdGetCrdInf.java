package tangdi.util.tool;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import tangdi.annotations.Init;
import tangdi.annotations.Optional;
import tangdi.engine.context.DB;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;

public class TdGetCrdInf
{
  public static List<FitItem> fitTbl;

  @Named("GetCrdInfo")
  public static int GetCrdInfo(@Optional @Named("CrdNo") String crdNo, @Optional @Named("Track2") String trk2, @Optional @Named("Track3") String trk3)
  {
    int result = 1;
    boolean ishandinput = true;
    if (((trk2 != null) && (!(trk2.trim().equals("")))) || (
      (trk3 != null) && (!(trk3.trim().equals(""))))) {
      ishandinput = false;
    }

    if ((ishandinput) && (((crdNo == null) || ("".equals(crdNo.trim()))))) {
      Log.error("输入数据错误,需要输入卡号或者二三磁信息", new Object[0]);
      return 1;
    }

    List fitTbl1 = new ArrayList();
    try
    {
      fitTbl1 = loadFit();
    }
    catch (SQLException e) {
      e.printStackTrace();
      Log.info("crdbininf装载失败", new Object[0]);
      return 1;
    }

    for (FitItem item : fitTbl1) {
      if (ishandinput) {
        int binlen = Integer.parseInt(item.getBinLen());
        int crdlen = Integer.parseInt(item.getCrdLen());
        if (crdlen != crdNo.length()) {
          result = 1;
        }
        else {
          if (crdNo.substring(0, binlen).equals(item.getBinCtt()))
          {
            Etf.setChildValue("IssNam", item.getIssNam());
            Etf.setChildValue("IssNo", item.getIssNo());
            Etf.setChildValue("CrdNam", item.getCrdNam());
            Etf.setChildValue("DCFlag", item.getDcfLag());
            Etf.setChildValue("bankCode", item.getBankCode());
            result = 0;
            break;
          }
          result = 1;
        }
      }
      else
      {
        int crdoff;
        int binlen;
        int crdlen;
        String binval;
        String crdNoTemp1;
        String crdNoTemp2;
        if ((trk2 != null) && (item.getCrdCrk().contains("2"))) {
          crdoff = 0;
          if (!(item.getBinOff().substring(0, 1).equals("0")))
          {
            crdoff = Integer.parseInt(item.getBinOff().substring(0, 
              1)) - 
              2;
          }
          binlen = Integer.parseInt(item.getBinLen());
          crdlen = Integer.parseInt(item.getCrdLen());
          binval = trk2.substring(crdoff, binlen + crdoff);

          crdNoTemp1 = trk2.substring(crdoff, crdoff + crdlen);
          crdNoTemp2 = trk2.substring(crdoff, crdoff + crdlen + 
            1);
          if (!(crdNoTemp2.equals(crdNoTemp1 + "D"))) {
            continue;
          }
          if (!(binval.equals(item.getBinCtt())))
            continue;
          Etf.setChildValue("IssNam", item.getIssNam());
          Etf.setChildValue("IssNo", item.getIssNo());
          Etf.setChildValue("CrdNam", item.getCrdNam());
          Etf.setChildValue("DCFlag", item.getDcfLag());
          Etf.setChildValue("CrdNo", 
            trk2.substring(crdoff, crdoff + crdlen));
          Etf.setChildValue(
            "TExpDat", 
            trk2.substring(crdoff + crdlen + 1, crdoff + 
            crdlen + 5));
          Etf.setChildValue("bankCode", item.getBankCode());
          break;
        }

        if ((trk3 != null) && (item.getCrdCrk().contains("3"))) {
          crdoff = 0;
          if (item.getCrdCrk().equals("3")) {
            if (!(item.getBinOff().substring(0).equals("0")))
            {
              crdoff = Integer.parseInt(item.getBinOff()
                .substring(0)) - 
                2;
            }
          }
          else if (!(item.getBinOff().substring(2).equals("0")))
          {
            crdoff = Integer.parseInt(item.getBinOff()
              .substring(2)) - 
              2;
          }

          binlen = Integer.parseInt(item.getBinLen());
          crdlen = Integer.parseInt(item.getCrdLen());
          binval = trk3.substring(crdoff, binlen + crdoff);

          crdNoTemp1 = trk3.substring(crdoff, crdoff + crdlen);
          crdNoTemp2 = trk3.substring(crdoff, crdoff + crdlen + 
            1);
          if (!(crdNoTemp2.equals(crdNoTemp1 + "D"))) {
            continue;
          }

          if (!(binval.equals(item.getBinCtt())))
            continue;
          Etf.setChildValue("IssNam", item.getIssNam());
          Etf.setChildValue("IssNo", item.getIssNo());
          Etf.setChildValue("CrdNam", item.getCrdNam());
          Etf.setChildValue("DCFlag", item.getDcfLag());
          Etf.setChildValue("CrdNo", 
            trk3.substring(crdoff, crdoff + crdlen));
          Etf.setChildValue(
            "TExpDat", 
            trk3.substring(crdoff + crdlen + 1, crdoff + 
            crdlen + 5));
          Etf.setChildValue("bankCode", item.getBankCode());
          break;
        }
      }

    }

    if (result == 1) {
      Etf.setChildValue("RspCod", "00001");
      Etf.setChildValue("RspMsg", "获取卡bin信息失败");
    }
    Log.info(" CrdNo length=" + crdNo.length() + " data: %s", 
      new Object[] { crdNo });
    return result;
  }

  @Init
  public static List<FitItem> loadFit()
    throws SQLException
  {
    List list = 
      DB.execQuery("select * from crdbininf");

    List fitTbl = new ArrayList();
    for (Map obj : list) {
      FitItem item = new FitItem();
      item.setIssNam((String)obj.get("ISSNAM"));
      item.setIssNo((String)obj.get("ISSNO"));
      item.setBankCode((String)obj.get("BANKCODE"));
      item.setCrdNam((String)obj.get("CRDNAM"));
      item.setFitCrk((String)obj.get("FITCRK"));
      item.setFitLen((String)obj.get("FITLEN"));
      item.setFitOff((String)obj.get("FITOFF"));
      item.setCrdOff((String)obj.get("CRDOFF"));
      item.setCrdLen((String)obj.get("CRDLEN"));
      item.setCrdCtt((String)obj.get("CRDCTT"));
      item.setCrdCrk((String)obj.get("CRDCRK"));
      item.setBinOff((String)obj.get("BINOFF"));
      item.setBinLen((String)obj.get("BINLEN"));
      item.setBinCtt((String)obj.get("BINCTT"));
      item.setBinCrk((String)obj.get("BINCRK"));
      item.setDcfLag((String)obj.get("DCFLAG"));
      fitTbl.add(item);
    }
    return fitTbl;
  }
}