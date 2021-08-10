package com.apogee.surveydemo;


import java.util.Locale;
/*Converter of UTM data*/
public class LatLon2UTM {

    private final double a;
    private final double e;
    private final double esq;
    private final double e0sq;
    final char[] digraphArrayN;
    int longitudeZoneValue;
    int latitudeZoneValue;
    double eastingValue;
    double northingValue;
    double latitude;
    double longitude;

    /*Latlong to UTM conversion method*/
    public LatLon2UTM() {
        double equatorialRadius = 6378137;
        double flattening = 298.2572235630;
        a = equatorialRadius;
        double f = 1 / flattening;
        double b = a * (1 - f);   // polar radius
        e = Math.sqrt(1 - Math.pow(b, 2) / Math.pow(a, 2));
        esq = (1 - (b / a) * (b / a));
        e0sq = e * e / (1 - Math.pow(e, 2));
        digraphArrayN = new char[]{
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
        };
    }


    /*FInal method which returns Easting-Northing and zone*/
    public String convertLatLonToUTM(double latitude, double longitude) {
        verifyLatLon(latitude, longitude);
        convert(latitude, longitude);
        return String.format(Locale.getDefault(), "%d%c %f %f",longitudeZoneValue, digraphArrayN[latitudeZoneValue], eastingValue , northingValue);
    }


    void convert(double latitude, double longitude) {
        double latRad = latitude * Math.PI / 180.0;
        double utmz = 1 + Math.floor((longitude + 180) / 6); // utm zone
        double zcm = 3 + 6 * (utmz - 1) - 180; // central meridian of a zone
        double latz = 0; // zone A-B for below 80S
        // convert latitude to latitude zone
        if (latitude > -80 && latitude < 72) {
            latz = Math.floor((latitude + 80) / 8) + 2; // zones C-W
        } else {
            if (latitude > 72 && latitude < 84) {
                latz = 21; // zone X
            } else {
                if (latitude > 84) {
                    latz = 23; // zones Y-Z
                }
            }
        }

        double N = a / Math.sqrt(1 - Math.pow(e * Math.sin(latRad), 2));
        double T = Math.pow(Math.tan(latRad), 2);
        double C = e0sq * Math.pow(Math.cos(latRad), 2);
        double A = (longitude - zcm) * Math.PI / 180.0 * Math.cos(latRad);

        // calculate M (USGS style)
        double M = latRad * (1.0 - esq * (1.0 / 4.0 + esq * (3.0 / 64.0 + 5.0 * esq / 256.0)));
        M = M - Math.sin(2.0 * latRad) * (esq * (3.0 / 8.0 + esq * (3.0 / 32.0 + 45.0 * esq / 1024.0)));
        M = M + Math.sin(4.0 * latRad) * (esq * esq * (15.0 / 256.0 + esq * 45.0 / 1024.0));
        M = M - Math.sin(6.0 * latRad) * (esq * esq * esq * (35.0 / 3072.0));
        M = M * a; //Arc length along standard meridian

        // calculate easting
        double k0 = 0.9996;
        double x = k0 * N * A * (1.0 + A * A * ((1.0 - T + C) / 6.0 + A * A * (5.0 - 18.0 * T + T * T + 72.0 * C - 58.0 * e0sq) / 120.0)); //Easting relative to CM
        x = x + 500000; // standard easting

        // calculate northing
        double y = k0 * (M + N * Math.tan(latRad) * (A * A * (1.0 / 2.0 + A * A * ((5.0 - T + 9.0 * C + 4.0 * C * C) / 24.0 + A * A * (61.0 - 58.0 * T + T * T + 600.0 * C - 330.0 * e0sq) / 720.0)))); // from the equator

        if (y < 0) {
            y = 10000000 + y; // add in false northing if south of the equator
        }

        longitudeZoneValue = (int) utmz;
        latitudeZoneValue = (int) latz;
        eastingValue = x;
        northingValue = y;
    }

