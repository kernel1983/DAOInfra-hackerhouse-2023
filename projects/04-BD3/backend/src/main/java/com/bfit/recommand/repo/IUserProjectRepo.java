package com.bfit.recommand.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bfit.recommand.data.entity.UserProject;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface IUserProjectRepo extends IService<UserProject> {



}
