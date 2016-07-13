package com.hackerkernel.cashking.constants;

public class EndPoints {
    //private static final String SERVER_URL = "http://192.168.1.3/co/cashking/";
    private static final String SERVER_URL = "http://api.hackerkernel.com/cashking/";
    private static final String VERSION = "v1.0/";
    private static final String BASE_URL = SERVER_URL + VERSION;
    public static final String REGISTER = BASE_URL + "login.php",
            OFFERLIST = BASE_URL + "getOfferList.php",
            IMAGE_BASE_URL = SERVER_URL + "/",
            GET_WALLET_AMOUNT = BASE_URL + "getWalletAmount.php",
            OFFER_DETAIL = BASE_URL + "getOfferDetail.php",
            DO_RECHARGE = BASE_URL + "doRecharge.php",
            R = BASE_URL + "r.php",
            VERIFY_PENDING_OFFER = BASE_URL + "getConversion.php";
}
