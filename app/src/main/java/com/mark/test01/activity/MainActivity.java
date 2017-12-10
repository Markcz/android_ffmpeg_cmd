package com.mark.test01.activity;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.mark.test01.R;
import com.mark.test01.config.Config;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private SimpleCursorAdapter mAdapter;
    private Cursor mCursor;
    private FFmpeg fFmpeg;

    private ListView listView;
    private MediaPlayer mMediaPlayer;
    private ProgressBar mProgressBar;
    private AlertDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        loadData();
    }

    //加载ffmpeg
    private void loadData() {
        fFmpeg = FFmpeg.getInstance(this);
        try{
            fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onSuccess() {

                }
            });
        }catch (FFmpegNotSupportedException e){

        }
    }

    //执行ffmpeg命令
    private void excuteCMD(String [] cmd){
        try{
            fFmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    closeProgressDialog();
                }

                @Override
                public void onProgress(String message) {

                }

                @Override
                public void onFailure(String message) {
                    closeProgressDialog();
                }

                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                    closeProgressDialog();
                }
            });
        }catch (FFmpegCommandAlreadyRunningException e){
            e.printStackTrace();


            fFmpeg = null;
            loadData();
            Log.e("CMD--2:",String.valueOf(cmd));
            //String cmd = "-version";
            excuteCMD(cmd);

        }
    }

    private static final String [] AUDIO_COLUMNS = new String[]{

            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media._ID,

    };

    private void initViews() {

        mMediaPlayer = new MediaPlayer();

        myContentObserver = new MyContentObserver(handler);
        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,false,myContentObserver);

        listView = (ListView) findViewById(R.id.list_view);

        mAdapter = new SimpleCursorAdapter(this,
                R.layout.list_view_item,
                null,
                AUDIO_COLUMNS,
                new int[]{
                        R.id.tv_audio_artist,
                        R.id.tv_audio_album,
                        R.id.tv_audio_title,
                        R.id.tv_audio_path,
                        R.id.iv_action
                }, 1);
        getSupportLoaderManager().initLoader(0,null,this);

        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                setListItemValue(view,cursor);

                return true;
            }
        });

        listView.setAdapter(mAdapter);

        //final String outPath = "/storage/emulated/0/xx" + System.currentTimeMillis() + "xx.mp3";


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView t = view.findViewById(R.id.tv_audio_path);
                String path = t.getText().toString();

                openProgressDialog();

                outPath = "/storage/emulated/0/"+System.currentTimeMillis() + "mm" + ".mp3";

                cmd = Config.CMD_AUDIO_FADE_IN(path,outPath,new int[]{0,5});
                Log.e("CMD--1:",String.valueOf(cmd));
                //String cmd = "-version";
                excuteCMD(cmd);


                //playMusic(path);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                TextView t = view.findViewById(R.id.tv_audio_path);
                String path = t.getText().toString();
                setupUpdateInfo(path,id);
                return true;
            }
        });
    }

    String [] cmd;
    String outPath;
    private void closeProgressDialog(){
        mDialog.dismiss();
    }

    private void openProgressDialog() {
        View progressDialogView = LayoutInflater.from(this).inflate(R.layout.progress_dialog,null);
        mProgressBar = progressDialogView.findViewById(R.id.progressBar);
        mDialog = new AlertDialog.Builder(this).create();
        //mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setContentView(progressDialogView);
    }


    ///播放音乐
    void playMusic(String path){
        if (mMediaPlayer != null){
            if (!mMediaPlayer.isPlaying()){
                try {
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setDataSource(path);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                mMediaPlayer = new MediaPlayer();
                try {
                    mMediaPlayer.setDataSource(path);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //设置更新信息
    void setupUpdateInfo(String path,long id){
        File oldFile = new File(path);
        if (oldFile.exists()){
            String newNameNoExtension = System.currentTimeMillis() + "-";
            String newName = newNameNoExtension + ".mp3";
            boolean b = oldFile.renameTo(new File(oldFile.getParent(),newName));
            if (b){
                Toast.makeText(MainActivity.this,"成功:" + newName,Toast.LENGTH_LONG).show();
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DISPLAY_NAME,newNameNoExtension);
                values.put(MediaStore.Audio.Media.TITLE,newNameNoExtension);
                values.put(MediaStore.Audio.Media.DATA,oldFile.getParent()+File.separator + newName);
                updateContentProvider(values,AUDIO_COLUMNS[4] + "=" + id,null);
            }
        }
    }

    //更新数据库
    void updateContentProvider(ContentValues values, String selection, String[] selectionArgs){

        getContentResolver().update(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                values,
                selection,
                selectionArgs
                );

        getContentResolver().notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, myContentObserver);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 11){
                refreshView();
            }
        }
    };

    void refreshView (){
        Bundle args = new Bundle();
        getSupportLoaderManager().restartLoader(0,args, this);
    }

    private MyContentObserver myContentObserver;

    public class MyContentObserver extends ContentObserver {

        private Handler handler;

        public MyContentObserver(Handler handler) {
            super(handler);
            this.handler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.e("onChange:","------------");
            handler.sendEmptyMessage(11);
        }
    }

    private void setListItemValue(View view, Cursor cursor) {
        if (view.getId() == R.id.tv_audio_title){
            TextView t = (TextView) view;
            t.setText(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
        }else if (view.getId() == R.id.tv_audio_artist){
            TextView t = (TextView) view;
            t.setText(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
        }else if (view.getId() == R.id.tv_audio_album){
            TextView t = (TextView) view;
            t.setText(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
        }else if (view.getId() == R.id.tv_audio_path){
            TextView t = (TextView) view;
            t.setText(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
        }else if (view.getId() == R.id.iv_icon_type){
            ImageView i = (ImageView) view;
            i.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader loader = new CursorLoader(
                this,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                AUDIO_COLUMNS,
                null,
                null,
                null
                );

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        MergeCursor c = new MergeCursor(new Cursor[]{mCursor});
        mAdapter.swapCursor(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(myContentObserver);
        if (mMediaPlayer!=null){
            if (mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

    }

    boolean isExit;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (mMediaPlayer!=null){
                if (mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
                mMediaPlayer = null;
            }if (isExit){
                finish();
            }else {
                Toast.makeText(this,"再次点击退出应用",Toast.LENGTH_SHORT).show();
                isExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                },2000);
                return true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
