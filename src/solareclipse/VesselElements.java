package solareclipse;

import java.util.Calendar;

import matrix.Matrix;

public class VesselElements
 {
  private double ascension; //月影軸方向の赤経(ラジアン)
  private double declination; //月影軸方向の赤緯(ラジアン)
  private double g; //月の中心から太陽中心までの距離
  private double x0; //月中心の座標
  private double y0;
  private double z0;
  private double tanf1;
  private double tanf2;
  private double c1;
  private double c2;
  private double l1;
  private double l2;
  private double siderealtime; //グリニッジ恒星時。角度。

  public VesselElements(EquatorialCoordinate possun, EquatorialCoordinate posmoon, Calendar cal)
   {
    //赤経をラジアン角に変換
    double alphasun = possun.getRightAscension() / 180.0 * Math.PI;
    double alphamoon = posmoon.getRightAscension() / 180.0 * Math.PI;

    //赤緯をラジアン角に変換
    double gammasun = possun.getCelestialDeclination() / 180.0 * Math.PI;
    double gammamoon = posmoon.getCelestialDeclination() / 180.0 * Math.PI;

    //地心赤道座標を計算する
    double Xs = possun.getDistance() * Math.cos(alphasun) * Math.cos(gammasun);
    double Ys = possun.getDistance() * Math.sin(alphasun) * Math.cos(gammasun);
    double Zs = possun.getDistance() * Math.sin(gammasun);

    double Xm = posmoon.getDistance() * Math.cos(alphamoon) * Math.cos(gammamoon);
    double Ym = posmoon.getDistance() * Math.sin(alphamoon) * Math.cos(gammamoon);
    double Zm = posmoon.getDistance() * Math.sin(gammamoon);

    //月から見た太陽の赤道直交座標
    double Gx = Xs - Xm;
    double Gy = Ys - Ym;
    double Gz = Zs - Zm;

    double tana=  Gy / Gx;
    double tand =Gz / Math.sqrt(Gx * Gx + Gy * Gy);

    //月影軸方向の赤経･赤緯
    ascension = Math.atan2(Gy , Gx);
    declination = Math.atan2(Gz , Math.sqrt(Gx * Gx + Gy * Gy));
    g = Math.sqrt(Gx * Gx + Gy * Gy + Gz * Gz) * Constants.AUde;

    //地球半径単位への変換
    Xm *= Constants.AUde;
    Ym *= Constants.AUde;
    Zm *= Constants.AUde;

    //変換行列を生成(縦配置)
    double[][] transmatrix = new double[3][3];

    double[] coordinate1 = new double[] { Xm ,Ym, Zm };
    double[] coordinate2 = new double[3];

    double angle = ascension + (Math.PI / 2.0);
    transmatrix[0][0]= Math.cos(angle);
    transmatrix[0][1]= -Math.sin(angle);
    transmatrix[0][2]= 0.0;
    transmatrix[1][0]= Math.sin(angle);
    transmatrix[1][1]= Math.cos(angle);
    transmatrix[1][2]= 0.0;
    transmatrix[2][0]= 0.0;
    transmatrix[2][1]= 0.0;
    transmatrix[2][2]= 1.0;
    Matrix.multiplication31type2(transmatrix, coordinate1, coordinate2);

    angle = (Math.PI / 2.0) - declination;
    transmatrix[0][0]= 1.0 ;
    transmatrix[0][1]= 0.0 ;
    transmatrix[0][2]= 0.0;
    transmatrix[1][0]= 0.0;
    transmatrix[1][1]= Math.cos(angle);
    transmatrix[1][2]= -Math.sin(angle);
    transmatrix[2][0]= 0.0;
    transmatrix[2][1]= Math.sin(angle);
    transmatrix[2][2]= Math.cos(angle);
    Matrix.multiplication31type2(transmatrix, coordinate2, coordinate1);

    x0 = coordinate1[0];
    y0 = coordinate1[1];
    z0 = coordinate1[2];

    double d = Constants.Dsun + Constants.Dmoon;
    tanf1 = d / Math.sqrt(g * g - d * d);
    c1 = z0 + (g * Constants.Dmoon) / d;

    d = Constants.Dsun - Constants.Dmoon;
    tanf2 = d / Math.sqrt(g * g - d * d);
    c2 = z0 - (g * Constants.Dmoon) / d;

    l1 = c1 * tanf1;
    l2 = c2 * tanf2;

    //グリニッジ恒星時を計算する
    siderealtime = Almanac.getGreenidgeSiderealTime(cal);
   }

  //月影軸方向の赤経(ラジアン)
  public double getAscension(){ return ascension; }

  //月影軸方向の赤緯(ラジアン)
  public double getDeclination(){ return declination; }

  //月の中心から太陽中心までの距離
  public double getG(){ return g; }

  //月中心のX座標
  public double getX0(){ return x0; }

  //月中心のY座標
  public double getY0(){ return y0; }

  //月中心のZ座標
  public double getZ0(){ return z0; }

  //月の半影円錐の半頂角
  public double getTanf1(){ return tanf1; }

  //月の本影円錐の半頂角
  public double getTanf2(){ return tanf2; }

  //月の半影円錐の頂点V1の基準座標系によるZ座標
  public double getC1(){ return c1; }

  //月の半影円錐の頂点V2の基準座標系によるZ座標
  public double getC2(){ return c2; }

  //基準面における月の半影の半径
  public double getL1(){ return l1; }

  //基準面における月の本影の半径
  public double getL2(){ return l2; }

  //恒星時
  public double getGreenidgeSiderealTime() { return siderealtime; }
 }
