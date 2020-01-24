package com.flores.baking.data.webservice;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class WebserviceClient {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";
    private static Retrofit mRetrofit = null;

    public static Webservice getWebservice() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit.create(Webservice.class);
    }
}
