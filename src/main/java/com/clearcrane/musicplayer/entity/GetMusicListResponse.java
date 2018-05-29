package com.clearcrane.musicplayer.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jjy on 2018/5/26.
 */

public class GetMusicListResponse {
    public List<Musics> Musics;

    public static class Musics {
        /**
         * PicSize : 12004
         * Name : {"zh-CN":"卡农","en-US":"Canon"}
         * Seq : 1
         * SingerName : {"zh-CN":"蒋勋","en-US":"JiangXun"}
         * PicURL_ABS : http://mres.cleartv.cn/default/c946bb4af444e2e86d3084569f66c7ea_148688147001.jpg
         * URL_ABS : http://mres.cleartv.cn/default/17cbfbcaa0c30342ee22f1355bcc6d6c_14868814792.mp3
         * MusicIntro : {"zh-CN":"卡农","en-US":"Canon"}
         * URL : /Music/resource/17cbfbcaa0c30342ee22f1355bcc6d6c_14868814792.mp3
         * ColumnName : {"zh-CN":"卡农","en-US":"Canon"}
         * PicURL : /Music/resource/c946bb4af444e2e86d3084569f66c7ea_148688147001.jpg
         * Duration : 309
         * MusicSize : 12699697
         * ID : 19
         */

        public int PicSize;
        public Name Name;
        public int Seq;
        public SingerName SingerName;
        public String PicURL_ABS;
        public String URL_ABS;
        public MusicIntro MusicIntro;
        public String URL;
        public ColumnName ColumnName;
        public String PicURL;
        public int Duration;
        public int MusicSize;
        public int ID;

        public static class Name {
            /**
             * zh-CN : 卡农
             * en-US : Canon
             */

            @SerializedName("zh-CN")
            public String zhCN;
            @SerializedName("en-US")
            public String enUS;
        }

        public static class SingerName {
            /**
             * zh-CN : 蒋勋
             * en-US : JiangXun
             */

            @SerializedName("zh-CN")
            public String zhCN;
            @SerializedName("en-US")
            public String enUS;
        }

        public static class MusicIntro {
            /**
             * zh-CN : 卡农
             * en-US : Canon
             */

            @SerializedName("zh-CN")
            public String zhCN;
            @SerializedName("en-US")
            public String enUS;
        }

        public static class ColumnName {
            /**
             * zh-CN : 卡农
             * en-US : Canon
             */

            @SerializedName("zh-CN")
            public String zhCN;
            @SerializedName("en-US")
            public String enUS;
        }
    }
}
