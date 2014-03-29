package com.example.myapplication.app;

//import android.annotation.TargetApi;
import android.os.Build;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.concurrent.LinkedBlockingQueue;

//@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class DownloadThread extends Thread {


    static final Object myMutex = new Object();
    int countLinks = 1;
    int countNameFiles = 1;
    static int localCountLinks,localCountNameFiles;
    public String downloadMyLink;
    public String myPathDownload;



    public LinkedBlockingQueue<String> myImages = new LinkedBlockingQueue();

    public void setDownloadMyLink(String downloadMyLink) {
        this.downloadMyLink = downloadMyLink;
    }
    //    MainActivity mainActivity = new MainActivity();
    public void setMyPathDownload(String myPathDownload) {
        this.myPathDownload = myPathDownload;
    }
    public LinkedBlockingQueue<String> getMyImages() {
        return myImages;
    }


    @Override
    public void run() {
        synchronized (myMutex){
            localCountLinks = countLinks++;
            localCountNameFiles = countNameFiles++;
        }
        String outFile = ( myPathDownload + "/newfile_" + localCountNameFiles+ ".jpg");
        try {
            MainActivity.saveImage(downloadMyLink, outFile, localCountLinks);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mainActivity.myImagesAdd(outFile);
//            mainActivity.setOutFile(outFile);
        myImages.add(outFile);
    }
}

