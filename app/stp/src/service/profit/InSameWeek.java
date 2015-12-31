package service.profit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.inject.Named;

public class InSameWeek
{
  @Named("IN_SAME_WEEK")
  public static int IS_IN_SAME_WEEK(@Named("old_date") String o_date, @Named("curr_date") String c_date)
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    Date old_date = null;
    Date curr_date = null;
    try
    {
      old_date = format.parse(o_date);
    } catch (ParseException e1) {
      e1.printStackTrace();
    }
    Calendar calendar = Calendar.getInstance();

    calendar.setTime(old_date);
    int old_week = calendar.get(3);
    try
    {
      curr_date = format.parse(c_date);
    } catch (ParseException e1) {
      e1.printStackTrace();
    }

    calendar.setTime(curr_date);
    int curr_week = calendar.get(3);
    if (old_week == curr_week) return 0;
    return -1; }

  @Named("IN_SAME_MONTH")
  public static int IS_IN_SAME_MONTH(@Named("old_date") String o_date, @Named("curr_date") String c_date) {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    Date old_date = null;
    Date curr_date = null;
    try
    {
      old_date = format.parse(o_date);
    } catch (ParseException e1) {
      e1.printStackTrace();
    }
    Calendar calendar = Calendar.getInstance();

    calendar.setTime(old_date);
    int old_month = calendar.get(2);
    try
    {
      curr_date = format.parse(c_date);
    } catch (ParseException e1) {
      e1.printStackTrace();
    }

    calendar.setTime(curr_date);
    int curr_month = calendar.get(2);
    if (old_month == curr_month) return 0;
    return -1;
  }
}