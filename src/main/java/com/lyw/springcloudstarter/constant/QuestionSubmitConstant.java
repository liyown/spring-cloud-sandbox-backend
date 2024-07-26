package com.lyw.springcloudstarter.constant;

import lombok.Getter;

/**
 * @author: liuyaowen
 * @poject: springboot-starter
 * @create: 2024-07-22 12:52
 * @Description:
 */
public interface QuestionSubmitConstant {

    // 题目提交状态
    String SUBMIT_STATUS_SUBMIT = "submit";
    String SUBMIT_STATUS_JUDGING = "judging";
    String SUBMIT_STATUS_SUCCESS = "success";
    String SUBMIT_STATUS_FAIL = "fail";


    String JUDGE_RESULT_SUCCESS = "Accepted";
    String JUDGE_RESULT_WRONG_ANSWER = "Wrong Answer";
    String JUDGE_RESULT_TIME_LIMIT_EXCEEDED = "Time Limit Exceeded";
    String JUDGE_RESULT_MEMORY_LIMIT_EXCEEDED = "Memory Limit Exceeded";
    String JUDGE_RESULT_RUNTIME_ERROR = "Runtime Error";
    String JUDGE_RESULT_SYSTEM_ERROR = "System Error";
    String JUDGE_RESULT_COMPILE_ERROR = "Compile Error";

    // 禁止操作
    String JUDGE_RESULT_FORBIDDEN_OPERATION = "Forbidden Operation";

    // 语言
    @Getter
    enum Language {
        JAVA("java"),
        PYTHON("python"),
        C("c"),
        CPP("cpp");

        private final String value;

        Language(String value) {
            this.value = value;
        }


        public static boolean contains(String language) {
            for (Language l : Language.values()) {
                if (l.getValue().equals(language)) {
                    return true;
                }
            }
            return false;
        }
    }

}
