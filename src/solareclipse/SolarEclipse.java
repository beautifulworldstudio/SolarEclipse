package solareclipse;

public class SolarEclipse
{

 private SolarEclipse(){}

 //本影の輪郭を計算する。qは角度で与えられる
 public static void getUmbralOutline(VesselElements ve, double q, double[] result)
  {
   getOutline(ve, ve.getL2(), ve.getTanf2(), q, result);
  }


 //半影の輪郭を計算する
 public static void getPenumbralOutline(VesselElements ve, double q, double[] result)
  {
   getOutline(ve, ve.getL1(), ve.getTanf1(), q, result);
  }


 //本影・半影の輪郭を計算する。qは角度で与えられる
 private static void getOutline(VesselElements ve, double l, double tan , double q, double[] result)
  {
   if (result.length != 3) return;

   double x = 0.0;
   double y = 0.0;
   double z = 1.0; //zの初期値
   double Q = q / 180 * Math.PI;
   double lastz = 0.0; //前のzの値
   double delta = 0.0;
   double cos_d = Math.cos(ve.getDeclination() / 180 * Math.PI);
   double sin_d = Math.sin(ve.getDeclination() / 180 * Math.PI );
   double cos_d2 = cos_d * cos_d;
   int count = 0;

   while(Math.abs(z - lastz) > 10e-7)//zが10の-7乗未満に収束しない場合ループ
    {
     lastz = z;
     delta = l - z * tan;
     x = ve.getX0() + delta * Math.cos(Q);
     y = ve.getY0() + delta * Math.sin(Q);

     double x2 = x * x;
     double y2 = y * y;

     z = (-Constants.e2 * y * cos_d * sin_d + Math.sqrt((1 - Constants.e2) * (1.0 - x2 -y2 - Constants.e2 *(1.0 - x2)* cos_d2 ))) /
         (1.0 - Constants.e2 * cos_d2);

     if (count++ > 10) break; //10回を超えたら強制的に打ち切り
    }

   result[0] = x;
   result[1] = y;
   result[2] = z;
  }

 //半影と地球外周楕円の交点を計算する
 public static void getCrossPoint(VesselElements ve, double[] result1, double[] result2)
  {
   if (result1.length != 3 || result2.length != 3) return;

   double x0 = ve.getX0();
   double y0 = ve.getY0();
   double l1 = ve.getL1();
   double r0 = Math.sqrt(x0 * x0 + y0 * y0);
   double theta = Math.atan2(y0, x0); //ラジアン。変換しない。
   double d = ve.getDeclination() / 180.0 * Math.PI;
   double cos_d = Math.cos(d);
   double sin_d = Math.sin(d);

   double E2 = (Constants.e2 * cos_d * cos_d) / (1.0 - Constants.e2 * sin_d * sin_d);

   double rhoplus = 1.0; //初期値
   double gammaplus = 0.0;

   int count = 0;
   //正の値の近似計算
   while(true)
    {
     double angle = Math.acos((r0 * r0 + rhoplus * rhoplus - l1 * l1) / (2.0 * r0 * rhoplus)); //近似計算//正の値
     if (angle < 0.0) angle = -angle;

     gammaplus = angle + theta;
     double newrhoplus = Math.sqrt((1.0 - E2) / (1.0 - E2 * Math.cos(gammaplus) * Math.cos(gammaplus)));

     double dif = Math.abs(newrhoplus - rhoplus);
     rhoplus = newrhoplus;

     if(dif < 10e-7) break;
     if(count++ > 5) break;
    }

   double rhominus = 1.0;
   double gammaminus = 0.0;
   count = 0;

   //負の値の近似計算
   while(true)
    {
     double angle = Math.acos((r0 * r0 + rhominus * rhominus - l1 * l1) / (2.0 * r0 * rhominus)); //近似計算//正の値
     if (angle > 0.0) angle = -angle;

     gammaminus = angle + theta;
     double newrhominus = Math.sqrt((1.0 - E2) / (1.0 - E2 * Math.cos(gammaminus) * Math.cos(gammaminus)));

     double dif = Math.abs(newrhominus - rhominus);
     rhominus = newrhominus;

     if(dif < 10e-7) break;
     if(count++ > 5) break;
    }
   result1[0] = rhoplus * Math.cos(gammaplus);
   result1[1] = rhoplus * Math.sin(gammaplus);
   result1[2] = 0.0;
   result2[0] = rhominus * Math.cos(gammaminus);
   result2[1] = rhominus * Math.sin(gammaminus);
   result2[2] = 0.0;

//   System.out.println("rhoplus = " + rhoplus + " rhominus =" + rhominus);
  }

 //基準面上の半影における点の偏角Qを計算する
 public static double getPenumbralQ(VesselElements ve, double[] point)
  {
   if(point == null || point.length != 3) return Double.NaN;

   double delta = ve.getL1() - point[2] * ve.getTanf1();

   double Q = Math.acos((point[0] - ve.getX0()) / delta) / Math.PI * 180.0;
   if ((point[1] - ve.getY0()) < 0.0) Q = -Q; //y方向が負ならば角度を反転する。
   if (Q < 0.0) Q += 360.0;

   return Q;
  }

/*
//本影の輪郭を計算する。qは角度で与えられる
 public static void getUmbralOutline2(VesselElements ve, double q, double[] result)
  {
   double x = 0.0;
   double y = 0.0;
   double z = 1.0; //zの初期値
   double Q = q / 180 * Math.PI;
   double lastz = 0.0; //前のzの値
   double delta = 0.0;
   double cos_d = Math.cos(ve.getDeclination() / 180 * Math.PI);
   double sin_d = Math.sin(ve.getDeclination() / 180 * Math.PI);
   double cos_d2 = cos_d * cos_d;
   int count = 0;

   while(Math.abs(z - lastz) > 10e-7)//zが10の-7乗未満に収束しない場合ループ
    {
     lastz = z;
     delta = ve.getL2() - z * ve.getTanf2();
System.out.println("L2 = " + ve.getL2() + " tanf2 = " + ve.getTanf2() + " delta = " + delta);

     x = ve.getX0() + delta * Math.cos(Q);
     y = ve.getY0() + delta * Math.sin(Q);

     double x2 = x * x;
     double y2 = y * y;

     z = (-Constants.e2 * y * cos_d * sin_d + Math.sqrt((1 - Constants.e2) * (1.0 - x2 -y2 - Constants.e2 *(1.0 - x2)* cos_d2 ))) /
         (1.0 - Constants.e2 * cos_d2);

     if (count++ > 10) break; //10回を超えたら強制的に打ち切り
    }

   result[0] = x;
   result[1] = y;
   result[2] = z;
  }
*/
}
