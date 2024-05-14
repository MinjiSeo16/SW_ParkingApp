package com.example.parkingapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Membership extends AppCompatActivity {
    private EditText aptNameInput;
    private EditText passwordInput;
    private EditText phoneNumberInput;
    private CheckBox residentCheckbox;
    private CheckBox adminCheckbox;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        dbHelper = new DatabaseHelper(this);

        aptNameInput = findViewById(R.id.editTextText);
        passwordInput = findViewById(R.id.editTextTextPassword);
        phoneNumberInput = findViewById(R.id.editTextPhone);
        residentCheckbox = findViewById(R.id.residentbox);
        adminCheckbox = findViewById(R.id.adminbox);
        Button signUpButton = findViewById(R.id.register);

        signUpButton.setOnClickListener(v -> signUp());
    }
    private void signUp() {
        String aptName = aptNameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        boolean isResident = residentCheckbox.isChecked();
        boolean isAdmin = adminCheckbox.isChecked();

        if (TextUtils.isEmpty(aptName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isAptExist(aptName)||!isValidPassword(aptName, password, isResident, isAdmin)) {
            Toast.makeText(this, "입력된 아파트가 존재하지 않거나 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.saveMemberInfo(aptName, password, phoneNumber, isResident);

        Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
        finish(); //
    }
    private boolean isAptExist(String aptName) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query("apartments", null, "apt_name=?", new String[]{aptName}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    private boolean isValidPassword(String aptName, String password, boolean isResident, boolean isAdmin) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String tableName = isResident ? "residents" : (isAdmin ? "admin" : "");
        if (TextUtils.isEmpty(tableName)) return false;

        Cursor cursor = database.query(tableName, null, "apt_name=? AND password=?", new String[]{aptName, password}, null, null, null);
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }
}