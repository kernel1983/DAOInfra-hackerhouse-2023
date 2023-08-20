package com.bfit.recommand.repo;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.bfit.recommand.data.entity.ProjectInfo;
import com.bfit.recommand.data.mapper.ProjectInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProjectInfoRepository {

    private final ProjectInfoMapper projectInfoMapper;

    public List<ProjectInfo> queryAll(){
        return new LambdaQueryChainWrapper<>(projectInfoMapper).list();
    }

    public ProjectInfo queryByProjectAddress(String projectAddress){
        return new LambdaQueryChainWrapper<>(projectInfoMapper)
                .eq(ProjectInfo::getProjectAddress, projectAddress)
                .last("limit 1").one();
    }

    public List<ProjectInfo> queryRecentByIssuer(String issuerAddress){
        return new LambdaQueryChainWrapper<>(projectInfoMapper)
                .ne(ProjectInfo::getIssuerAddress, issuerAddress)
                .orderByDesc(ProjectInfo::getDbCreateTime)
                .list();
    }

}
