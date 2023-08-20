package com.bfit.recommand.job;

import com.bfit.recommand.common.util.CustomOkHttpUtils;
import com.bfit.recommand.common.util.JsonUtils;
import com.bfit.recommand.config.BizProperties;
import com.bfit.recommand.data.entity.ProjectInfo;
import com.bfit.recommand.repo.ProjectInfoRepository;
import com.bfit.recommand.repo.UserInfoRepository;
import com.bfit.recommand.repo.UserProjectRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncUpDataJob {

    private final UserInfoRepository userInfoRepository;
    private final UserProjectRepository userProjectRepository;
    private final ProjectInfoRepository projectInfoRepository;
    private final BizProperties bizProperties;

    @SneakyThrows
    @Scheduled(cron = "0/15 * * * * ?")
    public void syncUp() {
        log.info("fetch data from nostra begin");
        String projectList = CustomOkHttpUtils.doGet(bizProperties.getNostraProjectsUrl());
        if (StringUtils.isNotBlank(projectList)){
            JsonUtils.toList(projectList, new TypeReference<List<ProjectInfo>>() {});
        }


    }

}
