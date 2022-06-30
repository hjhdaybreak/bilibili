package com.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.bilibili.dao.VideoDao;
import com.bilibili.domain.PageResult;
import com.bilibili.domain.Video;
import com.bilibili.domain.VideoTag;
import com.bilibili.domain.exception.ConditionException;
import com.bilibili.service.config.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Service
public class VideoService {

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;
//    public static void main(String[] args) {
//        List<VideoTag> list = new ArrayList<>();
//        VideoTag videoTag = new VideoTag();
//        videoTag.setVideoId(1L);
//        videoTag.setTagId(0L);
//        list.add(videoTag);
//        Video video = new Video();
//        video.setVideoTagList(new ArrayList<>(list));
//        System.out.println(JSONObject.toJSON(video));
//    }

    @Transactional
    public void addVideos(Video video) {
        Date now = new Date();

        video.setCreateTime(new Date());
        videoDao.addVideos(video);

        Long videoId = video.getId();
        List<VideoTag> tagList = video.getVideoTagList();
        tagList.forEach(item -> {
            item.setCreateTime(now);
            item.setVideoId(videoId);
        });

        videoDao.batchAddVideoTags(tagList);
    }

    public PageResult<Video> pageListVideos(Integer size, Integer no, String area) {
        if (size == null || no == null) {
            throw new ConditionException("参数异常");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("area", area);
        List<Video> list = new ArrayList<>();
        Integer total = videoDao.pageCountVideos(params); //满足条件的数量
        if (total > 0) {
            list = videoDao.pageListVideos(params);
        }
        return new PageResult<>(total, list);
    }

    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
        fastDFSUtil.viewVideoOnlineBySlices(request,response,url);
    }
}
