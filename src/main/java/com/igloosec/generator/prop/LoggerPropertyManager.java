package com.igloosec.generator.prop;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.igloosec.generator.mybatis.mapper.LoggerMapper;
import com.igloosec.generator.restful.model.LoggerYamlVO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class LoggerPropertyManager {
    
    private Map<Integer, LoggerPropertyInfo> cache;
    private ObjectMapper om = new ObjectMapper(new YAMLFactory());
    
    private File configDir = new File("./config");
    
    @Autowired
    private LoggerMapper mapper;
    
    @PostConstruct
    private void init() {
        this.cache = new HashMap<>();
        List<LoggerPropertyInfo> listInfo = this.mapper.listLogger();
        Map<String, LoggerPropertyInfo> mapInfo = listInfo.stream()
            .collect(Collectors.toMap(LoggerPropertyInfo::getName, x -> x));
        
        List<File> files = (List<File>) FileUtils.listFiles(configDir, new String[] {"yaml"}, true);
        for (File f: files) {
            try {
                LoggerProperty lp = om.readValue(f, LoggerProperty.class);
                
                LoggerPropertyInfo info = null;
                if (mapInfo.containsKey(f.getName())) {
                    info = mapInfo.get(f.getName());
                } else {
                    info = this.createLogger(f);
                }
                info.setLogger(lp);
                this.cache.put(info.getId(), info);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    
    private LoggerPropertyInfo createLogger(File f) throws Exception {
        LoggerPropertyInfo info = new LoggerPropertyInfo();
        
        info.setName(f.getName());
        info.setCreated(new Date().getTime());
        info.setLastModified(new Date().getTime());
        info.setIp("System");
        info.setStatus(0);
        info.setYamlStr(FileUtils.readFileToString(f, Charset.defaultCharset()));
        int i = mapper.insertLogger(info);
        log.debug("********************");
        log.debug(info);
        if (i == 0) {
            throw new Exception("can not insert logger");
        }
        return info;
    }

    public LoggerPropertyInfo getLogger(int id) {
        return this.cache.get(id);
    }
    
    public Map<Integer, LoggerPropertyInfo> listLogger() {
        return this.cache;
    }
    /**
     * @param name
     * @param yaml
     * @return
     */
    public boolean createLogger(LoggerYamlVO vo) {
        try {
            LoggerProperty lp = om.readValue(vo.getYaml(), LoggerProperty.class);
            LoggerPropertyInfo info = new LoggerPropertyInfo();
            info.setLogger(lp);
            info.setName(vo.getFileName());
            
            // TODO validateCheck
            this.cache.put(info.getId(), info);
            // TODO write File
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
    
    /**
     * @param name
     * @param yaml
     * @return
     */
    public boolean modifyLogger(LoggerYamlVO vo) {
        try {
            // TODO Stop logging
            LoggerProperty lp = om.readValue(vo.getYaml(), LoggerProperty.class);
            // TODO validateCheck
            this.cache.remove(vo.getId());
            LoggerPropertyInfo info = new LoggerPropertyInfo();
            info.setLogger(lp);
            info.setId(vo.getId());
            info.setName(vo.getNewFileName());
            info.setIp(vo.getIp());
            info.setLastModified(new Date().getTime());
            info.setYamlStr(vo.getYaml());
            mapper.updateLogger(info);
            this.cache.put(vo.getId(), info);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    public boolean deleteLogger(LoggerYamlVO vo) {
        try {
            // TODO Stop logging
            // TODO validateCheck
            this.cache.remove(vo.getId());
            
            // TODO remove File
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
    
    @Async
    public void run() throws IOException, InterruptedException {
        log.debug("Start Log Property manager");
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get("./config");
        path.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                log.debug("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    
                } else if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    
                } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    this.cache.remove(event.context());
                }
                
            }
            log.debug(this.cache);
            key.reset();
        }
    }
    
//    LogProperty a = mapper.readValue(new File("config/apache.yaml"), LogProperty.class);
//    while(true) {
//        Map<String, Object> b = a.generateLog();
//        queueService.pushLog(b);
//        log.debug(b);
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//} catch (IOException e1) {
//    // TODO Auto-generated catch block
//    e1.printStackTrace();
//}
}
