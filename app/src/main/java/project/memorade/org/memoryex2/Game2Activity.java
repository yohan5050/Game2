package project.memorade.org.memoryex2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;

public class Game2Activity extends AppCompatActivity {

    public ImageButton[] buttonArr = new ImageButton[9];
    public String buttonId[] = {"c0", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8"};
    TextView textView1, textView2, textView3, textView4, textView5, textView6;

    Random random;
    BackgroundTask task;
    final int randNum[] = new int[4];
    boolean[] ansCheck = new boolean[4];
    int ansCnt = 0;
    int timerVal;

    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);

        random = new Random();
        buttonArr[0] = (ImageButton) findViewById(R.id.c0);
        buttonArr[1] = (ImageButton) findViewById(R.id.c1);
        buttonArr[2] = (ImageButton) findViewById(R.id.c2);
        buttonArr[3] = (ImageButton) findViewById(R.id.c3);
        buttonArr[4] = (ImageButton) findViewById(R.id.c4);
        buttonArr[5] = (ImageButton) findViewById(R.id.c5);
        buttonArr[6] = (ImageButton) findViewById(R.id.c6);
        buttonArr[7] = (ImageButton) findViewById(R.id.c7);
        buttonArr[8] = (ImageButton) findViewById(R.id.c8);

        textView1 = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (TextView) findViewById(R.id.textView6);

        timerVal = 0;
        timer = new CountDownTimer(5000, 990) {
            @Override
            public void onTick(long millisUntilFinished) {
                textView3.setText("" + (5 - timerVal));
                timerVal++;
            }

            @Override
            public void onFinish() {
                timerVal = 0;
                MemoryTest();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 서로 다른 random number 4개(0 ~ 8)를 찾는다.
        for(int i = 0; i < 4; i++) {
            int tmp = random.nextInt(9);
            int cnt = 0;
            for(int j = 0; j < i; j++) {
                if(tmp != randNum[j]) cnt++;
            }

            if(cnt == i) randNum[i] = tmp;
            else i--;
        }

        if(task != null && task.getStatus() == AsyncTask.Status.RUNNING)
            task.cancel(true);

        task = new BackgroundTask();
        task.execute(0);
    }

    @Override
    protected void onStop() {
        super.onStop();

        task.cancel(true);
    }

    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        boolean flag = true;
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Integer doInBackground(Integer ... values) {
            // 4개의 버튼을 4번 깜박이기
            for(int i = 0; i < 8; i++) { // n번 깜박이기 위해서 n*2번 post()호출
                try {
                    Thread.sleep(1000); // 1초 간격으로
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
                //깜박이기
                publishProgress();
            }

            //1초 쉬었다가 onPostExecute()로 넘어간다.
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            return values[0];
        }

        protected void onProgressUpdate(Integer ... values) {
            if (flag) { // 4개의 버튼을 빨간색으로 바꿔준다.
                flag = false;
                for (int i = 0; i < 4; i++) {
                    int idx = randNum[i];
                    buttonArr[idx].setImageResource(R.drawable.oval2);
                }
            }
            else { // 4개의 버튼을 원래의 색으로 바꿔준다.
                flag = true;
                for (int i = 0; i < 4; i++) {
                    int idx = randNum[i];
                    buttonArr[idx].setImageResource(R.drawable.oval1);
                }
            }
        }

        protected void onPostExecute(Integer result) {
            for(int i = 0; i < 9; i++) {
                buttonArr[i].setVisibility(View.GONE);
            }
            textView1.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.VISIBLE);

            //5초 뒤에 기억력 테스트 화면으로 이동
            timer.cancel();
            timer.start();
        }

        protected void onCancelled() {
            super.onCancelled();
        }
    }

    void MemoryTest() {
        for(int i = 0; i < 9; i++) {
            buttonArr[i].setVisibility(View.VISIBLE);
        }
        textView1.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);
        textView3.setVisibility(View.GONE);

        textView1.setText("깜박이를 찾아주세요");
        textView2.setText("찾은 깜박이 수");

        // 찾은 깜박이 수 표시
        textView4.setVisibility(View.VISIBLE);
        textView5.setVisibility(View.VISIBLE);
        textView6.setVisibility(View.VISIBLE);

        for(int i = 0; i < 4; i++) {
            ansCheck[i] = false;
        }

        ansCnt = 0;
        for(int i = 0; i < 9; i++) {
            final int buttonIdx = i;
            buttonArr[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    boolean isAnswer = false;
                    System.out.println("test : 클릭하면 여기 들어와 : " + isAnswer);
                    for(int j = 0; j < 4; j++) {
                        if(buttonIdx == randNum[j]) {
                            if(!ansCheck[j]) {
                                ansCheck[j] = true;
                                buttonArr[buttonIdx].setImageResource(R.drawable.oval2);
                                isAnswer = true;
                                ansCnt++;
                                textView4.setText("" + ansCnt);
                                textView1.setText("Good!");
                                break;
                            }
                            else { // 이미 고른 답을 또 눌렀다면
                                isAnswer = true;
                            }
                        }
                    }
                    System.out.println("test : " + isAnswer);
                    if(!isAnswer) { // 고른 것이 답이 아니라면
                        textView1.setText("Wrong!");
                        System.out.println("test : 여기 들어오나?");
                    }

                    if(ansCnt == 4) { // 4개의 깜박이를 모두 찾은 경우, -> 다음 게임 시작 or 홈화면 선택.
                        textView1.setText("Congratulation!");
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Game2Activity.this);

                        // 다이얼로그 제목
                        //alertDialogBuilder.setTitle("Congratulation!");

                        // 내용
                        alertDialogBuilder
                                .setMessage("Congratulation!")
                                .setCancelable(false)
                                .setPositiveButton("Continue",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                onStop();
                                                // 게임 다시 시작
                                                Intent intent = new Intent(getApplicationContext(), Game2Activity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            }
                                        }
                                )
                                .setNegativeButton("Go Home",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // 게임 종료
                                                finish();
                                            }
                                        }
                                );

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }
                }
            });
        }
    }
}
