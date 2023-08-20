package com.bfit.recommand.repo;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bfit.recommand.data.entity.UserProject;
import com.bfit.recommand.data.mapper.UserProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class UserProjectRepository extends ServiceImpl<UserProjectMapper, UserProject> implements IUserProjectRepo {

    private final UserProjectMapper userProjectMapper;

    public Boolean insertBatch(List<UserProject> entityList){

        if (CollectionUtils.isEmpty(entityList)){
            return true;
        }

        return saveBatch(entityList);
    }

    public List<UserProject> queryListByProjectAddress(String projectAddress){
        return new LambdaQueryChainWrapper<>(userProjectMapper)
                .eq(UserProject::getProjectAddress, projectAddress)
                .list();
    }

    public List<UserProject> queryListByProjectAddressList(List<String> projectAddressList){
        return new LambdaQueryChainWrapper<>(userProjectMapper)
                .in(UserProject::getProjectAddress, projectAddressList)
                .list();
    }

    public List<UserProject> selectAll(){
        return new LambdaQueryChainWrapper<>(userProjectMapper).list();
    }

}
