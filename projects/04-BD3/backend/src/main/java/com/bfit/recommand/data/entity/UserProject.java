package com.bfit.recommand.data.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName("user_project")
public class UserProject {

    @TableId
    private Long id;
    private String issuerAddress;
    private String reviewerAddress;
    private Integer reviewScore;
    private String projectAddress;
    private String remark;
    private Date dbCreateTime;
    private Date dbUpdateTime;
    @TableLogic(delval = "0")
    private Boolean deleted;

}
