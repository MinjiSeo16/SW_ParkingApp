package com.example.parkingapp;

import android.annotation.SuppressLint;
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
                "capacity INTEGER," +
                "FOREIGN KEY (apt_id) REFERENCES apartments(id))";
        db.execSQL(createParkingAreasTable);

        // 이중 주차 구역 정보 테이블 생성
        String createDoubleParkingAreasTable = "CREATE TABLE double_parking_areas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "apt_id INTEGER," +
                "area_name TEXT," +
                "capacity INTEGER," +
                "time_slot TEXT," +
                "FOREIGN KEY (apt_id) REFERENCES apartments(id))";
        db.execSQL(createDoubleParkingAreasTable);

        //주민과 관리자 정보 테이블 생성
        String createResidentTable = "CREATE TABLE residents(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "apt_id INTEGER," +
                "phone_number TEXT," +
                "password TEXT,"+
                "FOREIGN KEY (apt_id) REFERENCES apartments(id))";
        db.execSQL(createResidentTable);

        String createAdminTable = "CREATE TABLE admin(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "apt_id INTEGER," +
                "phone_number TEXT," +
                "password TEXT," +
                "FOREIGN KEY (apt_id) REFERENCES apartments(id))";
        db.execSQL(createAdminTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 업그레이드 로직 추가 (필요에 따라)
    }

    public long getAptIdByAdminInfo(String phoneNumber, String Password) {
        SQLiteDatabase db = this.getReadableDatabase();
        long aptId = -1;
        String[] selectionArgs = {phoneNumber, Password};
        Cursor cursor_a = db.rawQuery("SELECT apt_id FROM admin WHERE phone_number=? AND password=?", selectionArgs);
        Cursor cursor_r = db.rawQuery("SELECT apt_id FROM residents WHERE phone_number=? AND password=?", selectionArgs);
        if (cursor_a != null && cursor_a.moveToFirst()) {
            int aptIdColumnIndex = cursor_a.getColumnIndex("apt_id");
            if (aptIdColumnIndex != -1){
                aptId = cursor_a.getLong(aptIdColumnIndex);
                Log.d("Login","find aptId");
            }
            else{
                Log.d("Login", "Column 'apt_id' not found");
            }
            cursor_a.close();
        }
        if (cursor_r != null &&cursor_r.moveToFirst()) {
            int aptIdColumnIndex = cursor_r.getColumnIndex("apt_id");
            if (aptIdColumnIndex != -1){
                aptId = cursor_r.getLong(aptIdColumnIndex);
                Log.d("Login","find aptId");
            }
            else{
                Log.d("Login", "Column 'apt_id' not found");
            }
            cursor_r.close();
        }
        db.close();
        return aptId;
    }

    @SuppressLint("Range")
    public long getAptIdByMBSInput(String aptName, String password, boolean isResident) {
        SQLiteDatabase db = this.getReadableDatabase();
        long aptId = -1;

        // apt_name을 통해 apartments 테이블에서 apt_id 조회
        Cursor aptCursor = db.rawQuery("SELECT id FROM apartments WHERE apt_name=?", new String[]{aptName});
        if (aptCursor != null && aptCursor.moveToFirst()) {
            aptId = aptCursor.getLong(aptCursor.getColumnIndex("id"));
            Log.e("Membership", "Apartment ID found: " + aptId);
            aptCursor.close();
        } else {
            if (aptCursor != null) {
                aptCursor.close();
            }
            db.close();
            Log.e("Membership", "Apartment not found");
            return aptId; // 아파트가 존재하지 않는 경우
        }
        String tableName = isResident ? "residents" : "admin";
        Cursor cursor = db.rawQuery("SELECT apt_id FROM " + tableName + " WHERE apt_id=? AND password=?", new String[]{String.valueOf(aptId)});
        boolean isValid = cursor != null;
        return isValid ? aptId : -1;
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