package com.example.myswimming;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myswimming.preLogin.LoginActivity;

import java.util.ArrayList;
import java.util.Locale;

public class TrainingActivity extends AppCompatActivity {

    private ImageView back; // 트레이닝 레이아웃에 있는 뒤로가기 아이콘(이미지)

    private EditText second;//, num;
    //int number;//스트링값인 횟수를 인트값으로 파싱해서 받을 변수
    private TextView fixedInfo, numInfo; // 사용자가 설정해 놓은 정보, 숫자 증가하는 정보 텍스트뷰
    private TextView countdown;
    private Button setButton, startPause, reset, info;

    private CountDownTimer countDownTimer;

    private boolean timerRunning;

    private long startTimeInMillis;
    private long timeLeftInMillis;// = START_TIME_IN_MILLIS; ->쉐어드저장하기때문에 선언 no
    private long endTime;

    //음성인식에 필요한 변수 선언
    Intent iRe;
    SpeechRecognizer recognizer;
    final int PERMISSION = 1;
    Button sttBtn;
    TextView textView;

    //러닝상태 감지하여 보이스를 온해줄 스레드와 핸들러
    Thread voiceThread;
    Handler voiceHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        //안드로이드 6.0버전 부터는 퍼미션을 따로 체크해줘야하기 때문에!
        if (Build.VERSION.SDK_INT >= 23) {
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }
        textView = findViewById(R.id.sttResult);
        sttBtn = findViewById(R.id.sttStart);

        fixedInfo = findViewById(R.id.trainingInfo); // 사용자가 세팅한 정보를 고정해서 보여주기
        numInfo = findViewById(R.id.numberOfTimesInfo); // 시작하면 1회~사용자 설정횟수까지 핸들러로 UI

