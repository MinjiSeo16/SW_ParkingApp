package com.example.parkingapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ParkingRegistration extends AppCompatActivity {

    private ParkingAreaDatabaseHelper databaseHelper;
    private EditText RegionNumInput;
    private EditText RegionInput;
    private long aptId;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        databaseHelper = new ParkingAreaDatabaseHelper(this);
        RegionInput = findViewById(R.id.editTextNumber);
        RegionNumInput = findViewById(R.id.editTextNumber2);
        Button RegistrationButton = findViewById(R.id.button6);
        Button CancelRightNowButton = findViewById(R.id.button10);
        Button BackButton = findViewById(R.id.button7);
        Intent intent = getIntent();
        aptId = intent.getLongExtra("aptId",-1);
        phoneNumber = intent.getStringExtra("phoneNumber");

        RegistrationButton.setOnClickListener(v -> {
            String Region = RegionInput.getText().toString().trim();
            String RegionNum = RegionNumInput.getText().toString().trim();
            if (TextUtils.isEmpty(RegionNum)||TextUtils.isEmpty(Region)) {
                Toast.makeText(this, "주차구역과 자리번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            // 주차 공간 정보 확인
            if (isParkingSpace((int) aptId, Region, RegionNum)) {
                // 주차정보테이블에 있을 경우 등록 -> 정보테이블에서 삭제, 사용자테이블 생성
                insertParkingUserInfo((int) aptId, Region, RegionNum, phoneNumber);
                Toast.makeText(this,"주차가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                deleteParkingSpaceInfo((int) aptId, Region, RegionNum);
            }
            else {  //주차 정보 테이블에 없을 경우
                if(isUserTableInfo((int) aptId, Region, RegionNum, phoneNumber)){  //사용자테이블에는 있을 경우
                    Toast.makeText(this,"이미 등록되어있습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(ParkingRegistration.this, ParkingOut.class);
                    intent2.putExtra("aptId", aptId);
                    intent2.putExtra("phoneNumber", phoneNumber);
                    intent2.putExtra("Region",Region);
                    intent2.putExtra("RegionNum",RegionNum);
                    startActivity(intent2);
                }
                else{  //사용자 테이블에도 없을 경우
                    Toast.makeText(this,"올바르지 않은 정보입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        CancelRightNowButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(ParkingRegistration.this, ParkingOut.class);
            intent2.putExtra("aptId", aptId);
            intent2.putExtra("phoneNumber", phoneNumber);
            startActivity(intent2);
        });
        BackButton.setOnClickListener(v -> {
            finish();
        });
    }
    private void deleteParkingSpaceInfo(int aptId, String region, String regionNum) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // 삭제할 주차 공간 정보의 조건
        String whereClause = "apt_id = ? AND parking_area = ? AND parking_space_number = ?";
        String[] whereArgs = {String.valueOf(aptId), region, regionNum};

        db.delete("ParkingSpace", whereClause, whereArgs);
        db.close();
    }
    private boolean isParkingSpace(int aptId, String region, String regionNum) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // 주차 공간 정보 확인을 위한 쿼리 조건
        String selection = "apt_id = ? AND parking_area = ? AND parking_space_number = ?";
        String[] selectionArgs = {String.valueOf(aptId), region, regionNum};

        // 주차 공간 정보 테이블에서 해당 정보가 있는지 확인
        Cursor cursor = db.query("ParkingSpace", null, selection, selectionArgs, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return exists;
    }
    private boolean isUserTableInfo(int aptId, String region, String regionNum,String phoneNumber){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String selection = "apt_id = ? AND parking_area = ? AND parking_space_number = ? AND resident_phone_number = ?";
        String[] selectionArgs = {String.valueOf(aptId), region, regionNum, phoneNumber};

        Cursor cursor = db.query("ParkingUserInfo", null, selection, selectionArgs, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }
    private void insertParkingUserInfo(int aptId, String region, String regionNum, String phoneNumber) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("apt_id", aptId);
        values.put("parking_area", region);
        values.put("parking_space_number", regionNum);
        values.put("resident_phone_number", phoneNumber);

        db.insert("ParkingUserInfo", null, values);
        db.close();
    }
}