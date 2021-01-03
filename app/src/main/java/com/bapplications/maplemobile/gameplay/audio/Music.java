package com.bapplications.maplemobile.gameplay.audio;

import android.media.MediaPlayer;

import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXAudioNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Music {

    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static String bgmpath = "";

    static {

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.reset();
            try {
                mediaPlayer.prepare();
            } catch (IOException | IllegalStateException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();

        });

    }

    public static void play(String path) {
        if (path.equals(bgmpath))
            return;
        String[] nodes = path.split("/");
        NXNode nxNode = Loaded.getFile(Loaded.WzFileName.SOUND).getRoot();
        for (String nodeName : nodes) {
            nxNode = nxNode.getChild(nodeName);
        }
        playAudioByNode((NXAudioNode) nxNode);

        bgmpath = path;
    }

    private static void playAudioByNode(NXAudioNode audioNode) {
        try {
            String bgmPath = Configuration.CACHE_DIRECTORY + bgmpath + ".mp3";
            File bgmFile = new File(bgmPath);
            if (!bgmFile.exists()){
                // create temp file that will hold byte array
                bgmFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(bgmFile);
            fos.write(audioNode.get().array());
            fos.close();

            // resetting mediaplayer instance to evade problems
            mediaPlayer.reset();
            mediaPlayer.setLooping(true);
            FileInputStream fis = new FileInputStream(bgmFile);
            mediaPlayer.setDataSource(fis.getFD());
            fis.close();

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void pauseBgm() {
        mediaPlayer.pause();
    }

    public static void stopBgm() {
        mediaPlayer.stop();
    }

    public static void resumeBgm() {
        mediaPlayer.start();
    }

}
