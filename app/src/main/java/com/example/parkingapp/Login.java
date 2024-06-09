package com.example.parkingapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private EditText passwordInput;
    private EditText phoneNumberInput;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        passwordInput = findViewById(R.id.password);
        phoneNumberInput = findViewById(R.id.phone);
        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(v -> login());
        Button BackButton = findViewById(R.id.button19);
        BackButton.setOnClickListener(v -> finish());
    }
    private void login() {
        String password = passwordInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        long aptId = dbHelper.getAptIdByAdminInfo(phoneNumber, password);

        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "비밀번호와 전화번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isValidUser(password, phoneNumber)) {
            boolean isAdmin = isAdminUser(password);
            if (isAdmin) {
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, AdminScreen.class);
                intent.putExtra("aptId", aptId);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);

            } else {
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, Residentscreen.class);
                intent.putExtra("aptId", aptId);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "로그인 실패. 비밀번호 또는 전화번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
        }

    }
    private boolean isValidUser(String password, String phoneNumber) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor_r = database.query("residents", null, "password=? AND phone_number=?", new String[]{password, phoneNumber}, null, null, null);
        Cursor cursor_a = database.query("admin", null, "password=? AND phone_number=?", new String[]{password, phoneNumber}, null, null, null);

        if (cursor_r.getCount() > 0 || cursor_a.getCount() > 0){
            cursor_a.close();
            cursor_r.close();
            Log.d("Login","isValidUser");
            return true;
        }
        else{
            cursor_a.close();
            cursor_r.close();
            Log.d("Login","is InValidUser");
            return false;
        }
    }
    private boolean isAdminUser(String password) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query("admin", null, "password=?", new String[]{password}, null, null, null);
        boolean isAdmin = cursor.getCount() > 0;
        cursor.close();
        Log.d("Login","isAdminUser or isresidentUser completed");
        return isAdmin;
    }
}