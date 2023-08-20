package com.bfit.recommand.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bfit.recommand.data.entity.UserInfo;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
