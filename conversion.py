from moviepy.video.io.VideoFileClip import VideoFileClip
from moviepy.video.fx.resize import resize
import os, re

def cutVideo(filepath):
############################################ Initializing ########################################################
    for filepath in filepath:
        video = VideoFileClip(filepath)
        duration = int(video.duration)
        duration_module = duration % 15
        size = int(video.duration / 15)
        size_custom = video.duration
        start_time = 0
        end_time = 15
        end_custom = round(size_custom % 15, 3)
        j = 0
################################## Check if Folder Already Exists ###############################################
        for j in range(100):
            if(os.path.isdir(os.getcwd() + '/' + 'Stories' + '0' + str(j+1)) == False):
                os.mkdir(os.getcwd() + '/' + 'Stories' + '0' + str(j+1))
                j += 1
                break
        path = (os.getcwd() + '/' + 'Stories' + '0' + str(j))
################################## If duration is divisible by 15 ###############################################
        if (duration_module == 0):
            for i in range(size):
                #if (abort == None):
                if(end_time > duration + (i * 0.01)):
                    break
                else:
                    new = video.subclip (start_time, end_time)
                    new.write_videofile(path + "/" + "stories" + '_0' + str(i + 1) + ".mp4", threads=8, preset="veryfast", logger=None, audio_codec='aac')
                    start_time += 15.01
                    end_time += 15.01

################################## If duration is NOT divisible by 15 ###############################################
        else:
            i = 0
            while i < size:
                new = video.subclip(start_time, end_time)
                new.write_videofile(path + "/" + "stories" + '_0' + str(i + 1) + ".mp4", threads=8, preset="veryfast", logger=None, audio_codec='aac')
                start_time += 15.01
                end_time += 15.01
                i += 1
            if (i == size):
                new = video.subclip(start_time, end_custom + 15.01)
                new.write_videofile(path + "/" + "stories" + '_0' + str(i + 1) + ".mp4", threads=8, preset="veryfast", logger=None, audio_codec='aac')
                break
        continue
    return True

######################################################## Finished ########################################################
