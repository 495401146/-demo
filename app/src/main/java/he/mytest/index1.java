package he.mytest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ant.liao.GifView;
import com.bumptech.glide.Glide;


public class index1 extends Activity {
    private Button btn;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index1);

        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //获取屏幕宽高
        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;



        // 从xml中得到GifView的句柄
        GifView gf1 = (GifView) findViewById(R.id.gif1);
        // 设置Gif图片源
        gf1.setGifImage(R.drawable.scrollball);
        // 设置显示的大小，拉伸或者压缩
        gf1.setShowDimension(screenWidth,screenHeigh-10);
        // 设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示
        gf1.setGifImageType(GifView.GifImageType.COVER);
    }
}


