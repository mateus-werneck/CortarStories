package com.example.Picotador;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static android.view.View.INVISIBLE;
import static com.example.Picotador.SaveStories.SaveStories;


public class StoriesCut {

    public static void Cut(String folder, Context context, FragmentManager fragmentManager, RecyclerFolderAdapter recyclerViewAdapter, NotificationManager notificationManager, RecyclerView recyclerView, DialogCreate aguarde, MainActivity mainActivity) {

           ProgressBar progressBar = mainActivity.findViewById(R.id.progressBar);
           ImageButton cancelButton = mainActivity.findViewById(R.id.cancelButton);
           FloatingActionButton select = mainActivity.findViewById(R.id.select);
           TextView selectVideo = mainActivity.findViewById(R.id.selectVideo);
           TextView progressText = mainActivity.findViewById(R.id.progressText);


           File dataFolder = new File (folder + "/stories.mp4");
           int threadCode = VideoCut(folder, fragmentManager, context, notificationManager, mainActivity, progressText);
           if (threadCode == 1) {
               File Stories = new File(folder + File.separator + "Stories/");
               ArrayList<File> allStories = new ArrayList<>();
               if (Objects.requireNonNull(Stories.listFiles()).length > 0) {
                   allStories.addAll(Arrays.asList(Objects.requireNonNull(Stories.listFiles())));
               Collections.sort(allStories);
               }

               SaveStories(allStories, Stories, dataFolder, context);

               Intent notificationIntent = new Intent(context, MainActivity.class);
               PendingIntent pendingIntent = PendingIntent.getActivity(context, 101, notificationIntent,
                       PendingIntent.FLAG_UPDATE_CURRENT);

               NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "StoriesCut")
                       .setSmallIcon(R.mipmap.ic_launcher_foreground)
                       .setContentText("Pronto")
                       .setContentTitle("Stories Picotado")
                       .setProgress(100, 100, false)
                       .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                       .setAutoCancel(true)
                       .setContentIntent(pendingIntent);


               DialogCreate Fim = new DialogCreate("Pronto", "Seu Stories terminou de ser picotado.", android.R.drawable.ic_dialog_info, recyclerViewAdapter, recyclerView);
               Fim.show(fragmentManager, "Pronto");

               select.setClickable(true);
               selectVideo.setVisibility(INVISIBLE);
               notificationManager.cancel(104);
               notificationManager.notify(104, notification.build());

           } else if (threadCode == 2) {
               aguarde.dismiss();
               DialogCreate Erro1 = new DialogCreate("Erro", "Verifique se seu stories tem mais de 15 segundos.", android.R.drawable.ic_dialog_info, null, recyclerView);
               Erro1.show(fragmentManager, "15 segundos");
               dataFolder.delete();
               progressBar.setVisibility(INVISIBLE);
               cancelButton.setVisibility(INVISIBLE);
               select.setClickable(true);
               notificationManager.cancel(104);
           } else if (threadCode == 3) {
               aguarde.dismiss();
               dataFolder.delete();
               select.setClickable(true);
           } else if (threadCode == 4) {
               aguarde.dismiss();
               DialogCreate Erro2 = new DialogCreate("Erro", "Tente selecionar o seu vídeo mais uma vez.", android.R.drawable.ic_dialog_info, null, recyclerView);
               Erro2.show(fragmentManager, "Caminho Nulo");
               dataFolder.delete();
               progressBar.setVisibility(INVISIBLE);
               cancelButton.setVisibility(INVISIBLE);
               select.setClickable(true);
               notificationManager.cancel(104);
           }

       }

       private static int VideoCut(String path, FragmentManager fragmentManager, Context context, NotificationManager notificationManager, MainActivity mainActivity, TextView progressText) {
        if (path != null) {
            String storiesName;
            ProgressBar progressBar = mainActivity.findViewById(R.id.progressBar);


            /////////////////////////////////////////// Creating Folder and Getting Video Information ///////////////////////

            File storiesFolder = new File(path + File.separator + "Stories");
            path = path + "/stories.mp4";
            float videoDuration = Float.parseFloat((FFprobe.getMediaInformation(path).getDuration()));
            String bitrate = (Integer.parseInt(FFprobe.getMediaInformation(path).getBitrate()) / 1000) + "k";
            double start_time = 0.00;

            if (videoDuration <= 15.99) {
                return 2;
            }
            String tempParts =  new DecimalFormat("#.##").format((videoDuration / 15.00));
            int videoParts = (int) (videoDuration / 15);
            if (videoParts < Double.parseDouble(tempParts)) {videoParts++;}

            if (!storiesFolder.exists()) {
                if (!storiesFolder.mkdir()) {
                    DialogCreate Erro4 = new DialogCreate("Erro", "Verifique se o aplicativo tem permissão para acessar o armazenamento do celular.", android.R.drawable.ic_dialog_info, null, null);
                    Erro4.show(fragmentManager, "Armazenamento");
                    return 3;

                }
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////////

            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "Picotador")
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentText("Processando")
                    .setColorized(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);


            notificationManager.notify(104, notification.build());
            progressBar.setMax((int) videoDuration * 1000);

            ////////////////////////////////////////////////////////////////////// Video Processing for Stories divisible by 15 seconds //////////////////////////////////////////////////////////////////////////////////////////////////////////
            ArrayList<Integer> ffmpeg = new ArrayList<>();

            for (int i = 0; i < videoParts; i++) {
                String encodedPath;
                if (i <= 8) {storiesName = "/stories0" + (i+1) + ".mp4";
                } else { storiesName = "/stories" + (i+1) + ".mp4";}

                FFmpeg.executeAsync("-ss " + start_time+ " -i " + path + " -t 15" + " -b:v " + bitrate + " -maxrate 30000k "  + "-bufsize " + bitrate  +
                                     " -c:a aac -c:v libx264 -strict -2 -preset ultrafast " + storiesFolder + storiesName, new ExecuteCallback() {
                    @Override
                    public void apply(long executionId, int returnCode) {
                    }
                });
                int progressFactor = i;
                Config.enableStatisticsCallback(new StatisticsCallback() {
                    @Override
                    public void apply(Statistics statistics) {
                        int progress = statistics.getTime();
                        ffmpeg.add(progress);
                        if(ffmpeg.get(0).equals(ffmpeg.get(1))) {
                            progressBar.setProgress(progressBar.getProgress() + 1000);
                            String percentage = String.valueOf(((int) ((((progressBar.getProgress() / 1000) / videoDuration) * 100) + 1))) + "%";
                            progressText.setText(percentage);
                            ffmpeg.clear();
                        } else {progressBar.setProgress((progress + (progressFactor * 15000)));
                                String percentage = String.valueOf((int)((((progress / 1000) + progressFactor * 15) / videoDuration) * 100)) + "%";
                                progressText.setText(percentage);}
                        notification.setProgress(15000, progress, false);
                        notification.setNotificationSilent();
                        notificationManager.notify(104, notification.build());

                    }
                });

                notification.setContentTitle("Parte " + (i + 1) + "/" + (videoParts));
                notificationManager.notify(104, notification.build());
                encodedPath = storiesFolder + storiesName;



                /////////////////////////////////////////////////////// Waiting for Async FFMpeg to Finish /////////////////////////////////////////////////////////////////////////////
                SystemClock.sleep(2000);
                while(FFmpeg.listExecutions().size() != 0) {
                    if (Thread.interrupted()) {
                        progressBar.setProgress(0);
                        return 0;
                    }
                    SystemClock.sleep(500);
                }

                int ffmpegLast = ffmpeg.get(ffmpeg.size() - 1);
                ffmpeg.clear();
                ffmpeg.add(ffmpegLast);


                //////////////////////////////////////////////////// Double Checking if the Video is up to 15 seconds ///////////////////////////////////////////////////////////////////
                String encodedDuration =  new DecimalFormat("#.##").format(Float.parseFloat(FFprobe.getMediaInformation(encodedPath).getDuration()));



                if (Double.parseDouble(encodedDuration) > 15.50) {
                    FFmpeg.execute("-i " + encodedPath + " -t 15 -b:v " + bitrate + " -maxrate 30000k"  + " -bufsize " + bitrate +
                            " -c copy -strict -2 -y " + storiesFolder + "/" + "stories_temp" + ".mp4");
                    File oldVideoEncode = new File(encodedPath);
                    oldVideoEncode.delete();
                    File replacementVideo = new File(storiesFolder + "/" + "stories_temp" + ".mp4");
                    if (!oldVideoEncode.exists()) {
                        replacementVideo.renameTo(oldVideoEncode);}

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                }

                start_time += Double.parseDouble(encodedDuration);
                SystemClock.sleep(500);


            }

            ////////////////////////////////////////////////////////// Deleting temp file and going back to Thread /////////////////////////////////////////////////////////////////////////
            File stories = new File(storiesFolder + "/stories.mp4");
            stories.delete();
            mainActivity.findViewById(R.id.cancelButton).setVisibility(INVISIBLE);
            mainActivity.findViewById(R.id.progressText).setVisibility(INVISIBLE);
            mainActivity.findViewById(R.id.progressBar).setVisibility(INVISIBLE);
            ((ProgressBar) mainActivity.findViewById(R.id.progressBar)).setProgress(0);
            ((ProgressBar) mainActivity.findViewById(R.id.progressBar)).setMax(100);
            notificationManager.cancel(104);
            return 1;

        } else {
            return 4;
        }

    }

}

