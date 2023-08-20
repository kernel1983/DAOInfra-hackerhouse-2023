package com.bfit.recommand.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenizerEnum {

    GOOD(0,"well done, happy, love, 比较好，不错，非常满意, impressed, impressive, excellent, valuable, expected"),
    BAD(-1,"unexpected, sorry, 差劲, 差")
    ;


    private long code;
    private String desc;

}
