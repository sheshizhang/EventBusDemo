package com.eventbus.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.eventbus.demo.eventBus.EventBus;
import com.eventbus.demo.eventBus.Firends;
import com.eventbus.demo.eventBus.Subscribe;
import com.eventbus.demo.eventBus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
    }

    public void secondBtn(View view){
        startActivity(new Intent(MainActivity.this,SecondActivity.class));
    }

    /**
     * 接收事件在主线程
     * @param firends
     */
    @Subscribe(threadMode=ThreadMode.MainThread)
    public void EventMessage(Firends firends){
        Toast.makeText(this,"姓名:"+firends.getName()+"::"
            +"密码:"+firends.getPassword(),Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode=ThreadMode.Async)
    public void ThreadEventMessage(Firends firends){
        Toast.makeText(this,"子线程姓名:"+firends.getName()+"::"
                +"子线程密码:"+firends.getPassword(),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
