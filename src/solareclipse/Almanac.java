package solareclipse;

import java.util.Calendar;
import java.util.TimeZone;

public class Almanac
 {
  private Almanac(){}

  //グリニッジ恒星時を計算する
  public static double getGreenidgeSiderealTime(Calendar cal)
   {
    cal.get(Calendar.DAY_OF_YEAR);//setした日付を有効にするためのダミー

    cal.setTimeZone(TimeZone.getTimeZone("UTC")); //世界協定時刻へ変換

    //TJD(NASAが導入した世界時1968年3月24日0時からの日数)の計算方法
    //グレゴリオ暦（1582年10月15日以降）の西暦年をY、月をM、日をDとする。
    //ただし1月のはM=13、2月はM=14、YはY=Y-1とする。

    double JD = getJulianDay(cal);
    double TJD = JD -2440000.5;
    double thetaG = (0.671262 + 1.0027379094 * TJD);

    return 360.0 * (thetaG - Math.floor(thetaG));
   }

  //ユリウス日を計算する
  public static double getJulianDay(Calendar cal)
   {
    double Y = (double)cal.get(Calendar.YEAR);
    double M = (double)cal.get(Calendar.MONTH) + 1.0; //Calendarは0から11で格納するため、1加算
    double D = (double)cal.get(Calendar.DAY_OF_MONTH);
    double H = (double)cal.get(Calendar.HOUR_OF_DAY);
    double Mi = (double)cal.get(Calendar.MINUTE);
    double S = (double)cal.get(Calendar.SECOND);

    if (M < 3.0){Y -= 1.0;  M += 12.0;}

    return Math.floor(365.25 * Y) + Math.floor(Y / 400.0) - Math.floor(Y / 100.0) + Math.floor(30.59 * (M - 2.0)) + D +1721088.5 + H/ 24.0 + Mi/1440.0 + S/ 86400.0;
   }
 }
