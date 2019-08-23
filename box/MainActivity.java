package com.example.kdm.box;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView startLabel;
    private ImageView box;
    private ImageView green;
    private ImageView black;
    private ImageView yellow;

    private int frameHeight;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;

    private int boxY;
    private int greenX;
    private int greenY;
    private int yellowX;
    private int yellowY;
    private int blackX;
    private int blackY;

    private int boxSpeed;
    private int greenSpeed;
    private int yellowSpeed;
    private int blackSpeed;

    private int score = 0;

    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;

    private boolean action_flag = false;
    private boolean start_flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sound = new SoundPlayer(this);

        scoreLabel = (TextView)findViewById(R.id.scoreLabel);
        startLabel = (TextView)findViewById(R.id.startLabel);
        box = (ImageView)findViewById(R.id.box);
        green = (ImageView)findViewById(R.id.green);
        black = (ImageView)findViewById(R.id.black);
        yellow = (ImageView)findViewById(R.id.yellow);

        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;


        boxSpeed = Math.round(screenHeight / 60F);
        greenSpeed = Math.round(screenWidth / 60F);
        yellowSpeed = Math.round(screenWidth / 36F);
        blackSpeed = Math.round(screenWidth / 45F);

        Log.v("SPEED_BOX", boxSpeed + "");
        Log.v("SPEED_GREEN", greenSpeed + "");
        Log.v("SPEED_YELLOW", yellowSpeed + "");
        Log.v("SPEED_BLACK", blackSpeed + "");


        green.setX(-80);
        green.setY(-80);
        yellow.setX(-80);
        yellow.setY(-80);
        black.setX(-80);
        black.setY(-80);

        scoreLabel.setText("Score: 0");


    }

    public void changepos(){

        hitCheck();

        //Green
        greenX -= greenSpeed;
        if (greenX < 0){
            greenX = screenWidth + 20;
            greenY = (int)Math.floor(Math.random() * (frameHeight - green.getHeight()));
        }
        green.setX(greenX);
        green.setY(greenY);

        //Black
        blackX -= blackSpeed;
        if(blackX < 0){
            blackX = screenWidth + 10;
            blackY = (int)Math.floor(Math.random() * (frameHeight - black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);

        //Yellow
        yellowX -= yellowSpeed;
        if (yellowX < 0){
            yellowX = screenWidth + 5000;
            yellowY = (int)Math.floor(Math.random() * (frameHeight - yellow.getHeight()));
        }
        yellow.setX(yellowX);
        yellow.setY(yellowY);


        if (action_flag == true){
            boxY -= boxSpeed;
        }else{
            boxY += boxSpeed;
        }

        if(boxY < 0)boxY = 0;
        if(boxY > frameHeight - boxSize)boxY = frameHeight - boxSize;

        box.setY(boxY);
        scoreLabel.setText("Score: " + score);
    }

    public void hitCheck()

    {
        //Green
        int greenCenterX = greenX + green.getWidth() / 2;
        int greenCenterY = greenY + green.getHeight() / 2;

        if (0 <= greenCenterX && greenCenterX <= boxSize && boxY <= greenCenterY && greenCenterY <= boxY + boxSize) {

            score += 10;
            greenX = -10;
            sound.playHitSound();
        }

        //Yellow
        int yellowCenterX = yellowX + yellow.getWidth() / 2;
        int yellowCenterY = yellowY + yellow.getHeight() / 2;

        if (0 <= yellowCenterX && yellowCenterX <= boxSize && boxY <= yellowCenterY && yellowCenterY <= boxY + boxSize) {

            score += 30;
            yellowX = -10;
            sound.playHitSound();
        }

        //Black
        int blackCenterX = blackX + black.getWidth() / 2;
        int blackCenterY = blackY + black.getHeight() / 2;

        if (0 <= blackCenterX && blackCenterX <= boxSize && boxY <= blackCenterY && blackCenterY <= boxY + boxSize) {

           timer.cancel();
           timer = null;
           sound.playOverSound();

            Intent intent = new Intent(getApplicationContext(),result.class);
            intent.putExtra("Score: ", score);
            startActivity(intent);
        }
    }

    public boolean onTouchEvent(MotionEvent me){
        if(start_flag == false){

            start_flag = true;

            FrameLayout frame = findViewById(R.id.frame);
            frameHeight = frame.getHeight();

            boxY = (int)box.getY();
            boxSize = box.getHeight();


            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changepos();
                        }
                    });
                }
            },0,20);

        }else{
            if(me.getAction() == MotionEvent.ACTION_DOWN){
                action_flag = true;
            }else if(me.getAction() == MotionEvent.ACTION_UP) {
                action_flag = false;
            }
        }



            box.setY(boxY);

        return true;
        }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){

        if (event.getAction() == KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
