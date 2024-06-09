package com.example.parkingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class Residentscreen extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    Button DoubleviewParkingButton;
    Button DoubleRCButton;
    Button ViewdoubleUserInfoButton;
    long aptId =-1;
    String phoneNumber = getIntent().getStringExtra("phoneNumber");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rscreen);

        databaseHelper = new DatabaseHelper(this);
        Button viewParkingStatusButton = findViewById(R.id.button);
        Button RegistercancelButton = findViewById(R.id.button2);
        ViewdoubleUserInfoButton = findViewById(R.id.button3);
        DoubleviewParkingButton = findViewById(R.id.button4);
        DoubleRCButton = findViewById(R.id.button5);
        aptId = getIntent().getLongExtra("apt",-1);

        viewParkingStatusButton.setOnClickListener(v -> {
            // 주차 공간 조회 화면으로 이동
            Intent intent = new Intent(Residentscreen.this, ParkingCheck.class);
            intent.putExtra("aptId",aptId);
            startActivity(intent);
        });
        
        RegistercancelButton.setOnClickListener(v -> {      //주차 등록/취소 버튼 클릭 시 처리
            startActivity(new Intent(Residentscreen.this, ParkingRegistration.class));
            Intent intent = new Intent(Residentscreen.this, ParkingRegistration.class);
            intent.putExtra("aptId",aptId);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDoubleParkingButtonsVisibility(); // 버튼 갱신
                handler.postDelayed(this, 60000); // 1분마다 반복 실행
            }
        }, 60000); // 처음 실행은 1분 후에 시작    // 2중주차 등록/취소 버튼 시간대에 따라 동적으로 표시

        ViewdoubleUserInfoButton.setOnClickListener(v->{
            Intent intent = new Intent(Residentscreen.this, View_user_doubleparking.class);
            intent.putExtra("aptId",aptId);
            startActivity(intent);
        });
        DoubleviewParkingButton.setOnClickListener(v->{ //이중 주차 공간 조회
            Intent intent = new Intent(Residentscreen.this, DoubleParkingCheck.class);
            intent.putExtra("aptId",aptId);
            startActivity(intent);
        });

        DoubleRCButton.setOnClickListener(v -> {        // 이중주차 등록/취소 버튼 클릭 시 처리
            startActivity(new Intent(Residentscreen.this, DoubleParkingRegistration.class));
            Intent intent = new Intent(Residentscreen.this, DoubleParkingRegistration.class);
            intent.putExtra("aptId",aptId);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);
        });
    }
    private void updateDoubleParkingButtonsVisibility() {
        boolean showButton = shouldShowDoubleParkingButton();
        if (showButton) {
            DoubleviewParkingButton.setVisibility(View.VISIBLE);
            DoubleRCButton.setVisibility(View.VISIBLE);
            ViewdoubleUserInfoButton.setVisibility(View.VISIBLE);
        } else {
            DoubleviewParkingButton.setVisibility(View.GONE);
            DoubleRCButton.setVisibility(View.GONE);
            ViewdoubleUserInfoButton.setVisibility(View.GONE);
        }
    }
    private boolean shouldShowDoubleParkingButton() {
        DatabaseHelper databaseHelper1 = new DatabaseHelper(getApplicationContext());
        String timeSlot = databaseHelper1.doubleparkingtime(aptId);
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        if(!timeSlot.isEmpty()) {
            String[] timeRange = timeSlot.split(" - ");
            String startTime = timeRange[0];
            String endTime = timeRange[1];
            String[] startHourMinute = startTime.split(":");  //시간 분 분리
            int startHour = Integer.parseInt(startHourMinute[0]);
            int startMinute = Integer.parseInt(startHourMinute[1]);
            String[] endHourMinute = endTime.split(":");
            int endHour = Integer.parseInt(endHourMinute[0]);
            int endMinute = Integer.parseInt(endHourMinute[1]);

            // 현재 시간이 이중주차 가능 시간대에 포함되는지 확인
            if (currentHour > startHour || (currentHour == startHour && currentMinute >= startMinute)) {
                if (currentHour < endHour || (currentHour == endHour && currentMinute < endMinute)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // DatabaseHelper 사용 종료
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}