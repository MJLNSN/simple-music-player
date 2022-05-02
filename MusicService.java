package com.example.musicapplication;



import static com.example.musicapplication.MainActivity.changeMusicName;
import static com.example.musicapplication.MainActivity.changeMusicNum;
import static com.example.musicapplication.MainActivity.control;
import static com.example.musicapplication.MainActivity.getMusicNum;
import static com.example.musicapplication.MainActivity.newAnimator;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    private MediaPlayer player;
    private Timer timer;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        MusicControl musicControl=new MusicControl();
        return musicControl;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player=new MediaPlayer();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //创建一个内部类MusicControl，功能是让主程序控制sevise里面的多媒体对象。IBinder 是Binder的子类，因此要返回MusicControl给IBinder。
    class MusicControl extends Binder{
        public void play() {
            try{
                player.reset();//重置音乐播放器
                loadMusic(getMusicNum());//加载多媒体文件
                addTimer();//添加计时器
                player.start(); //开始播放音乐
            }catch (Exception exception) {//catch用来处理播放时产生的异常
                exception.printStackTrace();
            }
        }
        public void pausePlay(){
            player.pause();    //暂停播放
        }
        public void continuePlay(){
            player.start();   //继续播放
        }
        public void stopPlay(){
            player.stop();
            player.release();
            try {
                timer.cancel();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        public void seekTo(int progress){
            player.seekTo(progress);//设置播放位置播放
        }

        //加载多媒体文件
        public void loadMusic(int i){
            switch (i) {
                case 0:
                    player = MediaPlayer.create(getApplicationContext(), R.raw.music1);
                    break;
                case 1:
                    player = MediaPlayer.create(getApplicationContext(), R.raw.music2);
                    break;
                case 2:
                    player = MediaPlayer.create(getApplicationContext(), R.raw.music3);
                    break;
                case 3:
                    player = MediaPlayer.create(getApplicationContext(), R.raw.music4);
                    break;
                case 4:
                    player = MediaPlayer.create(getApplicationContext(), R.raw.music5);
                    break;

            }
        }
    }

    public void addTimer(){
        if (timer == null) {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() { //run就是多线程的一个东西
                    if (player == null) return; //如果player没有实例化，就退出。
                    int duration = player.getDuration();//获取歌曲总长度
                    int currentDuration = player.getCurrentPosition();
                    //将音乐的总时长、播放时长封装到消息对象中去；
                    Message message = MainActivity.handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("duration", duration);
                    bundle.putInt("currentDuration", currentDuration);
                    message.setData(bundle);//使用bundle给主线程发消息

                    //将消息添加到主线程中
                    MainActivity.handler.sendMessage(message);

                }
            };
            //开始计时任务后5ms，执行第一次任务，以后每100ms执行一次任务
            timer.schedule(task, 5, 100);
        }
    }
}