package com.xiaoniu.scrachcardview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.xiaoniu.scratchview.ScratchImageView;
import com.xiaoniu.scratchview.ScratchListener;

public class MainActivity extends Activity {
    ScratchImageView iv_scratch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_scratch = (ScratchImageView) findViewById(R.id.iv_scratch);
        iv_scratch.setScratchListener(new ScratchListener() {
            @Override
            public void scratchSuccess(View view) {
                Toast.makeText(MainActivity.this,"成功",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
