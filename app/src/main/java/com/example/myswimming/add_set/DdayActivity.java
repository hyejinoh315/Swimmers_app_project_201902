package com.example.myswimming.add_set;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import com.example.myswimming.DayPlusActivity;
import com.example.myswimming.R;
import com.example.myswimming.adapters.DayAdapter;
import com.example.myswimming.items.DayInfo;
import com.example.myswimming.preLogin.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DdayActivity extends AppCompatActivity {
    private static final String TAG = "DdayActivity";

    private ImageView back; // 뒤로가기 버튼
    private CalendarView calendarView; // 달력 뷰
    private RecyclerView dayRecyclerView; // 일정추가되는 리사이클러뷰 (day_row 레이아웃)
    private RecyclerView.LayoutManager dayLayoutManager; // 리사이클러뷰 정렬 형태 - 리니어 레이아웃
    private ArrayList<DayInfo> dayInfoArrayList;// = new ArrayList<>(); // 리사이클러뷰가 담길 리스트
    private DayAdapter dayAdapter; // 사용할 어댑터
    private String title, memo, date; // 리사이클러뷰 내부에 사용될 변수들
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dday);
        //어레이리스트 사이즈를 확인하는 메소드에서 리스트가 없을 시 널포인트익셉션 처리
        if(dayInfoArrayList == null){
            dayInfoArrayList = new ArrayList<>();
        }
        //화면 생성하자마자 캘린더뷰의 데이터를 받아온다
        calendarView = findViewById(R.id.calendarView);
        //데이터 포맷을 지정하고
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
        final String date = f.format(new Date(calendarView.getDate()));
        //선택된 날짜에 데이터 포맷으로 얻어온 날짜값을 넣어준다
        selectedDate = date;

        back = findViewById(R.id.icon_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // 뒤로가기 아이콘을 클릭했을 때 뒤로가기 버튼 눌른 것과 같은 액션을 함
            }
        });
        //로그인한 아이디/ 선택한 날짜 값의 제이슨 데이터를 불러와서 뷰에 뿌려준다
        SharedPreferences pref = getSharedPreferences(LoginActivity.etId+"DAYS", MODE_PRIVATE);
        Gson gson =  new Gson();
        String json = pref.getString(date, ""); //선택된 날짜 값에 저장된 밸류만 불러오기
        Type type = new TypeToken<ArrayList<DayInfo>>(){}.getType();
        dayInfoArrayList = gson.fromJson(json, type);
        if(dayInfoArrayList == null){
            dayInfoArrayList = new ArrayList<>();
        }
        //이전 데이터들을 보이지 않게하고 현재 관련 데이터를 보이게 새로뿌려주는 로직을 추가함 (위는 단순히 데이터를 불러오는 역할이다)
        dayRecyclerView = findViewById(R.id.dayRecyclerView);
        dayRecyclerView.setHasFixedSize(true);
        //리니어레이아웃매니저를 사용하겠다고 선언
        dayLayoutManager = new LinearLayoutManager(DdayActivity.this);
        dayRecyclerView.setLayoutManager(dayLayoutManager);
        //어댑터를 지정한다
        dayAdapter = new DayAdapter(dayInfoArrayList);
        dayRecyclerView.setAdapter(dayAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //반환된 코드 값으로 결과를 다르게 만듦
        if (requestCode == 777) { //일정 추가에 대한 결과값을 받아오는 반환코드
            if (resultCode == RESULT_OK) {
                title = data.getStringExtra("title"); // 작성되어
                date = data.getStringExtra("date"); // 보내진
                memo = data.getStringExtra("memo"); // 인텐트값을 받는다
                //받아온 인텐트를 새 item 에 넣고, 리스트에 추가한다 (DayInfo에 값이 들어가고 그 값이 리스트에 추가된다)
                dayInfoArrayList.add(new DayInfo(title, date, memo));
                Log.e(TAG, "추가)아이템 날짜값과 선택된 날짜 값이 동일한지 확인하자"+date+"=="+selectedDate );
                //어댑터에 달라진 정보를 저장한다
                dayAdapter.notifyDataSetChanged();
                //데이터를 저장하는 메소드
                SharedPreferences pref = getSharedPreferences(LoginActivity.etId+"DAYS", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Gson gson = new Gson();
                String json = gson.toJson(dayInfoArrayList);
                //선택된 날짜(키)에 리사이클러뷰를 새로 저장한다
                editor.putString(date, json);
                //저장된정보를 반영한다
                editor.commit();
            }
        }
        if (requestCode == 555) { //일정 수정에 대한 결과값을 받아오는 반환코드
            if (resultCode == RESULT_OK) {
                title = data.getStringExtra("title"); // 작성되어
                date = data.getStringExtra("date"); // 보내진
                memo = data.getStringExtra("memo"); // 인텐트값을 받는다

                int index = data.getIntExtra("index", 0);
                Log.d(TAG, "선택한 일정 리사이클러뷰의 인덱스 값 : " + index);

                DayInfo dayInfo = new DayInfo(title, date, memo);
                Log.e(TAG, "수정)아이템 날짜값과 선택된 날짜 값이 동일한지 확인하자"+date+"=="+selectedDate );
                //인덱스 값을 통해 해당 위치의 리사이클러뷰 아이템 항목을 재 설정(수정)한다
                dayInfoArrayList.set(index/*인텐트로 다시 보내진 인덱스 값 입력*/, dayInfo);
                //어댑터에 수정된 정보를 반영한다
                dayAdapter.notifyDataSetChanged();
                //위와 동일) 데이터 저장 메소드
                SharedPreferences pref = getSharedPreferences(LoginActivity.etId+"DAYS", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Gson gson = new Gson();
                String json = gson.toJson(dayInfoArrayList);
                //선택된 날짜(키)에 제이슨 밸류(아이템)를 새로(키캆이 동일할 경우 덮어씌워짐) 저장한다
                editor.putString(date, json);
                editor.commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //클릭했음 -> 날짜 받아오기 -> 날짜같은 리스트 보여주기 -> 한번더 누르면 액티비티 전환
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //캘린더뷰를 선택했을 때 날짜값을 아래와 같은 형식으로 받아온다
                SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                String date = f.format(calendar.getTime());
                // 달력을 선택하면 그것을 토대로 년,월,일값이 얻어짐
                Log.e(TAG, "달력클릭 체인지 디버깅 " + selectedDate +"//"+date);
                //현재 선택된 날짜(키)에 저장된 data 불러오기
                SharedPreferences pref = getSharedPreferences(LoginActivity.etId+"DAYS", MODE_PRIVATE);
                Gson gson =  new Gson();
                String json = pref.getString(date, "");
                Type type = new TypeToken<ArrayList<DayInfo>>(){}.getType();
                dayInfoArrayList = gson.fromJson(json, type);
                //데이터를 불러올 때 널포인트 익셉션 처리를 해준다(리스트가 없는 경우 리스트의 사이즈를 확인하는 메소드에 에러가 뜸)
                if(dayInfoArrayList == null){
                    dayInfoArrayList = new ArrayList<>();
                }
                //이전 데이터들을 보이지 않게하고 현재 관련 데이터를 보이게 새로뿌려주는 로직을 추가함 (위는 단순히 데이터를 불러오는 역할이다)
                dayRecyclerView = findViewById(R.id.dayRecyclerView);
                dayRecyclerView.setHasFixedSize(true);
                //리니어레이아웃매니저를 사용하겠다고 선언
                dayLayoutManager = new LinearLayoutManager(DdayActivity.this);
                dayRecyclerView.setLayoutManager(dayLayoutManager);
                //어댑터를 지정한다
                dayAdapter = new DayAdapter(dayInfoArrayList);
                dayRecyclerView.setAdapter(dayAdapter);

                if (selectedDate.equals(date)) {
                    Intent intent = new Intent(DdayActivity.this, DayPlusActivity.class);
                    intent.putExtra("date", date);
                    //메모하는 액티비티로 날짜값을 인텐트 전달
                    startActivityForResult(intent, 777);
                } else {
                    selectedDate = date;
                }
                // 캘린더 있는 액티비티가 종료되면, 새롭게 생성이 되어 리스트 추가 결과가 보이지 않고,
                // 종료하지 않으면 스택으로 쌓이게 된다. 이럴 때 사용하는 것이 startActivityForResult 메소드
                // 인텐트값을 넘기며 액티비티가 전환되고, 다시 돌아올 때 새로운 액티비티가 생성되는것이 아닌
                // 리퀘스트 코드 값으로 다른 결과물을 만들 수 있음
            }
        });
        //각각의 뷰드를 클릭했을 때 수정과 삭제가 가능하도록 만듦
        dayAdapter.setOnItemClickListener(new DayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DayInfo dayInfo) {
                Log.e(TAG, "수정 onItemClick: title + memo: " + dayInfo.title + " / " + dayInfo.memo);
                Intent intent = new Intent(DdayActivity.this, DayPlusActivity.class);
                //갖고 있던 값을 인텐트로 전달하는 방법??
                intent.putExtra("index", dayInfoArrayList.indexOf(dayInfo));
                //터치 되는 인덱스 값을 인텐트에 넣어 전달(int형, 다시 받아오며 수정되는 용도로 쓰인다)
                Log.e(TAG, "수정 onItemClick: index: " + dayInfoArrayList.indexOf(dayInfo));

                //구현할것, 터치된 리스트 안의 내용물을 인덱스로 전달하기
                intent.putExtra("title", dayInfo.title);
                intent.putExtra("date", dayInfo.date);
                intent.putExtra("memo", dayInfo.memo);

                startActivityForResult(intent, 555);
            }

            @Override
            public void onDeleteClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DdayActivity.this);
                builder.setMessage("정말로 삭제하시겠습니까?\n삭제 후에는 복구가 불가능 합니다.")
                        .setCancelable(true) // 뒤로가기 눌렀을 때 취소 가능한지, (false)면 no 버튼을 눌러야 취소됨
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //받아온 포지션 값으로 해당 위치의 리사이클러뷰를 삭제한다
                                dayInfoArrayList.remove(position);
                                dayAdapter.notifyItemRemoved(position);
                                dayAdapter.notifyDataSetChanged();
                                //삭제된데이터저장
                                SharedPreferences pref = getSharedPreferences(LoginActivity.etId+"DAYS", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(dayInfoArrayList);
                                editor.putString(date, json);
                                editor.commit();
                                Log.e("삭제후 디버깅", "onClick: " + json + "/" + pref.getString(LoginActivity.etId+"day list", "")) ;

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel(); // 부정버튼 누르면 단순히 다이얼로그만 취소
                            }
                        });
                AlertDialog alert = builder.create(); // (동적) 위의 설정을 토대로 알림창을 만든다
                alert.setTitle("SWIMMERS"); // 타이틀을 지정해주고
                alert.show(); // 알림창을 보여준다
            }
        });
    }
}
