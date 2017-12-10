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

}
