package com.igloosec.generator.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.igloosec.generator.prop.LoggerPropertyInfo;

@Mapper
public interface LoggerMapper {
    int selectNumber(@Param("number") int number);

    List<LoggerPropertyInfo> listLogger();

    int insertLogger(LoggerPropertyInfo info);

    int updateLogger(LoggerPropertyInfo info);

    int updateLoggerStatus(int id, int status);

    int insertHistory(int loggerId, String ip, long lastModified, String msg);
}