package com.apogee.surveydemo.Sattelite;

public class AzimElev {

    public float azimuth;
    public float elevation;

    public float getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public AzimElev(float azim, float elev) {
        this.azimuth = azim;
        this.elevation = elev;
    }
}
