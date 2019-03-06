/*
 * Copyright (c) 2017-2018 PlayerOne.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package oyz.com.eosapi.net;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.reactivex.Observable;
import oyz.com.eosapi.model.api.AccountInfoRequest;
import oyz.com.eosapi.model.api.EosChainInfo;
import oyz.com.eosapi.model.api.GetAbiResponse;
import oyz.com.eosapi.model.api.GetBalanceRequest;
import oyz.com.eosapi.model.api.GetCodeRequest;
import oyz.com.eosapi.model.api.GetCodeResponse;
import oyz.com.eosapi.model.api.GetRequestForCurrency;
import oyz.com.eosapi.model.api.GetRequiredKeys;
import oyz.com.eosapi.model.api.GetTableRequest;
import oyz.com.eosapi.model.api.JsonToBinRequest;
import oyz.com.eosapi.model.api.JsonToBinResponse;
import oyz.com.eosapi.model.api.PushTxnResponse;
import oyz.com.eosapi.model.api.RequiredKeysResponse;
import oyz.com.eosapi.model.chain.PackedTransaction;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by swapnibble on 2017-09-08.
 */

public interface NodeosApi {

    @POST("/v1/chain/{infoType}")
    Observable<EosChainInfo> readInfo(@Path("infoType") String infoType);

    @POST("/v1/chain/get_account")
    Observable<JsonObject> getAccountInfo(@Body AccountInfoRequest body);

    @POST("/v1/chain/get_table_rows")
    Observable<JsonObject> getTable(@Body GetTableRequest body);

    @POST("/v1/chain/push_transaction")
    Observable<PushTxnResponse> pushTransaction(@Body PackedTransaction body);

    @POST("/v1/chain/push_transaction")
    Observable<JsonObject> pushTransactionRetJson(@Body PackedTransaction body);

    @POST("/v1/chain/get_required_keys")
    Observable<RequiredKeysResponse> getRequiredKeys(@Body GetRequiredKeys body);

    @POST("/v1/chain/get_currency_balance")
    Observable<JsonArray> getCurrencyBalance(@Body GetBalanceRequest body);

    @POST("/v1/chain/get_currency_stats")
    Observable<JsonObject> getCurrencyStats(@Body GetRequestForCurrency body);


    @POST("/v1/chain/abi_json_to_bin")
    Observable<JsonToBinResponse> jsonToBin(@Body JsonToBinRequest body);

    @POST("/v1/chain/get_code")
    Observable<GetCodeResponse> getCode(@Body GetCodeRequest body);

    @POST("/v1/chain/get_abi")
    Observable<GetAbiResponse> getAbi(@Body GetCodeRequest body);

    @POST("/v1/history/get_controlled_accounts")
    Observable<JsonObject> getServants(@Body JsonObject body);

    @POST("/v1/history/get_actions")
    Observable<JsonObject> getActions(@Body JsonObject body);

    String GET_SERVANTS_KEY = "controlling_account";
}
