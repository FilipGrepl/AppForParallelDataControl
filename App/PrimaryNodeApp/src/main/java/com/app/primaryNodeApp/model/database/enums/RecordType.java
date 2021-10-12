/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.enums;

/**
 * The enumerator which represents type of utilization record of node in database.
 * @author Filip
 */
public enum RecordType {
    SECOND5, MINUTE, MINUTES_30, HOUR, HOURS_12, DAY;

    public static RecordType typeAfter(RecordType actType) {
        switch (actType)  {
            case SECOND5:
                return MINUTE;
            case MINUTE:
                return MINUTES_30;
            case MINUTES_30:
                return HOUR;
            case HOUR:
                return HOURS_12;
            case HOURS_12:
                return DAY;
            default:
                return null;
        }
    }
    public static RecordType typeBefore(RecordType actType) {
        switch (actType)  {
            case MINUTE:
                return SECOND5;
            case MINUTES_30:
                return MINUTE;
            case HOUR:
                return MINUTES_30;
            case HOURS_12:
                return HOUR;
            case DAY:
                return HOURS_12;
            default:
                return null;
        }
    }
    
}


/*
akumulace:
SECONDS5 -> every 12 values -> create MINUTE
MINUTE -> every 60*2 values (60 values accumulation) -> create HOUR
HOUR -> every 24*7+24 = 192h (24 values accumulation) -> create DAY
DAY -> if size() > 365 -> delete record
*/
