package com.example.parkingapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
        Button BackButton = findViewById(R.id.button18);
        BackButton.setOnClickListener(v -> finish());

    }
    private void signUp() {
        String aptName = aptNameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        boolean isResident = residentCheckbox.isChecked();
        boolean isAdmin = adminCheckbox.isChecked();
        Log.d("Membership", "Sign up started");

        if (TextUtils.isEmpty(aptName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phoneNumber)) {
            Log.d("Membership", "Empty fields detected");
            Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isAptExist(aptName)) {
            Log.d("Membership", "Apartment does not exist");
            Toast.makeText(this, "입력된 아파트가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidPassword(aptName, password, isResident, isAdmin)) {
            Log.d("Membership", "Invalid password");
            Toast.makeText(this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        saveMemberInfo(aptName, password, phoneNumber, isResident);
        Log.d("Membership", "Saving member info");
        Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
        Log.d("Membership", "Sign up completed");
    }
    private boolean isAptExist(String aptName) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query("apartments", null, "apt_name=?", new String[]{aptName}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        database.close();
        Log.e("Membership", "Apartment exists: " + exists);
        return exists;
    }
    private boolean isValidPassword(String aptName, String password, boolean isResident, boolean isAdmin) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // 주민 또는 관리자 비밀번호 컬럼 선택
        String passwordColumn = isResident ? "resident_password" : (isAdmin ? "admin_password" : "");
        if (TextUtils.isEmpty(passwordColumn)) {
            database.close();
            Log.d("Membership", "No password column selected");
            return false;
        }
        // 아파트 이름으로 비밀번호 조회
        Cursor cursor = database.rawQuery("SELECT " + passwordColumn + " FROM apartments WHERE apt_name=?", new String[]{aptName});
        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) cursor.close();
            database.close();
            Log.d("Membership", "Password query failed or no result");
            return false;
        }
        @SuppressLint("Range") String storedPassword = cursor.getString(cursor.getColumnIndex(passwordColumn));
        cursor.close();
        boolean valid = password.equals(storedPassword);
        Log.d("Membership", "Password valid: " + valid);
        return valid;
    }
    public void saveMemberInfo(String aptName, String password, String phoneNumber, boolean isResident) {
        Log.e("Membership","success in saveMemberInfo");
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String tableName = isResident ? "residents" : "admin";
        long aptId = dbHelper.getAptIdByMBSInput(aptName,password,isResident);
        Log.e("isResident", "isResident: " + isResident);
        Log.e("Membership", "Apt ID: " + aptId);

        if (aptId == -1) {
            Log.e("Membership", "Invalid apartment ID");
            Toast.makeText(this, "아파트 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            database.close();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("apt_id",aptId);
        values.put("phone_number",phoneNumber);
        values.put("password",password);
        database.insert(tableName, null, values);
        database.close();
        Log.d("Membership", "Saving member info");
    }
}