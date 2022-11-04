package com.jaramgroupware.penalty.domain.penalty;

import com.jaramgroupware.penalty.utils.spec.PredicatesBuilder;
import com.jaramgroupware.penalty.utils.spec.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

//ref : https://attacomsian.com/blog/spring-data-jpa-specifications
public class PenaltySpecification implements Specification<Penalty>{

    private final List<SearchCriteria> list = new ArrayList<>();

    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<Penalty> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        PredicatesBuilder predicatesBuilder = new PredicatesBuilder();

        query.distinct(true);

        //count query error
        //ref : https://starrybleu.github.io/development/2018/08/10/jpa-n+1-fetch-strategy-specification.html
        if (query.getResultType() != Long.class && query.getResultType() != long.class){
            root.fetch("targetMember", JoinType.LEFT);
        }

        return predicatesBuilder.build(root,query,builder,list);
    }
}
