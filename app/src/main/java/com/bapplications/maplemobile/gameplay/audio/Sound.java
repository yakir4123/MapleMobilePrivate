package com.bapplications.maplemobile.gameplay.audio;

import android.media.MediaPlayer;

import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXAudioNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXNoChildrenNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;

public class Sound {

    private static int id;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    public enum Name {
        PORTAL,
        JUMP;
    }

    private static Map<Integer, String> samples = new HashMap<>();
    private static Map<Name, Integer> soundsId = new EnumMap<>(Sound.Name.class);


    public static void init(){
        NXNode gamesrc = Loaded.getFile(Loaded.WzFileName.SOUND).getRoot().getChild("Game.img");
        addSound(Name.JUMP, gamesrc.getChild("Jump"));
        addSound(Name.PORTAL, gamesrc.getChild("Portal"));
    }


    public Sound(NXNode src) {
        if (src instanceof NXAudioNode)
            id = addSound(src);
    }

    public Sound(Name name) {
        id = soundsId.get(name);

        // create temp file that will hold byte array
        File tempMp3 = new File(samples.get(id));

        // resetting mediaplayer instance to evade problems
        mediaPlayer.reset();
        FileInputStream fis;
        try {
            fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());
            fis.close();
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void addSound(Name name, NXNode src) {
        id = addSound(src);

        if (id != 0)
            soundsId.put(name, id);
    }

    public void play() {
        mediaPlayer.start();
    }


    private static int addSound(NXNode src)
    {
        NXAudioNode ad = (NXAudioNode) src;

        ByteBuf data = ad.getAudioBuf();

        if (data == null) {
            return 0;
        }
        int id = ad.hashCode();

        if (samples.get(id) != null)
            return 0;


        // create temp file that will hold byte array
        try {
            String soundPath = Configuration.CACHE_DIRECTORY + src.getName() + ".mp3";
            File soundFile = new File(soundPath);
            if (!soundFile.exists()){
                // create temp file that will hold byte array
                soundFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(soundFile);
            fos.write(data.array());
            samples.put(id, soundFile.getAbsolutePath());

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        return id;
    }

}
