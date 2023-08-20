package com.bfit.recommand.data.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName("user_info")
public class UserInfo {

    @TableId
    private Long id;
    private String userName;
    private String userWallet;
    private String avatar;
    private Date dbCreateTime;
    private Date dbUpdateTime;
    @TableLogic(value = "0")
    private boolean deleted;

}
