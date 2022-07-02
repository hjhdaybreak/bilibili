package com.bilibili.api;

import com.bilibili.domain.JsonResponse;
import com.bilibili.domain.Video;
import com.bilibili.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
public class DemoApi {
    @Autowired
    private ElasticSearchService elasticSearchService;
    @GetMapping("/es-videos")
    public JsonResponse<Video> getEsVideos(@RequestParam String keyword){
       Video video = elasticSearchService.getVideos(keyword);
       return new JsonResponse<>(video);
    }

}
