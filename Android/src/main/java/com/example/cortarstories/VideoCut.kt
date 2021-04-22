package com.example.cortarstories

import android.system.Os
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFprobe
import com.google.android.material.snackbar.Snackbar
import java.io.File
import kotlin.math.roundToInt


fun Stories(path: String?, progressBar: ProgressBar, Snack: View, progressBar2: ProgressBar): Int {
 ////////////////////////////////////////////////////////////////////// Parameters //////////////////////////////////////////////////////////////////////////////////////////////////
        var folder: String = "/storage/emulated/0/Pictures/Stories"
        if(!File(folder).exists()) {
            Os.mkdir("/storage/emulated/0/Pictures/" + "Stories", 1)
        }
        var startTime: Double = 0.00
        val info = FFprobe.getMediaInformation(path.toString())
        val duration = info.duration.toFloat()
        if (duration.toDouble() <= 15.99){
            progressBar.visibility = View.INVISIBLE
            progressBar2.visibility = View.INVISIBLE
            return 0

        }
        var parts = (duration / 15).roundToInt()
        parts += 1
        if (parts > (duration / 15) + 1) { parts += -1 }
        progressBar.max = parts
        var end_time_custom = (duration % 15.01 + 15 * parts)
        var c: Int = 0
    ////////////////////////////////////////////////////////////////////// Video divisible by 15 //////////////////////////////////////////////////////////////////////////////////////////////////
        if (duration.toInt() % 15 == 0) {
            for (j in 0..100) {
                if(!File(folder + "/stories" + "0" + (j + 1).toString()).exists()) {
                    Os.mkdir("/storage/emulated/0/Pictures/" + "Stories/stories0" + (j + 1).toString(), 1)
                    c = j + 1
                    break
                }
                    }
            for (i in 0 until parts - 1) {
                var Mybar: Snackbar = Snackbar.make(Snack, "Parte " + (i + 1).toString() + "/" + (parts - 1).toString(), 10000)
                Mybar.show()
                FFmpeg.execute(" -ss " + startTime + " -t 15" + " -i " + path + " -b 8000k -maxrate 10000k -bufsize 8000k -vf scale=1080:-1 -r 30" + " -acodec aac -strict -2 -y  /storage/emulated/0/Pictures/Stories/stories0" + (c).toString() + "/" + "stories" + "0" + (i + 1).toString() + ".mp4")
                progressBar.progress = i + 1
                var secondary_path =  "/storage/emulated/0/Pictures/Stories/stories0" + (c).toString() + "/" + "stories" + "0" + (i + 1).toString() + ".mp4"
                var secondary_info = FFprobe.getMediaInformation(secondary_path.toString())
                var secondary_duration = secondary_info.duration.toFloat()
                if (secondary_duration > 15.09) {
                    FFmpeg.execute("-i " + secondary_path + " -t 15 -b 8000k -maxrate 10000k -bufsize 8000k -vf scale=1080:-1 -r 30" + " -acodec aac -strict -2 -y  /storage/emulated/0/Pictures/Stories/stories0" + (c).toString() + "/" + "stories_temp" + ".mp4")
                    var video_old: File = File(secondary_path)
                    video_old.delete()
                    var video_new: File = File("/storage/emulated/0/Pictures/Stories/stories0" + (c).toString() + "/" + "stories_temp" + ".mp4")
                    if (!video_old.exists()){
                        video_new.renameTo(video_old)}
                    startTime += 15.01
                    } else {
                startTime += 15.01
                    }
            }
            progressBar2.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            progressBar.progress = 0
            return 1
////////////////////////////////////////////////////////////////////// Video NOT divisible by 15 //////////////////////////////////////////////////////////////////////////////////////////////////
        }else if (duration.toInt() % 15 > 0){
            var i = 0
            for (j in 0..100) {
                if(!File(folder + "/stories" + "0" + (j + 1).toString()).exists()) {
                    Os.mkdir("/storage/emulated/0/Pictures/" + "Stories/stories0" + (j + 1).toString(), 1)
                    c = (j + 1)
                    break
                }
            }
            while (i < parts - 1) {
                var Mybar: Snackbar = Snackbar.make(Snack, "Parte " + (i + 1).toString() + "/" + parts.toString(), 10000)
                Mybar.show()
                FFmpeg.execute(" -ss " + startTime + " -t  15" + " -i " + path + " -b 8000k -maxrate 10000k -bufsize 8000k -vf scale=1080:-1 -r 30" + " -acodec aac -strict -2 -y /storage/emulated/0/Pictures/Stories/stories0" + (c).toString() + "/" + "stories" + "0" + (i + 1).toString() + ".mp4")
                progressBar.progress = i + 1
                var secondary_path =  "/storage/emulated/0/Pictures/Stories/stories0" + (c).toString() + "/" + "stories" + "0" + (i + 1).toString() + ".mp4"
                var secondary_info = FFprobe.getMediaInformation(secondary_path.toString())
                var secondary_duration = secondary_info.duration.toFloat()
                if (secondary_duration > 15.09) {
                    FFmpeg.execute("-i " + secondary_path + " -t 15 -b 8000k -maxrate 10000k -bufsize 8000k -vf scale=1080:-1 -r 30" + " -acodec aac -strict -2 -y /storage/emulated/0/Pictures/Stories/stories0" + (c).toString() + "/" + "stories_temp" +  ".mp4")
                    var video_old: File = File(secondary_path)
                    video_old.delete()
                    var video_new: File = File("/storage/emulated/0/Pictures/Stories/stories0" + (c).toString() + "/" + "stories_temp" + ".mp4")
                    if (!video_old.exists()){
                        video_new.renameTo(video_old)}
                    startTime += 15.01
                    i += 1
                } else {
                    startTime += 15.01
                    i += 1 }

                if (i == parts - 1) {
                var Mybar: Snackbar = Snackbar.make(Snack, "Parte " + (i + 1).toString() + "/" + parts.toString(), 10000)
                Mybar.show()
                FFmpeg.execute(" -ss " + startTime + " -to " + end_time_custom + " -i " + path + " -b 5000k -maxrate 10000k -bufsize 5000k -vf scale=1080:-1 -r 30" + " -acodec aac -strict -2 -y  /storage/emulated/0/Pictures/Stories/stories0" + (c).toString() + "/" + "stories" + "0" + (i + 1).toString() + ".mp4")
                var secondary_path =  "/storage/emulated/0/Pictures/Stories/stories0" + (c).toString() + "/" + "stories" + "0" + (i + 1).toString() + ".mp4"
                progressBar.progress = parts
                if (secondary_duration > 15.09) {
                    FFmpeg.execute("-i " + secondary_path + " -t 15 -b 8000k -maxrate 10000k -bufsize 8000k -vf scale=1080:-1 -r 30" + " -acodec aac -strict -2 -y  /storage/emulated/0/Pictures/Stories/stories0" + (c).toString() + "/" + "stories_temp" + ".mp4")
                    var video_old: File = File(secondary_path)
                    video_old.delete()
                    var video_new: File = File("/storage/emulated/0/Pictures/Stories/stories0" + (c).toString() + "/" + "stories_temp" + ".mp4")
                    if (!video_old.exists()){
                    video_new.renameTo(video_old)}
                    progressBar2.visibility = View.INVISIBLE
                    progressBar.visibility = View.INVISIBLE
                    progressBar.progress = 0
                    return 1
                } else {
                    progressBar.visibility = View.INVISIBLE
                    progressBar2.visibility = View.INVISIBLE
                    progressBar.progress = 0
                    return 1
                }
                }
            }

        }
    return 1
    }
