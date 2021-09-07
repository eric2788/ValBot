package com.ericlam.qqbot.valbot.service;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

@Service
public class YesNoDataService {

    private static final Random RANDOM = new Random();
    private static final Pattern questionPattern = Pattern.compile("^.+是.+吗[\\?\\？]*$");

    private final Logger logger;
    private final Map<String, Boolean> answers;

    public YesNoDataService(ValDataService dataService, Logger logger) {
        this.logger = logger;
        this.answers = dataService.getData().answers;
    }

    public boolean isInvalidQuestion(String question) {
        return !questionPattern.matcher(question).find();
    }

    public String getYesNoAnswer(String question) {
        if (isInvalidQuestion(question)) {
            return null;
        }
        question = removeQuestionMark(question);
        if (!this.answers.containsKey(question)){
            this.logger.info("找不到问题，正在生成新的答案...");
            var result = RANDOM.nextBoolean();
            this.logger.info("新答案为 {}", result);
            this.setYesNoAnswer(question, result);
        }
        return this.answers.get(question) ? "确实" : "并不是";
    }

    public void setYesNoAnswer(String question, boolean result){
        question = removeQuestionMark(question);
        this.answers.put(question, result);
    }

    public boolean removeYesNoAnswer(String question){
        return this.answers.remove(question) != null;
    }

    private String removeQuestionMark(String q) {
        return q.replaceAll("[\\?\\？]", "");
    }

}
