package com.hackerkernel.cashking.parser;

import android.util.Log;

import com.hackerkernel.cashking.constants.Constants;
import com.hackerkernel.cashking.constants.EndPoints;
import com.hackerkernel.cashking.pojo.DealsListPojo;
import com.hackerkernel.cashking.pojo.DetailOfferPojo;
import com.hackerkernel.cashking.pojo.OfferInstallementPojo;
import com.hackerkernel.cashking.pojo.SimplePojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JsonParsor {
    public  static SimplePojo simpleParser(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        SimplePojo simplePojo = new SimplePojo();
        simplePojo.setMessage(obj.getString(Constants.COM_MESSAGE));
        simplePojo.setReturned(obj.getBoolean(Constants.COM_RETURN));
        return simplePojo;


    }
    public static List<DealsListPojo> parseDealsList(JSONArray data) throws JSONException {
        List<DealsListPojo> dealList = new ArrayList<>();
        for (int i = 0; i <data.length() ; i++) {
            JSONObject obj = data.getJSONObject(i);
            DealsListPojo pojo = new DealsListPojo();
            pojo.setDealName(obj.getString(Constants.COM_NAME));
            pojo.setDealOffer(obj.getString(Constants.COM_AMOUNT));
            pojo.setDealDescription(obj.getString(Constants.COM_SHORT_DESCRIPTION));
            pojo.setId(obj.getString(Constants.COM_ID));
            pojo.setImageUrl(obj.getString(Constants.COM_IMG));
            dealList.add(pojo);
        }
        return dealList;

    }
    public static DetailOfferPojo parseDetailOffer(JSONArray data) throws JSONException {
        DetailOfferPojo pojo = new DetailOfferPojo();
        for (int i = 0; i <data.length() ; i++) {
            JSONObject obj = data.getJSONObject(i);
            pojo.setImageUrl(obj.getString(Constants.COM_IMG));
            pojo.setName(obj.getString(Constants.COM_NAME));
            pojo.setAmount(obj.getString(Constants.COM_AMOUNT));
            pojo.setDetailDescription(obj.getString(Constants.COM_DETAIL_DESCRIPTION));
            pojo.setShortDescription(obj.getString(Constants.COM_SHORT_DESCRIPTION));
            pojo.setDetailInstruction(obj.getString(Constants.COM_DETAIL_INSTRUCTION));
            pojo.setNote(obj.getString(Constants.COM_NOTE));

            List<OfferInstallementPojo> list = new ArrayList<>();
            JSONArray installmentArray = obj.getJSONArray(Constants.COM_INSTALLMENT);
            for (int j = 0; j < installmentArray.length(); j++) {
                JSONObject jo = installmentArray.getJSONObject(j);
                OfferInstallementPojo c = new OfferInstallementPojo();
                c.setDescription(jo.getString(Constants.COM_DESCRIPTION));
                c.setAmount(jo.getString(Constants.COM_AMOUNT));
                c.setType(jo.getString(Constants.COM_TYPE));
                list.add(c);
            }
            pojo.setInstallmentList(list);
        }
        return pojo;
    }
}