package com.igloosec.generator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.igloosec.generator.prop.LoggerPropertyManager;
import com.igloosec.generator.restful.model.LoggerYamlVO;
import com.igloosec.generator.restful.model.SingleObjectResponse;
import com.igloosec.generator.service.SocketService;

@RestController
@RequestMapping(value = "/logger")
public class LoggerManageRestController {
    
    @Autowired
    private LoggerPropertyManager loggerPropMng;
    
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody SingleObjectResponse create(@RequestBody LoggerYamlVO vo) {
        return new SingleObjectResponse(HttpStatus.OK.value(), "OK", loggerPropMng.createLogger(vo));
    }
    
    @RequestMapping(value = "/modify", method = RequestMethod.PATCH)
    public @ResponseBody SingleObjectResponse modify(@RequestBody LoggerYamlVO vo) {
        return new SingleObjectResponse(HttpStatus.OK.value(), "OK", loggerPropMng.modifyLogger(vo));
    }
    
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public @ResponseBody SingleObjectResponse delete(@RequestBody LoggerYamlVO vo) {
        return new SingleObjectResponse(HttpStatus.OK.value(), "OK", loggerPropMng.deleteLogger(vo));
    }
}
