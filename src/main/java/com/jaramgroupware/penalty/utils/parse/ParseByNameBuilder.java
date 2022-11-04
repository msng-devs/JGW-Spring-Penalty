package com.jaramgroupware.penalty.utils.parse;

import com.jaramgroupware.penalty.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParseByNameBuilder {
    public Object parse(String target,String targetClass){
        switch (targetClass){

            case "String":
                return target;

            case "Integer":
                return Integer.parseInt(target);
            case "Member":
                return Member.builder().id(target).build();
            case "Long":
                return Long.parseLong(target);
            case "Bool":
                return Boolean.parseBoolean(target);
            case "Boolean":
                return Boolean.parseBoolean(target);
            default:
                return target;
        }
    }
}
