/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.NodeOverview.SelectUsage;

import com.app.primaryNodeApp.services.dataClasses.NodeData.UsageEnum;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

/**
 * The backing bean for selectOneButton of secondary node usage scale (nodesOverview, nodeDetail).
 * @author filip
 */
@ViewScoped
@ManagedBean
public class SelectUsageBean implements Serializable {
    
    /** STATIC PROPERTIES **/
    
    private static final Integer ITEMS_IN_TABLE_SELECT = 4;
    
    public static enum SelectUsageTypeEnum {
        TABLE_SELECT, GRAPH_SELECT
    }
    
    /** OBJECT PROPERTIES **/

    private UsageEnum selectValue; 
    private List<SelectItem> selectValues;
    private SelectUsageTypeEnum usageType;
    
    /** OBJECT METHODS **/

    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    public void postConstruct() {        
        this.selectValues = new ArrayList<>();
        this.selectValue = UsageEnum.HOUR;
        this.usageType = SelectUsageTypeEnum.TABLE_SELECT;
    }

    /**
     * Getter of selected usage value.
     * @return Selected usage value.
     */
    public UsageEnum getSelectValue() {
        return selectValue;
    }

    /**
     * Setter of selected usage value.
     * @param selectValue New value of selected usage value.
     */
    public void setSelectValue(UsageEnum selectValue) {
        this.selectValue = selectValue;
    }

    /**
     * Getter of all values, that can be selected as usage scale.
     * @return List of select items that can be selected.
     */
    public List<SelectItem> getSelectValues() {
        return selectValues;
    }

    /**
     * Setter of usage type.
     * @param usageType New value of usage type.
     */
    public void setUsageType(SelectUsageTypeEnum usageType) {
        this.usageType = usageType;
    }

    /**
     * Method which creates list of select items depending on usage type.
     */
    public void createSelectItems() {
        this.selectValues.clear();
        for (UsageEnum enumValue : UsageEnum.values()) {
            SelectItem sl = new SelectItem(enumValue.toString(), enumValue.getMessage());
            this.selectValues.add(sl);
            
            if (this.usageType == SelectUsageTypeEnum.TABLE_SELECT && selectValues.size() == ITEMS_IN_TABLE_SELECT)
                break;
        }
    }
}
