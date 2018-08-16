package com.eventbus.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.eventbus.demo.eventBus.EventBus;
import com.eventbus.demo.eventBus.Firends;

public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
    }

    /**
     * 发送事件
     * @param view
     */
    public void OnClickBtn(View view){
        EventBus.getDefault().post(new Firends("zhangsan","123456"));
        finish();
    }
}
