package com.lyw.springcloudstarter.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @TableName question
 */
@TableName(value ="question")
@Data
public class Question implements Serializable {
    private Long id;

    private String title;

    private String content;

    private String tags;

    private String answer;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    private String judgeCase;
    private String judgeConfig;
    private Integer submitNum;
    private Integer thumbNum;

    private Long userid;
    private Integer favourNum;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;
}