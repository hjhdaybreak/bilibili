package com.bilibili.dao;

import com.bilibili.domain.File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileDao {

    File getFileByMD5(String fileMD5);

    Integer addFile(File dbFileMD5);
}
