package com.StreamingApp.Service;

import com.StreamingApp.Entity.Video;
import com.StreamingApp.Repo.VideoRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    @Value(
            "${files.video}"
    )
    String DIR;
    @Value(
            "${files.video.hls}"
    )
    String HLS_DIR;

    @PostConstruct
    public void init() {

        File file = new File(DIR);
        File file1 = new File(HLS_DIR);


        try {
           Files.createDirectories(Paths.get(HLS_DIR));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!file.exists()) {
            file.mkdir();
            System.out.println("Folder Created:");
        }
        else {
            System.out.println("Folder already created");
        }

    }

    @Autowired
    private  VideoRepo videoRepo;

        public Video save(Video video, MultipartFile file) {
            // original file name

            try {


                String filename = file.getOriginalFilename();
                String contentType = file.getContentType();
                InputStream inputStream = file.getInputStream();


                // file path
                String cleanFileName = StringUtils.cleanPath(filename);


                //folder path : create

                String cleanFolder = StringUtils.cleanPath(DIR);


                // folder path with  filename
                Path path = Paths.get(cleanFolder, cleanFileName);

                System.out.println(contentType);
                System.out.println(path);

                // copy file to the folder
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);


                // video meta data

                video.setContentType(contentType);
                video.setFilePath(path.toString());
                // metadata save
                Video savedVideo = videoRepo.save(video);
                //processing video
                VideoProcessing(video.getVideoId());
                //delete actual video file and database entry  if exception



                return savedVideo;

            } catch (IOException e) {
                e.printStackTrace();
                 new RuntimeException("Error in processing video ");
            }
        return null;

        }
    public Video get( int Id){
           Video video= videoRepo.findById(Id).orElseThrow(()->new RuntimeException("video not found"));
        return video;

    }
    public Video getByTitle(String title){
        return null;
    }
    public List<Video> getAll(){
        return videoRepo.findAll();
    }

    @Override
    public String VideoProcessing(int videoId) {
        Video video = this.get(videoId);
        String filePath = video.getFilePath();
//            path where to store data
        Path VideoPath = Paths.get(filePath);

//            String Output360p=HLS_DIR+videoId+"/360p/";
//            String Output720p=HLS_DIR+videoId+"/720p/";
//            String Output1080p=HLS_DIR+videoId+"/1080p/";
        try {
//                Files.createDirectories(Paths.get(Output360p));
//                Files.createDirectories(Paths.get(Output720p));
//                Files.createDirectories(Paths.get(Output1080p));
            Path outputPath = Paths.get(HLS_DIR, String.valueOf(videoId));

            Files.createDirectories(outputPath);

//            String ffmpegPath = "C:\\ffmpeg\\ffmpeg\\bin\\ffmpeg.exe"; // Ensure you use double backslashes for file paths in Java strings
//            String command = ffmpegPath + " -i \"C:\\Users\\asus\\Documents\\projects\\VideoStreamingApp\\videos\\big_buck_bunny_720p_1mb.mp4\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"videos_hls\\1\\segment_%3d.ts\" \"videos_hls\\1\\master.m3u8\"";


            String ffmpegCmd = String.format(
                    "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\"  \"%s/master.m3u8\" ",
                    VideoPath, outputPath, outputPath);
            System.out.println(ffmpegCmd);
            //file this command
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", ffmpegCmd);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exit = process.waitFor();
            if (exit != 0) {
                throw new RuntimeException("video processing failed!!");
            }

            return String.valueOf(videoId);
        } catch (IOException ex) {
            throw new RuntimeException("Video processing failed!!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }





}

