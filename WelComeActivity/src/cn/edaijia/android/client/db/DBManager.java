package cn.edaijia.android.client.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import cn.edaijia.android.client.AppInfo;
import cn.edaijia.android.client.model.AppContentInfo;
import cn.edaijia.android.client.util.Logger;
import cn.edaijia.android.client.util.Utils;

/**
 * ���ݿ������
 * 
 * @author CaoZheng email:caozheng_119@163.com
 * @create time:2012-8-10 10:07 --------------------last
 *         update:----------------- coder: update time: Copyright (c) YDJ
 *         corporation All Rights Reserved. INFORMATION
 */

public class DBManager {

    /** ������ */
    private static final String CLASS_NAME = DBManager.class.getName();

    /** ���ݿ����ơ� */
    private static final String DB_FILE_NAME = "edaijia.db";

    private static Logger sLogger = Logger.createLogger("DBManager");

    private static final String TABLE_CALL_LOG = "phone_info";
    private static final String TABLE_APP_CONTENT = "app_content";
    private static final String TABLE_MY_DRIVER_LIST = "driver_list";

    private DBManager() {

    }

    /**
     * �����ݿ⡣<BR>
     * ���ݿⲻ�ɴ򿪡����ߴ���ʧ��ʱ�׳��쳣��<BR>
     * 
     * @throws ����
     */
    private static SQLiteDatabase openDatabase() {
        SQLiteDatabase oDatabase = null;
        try {
            // ���Ŀ¼��û��,����
            File dbDir = new File(AppInfo.BASE_DATA_DIR + File.separator
                    + Utils.BASE_FOLDER_NAME + File.separator + "databases");
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }
            String dataBaseName = dbDir.getPath() + File.separator
                    + DB_FILE_NAME;
            oDatabase = SQLiteDatabase.openOrCreateDatabase(dataBaseName, null);
            sLogger.d(CLASS_NAME + ".���ݿⴴ�����");
            return oDatabase;
        } catch (Exception e) {
            sLogger.d(CLASS_NAME + ".���ݿ��Ѿ���");
        }
        return null;
    }

    private static void closeDB(SQLiteDatabase oDatabase) {
        if (oDatabase != null && oDatabase.isOpen()) {
            oDatabase.close();
        }
    }

    /**
     */
    private static void createTable(SQLiteDatabase oDataBase, String sTable,
            String sql) throws Exception {
        try {
            oDataBase.rawQuery("SELECT * FROM " + sTable, null);
            // //
            // // ��ѯ����
            // final String EXISTS_TABLE_INFO =
            // "SELECT * FROM sqlite_master WHERE type='table' AND name='" +
            // sTable +"'";
            // Cursor cursor = oDataBase.rawQuery(EXISTS_TABLE_INFO, null);
            // if (cursor != null && cursor.getCount() > 0){
            //
            // } else {
            // oDataBase.execSQL(sql);
            // }
        } catch (Exception e) {
            oDataBase.execSQL(sql);
        }
    }

    /**
     * �������ݱ�
     * 
     */
    public static void createTables() {
        sLogger.d(CLASS_NAME + ".createTable()-------->>>");

        // ������
        final String CREATE_PHONE_INFO_SQL = "CREATE TABLE IF NOT EXISTS "
                + TABLE_CALL_LOG + "(_id integer PRIMARY KEY AUTOINCREMENT "
                + ", udid TEXT NOT NULL " + ", latitude TEXT NOT NULL"
                + ", longtitude TEXT NOT NULL" + ", driver_id TEXT NOT NULL"
                + ", device TEXT NOT NULL" + ", version TEXT NOT NULL"
                + ", phone TEXT NOT NULL" + ", os TEXT NOT NULL"
                + ", call_time TEXT NOT NULL)";
        final String CREATE_APP_CONTENT_SQL = "CREATE TABLE IF NOT EXISTS "
                + TABLE_APP_CONTENT
                + "( _id integer PRIMARY KEY AUTOINCREMENT "
                + ", expire_at  TEXT NOT NULL " + ", cont TEXT NOT NULL "
                + " )";
        final String CREATE_MY_DRIVER_LIST = "CREATE TABLE IF NOT EXISTS "
                + TABLE_MY_DRIVER_LIST
                + "( _id integer PRIMARY KEY AUTOINCREMENT "
                + ", phone TEXT NOT NULL " + ", time long NOT NULL "
                + ", driverInfo TEXT NOT NULL " + " )";

        SQLiteDatabase dataBase = null;
        try {
            dataBase = openDatabase();
            if (dataBase != null) {
                createTable(dataBase, TABLE_MY_DRIVER_LIST, CREATE_MY_DRIVER_LIST);
                createTable(dataBase, TABLE_CALL_LOG, CREATE_PHONE_INFO_SQL);
                createTable(dataBase, TABLE_APP_CONTENT, CREATE_APP_CONTENT_SQL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDB(dataBase);
        }
        sLogger.d(CLASS_NAME + ".createTable()<<<--------");
    }

    /** ��ձ������� **/
    public static boolean clearPhoneInfo() {
        SQLiteDatabase dataBase = null;
        try {
            String sql = "DELETE FROM " + TABLE_CALL_LOG;
            dataBase = openDatabase();
            if (dataBase != null) {
                dataBase.execSQL(sql);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDB(dataBase);
        }
        return false;
    }

    public static boolean hasUnUploadCallLog() {
        SQLiteDatabase dataBase = null;
        boolean hasData = false;
        dataBase = openDatabase();
        if (dataBase != null) {
            String sql = "SELECT * FROM " + TABLE_CALL_LOG
                    + " ORDER BY _id DESC limit 1";
            Cursor cursor = dataBase.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                hasData = true;
            }
            if (cursor != null){
                cursor.close();
            }
        }
        closeDB(dataBase);
        return hasData;
    }

    /**
     * ��ѯͨ����¼ ���һ��
     * 
     */
    public static UploadInfo getFailedInfoLaste() {
        SQLiteDatabase dataBase = null;
        dataBase = openDatabase();
        UploadInfo unUploadInfo = null;
        if (dataBase != null) {
            String sql = "SELECT * FROM " + TABLE_CALL_LOG
                    + " ORDER BY _id DESC limit 1";
            Cursor cursor = dataBase.rawQuery(sql, null);
            while (cursor.moveToNext()) {

                unUploadInfo = new UploadInfo();
                unUploadInfo.setLatitude(cursor.getString(cursor
                        .getColumnIndex("latitude")));
                unUploadInfo.setLongtitude(cursor.getString(cursor
                        .getColumnIndex("longtitude")));
                unUploadInfo.setDriver_id(cursor.getString(cursor
                        .getColumnIndex("driver_id")));
                unUploadInfo.setDevice(cursor.getString(cursor
                        .getColumnIndex("device")));
                unUploadInfo.setVersion(cursor.getString(cursor
                        .getColumnIndex("version")));
                unUploadInfo.setPhone(cursor.getString(cursor
                        .getColumnIndex("phone")));
                unUploadInfo
                        .setOs(cursor.getString(cursor.getColumnIndex("os")));
                unUploadInfo.setCall_time(cursor.getString(cursor
                        .getColumnIndex("call_time")));
                unUploadInfo.setUdid(cursor.getString(cursor
                        .getColumnIndex("udid")));
            }
            cursor.close();
        }
        closeDB(dataBase);
        return unUploadInfo;
    }

    /***
     * ��ѯ�ϴ���Ϣʧ�ܱ���ȫ������
     */
    public static List<UploadInfo> getFailedInfo() {
        SQLiteDatabase dataBase = null;
        dataBase = openDatabase();
        List<UploadInfo> data = new ArrayList<UploadInfo>();
        if (dataBase != null) {
            String sql = "select * from " + TABLE_CALL_LOG;
            Cursor cursor = dataBase.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                UploadInfo fi = new UploadInfo();
                fi.setLatitude(cursor.getString(cursor
                        .getColumnIndex("latitude")));
                fi.setLongtitude(cursor.getString(cursor
                        .getColumnIndex("longtitude")));
                fi.setDriver_id(cursor.getString(cursor
                        .getColumnIndex("driver_id")));
                fi.setDevice(cursor.getString(cursor.getColumnIndex("device")));
                fi.setVersion(cursor.getString(cursor.getColumnIndex("version")));
                fi.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                fi.setOs(cursor.getString(cursor.getColumnIndex("os")));
                fi.setCall_time(cursor.getString(cursor
                        .getColumnIndex("call_time")));
                fi.setUdid(cursor.getString(cursor.getColumnIndex("udid")));
                data.add(fi);
            }
            cursor.close();
        }
        closeDB(dataBase);
        return data;
    }

    /**
     * �ύ�ͻ���Ϣʧ�� ����
     * 
     * @param lat
     * @param lont
     * @param ver
     * @param phone
     * @param imei
     * @param driver
     * @param os
     * @param time
     * @throws Exception
     */
    public static void saveFailedInfo(String lat, String lont, String ver,
            String phone, String driver_id, String device, String os,
            String time, String udid) {
        SQLiteDatabase dataBase = null;
        dataBase = openDatabase();
        SQLiteStatement stmt = null;
        try {
            if (dataBase != null) {

                String sql = "INSERT INTO "
                        + TABLE_CALL_LOG
                        + " (latitude, longtitude , version ,phone ,driver_id ,device ,os ,call_time ,udid)"
                        + " VALUES(?, ?, ?, ?, ?, ?, ?, ? ,?)";
                stmt = dataBase.compileStatement(sql);
                setBindValue(stmt, 1, lat);
                setBindValue(stmt, 2, lont);
                setBindValue(stmt, 3, ver);
                setBindValue(stmt, 4, phone);
                setBindValue(stmt, 5, driver_id);
                setBindValue(stmt, 6, device);
                setBindValue(stmt, 7, os);
                setBindValue(stmt, 8, time);
                setBindValue(stmt, 9, udid);
                stmt.execute();
            }
        } catch (Exception seq) {
            sLogger.e(CLASS_NAME + ".saveOrderPathInfo.e:" + seq.getMessage());
            seq.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            closeDB(dataBase);
        }
    }

    /**
     * ����ʱ��ɾ���ϴ��ɹ��ļ�¼
     * 
     * @param time
     * @throws Exception
     */
    public static void deleteFailedInfo(String time) {
        SQLiteDatabase dataBase = null;
        dataBase = openDatabase();
        try {
            if (dataBase != null) {
                String sql = "DELETE FROM " + TABLE_CALL_LOG + " WHERE call_time = '"
                        + time + "'";
                dataBase.execSQL(sql);
            }
        } catch (SQLException e) {
        } finally {
            closeDB(dataBase);
        }
    }

    /**
     * app_content app�ı�����
     * 
     * @param expirtAt
     * @param content
     */
    public static void saveAppContent(String expire_at, String content) {
        SQLiteDatabase dataBase = null;
        dataBase = openDatabase();
        SQLiteStatement stmt = null;
        try {
            if (dataBase != null) {
                String sql = "INSERT INTO " + TABLE_APP_CONTENT + " (expire_at, cont )"
                        + " VALUES( ?, ?)";
                stmt = dataBase.compileStatement(sql);
                setBindValue(stmt, 1, expire_at);
                setBindValue(stmt, 2, content);
                stmt.execute();
            }
        } catch (Exception seq) {
            sLogger.e(CLASS_NAME + ".saveAppcontent.e:" + seq.getMessage());
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            closeDB(dataBase);
        }
    }

    /**
     * ¼��绰��¼
     * 
     * @param time
     * @param list
     */
    public static void saveDriverlist(String phone, long time, String list) {
        SQLiteDatabase dataBase = null;
        dataBase = openDatabase();
        SQLiteStatement stmt = null;
        try {
            if (dataBase != null) {
                String sql = "INSERT INTO " + TABLE_MY_DRIVER_LIST + " (phone , time, driverInfo )"
                        + " VALUES( ? ,?, ?)";

                stmt = dataBase.compileStatement(sql);
                setBindValue(stmt, 1, phone);
                setBindLongValue(stmt, 2, time);
                setBindValue(stmt, 3, time + "|" + list);
                stmt.execute();
            }
        } catch (Exception seq) {
            sLogger.e(CLASS_NAME + ".saveAppcontent.e:" + seq.getMessage());
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            closeDB(dataBase);
        }
    }

    /**
     * ���ݵ绰�����ѯ��¼
     */
    public static String getCountbyId(String phone) {
        SQLiteDatabase dataBase = null;
        dataBase = openDatabase();
        String id = null;
        if (dataBase != null) {
            String sql = "SELECT _id ,phone , time , driverInfo FROM " + TABLE_MY_DRIVER_LIST + " WHERE phone ='"
                    + phone + "'";
            Cursor cursor = dataBase.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                id = cursor.getString(cursor.getColumnIndex("_id"));
            }
        }
        closeDB(dataBase);
        return id;
    }

    /**
     * ����_idɾ����ѯ��¼
     */
    public static void deleteCount(String id) {
        SQLiteDatabase dataBase = null;
        dataBase = openDatabase();
        if (dataBase != null) {
            String sql = "DELETE FROM " + TABLE_MY_DRIVER_LIST + " WHERE _id = '" + id + "'";
            dataBase.execSQL(sql);
        }
        closeDB(dataBase);
    }

    /**
     * ��ѯ�绰��¼����
     */
    public static ArrayList<String> getCountDriverList() {
        SQLiteDatabase dataBase = null;
        dataBase = openDatabase();
        ArrayList<String> list = null;
        if (dataBase != null) {
            String sql = "SELECT phone ,time , driverInfo FROM " + TABLE_MY_DRIVER_LIST + " ORDER BY time DESC limit 10 ";
            Cursor cursor = dataBase.rawQuery(sql, null);
            if (cursor.getCount() != 0) {
                list = new ArrayList<String>();
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(cursor
                            .getColumnIndex("driverInfo")));
                }
            }

            cursor.close();
        }
        closeDB(dataBase);
        return list;
    }

    /**
     * ��ѯapp����
     */
    public static AppContentInfo getAppContent() {
        SQLiteDatabase dataBase = null;
        dataBase = openDatabase();
        AppContentInfo info = null;
        if (dataBase != null) {
            String sql = "SELECT expire_at , cont FROM " + TABLE_APP_CONTENT + " WHERE _id = 1 ";
            Cursor cursor = dataBase.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                info = new AppContentInfo(cursor.getString(cursor
                        .getColumnIndex("cont")));
            }
            cursor.close();
        }
        closeDB(dataBase);
        return info;
    }

    /**
     * ����app_content
     * 
     */
    public static void updateAppContent(String expire_at, String content) {
        SQLiteDatabase dataBase = null;
        dataBase = openDatabase();
        if (dataBase != null) {
            sLogger.d("------------->update");
            String sql = "UPDATE " + TABLE_APP_CONTENT + " SET expire_at='" + expire_at
                    + "', cont= '" + content + "' WHERE _id = 1 ";
            dataBase.execSQL(sql);
        }
        closeDB(dataBase);
    }

    /**
     * ������<BR>
     * �洢ֵΪ""ʱ����Ϊnull
     * 
     * @param statement
     *            SQLiteStatement <BR>
     * @param index
     *            index<BR>
     * @param value
     *            ֵ<BR>
     */
    private static void setBindValue(SQLiteStatement statement, int index,
            String value) {
        if (value == null || value.length() == 0) {
            statement.bindNull(index);
        } else {
            statement.bindString(index, value);
        }
    }

    /**
     * ������
     * 
     * @param statement
     *            SQLiteStatement<BR>
     * @param index
     *            index<BR>
     * @param value
     *            ֵ<BR>
     */
    private static void setBindLongValue(SQLiteStatement statement, int index,
            long value) {
        statement.bindLong(index, value);
    }
}
