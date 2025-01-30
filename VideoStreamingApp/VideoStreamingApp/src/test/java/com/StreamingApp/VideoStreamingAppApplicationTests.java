package com.StreamingApp;

import com.StreamingApp.Service.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VideoStreamingAppApplicationTests {
	@Autowired
	VideoService videoService;
	@Test
	void contextLoads() {
		videoService.VideoProcessing(1);
	}

}
