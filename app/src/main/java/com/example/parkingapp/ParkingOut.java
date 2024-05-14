package com.example.parkingapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ParkingOut extends AppCompatActivity {

    private ParkingAreaDatabaseHelper databaseHelper;
    private long aptId;
    private String phoneNumber, Region, RegionNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out);

        databaseHelper = new ParkingAreaDatabaseHelper(this);
        Button cancelButton = findViewById(R.id.button8);
        Button BackButton = findViewById(R.id.button9);
        Intent intent = getIntent();
        aptId = intent.getLongExtra("aptId",-1);
        phoneNumber = intent.getStringExtra("phoneNumber");
        Region = intent.getStringExtra("Region");
        RegionNum = intent.getStringExtra("RegionNum");

        cancelButton.setOnClickListener(v -> {
            deleteParkingUserInfo((int) aptId, Region, RegionNum, phoneNumber);
            insertParkingSpaceInfo((int)aptId, Region, RegionNum);
            Toast.makeText(this,"주차 등록이 취소되었습니다.", Toast.LENGTH_SHORT).show();
        });

        BackButton.setOnClickListener(v -> {
            finish();
        });
    }
    private void deleteParkingUserInfo(int aptId, String region, String regionNum, String phoneNumber) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // 삭제할 주민 정보의 조건
        String whereClause = "apt_id = ? AND parking_area = ? AND parking_space_number = ?AND resident_phone_number = ?";
        String[] whereArgs = {String.valueOf(aptId), region, regionNum, phoneNumber};

        db.delete("ParkingUserInfo", whereClause, whereArgs);
        db.close();
    }
    private void insertParkingSpaceInfo(int aptId, String region, String regionNum) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("apt_id", aptId);
        values.put("parking_area", region);
        values.put("parking_space_number", regionNum);

        db.insert("ParkingSpace", null, values);
        db.close();
    }

}