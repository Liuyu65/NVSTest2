package com.example.liuyu.nvstest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private Button bt1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ShadowImageView shadow = (ShadowImageView) findViewById(R.id.shadow);
        bt1 = (Button) findViewById(R.id.bt1);
        Button bt2 = (Button) findViewById(R.id.bt2);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bt1_anim);
                bt1.startAnimation(animation);

            }
        });


        /**
         *
         * @param context Activity
         * @param view 绑定角标view
         * @param tag 用于绑定的唯一标记
         * @param num 角标数字
         * @param style 显示样式  （如使用小图标，设置样式为 STYLE_SMALL ）
         * @return SuperBadgeHelper
         */
        //        SuperBadgeHelper photo = SuperBadgeHelper.init(this, shadow, "photo" , 4, SuperBadgeHelper.STYLE_DEFAULT);
        //
        //        SuperBadgeHelper textView = init(this, viewById, "textView", 0);
        //        photo.bindView(textView);


        //        shadow.setImageResource(R.mipmap.ic_launcher);
        //        shadow.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        //        shadow.setImageBitmap(R.mipmap.ic_launcher);
        //        shadow.setImageRadius(10);
    }
}
