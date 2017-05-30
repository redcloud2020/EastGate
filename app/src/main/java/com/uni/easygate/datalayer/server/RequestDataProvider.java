package com.uni.easygate.datalayer.server;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.uni.easygate.config.Config;
import com.uni.easygate.config.Parameters;
import com.uni.easygate.utilities.Methods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Sammy Shwairy on 2/28/2016.
 */
public class RequestDataProvider {

    protected Context mContext;

    public RequestDataProvider(Context mContext) {
        this.mContext = mContext;
    }

    public RequestModel sendMeasurements(String username, String password, JSONArray jsonObject) throws JSONException, UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        params.put(Parameters.USERNAME, username);
        params.put(Parameters.PASSWORD, password);
        params.put(Parameters.JSON_OBJECT, jsonObject);
        params.put(Parameters.TYPE, Parameters.TYPE_MEASUREMENT);
        String url = Config.URL;
        return new RequestModel(url, params);
    }
    public RequestModel addTruckEntry   (String username, String password, JSONArray jsonObject) throws JSONException, UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        params.put(Parameters.USERNAME, username);
        params.put(Parameters.PASSWORD, password);
        params.put(Parameters.JSON_OBJECT, jsonObject);
        params.put(Parameters.TYPE, Parameters.CAMP_ENTRANCE);
        String url = Config.URL;
        return new RequestModel(url, params);
    }
    public RequestModel addTruckExit   (String username, String password, JSONArray jsonObject) throws JSONException, UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        params.put(Parameters.USERNAME, username);
        params.put(Parameters.PASSWORD, password);
        params.put(Parameters.JSON_OBJECT, jsonObject);
        params.put(Parameters.TYPE, Parameters.CAMP_EXIT);
        String url = Config.URL;
        return new RequestModel(url, params);
    }

    public RequestModel addIsFull(String username, String password, JSONArray jsonObject) throws JSONException, UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        params.put(Parameters.USERNAME, username);
        params.put(Parameters.PASSWORD, password);
        params.put(Parameters.JSON_OBJECT, jsonObject);
        params.put(Parameters.TYPE, Parameters.TYPE_IS_FULL_ADD);
        String url = Config.URL;
        return new RequestModel(url, params);
    }
    public GetRequestModel getUsers(String username, String password,  String timestamp) throws JSONException, UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        params.put(Parameters.USERNAME, username);
        params.put(Parameters.PASSWORD, password);
        params.put(Parameters.TYPE, Parameters.TYPE_USERS);
        params.put(Parameters.TIMESTAMP, timestamp);
        String url = Config.URL;
        return new GetRequestModel(url, params);
    }
    public GetRequestModel getComment(String username, String password,  JSONArray jsonArray) throws JSONException, UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        params.put(Parameters.USERNAME, username);
        params.put(Parameters.PASSWORD, password);
        params.put(Parameters.TYPE, Parameters.COMMENT);
        params.put(Parameters.COMMENT, jsonArray);
        String url = Config.URL;
        return new GetRequestModel(url, params);
    }

    public GetRequestModel getRemainingTrucks(String username, String password,  String timestamp) throws JSONException, UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        params.put(Parameters.USERNAME, username);
        params.put(Parameters.PASSWORD, password);
        params.put(Parameters.TYPE, Parameters.TYPE_REMAINING);
        params.put(Parameters.TIMESTAMP, timestamp);
        String url = Config.URL;
        return new GetRequestModel(url, params);
    }
    public GetRequestModel getTanks(String username, String password,  String timestamp) throws JSONException, UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        params.put(Parameters.USERNAME, username);
        params.put(Parameters.PASSWORD, password);
        params.put(Parameters.TYPE, Parameters.TYPE_TANKS);
        params.put(Parameters.TIMESTAMP, Methods.getCurrentTimeStampApi());
        String url = Config.URL;
        return new GetRequestModel(url, params);
    }
    public GetRequestModel getTrucks(String username, String password,  String timestamp) throws JSONException, UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        params.put(Parameters.USERNAME, username);
        params.put(Parameters.PASSWORD, password);
        params.put(Parameters.TYPE, Parameters.TYPE_TRUCKS);
        params.put(Parameters.TIMESTAMP, timestamp);
        String url = Config.URL;
        return new GetRequestModel(url, params);
    }
    public GetRequestModel getDestinations(String username, String password,  String timestamp) throws JSONException, UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        params.put(Parameters.USERNAME, username);
        params.put(Parameters.PASSWORD, password);
        params.put(Parameters.TYPE, Parameters.TYPE_DESTINATIONS);
        params.put(Parameters.TIMESTAMP, timestamp);
        String url = Config.URL;
        return new GetRequestModel(url, params);
    }

    public RequestModel addComment(String username, String password, JSONArray jsonArray) throws JSONException, UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        params.put(Parameters.USERNAME, username);
        params.put(Parameters.PASSWORD, password);
        params.put(Parameters.TYPE, Parameters.TYPE_COMMENT);
        params.put(Parameters.JSON_OBJECT, jsonArray);
        String url = Config.URL;
        return new RequestModel(url, params);
    }
    public RequestModel addUserActivity(String username, String password, JSONArray jsonArray) throws JSONException, UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        params.put(Parameters.USERNAME, username);
        params.put(Parameters.PASSWORD, password);
        params.put(Parameters.TYPE, Parameters.USER_USAGE);
        params.put(Parameters.JSON_OBJECT, jsonArray);
        String url = Config.URL;
        return new RequestModel(url, params);
    }
}
