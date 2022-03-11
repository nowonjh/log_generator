package com.yuganji.generator.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yuganji.generator.field.IDField;
import com.yuganji.generator.field.IFieldGenerator;
import com.yuganji.generator.field.IPField;
import com.yuganji.generator.field.IntField;
import com.yuganji.generator.field.Ip2LocField;
import com.yuganji.generator.field.PayloadField;
import com.yuganji.generator.field.StrField;
import com.yuganji.generator.field.TimeField;
import com.yuganji.generator.field.UrlField;
import com.yuganji.generator.util.Constants;

import lombok.Data;

@Data
public class FieldInfoVO implements Comparable<Integer> {
    private String type;
    private Map<String, Double> values;
    @JsonProperty("raw_format")
    private String rawFormat;
    @JsonProperty("parse_format")
    private String parseFormat;
    private String based;
    @JsonProperty("parser_name")
    private String parserName;
    
    private transient int order;
    
    private transient IFieldGenerator ins;
    
    public IFieldGenerator getInstance() {
        if (type.equalsIgnoreCase(Constants.DataType.IP.getValue())) {
            return new IPField(values);
        } else if (type.equalsIgnoreCase(Constants.DataType.STR.getValue())) {
            return new StrField(values);
        } else if (type.equalsIgnoreCase(Constants.DataType.TIME.getValue())) {
            return new TimeField(rawFormat, parseFormat);
        } else if (type.equalsIgnoreCase(Constants.DataType.INT.getValue())) {
            return new IntField(values);
        } else if (type.equalsIgnoreCase(Constants.DataType.PAYLOAD.getValue())) {
            return new PayloadField(values);
        } else if (type.equalsIgnoreCase(Constants.DataType.URL.getValue())) {
            return new UrlField(values);
        } else if (type.equalsIgnoreCase(Constants.DataType.SPARROW_ID.getValue())) {
            return new IDField(this.parserName);
        }else if (type.equalsIgnoreCase(Constants.DataType.IP2LOC.getValue())) {
            return new Ip2LocField(this.based, 2);
        }
//        throw new Exception(this.toString());
        return null;
    }
    
    public FieldVO get() throws Exception {
        if (this.ins == null) {
            this.ins = this.getInstance();
        }
        if (this.ins == null) {
            throw new Exception("### ERROR ###\n" + this.toString());
        }
        return this.ins.get();
    }

    @Override
    public int compareTo(Integer o) {
        return o.compareTo(order);
    }
}