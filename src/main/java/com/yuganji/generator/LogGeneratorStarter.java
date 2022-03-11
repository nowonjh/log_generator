package com.yuganji.generator;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yuganji.generator.model.OutputInfoVO;
import com.yuganji.generator.output.OutputService;

@Service
public class LogGeneratorStarter {

    private OutputService socketService;

    @Autowired
    public LogGeneratorStarter(OutputService socketService) {
        this.socketService = socketService;
    }
    @PostConstruct
    private void init() {
        OutputInfoVO vo =new OutputInfoVO(3305, 100000);
        socketService.open(vo);
    }
}
