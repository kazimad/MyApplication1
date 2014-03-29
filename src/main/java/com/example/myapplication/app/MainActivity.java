package com.example.myapplication.app;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import org.w3c.dom.Text;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;


public class MainActivity extends ActionBarActivity {
    private Bitmap myBitmap;
    static int numberOfThreads = 4;
    String myLinks;
    String ImagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        final ArrayList <DownloadThread> myListOfThread = new ArrayList<DownloadThread>();
        final LinkedBlockingQueue <String> myStringQueue = new LinkedBlockingQueue<String>();                 // queue of links to download
        final LinkedBlockingQueue<String> myImages = new LinkedBlockingQueue<String>();                                   //  queue of images


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button download = (Button) findViewById(R.id.download);
//        Text CatDownload = (Text) findViewById(R.id.CatDownload);
        final ImageView myImageViev = (ImageView) findViewById(R.id.imageView);
        final DownloadThread myThread = new DownloadThread();
        download.setEnabled(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isCDMount = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                if (isCDMount) {
                    ImagePath = String.valueOf(getExternalCacheDir());
                } else {
                    ImagePath = String.valueOf(getCacheDir());
                }

            }
        }).start();

        download.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View view) {
                download.setEnabled(false);

                try {
                    BufferedReader bf = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.links)));     // читает фаил со ссылками из папки raw
                    while ((myLinks = bf.readLine()) != null) {

                        myStringQueue.add(myLinks);
                    }
                    for (int i = 0; i <numberOfThreads; i++) {
                        myListOfThread.add(myThread);

                        myThread.start();
                    }
                    while (!myStringQueue.isEmpty()) {
                        myThread.setDownloadMyLink(myStringQueue.take());
                    }
                    for (DownloadThread myThread : myListOfThread) {
                        myThread.join();
                    }



                    myThread.setMyPathDownload(ImagePath);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            myImages = myThread.getMyImages();
                            while (!myImages.isEmpty()) {

                                String displayedImage = null;
                                try {
                                    displayedImage = myImages.take();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                BitmapFactory.Options myOptions  = new BitmapFactory.Options();
                                myOptions.inJustDecodeBounds = true;
                                int memory  = myOptions.outHeight*myOptions.outWidth*4;
                                myOptions.inJustDecodeBounds=false;
                                long allMemory = Runtime.getRuntime().freeMemory();
                                if (memory>allMemory){
                                    myOptions.inSampleSize = 4;
                                }
                                myBitmap = BitmapFactory.decodeFile(displayedImage,myOptions);
                                myImageViev.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        myImageViev.setImageBitmap(myBitmap);

                                    }
                                });
                            }
                        }
                    }).start();




                } catch (FileNotFoundException e) {
                    System.out.println("File with links is unavailable");
                } catch (IOException e) {
                    System.out.println("Can't read lines from file");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    protected static void saveImage(String link, String outFile, int countLinks) throws IOException {
        try {
            URL url = new URL(link);
            InputStream is = new BufferedInputStream(url.openStream());
            OutputStream os = new FileOutputStream(outFile);
            int length;
            while ((length = is.read()) != -1) { // reads by bytes while it is possible
                os.write(length);
            }
            is.close();
            os.close();
            System.out.println("File # " + countLinks + " is written");
        } catch (UnknownHostException q) {
            System.out.println("link #" + countLinks + " is unavailable1 ");
        } catch (SocketException qq) {
            System.out.println("link #" + countLinks + " is unavailable2");
        } catch (Exception qqq) {
            System.out.println("link #" + countLinks + " is unavailable3");
        }
    }
////////
//
}
