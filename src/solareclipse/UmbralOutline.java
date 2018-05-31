package solareclipse;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import astronomy.VesselElements;
import matrix.Matrix;


public class UmbralOutline extends JPanel implements WindowListener, Runnable
 {
  private JFrame window;
  private BufferedImage earth;
  private BufferedImage screen;
  private BufferedImage screen2;
  private double scrAngle = 30.0;
  private int imageWidth;
  private int imageHeight;
  private int scrWidth;
  private int scrHeight;
  private double longitudeleft = -29.6;
  private int Yequator = 198;
  private TimeZone tz;
  private Date finish;
  private Calendar targettime;
  private boolean painted;

  private static final double Altitude = 17000; //高度
  private double[] viewpoint; // = new double[]{ -11581.09, 11581.09,0.0};
  private double[][] camvec ; // = new double[][]{{-Math.cos(Math.PI / 4.0), -Math.sin(Math.PI / 4.0), 0.0}, { Math.cos(Math.PI / 4.0), -Math.sin(Math.PI / 4.0), 0.0}, { 0.0, 0.0, 1.0 }};
  private double InverseCam[][];

  public UmbralOutline(Calendar starttime, Date endtime) throws IOException
   {
    earth = ImageIO.read(new File("C:\\Users\\Kentaro\\Documents\\java\\astronomy\\map.png"));
    imageWidth = earth.getWidth();
    imageHeight = earth.getHeight();
    screen2 = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
    scrWidth = 800;
    scrHeight = 600;
    screen = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
    initCameraVector(100, 60);

    targettime = starttime;
    finish = endtime;
    tz = TimeZone.getTimeZone("UTC");
    painted = false;

    window = new JFrame("Umbral outline");
    window.getContentPane().add(this);
    window.addWindowListener(this);
    window.setSize(800, 600);
    window.setVisible(true);

    createScreen2();//球体地図を生成する。
   }

  public void windowActivated(WindowEvent w){}
  public void windowDeactivated(WindowEvent w){}
  public void windowIconified(WindowEvent w){}
  public void windowDeiconified(WindowEvent w){}
  public void windowOpened(WindowEvent w){}
  public void windowClosed(WindowEvent w){}

  public void windowClosing(WindowEvent w)
   {
    System.exit(0);
   }

  @Override
  public void run()
   {
    while(true)
     {
      if (!painted) continue;
      try
       {
        Thread.sleep(100);
       }
      catch(Exception e){ break;}

      targettime.setTimeZone(tz);// タイムゾーンをUTCに

      int Day = targettime.get(Calendar.DAY_OF_MONTH);
      int Hour = targettime.get(Calendar.HOUR_OF_DAY);
      int Minute = targettime.get(Calendar.MINUTE);

      Minute += 3;
      if(Minute >= 60)
       {
        Minute -= 60;
        Hour += 1;
        if (Hour >= 24)
         {
          Hour -= 24;
          Day += 1;
         }
       }
      targettime.set(Calendar.DAY_OF_MONTH, Day);
      targettime.set(Calendar.HOUR_OF_DAY, Hour);
      targettime.set(Calendar.MINUTE, Minute);
      targettime.get(Calendar.DAY_OF_MONTH); //ダミー
      Date newtime = targettime.getTime();

      if (finish.before(newtime)) break;

      painted = false;
      updateScreen2();
     }
   }


  //球体版のカメラの変換行列を設定する。
  private void initCameraVector(double longitude, double latitude)
   {
    viewpoint = new double[3];
    double[] xaxis = new double[3];
    double[] position = new double[3];

    double phai = longitude / 180.0 * Math.PI;
    double rho = latitude / 180.0 * Math.PI;

    //緯度に基づいて回転させる
    position[0] = Altitude * Math.cos(rho);
    position[1] = 0.0;
    position[2] = Altitude * Math.sin(rho);

    //Z軸に基づいて回転させる。
    double[][] Zrevolve = new double[][]{{Math.cos(phai), Math.sin(phai), 0.0 } , { -Math.sin(phai), Math. cos(phai), 0.0 }, { 0.0, 0.0, 1.0 }};
    Matrix.multiplication31type2(Zrevolve, position, viewpoint);

    //接線ベクトルを計算する
    position[0] = 0.0;
    position[1] = 1.0;
    position[2] = 0.0;

//    double[][] Xrevolve = new double[][]{{1.0, 0.0, 0.0 }, { 0.0, Math.cos(rho), -Math.sin(rho) }, {0.0 Math.sin(rho), Math. cos(rho) }};

    Matrix.multiplication31type2(Zrevolve, position, xaxis);

    double norm = Math.sqrt(viewpoint[0] * viewpoint[0] + viewpoint[1] * viewpoint[1] + viewpoint[2] * viewpoint[2]);
    double[] yaxis = new double[]{ -(viewpoint[0] / norm) , -(viewpoint[1] / norm), -(viewpoint[2] / norm)};

    double[] zaxis = new double[3];
    Matrix.getOuterProduct(xaxis, yaxis, zaxis);

    norm = Math.sqrt(zaxis[0] * zaxis[0] + zaxis[1] * zaxis[1] + zaxis[2] * zaxis[2]);
    zaxis[0] /= norm;
    zaxis[1] /= norm;
    zaxis[2] /= norm;


    camvec = new double[][]{xaxis, yaxis, zaxis };
    InverseCam = new double[3][3];
    Matrix.getInverseMatrixType2(camvec, InverseCam);
   }

  //画面を更新する(球体版)
  private void createScreen2()
   {
    double hinode_keido, hinoiri_keido, asayake, higure;
    int hinoiriX=0, hinodeX=0;
    int asayakeX=0, higureX=0;

    if (screen2 == null | earth == null) return;

   //イメージを初期化
    for (int i = 0; i < imageWidth; i++)
     {
      for (int j = 0; j < imageHeight; j++)
       {
        screen2.setRGB(i, j, 0);
       }
     }

    double scrDist = (scrWidth / 2.0) / Math.tan(scrAngle / 180.0 * Math.PI);
    double [] vector = new double[3];
    double [] result = new double[3];

    double[] center = new double[] { viewpoint[0], viewpoint[1], viewpoint[2] }; //地球中心へ向かうベクトル
    double norm = Math.sqrt(center[0] * center[0] + center[1] * center[1] + center[2] * center[2]);
    center[0] /= norm;
    center[1] /= norm;
    center[2] /= norm;

    double altitude = Math.sqrt(viewpoint[0] * viewpoint[0] + viewpoint[1] * viewpoint[1] + viewpoint[2] * viewpoint[2]);

   for (int i = 0; i < scrWidth; i++)
    {
     double x = i - (scrWidth / 2);
     double y = scrDist;
     for (int j = 0; j < scrHeight; j++)
      {
       vector[0] = x;
       vector[1] = y;
       vector[2] = -(j - (scrHeight / 2));
       norm = Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
       vector[0] /= norm;
       vector[1] /= norm;
       vector[2] /= norm;
       Matrix.multiplication31type2(camvec, vector, result);

       double theta = Math.acos(-center[0] * result[0] - center[1] * result[1] - center[2] * result[2]);

       double b2 = 2.0 * altitude * Math.cos(theta); //コサインを変換する必要ないのでは?
       double D = b2 * b2 - 4.0 * (altitude * altitude - Constants.de * Constants.de);

       if (D >= 0)
        {
         double root = 0.5 * Math.sqrt(D);

         double distance = altitude * Math.cos(theta) - root;
         if (distance < 0) { /* System.out.println("負の実数解");*/;  return;  }

         //x1は交点までの距離。方向ベクトルに距離を掛けると位置が出る
         double pointX = viewpoint[0] + result[0] * distance;
         double pointY = viewpoint[1] + result[1] * distance;
         double pointZ = viewpoint[2] + result[2] * distance;

         double latitude = Math.asin(pointZ / Constants.de) / Math.PI * 180.0;
         double longitude = Math.acos(pointX / Math.sqrt(pointX * pointX + pointY * pointY))/ Math.PI * 180.0;
         if (pointY < 0) longitude = -longitude; //象限を考慮する

         double xOnMap = longitude - longitudeleft;
         if (xOnMap < 0.0) xOnMap += 360.0;
         int x1 = (int)(xOnMap * (imageWidth / 360.0));

         double yOnMap = (imageHeight / 180.0) * latitude;
         int y1 = Yequator - (int)yOnMap;
         if (x1 < earth.getWidth() & y1 < earth.getHeight()) screen2.setRGB(i, j, earth.getRGB(x1, y1));
        }
      }
    }
  }


  //画面を更新する(球体地図)
  public void updateScreen2()
   {
    double hinode_keido, hinoiri_keido, asayake, higure;
    int hinoiriX=0, hinodeX=0;
    int asayakeX=0, higureX=0;
    double x , y;
    double halfPI = Math.PI / 2.0;
    double scrDist = (scrWidth / 2.0) / Math.tan(scrAngle / 180.0 * Math.PI);

   if (screen == null | screen2 == null) return;

    //イメージを初期化
    for (int i = 0; i < scrWidth; i++)
     {
      for (int j = 0; j < scrHeight; j++)
       {
        screen.setRGB(i, j, screen2.getRGB(i, j));
       }
     }

    EquatorialCoordinate sun = new EquatorialCoordinate();
    EquatorialCoordinate moon = new EquatorialCoordinate();

    double[] result = new double[3];
    double[] result2 = new double[3];
    double[] location = new double[2];

    try
     {
      StarPosition.getSunRightAscension(targettime, result);
      System.out.println("Sun RightAscension = " + result[0]);
      sun.setRightAscension(result[0]);
      sun.setCelestialDeclination(result[1]);
      sun.setDistance(result[2]);

      StarPosition.getMoonRightAscension(targettime, result);
      System.out.println("Moon RightAscension = " + result[0]);
      moon.setRightAscension(result[0]);
      moon.setCelestialDeclination(result[1]);
      moon.setDistance(result[2]);
     }
    catch(IllegalArgumentException e){ return ;}


   //日の出･日の入りの同時線を描く
    for(int i = -90;i < 90; i++)
     {
      //緯度を取得
      double latitude = i;//getLatitudeFromY(Yequator - i);
      double phai0 =  Almanac.getGreenidgeSiderealTime(targettime);//グリニッジ恒星時

      double dist = sun.getDistance();
      double parallax = StarPosition.getSunParallax(dist);//太陽視差
      double k = StarPosition.getSunriseAltitude(StarPosition.getSunDiameter(dist), 0.0, StarPosition.refraction, parallax);

      //緯度を元に時角を計算する
      double jikaku = StarPosition.getTimeAngle(k, sun.getCelestialDeclination(), latitude);

      if(!Double.isNaN(jikaku))//時角がNaNでない
       {
        hinode_keido = StarPosition.reviseAngle(-jikaku + sun.getRightAscension() - phai0);
        hinoiri_keido = StarPosition.reviseAngle(jikaku + sun.getRightAscension() - phai0);
     //   hinodeX =(int)getXfromLongitude(hinode_keido);
     //   hinoiriX = (int)getXfromLongitude(hinoiri_keido);//昼側か調べる
        drawRoutine(hinode_keido, latitude, 0xffffff);
        drawRoutine(hinoiri_keido, latitude, 0xffffff);
       }
     }

    //輪郭の描画
    VesselElements ve = new VesselElements(sun, moon, targettime);
    SolarEclipse.getCrossPoint(ve, result, result2);
    double maxQ = SolarEclipse.getPenumbralQ(ve, result);
    double minQ = SolarEclipse.getPenumbralQ(ve, result2);

    //半影の描画
    if (Double.isNaN(maxQ) && Double.isNaN(minQ))
     {
      for ( double i = 0.0; i <= 360.0 ; i+= 0.2)
       {
        SolarEclipse.getPenumbralOutline(ve,  i, result);
        if ( Double.isNaN(result[0]) |  Double.isNaN(result[1]) |  Double.isNaN(result[2])) continue;//NaNが含まれていたらスキップする
        Coordinate.transformICRStoGCS(ve, result, location);

        drawRoutine(location[0], location[1], 0xff0000);
       }
     }
   else
     {
      if ((maxQ - minQ) >= 0.0) maxQ -= 360.0;
      //maxQが通常の計算でNaNとなる場合に備えて、強制的に描画する。
      SolarEclipse.getPenumbralOutline(ve,  maxQ, result);
      result[2] = -0.01;//強制的に基準面に設定する
      Coordinate.transformICRStoGCS(ve, result, location);
      drawRoutine(location[0], location[1], 0x00);

      for ( double i = Math.ceil(maxQ); i < minQ ; i+= 0.2)
       {
        SolarEclipse.getPenumbralOutline(ve,  i, result);
        if ( Double.isNaN(result[0]) |  Double.isNaN(result[1]) |  Double.isNaN(result[2])) continue;//NaNが含まれていたらスキップする
        Coordinate.transformICRStoGCS(ve, result, location);
        drawRoutine(location[0], location[1], 0xff0000);
       }

      //minQが通常の計算でNaNとなる場合に備えて、強制的に描画する。
      SolarEclipse.getPenumbralOutline(ve,  minQ, result);
      result[2] = -0.01;//強制的に基準面に設定する
      Coordinate.transformICRStoGCS(ve, result, location);
      drawRoutine(location[0], location[1], 0x00);
     }


   //本影の描画
    for ( int i = 0; i <= 360; i+= 5)
     {
      SolarEclipse.getUmbralOutline(ve,  (double)i, result);
      if ( Double.isNaN(result[0]) |  Double.isNaN(result[1]) |  Double.isNaN(result[2])) continue;//NaNが含まれていたらスキップする

      Coordinate.transformICRStoGCS(ve, result, location);
      drawRoutine(location[0], location[1], 0x00);
     }

    repaint();
   }

  private void drawRoutine(double longitude, double latitude, int color)
   {
    double halfPI = Math.PI / 2.0;
    double scrDist = (scrWidth / 2.0) / Math.tan(scrAngle / 180.0 * Math.PI);

        double Z = Math.sin(latitude / 180.0 * Math.PI) * Constants.de;//緯度からZ座標を計算
        double X = Math.cos(longitude / 180.0 * Math.PI)  * Math.cos(latitude / 180.0 * Math.PI) *  Constants.de;
        double Y = Math.sin(longitude / 180.0 * Math.PI)  * Math.cos(latitude / 180.0 * Math.PI) *  Constants.de;

        double Vx = viewpoint[0] - X;
        double Vy = viewpoint[1] - Y;
        double Vz = viewpoint[2] - Z;
        double norm = Math.sqrt(Vx * Vx + Vy * Vy + Vz * Vz);
        Vx /= norm;
        Vy /= norm;
        Vz /= norm;

        norm = Math.sqrt(X * X + Y * Y + Z * Z);
        double pointX = X / norm;
        double pointY = Y / norm;
        double pointZ = Z / norm;

        double centerangle = Math.acos(-pointX * Vx - pointY * Vy - pointZ * Vz);
        if (centerangle < halfPI) return;
//System.out.println("centerangle = " + centerangle);
        double difX = X - viewpoint[0];
        double difY = Y - viewpoint[1];
        double difZ = Z - viewpoint[2];

        norm = Math.sqrt(difX * difX + difY * difY + difZ * difZ);
        difX /= norm;
        difY /= norm;
        difZ /= norm;
//本来は逆行列で変換する。
        double screenpoint[] = new double[3];
        double XYZpoint[] = new double[]{ difX, difY, difZ };
        Matrix.multiplication31type2(InverseCam, XYZpoint, screenpoint);

        double ratio =  scrDist / screenpoint[1] ;
        double screenX = screenpoint[0] * ratio + (scrWidth /2);
        double screenZ = (scrHeight /2) - screenpoint[2] * ratio;

        if (screenX > 0.0 & screenX < scrWidth & screenZ > 0.0 & screenZ < scrWidth)
         {
          screen.setRGB((int)screenX, (int)screenZ, color);
         }
   }


 //画面を更新する
  public void updateScreen()
   {
    double hinode_keido, hinoiri_keido, asayake, higure;
    int hinoiriX=0, hinodeX=0;
    int asayakeX=0, higureX=0;
    double x , y;

    if (screen == null | earth == null) return;

    //イメージを初期化
    for (int i = 0; i < imageWidth; i++)
     {
      for (int j = 0; j < imageHeight; j++)
       {
        screen.setRGB(i, j, earth.getRGB(i, j));
       }
     }

    Graphics imgG = screen.getGraphics();
    EquatorialCoordinate sun = new EquatorialCoordinate();
    EquatorialCoordinate moon = new EquatorialCoordinate();


    double[] result = new double[3];
    double[] result2 = new double[3];
    double[] location = new double[2];

    try
     {
      StarPosition.getSunRightAscension(targettime, result);

      sun.setRightAscension(result[0]);
      sun.setCelestialDeclination(result[1]);
      sun.setDistance(result[2]);

      StarPosition.getMoonRightAscension(targettime, result);
      moon.setRightAscension(result[0]);
      moon.setCelestialDeclination(result[1]);
      moon.setDistance(result[2]);
     }
    catch(IllegalArgumentException e){ return ;}


    //日の出･日の入りの同時線を描く
    for(int i = 0;i < imageHeight; i++)
     {
      //緯度を取得
      double latitude = getLatitudeFromY(Yequator - i);
      double phai0 =  Almanac.getGreenidgeSiderealTime(targettime);//グリニッジ恒星時

      double dist = sun.getDistance();
      double parallax = StarPosition.getSunParallax(dist);//太陽視差
      double k = StarPosition.getSunriseAltitude(StarPosition.getSunDiameter(dist), 0.0, StarPosition.refraction, parallax);

      //緯度を元に時角を計算する
      double jikaku = StarPosition.getTimeAngle(k, sun.getCelestialDeclination(), latitude);

      if(!Double.isNaN(jikaku))//時角がNaNでない
       {
        hinode_keido = StarPosition.reviseAngle(-jikaku + sun.getRightAscension() - phai0);
        hinoiri_keido = StarPosition.reviseAngle(jikaku + sun.getRightAscension() - phai0);
        hinodeX =(int)getXfromLongitude(hinode_keido);
        hinoiriX = (int)getXfromLongitude(hinoiri_keido);//昼側か調べる

        if (hinodeX < imageWidth ) screen.setRGB(hinodeX, i, 0xff0000);
        if (hinodeX < imageWidth ) screen.setRGB(hinoiriX, i, 0xff0000);
       }
     }

    //輪郭の描画
    VesselElements ve = new VesselElements(sun, moon, targettime);
    SolarEclipse.getCrossPoint(ve, result, result2);
    double maxQ = SolarEclipse.getPenumbralQ(ve, result);
    double minQ = SolarEclipse.getPenumbralQ(ve, result2);
      int x1= 0, y1= 0 ;
    //半影の描画
    if (Double.isNaN(maxQ) && Double.isNaN(minQ))
     {
      for ( double i = 0.0; i <= 360.0 ; i+= 3.0)
       {
        SolarEclipse.getPenumbralOutline(ve,  i, result);
        if ( Double.isNaN(result[0]) |  Double.isNaN(result[1]) |  Double.isNaN(result[2])) continue;//NaNが含まれていたらスキップする
        Coordinate.transformICRStoGCS(ve, result, location);

        x = location[0] - longitudeleft;
        if (x < 0.0) x += 360.0;
        int x2 = (int)(x * (imageWidth / 360.0));

        y = (imageHeight / 180.0) * location[1];
        int y2 = Yequator - (int)y;

        //描画
        if (i != 0.0) imgG.drawLine(x1, y1,  x2, y2);

        x1 = x2; y1 = y2;
       }
     }

    else
     {
      if ((maxQ - minQ) >= 0.0) maxQ -= 360.0;
      int x2= 0;
      int y2 = 0;


      SolarEclipse.getPenumbralOutline(ve,  maxQ, result);
      result[2] = 0.0;//強制的に基準面に設定する

      Coordinate.transformICRStoGCS(ve, result, location);
      x = location[0] - longitudeleft;
      if (x < 0.0) x += 360.0;
      x1 = (int)(x * (imageWidth / 360.0));

      y = (imageHeight / 180.0) * location[1];
      y1 = Yequator - (int)y;

      for ( double i = Math.ceil(maxQ); i < minQ ; i+= 3.0)
       {
        SolarEclipse.getPenumbralOutline(ve,  i, result);
        if ( Double.isNaN(result[0]) |  Double.isNaN(result[1]) |  Double.isNaN(result[2])) continue;//NaNが含まれていたらスキップする
        Coordinate.transformICRStoGCS(ve, result, location);

        x = location[0] - longitudeleft;
        if (x < 0.0) x += 360.0;
        x2 = (int)(x * (imageWidth / 360.0));

        y = (imageHeight / 180.0) * location[1];
        y2 = Yequator - (int)y;

        //描画
        imgG.drawLine(x1, y1,  x2, y2);

        x1 = x2; y1 = y2;
       }

      SolarEclipse.getPenumbralOutline(ve,  minQ, result);
      result[2] = 0.0;//強制的に基準面に設定する

      Coordinate.transformICRStoGCS(ve, result, location);
      x = location[0] - longitudeleft;
      if (x < 0.0) x += 360.0;
      x2 = (int)(x * (imageWidth / 360.0));

      y = (imageHeight / 180.0) * location[1];
      y2 = Yequator - (int)y;
      imgG.drawLine(x1, y1,  x2, y2);
     }

    //本影の描画
    imgG.setColor(Color.BLACK);

    SolarEclipse.getUmbralOutline(ve,  0.0, result);
    Coordinate.transformICRStoGCS(ve, result, location);

    x = location[0] - longitudeleft;
    if (x < 0.0) x += 360.0;
    x1 = (int)(x * (imageWidth / 360.0));

    y = (imageHeight / 180.0) * location[1];
    y1 = Yequator - (int)y;

    for ( int i = 60; i <= 360; i+= 60)
     {
      SolarEclipse.getUmbralOutline(ve,  (double)i, result);
      if ( Double.isNaN(result[0]) |  Double.isNaN(result[1]) |  Double.isNaN(result[2])) continue;//NaNが含まれていたらスキップする

      Coordinate.transformICRStoGCS(ve, result, location);
//      System.out.println("location[0] " + location[0] + " location[1] =" +location[1] ) ;

      x = location[0] - longitudeleft;
      if (x < 0.0) x += 360.0;
      int x2 = (int)(x * (imageWidth / 360.0));

      y = (imageHeight / 180.0) * location[1];
      int y2 = Yequator - (int)y;

      imgG.drawLine(x1, y1,  x2, y2);
      x1 = x2; y1 = y2;
     }

    repaint();
   }


  public void paint(Graphics g)
   {
    g.drawImage(screen, 0 , 0, null);
    painted = true;
   }

  //経度からX座標を計算する
  private double getXfromLongitude(double longitude)
   {
    double result = longitude - longitudeleft;

    if (result <= -360.0) { result += Math.ceil(result / 360.0) * 360.0; }
    else if(result >= 360.0) { result -= Math.floor(result / 360.0) *360.0; }

    result = result / 360.0 * imageWidth; //X軸方向の変換

    if( result > imageWidth) result -= imageWidth;
    else if (result < 0) result += imageWidth;

    return result;
   }

  //Y座標から緯度を計算する
  private double getLatitudeFromY(int y)
   {
    return (double)y / (double)Yequator * 90.0;
   }

  public static void main(String args[]) throws IOException
   {
    TimeZone zone = TimeZone.getTimeZone("UTC");

    Calendar utc = Calendar.getInstance(zone);
    utc.set(2035,  8,  2,  4, 0, 0);
//    utc.set(2012,  4, 21,  3,  0, 0);
//    utc.set(2012, 10, 14,  1,  0, 0);
//    utc.set(2013,  4,  10,  1, 30, 0);
//      utc.set(2013, 10,  3, 14, 50, 0);
//      utc.set(2014,  3, 29, 8, 0, 0);
//      utc.set(2016,  2,  9, 4, 0, 0);
//      utc.set(2016,  8,  1, 11, 30, 0);

//    utc.set(2018,  7, 11, 11, 30, 0);
//    utc.set(2019,  0,  6,  3, 40, 0);
//    utc.set(2019, 11, 26,  8,  0, 0);
//     utc.set(2020,  5, 21,  9, 30, 0);
//      utc.set(2028,  6, 22, 6 , 0, 0);
//    utc.set(2030, 10, 25,  9, 00, 0);
//    utc.set(2032, 10,  3, 7, 30, 0);

//    utc.set(2038, 11, 26,  5, 0, 0);


//    utc.set(2030,  5,  1,  9,  0, 0);
    utc.get(Calendar.DAY_OF_MONTH); //ダミー

    Date end = utc.getTime();

    utc.set(2035,  8,  1, 23, 30, 0);

//    utc.set(2012,  4, 20, 21, 30, 0);
//    utc.set(2012, 10, 13, 20, 30, 0);
//    utc.set(2013,  4,  9, 21, 00, 0);
//      utc.set(2013, 10,  3, 10, 10, 0);
//      utc.set(2014,  3, 29, 3, 0, 0);
//    utc.set(2016,  2,  9, 0, 00, 0);
//      utc.set(2016,  8,  1, 7, 0, 0);
//     utc.set(2018,  7, 11,  9, 00, 0);
//    utc.set(2019,  0,  5,  23, 40, 0);
//    utc.set(2019, 11, 26,  3, 40, 0);
//    utc.set(2020,  5, 21,  3, 40, 0);
//    utc.set(2028,  6, 22,  0, 30, 0);
//    utc.set(2030, 10, 25,  4, 30, 0);
//    utc.set(2032, 10,  3, 3, 0, 0);

//    utc.set(2038, 11, 25, 23, 30, 0);

//    utc.set(2030,  5,  1,  5, 30, 0);
    utc.get(Calendar.DAY_OF_MONTH); //ダミー

    Thread th = new Thread(new UmbralOutline(utc, end));
    th.start();
   }
 }
