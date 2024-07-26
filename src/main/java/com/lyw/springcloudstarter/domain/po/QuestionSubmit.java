package com.lyw.springcloudstarter.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @TableName question_submit
 */
@TableName(value ="question_submit")
@Data
public class QuestionSubmit implements Serializable {

    private Long id;

    private String language;

    private String code;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    // 判题状态
    private String status;
    // 判题信息
    private String judgeInfo;


    // 有索引
    private Long questionId;
    // 有索引
    private Long userid;



    private Date createTime;
    private Date updateTime;
    private Integer isDelete;
}