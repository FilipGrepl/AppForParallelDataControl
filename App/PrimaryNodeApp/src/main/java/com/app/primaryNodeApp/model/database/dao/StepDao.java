/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.dao;

import com.app.primaryNodeApp.model.database.entity.Step;
import java.util.List;
import javax.persistence.criteria.Predicate;

/**
 * The Dao object of Step entity.
 * @author Filip
 */
public class StepDao extends Dao<Step, Long> {
    
    /**
     * Constructor
     */
    public StepDao() {
        super(Step.class);
    }
    
    /**
     * Method that returns previous step for entered step.
     * @param step Step for which the previous step to be returned.
     * @return Previous step for entered step. If no entity is found, null is returned.
     */
    public Step getPreviousStep(Step step) {
        this.prepareCriteria();
        Predicate[] predicates = new Predicate[2];
        predicates[0] = this.queryBuilder.equal(this.queryRoot.get("job"), step.getJob().getId());
        predicates[1] = this.queryBuilder.equal(this.queryRoot.get("stepOrder"), step.getStepOrder()-1);
        this.queryCriteria.select(this.queryRoot).where(this.queryBuilder.and(predicates));
        List<Step> steps = this.executeQuery();
        if (steps.isEmpty())
            return null;
        else if (steps.size() != 1) {
            StringBuilder IDs = new StringBuilder();
            for (Step s : steps) IDs.append(s.getId()).append(" ");
            daoLogger.logDuplicateStepOrderErr(step.getJob().getId(), IDs.toString());
        }
        return steps.get(0);
    }    
}
