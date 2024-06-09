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
        Button BackButton = findViewById(R.id.button17);

        BackButton.setOnClickListener(v->{
            finish();
        });

        addParkingAreaButton.setOnClickListener(v -> {
            // 주차 가능 구역 추가
            EditText newParkingAreaInput = createEditText("주차 가능 구역명 - 주차 가능 수");
            parkingAreaLayout.addView(newParkingAreaInput);
        });

        addDoubleParkingButton.setOnClickListener(v -> {
            // 2중 주차 구역 추가
            EditText newDoubleParkingAreaInput = createEditText("2중 주차 구역명 - 2중 주차 가능 수 - 2중 주차 가능 시간 (예: 10:00 - 12:00)");

            LinearLayout doubleParkingItemLayout = new LinearLayout(this);
            doubleParkingItemLayout.setOrientation(LinearLayout.VERTICAL);
            doubleParkingItemLayout.addView(newDoubleParkingAreaInput);

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
            finish();
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
                EditText editText = (EditText) child;
                String inputText = editText.getText().toString().trim();

                if (!inputText.isEmpty()) {
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

                EditText areaNameEditText = (EditText) doubleParkingItemLayout.getChildAt(0);
                EditText capacityEditText = (EditText) doubleParkingItemLayout.getChildAt(1);
                EditText timeEditText = (EditText) doubleParkingItemLayout.getChildAt(2);

                if (areaNameEditText != null && capacityEditText != null && timeEditText != null) {
                    String areaName = areaNameEditText.getText().toString().trim();
                    String capacity = capacityEditText.getText().toString().trim();
                    String time = timeEditText.getText().toString().trim();

                    // 각 정보를 하나의 문자열로 항목별로 저장
                    String doubleParkingInfo = areaName + " - " + capacity + " - " + time;
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
            String[] parts = parkingArea.split(" - ");
            String areaName = null;
            String capacity = null;
            if (parts.length >= 2) {
                areaName = parts[0].trim();
                capacity = parts[1].trim();
            }
            ContentValues parkingValues = new ContentValues();
            parkingValues.put("apt_id", aptId);
            parkingValues.put("area_name", areaName);
            parkingValues.put("capacity", capacity);
            database.insert("parking_areas", null, parkingValues);
        }

        for (String doubleParking : doubleParkingInfo) {
            String [] d_parts = doubleParking.split(" - ");
            String d_areName = null;
            String d_capacity = null;
            String time = null;
            if (d_parts.length >=3){
                d_areName = d_parts[0].trim();
                d_capacity = d_parts[1].trim();
                time = d_parts[2].trim();
            }
            ContentValues doubleParkingValues = new ContentValues();
            doubleParkingValues.put("apt_id", aptId);
            doubleParkingValues.put("area_name", d_areName);
            doubleParkingValues.put("capacity", d_capacity);
            doubleParkingValues.put("time_slot",time);
            database.insert("double_parking_areas", null, doubleParkingValues);
        }

        database.close();
    }
}
