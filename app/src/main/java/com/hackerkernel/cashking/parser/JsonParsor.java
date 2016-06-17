package com.hackerkernel.cashking.parser;

import com.hackerkernel.cashking.constants.Constants;
import com.hackerkernel.cashking.pojo.SimplePojo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by QUT on 6/16/2016.
 */
public class JsonParsor {
    public  static SimplePojo simpleParser(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        SimplePojo simplePojo = new SimplePojo();
        simplePojo.setMessage(obj.getString(Constants.COM_MESSAGE));
        simplePojo.setReturned(obj.getBoolean(Constants.COM_RETURN));
        return simplePojo;


    }
}
