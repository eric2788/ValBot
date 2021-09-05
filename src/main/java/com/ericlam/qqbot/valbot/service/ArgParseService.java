package com.ericlam.qqbot.valbot.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class ArgParseService {

    private final Map<Class<?>, Function<String, ?>> parserMap = new ConcurrentHashMap<>();

    public <T> void addParser(Class<T> type, Function<String, T> parser){
        this.parserMap.put(type, parser);
    }


    @PostConstruct
    public void onCreate(){
        parserMap.put(Boolean.class, Boolean::parseBoolean);
        parserMap.put(Integer.class, Integer::parseInt);
        parserMap.put(Short.class, Short::parseShort);
        parserMap.put(Long.class, Long::parseLong);
        parserMap.put(Byte.class, Byte::parseByte);
        parserMap.put(Double.class, Double::parseDouble);
        parserMap.put(Float.class, Float::parseFloat);
    }

    @SuppressWarnings("unchecked")
    public <T> T tryParse(String str, Class<T> type){
        if (parserMap.containsKey(type)){
            return (T) parserMap.get(type).apply(str);
        }else{
            throw new IllegalStateException("unknown type to parse: "+type);
        }
    }
}
