package com.example.musicapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Timer;

public class MainActivity extends AppCompatActivity{

    private  static int musicNum=0;//确定哪首歌播放
    private static ImageView iv_cover;
    private static SeekBar sb;
    private static TextView tv_progress,tv_total,musicName;
    private Button btn_play,btn_pause,btn_continue,btn_exit;
    public static ObjectAnimator animator;//声明一个动画组件animator
    public  static MusicService.MusicControl control;//声明MusicService中的音乐控制器
    private Button back,btn_last,btn_next;

    public ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            control=(MusicService.MusicControl)iBinder;;//实例化control
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,MusicList.class);
                startActivity(intent);
            }
        });

        musicName=findViewById(R.id.musicName);
        iv_cover = findViewById(R.id.iv_cover);
        sb = findViewById(R.id.sb);
        tv_progress = findViewById(R.id.tv_progress);
        tv_total = findViewById(R.id.tv_total);

        btn_play = findViewById(R.id.btn_play);
        btn_pause = findViewById(R.id.btn_pause);
        btn_continue = findViewById(R.id.btn_continue);
        btn_exit = findViewById(R.id.btn_exit);
        btn_last=findViewById(R.id.btn_last);
        btn_next=findViewById(R.id.btn_next);

        OnClick myOnclick = new OnClick();
        btn_play.setOnClickListener(myOnclick);
        btn_pause.setOnClickListener(myOnclick);
        btn_continue.setOnClickListener(myOnclick);
        btn_exit.setOnClickListener(myOnclick);
        btn_last.setOnClickListener(myOnclick);
        btn_next.setOnClickListener(myOnclick);


        //改变musicNum
       for(int i=0;i<20;i++){
           if(i==musicNum){
               changeMusicNum(i);
               break;
           }
       }
       //改变musicName
        changeMusicName(musicNum);


        //指定MainActivity与MusicService之间要连接；
        Intent myIntent = new Intent(MainActivity.this,MusicService.class);
        bindService(myIntent,connection,BIND_AUTO_CREATE);

        //执行动画的对象是iv_cover，// 动画效果是0-360°旋转（用的是浮点数，所以加个f）。
        animator = ObjectAnimator.ofFloat(iv_cover,"rotation",0.0f,360.0f);
        animator.setDuration(10000); //旋转一周的时长，单位是毫秒，此处设置了10s
        animator.setInterpolator(new LinearInterpolator());//设置匀速转动
        animator.setRepeatCount(-1);//设置循环，此处设置的是无限循环。如果是正值，意味着转动多少圈。
        sb.setOnSeekBarChangeListener(new seekBarlistener());

    }
    public static ObjectAnimator newAnimator(){
        //执行动画的对象是iv_cover，// 动画效果是0-360°旋转（用的是浮点数，所以加个f）。
        ObjectAnimator animator1= ObjectAnimator.ofFloat(iv_cover,"rotation",0.0f,360.0f);
        animator1.setDuration(10000); //旋转一周的时长，单位是毫秒，此处设置了10s
        animator1.setInterpolator(new LinearInterpolator());//设置匀速转动
        animator1.setRepeatCount(-1);//设置循环，此处设置的是无限循环。如果是正值，意味着转动多少圈。
        animator1.start();
        animator1.resume();
        return animator1;
    }

    //改变musicNum
    public static void changeMusicNum(int i){
        musicNum=i;
    }

    public static int getMusicNum() {
        return musicNum;
    }

    //改变musicName
    public static void changeMusicName(int i){
        switch(musicNum){
            case 0:
                musicName.setText(R.string.n1);
                break;
            case 1:
                musicName.setText(R.string.n2);
                break;
            case 2:
                musicName.setText(R.string.n3);
                break;
            case 3:
                musicName.setText(R.string.n4);
                break;
            case 4:
                musicName.setText(R.string.n5);
                break;

        }
}

    class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_play:{
                    control.play();//播放音乐
                    animator.start();//光盘开始转动
                }
                break;
                case R.id.btn_pause:{
                    control.pausePlay();
                    animator.pause();
                }
                break;
                case R.id.btn_continue:
                    //继续播放音乐
                    control.continuePlay();
                    //光盘继续转
                    animator.resume();
                    break;
                case R.id.btn_exit:
                    finish();
                    break;
                case R.id.btn_next:
                    animator=newAnimator();
                    if(getMusicNum()==MusicList.getMusicListNum()-1)
                        changeMusicNum(0);
                    else {
                        changeMusicNum(getMusicNum() + 1);
                    }
                    changeMusicName(getMusicNum());
                    control.play();

                    break;
                case R.id.btn_last:
                    animator.pause();
                    if(getMusicNum()==0)
                        changeMusicNum(MusicList.getMusicListNum()-1);
                    else {
                        changeMusicNum(getMusicNum()-1);
                    }
                    changeMusicName(getMusicNum());
                    control.play();


                    break;

            }
        }
    }
    protected void onDestroy() {
        control.stopPlay();
        unbindService(connection);
        super.onDestroy();

    }


    class seekBarlistener implements SeekBar.OnSeekBarChangeListener{
        @Override
        //进度条行进时候的监听
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (i == seekBar.getMax()) {
                animator.pause();
            }
            if (b){//判断是否来自用户
                control.seekTo(i);
            }

        }
        @Override
        //点击进度条开始拖动进度条的监听
        public void onStartTrackingTouch(SeekBar seekBar) {
            control.pausePlay();
            animator.pause();

        }

        @Override
        //停止拖动时候的监听
        public void onStopTrackingTouch(SeekBar seekBar) {
            control.continuePlay();
            animator.resume();
        }

    }

    //Handler主要用于异步消息的处理，在这里是处理子线程MusicService传来的消息
    public static Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message message) {
            Bundle bundle =message.getData();
            int duration = bundle.getInt("duration");//把音乐时长放在bundle里
            int currentDuration = bundle.getInt("currentDuration");//把音乐当前播放时长放在bundle里

            sb.setMax(duration);
            sb.setProgress(currentDuration);

            //显示总时长
            int minute = duration / 1000 /60;
            int second = duration / 1000 % 60;
            String strMinute = "";
            String strSecond = "";
            if (minute < 10){
                strMinute = "0" +minute;
            }
            else strMinute=""+minute;
            if (second < 10){
                strSecond = "0" + second;
            }
            else strSecond=""+second;
            tv_total.setText(strMinute + ":" + strSecond);


            //显示播放时长
            minute = currentDuration / 1000 /60;
            second = currentDuration / 1000 % 60;

            if (minute < 10){
                strMinute = "0" +minute;
            }else strMinute=""+minute;
            if (second < 10){
                strSecond = "0" + second;
            }else strSecond=""+second;
            tv_progress.setText(strMinute + ":" + strSecond);

        }

    };


}
