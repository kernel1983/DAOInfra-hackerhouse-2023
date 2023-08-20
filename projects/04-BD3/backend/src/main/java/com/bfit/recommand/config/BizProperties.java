package com.bfit.recommand.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class BizProperties {

    @Value("${bfit.recommend.nostra.relay.projects:https://api.web3bd.network/api/projects}")
    private String nostraProjectsUrl;

}
