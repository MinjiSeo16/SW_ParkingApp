package com.example.parkingapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class View_user_parking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_user);

        Button loginButton = findViewById(R.id.button21);
        loginButton.setOnClickListener(v -> {
            finish();
        });

        ParkingAreaDatabaseHelper dbHelper = new ParkingAreaDatabaseHelper(this);
        long aptId = getIntent().getLongExtra("aptId",-1);

        if (aptId != -1) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            // 아파트 ID에 해당하는 주차 사용자 정보 조회
            Cursor cursor = db.rawQuery("SELECT * FROM ParkingUserInfo WHERE apt_id = ?", new String[]{String.valueOf(aptId)});
            TextView userInfoTextView = findViewById(R.id.userInfoTextView2);

            // 결과를 텍스트뷰에 표시
            if (cursor.moveToFirst()) {
                StringBuilder userInfo = new StringBuilder();
                do {
                    @SuppressLint("Range") String parkingArea = cursor.getString(cursor.getColumnIndex("parking_area"));
                    @SuppressLint("Range") int parkingSpaceNumber = cursor.getInt(cursor.getColumnIndex("parking_space_number"));
                    @SuppressLint("Range") String residentPhoneNumber = cursor.getString(cursor.getColumnIndex("resident_phone_number"));

                    // 텍스트뷰에 정보 추가
                    userInfo.append("주차 구역: ").append(parkingArea)
                            .append(", 공간 번호: ").append(parkingSpaceNumber)
                            .append(", 사용자 전화번호: ").append(residentPhoneNumber)
                            .append("\n");
                } while (cursor.moveToNext());

                // 텍스트뷰에 최종 결과 표시
                userInfoTextView.setText(userInfo.toString());
            } else {
                userInfoTextView.setText("아파트에 주차 중인 주민이 없습니다.");
            }
            // 커서와 데이터베이스 닫기
            cursor.close();
            db.close();
        }
    }
}