package com.apogee.surveydemo.model;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import com.apogee.surveydemo.Database.DatabaseOperation;
import com.apogee.surveydemo.bean.BleBean;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhijeet on 13/09/2019.
 */
public class BleModel {
    Context context;
    String server_ip = null;
    String port = null;
    private static final String TAG = "BleModel.java";

    public static final String IP = "Ipkey";
    public static final String PORT = "Portkey";

    public BleModel(Context context) {

        this.context = context;
        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String ipp = sharedPreferences.getString(IP, "default value");
        String pprt = sharedPreferences.getString(PORT, "default value");
        if(!pprt.equalsIgnoreCase("default value") || !ipp.equalsIgnoreCase("default value")){
            server_ip = ipp;
            port = pprt;
        }
    }

    /*Request to the server for getting BLE and command related data*/

    public long requestBleDetail() {
        long result = 0;
        HttpResponse response = null;
        JSONObject jsonObject = null;
        String data = "";
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HttpPost httppost = null;


            if (server_ip!=null || port!=null){
                httppost = new HttpPost("http://" + server_ip + ":" + port + "/BLE_ProjectV6_2/resources/getAllTableRecords");
            }else {
                httppost = new HttpPost("http://" + "120.138.10.146" + ":" + "8080"+ "/BLE_ProjectV6_2/resources/getAllTableRecords");

            }
            // httppost = new HttpPost("http://" + "192.168.1.22" + ":" + "8080" + "/BLE_project/resources/getAllTableRecords");
             // httppost = new HttpPost("http://" + "45.114.142.35" + ":" + "8090"+ "/BLE_Project/resources/getAllTableRecords");

            HttpParams httpParameters = new BasicHttpParams();
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new StringEntity("12" + "", "UTF-8"));
            try {
                response = httpClient.execute(httppost);
                //data = EntityUtils.toString(response.getEntity());
                jsonObject = processHttpResponse(response);
                result= getPipeDataFromJson(jsonObject);
            } catch (Exception e) {
                Log.e(TAG, "Error in http execute " + e);
            }
            httpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
            Log.e(TAG, "Error in http connection " + e.toString());
        }
        return result;
    }

    public long getPipeDataFromJson(JSONObject jsonObject) {
        long result=0;
        try {
            JSONArray jsonArray1 = jsonObject.getJSONArray("command");
            List<BleBean> commandList = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setCommand_id(jsonObject1.getInt("id"));
                bleBean.setCommand_name(jsonObject1.getString("command"));
                bleBean.setCommand_type_id1(jsonObject1.getInt("command_type_id"));
                bleBean.setStart_del(jsonObject1.getString("starting_del"));
                bleBean.setEnd_del(jsonObject1.getString("end_del"));
                bleBean.setSelection(jsonObject1.getInt("selection"));
                bleBean.setInput_id(jsonObject1.getInt("input"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                bleBean.setFormat(jsonObject1.getString("format"));
                commandList.add(bleBean);
            }
            JSONArray jsonArray2 = jsonObject.getJSONArray("servicies");
            List<BleBean> servicels = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray2.length(); i++) {
                JSONObject jsonObject1 = jsonArray2.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setServices_id(jsonObject1.getInt("id"));
                bleBean.setService_name(jsonObject1.getString("service_name"));
                bleBean.setServise_uuid(jsonObject1.getString("service_uuid"));
                bleBean.setDevice_id(jsonObject1.getInt("device_id"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                servicels.add(bleBean);
            }
            JSONArray jsonArray3 = jsonObject.getJSONArray("charachtristics");
            List<BleBean> charls = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray3.length(); i++) {
                JSONObject jsonObject1 = jsonArray3.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setCharachtristics_id(jsonObject1.getInt("id"));
                bleBean.setChar_name(jsonObject1.getString("name"));
                bleBean.setServices_id(jsonObject1.getInt("service_id"));
                bleBean.setChar_uuid(jsonObject1.getString("uuid"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                charls.add(bleBean);
            }
            JSONArray jsonArray4 = jsonObject.getJSONArray("command_type");
            List<BleBean> command_typels = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray4.length(); i++) {
                JSONObject jsonObject1 = jsonArray4.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setCommand_type_id(jsonObject1.getInt("id"));
                bleBean.setCommand_type_name(jsonObject1.getString("name"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                command_typels.add(bleBean);
            }
            JSONArray jsonArray5 = jsonObject.getJSONArray("device");
            List<BleBean> devicels = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray5.length(); i++) {
                JSONObject jsonObject1 = jsonArray5.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setDevice_id(jsonObject1.getInt("id"));
                bleBean.setManufacturer_id(jsonObject1.getInt("manufacture_id"));
                bleBean.setDevice_type_id(jsonObject1.getInt("device_type_id"));
                bleBean.setModel_id(jsonObject1.getInt("model_id"));
                bleBean.setRemark(jsonObject1.getString("remark"));
//                bleBean.setServices_id(jsonObject1.getInt("service_id"));
//                bleBean.setChar_uuid(jsonObject1.getString("uuid"));
                devicels.add(bleBean);
            }
            JSONArray jsonArray6 = jsonObject.getJSONArray("modal_type");
            List<BleBean> modeltypels = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray6.length(); i++) {
                JSONObject jsonObject1 = jsonArray6.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setModel_type_id(jsonObject1.getInt("id"));
                bleBean.setModel_type_name(jsonObject1.getString("type"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                modeltypels.add(bleBean);
            }

            JSONArray jsonArray7 = jsonObject.getJSONArray("rule");
            List<BleBean> rulels = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray7.length(); i++) {
                JSONObject jsonObject1 = jsonArray7.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setRule_id(jsonObject1.getInt("id"));
                bleBean.setCommand_id(jsonObject1.getInt("command_id"));
                bleBean.setRemark1(jsonObject1.getString("description"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                rulels.add(bleBean);
            }
            JSONArray jsonArray8 = jsonObject.getJSONArray("operation_name");
            List<BleBean> opnamels = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray8.length(); i++) {
                JSONObject jsonObject1 = jsonArray8.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setOperation_id(jsonObject1.getInt("id"));
                bleBean.setOperation_name(jsonObject1.getString("operation_name"));
                bleBean.setIs_super_child(jsonObject1.getString("is_super_child"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                bleBean.setParent_id(jsonObject1.getString("parent_id"));
                opnamels.add(bleBean);
            }
            JSONArray jsonArray9 = jsonObject.getJSONArray("device_type");
            List<BleBean> device_typels = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray9.length(); i++) {
                JSONObject jsonObject1 = jsonArray9.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setDevice_type_id(jsonObject1.getInt("id"));
                bleBean.setDevice_type(jsonObject1.getString("type"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                //bleBean.setRemark1(jsonObject1.getString("description"));
                device_typels.add(bleBean);
            }
            JSONArray jsonArray10 = jsonObject.getJSONArray("manufacturer");
            List<BleBean> manufactrerls = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray10.length(); i++) {
                JSONObject jsonObject1 = jsonArray10.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setManufacturer_id(jsonObject1.getInt("id"));
                bleBean.setManufacturer_name(jsonObject1.getString("name"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                //bleBean.setRemark1(jsonObject1.getString("description"));
                manufactrerls.add(bleBean);
            }
            JSONArray jsonArray11 = jsonObject.getJSONArray("device_registration");
            List<BleBean> devicerefls = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray11.length(); i++) {
                JSONObject jsonObject1 = jsonArray11.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setDevice_reg_id(jsonObject1.getInt("id"));
                bleBean.setDevice_id(jsonObject1.getInt("device_id"));
                bleBean.setReg_no(jsonObject1.getString("reg_no"));
                bleBean.setManufactrerdate(jsonObject1.getString("manufactrer_date"));
                bleBean.setDate2(jsonObject1.getString("dater2"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                devicerefls.add(bleBean);
            }
            JSONArray jsonArray12 = jsonObject.getJSONArray("model");
            List<BleBean> modells = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray12.length(); i++) {
                JSONObject jsonObject1 = jsonArray12.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setModel_id(jsonObject1.getInt("id"));
                bleBean.setModel_name(jsonObject1.getString("device_name"));
                bleBean.setDevice_no(jsonObject1.getString("device_no"));
                bleBean.setModel_type_id(jsonObject1.getInt("model_type_id"));
                bleBean.setWarranty(jsonObject1.getString("warranty_period"));
                bleBean.setDevice_address(jsonObject1.getString("device_address"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                bleBean.setNo_of_module(jsonObject1.getInt("no_of_module"));
                modells.add(bleBean);
            }
            JSONArray jsonArray13 = jsonObject.getJSONArray("ble_operation_name");
            List<BleBean> bleOperationList = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray13.length(); i++) {
                JSONObject jsonObject1 = jsonArray13.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setBle_operation_id(jsonObject1.getInt("ble_operation_name_id"));
                bleBean.setBle_operationName(jsonObject1.getString("ble_operation_name"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                // bleBean.setDate2(jsonObject1.getString("dater2"));
                bleOperationList.add(bleBean);
            }
            JSONArray jsonArray14 = jsonObject.getJSONArray("device_characteristic_ble_map");
            List<BleBean> ble_mapplist = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray14.length(); i++) {
                JSONObject jsonObject1 = jsonArray14.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setBle_operationMapping_id(jsonObject1.getInt("device_characteristic_ble_map_id"));
                bleBean.setBle_operation_id(jsonObject1.getInt("ble_operation_name_id"));
                bleBean.setDevice_id(jsonObject1.getInt("device_id"));
                bleBean.setOrder_no(jsonObject1.getString("order_no"));
                bleBean.setCharachtristics_id(jsonObject1.getInt("write_characteristic_id"));
                bleBean.setCharachtristicsReadId(jsonObject1.getInt("read_characteristic_id"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                ble_mapplist.add(bleBean);
            }

            JSONArray jsonArray15 = jsonObject.getJSONArray("device_map");
            List<BleBean> device_mapplist = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray15.length(); i++) {
                JSONObject jsonObject1 = jsonArray15.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setDevice_map_id(jsonObject1.getInt("device_map_id"));
                bleBean.setFinished_device_id(jsonObject1.getInt("finished_device_id"));
//              bleBean.setBle_device_id(jsonObject1.getInt("ble_device_id"));
                bleBean.setModule_id(jsonObject1.getInt("module_device_id"));
                bleBean.setRemark(jsonObject1.getString("remark"));
                device_mapplist.add(bleBean);
            }

            JSONArray jsonArray16 = jsonObject.getJSONArray("input");
            List<BleBean> inputlist = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray16.length(); i++) {
                JSONObject jsonObject1 = jsonArray16.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setCommand_id(jsonObject1.getInt("command_id"));
                bleBean.setRemark3(jsonObject1.getString("remark"));
                bleBean.setInput_id(jsonObject1.getInt("input_id"));
                bleBean.setParameter_id(jsonObject1.getInt("parameter_id"));
                inputlist.add(bleBean);
            }

            JSONArray jsonArray17 = jsonObject.getJSONArray("selection");
            List<BleBean> selectionlist = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray17.length(); i++) {
                JSONObject jsonObject1 = jsonArray17.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setCommand_id(jsonObject1.getInt("command_id"));
                bleBean.setSelection_id(jsonObject1.getInt("selection_id"));
                bleBean.setParameter_id(jsonObject1.getInt("parameter_id"));
                bleBean.setSelection_value_no(jsonObject1.getInt("selection_value_no"));
                bleBean.setRemark4(jsonObject1.getString("remark"));
                selectionlist.add(bleBean);
            }

            JSONArray jsonArray18 = jsonObject.getJSONArray("parameter");
            List<BleBean> parameterlist = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray18.length(); i++) {
                JSONObject jsonObject1 = jsonArray18.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setParameter_name(jsonObject1.getString("parameter_name"));
                bleBean.setParameter_type(jsonObject1.getString("parameter_type"));
                bleBean.setParameter_id(jsonObject1.getInt("parameter_id"));
                bleBean.setRemark5(jsonObject1.getString("remark"));
                parameterlist.add(bleBean);
            }

            JSONArray jsonArray19 = jsonObject.getJSONArray("selection_value");
            List<BleBean> selection_valuelist = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray19.length(); i++) {
                JSONObject jsonObject1 = jsonArray19.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setRevision_no1(jsonObject1.getInt("revision_no"));
                bleBean.setRemark8(jsonObject1.getString("remark"));
                bleBean.setDisplay_value(jsonObject1.getString("display_value"));
                bleBean.setSelection_value_id(jsonObject1.getInt("selection_value_id"));
                bleBean.setByte_value(jsonObject1.getString("byte_value"));
                bleBean.setSelection_id1(jsonObject1.getInt("selection_id"));
                selection_valuelist.add(bleBean);
            }

            JSONArray jsonArray20 = jsonObject.getJSONArray("command_device_map");
            List<BleBean> command_device_maplist = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray20.length(); i++) {
                JSONObject jsonObject1 = jsonArray20.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setCommand_id2(jsonObject1.getInt("command_id"));
                bleBean.setDevice_id2(jsonObject1.getInt("device_id"));
                bleBean.setOperation_id2(jsonObject1.getInt("operation_id"));
                bleBean.setOrder_no1(jsonObject1.getInt("order_no"));
                bleBean.setDelay(jsonObject1.getInt("delay"));
                bleBean.setRemark11(jsonObject1.getString("remark"));
                bleBean.setId(jsonObject1.getInt("id"));
                command_device_maplist.add(bleBean);
            }

            JSONArray jsonArray21 = jsonObject.getJSONArray("sub_byte_division");
            List<BleBean> sub_byte_divisionlist = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray21.length(); i++) {
                JSONObject jsonObject1 = jsonArray21.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setSub_division_no(jsonObject1.getInt("sub_division_no"));
                bleBean.setRevision_no2(jsonObject1.getInt("revision_no"));
                bleBean.setSub_byte_division_id(jsonObject1.getInt("sub_byte_division_id"));
                bleBean.setRemark9(jsonObject1.getString("remark"));
                bleBean.setParameter_name1(jsonObject1.getString("parameter_name"));
                bleBean.setByte_id(jsonObject1.getInt("byte_id"));
                bleBean.setStart_pos(jsonObject1.getInt("start_pos"));
                bleBean.setNo_of_bit(jsonObject1.getInt("no_of_bit"));
                sub_byte_divisionlist.add(bleBean);
            }

            JSONArray jsonArray22 = jsonObject.getJSONArray("byte_data");
            List<BleBean> byte_datalist = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray22.length(); i++) {
                JSONObject jsonObject1 = jsonArray22.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setCommand_id1(jsonObject1.getInt("command_id"));
                bleBean.setRevision_no3(jsonObject1.getInt("revision_no"));
                bleBean.setSub_byte_division(jsonObject1.getInt("sub_byte_division"));
                bleBean.setRemark10(jsonObject1.getString("remark"));
                bleBean.setParameter_name2(jsonObject1.getString("parameter_name"));
                bleBean.setByte_data_id(jsonObject1.getInt("byte_data_id"));
                byte_datalist.add(bleBean);
            }

            JSONArray jsonArray23 = jsonObject.getJSONArray("sub_division_selection");
            List<BleBean> subdivision_selectionlist = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray23.length(); i++) {
                JSONObject jsonObject1 = jsonArray23.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setSub_bytedivision_id(jsonObject1.getInt("sub_byte_division_id"));
                bleBean.setSub_division_selection_id(jsonObject1.getInt("sub_division_selection_id"));
                bleBean.setDisplayvalue(jsonObject1.getString("display_value"));
                bleBean.setBit_value(jsonObject1.getString("bit_value"));
                subdivision_selectionlist.add(bleBean);
            }
            JSONArray jsonArray24 = jsonObject.getJSONArray("datum_data");
            List<BleBean> datum_datalist = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray24.length(); i++) {
                JSONObject jsonObject1 = jsonArray24.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setDatum_idd(jsonObject1.getString("datum_id"));
                bleBean.setDatum_command(jsonObject1.getString("datum_command"));
                bleBean.setDatum_name(jsonObject1.getString("datum_name"));
                datum_datalist.add(bleBean);
            }

            JSONArray jsonArray25 = jsonObject.getJSONArray("command_param_map");
            List<BleBean> param_list = new ArrayList<BleBean>();
            for (int i = 0; i < jsonArray25.length(); i++) {
                JSONObject jsonObject1 = jsonArray25.getJSONObject(i);
                BleBean bleBean = new BleBean();
                bleBean.setCommandId(jsonObject1.getString("command_id"));
                bleBean.setSelectionValueId(jsonObject1.getString("selection_value_id"));
                bleBean.setParameterID(jsonObject1.getString("parameter_id"));
                bleBean.setSubdivision_selection_id(jsonObject1.getString("sub_division_selection_id"));
                param_list.add(bleBean);
            }

            DatabaseOperation dbTask = new DatabaseOperation(context);
            dbTask.open();
            result = dbTask.insertmanufectureDetail(manufactrerls);
            result = dbTask.insertcommand_typeDetail(command_typels);
            result = dbTask.insertmodel_typeDetail(modeltypels);
            result = dbTask.insertmodelDetail(modells);
            result = dbTask.insertdevice_typeDetail(device_typels);
            result = dbTask.insertdeviceDetail(devicels);
            //result = dbTask.insertdeviceDetail(devicels);
            result = dbTask.insertoperationDetail(opnamels);
            result = dbTask.insertcommandDetail(commandList);
            result = dbTask.insertservicesDetail(servicels);
            result = dbTask.insertcharachtristicsDetail(charls);
            result = dbTask.insertruleDetail(rulels);
            result = dbTask.insertdeviceregDetail(devicerefls);
            result = dbTask.insertbleOperation(bleOperationList);
            result = dbTask.insertbleOperationMap(ble_mapplist);
            result = dbTask.insertDeviceMap(device_mapplist);
            result = dbTask.insertinput(inputlist);
            result = dbTask.insertselection(selectionlist);
            result = dbTask.insertparameter(parameterlist);
            result = dbTask.insertselectionvalue(selection_valuelist);
            result = dbTask.insertcommanddevicemapvalue(command_device_maplist);
            result = dbTask.insertsubbytedivisionvalue(sub_byte_divisionlist);
            result = dbTask.insertbytedatavalue(byte_datalist);
            result = dbTask.insertSubdivisionSelectionvalue(subdivision_selectionlist);
            result = dbTask.insertDatumData(datum_datalist);
            result = dbTask.insertCmdParamData(param_list);
            dbTask.close();

        } catch (Exception e) {
            Log.e(TAG, "Error in getPipeDataFromJson " + e.toString());
        }
        return  result;
    }

    public static JSONObject processHttpResponse(HttpResponse response)
            throws UnsupportedEncodingException, IllegalStateException, IOException, JSONException {
        JSONObject top = null;
        StringBuilder builder = new StringBuilder();
        String dec = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            String decoded = new String(builder.toString().getBytes(), "UTF-8");
            Log.d(TAG, "decoded http response: " + decoded);
            JSONTokener tokener = new JSONTokener(Uri.decode(builder.toString()));
            top = new JSONObject(tokener);
        } catch (JSONException t) {
            Log.w(TAG, "<processHttpResponse> caught: " + t + ", handling as string...");
        } catch (IOException e) {
            Log.e(TAG, "caught processHttpResponse IOException : " + e, e);
        } catch (Throwable t) {
            Log.e(TAG, "caught processHttpResponse Throwable : " + t, t);
        }
        return top;
    }

    public String getServer_ip() {
        return server_ip;
    }

    public void setServer_ip(String server_ip) {
        this.server_ip = server_ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public byte[]  funcRequest(String req){
        long result=0;
        byte[] transMsg=null;
        byte[] contentRes=null;
        try{
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String data = req;
            transMsg= data.getBytes();
            HttpClient httpClient =null;
            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 0);
                HttpConnectionParams.setSoTimeout(httpParameters, 0);
                httpClient = new DefaultHttpClient(httpParameters);
            }catch (Exception e){
                System.out.println(": "+e);
            }

            HttpPost httppost;

            if (server_ip!=null || port!=null){
                httppost = new HttpPost("http://" + server_ip + ":" + port + "/resources/TestFile/Base");
            }else {
                httppost = new HttpPost("http://" + "120.138.10.197" + ":"+"8084"+"/resources/TestFile/Base");
            }

            //httppost.setHeader("Content-type", "application/byteArray");
            httppost.setEntity(new ByteArrayEntity(transMsg));
            HttpResponse response = httpClient.execute(httppost);
            System.out.println("Web Service Response: " + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                result = 1;
            }
            HttpEntity entity=response.getEntity();
            if(entity!=null){
                contentRes = EntityUtils.toByteArray(response.getEntity());
                System.out.println("content " );
            }
            processHttpResponse(response);
            httpClient.getConnectionManager().shutdown();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "Error in requestData: " + e);

        }
        return contentRes;
    }

}
