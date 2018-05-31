package solareclipse;

import matrix.Matrix;

public class Coordinate
 {
  //国際天文基準座標系 (International Celestial Reference System、略号ICRS)
  //地心座標系 geocentric coordinate system
  private Coordinate(){}

  public static void transformICRStoGCS(VesselElements ve, double[] point , double[] result)
   {
    if (result.length != 2 ) return;
    double  d = ve.getDeclination();//ラジアン

   //変換行列を生成(縦配置)
    double[][] transmatrix = new double[3][3];

    double[] coordinate1 = new double[] { point[0], point[1], point[2] };
    double[] coordinate2 = new double[3];

    double angle = d - (Math.PI / 2.0);
    transmatrix[0][0]= 1.0 ;
    transmatrix[0][1]= 0.0 ;
    transmatrix[0][2]= 0.0;
    transmatrix[1][0]= 0.0;
    transmatrix[1][1]= Math.cos(angle);
    transmatrix[1][2]= -Math.sin(angle);
    transmatrix[2][0]= 0.0;
    transmatrix[2][1]= Math.sin(angle);
    transmatrix[2][2]= Math.cos(angle);
    Matrix.multiplication31type2(transmatrix, coordinate1, coordinate2);

    angle = (ve.getGreenidgeSiderealTime() / 180.0 * Math.PI) - ve.getAscension() - (Math.PI / 2.0);
    transmatrix[0][0]= Math.cos(angle);
    transmatrix[0][1]= -Math.sin(angle);
    transmatrix[0][2]= 0.0;
    transmatrix[1][0]= Math.sin(angle);
    transmatrix[1][1]= Math.cos(angle);
    transmatrix[1][2]= 0.0;
    transmatrix[2][0]= 0.0;
    transmatrix[2][1]= 0.0;
    transmatrix[2][2]= 1.0;
    Matrix.multiplication31type2(transmatrix, coordinate2, coordinate1);

    result[0] = Math.atan2(coordinate1[1] , coordinate1[0]) / Math.PI * 180.0; //経度

    //緯度を求める
    double denominator = Math.sqrt(coordinate1[0] * coordinate1[0] + coordinate1[1] * coordinate1[1]); //root(u* u + v* v);
    double coequation1 = coordinate1[2] / denominator;
    double coequation2 = Constants.e2 / denominator;
    double tan_phai = coequation1;
    double lasttan_phai  = -tan_phai;//tan_phaiと一致させないための工夫。
    int count = 0;

    while( Math.abs(tan_phai - lasttan_phai) > 10e-7)
     {
      lasttan_phai = tan_phai;
      tan_phai = coequation1+ coequation2 * (lasttan_phai / Math.sqrt(1.0 + (1.0 - Constants.e2) * lasttan_phai * lasttan_phai));

      if (count++ > 10) break;
     }
    result[1] = Math.atan(tan_phai) / Math.PI * 180.0; //緯度
   }
 }

