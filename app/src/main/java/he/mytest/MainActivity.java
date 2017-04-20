package he.mytest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.media.CamcorderProfile;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ant.liao.GifView;
import com.bumptech.glide.Glide;


public class MainActivity extends Activity {

    private SurfaceView surfaceView;   //视频显示区域的声明
    private RelativeLayout layout;   //相对布局的声明
    private Callback callback;   //
    private Camera camera;
    private MediaRecorder mediaRecorder;   //媒体录制
    private Button start;   //按钮
    private Button stop;
    private String viedoPath;
    private String imgPath;
    private boolean flag = true;
    private String imgDir = UUID.randomUUID().toString();
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置横屏显示
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_main);

        start = (Button) this.findViewById(R.id.start);
        this.stop = (Button) this.findViewById(R.id.stop);
        //加载gif图片
        android.util.DisplayMetrics dm = new DisplayMetrics();
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
        gf1.setShowDimension(screenWidth, screenHeigh - 10);
        // 设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示
        gf1.setGifImageType(GifView.GifImageType.COVER);

        layout = (RelativeLayout) this.findViewById(R.id.layout);
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        callback = new MyCallback();
        surfaceView.getHolder().setFixedSize(1280, 720);
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(callback);
        Toast.makeText(getApplicationContext(), "please wait 2 second", Toast.LENGTH_SHORT).show();
        Timer time = new Timer();
        Timer time1 = new Timer();
        //使用定时器传递消息，因为定时器中不能直接更新UI,
        //此任务为开启摄像头
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        },2000);
        //设置十秒后关闭摄像头
        time1.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }
        },12000);

        //启动handler开启摄像头，实现更新UI操作
        handler = new Handler() {
            public void handleMessage(Message msg) {
                //以message区分开启还是关闭摄像头
                if (msg.what == 1) {
                    Toast.makeText(getApplicationContext(), "start record ,please focus on the picture", Toast.LENGTH_SHORT).show();
                    StartRecord();
                }
                else if(msg.what == 0){
                    Toast.makeText(getApplicationContext(), "stop record ", Toast.LENGTH_SHORT).show();
                    StopRecord();
                }
                super.handleMessage(msg);
            };
        };
    }

    private class MyCallback implements Callback   //回调类
    {

        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
                                   int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void surfaceCreated(SurfaceHolder arg0) {
            // TODO Auto-generated method stub
            try
            {
                try {
                    camera = Camera.open(1);    //打开摄像
                }catch (Exception e)
                {
                    camera.release();
                    camera = null;
                }
                camera.setDisplayOrientation(90);
                camera.setPreviewDisplay(surfaceView.getHolder());
                camera.startPreview();  //开始预览
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder arg0) {
            // TODO Auto-generated method stub
            if(camera != null)
            {
                camera.stopPreview();   //停止预览
                camera.release();      //释放资源
                camera = null;
                getBitmapsFromVideo();
                if(flag == false)
                {
                    Toast.makeText(getApplicationContext(), "对不起，录制失败，请重新录制", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    public void startVoide(View v)
    {
        switch(v.getId())
        {
            case R.id.start:
                start.setEnabled(false);
                stop.setEnabled(true);
                StartRecord();
                break;
            case R.id.stop:
                start.setEnabled(true);
                stop.setEnabled(false);
                StopRecord();
                break;
        }
    }
    //停止视频录制
    private void StopRecord() {
        if(mediaRecorder != null)
        {
            mediaRecorder.stop();   //停止
            mediaRecorder.reset();   //重置
            mediaRecorder.release();    //释放资源
            camera.lock();
            mediaRecorder = null;
        }
    }

    //开始视频录制
    private void StartRecord() {
        try
        {
            viedoPath = Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".3gp";
            System.out.print(viedoPath);
            File file = new File(viedoPath);
            camera.unlock();
            mediaRecorder = new MediaRecorder();    //媒体录制对象
            mediaRecorder.setCamera(camera);   //设置摄像
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);  //设置输出的文件的格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);   //设置编码
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setOutputFile(file.getAbsolutePath());   //设置输出文件的路径
            System.out.print("我保存了视频路径");
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            mediaRecorder.setVideoSize(1280, 720);
            //mediaRecorder.setVideoFrameRate(4);
            mediaRecorder.setVideoEncodingBitRate(100*1024*1024);
            mediaRecorder.setOrientationHint(270);
            mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
            mediaRecorder.prepare();   //缓冲

            mediaRecorder.start();   //开始录制



        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        layout.setVisibility(ViewGroup.VISIBLE);   //当触屏的时候按钮可见
        return super.onTouchEvent(event);
    }
    //将视频转位图
    public void getBitmapsFromVideo() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(viedoPath);
        // 取得视频的长度(单位为毫秒)
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // 取得视频的长度(单位为秒)
        int seconds = Integer.valueOf(time) / 200;
        // 得到每一秒时刻的bitmap比如第一秒,第二秒()
        for (int i = 1; i <= 50; i++) {
            Bitmap bitmap = retriever.getFrameAtTime(i * 200 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            String imgPath = Environment.getExternalStorageDirectory() + File.separator+"capture"+File.separator+imgDir+ File.separator + i + ".jpg";
            saveBitmap(bitmap,imgPath);
        }
//        for (int i = 1; i <= seconds; i++) {
//            Bitmap bitmap = retriever.getFrameAtTime(i * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//            String imgPath = Environment.getExternalStorageDirectory() + File.separator+"capture"+File.separator+imgDir+ File.separator + i + ".jpg";
//            saveBitmap(bitmap,imgPath);
//        }

    }
    //将图片保存到指定的路径
    private void saveBitmap(Bitmap bm, String path) {
        String dir = path.substring(0,path.lastIndexOf("/"));
        File dirFile =  new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