    /*Verifying is lat long correct or not.*/
    void verifyLatLon(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0 || longitude < -180.0
                || longitude >= 180.0) {
            throw new IllegalArgumentException(
                    "Legal ranges: latitude [-90,90], longitude [-180,180).");
        }
    }

    /*UTM to Degree converter converts Easting northing to Lat long according to zone*/
    public String UTM2Deg(String UTM) {
            String[] parts = UTM.split(" ");
            int Zone = Integer.parseInt(parts[0]);
            char Letter = parts[1].toUpperCase(Locale.ENGLISH).charAt(0);
            double Easting = Double.parseDouble(parts[2]);
            double Northing = Double.parseDouble(parts[3]);
            double Hem;
            if (Letter > 'M')
                Hem = 'N';
            else
                Hem = 'S';
            double north;
            if (Hem == 'S')
                north = Northing - 10000000;
            else
                north = Northing;
            latitude = (north / 6366197.724 / 0.9996 + (1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2) - 0.006739496742 * Math.sin(north / 6366197.724 / 0.9996) * Math.cos(north / 6366197.724 / 0.9996) * (Math.atan(Math.cos(Math.atan((Math.exp((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))) * (1 - 0.006739496742 * Math.pow((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))), 2) / 2 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2) / 3)) - Math.exp(-(Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))) * (1 - 0.006739496742 * Math.pow((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))), 2) / 2 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2) / 3))) / 2 / Math.cos((north - 0.9996 * 6399593.625 * (north / 6366197.724 / 0.9996 - 0.006739496742 * 3 / 4 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.pow(0.006739496742 * 3 / 4, 2) * 5 / 3 * (3 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 4 - Math.pow(0.006739496742 * 3 / 4, 3) * 35 / 27 * (5 * (3 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 4 + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 3)) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))) * (1 - 0.006739496742 * Math.pow((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))), 2) / 2 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) + north / 6366197.724 / 0.9996))) * Math.tan((north - 0.9996 * 6399593.625 * (north / 6366197.724 / 0.9996 - 0.006739496742 * 3 / 4 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.pow(0.006739496742 * 3 / 4, 2) * 5 / 3 * (3 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 4 - Math.pow(0.006739496742 * 3 / 4, 3) * 35 / 27 * (5 * (3 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 4 + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 3)) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))) * (1 - 0.006739496742 * Math.pow((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))), 2) / 2 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) + north / 6366197.724 / 0.9996)) - north / 6366197.724 / 0.9996) * 3 / 2) * (Math.atan(Math.cos(Math.atan((Math.exp((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))) * (1 - 0.006739496742 * Math.pow((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))), 2) / 2 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2) / 3)) - Math.exp(-(Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))) * (1 - 0.006739496742 * Math.pow((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))), 2) / 2 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2) / 3))) / 2 / Math.cos((north - 0.9996 * 6399593.625 * (north / 6366197.724 / 0.9996 - 0.006739496742 * 3 / 4 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.pow(0.006739496742 * 3 / 4, 2) * 5 / 3 * (3 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 4 - Math.pow(0.006739496742 * 3 / 4, 3) * 35 / 27 * (5 * (3 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 4 + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 3)) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))) * (1 - 0.006739496742 * Math.pow((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))), 2) / 2 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) + north / 6366197.724 / 0.9996))) * Math.tan((north - 0.9996 * 6399593.625 * (north / 6366197.724 / 0.9996 - 0.006739496742 * 3 / 4 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.pow(0.006739496742 * 3 / 4, 2) * 5 / 3 * (3 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 4 - Math.pow(0.006739496742 * 3 / 4, 3) * 35 / 27 * (5 * (3 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 4 + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 3)) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))) * (1 - 0.006739496742 * Math.pow((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))), 2) / 2 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) + north / 6366197.724 / 0.9996)) - north / 6366197.724 / 0.9996)) * 180 / Math.PI;
            latitude = Math.round(latitude * 10000000);
            latitude = latitude / 10000000;
            longitude = Math.atan((Math.exp((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))) * (1 - 0.006739496742 * Math.pow((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))), 2) / 2 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2) / 3)) - Math.exp(-(Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))) * (1 - 0.006739496742 * Math.pow((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))), 2) / 2 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2) / 3))) / 2 / Math.cos((north - 0.9996 * 6399593.625 * (north / 6366197.724 / 0.9996 - 0.006739496742 * 3 / 4 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.pow(0.006739496742 * 3 / 4, 2) * 5 / 3 * (3 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 4 - Math.pow(0.006739496742 * 3 / 4, 3) * 35 / 27 * (5 * (3 * (north / 6366197.724 / 0.9996 + Math.sin(2 * north / 6366197.724 / 0.9996) / 2) + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 4 + Math.sin(2 * north / 6366197.724 / 0.9996) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2) * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) / 3)) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))) * (1 - 0.006739496742 * Math.pow((Easting - 500000) / (0.9996 * 6399593.625 / Math.sqrt((1 + 0.006739496742 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)))), 2) / 2 * Math.pow(Math.cos(north / 6366197.724 / 0.9996), 2)) + north / 6366197.724 / 0.9996)) * 180 / Math.PI + Zone * 6 - 183;
            longitude = Math.round(longitude * 10000000);
            longitude = longitude / 10000000;

            return String.valueOf(latitude)+","+String.valueOf(longitude);
    }

    /*Distance calculation*/

    public double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }




  /*  *//*LatLong conversion DDMM to degree*//*
    public String latlngcnvrsn(String lat , String lon){
        String latitude1 = lat;
        String arr1[] = latitude1.split("\\.");
        String beforePoint = arr1[0];
        String firsthalf = beforePoint.substring(0, 2);
        String secondhalf = beforePoint.substring(2, 4);
        String afterPoint = arr1[1];
        String finalSubString = (secondhalf + afterPoint);
        double getval = Double.parseDouble(finalSubString)/60;
        int value = (int)Math.round(getval);
        String afterMultiply = Integer.toString(value);
        String finalString = firsthalf + "." + afterMultiply;
//       Double latitude = new Double(finalString);
        String longitude1 = lon;
        String arr2[] = longitude1.split("\\.");
        String beforePoint2 = arr2[0];
        String firsthalf2 = beforePoint2.substring(0, 3);
        String secondhalf2 = beforePoint2.substring(3, 5);
        String afterPoint2 = arr2[1];
        String finalSubString2 = (secondhalf2 + afterPoint2);
        double getval2 = Double.parseDouble(finalSubString2)/60;
        int value2 =(int)Math.round(getval2);
        String afterMultiply2 = Integer.toString(value2);
        String finalString2 = firsthalf2 + "." + afterMultiply2;
//       Double longitude = new Double(finalString2);
        return finalString+"_"+finalString2;
    }*/

    /*LatLong conversion DDMM to degree*/
    public String latlngcnvrsn(String lat , String lon){
        String latitude1 = lat;
        int length = latitude1.length();
        String firsthalf = latitude1.substring(0, 2);
        String secondhalf = latitude1.substring(2, length);
        double secondhalfff = Double.parseDouble(secondhalf);
        double getval = secondhalfff/60;
        double finalval = Double.parseDouble(firsthalf)+getval;
        String finalString = String.valueOf(finalval);
        String longitude1 = lon;
        int length2 = longitude1.length();
        String firsthalf2 = longitude1.substring(0, 3);
        String secondhalf2 = longitude1.substring(3, length2);
        double secondhalfff2 = Double.parseDouble(secondhalf2);
        double getval2 = secondhalfff2/60;
        double finalval2 = Double.parseDouble(firsthalf2)+getval2;
        String finalString2 = String.valueOf(finalval2);
        return finalString+"_"+finalString2;
    }

    public String latlngcnvrsnNew(String lat , String lon){
        String latitude1 = lat;
        int length = latitude1.length();
        String firsthalf = latitude1.substring(0, 2);
        String secondhalf = latitude1.substring(2, length);
        double secondhalfff = Double.parseDouble(secondhalf);
        double getval = secondhalfff/60;
        double finalval = Double.parseDouble(firsthalf)+getval;
        String finalString = String.valueOf(finalval);
        String longitude1 = lon;
        int length2 = longitude1.length();
        String firsthalf2 = longitude1.substring(0, 2);
        String secondhalf2 = longitude1.substring(2, length2);
        double secondhalfff2 = Double.parseDouble(secondhalf2);
        double getval2 = secondhalfff2/60;
        double finalval2 = Double.parseDouble(firsthalf2)+getval2;
        String finalString2 = String.valueOf(finalval2);
        return finalString+"_"+finalString2;
    }


    public String twosCompliment(String bin) {
        String twos = "", ones = "";

        for (int i = 0; i < bin.length(); i++) {
            ones += flip(bin.charAt(i));
        }
        int number0 = Integer.parseInt(ones, 2);
        StringBuilder builder = new StringBuilder(ones);
        boolean b = false;
        for (int i = ones.length() - 1; i > 0; i--) {
            if (ones.charAt(i) == '1') {
                builder.setCharAt(i, '0');
            } else {
                builder.setCharAt(i, '1');
                b = true;
                break;
            }
        }
        if (!b)
            builder.append("1", 0, 7);

        twos = builder.toString();

        return twos;
    }

    // Returns '0' for '1' and '1' for '0'
    public char flip(char c) {
        return (c == '0') ? '1' : '0';
    }

     String hexToBin(String s) {
       long num = Long.parseLong(s, 16);
         String binary = Long.toBinaryString(num);
       // return new BigInteger(s, 16).toString(2);
        return binary;
    }

  }
