package com.apogee.surveydemo

import android.content.Context
import android.content.SharedPreferences

class PreferenceStore(val context: Context){
    // Shared Preferences
    var pref: SharedPreferences? = null

    // Editor for Shared preferences
    var editor: SharedPreferences.Editor? = null

    // Context
    var _context: Context?= null

    var appConstants = AppConstants()




    // Constructor
    init{
        this._context = context
        pref = _context!!.getSharedPreferences(appConstants.PREF_NAME, appConstants.PRIVATE_MODE)
        editor = pref!!.edit()
    }



    fun getLatitude(): String {
        return pref!!.getString(appConstants.LATITUDE, "")!!
    }

    fun setLatitude(latitude: String) {

        editor!!.putString(appConstants.LATITUDE, latitude)
        editor!!.commit()
    }

    fun getLongtitude(): String {
        return pref!!.getString(appConstants.LONGTITUDE, "")!!
    }

    fun setLongtitude(longtitude: String) {

        editor!!.putString(appConstants.LONGTITUDE, longtitude)

        editor!!.commit()
    }


    fun setBTConnected(isbtConnect : Boolean){
        editor!!.putBoolean(appConstants.BT_CONNECTED, isbtConnect)
        editor!!.commit()
    }

    fun isbtConnected(): Boolean {
        return pref!!.getBoolean(appConstants.BT_CONNECTED, false)
    }

    fun setIP(ip : String){
        editor!!.putString(appConstants.IP_KEY, ip)
        editor!!.commit()
    }

    fun getIP(): String{
        return pref!!.getString(appConstants.IP_KEY,"")!!
    }

    fun setPort(port : String){
        editor!!.putString(appConstants.PORT_KEY, port)
        editor!!.commit()
    }

    fun getPort() : String{
        return pref!!.getString(appConstants.PORT_KEY,"")!!
    }

    fun setProjectBle(ble_project : String){
        editor!!.putString(appConstants.BLE_PROJECT, ble_project)
        editor!!.commit()
    }

    fun getProjectBle() : String{
        return pref!!.getString(appConstants.BLE_PROJECT,"")!!
    }

}
