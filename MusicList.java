package com.example.musicapplication;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MusicList extends AppCompatActivity {
    private RecyclerView musicList;
    private static int[] names={R.string.n1,R.string.n2,R.string.n3,R.string.n4,R.string.n5};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        musicList=findViewById(R.id.musicList);
        musicList.setLayoutManager(new LinearLayoutManager(MusicList.this,LinearLayoutManager.VERTICAL,false));
        musicList.setAdapter(new MyAdapter());


    }

    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {
        @NonNull
        @Override
        //把item_layout布局转成视图
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(MusicList.this,R.layout.item_layout,null);
            MyHolder myHolder=new MyHolder(view);
            return myHolder;
        }
        @Override
        //给每个itemView赋具体的内容
        public void onBindViewHolder(@NonNull MyHolder holder,  int position) {
            //holder.name.setText(names[position]);
           TextView textView= holder.itemView.findViewById(R.id.name);
           textView.setText(names[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=null;
                    intent= new Intent(MusicList.this,MainActivity.class);
                    //播放点击的歌曲
                    MainActivity.changeMusicNum(position);
                    MainActivity.control.pausePlay();
                    MainActivity.animator=MainActivity.newAnimator();
                    startActivity(intent);
                    MainActivity.control.play();
                    MainActivity.changeMusicName(MainActivity.getMusicNum());
                                    }
            });
        }

        @Override
        public int getItemCount() {
            return names.length;
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder{
        //private TextView name;
        public MyHolder(@NonNull View itemView){
            super(itemView);
            //name=findViewById(R.id.name);

        }

    }
    public static int getMusicListNum(){
        return names.length;
    }
    public  static void destroy(){

    }

}