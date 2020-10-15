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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myswimming.R;
import com.example.myswimming.SizePlusActivity;
import com.example.myswimming.adapters.SizeReplyAdapter;
import com.example.myswimming.items.SizeReplyInfo;
import com.example.myswimming.preLogin.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SizeBoardActivity extends AppCompatActivity {

    private ImageView reply;
    private TextView backToList;
    private ArrayList<SizeReplyInfo> rArrayList;// = new ArrayList<>(); /* 새 어레이리스트 선언 뺀 이유 = 42번째줄*/
    private RecyclerView replyRecyclerView;
    private SizeReplyAdapter replyAdapter;
    private TextView title, time, fit, grade, memo, gender, age, tall, kg, torso;
    private TextView icon;//(...)more 버튼


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size_board);
        final Intent getIntent = getIntent();
        brand = getIntent.getStringExtra("brandName");
        product = getIntent.getStringExtra("productName");
        size = getIntent.getStringExtra("productSize");
        bIndex = getIntent.getIntExtra("index", 0);
        식별자 = getIntent.getStringExtra("id");

        loadData(); // -> 여기서 어레이리스트가 선언되기 때문에
        icon = findViewById(R.id.icon_more);


        backToList = findViewById(R.id.backToList);
        backToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        title = findViewById(R.id.sizeBoardTitle);
        time = findViewById(R.id.sizeBoardTime);
        fit = findViewById(R.id.sizeBoardFit);
        grade = findViewById(R.id.sizeBoardGrade);
        memo = findViewById(R.id.sizeBoardMemo);
        gender = findViewById(R.id.sizeBoardGender);
        age = findViewById(R.id.sizeBoardAge);
        tall = findViewById(R.id.sizeBoardTall);
        kg = findViewById(R.id.sizeBoardKg);
        torso = findViewById(R.id.sizeBoardTorso);

        replyRecyclerView = findViewById(R.id.repleRecyclerView);
        replyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        replyAdapter = new SizeReplyAdapter(rArrayList);
        replyRecyclerView.setAdapter(replyAdapter);
        /**/
        replyAdapter.setOnItemClickListener(new SizeReplyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final SizeReplyInfo replyInfo) {
                //Log.d("인덱스 디버깅", "onItemClick: " + rArrayList.indexOf(replyInfo));

                AlertDialog.Builder builder = new AlertDialog.Builder(SizeBoardActivity.this);
                builder.setTitle("댓글 수정");
                //EditText 삽입하기
                final EditText et = new EditText(SizeBoardActivity.this);
                final int index = rArrayList.indexOf(replyInfo);
                et.setHint("댓글 작성 시 타인에 대한 배려와 책임을 담아주세요");
                et.setTextSize(16);
                builder.setView(et);
                et.setText(replyInfo.reply);
                //저장 버튼 설정
                builder.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String memo = et.getText().toString();
                        //TODO 피드백받아서 수정시에 변경된 아이디 값을 저장할지 말지
                        SizeReplyInfo replyInfoEdit = new SizeReplyInfo(replyInfo.userId, memo, replyInfo.time);
                        rArrayList.set(index, replyInfoEdit);
                        replyAdapter.notifyDataSetChanged();
                        saveData();
                        dialog.dismiss();
                    }
                });
                //취소 버튼 설정
                builder.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //다이얼로그 창 띄우기
                builder.show();

            }

            @Override
            public void onDeleteClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SizeBoardActivity.this);
                builder.setMessage("정말로 삭제하시겠습니까?\n삭제 후에는 복구가 불가능 합니다.")
                        .setCancelable(true) // 뒤로가기 눌렀을 때 취소 가능한지, (false)면 no 버튼을 눌러야 취소됨
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { // 긍정버튼
                                removeItem(position);
                                saveData();
                                //Log.d("삭제후 디버깅", "onClick: " + getSharedPreferences("REPLY", MODE_PRIVATE).getString("REPLY", ""));
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
        /**/
        reply = findViewById(R.id.icon_reply);
        reply.setOnClickListener(new View.OnClickListener() {
            // 댓글 아이콘을 누르면 텍스트 입력이 가능한 다이얼로그가 실행된다
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SizeBoardActivity.this);
                builder.setTitle("댓글 입력창");
                //EditText 삽입하기
                final EditText et = new EditText(SizeBoardActivity.this);
                et.setHint("댓글 작성 시 타인에 대한 배려와 책임을 담아주세요");
                et.setTextSize(16);
                builder.setView(et);
                //저장 버튼 설정
                builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 받아진 에딧 텍스트 값을 리사이클러뷰 아이템 창에 넘기기
                        String memo = et.getText().toString();
                        String inTime = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm").format(new java.util.Date());

                        SharedPreferences pref = getSharedPreferences("USER", MODE_PRIVATE);
                        String details = pref.getString(LoginActivity.etId, "");
                        JsonParser parser = new JsonParser();

                        String nameP = parser.parse(details).getAsJsonObject().get("name").getAsString();

                        SizeReplyInfo replyInfo = new SizeReplyInfo(nameP, memo, inTime);
                        rArrayList.add(replyInfo);
                        replyAdapter.notifyDataSetChanged();
                        saveData();
                        dialog.dismiss();
                    }
                });
                //취소 버튼 설정
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //다이얼로그 창 띄우기
                builder.show();
            }
        });

        String bTitle = getIntent.getStringExtra("boardTitle");
        final String bTime = getIntent.getStringExtra("systemTime");
        final String bFit = getIntent.getStringExtra("sizeFit");
        final String bGrade = getIntent.getStringExtra("productGrade");
        final String bMemo = getIntent.getStringExtra("userMemo");

        String bGender = getIntent.getStringExtra("gender");
        String bAge = getIntent.getStringExtra("age");
        String bTall = getIntent.getStringExtra("tall");
        String bKg = getIntent.getStringExtra("kg");
        String bTorso = getIntent.getStringExtra("torso");

        title.setText(bTitle);
        time.setText(bTime);
        fit.setText(" 제 몸에 " + bFit);
        grade.setText(bGrade);
        memo.setText(bMemo);

        gender.setText(bGender);
        age.setText(bAge);
        tall.setText(bTall);
        kg.setText(bKg);
        torso.setText(bTorso);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu p = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.board_menu, p.getMenu());
                //p.setGravity(Gravity.RIGHT); api레벨 조절
                Log.d("아이콘메뉴 디버깅", "onClick: " + getIntent().getStringExtra("id"));

                if (LoginActivity.etId.equals(getIntent().getStringExtra("id"))) {//아이디 값이 같은 상태
                    p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            int idx = item.getItemId();

                            switch (idx) {
                                case R.id.menu_edit:
                                    Intent intent = new Intent(SizeBoardActivity.this, SizePlusActivity.class);
                                    bIndex = getIntent.getIntExtra("index", 0);
                                    String bName = getIntent.getStringExtra("brandName");
                                    String pName = getIntent.getStringExtra("productName");
                                    String pSize = getIntent.getStringExtra("productSize");
                                    intent.putExtra("index", bIndex);
                                    //해당 인덱스값을 가진 게시판의 제목이 변경된다(시간은 변경 안됨)
                                    intent.putExtra("brandName", bName);
                                    intent.putExtra("productName", pName);
                                    intent.putExtra("productSize", pSize);
                                    intent.putExtra("systemTime", bTime);
                                    intent.putExtra("sizeFit", bFit);
                                    intent.putExtra("productGrade", bGrade);
                                    intent.putExtra("userMemo", bMemo);

                                    intent.putExtra("수정", ""); //수정가능하게 인텐트 값을 확인시키기

                                    startActivityForResult(intent, 10001);

                                    return true;
                                case R.id.menu_delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SizeBoardActivity.this);
                                    builder.setTitle("SWIMMERS")
                                            .setMessage("삭제가 완료되면 복구가 불가능 합니다.\n게시물을 삭제하시겠습니까?")
                                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent();
                                                    final int index = getIntent.getIntExtra("index", 0);
                                                    intent.putExtra("index", index);
                                                    Toast.makeText(getApplicationContext(), "게시물이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                                    setResult(RESULT_OK, intent);
                                                    finish();
                                                }
                                            })
                                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    builder.show();
                                    return true;
                                case R.id.menu_share:
                                    Toast.makeText(getApplicationContext(), "공유하기", Toast.LENGTH_SHORT).show();
                                    return true;
                            }
                            return false;
                        }
                    });
                } else {//아이디값이 다른 상태
                    p.getMenu().getItem(0).setVisible(false);
                    p.getMenu().getItem(1).setVisible(false);
                    p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int idx = item.getItemId();
                            switch (idx) {
                                case R.id.menu_share:
                                    Toast.makeText(getApplicationContext(), "공유하기", Toast.LENGTH_SHORT).show();
                                    return true;
                            }
                            return false;
                        }
                    });
                }
                p.show();
            }
        });
    }

    public void removeItem(int position) {
        rArrayList.remove(position);
        replyAdapter.notifyItemRemoved(position);
    }

    private void saveData() {//리플하고 게시판 항목은 모두에게 저장되어 보여져야 하는데 어떻게 조절해야 하나
        SharedPreferences pref = getSharedPreferences("REPLY", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        //보드마다 식별이 가능한 key 값을 다르게 만든다. -> 보드 제목과 작성된 시간을 결합한다 **(제목은 수정될수 있으므로 아이디로 변경해야함)
        String key = bTitle + getIntent().getStringExtra("systemTime");
        String json = gson.toJson(rArrayList);
        //Log.d("리플 데이터 저장 디버깅", "saveData: " + json);
        //Log.d("저장하기 리플 식별자 디버깅", key);
        editor.putString(key, json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences pref = getSharedPreferences("REPLY", MODE_PRIVATE);
        Gson gson = new Gson();
        String key = bTitle + getIntent().getStringExtra("systemTime");
        String json = pref.getString(key, "");
        //Log.d("불러오기 리플 식별자 디버깅", key);
        Type type = new TypeToken<ArrayList<SizeReplyInfo>>() {
        }.getType(); // alt + enter -> Type(java.lang.reflect)클릭
        rArrayList = gson.fromJson(json, type);

        if (rArrayList == null) {
            rArrayList = new ArrayList<>();
        }
    }

    String bTitle;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10001) { //반환코드
            if (resultCode == RESULT_OK) { //RESULT_OK 결과코드 OK 반환시
//                title, time, fit, grade, memo, gender, age, tall, kg, torso
                bTitle = data.getStringExtra("boardTitle");
                String bTime = data.getStringExtra("systemTime");
                String bFit = data.getStringExtra("sizeFit");
                String bGrade = data.getStringExtra("productGrade");
                String bMemo = data.getStringExtra("userMemo");

                String bGender = data.getStringExtra("gender");
                String bAge = data.getStringExtra("age");
                String bTall = data.getStringExtra("tall");
                String bKg = data.getStringExtra("kg");
                String bTorso = data.getStringExtra("torso");

                brand = data.getStringExtra("brandName");
                Log.d("디버깅2", "onActivityResult: " + brand);
                product = data.getStringExtra("productName");
                size = data.getStringExtra("productSize");

                수정여부 = data.hasExtra("수정완료");

                title.setText(bTitle);
                time.setText(bTime);
                fit.setText(" 제 몸에 " + bFit);
                grade.setText(bGrade);
                memo.setText(bMemo);

                gender.setText(bGender);
                age.setText(bAge);
                tall.setText(bTall);
                kg.setText(bKg);
                torso.setText(bTorso);

                saveData(); // 보드에 따른 댓글 저장하기

            }
        }
    }

    String brand, product, size;
    int bIndex;
    String 식별자;
    boolean 수정여부;//무분별하게 정보가 수정되기 때문에 수정 여부를 체크하여 정보 수정을 막는다.

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        saveData();
        Log.d("뒤로가기 아이디 디버깅", "onBackPressed: " + 식별자 + "=" + LoginActivity.etId);
        Log.d("뒤로가기 불린 디버깅", "onBackPressed: " + 수정여부 );
        if(LoginActivity.etId.equals(식별자) && 수정여부) {
            Intent intent = new Intent(SizeBoardActivity.this, SizeActivity.class);
            intent.putExtra("brandName", brand);
            intent.putExtra("productName", product);
            intent.putExtra("productSize", size);
            intent.putExtra("systemTime", time.getText().toString());
            //수정식별인덱스(정수!!!!) indexNo 관련없다
            intent.putExtra("index", bIndex);
            intent.putExtra("sizeFit", fit.getText().toString());
            intent.putExtra("productGrade", grade.getText().toString());
            intent.putExtra("userMemo", memo.getText().toString());
            intent.putExtra("gender", gender.getText().toString());
            intent.putExtra("age", age.getText().toString());
            intent.putExtra("tall", tall.getText().toString());
            intent.putExtra("kg", kg.getText().toString());
            intent.putExtra("torso", torso.getText().toString());
            //수정시 반환 코드
            setResult(RESULT_FIRST_USER, intent);
        }
        finish();
    }

    /*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(LoginActivity.etId.equals(getIntent().getStringExtra("id"))){//아이디 값이 같은 상태

        }else{//아이디값이 다른 상태
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int idx = item.getItemId();

        switch (idx){
            case R.id.menu_edit:
                Toast.makeText(getApplicationContext(), "수정", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_delete:
                Toast.makeText(getApplicationContext(), "삭제", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_share:
                Toast.makeText(getApplicationContext(), "공유", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

}
