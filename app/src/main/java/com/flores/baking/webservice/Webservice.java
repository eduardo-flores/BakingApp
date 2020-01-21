package com.flores.baking.webservice;

import com.flores.baking.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Webservice {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipes();

}
