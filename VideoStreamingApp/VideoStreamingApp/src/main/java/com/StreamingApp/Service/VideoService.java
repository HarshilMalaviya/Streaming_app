package com.StreamingApp.Service;

import com.StreamingApp.Entity.Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {

    //save  video
    static Video save(Video video, MultipartFile file) {
        return null;
    }



    // get video by  id
    Video get(int Id);


    // get video by title

    Video getByTitle(String title);

    List<Video> getAll();


    //video processing
    String VideoProcessing(int videoId);

}

