package com.bfit.recommand.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommandNeed {

    private String avatar;
    private String name;
    private String walletAddress;

}
