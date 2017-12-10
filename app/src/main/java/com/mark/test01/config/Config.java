package com.mark.test01.config;

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


}
