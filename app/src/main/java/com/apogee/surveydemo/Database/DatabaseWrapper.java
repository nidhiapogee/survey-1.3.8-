package com.apogee.surveydemo.Database;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

public class DatabaseWrapper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseWrapper.java";
    private static final String DATABASE_NAME = "blelocation.db";
    private static final int DATABASE_VERSION = 3;
    private static final SQLiteDatabase.CursorFactory CURSOR_FACTORY = null;
    Context context;

    private static final String serverIpQuery = " CREATE TABLE server_ip ( "
            + "  server_ip_id INTEGER  NOT NULL, "
            + "  ip TEXT NOT NULL,"
            + "  port TEXT NOT NULL,"
            + "  remark TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "  PRIMARY KEY (server_ip_id)"
            + ");";

    private static final String TcpIpQuery = " CREATE TABLE tcp_ip ( "
            + "  tcp_ip_id INTEGER  NOT NULL,"
            + "  ip TEXT NOT NULL,"
            + "  port TEXT NOT NULL,"
            + "  remark TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "  PRIMARY KEY (tcp_ip_id)"
            + ");";


    private static final String manufactrurQuery = " CREATE TABLE manufacturer ("
            + " manufacturer_id  INTEGER PRIMARY KEY autoincrement,"
            + " name TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " revision_no TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ");";
    private static final String deviceQuery = " CREATE TABLE device ("
            + " id  INTEGER PRIMARY KEY autoincrement,"
            + " manufacturer_id INTEGER DEFAULT NULL,"
            + " device_type_id INTEGER DEFAULT NULL,"
            + " model_id INTEGER DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " revision_no TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + " timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " created_by TEXT DEFAULT NULL,"
            + " FOREIGN KEY (manufacturer_id) REFERENCES manufacturer(id),"
            + " FOREIGN KEY (device_type_id) REFERENCES device_type(id),"
            + " FOREIGN KEY (model_id) REFERENCES model(id)"
            + ");";

    private static final String operation_namequery = " CREATE TABLE operation ("
            + " id  INTEGER PRIMARY KEY autoincrement,"
            + " operation_name TEXT DEFAULT NULL,"
            + " is_super_child TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " revision_no TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + " parent_id TEXT DEFAULT NULL,"
            + " timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ");";
    private static final String commandQuery = " CREATE TABLE command ("
            + " command_id  INTEGER PRIMARY KEY autoincrement,"
            + " command TEXT DEFAULT NULL,"
            + " start_del TEXT DEFAULT NULL,"
            + " end_del TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " created_by TEXT  DEFAULT NULL,"
            + " revision_no TEXT DEFAULT NULL,"
            + " command_type_id INTEGER DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + " format TEXT DEFAULT NULL,"
            + " selection INTEGER DEFAULT 0,"
            + " input INTEGER DEFAULT 0,"
            + " bitwise INTEGER DEFAULT 0,"
            + " timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (command_type_id) REFERENCES command_type (id)"
            + ");";
    private static final String inputquery = " CREATE TABLE input ("
            + " input_id  INTEGER PRIMARY KEY autoincrement,"
            + " command_id  INTEGER DEFAULT NULL,"
            + " parameter_id  INTEGER DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " created_by TEXT DEFAULT NULL,"
            + " revision_no TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + " timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (command_id) REFERENCES command (id),"
            + " FOREIGN KEY (parameter_id) REFERENCES parameter (id)"
            + ");";

    private static final String parameterquery = " CREATE TABLE parameter ("
            + " parameter_id  INTEGER PRIMARY KEY autoincrement,"
            + " parameter_name TEXT DEFAULT NULL,"
            + " parameter_type TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " created_by TEXT DEFAULT NULL,"
            + " revision_no TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP);";

    private static final String selectionquery = " CREATE TABLE selection ("
            + " selection_id  INTEGER PRIMARY KEY autoincrement,"
            + " command_id  INTEGER DEFAULT NULL,"
            + " parameter_id  INTEGER DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " created_by TEXT DEFAULT NULL,"
            + " revision_no TEXT DEFAULT NULL,"
            + " selection_value_no INTEGER DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (command_id) REFERENCES command (id),"
            + " FOREIGN KEY (parameter_id) REFERENCES parameter (id)"
            + ");";
    private static final String servicesquery = " CREATE TABLE services ("
            + " id  INTEGER PRIMARY KEY autoincrement,"
            + " services_name TEXT DEFAULT NULL,"
            + " services_uuid TEXT DEFAULT NULL,"
            + " device_id INTEGER DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " revision_no TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (device_id) REFERENCES device (id)"
            + ");";
    private static final String charachtristicsQuery = " CREATE TABLE charachtristics ("
            + " id  INTEGER PRIMARY KEY autoincrement,"
            + " char_name TEXT DEFAULT NULL,"
            + " char_uuid TEXT DEFAULT NULL,"
            + " service_id INTEGER DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " revision_no TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (service_id) REFERENCES services (id)"
            + ");";
    private static final String mode_typelQuery = " CREATE TABLE model_type ("
            + " id  INTEGER PRIMARY KEY autoincrement,"
            + " type TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " revision_no TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + " timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ");";
    private static final String modelQuery = " CREATE TABLE model ("
            + " id  INTEGER PRIMARY KEY autoincrement,"
            + " device_name TEXT DEFAULT NULL,"
            + " device_no TEXT DEFAULT NULL,"
            + " device_address TEXT DEFAULT NULL,"
            + " model_type_id INTEGER DEFAULT NULL,"
            + " no_of_module INTEGER DEFAULT NULL,"
            + " warranty TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " revision_no TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + " timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
           + " FOREIGN KEY (model_type_id) REFERENCES model_type(id)"
            + ");";
    private static final String device_typelQuery = " CREATE TABLE device_type ("
            + " id  INTEGER PRIMARY KEY autoincrement,"
            + " type TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " revision_no TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + " timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String command_typequery = " CREATE TABLE command_type ("
            + " id  INTEGER PRIMARY KEY autoincrement,"
            + " command_name TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " revision_no TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ");";
    private static final String rulesquery = " CREATE TABLE rule ("
            + " id  INTEGER PRIMARY KEY autoincrement,"
            + " command_id INTEGER DEFAULT NULL,"
            + " description TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " revision_no TEXT DEFAULT NULL,"
            + " created_by TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (command_id) REFERENCES command(id)"
            + ");";
    private static final String device_reqistrationquery = " CREATE TABLE device_registration ("
            + " id  INTEGER PRIMARY KEY autoincrement,"
            + " device_id INTEGER DEFAULT NULL,"
            + " reg_no TEXT DEFAULT NULL,"
            + " manufactrure_date TEXT DEFAULT NULL,"
            + " date2 TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " revision_no TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (device_id) REFERENCES device(id)"
            + ");";
    private static final String  BleOperationquery = " CREATE TABLE ble_operation ("
            + " ble_operation_id  INTEGER PRIMARY KEY autoincrement,"
            + " operation_name TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ");";
    private static final String BleOperationMappingquery = " CREATE TABLE ble_operation_mapping ("
            + " ble_operation_mapping_id  INTEGER PRIMARY KEY autoincrement,"
            + " device_id INTEGER DEFAULT NULL,"
            + " charachtristics_write_id INTEGER DEFAULT NULL,"
            + " charachtristics_read_id INTEGER DEFAULT NULL,"
            + " ble_operation_id INTEGER DEFAULT NULL,"
            + " order_no TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (device_id) REFERENCES device(id),"
            + " FOREIGN KEY (charachtristics_write_id) REFERENCES charachtristics(id),"
            + " FOREIGN KEY (charachtristics_read_id) REFERENCES charachtristics(id),"
            + " FOREIGN KEY (ble_operation_id) REFERENCES ble_operation(ble_operation_id)"
            + ");";
    private static final String Device_mapquery = " CREATE TABLE device_map ("
            + " device_map_id  INTEGER PRIMARY KEY autoincrement,"
            + " finished_device_id INTEGER DEFAULT NULL,"
            + " ble_device_id INTEGER DEFAULT NULL,"
            + " module_device_id INTEGER DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (finished_device_id) REFERENCES device(id),"
            + " FOREIGN KEY (ble_device_id) REFERENCES device(id),"
            + " FOREIGN KEY (module_device_id) REFERENCES device(id)"
            + ");";

    private static final String device_command_mapquery = " CREATE TABLE device_command_map ("
            + " device_command_id  INTEGER PRIMARY KEY autoincrement,"
            + " device_id INTEGER DEFAULT NULL,"
            + " command_id INTEGER DEFAULT NULL,"
            + " operation_id INTEGER DEFAULT NULL,"
            + " order_no INTEGER DEFAULT NULL,"
            + " delay INTEGER DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " remark TEXT DEFAULT NULL,"
            + " revision_no INTEGER DEFAULT NULL,"
            + " timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (device_id) REFERENCES device(id),"
            + " FOREIGN KEY (command_id) REFERENCES command(id),"
            + " FOREIGN KEY (operation_id) REFERENCES operation(id)"
            + ");";

    private static final String selection_valuequery = " CREATE TABLE selection_value ("
            + " selection_value_id  INTEGER PRIMARY KEY autoincrement,"
            + " display_value TEXT DEFAULT NULL,"
            + " byte_value TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " created_by TEXT DEFAULT NULL,"
            + " selection_id INTEGER DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + " revision_no INTEGER DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (selection_id) REFERENCES selection(id)"
            + ");";

    private static final String command_validationquery = " CREATE TABLE command_validation ("
            + " command_validation_id  INTEGER PRIMARY KEY autoincrement,"
            + " validation_description TEXT DEFAULT NULL,"
            + " holding_validation TEXT DEFAULT NULL,"
            + " command_id INTEGER DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " created_by TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + " revision_no INTEGER DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (command_id) REFERENCES command(id)"
            + ");";

    private static final String byte_dataquery = " CREATE TABLE byte_data ("
            + " byte_data_id  INTEGER PRIMARY KEY autoincrement,"
            + " sub_byte_division INTEGER DEFAULT NULL,"
            + " command_id INTEGER DEFAULT NULL,"
            + " parameter_name TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " created_by TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + " revision_no INTEGER DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (command_id) REFERENCES command(id)"
            + ");";

    private static final String sub_division_selectionquery = " CREATE TABLE sub_division_selection ("
            + " sub_division_selection_id  INTEGER PRIMARY KEY autoincrement,"
            + " display_value TEXT DEFAULT NULL,"
            + " bit_value TEXT DEFAULT NULL,"
            + " sub_byte_division_id INTEGER DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " created_by TEXT DEFAULT NULL,"
            + " revision_no INTEGER DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (sub_byte_division_id) REFERENCES sub_byte_division(sub_byte_division_id)"
            + ");";

    private static final String sub_byte_divisionquery = " CREATE TABLE sub_byte_division ("
            + " sub_byte_division_id  INTEGER PRIMARY KEY autoincrement,"
            + " byte_id INTEGER DEFAULT NULL,"
            + " parameter_name TEXT DEFAULT NULL,"
            + " start_pos INTEGER DEFAULT NULL,"
            + " no_of_bit INTEGER DEFAULT NULL,"
            + " sub_division_no INTEGER DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " remark TEXT DEFAULT NULL,"
            + " revision_no INTEGER DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (byte_id) REFERENCES byte_data(byte_data_id)"
            + ");";

    private static final String tasktablequery = " CREATE TABLE Task_table ("
            + " task_id  INTEGER PRIMARY KEY autoincrement,"
            + " type_of_survey TEXT DEFAULT NULL,"
            + " task_title TEXT DEFAULT NULL,"
            + " task_person TEXT DEFAULT NULL,"
            + " project_id INTEGER DEFAULT NULL,"
            + " task_description TEXT DEFAULT NULL,"
            + " task_timeline TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " remark TEXT DEFAULT NULL,"
            + " revision_no INTEGER DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (project_id) REFERENCES project_table(idproject_Table)"
            + ");";


    private static final String datumquery = " CREATE TABLE Datum ("
            + " datum_id_server  INTEGER PRIMARY KEY autoincrement,"
            + " name TEXT DEFAULT NULL,"
            + " datum_command TEXT DEFAULT NULL,"
            + " major_axis TEXT DEFAULT NULL,"
            + " flattening TEXT DEFAULT NULL,"
            + " x_axis_shift TEXT DEFAULT NULL,"
            + " Y_axis_shift TEXT DEFAULT NULL,"
            + " Z_axis_shift TEXT DEFAULT NULL,"
            + " rot_x_axis TEXT DEFAULT NULL,"
            + " rot_y_axis TEXT DEFAULT NULL,"
            + " rot_z_axis TEXT DEFAULT NULL,"
            + " scale TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " remark TEXT DEFAULT NULL,"
            + " revision_no INTEGER DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String cmd_paramquery = " CREATE TABLE command_param_map ("
            + " command_param_map_id  INTEGER PRIMARY KEY autoincrement,"
            + " command_id TEXT DEFAULT NULL,"
            + " selection_value_id TEXT DEFAULT NULL,"
            + " parameter_id TEXT DEFAULT NULL,"
            + " sub_division_selection_id TEXT DEFAULT NULL"
            + ");";

    private static final String latlongquery = " CREATE TABLE location ("
            + " id  INTEGER PRIMARY KEY autoincrement,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " latitude TEXT DEFAULT NULL,"
            + " longitude TEXT DEFAULT NULL,"
            + " status TEXT DEFAULT NULL,"
            + " accuracy TEXT DEFAULT NULL,"
            + " remark TEXT DEFAULT NULL,"
            + " revision_no INTEGER DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String mainProjectquery = " CREATE TABLE project_table ("
            + " idproject_Table  INTEGER PRIMARY KEY autoincrement,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " Project_name TEXT DEFAULT NULL,"
            + " Datum TEXT DEFAULT NULL,"
            + " Operator TEXT DEFAULT NULL,"
            + " status TEXT DEFAULT NULL,"
            + "  timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ");";


    private static final String deviceinfoquery = "CREATE TABLE DeviceInfo ("
            + " iddevice  INTEGER PRIMARY KEY autoincrement,"
            + " BaseName TEXT DEFAULT NULL,"
            + " Latitude TEXT DEFAULT NULL,"
            + " longitude TEXT DEFAULT NULL,"
            + " height TEXT DEFAULT NULL,"
            + " Antennaheight TEXT DEFAULT NULL,"
            + " Antenna_AFC TEXT DEFAULT NULL,"
            + " communicationmode TEXT DEFAULT NULL,"
            + " timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ");";


    private static final String surveydataquery = "CREATE TABLE survgey_data ("
            + " idtopo_survey  INTEGER PRIMARY KEY autoincrement,"
            + " Point_name TEXT DEFAULT NULL,"
            + " task_id INTEGER DEFAULT NULL,"
            + " zone TEXT DEFAULT NULL,"
            + " Easting TEXT DEFAULT NULL,"
            + " Northing TEXT DEFAULT NULL,"
            + " Elevation TEXT DEFAULT NULL,"
            + " point_code TEXT DEFAULT NULL,"
            + " Horizontal_Accuracy TEXT DEFAULT NULL,"
            + " Vertical_Accuracy TEXT DEFAULT NULL,"
            + " Time TEXT DEFAULT NULL,"
            + " Date TEXT DEFAULT NULL,"
            + " AntennaHeight TEXT DEFAULT NULL,"
            + " RecordType TEXT DEFAULT NULL,"
            + " PrecisionType TEXT DEFAULT NULL,"
            + " timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (task_id) REFERENCES Task_table(task_id)"
            + ");";


    private static final String savedconfigurationquery = "CREATE TABLE SavedConfiguration ("
            + " idconfig  INTEGER PRIMARY KEY autoincrement,"
            + " ConfigName TEXT DEFAULT NULL,"
            + " Type_of_config TEXT DEFAULT NULL,"
            + " Type_of_coreection TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " remark TEXT DEFAULT NULL,"
            + " revision_no INTEGER DEFAULT NULL,"
            + " timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ");";


    private static final String savedconfigurationdataquery = "CREATE TABLE SavedConfigurationData ("
            + " id  INTEGER PRIMARY KEY autoincrement,"
            + " idconfig INTEGER DEFAULT NULL,"
            + " Parameter_Name TEXT DEFAULT NULL,"
            + " Parameter_value TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " remark TEXT DEFAULT NULL,"
            + " revision_no INTEGER DEFAULT NULL,"
            + " timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (idconfig) REFERENCES SavedConfiguration(idconfig)"
            + ");";

    private static final String savedconfigurationcommandsquery = "CREATE TABLE SavedConfigurationCommands ("
            + " id  INTEGER PRIMARY KEY autoincrement,"
            + " idconfigg INTEGER DEFAULT NULL,"
            + " Command TEXT DEFAULT NULL,"
            + " Delay TEXT DEFAULT NULL,"
            + " active TEXT  NOT NULL DEFAULT 'Y',"
            + " remark TEXT DEFAULT NULL,"
            + " revision_no INTEGER DEFAULT NULL,"
            + " timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            +  "format INTEGER DEFAULT NULL,"
            + " FOREIGN KEY (idconfigg) REFERENCES SavedConfiguration(idconfig)"
            + ");";

    public DatabaseWrapper(Context context) {
        super(context, DATABASE_NAME, CURSOR_FACTORY, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        boolean result = false;
        try {
            File dbFile = new ContextWrapper(context).getDatabasePath(DATABASE_NAME);
            String path = dbFile.getAbsolutePath();
            Log.d("Info", "db path : " + dbFile.getAbsolutePath());
            try {
                db.execSQL(serverIpQuery);
                db.execSQL(TcpIpQuery);
                db.execSQL("INSERT OR REPLACE INTO server_ip(server_ip_id,ip,port,remark) values ("
                        + "1, '45.114.142.35','8080', (SELECT remark FROM server_ip WHERE server_ip_id = 1)) ");
                db.execSQL("INSERT OR REPLACE INTO tcp_ip(tcp_ip_id,ip,port,remark) values ("
                        + "1, '45.114.142.35','8090', (SELECT remark FROM tcp_ip WHERE tcp_ip_id = 1)) ");
                result = true;
            } catch (Exception e) {
                result = false;
            }
            if (result) {
                result = false;
                db.execSQL(manufactrurQuery);
                db.execSQL(command_typequery);
                db.execSQL(mode_typelQuery);
                db.execSQL(modelQuery);
                db.execSQL(device_typelQuery);
                db.execSQL(deviceQuery);
                db.execSQL(operation_namequery);
                db.execSQL(commandQuery);
                db.execSQL(servicesquery);
                db.execSQL(charachtristicsQuery);
                db.execSQL(rulesquery);
                db.execSQL(device_reqistrationquery);
                db.execSQL(BleOperationquery);
                db.execSQL(BleOperationMappingquery);
                db.execSQL(Device_mapquery);
                db.execSQL(inputquery);
                db.execSQL(parameterquery);
                db.execSQL(selectionquery);
                db.execSQL(device_command_mapquery);
                db.execSQL(selection_valuequery);
                db.execSQL(command_validationquery);
                db.execSQL(byte_dataquery);
                db.execSQL(sub_division_selectionquery);
                db.execSQL(sub_byte_divisionquery);
                db.execSQL(tasktablequery);
                db.execSQL(datumquery);
                db.execSQL(cmd_paramquery);
                db.execSQL(latlongquery);
                db.execSQL(mainProjectquery);
                db.execSQL(surveydataquery);
                db.execSQL(deviceinfoquery);
                db.execSQL(savedconfigurationquery);
                db.execSQL(savedconfigurationdataquery);
                db.execSQL(savedconfigurationcommandsquery);
                result = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Query execution error " + e);
            result = false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS server_ip");
        db.execSQL("DROP TABLE IF EXISTS manufacturer");
        db.execSQL("DROP TABLE IF EXISTS charachtristics");
        db.execSQL("DROP TABLE IF EXISTS services");
        db.execSQL("DROP TABLE IF EXISTS command");
        db.execSQL("DROP TABLE IF EXISTS operation");
        db.execSQL("DROP TABLE IF EXISTS device");
        db.execSQL("DROP TABLE IF EXISTS command_type");
        db.execSQL("DROP TABLE IF EXISTS device_type");
        db.execSQL("DROP TABLE IF EXISTS model");
        db.execSQL("DROP TABLE IF EXISTS model_type");
        db.execSQL("DROP TABLE IF EXISTS input");
        db.execSQL("DROP TABLE IF EXISTS parameter");
        db.execSQL("DROP TABLE IF EXISTS selection");
        db.execSQL("DROP TABLE IF EXISTS device_command_map");
        db.execSQL("DROP TABLE IF EXISTS selection_value");
        db.execSQL("DROP TABLE IF EXISTS command_validation");
        db.execSQL("DROP TABLE IF EXISTS byte_data");
        db.execSQL("DROP TABLE IF EXISTS sub_division_selection");
        db.execSQL("DROP TABLE IF EXISTS sub_byte_division");
        db.execSQL("DROP TABLE IF EXISTS Task_table");
        db.execSQL("DROP TABLE IF EXISTS Datum");
        db.execSQL("DROP TABLE IF EXISTS location");
        db.execSQL("DROP TABLE IF EXISTS project_table");
        db.execSQL("DROP TABLE IF EXISTS survgey_data");
        db.execSQL("DROP TABLE IF EXISTS DeviceInfo");
        db.execSQL("DROP TABLE IF EXISTS SavedConfiguration");
        db.execSQL("DROP TABLE IF EXISTS SavedConfigurationData");
        db.execSQL("DROP TABLE IF EXISTS SavedConfigurationCommands");
    }
}
