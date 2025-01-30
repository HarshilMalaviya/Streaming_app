package com.StreamingApp.Controller;

import com.StreamingApp.AppConstants;
import com.StreamingApp.Entity.Video;
import com.StreamingApp.Payload.Custommessage;
import com.StreamingApp.Repo.VideoRepo;
import com.StreamingApp.Service.VideoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("api/v1/video")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")

public class VideoController {
    @Autowired
    private final VideoRepo videoRepo;
    @Autowired
    private final VideoServiceImpl videoServiceImpl;

    // Constructor for dependency injection

    @PostMapping
    public ResponseEntity<?> Create(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description
            ){
        Video  video=new Video();
        video.setDescription(description);
        video.setTitle(title);
        Video savedVideo = videoServiceImpl.save(video, file);
        if (savedVideo != null) {
            return ResponseEntity.status(HttpStatus.OK).body(video);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Custommessage.builder().message("Video not uploaded ").success(false).build());
        }
    }
   @GetMapping("/stream/{Id}")
    public ResponseEntity<Resource>stream(
            @RequestBody int Id
    ){
       Video video= videoServiceImpl.get(Id);
       String contentType = video.getContentType();
       Path path = Paths.get(video.getFilePath());
       Resource resource=new FileSystemResource(path);
       if(contentType==null){
           contentType="application/octet-stream";
       }
       return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
               .body(resource);
   }

    @GetMapping("/stream/range/{Id}")
    public ResponseEntity<Resource>streamVideoRange(
            @RequestBody int Id,
            @RequestHeader(value = "Range",required = false)String range

    ){
        System.out.println(range);
        Video video= videoServiceImpl.get(Id);
        String contentType = video.getContentType();
        Path path = Paths.get(video.getFilePath());
        Resource resource=new FileSystemResource(path);
        if(contentType==null){
            contentType="application/octet-stream";
        }
        long fileLength =path.toFile().length();
        if (range==null){
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        }
        long rangeStart;
        long rangeEnd;
        String[] ranges = range.replace("bytes=", "").split("-");
        rangeStart=Long.parseLong(ranges[0]);

        rangeEnd=rangeStart+ AppConstants.CHUNK_SIZE-1;
        if (rangeEnd>=fileLength){
            rangeEnd=fileLength-1;
        }

//        if (ranges.length>1){
//            rangeEnd=Long.parseLong(ranges[1]);
//        }else {
//            rangeEnd= fileLength-1;
//        }
//        if (rangeEnd>fileLength-1){
//            rangeEnd=fileLength-1;
//        }
        InputStream inputStream;
        try {
            inputStream= Files.newInputStream(path);
            inputStream.skip(rangeStart);
            long contentLength=rangeEnd-rangeStart+1;

            byte[] data= new byte[(int)contentLength];
            int read=inputStream.read(data,0,data.length);
            System.out.println("read(no of bytes):"+read);


            HttpHeaders headers =new HttpHeaders();
            headers.add("Contant-Range","bytes "+rangeStart+"-"+rangeEnd+"/"+fileLength);
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("X-Content-Type-Options", "nosniff");
            headers.setContentLength(contentLength);

            return ResponseEntity
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new ByteArrayResource(data));

        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }
   @GetMapping
    public List<Video>getAll(){
        return videoServiceImpl.getAll();
    }
    //serve hls playlist

    //master.m2u8 file

    @Value(
            "${files.video.hls}"
    )
    String HLS_DIR;

    @GetMapping("/{videoId}/master.m3u8")
    public ResponseEntity<Resource> serverMasterFile(
            @PathVariable String videoId
    ) {

//        creating path
        Path path = Paths.get(HLS_DIR, videoId, "master.m3u8");

        System.out.println(path);

        if (!Files.exists(path)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource resource = new FileSystemResource(path);

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_TYPE, "application/vnd.apple.mpegurl"
                )
                .body(resource);


    }

    //serve the segments

    @GetMapping("/{videoId}/{segment}.ts")
    public ResponseEntity<Resource> serveSegments(
            @PathVariable String videoId,
            @PathVariable String segment
    ) {

        // create path for segment
        Path path = Paths.get(HLS_DIR, videoId, segment + ".ts");
        if (!Files.exists(path)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource resource = new FileSystemResource(path);

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_TYPE, "video/mp2t"
                )
                .body(resource);

    }

}
