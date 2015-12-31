import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.inject.Named;

public class InCurrentWeek
{
  @Named("IS_IN_CURRENT_WEEK")
  public static int IS_IN_CURRENT_WEEK(@Named("old_date") String date)
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    Date old_date = null;
    Date curr_date = new Date();
    try
    {
      old_date = format.parse(date);
    } catch (ParseException e1) {
      e1.printStackTrace();
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(old_date);
    int old_week = calendar.get(3);

    calendar.setTime(curr_date);
    int curr_week = calendar.get(3);
    if (old_week == curr_week) return 0;
    return -1;
  }
}