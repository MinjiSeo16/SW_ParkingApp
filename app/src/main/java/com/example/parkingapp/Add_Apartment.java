package com.example.parkingapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class Add_Apartment extends AppCompatActivity {
    private EditText aptNameInput;
    private EditText adminPasswordInput;
    private EditText residentPasswordInput;
    private LinearLayout parkingAreaLayout;
    private LinearLayout doubleParkingLayout;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addapt);

        aptNameInput = findViewById(R.id.aptName);
        adminPasswordInput = findViewById(R.id.adminPassword);
        residentPasswordInput = findViewById(R.id.residentPassword);
        parkingAreaLayout = findViewById(R.id.parkingAreaLayout);
        doubleParkingLayout = findViewById(R.id.doubleParkingLayout);
        dbHelper = new DatabaseHelper(this);

        Button addParkingAreaButton = findViewById(R.id.addParkingArea);
        Button addDoubleParkingButton = findViewById(R.id.addDoubleParking);
        Button addaptButton = findViewById(R.id.addapt);

        addParkingAreaButton.setOnClickListener(v -> {
            // 주차 가능 구역 추가
            EditText newParkingAreaInput = createEditText("주차 가능 구역명 - 주차 가능 수");
            parkingAreaLayout.addView(newParkingAreaInput);
        });

        addDoubleParkingButton.setOnClickListener(v -> {
            // 2중 주차 구역 추가
            EditText newDoubleParkingAreaInput = createEditText("2중 주차 구역명 - 2중 주차 가능 수");
            EditText newDoubleParkingTimeInput = createEditText("2중 주차 가능 시간 (예: 10:00 - 12:00)");

            LinearLayout doubleParkingItemLayout = new LinearLayout(this);
            doubleParkingItemLayout.setOrientation(LinearLayout.VERTICAL);
            doubleParkingItemLayout.addView(newDoubleParkingAreaInput);
            doubleParkingItemLayout.addView(newDoubleParkingTimeInput);

            doubleParkingLayout.addView(doubleParkingItemLayout);
        });

        addaptButton.setOnClickListener(v -> {
            // 입력값 가져오기
            String aptName = aptNameInput.getText().toString();
            String adminPassword = adminPasswordInput.getText().toString();
            String residentPassword = residentPasswordInput.getText().toString();

            if (TextUtils.isEmpty(aptName) || TextUtils.isEmpty(adminPassword) || TextUtils.isEmpty(residentPassword)) {
                Toast.makeText(Add_Apartment.this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> parkingAreaInfo = getInputsFromLayout(parkingAreaLayout);
            List<String> doubleParkingInfo = getDoubleParkingInputsFromLayout(doubleParkingLayout);

            // 아파트 정보와 주차 구역 정보, 2중 주차 정보 저장 -> 고정데이터베이스에
            saveApartmentInfo(aptName, adminPassword, residentPassword, parkingAreaInfo, doubleParkingInfo);
            Toast.makeText(Add_Apartment.this, "아파트가 등록되었습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 메인 화면으로 돌아감
        });
    }

    private EditText createEditText(String hint) {
        EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setHint(hint);

        return editText;
    }

    private List<String> getInputsFromLayout(LinearLayout layout) {
        List<String> inputs = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof EditText) {
                String inputText = ((EditText) child).getText().toString().trim();
                if (!TextUtils.isEmpty(inputText)) {
                    inputs.add(inputText);
                }
            }
        }
        return inputs;
    }

    private List<String> getDoubleParkingInputsFromLayout(LinearLayout layout) {
        List<String> doubleParkingInputs = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout doubleParkingItemLayout = (LinearLayout) child;
                String areaName = ((EditText) doubleParkingItemLayout.getChildAt(0)).getText().toString().trim();
                String capacity = ((EditText) doubleParkingItemLayout.getChildAt(1)).getText().toString().trim();
                String time = ((EditText) doubleParkingItemLayout.getChildAt(2)).getText().toString().trim(); // 시간 입력란을 추가하여 시간 정보 가져오기

                if (!TextUtils.isEmpty(areaName) && !TextUtils.isEmpty(capacity)) {
                    String doubleParkingInfo = areaName + " - 2중 주차 가능 수: " + capacity;
                    if (!TextUtils.isEmpty(time)) {
                        doubleParkingInfo += " - 시간: " + time;
                    }
                    doubleParkingInputs.add(doubleParkingInfo);
                }
            }
        }
        return doubleParkingInputs;
    }

    public void saveApartmentInfo(String aptName, String adminPassword, String residentPassword,
                                  List<String> parkingAreaInfo, List<String> doubleParkingInfo) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues aptValue = new ContentValues();
        aptValue.put("apt_name", aptName);
        aptValue.put("admin_password", adminPassword);
        aptValue.put("resident_password", residentPassword);

        long aptId = database.insert("apartments", null, aptValue);

        for (String parkingArea : parkingAreaInfo) {
            ContentValues parkingValues = new ContentValues();
            parkingValues.put("apt_id", aptId);
            parkingValues.put("parking_info", parkingArea);
            database.insert("parking_areas", null, parkingValues);
        }

        for (String doubleParking : doubleParkingInfo) {
            ContentValues doubleParkingValues = new ContentValues();
            doubleParkingValues.put("apt_id", aptId);
            doubleParkingValues.put("double_parking_info", doubleParking);
            database.insert("double_parking_areas", null, doubleParkingValues);
        }

        database.close();
    }
}
