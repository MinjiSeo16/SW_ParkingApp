package com.example.parkingapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class DoubleParkingCheck extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    private ArrayList<String> parkingDataList;
    long aptId = getIntent().getLongExtra("apt", -1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doublecheck);

        ListView parkingListView = findViewById(R.id.parkingListView2);
        parkingDataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, parkingDataList);
        parkingListView.setAdapter(adapter);
        displayParkingInfo((int) aptId);

        Button loginButton = findViewById(R.id.뒤로가기2);
        loginButton.setOnClickListener(v -> {
            finish();
        });

    }

    // 주차 정보를 데이터베이스에서 조회하고 화면에 표시하는 메소드
    private void displayParkingInfo(int aptId) {
        ParkingAreaDatabaseHelper dbHelper = new ParkingAreaDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM ParkingSpace WHERE apt_id = " + aptId;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int parkingSpaceNumber = cursor.getInt(cursor.getColumnIndex("parking_space_number"));
                @SuppressLint("Range") String parkingArea = cursor.getString(cursor.getColumnIndex("parking_area"));
                String parkingInfo = "주차 공간: " + parkingArea + " - 번호: " + parkingSpaceNumber;
                parkingDataList.add(parkingInfo);
            } while (cursor.moveToNext());

            cursor.close();
            adapter.notifyDataSetChanged(); // 리스트뷰 갱신
        }

        db.close();
    }
}