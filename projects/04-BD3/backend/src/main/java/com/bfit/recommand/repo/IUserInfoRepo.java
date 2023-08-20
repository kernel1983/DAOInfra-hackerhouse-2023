package com.bfit.recommand.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bfit.recommand.data.entity.UserInfo;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface IUserInfoRepo extends IService<UserInfo> {
}
