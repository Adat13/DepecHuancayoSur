package com.depec.depechuancayosur;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BibleApiService {
    private static final String BASE_URL = "https://bibleapi.co/";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static BibleApi getBibleApi() {
        return retrofit.create(BibleApi.class);
    }
}
