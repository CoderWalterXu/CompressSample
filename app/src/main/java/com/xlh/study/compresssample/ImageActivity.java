package com.xlh.study.compresssample;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.xlh.compresslibrary.netease.utils.ImageUtil.getImageStreamFromExternal;

/**
 * @author: Watler Xu
 * time:2020/1/6
 * description:
 * version:0.0.1
 */
public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        String path = getIntent().getStringExtra("path");
        ImageView imageView = findViewById(R.id.image);

        imageView.setImageURI(getImageStreamFromExternal(path));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
