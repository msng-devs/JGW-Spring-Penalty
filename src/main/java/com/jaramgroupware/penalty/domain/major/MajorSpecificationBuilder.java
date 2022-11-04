package com.jaramgroupware.penalty.domain.major;

import com.jaramgroupware.penalty.utils.spec.SearchCriteria;
import com.jaramgroupware.penalty.utils.spec.SearchOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class MajorSpecificationBuilder {

    @Getter
    @AllArgsConstructor
    private enum LikeKeys{

        NAME("name","name");

        private final String queryParamName;
        private final String tableName;
    }

    public MajorSpecification toSpec(MultiValueMap<String, String> queryParam){


        MajorSpecification specification = new MajorSpecification();

            for (LikeKeys key : LikeKeys.values()) {
                if(queryParam.containsKey(key.getQueryParamName())){
                    specification.add(new SearchCriteria(key.getTableName()
                            , Arrays.asList(new String[]{queryParam.getFirst(key.getQueryParamName())})
                            ,SearchOperation.MATCH));
                }
            }

            return specification;
    }

}

