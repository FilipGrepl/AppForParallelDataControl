/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages;


import com.app.commons.socketMessages.sendingItems.UsageValues;

/**
 * Message, which is sent from Secondary to Primary node when some step is executed and contains the information about usage of system resources on Secondary node.
 * @author Filip
 */
public class UsageMsg extends GenericParamSocketMsg<UsageValues> {
    
    public UsageMsg (UsageValues usageValues) {
        super(MsgType.USAGE_OF_RESOURCES, usageValues);
    }
    
    @Override
    public String toString() {
         return "\n\t\tUsageMsg: \n\t\t{"
                + "\n\t\t\t CPU load: "+this.getMessage().getCpuLoad()
                + "\n\t\t\t, RAM usage: "+this.getMessage().getRamUsage()
                +"\n\t\t}\n\t";
    }
}
