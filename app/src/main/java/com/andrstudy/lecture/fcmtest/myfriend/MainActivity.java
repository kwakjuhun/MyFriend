package com.andrstudy.lecture.fcmtest.myfriend;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.AlarmClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.andrstudy.lecture.fcmtest.myfriend.network.ApplicationController;
import com.andrstudy.lecture.fcmtest.myfriend.network.Data;
import com.andrstudy.lecture.fcmtest.myfriend.network.NetworkService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView questionTextVIew, answerTextview;
    TextToSpeech tts;
    private NetworkService networkService;

//    public void inputVoice(View view) {
//        questionTextVIew.setText("코로나 검색해줘");
//        replyAnswer("코로나 검색해줘");
//    }

    public void inputVoice(View view) {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
            final SpeechRecognizer stt = SpeechRecognizer.createSpeechRecognizer(this);
            stt.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    toast("음성 입력 시작...");
                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                }

                @Override
                public void onEndOfSpeech() {
                    toast("음성 입력 종료");
                }

                @Override
                public void onError(int error) {
                    toast("오류 발생 : " + error);
                    stt.destroy();
                }

                @Override
                public void onResults(Bundle results) {
                    final ArrayList<String> result = (ArrayList<String>) results.get(SpeechRecognizer.RESULTS_RECOGNITION);
                    questionTextVIew.setText(result.get(0));
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            replyAnswer(result.get(0));
                        }
                    }, 500);
                    stt.destroy();
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
            stt.startListening(intent);
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private void replyAnswer(String input) {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("body", input);
            Data data = new Data();
            data.setData(input);
            Call<Data> res = networkService.talk(data);
            res.enqueue(new Callback<Data>() {
                @Override
                public void onResponse(Call<Data> call, Response<Data> response) {
                    Log.d("Retrofit", response.toString());
                    Data data = response.body();
                    if (response.body() != null) {
                        selecter(data);
                    }
                }

                @Override
                public void onFailure(Call<Data> call, Throwable t) {
                    Log.e("Err", t.getMessage());
                }
            });
        } catch (Exception e) {
            toast(e.toString());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
            toast("권한이 필요합니다.");
        }
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.KOREAN);
            }
        });
        questionTextVIew = findViewById(R.id.questionTextView);
        answerTextview = findViewById(R.id.answerTextView);
        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("10.0.2.2", 8080);
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    private void selecter(Data data) {
//        answer("코로나에 대해 검색합니다.");
//        Intent intent = new Intent(this, WebViewActivity.class);
//        intent.putExtra("value", "코로나");
//        startActivity(intent);

        Handler handler = new Handler();

        switch (data.getAction()) {
            case "대답":
                answer(data.getValue());
                break;
            case "음악":
                answer("");
                handler.postDelayed(new Runnable() {
                    public void run() {


                    }
                }, 1000);
                break;
            case "알람":
//                data.getValue()
//                if(data.getValue())
//                if (data.getDept().equals("asdf")) {
//
//                    data.getValue().
//                    createAlarm("myFriend",2,1);
//                }
                break;
            case "검색":
                answer(data.getValue() + "에 대해 검색합니다.");
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("value", data.getValue());
                startActivity(intent);
                break;
            case "전화":
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                int tmp = 0;
                callIntent.setData(Uri.parse("tel:"+tmp));
                startActivity(callIntent);
                break;
            case "문자":


                break;
            case "에러":
            default:
                answer("뭐라는거야?");
                toast("인식 불가");
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void answer(String text) {
        answerTextview.setText(text);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }


    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}