        //음성인식에 필요한 변수 초기화, 사용자에게 음성을 요구하고 음성 인식기를 통해 전송하는 활동을 시작
        iRe = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //인텐트에 사용되는 여분의 키
        iRe.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        //인식할 언어를 한글로
        iRe.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        sttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //새로운 리코그나이저 생성
                recognizer = SpeechRecognizer.createSpeechRecognizer(TrainingActivity.this);
                recognizer.setRecognitionListener(listener);
                //듣기 시작
                recognizer.startListening(iRe);
            }
        });
        SharedPreferences pref = getSharedPreferences(LoginActivity.etId + "timers", MODE_PRIVATE);
        if (!pref.getBoolean("도움말확인", false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TrainingActivity.this);
            builder.setTitle("트레이닝 타이머 사용법")
                    .setMessage("\n1. 트레이닝 시간을 입력한 후 (set) 버튼을 클릭하여 설정을 완료합니다.\n\n" +
                            "2. START 버튼을 누르면 타이머가 설정된 시간에 맞춰 작동되고 PAUSE 버튼을 누르면 타이머가 멈춥니다.\n\n" +
                            "3. RESET 버튼을 누르면 사용자가 마지막으로 설정한 트레이닝 시간으로 되돌려 줍니다.\n\n" +
                            "4. 시계를 터치하면 음성 인식이 시작됩니다. 음성으로 x초/시작/멈춤/종료 명령을 내려보세요!\n\n" +
                            "5. 다른 운동을 할 경우에도 이용해 보세요.");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }

        back = findViewById(R.id.icon_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // 뒤로가기 아이콘을 클릭했을 때 뒤로가기 버튼 눌른 것과 같은 액션을 함
            }
        });

        second = findViewById(R.id.edit_second);
        //num = findViewById(R.id.edit_numberOfTimes); // 입력값

        setButton = findViewById(R.id.button_set);
        countdown = findViewById(R.id.countdown);

        countdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //새로운 리코그나이저 생성
                recognizer = SpeechRecognizer.createSpeechRecognizer(TrainingActivity.this);
                recognizer.setRecognitionListener(listener);
                //듣기 시작
                recognizer.startListening(iRe);
            }
        });

        startPause = findViewById(R.id.start_pause);
        reset = findViewById(R.id.reset);
        info = findViewById(R.id.button_info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TrainingActivity.this);
                builder.setTitle("트레이닝 타이머 사용법")
                        .setMessage("\n1. 트레이닝 시간을 입력한 후 (set) 버튼을 클릭하여 설정을 완료합니다.\n\n" +
                                "2. START 버튼을 누르면 타이머가 설정된 시간에 맞춰 작동되고 PAUSE 버튼을 누르면 타이머가 멈춥니다.\n\n" +
                                "3. RESET 버튼을 누르면 사용자가 마지막으로 설정한 트레이닝 시간으로 되돌려 줍니다.\n\n" +
                                "4. 시계를 터치하면 음성 인식이 시작됩니다. 음성으로 x초/시작/멈춤/종료 명령을 내려보세요!\n\n" +
                                "5. 다른 운동을 할 경우에도 이용해 보세요.");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String inputNum = num.getText().toString();
                //Log.e("셋버튼 디버깅1", "onClick: " + inputNum);
                String input = second.getText().toString();
                Log.e("셋버튼 디버깅2", "onClick: " + input);
                if (/*inputNum.length() == 0 || */input.length() == 0) {
                    Toast.makeText(TrainingActivity.this, "운동 횟수와 회당 시간을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                //number = Integer.parseInt(inputNum); // 받아온 숫자를 int 값으로 파싱
                //Log.e("셋버튼 디버깅3", "onClick: " + number);
                long millisInput = Long.parseLong(input) * 1000; // 입력시간을 초단위로 변경, 60곱하면(60000) 분단위
                Log.e("셋버튼 디버깅4", "onClick: " + millisInput);
                if (/*number == 0 || */millisInput == 0) {
                    Toast.makeText(TrainingActivity.this, "올바른 운동 횟수와 회당 시간을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(/*number, */millisInput);
                second.setText("");
                //fixedInfo.setText(input + "초 인터벌 " + number + "회"); //인포에 값이 담기고
                //fixedInfo.setVisibility(View.VISIBLE); // 사용자가 잘 설정했나 확인이 가능하게 화면에 띄워진다
                //num.setText("");
            }
        });

        startPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("퍼즈 디버깅", "onClick: " + countdown.getText().toString() + number);

                if (timerRunning) {
                    pauseTimer();
                } else {
                    if (countdown.getText().toString().equals("00:00")/* || number < 1*/) {
                        //운동 종료 후 시간이 초기화 되는데 설정 값이 저장되어 있기 때문에 (루프) 재실행을 방지한다
                        Toast.makeText(TrainingActivity.this, "타이머를 설정해 주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        startTimer();
                    }
                }
            }

        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        voiceHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (timerRunning) {
                    //새로운 리코그나이저 생성
                    recognizer = SpeechRecognizer.createSpeechRecognizer(TrainingActivity.this);
                    recognizer.setRecognitionListener(listener);
                    //듣기 시작
                    recognizer.startListening(iRe);
                }
            }
        };

        voiceThread = new Thread() {
            //run은 jvm이 쓰레드를 채택하면, 해당 쓰레드의 run메서드를 수행한다.
            public void run() {
                super.run();
                while (true) {
                    try {
                        Thread.sleep(5000);
                        voiceHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        voiceThread.start();
    }

    private void setTime(/*int numbers, */long milliseconds) {
        //number = numbers;
        startTimeInMillis = milliseconds;
        resetTimer();
        closeKeyBoard();
        Log.d("불린 값 디버깅", "setTime: " + timerRunning);
    }

    /*    private Handler numHandler = new Handler() { // 카운트 다운 핸들러
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        resetTimer();
                        startTimer();
                        break;
                    case 1:
                        resetTimer();
                        countdown.setText("00:00");
                        fixedInfo.setVisibility(View.INVISIBLE); // 사용자가 설정한 값을 띄우는 인포메이션 소멸
                        break;
                }
            }

        };
    */
    private void startTimer() {
        //number--;//횟수 감소

        endTime = System.currentTimeMillis() + timeLeftInMillis;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                startPause.setText("start");
                updateButtons();
                Log.d("불린 값 디버깅", "startTimer:onFinish: " + timerRunning);
                /*여기부터 반복구간, 입력받아진 값을 통해 몇회 반복할지 설정하자

                if (number != 0) {
                    numHandler.sendEmptyMessage(0);
                } else {
                    numHandler.sendEmptyMessage(1);
                }
*/
                Toast.makeText(TrainingActivity.this,
                        "운동이 끝났습니다 시간을 다시 설정해 주세요", Toast.LENGTH_SHORT).show();
            }
        }.start();

        timerRunning = true;
        updateButtons();

    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        updateButtons();
    }

    private void resetTimer() {
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();
        updateButtons();
        Log.d("불린 값 디버깅", "resetTimer: " + timerRunning);
        fixedInfo.setVisibility(View.INVISIBLE);
    }

    private void updateCountDownText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600; //3600초 == 1시간
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);

        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }
        countdown.setText(timeLeftFormatted);
    }

    private void updateButtons() { // == upDateWatchInterface
        if (timerRunning) {
            second.setVisibility(View.INVISIBLE);
            setButton.setVisibility(View.INVISIBLE);

            reset.setVisibility(View.INVISIBLE);
            startPause.setText("pause");
        } else {
            second.setVisibility(View.VISIBLE);
            setButton.setVisibility(View.VISIBLE);

            startPause.setText("start");
            if (timeLeftInMillis < 1000) {
                startPause.setVisibility(View.INVISIBLE);
            } else {
                startPause.setVisibility(View.VISIBLE);
            }
        }

        if (timeLeftInMillis < startTimeInMillis && !timerRunning) {
            reset.setVisibility(View.VISIBLE);
        } else {
            reset.setVisibility(View.INVISIBLE);
        }
    }

    //초를 입력받은 창에 숫자를 입력하고 시간을 설정하는 버튼을 눌러도 키보드가 내려가지 않아서 인터페이스를 좋게 만들기 위한 메소드를 생성
    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //나갔다 들어올 때 타이머 저장할 쉐어드
        SharedPreferences pref = getSharedPreferences(LoginActivity.etId + "timers", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putLong("startTimeInMillis", startTimeInMillis);
        editor.putLong("millisLeft", timeLeftInMillis);
        editor.putBoolean("timerRunning", timerRunning);
        editor.putLong("endTime", endTime);
        editor.putBoolean("도움말확인", true);
        //editor.putInt("횟수", number);

        editor.apply();

        /**/
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        //Log.d("onStop 남은 횟수 디버깅", "onStop: " + number);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getSharedPreferences(LoginActivity.etId + "timers", MODE_PRIVATE);

        startTimeInMillis = pref.getLong("startTimeInMillis", 0);
        timeLeftInMillis = pref.getLong("millisLeft", startTimeInMillis);
        timerRunning = pref.getBoolean("timerRunning", false);
        //number = pref.getInt("횟수", 1);

        updateButtons();
        updateCountDownText();

        if (timerRunning) {
            endTime = pref.getLong("endTime", 0);
            timeLeftInMillis = endTime - System.currentTimeMillis();

            if (timeLeftInMillis < 0) {
                timeLeftInMillis = 0;
                timerRunning = false;
                updateCountDownText();
                updateButtons();
            } else {
                startTimer();
            }
        }
        Log.d("불린 값 디버깅", "onStart: " + timerRunning);
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            // 사용자가 말하기 시작할 준비가되면 호출된다
            if (!timerRunning) {
                Toast.makeText(getApplicationContext(),
                        "음성인식을 시작합니다", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),
                        "레코더 대기중", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onBeginningOfSpeech() {
            // 사용자가 말하기 시작했을 때 호출된다
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // 입력받는 소리의 크기를 알려줌
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // 사용자가 말을 시작하고 인식이 된 단어를 buffer 에 담는다
        }

        @Override
        public void onEndOfSpeech() {
            // 사용자가 말하기를 중지하면 호출됨
        }

        @Override
        public void onError(int error) {
            // 네트워크 또는 인식 오류가 발생했을 때 호출됨
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트워크 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                //case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                //    message = "RECOGNIZER 바쁨";
                //    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버 오류";
                    break;
                //case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                //    message = "말하기 시간초과";
                //    break;
                default:
                    message = "알 수 없는 오류";
                    break;
            }
            if (!timerRunning) {
                Toast.makeText(getApplicationContext(),
                        "에러가 발생하였습니다 : " + message + "\n잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
            //새로운 리코그나이저 생성
/*            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recognizer = SpeechRecognizer.createSpeechRecognizer(TrainingActivity.this);
            recognizer.setRecognitionListener(listener);
            //듣기 시작
            recognizer.startListening(iRe);
*/
        }

        @Override
        public void onResults(Bundle results) {
            Log.d("불린 값 디버깅", "음성 onResults: " + timerRunning);
            Log.e("리셋버튼 값 디버깅", ": " + (timeLeftInMillis < startTimeInMillis) + "//" + !timerRunning);
            // 인식 결과가 준비되면 호출됨
            // 음성인식된 결과를 ArrayList 로 모아온다
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            textView.setText(matches.get(0));
//얻어온 결과를 공백단위로 잘라서 배열에 넣는다
            int x = 0; //몇초가 담길 변수
            String[] array = matches.get(0).split(" ");
            for (int i = 0; i < array.length; i++) {
                Log.d("음성인식 결과", "onResults: " + array[i]);
                //array[i].contains("초"); timerRunning = false;
                //array[i].contains("시작"); timerRunning = false;
                //array[i].contains("멈춤"); timerRunning = true;
                //array[i].contains("취소"); timerRunning = false; reset.getVisibility(); //버튼이 보일 때
                if (array[i].contains("초")) {
                    try {
                        String 결과 = array[i].substring(0, array[i].length() - 1);
                        Log.e("결과디버깅", "onResults: " + Integer.parseInt(결과));
                        x = Integer.parseInt(결과);
                    } catch (RuntimeException e) {
                        // 런타임 익셉션을 모두 잡아준다
                    } catch (Exception e) {
                        //
                    }
                }
            }
            Log.e("초에서 파싱된 결과 디버깅", "onResults: " + x);
            Message msg = new Message();


            if (x != 0 && matches.get(0).contains("초") && matches.get(0).contains("시작") && !timerRunning) {
                msg.setTarget(sec_start_handler);
                msg.arg1 = x;
                msg.sendToTarget();
            } else if (x != 0 && matches.get(0).contains("초") && !matches.get(0).contains("시작") && !timerRunning) {
                msg.setTarget(sec_handler);
                msg.arg1 = x;
                msg.sendToTarget();
            } else if (!matches.get(0).contains("초") && matches.get(0).contains("시작")
                    && !timerRunning && !countdown.equals("00:00")) {
                msg.setTarget(start_handler);
                msg.sendToTarget();
            } else if (matches.get(0).contains("멈춤") && timerRunning) {
                msg.setTarget(pause_handler);
                msg.sendToTarget();
            } else if (matches.get(0).contains("종료") || matches.get(0).contains("취소")) {
                msg.setTarget(end_handler);
                msg.sendToTarget();
            } else {
                Toast.makeText(getApplicationContext(),
                        "잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
/*                //새로운 리코그나이저 생성
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                recognizer = SpeechRecognizer.createSpeechRecognizer(TrainingActivity.this);
                recognizer.setRecognitionListener(listener);
                //듣기 시작
                recognizer.startListening(iRe);
            }


/* timeLeftInMillis < startTimeInMillis && !timerRunning)  <- 리셋 버튼이 보이는 유일한 조건.

            // textView 에 setText 로 음성인식된 결과를 수정해줍니다.
            for(int i = 0; i < matches.size() ; i++){
                textView.setText(matches.get(i));
            }
/* todo 결과값을 파싱
* 1. xx초 = 초 입력 + 셋버튼
* 2. 시작 = a. 처음부터(스타트) b. 중간부터(스타트) c. 끝난 후 (리셋+스타트)
* 3. 멈춤 = a. 스타트중(멈춤)
* 4. 취소 = a. 끝난 후 b. 중간(=멈춤)도중 = clear
* https://coding-factory.tistory.com/126 문자열 단위로 자르는 방법
*/
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // 부분 인식 결과를 사용할 수 있을 때 호출됨
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // 향후 이벤트를 추가하기 위해 예약됨
        }
    };

    private Handler sec_start_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "운동을 시작합니다", Toast.LENGTH_SHORT).show();

            int x = msg.arg1;
            long millisInput = x * 1000;//입력시간을초단위로변경,60곱하면(60000)분단위
            setTime(millisInput);
            second.setText("");
            startTimer();
        }
    };
    private Handler sec_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "시간을 셋팅하였습니다", Toast.LENGTH_SHORT).show();

            int x = msg.arg1;
            long millisInput = x * 1000;//입력시간을초단위로변경,60곱하면(60000)분단위
            setTime(millisInput);
            second.setText("");
        }
    };
    private Handler start_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "운동을 시작합니다", Toast.LENGTH_SHORT).show();

            startTimer();
        }
    };
    private Handler pause_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "운동을 멈춥니다", Toast.LENGTH_SHORT).show();

            pauseTimer();
        }
    };
    private Handler end_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "운동을 취소하고 시간을 다시 셋팅하였습니다", Toast.LENGTH_SHORT).show();

            if (timerRunning) {
                pauseTimer();
            }
            resetTimer();
        }
    };

    public void soundPlay() {
        final SoundPool sp = new SoundPool(1, // 최대 음악파일의 개수
                AudioManager.STREAM_MUSIC, // 스트림 타입
                0); // 음질 - 기본값:0

        // 각각의 재생하고자하는 음악을 미리 준비한다
        final int soundID = sp.load(this, // 현재 화면의 제어권자
                R.raw.iphonebell,    // 음악 파일
                0);    // 우선순위

        sp.play(soundID, 1, 1, 0, 0, 1);

/*              sp.play(soundID, // 준비한 soundID
                        1,         // 왼쪽 볼륨 float 0.0(작은소리)~1.0(큰소리)
                        1,         // 오른쪽볼륨 float
                        0,         // 우선순위 int
                        0,     // 반복회수 int -1:무한반복, 0:반복안함
                        0.5f);    // 재생속도 float 0.5(절반속도)~2.0(2배속)
*/
    }
}
