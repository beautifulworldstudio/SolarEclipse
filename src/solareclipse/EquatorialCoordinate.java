package solareclipse;

public class EquatorialCoordinate
{
 private double right_ascension;//赤経
 private double celestial_declination;//赤緯
 private double distance; //距離


 //赤経を返す
 public double getRightAscension()
  {
   return right_ascension;
  }

 //赤緯を返す
 public double getCelestialDeclination()
  {
   return celestial_declination;
  }

 //赤経をセットする
 public boolean setRightAscension(double val)
  {
   if (val < 0.0 | val > 360.0) return false;

   right_ascension = val;

   return true;
  }

 //赤緯をセットする
 public boolean setCelestialDeclination(double val)
  {
   if (val < -90.0 | val > 90.0) return false;

   celestial_declination = val;

   return true;
  }

 //距離を返す
 public double getDistance()
  {
   return distance;
  }

 //距離をセットする
 public boolean setDistance(double val)
  {
   if (val < 0.0) return false;

   distance = val;

   return true;
  }
}
