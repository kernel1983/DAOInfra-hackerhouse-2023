package com.bfit.recommand.data.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("project_info")
public class ProjectInfo implements Serializable {

    /**
     * (
     *     id              bigint auto_increment
     *         primary key,
     *     issuer_address  varchar(512)  default '' null,
     *     description     varchar(3000) default '' not null,
     *     project_address varchar(512)  default '' null,
     *     db_create_time  datetime                 null,
     *     db_update_time  datetime                 null,
     *     deleted         tinyint                  null
     * )
     */
    private Long id;
    private String issuerAddress;
    private String projectAddress;
    private String description;
    @JsonIgnore
    private Date dbCreateTime;
    @JsonIgnore
    private Date dbUpdateTime;
    @TableLogic(value = "0")
    private Boolean deleted;

}
