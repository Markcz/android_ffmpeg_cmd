package com.mark.test01.config;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Mark on 2017/11/27.
 */
public class Config {


    /**
     * 音频淡入
     * @param in
     * @param out
     * @param start
     * @param
     * @return
     */
    public static String [] GET_AUDIO_FADE_IN_OUT(String in, String out,int []start){
        String cmd =
                "-i " +
                in +
                " -af afade=t=in:st="+start[0]+":d="+start[1] + " "+
                out
                ;
        return cmd.split(" ");
    }

    //ffmpeg -i input.mov -af afade=t=in:st=0:d=3,afade=t=out:st=13:d=3 output.mp4
    public static String [] GET_AUDIO_FADE_IN_OUT(String in, String out){
        String cmd =   "-i " +
                        in +
                        " -af afade=t=in:st=0:0:d=0:10 " +
                        out
                ;
        return cmd.split(" ");
    }

    /**
     * 从指定时间处理 音频淡入
     * @param in
     * @param out
     * @param span span[0]: 起始时间  span[1]:时长
     * @return
     */
    public static String [] CMD_AUDIO_FADE_IN(String in, String out,int[] span){
        String cmd = "-i*" +
                in +
                "*-ss*" + span[0] + "*-t*" + span[1] +
                "*-af*afade=t=in:st="+span[0]+":d="+span[1]+"*" +
                out
                ;
        return cmd.split("\\*");
    }

    /**
     * 从指定时间处理 音频淡入淡出
     * @param in
     * @param out
     * @param span span[0]: 起始时间  span[1]:时长
     * @return
     */
    public static String [] CMD_AUDIO_FADE_OUT(String in, String out,int[] span){
        String cmd =   "-i*" +
                in +
                "*-ss*" + span[0] + "*-t*" + span[1] +
                "*-af*afade=t=out:st="+span[0]+":d="+span[1]+"*" +
                out
                ;
        return cmd.split("\\*");
    }

    public static void setMyRingtone(Context context, String path) {
        File sdfile = new File(path);
        String ab_path = sdfile.getAbsolutePath();
        ContentValues values = new ContentValues();
        Uri uri_query = MediaStore.Audio.Media.getContentUriForPath(ab_path);
// 查询音乐文件在媒体库是否存在
        Cursor cursor = context.getContentResolver().query(uri_query, null,
                MediaStore.MediaColumns.DATA + "=?", new String[]{ab_path},
                null);


        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            String _id = cursor.getString(0);
            Uri newUri = ContentUris.withAppendedId(uri_query,
                    Long.valueOf(_id));
            RingtoneManager.setActualDefaultRingtoneUri(context,
                    RingtoneManager.TYPE_RINGTONE, newUri);
            Toast.makeText(context, "设置电话铃声成功1！", Toast.LENGTH_SHORT).show();
        } else {


            values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
            values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            values.put(MediaStore.MediaColumns.SIZE, sdfile.length());

            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);

//注意这一行，一定是只设置IS_NOTIFICATION这一个值。同理，如果设置通知音或闹铃也都只设置这一个值，只是在RingtoneManager.setActualDefaultRingtoneUri()这里区别是哪种铃声


            Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile
                    .getAbsolutePath());
            Uri newUri = context.getContentResolver().insert(uri, values);
            RingtoneManager.setActualDefaultRingtoneUri(context,
                    RingtoneManager.TYPE_RINGTONE, newUri);//这里设置不同的TYPE来区别铃声种类
            Toast.makeText(context, "设置电话铃声成功2！", Toast.LENGTH_SHORT).show();
            System.out.println("setMyRingtone()-----铃声");
        }
    }

}
