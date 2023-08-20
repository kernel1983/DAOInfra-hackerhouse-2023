package com.bfit.recommand.repo;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bfit.recommand.data.entity.UserInfo;
import com.bfit.recommand.data.mapper.UserInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserInfoRepository extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoRepo {

    private final UserInfoMapper userInfoMapper;

    public List<UserInfo> queryAll() {
        return new LambdaQueryChainWrapper<>(userInfoMapper).list();
    }

    public List<UserInfo> queryByUserWalletList(List<String> userWalletList) {

        return new LambdaQueryChainWrapper<>(userInfoMapper)
                .in(UserInfo::getUserWallet, userWalletList)
                .list();
    }

    public Boolean insertBatch(List<UserInfo> entityList){

        if (CollectionUtils.isEmpty(entityList)){
            return true;
        }

        return saveBatch(entityList);
    }

}
