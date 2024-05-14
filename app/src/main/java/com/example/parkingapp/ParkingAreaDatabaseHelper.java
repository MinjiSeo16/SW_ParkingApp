package com.example.parkingapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ParkingAreaDatabaseHelper extends SQLiteOpenHelper {
    private static final String NEW_DATABASE_NAME = "new_apartment_db";
    private static final int NEW_DATABASE_VERSION = 1;
    public ParkingAreaDatabaseHelper(Context context) {
        super(context, NEW_DATABASE_NAME, null, NEW_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 주차 공간 정보 테이블 생성
        String createParkingSpaceTable = "CREATE TABLE IF NOT EXISTS ParkingSpace (" +
                "apt_id INTEGER," +
                "parking_area TEXT," +
                "parking_space_number INTEGER)";
        db.execSQL(createParkingSpaceTable);

        // 이중 주차 공간 정보 테이블 생성
        String createDoubleParkingSpaceTable = "CREATE TABLE IF NOT EXISTS DoubleParkingSpace (" +
                "apt_id INTEGER," +
                "parking_area TEXT," +
                "parking_space_number INTEGER)";
        db.execSQL(createDoubleParkingSpaceTable);

        // 주차 사용자 정보 테이블 생성
        String ParkingUserInfoTable = "CREATE TABLE IF NOT EXISTS ParkingUserInfo (" +
                "apt_id INTEGER," +
                "parking_area TEXT," +
                "parking_space_number INTEGER," +
                "resident_phone_number TEXT)";
        db.execSQL(ParkingUserInfoTable);

        // 이중 주차 사용자 정보 테이블 생성
        String DoubleParkingUserInfoTable = "CREATE TABLE IF NOT EXISTS DoubleParkingUserInfo (" +
                "apt_id INTEGER," +
                "parking_area TEXT," +
                "parking_space_number INTEGER," +
                "resident_phone_number TEXT)";
        db.execSQL(DoubleParkingUserInfoTable);

        // 이전 데이터베이스로부터 주차 구역 정보를 가져와 초기화
        initializeParkingSpacesFromOldDatabase(db);
    }

    private void initializeParkingSpacesFromOldDatabase(SQLiteDatabase newDB) {
        DatabaseHelper oldDatabaseHelper = new DatabaseHelper(this.getContext());
        SQLiteDatabase oldDB = oldDatabaseHelper.getReadableDatabase();

        // 주차 구역 정보 가져오기
        Cursor cursor = oldDB.rawQuery("SELECT apt_id, area_name,capacity FROM parking_areas", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int aptId = cursor.getInt(cursor.getColumnIndex("apt_id"));
                @SuppressLint("Range") String areaName = cursor.getString(cursor.getColumnIndex("area_name"));
                @SuppressLint("Range") int capacity = cursor.getInt(cursor.getColumnIndex("capacity"));

                // 주차 가능 수(용량)에 따라 자리 번호 초기화
                for (int i = 1; i <= capacity; i++) {
                    ContentValues values = new ContentValues();
                    values.put("apt_id", aptId);
                    values.put("parking_area", areaName);
                    values.put("parking_space_number", i);

                    // 주차 공간 정보 테이블에 삽입
                    newDB.insert("ParkingSpace", null, values);
                }
            }
            cursor.close();
        }
        //이중주차 구역 정보 가져오기
        Cursor cursor1 = oldDB.rawQuery("SELECT apt_id, area_name,capacity FROM double_parking_areas", null);
        if (cursor1 != null) {
            while (cursor1.moveToNext()) {
                @SuppressLint("Range") int aptId = cursor1.getInt(cursor1.getColumnIndex("apt_id"));
                @SuppressLint("Range") String areaName = cursor1.getString(cursor1.getColumnIndex("area_name"));
                @SuppressLint("Range") int capacity = cursor1.getInt(cursor1.getColumnIndex("capacity"));

                // 주차 가능 수(용량)에 따라 자리 번호 초기화
                for (int i = 1; i <= capacity; i++) {
                    ContentValues values = new ContentValues();
                    values.put("apt_id", aptId);
                    values.put("parking_area",areaName);
                    values.put("parking_space_number", i);

                    // 주차 공간 정보 테이블에 삽입
                    newDB.insert("DoubleParkingSpace", null, values);
                }
            }
            cursor1.close();
        }

        // 이전 데이터베이스 닫기
        oldDB.close();
    }

    private Context getContext() {
        return null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 데이터베이스 업그레이드 로직 추가 (필요에 따라 구현)
    }
}