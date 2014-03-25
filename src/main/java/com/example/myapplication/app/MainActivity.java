package com.example.myapplication.app;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import org.w3c.dom.Text;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;


public class MainActivity extends ActionBarActivity {
    static File myFileWithLinks = new File ("links.txt");
    static String nameDir = "cats/";
    static File myDir = new File(nameDir);
    final LinkedBlockingQueue <String> myStringQueue = new LinkedBlockingQueue<String>();

    String links;
    int countLinks = 1;
    int countNameFiles = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button download = (Button) findViewById(R.id.download);
        Text CatDownload = (Text) findViewById(R.id.CatDownload);

        makeDir();



        download.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                makeDir();

                try {
                    BufferedReader bf = new BufferedReader(new FileReader(myFileWithLinks));
                    while ((links = bf.readLine()) != null) {
                        myStringQueue.add(links);
                    }


                    new Thread(new Runnable() {

                        String myLInk = myStringQueue.take();
                        @Override
                        public void run() {
                            if (!myStringQueue.isEmpty()) {                 // тут надо было бы реализовать CallBack - но пока не понятно как...


                                String outFile = ("cats/newfile_" + countNameFiles + ".jpg");
                                try {
                                    saveImage(myLInk, outFile, countLinks);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                countNameFiles++;
                                countLinks++;
                            }
                        }
                    }).start();

//                File [] imageArray = myDir.listFiles();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//                    }
//                }).start();





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
            InputStream is = url.openStream();
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

    public static void makeDir () {
        if (!myDir.exists()) {
            myDir.mkdir();
        }
    }

}
////////////
///