package com.example.parkingapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button Add_Apartment = findViewById(R.id.Add_Apartment);
        Button Membership = findViewById(R.id.Membership);
        Button Login = findViewById(R.id.Login);

        Add_Apartment.setOnClickListener(v -> {
            // 아파트 등록 버튼 클릭 시 Add_Apartment 로 이동
            Intent intent = new Intent(MainActivity.this, Add_Apartment.class);
            startActivity(intent);
        });

        Membership.setOnClickListener(v -> {
            // 멤버십 가입 버튼 클릭 시 Membership 으로 이동
            Intent intent = new Intent(MainActivity.this, Membership.class);
            startActivity(intent);
        });

        Login.setOnClickListener(v -> {
            // 로그인 버튼 클릭 시 Login 으로 이동
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        });
    }
}
