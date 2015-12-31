
import java.util.Random;
import javax.inject.Named;
import tangdi.annotations.Optional;
import tangdi.engine.context.Etf;
import tangdi.engine.context.Log;

public class TdRandom
{
  public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final String letterChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final String numberChar = "0123456789";

  @Named("generateMixRandom")
  public static int generateMixRandom(@Named("length") int length, @Named("NumOrStr") @Optional String NumOrStr, @Named("UppLow") String UppLow)
  {
    Log.info("执行原子组件开始: generateMixRandom ", new Object[0]);

    String flag = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    if (NumOrStr == null) {
      Log.info("原子函数[generateMixRandom]未设置参数[NumOrStr], 默认值 [ALL]", new Object[0]);
      NumOrStr = "ALL";
    } else if (NumOrStr.equals("NUM")) {
      flag = "0123456789";
    } else if (NumOrStr.equals("STR")) {
      flag = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    }

    if (UppLow == null) {
      Log.info("原子函数[generateMixRandom]未设置参数[uppLow], 取默认值 [MIX]", new Object[0]);
      UppLow = "MIX";
    }

    StringBuffer sb = new StringBuffer();
    Random random = new Random();
    for (int i = 0; i < Integer.valueOf(length).intValue(); i++) {
      sb.append("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(random.nextInt(flag.length())));
    }
    if (UppLow.equals("MIX"))
      setEtfBody("Random", sb.toString());
    else if (UppLow.equals("UPP"))
      setEtfBody("Random", sb.toString().toUpperCase());
    else if (UppLow.equals("LOW")) {
      setEtfBody("Random", sb.toString().toLowerCase());
    }
    Log.info("执行原子组件结束: generateMixRandom ", new Object[0]);
    return 0;
  }

  public static void main(String[] args) {
    generateMixRandom(10, "", "LOW");
  }

  public static void setEtfBody(String nodeName, String nodeValue)
  {
    if (nodeValue != null) {
      if (Etf.getChildValue(nodeName) != null) {
        Etf.setChildValue(nodeName, nodeValue);
        Log.info("设置ETF树[" + nodeName + "]节点值[" + nodeValue + "]", new Object[0]);
      } else {
        Etf.setChildValue(nodeName, nodeValue);
        Log.info("向ETF树添加[" + nodeName + "]节点,节点值[" + nodeValue + "]", new Object[0]);
      }
    }
    else Log.info("返回[" + nodeName + "]节点为空，不添加节点", new Object[0]);
  }
}