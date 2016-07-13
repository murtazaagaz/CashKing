package com.hackerkernel.cashking.parser;

import com.hackerkernel.cashking.constants.Constants;
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
    public  static SimplePojo SimpleParser(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        SimplePojo simplePojo = new SimplePojo();
        simplePojo.setMessage(obj.getString(Constants.COM_MESSAGE));
        simplePojo.setReturned(obj.getBoolean(Constants.COM_RETURN));
        return simplePojo;


    }

    public static List<DealsListPojo> parseDealsList(JSONArray data,String type) throws JSONException {
        List<DealsListPojo> dealList = new ArrayList<>();
        for (int i = 0; i <data.length() ; i++) {
            JSONObject obj = data.getJSONObject(i);
            DealsListPojo pojo = new DealsListPojo();
            pojo.setDealName(obj.getString(Constants.COM_NAME));
            pojo.setDealAmount(obj.getString(Constants.COM_AMOUNT));
            pojo.setDealDescription(obj.getString(Constants.COM_SHORT_DESCRIPTION));
            pojo.setId(obj.getString(Constants.COM_ID));
            pojo.setImageUrl(obj.getString(Constants.COM_IMG));


            if (type.equals(Constants.OFFER_TYPE_PENDING)){
                //Parse pending offers
                pojo.setAffOfferId(obj.getString(Constants.COM_AFF_OFFER_ID));
                pojo.setNetworkId(obj.getString(Constants.COM_NETWORK_ID));
            }else if (type.equals(Constants.OFFER_TYPE_COMPLETED)){
                //Parse Completed offers
                pojo.setTime(obj.getString(Constants.COM_TIME));
            }


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

            pojo.setLink(obj.getString(Constants.COM_LINK));
            pojo.setAffOfferId(obj.getString(Constants.COM_OFFER_ID));
            pojo.setAffId(obj.getString(Constants.COM_AFF_ID));

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