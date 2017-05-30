package com.uni.easygate.config;

/**
 * Created by Sammy Shwairy on 2/28/2016.
 */

public interface Parameters {

        String VEHICLE_NUMBER = "VEHICLE_NUMBER";
        String DRIVER_NUMBER = "DRIVER_NUMBER";
        String USER_NUMBER = "USER_NUMBER";
        String FIRST_TIME = "FIRST_TIME";
        String USER_ID_FOR_LOG = "USER_ID_FOR_LOG";
        //api params
        String USERNAME = "username";
        String PASSWORD = "password";
        String TYPE = "type";
        String JSON_OBJECT = "jsonObject";
        String TYPE_MEASUREMENT = "MeasurementLog";
        String TYPE_LOCATION  = "LocationLog";
        String TYPE_USERS = "Users";
        String TIMESTAMP = "timestamp";
        String TYPE_TANKS = "Tanks";
        String TYPE_COMMENT = "CommentLog";
        String COMMENT = "Comment";
        String TYPE_IS_FULL_ADD = "IsFullLog";
        String TYPE_MEASUREMENT_EMPTYING = "MeasurementEmptyingLog";
        String DUMMY_TIMESTAMP = "0000-00-00 00:00:00";
        String TYPE_REMAINING = "GetRemainingTrucks";
        String TYPE_TRUCKS = "Trucks";
        String CAMP_ENTRANCE = "CampEntrance";
        String TYPE_DESTINATIONS = "Destinations";
        String CAMP_EXIT = "CampExit";

        //Json objet params
        String ID = "id";
        String DRIVER_NAME = "DriverName";
        String TRUCK_NUMBER = "TruckNumber";
        String BLOCK = "Block";
        String DISTRICT = "District";
        String TANK_NUMBER = "TankNumber";
        String MEASUREMENT_BLACK_1 = "MeasurementBlack_1";
        String MEASUREMENT_BLACK_2 = "MeasurementBlack_2";
        String MEASUREMENT_COLOR_1= "MeasurementColor_1";
        String MEASUREMENT_COLOR_2= "MeasurementColor_2";
        String MEASUREMENT_AT = "MeasurementAt";
        String USER_USAGE = "UserUsage";

        //
        String LATITUDE = "LATITUDE";
        String LONGITUDE = "LONGITUDE";

        String USER_ID_ABOUT = "USER_ID_ABOUT";

        String ACTIVITY_LOG_IN = "1";
        String ACTIVITY_LOG_OUT = "2";
        String ACTIVITY_GPS_TURNED_ON = "3";
        String ACTIVITY_GPS_TURNED_OFF = "3";
        String ACTIVITY_AUTOMATIC_ON = "5";
        String ACTIVITY_AUTOMATIC_OFF = "5";

        String LOGGED = "LOGGED";
        String GET = "GET";
}
