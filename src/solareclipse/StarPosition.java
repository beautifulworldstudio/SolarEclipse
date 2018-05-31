package solareclipse;

import java.util.Calendar;
import java.util.TimeZone;


public class StarPosition
 {
  private StarPosition(){}


  //大気差(35'8'')
  public static final double refraction = 0.58556;

  //度からラジアンへ変換する場合の係数
  private static final double DegPi = Math.PI / 180.0;

  private static final double[] moontableA = new double[]
   {
    1.2740, 100.738, 4133.3536,
    0.6583, 235.700, 8905.3422,
    0.2136, 269.926, 9453.9773,
    0.1856, 177.525, 359.9905,
    0.1143, 6.546, 9664.0404,
    0.0588, 214.22, 638.635,
    0.0572, 103.21, 3773.363,
    0.0533, 10.66, 13677.331,
    0.0459, 238.18, 8545.352,
    0.0410, 137.43, 4411.998,
    0.0348, 117.84, 4452.671,
    0.0305, 312.49, 5131.979,
    0.0153, 130.84, 758.698,
    0.0125, 141.51, 14436.029,
    0.0110, 231.59, 4892.052,
    0.0107, 336.44, 13038.696,
    0.0100, 44.89, 14315.966,
    0.0085, 201.5, 8266.71,
    0.0079, 278.2, 4493.34,
    0.0068, 53.2, 9265.33,
    0.0052, 197.2, 319.32,
    0.0050, 295.4, 4812.66,
    0.0048, 235.0, 19.34,
    0.0040, 13.2, 13317.34,
    0.0040, 145.6, 18449.32,
    0.0040, 119.5, 1.33,
    0.0039, 111.3, 17810.68,
    0.0037, 349.1, 5410.62,
    0.0027, 272.5, 9183.99,
    0.0026, 107.2, 13797.39,
    0.0024, 211.9, 988.63,
    0.0024, 252.8, 9224.66,
    0.0022, 240.6, 8185.36,
    0.0021, 87.5, 9903.97,
    0.0021, 175.1, 719.98,
    0.0021, 105.6, 3413.37,
    0.0020, 55.0, 19.34,
    0.0018, 4.1, 4013.29,
    0.0016, 242.2, 18569.38,
    0.0012, 339.0, 12678.71,
    0.0011, 276.5, 19208.02,
    0.0009, 218, 8586.0,
    0.0008, 188, 14037.3,
    0.0008, 204, 7906.7,
    0.0007, 140, 4052.0,
    0.0007, 275, 4853.3,
    0.0007, 216, 278.6,
    0.0006, 128, 1118.7,
    0.0005, 247, 22582.7,
    0.0005, 181, 19088.0,
    0.0005, 114, 17450.7,
    0.0005, 332, 5091.3,
    0.0004, 313, 398.7,
    0.0004, 278, 120.1,
    0.0004, 71, 9584.7,
    0.0004, 20, 720.0,
    0.0003, 83, 3814.0,
    0.0003, 66, 3494.7,
    0.0003, 147, 18089.3,
    0.0003, 311, 5492.0,
    0.0003, 161, 40.7,
    0.0003, 280, 23221.3
  };


  private static final double[] moontableB =
   {
    0.2806, 228.235, 9604.0088,
    0.2777, 138.311, 60.0316,
    0.1732, 142.427, 4073.3220,
    0.0554, 194.01, 8965.374,
    0.0463, 172.55, 698.667,
    0.0326, 328.96, 13737.362,
    0.0172, 3.18, 14375.997,
    0.0093, 277.4, 8845.31,
    0.0088, 176.7, 4711.96,
    0.0082, 144.9, 3713.33,
    0.0043, 307.6, 5470.66,
    0.0042, 103.9, 18509.35,
    0.0034, 319.9, 4433.31,
    0.0025, 196.5, 8605.38,
    0.0022, 331.4, 13377.37,
    0.0021, 170.1, 1058.66,
    0.0019, 230.7, 9244.02,
    0.0018, 243.3, 8206.68,
    0.0018, 270.8, 5192.01,
    0.0017, 99.8, 14496.06,
    0.0016, 135.7, 420.02,
    0.0015, 211.1, 9284.69,
    0.0015, 45.8, 9964.00,
    0.0014, 219.2, 299.96,
    0.0013, 95.8, 4472.03,
    0.0013, 155.4, 379.35,
    0.0012, 38.4, 4812.68,
    0.0012, 148.2, 4851.36,
    0.0011, 138.3, 19147.99,
    0.0010, 18.0, 12978.66,
    0.0008, 70, 17870.7,
    0.0008, 326, 9724.1,
    0.0007, 294, 13098.7,
    0.0006, 224, 5590.7,
    0.0006, 52, 13617.3,
    0.0005, 280, 8485.3,
    0.0005, 239, 4193.4,
    0.0004, 311, 9483.9,
    0.0004, 238, 23281.3,
    0.0004, 81, 10242.6,
    0.0004, 13, 9325.4,
    0.0004, 147, 14097.4,
    0.0003, 205, 22642.7,
    0.0003, 107, 18149.4,
    0.0003, 146, 3353.3,
    0.0003, 234, 19268.0
   };

  //任意の時刻の太陽の黄経(角度)。
  public static double getSunEclipticLongitude(Calendar cal)
   {
    double T = getTimeDifferenceJST(cal);

    double result=280.4603 + 360.00769 * T +
 		    (1.9146 + 0.00005 * T) * Math.sin((357.538 + 359.991 * T)  / 180.0 * Math.PI) +
			 0.02 * Math.sin((355.05 + 719.981 * T)  / 180.0 * Math.PI) +
			 0.0048 * Math.sin((234.95 + 19.341 * T) / 180.0 * Math.PI) +
			 0.002 * Math.sin((247.1 + 329.64 * T) / 180.0 * Math.PI) +
			 0.0018 * (Math.sin((297.8 + 4452.67 * T) / 180.0 * Math.PI) + Math.sin((251.3 + 0.2 * T) / 180.0 * Math.PI)) +
			 0.0015 * Math.sin((343.2 + 450.37 * T)  / 180.0 * Math.PI) +
			 0.0013 * Math.sin((81.4 + 225.18 * T)  / 180.0 * Math.PI) +
			 0.0008 * Math.sin((132.5 + 659.29 * T)  / 180.0 * Math.PI) +
			 0.0007 * (Math.sin((153.3 + 90.38 * T) / 180.0 * Math.PI) +  Math.sin((206.8 + 30.35 * T) / 180.0 * Math.PI)) +
			 0.0006 * Math.sin((29.8 + 337.18 * T)  / 180.0 * Math.PI) +
			 0.0005 * (Math.sin((207.4 + 1.5 * T) / 180.0 * Math.PI) + Math.sin((291.2 + 22.81 * T) / 180.0 * Math.PI)) +
			 0.0004 * (Math.sin((234.9 + 315.56 * T) / 180.0 * Math.PI) + Math.sin((157.3 + 299.3 * T) / 180.0 * Math.PI) + Math.sin((21.1 + 720.02 * T) / 180.0 * Math.PI)) +
			 0.0003 * (Math.sin((352.5 + 1079.97 * T) / 180.0 * Math.PI) + Math.sin((329.7 + 44.43 * T) / 180.0 * Math.PI));

    if(result > 360.0){ result = result - 360.0 * Math.floor(result / 360.0); }
    else if ( result < 0.0) { result = result + 360.0 * Math.ceil(result / 360.0); }

    return result;
   }


  //任意の時刻の太陽の赤経(角度)
  public static void getSunRightAscension(Calendar cal, double[] result) throws IllegalArgumentException
   {
    if (result.length != 3) throw new IllegalArgumentException();

    double T = getTimeDifferenceJST(cal);
System.out.println("T=" + T);
    double ramda =280.4603 + 360.00769 * T +
 		    (1.9146 + 0.00005 * T) * Math.sin((357.538 + 359.991 * T) * DegPi) +
			 0.02 * Math.sin((355.05 + 719.981 * T) * DegPi) +
			 0.0048 * Math.sin((234.95 + 19.341 * T) * DegPi) +
			 0.002 * Math.sin((247.1 + 329.64 * T) * DegPi) +
			 0.0018 * (Math.sin((297.8 + 4452.67 * T) * DegPi) + Math.sin((251.3 + 0.2 * T) * DegPi)) +
			 0.0015 * Math.sin((343.2 + 450.37 * T)  * DegPi) +
			 0.0013 * Math.sin((81.4 + 225.18 * T)  * DegPi) +
			 0.0008 * Math.sin((132.5 + 659.29 * T)  * DegPi) +
			 0.0007 * (Math.sin((153.3 + 90.38 * T) * DegPi) +  Math.sin((206.8 + 30.35 * T) * DegPi)) +
			 0.0006 * Math.sin((29.8 + 337.18 * T)  * DegPi) +
			 0.0005 * (Math.sin((207.4 + 1.5 * T) * DegPi) + Math.sin((291.2 + 22.81 * T) * DegPi)) +
			 0.0004 * (Math.sin((234.9 + 315.56 * T) * DegPi) + Math.sin((157.3 + 299.3 * T) * DegPi) + Math.sin((21.1 + 720.02 * T) * DegPi)) +
			 0.0003 * (Math.sin((352.5 + 1079.97 * T) * DegPi) + Math.sin((329.7 + 44.43 * T) * DegPi));

    if(ramda > 360.0){ ramda = ramda - 360.0 * Math.floor(ramda / 360.0); }
    else if ( ramda < 0.0) { ramda = ramda + 360.0 * Math.ceil(ramda / 360.0); }

    //黄道傾斜角
    double inclination = getInclination(T);

    //黄経から赤経を計算する
    result[0] = getSunRightAscension(ramda, inclination);

    //黄経から赤緯を計算する
    result[1] = getSunDeclination(ramda, inclination);

    //太陽までの距離
    result[2] = getSunDistance(T);
   }


  //月の黄経(角度) Tは
  public static void getMoonRightAscension(Calendar cal, double[] result)
   {
    if (result.length != 3) throw new IllegalArgumentException();

    double T = getTimeDifferenceUT(cal);

    double Am = 0.004 * Math.sin((119.5 + 1.33 * T) * DegPi) +
                0.002 * Math.sin((55.0 + 19.34 * T) * DegPi) +
                0.0006 * (Math.sin((71 + 0.2 * T) * DegPi) + Math.sin((54 + 19.3 * T) * DegPi));

    double ramda = 218.3161 + 4812.67881 * T + 6.2887 * Math.sin((134.961 + 4771.9886 * T + Am) * DegPi);

    for (int i = 0; i < moontableA.length; )
     {
      ramda += moontableA[i++] * Math.sin((moontableA[i++] + moontableA[i++] * T) * DegPi);
     }
    if(ramda > 360.0){ ramda = ramda - 360.0 * Math.floor(ramda / 360.0); }
    else if ( ramda < 0.0) { ramda +=360.0 * Math.ceil(ramda / -360.0); }

    double Bm = 0.0267 * Math.sin((234.95 + 19.341 * T) * DegPi) +
               0.0043 * Math.sin((322.1 + 19.36 * T) * DegPi) +
               0.0040 * Math.sin((119.5 + 1.33 * T) * DegPi) +
               0.0020 * Math.sin((55.0 + 19.34 * T) * DegPi) +
               0.0005 * Math.sin((307 + 19.4 * T) * DegPi);

    double gamma = 5.1282 * Math.sin((93.273 + 4832.0202 * T + Bm ) * DegPi);
    for (int i = 0; i < moontableB.length; )
     {
      gamma += moontableB[i++] * Math.sin((moontableB[i++] + moontableB[i++] * T) * DegPi);
     }
    if(gamma > 360.0){ gamma = gamma - 360.0 * Math.floor(gamma / 360.0); }
    else if ( gamma < 0.0) { gamma = gamma + 360.0 * Math.ceil(gamma / -360.0); }

    double e = getInclination(T) * DegPi;

    double alpha = ramda * DegPi;
    double beta = gamma * DegPi;

    double U = Math.cos(beta) * Math.cos(alpha);
    double V = -Math.sin(beta) * Math.sin(e) + Math.cos(beta) * Math.sin(alpha) * Math.cos(e);
    double W = Math.sin(beta) * Math.cos(e) + Math.cos(beta) * Math.sin(alpha) * Math.sin(e);

    //月の赤経
    result[0] = Math.atan2(V, U) / Math.PI * 180.0;
    if(result[0] > 360.0){ result[0] = result[0] - 360.0 * Math.floor(result[0] / 360.0); }
    else if ( result[0] < 0.0) { result[0] +=360.0 * Math.ceil(result[0] / -360.0); }

    //月の赤緯
    result[1] = Math.atan(W /  Math.sqrt(U * U + V * V)) / Math.PI * 180.0;

    //月の距離
    result[2] = Constants.de / Math.sin(getMoonParallax(T) * DegPi) / Constants.AU;
   }


  //太陽までの距離
  public static double getSunDistance(Calendar cal)
   {
    double T = getTimeDifferenceJST(cal);

    return getSunDistance(T);
   }


  //太陽までの距離
  private static double getSunDistance(double T)
   {
    double q = (0.007256 - 0.0000002 * T) * Math.sin((267.54 + 359.991 * T) * DegPi) +
			0.000091 * Math.sin((265.1 + 719.98 * T) * DegPi) +
			0.00003 * Math.sin(90 * DegPi) +
			0.000013 * Math.sin((27.8 + 4452.67 * T) * DegPi) +
			0.000007 * (Math.sin((254 + 450.4 * T) * DegPi) + Math.sin((156 + 329.6 * T) * DegPi));

    return Math.pow(10.0, q);
   }


  //黄道座標から太陽の赤経を計算する
  public static double getSunRightAscension(double elon, double e)
   {
    double alpha = Math.atan(Math.cos(e * DegPi) * Math.tan(elon * DegPi)) / Math.PI * 180;
    if(elon >= 180.0 && elon < 360.0)
     {
      while(alpha < 180.0 || alpha >= 360.0)
       {
        if(alpha < 180.0) alpha += 180.0;
        else if(alpha >= 360.0) alpha -= 180.0;
       }
     }
    else if(elon >= 0.0 && elon < 180.0)
     {
      while(alpha < 0.0 || alpha >= 180.0)
       {
        if(alpha < 0.0) alpha += 180.0;
        else if(alpha >= 180.0) alpha -= 180.0;
       }
     }
    return alpha;
   }


  //黄道座標から赤緯を計算する
  public static double getSunDeclination(double elon, double e)
   {
    return Math.asin(Math.sin(e * DegPi) * Math.sin(elon * DegPi)) / Math.PI * 180;
   }


 //黄道傾斜角
  public static double getInclination(double T)
   {
    return 23.439291 -0.000130042 * T;
   }

  //恒星時(時刻,　経度λ)
  public static double getSidereal(Calendar cal, double ramda)
   {
    double T = getTimeDifferenceUT(cal);

    double phai = 325.4606+360.007700536 * T + 0.00000003879 * T * T  + ramda; //時間、分、秒は考慮されているため、dの項は削除

    while(phai < -180.0 || phai > 180.0)
     {
      if(phai < -180.0) phai += 360.0;
      else if(phai > 180.0) phai -= 360.0;
     }

    return phai;
   }


  //恒星時(時間変数T,経過日数d,経度λ)
  public static double getSidereal(double T, double d, double ramda)
   {
    double phai = 325.4606+360.007700536 * T + 0.00000003879 * T * T + 360.0 * d + ramda;
    while(phai < -180.0 || phai > 180.0)
     {
      if(phai < -180.0) phai += 360.0;
      else if(phai > 180.0) phai -= 360.0;
     }

    return phai;
   }


  //2000年1月1日からの時刻変数Tを計算する
  public static double getTime(double Year, double Month, double Day, double Hour)
   {
    double y = Year - 2000.0;
    if(Month <= 2) { Month +=12; y -=1; }

   //2000年1月1日から指定日の午前0時までの経過日数（元式を変更33.875から33.5に）
    double K = 365 * y + 30 * Month + Day - 33.5 + Math.floor(3 * (Month + 1.0) / 5.0) + Math.floor(y / 4.0);

    return (K + Hour / 24.0 + getDeltaT(Year) / 86400.0) / 365.25;
   }


  //2000年1月1日0時(世界協定時間)からの時刻変数Tを計算する
  public static double getTimeDifferenceUT(Calendar cal)
   {
    cal.get(Calendar.DAY_OF_YEAR);//setした日付を有効にするためのダミー
    cal.setTimeZone(TimeZone.getTimeZone("UTC"));

    double Year = cal.get(Calendar.YEAR);
    double Month = (double)cal.get(Calendar.MONTH) + 1.0;
    double Day = (double)cal.get(Calendar.DAY_OF_MONTH);
    double Hour = (double)cal.get(Calendar.HOUR_OF_DAY);
    double Minute = (double)cal.get(Calendar.MINUTE);
    double Second = (double)cal.get(Calendar.SECOND);

    double y = Year - 2000.0;
    if(Month <= 2) { Month +=12; y -=1; }

   //2000年1月1日から指定日の午前0時までの経過日数
    double K = 365 * y + 30 * Month + Day - 33.5 + Math.floor(3 * (Month + 1.0) / 5.0) + Math.floor(y / 4.0);

    return (K + Hour / 24.0 + Minute / 1440.0 +  Second / 86400.0 + getDeltaT(Year) / 86400.0) / 365.25;
   }


  //2000年1月1日0時(日本時間)からの時刻変数Tを計算する
  public static double getTimeDifferenceJST(Calendar cal)
   {
    cal.get(Calendar.DAY_OF_YEAR);//setした日付を有効にするためのダミー
    cal.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

    double Year = cal.get(Calendar.YEAR);
    double Month = (double)cal.get(Calendar.MONTH) + 1.0;
    double Day = (double)cal.get(Calendar.DAY_OF_MONTH);
    double Hour = (double)cal.get(Calendar.HOUR_OF_DAY);
    double Minute = (double)cal.get(Calendar.MINUTE);
    double Second = (double)cal.get(Calendar.SECOND);
    double y = Year - 2000.0;

    if(Month <= 2) { Month +=12; y -=1; }

   //2000年1月1日から指定日の午前0時までの経過日数
    double K = 365 * y + 30 * Month + Day - 33.875 + Math.floor(3 * (Month + 1.0) / 5.0) + Math.floor(y / 4.0);
    return (K + Hour / 24.0 + Minute / 1440.0 +  Second / 86400.0 + getDeltaT(Year) / 86400.0) / 365.25;
   }


  //自転遅れΔTを計算する(NASAの公式)
  public static double getDeltaT(double Y)
   {
    //ΔTを計算する
    double deltaT = 0.0, t = 0;

    if(Y < 1961.0 && Y >= 1941.0){ t = Y - 1950.0; deltaT =  29.07 + 0.407 * t - Math.pow(t, 2.0 / 233.0) + Math.pow(t, 3.0 / 2547.0); }
    else if(Y < 1986.0 && Y >= 1961.0){ t = Y - 1975.0; deltaT = 45.45 + 1.067 * t - Math.pow(t, 2.0 / 260.0) - Math.pow(t, 3.0 / 718.0); }
    else if(Y < 2005.0 && Y >= 1986.0){ t = Y - 2000.0; deltaT = 63.86 + 0.3345 * t - 0.060374 * Math.pow(t,2.0) + 0.0017275 * Math.pow(t,3.0) + 0.000651814 * Math.pow(t,4.0) + 0.00002373599 * Math.pow(t,5.0);}
    else { t = Y - 2000; deltaT = 62.92 + 0.32217 * t + 0.005589 * Math.pow(t,2) ;}

    return deltaT;
   }


  //太陽視半径(太陽までの天文単位での距離)
  public static double getSunDiameter(double r)
   {
    return 0.266994 / r;
   }


  //視差(太陽までの天文単位での距離)
  public static double getSunParallax(double r)
   {
    return 0.00244428 / r;
   }


  //月の視差を計算する
  private static double getMoonParallax(double T)
   {
    return  0.9507 * Math.sin(Math.PI / 2.0) +
                    0.0518 * Math.sin((224.98 + 4771.989 * T) * DegPi) +
                    0.0095 * Math.sin((190.7 + 4133.35 * T) * DegPi) +
                    0.0078 * Math.sin((325.7 +  8905.34 * T) * DegPi) +
                    0.0028 * Math.sin((0.0 + 9543.98 * T) * DegPi) +
                    0.0009 * Math.sin((100.0 + 13677.3 * T) * DegPi) +
                    0.0005 * Math.sin((329 + 8545.4 * T) * DegPi) +
                    0.0004 * Math.sin((194 + 3773.4 * T) * DegPi) +
                    0.0003 * Math.sin((227 + 4412.0 * T) * DegPi);
   }

  //日の出高度(太陽視半径S,大気屈折効果E,大気差R,視差π)
  public static double getSunriseAltitude(double S, double E, double R, double P)
   {
    return -S - E- R + P;
   }


  //薄明高度の計算(大気屈折効果E,視差π)
  public static double getTwilightAltitude(double E, double P)
   {
    return -E - P -7.3611;
   }


  //時角(日の出高度k, 太陽の赤緯, 地球上の緯度)
  public static double getTimeAngle(double k, double delta, double lat)
   {
    delta = delta / 180.0 * Math.PI;
    lat = lat / 180.0 * Math.PI;

    return Math.abs(Math.acos((Math.sin(k / 180.0 * Math.PI) - Math.sin(delta) * Math.sin(lat)) / (Math.cos(delta) * Math.cos(lat))) * 180.0 / Math.PI);
   }


  //太陽の高度を計算する(赤経 , 赤緯、緯度、恒星時,)
  public static double getSunAltitude(double asc ,double dec, double lat, double phai )
   {
    double t = (phai - asc) / 180.0 * Math.PI;
    double latitude = lat / 180.0 * Math.PI;
    double declination = dec / 180.0 * Math.PI;
    double result = Math.asin(Math.sin(declination)* Math.sin(latitude) + Math.cos(declination) * Math.cos(latitude) * Math.cos(t)) / Math.PI * 180.0;

    double R = 0.0167 / Math.tan((result + 8.6 / (result + 4.4)) /180.0 * Math.PI);

    return result + R;
   }


  //角度の補正(角度)
  public static double reviseAngle(double angle)
   {
    if (angle >= 360.0 | angle < 0.0) return angle - Math.floor(angle / 360.0) * 360.0;

    return angle;
   }
 }

