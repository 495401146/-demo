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

    private SurfaceView surfaceView;   //��Ƶ��ʾ���������
    private RelativeLayout layout;   //��Բ��ֵ�����
    private Callback callback;   //
    private Camera camera;
    private MediaRecorder mediaRecorder;   //ý��¼��
    private Button start;   //��ť
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
        // ���ú�����ʾ
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_main);

        start = (Button) this.findViewById(R.id.start);
        this.stop = (Button) this.findViewById(R.id.stop);
        //����gifͼƬ
        android.util.DisplayMetrics dm = new DisplayMetrics();
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
        gf1.setShowDimension(screenWidth, screenHeigh - 10);
        // ���ü��ط�ʽ���ȼ��غ���ʾ���߼��ر���ʾ��ֻ��ʾ��һ֡����ʾ
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
        //ʹ�ö�ʱ��������Ϣ����Ϊ��ʱ���в���ֱ�Ӹ���UI,
        //������Ϊ��������ͷ
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        },2000);
        //����ʮ���ر�����ͷ
        time1.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }
        },12000);

        //����handler��������ͷ��ʵ�ָ���UI����
        handler = new Handler() {
            public void handleMessage(Message msg) {
                //��message���ֿ������ǹر�����ͷ
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

    private class MyCallback implements Callback   //�ص���
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
                    camera = Camera.open(1);    //������
                }catch (Exception e)
                {
                    camera.release();
                    camera = null;
                }
                camera.setDisplayOrientation(90);
                camera.setPreviewDisplay(surfaceView.getHolder());
                camera.startPreview();  //��ʼԤ��
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
                camera.stopPreview();   //ֹͣԤ��
                camera.release();      //�ͷ���Դ
                camera = null;
                getBitmapsFromVideo();
                if(flag == false)
                {
                    Toast.makeText(getApplicationContext(), "�Բ���¼��ʧ�ܣ�������¼��", Toast.LENGTH_SHORT).show();
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
    //ֹͣ��Ƶ¼��
    private void StopRecord() {
        if(mediaRecorder != null)
        {
            mediaRecorder.stop();   //ֹͣ
            mediaRecorder.reset();   //����
            mediaRecorder.release();    //�ͷ���Դ
            camera.lock();
            mediaRecorder = null;
        }
    }

    //��ʼ��Ƶ¼��
    private void StartRecord() {
        try
        {
            viedoPath = Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".3gp";
            System.out.print(viedoPath);
            File file = new File(viedoPath);
            camera.unlock();
            mediaRecorder = new MediaRecorder();    //ý��¼�ƶ���
            mediaRecorder.setCamera(camera);   //��������
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);  //����������ļ��ĸ�ʽ
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);   //���ñ���
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setOutputFile(file.getAbsolutePath());   //��������ļ���·��
            System.out.print("�ұ�������Ƶ·��");
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            mediaRecorder.setVideoSize(1280, 720);
            //mediaRecorder.setVideoFrameRate(4);
            mediaRecorder.setVideoEncodingBitRate(100*1024*1024);
            mediaRecorder.setOrientationHint(270);
            mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
            mediaRecorder.prepare();   //����

            mediaRecorder.start();   //��ʼ¼��



        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        layout.setVisibility(ViewGroup.VISIBLE);   //��������ʱ��ť�ɼ�
        return super.onTouchEvent(event);
    }
    //����Ƶתλͼ
    public void getBitmapsFromVideo() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(viedoPath);
        // ȡ����Ƶ�ĳ���(��λΪ����)
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // ȡ����Ƶ�ĳ���(��λΪ��)
        int seconds = Integer.valueOf(time) / 200;
        // �õ�ÿһ��ʱ�̵�bitmap�����һ��,�ڶ���()
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
    //��ͼƬ���浽ָ����·��
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

