from moviepy.video.io.VideoFileClip import VideoFileClip
from moviepy.video.fx.resize import resize
import os, re

def cutVideo(filepaths):
############################################ Initializing ########################################################
    for filepath in filepaths:
        video = VideoFileClip(filepath)
        duration = int(video.duration)
        size = int(duration / 15)
        real_size = duration / 15
        if(size > real_size):
            size = size - 1
        end_custom = (15 * size) + (duration % 15)
        start_time = 0
        end_time = 15   
        j = 0
################################## Check if Folder Already Exists ###############################################
        for j in range(100):
            if(os.path.isdir(os.getcwd() + '/Stories' + '0' + str(j+1)) == False):
                os.mkdir(os.getcwd() + '/Stories' + '0' + str(j+1))
                j += 1
                break
        path = (os.getcwd() + '/Stories' + '0' + str(j))
################################## If duration is divisible by 15 ###############################################
        if (duration % 15 == 0):
            for i in range(size):
                #if (abort == None):
                if(end_time > duration + (i-1 * 0.01)):
                    break
                else:
                    new = video.subclip (start_time, end_time)
                    new.write_videofile(path + "/stories" + '_0' + str(i + 1) + ".mp4", threads=8, preset="veryfast", audio_codec='aac')
                    start_time += 15.01
                    end_time += 15.01

################################## If duration is NOT divisible by 15 ###############################################
        else:
            i = 0
            while i < size:
                new = video.subclip(start_time, end_time)
                new.write_videofile(path + "/stories" + '_0' + str(i + 1) + ".mp4", threads=8, preset="veryfast", audio_codec='aac')
                start_time += 15.01
                end_time += 15.01
                i += 1
            if (i == size):
                new = video.subclip(start_time + 0.01, end_custom)
                new.write_videofile(path + "/stories" + '_0' + str(size + 1) + ".mp4", threads=8, preset="veryfast", audio_codec='aac')
                

######################################################## Finished ########################################################
