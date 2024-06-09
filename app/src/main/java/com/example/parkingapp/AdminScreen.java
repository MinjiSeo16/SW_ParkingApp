package com.example.parkingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AdminScreen extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Handler handler;
    long aptId = -1;
    Button doubleParkingCheckButton;
    Button viewtwoResidentsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ascreen);

        databaseHelper = new DatabaseHelper(this);
        Button viewResidentsButton = findViewById(R.id.viewResidentButton);
        viewtwoResidentsButton = findViewById(R.id.button16);
        Button parkingCheckButton = findViewById(R.id.ParkingCheck);
        doubleParkingCheckButton = findViewById(R.id.DoubleParkingCheck);
        aptId = getIntent().getLongExtra("aptId",-1);

        viewResidentsButton.setOnClickListener(v -> {
            // 일반주차 주민정보 화면으로 이동
            Intent intent = new Intent(AdminScreen.this, View_user_parking.class);
            intent.putExtra("aptId",aptId);
            startActivity(intent);
        });

        parkingCheckButton.setOnClickListener(v -> {
            // 주차 공간 조회 화면으로 이동
            Intent intent = new Intent(AdminScreen.this, ParkingCheck.class);
            intent.putExtra("aptId",aptId);
            startActivity(intent);
        });

        // 매 분마다 버튼을 동적으로 갱신하여 시간에 따라 보이거나 숨김 처리
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDoubleParkingButtonsVisibility(); // 버튼 갱신
                handler.postDelayed(this, 60000); // 1분마다 반복 실행
            }
        }, 60000); // 처음 실행은 1분 후에 시작

        doubleParkingCheckButton.setOnClickListener(v->{    // 이중 주차 공간 조회
            Intent intent = new Intent(AdminScreen.this, DoubleParkingCheck.class);
            intent.putExtra("aptId",aptId);
            startActivity(intent);
        });
        viewtwoResidentsButton.setOnClickListener((v->{     // 이중 주차 주민 정보
            Intent intent = new Intent(AdminScreen.this, View_user_doubleparking.class);
            intent.putExtra("aptId",aptId);
            startActivity(intent);
        }));
    }
    private void updateDoubleParkingButtonsVisibility() {
        boolean showButton = shouldShowDoubleParkingButton();
        if (showButton) {
            doubleParkingCheckButton.setVisibility(View.VISIBLE);
            viewtwoResidentsButton.setVisibility(View.VISIBLE);
        } else {
            doubleParkingCheckButton.setVisibility(View.GONE);
            viewtwoResidentsButton.setVisibility((View.GONE));
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
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}