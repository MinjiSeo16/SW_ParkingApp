package com.example.parkingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "apartment_db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 아파트 정보 테이블 생성
        String createApartmentsTable = "CREATE TABLE apartments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "apt_name TEXT," +
                "admin_password TEXT," +
                "resident_password TEXT)";
        db.execSQL(createApartmentsTable);

        // 주차 구역 정보 테이블 생성
        String createParkingAreasTable = "CREATE TABLE parking_areas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "apt_id INTEGER," +
                "area_name TEXT," +
                "capacity INTEGER)";
        db.execSQL(createParkingAreasTable);

        // 이중 주차 구역 정보 테이블 생성
        String createDoubleParkingAreasTable = "CREATE TABLE double_parking_areas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "apt_id INTEGER," +
                "area_name TEXT," +
                "capacity INTEGER," +
                "time_slot TEXT)";
        db.execSQL(createDoubleParkingAreasTable);

        //주민과 관리자 정보 테이블 생성
        String createResidentTable = "CREATE TABLE residents(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "apt_id INTEGER," +
                "phone_number TEXT," +
                "resident_password TEXT)";
        db.execSQL(createResidentTable);

        String createAdminTable = "CREATE TABLE admin(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "apt_id INTEGER," +
                "phone_number INTEGER," +
                "admin_password TEXT)";
        db.execSQL(createAdminTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 업그레이드 로직 추가 (필요에 따라)
    }
    public void saveMemberInfo(String aptName, String password, String phoneNumber, boolean isResident) {
        SQLiteDatabase db = this.getWritableDatabase();
        String tableName = isResident ? "residents" : "admin";
        ContentValues values = new ContentValues();
        values.put("apt_id",aptName);
        values.put("phone_number", phoneNumber);
        values.put("password",password);

        long id = db.insert(tableName,null,values);
        db.close();
    }
    public long getAptIdByAdminInfo(String phoneNumber, String adminPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        long aptId = -1;
        String[] selectionArgs = {phoneNumber, adminPassword};
        Cursor cursor = db.rawQuery("SELECT apt_id FROM admin WHERE phone_number=? AND admin_password=?", selectionArgs);
        if (cursor != null && cursor.moveToFirst()) {
            int aptIdColumnIndex = cursor.getColumnIndex("apt_id");
            if (aptIdColumnIndex != -1){
                aptId = cursor.getLong(aptIdColumnIndex);
            }
            else{
                Log.e("Cursor", "Column 'apt_id' not found");
            }
        }
        db.close();
        return aptId;
    }
    public String doubleparkingtime(long aptId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT time_slot FROM double_parking_areas WHERE apt_id = ? LIMIT 1", new String[]{String.valueOf(aptId)});
        String timeSlot = null;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("time_slot");
            if (columnIndex != -1) {
                timeSlot = cursor.getString(columnIndex);
            } else {
                Log.e("TAG", "Column 'time_slot' not found in the cursor");
            }
        }
        db.close();
        return timeSlot;
    }
}
