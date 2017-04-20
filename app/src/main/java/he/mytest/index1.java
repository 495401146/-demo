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
        //��ȡ��Ļ��Ϣ
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //��ȡ��Ļ���
        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;



        // ��xml�еõ�GifView�ľ��
        GifView gf1 = (GifView) findViewById(R.id.gif1);
        // ����GifͼƬԴ
        gf1.setGifImage(R.drawable.scrollball);
        // ������ʾ�Ĵ�С���������ѹ��
        gf1.setShowDimension(screenWidth,screenHeigh-10);
        // ���ü��ط�ʽ���ȼ��غ���ʾ���߼��ر���ʾ��ֻ��ʾ��һ֡����ʾ
        gf1.setGifImageType(GifView.GifImageType.COVER);
    }
}


