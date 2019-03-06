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
package io.plactal.eoscommander.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.plactal.eoscommander.data.local.repository.EosAccountRepository;
import io.plactal.eoscommander.data.prefs.PreferencesHelper;
import io.plactal.eoscommander.data.wallet.EosWalletManager;
import io.reactivex.Observable;
import oyz.com.eosapi.crypto.ec.EosPrivateKey;
import oyz.com.eosapi.crypto.ec.EosPublicKey;
import oyz.com.eosapi.model.abi.EosAbiMain;
import oyz.com.eosapi.model.api.AccountInfoRequest;
import oyz.com.eosapi.model.api.EosChainInfo;
import oyz.com.eosapi.model.api.GetBalanceRequest;
import oyz.com.eosapi.model.api.GetCodeRequest;
import oyz.com.eosapi.model.api.GetRequestForCurrency;
import oyz.com.eosapi.model.api.GetRequiredKeys;
import oyz.com.eosapi.model.api.GetTableRequest;
import oyz.com.eosapi.model.api.JsonToBinRequest;
import oyz.com.eosapi.model.api.PushTxnResponse;
import oyz.com.eosapi.model.chain.Action;
import oyz.com.eosapi.model.chain.PackedTransaction;
import oyz.com.eosapi.model.chain.SignedTransaction;
import oyz.com.eosapi.model.types.EosNewAccount;
import oyz.com.eosapi.model.types.EosTransfer;
import oyz.com.eosapi.model.types.TypeAccountName;
import oyz.com.eosapi.model.types.TypeAsset;
import oyz.com.eosapi.model.types.TypeChainId;
import oyz.com.eosapi.model.types.TypePublicKey;
import oyz.com.eosapi.model.types.TypeSymbol;
import oyz.com.eosapi.net.NodeosApi;
import oyz.com.eosapi.util.Consts;
import oyz.com.eosapi.util.StringUtils;
import oyz.com.eosapi.util.Utils;

/**
 * Created by swapnibble on 2017-11-03.
 */
@Singleton
public class EoscDataManager {

    private final NodeosApi mNodeosApi;
    private final PreferencesHelper mPrefHelper;
    private final EosWalletManager  mWalletMgr;
    private final EosAccountRepository mAccountRepository;

    private HashMap<String,EosAbiMain> mAbiObjHouse;

    @Inject
    public EoscDataManager(NodeosApi nodeosApi, EosWalletManager walletManager, EosAccountRepository accountRepository, PreferencesHelper prefHelper) {
        mNodeosApi = nodeosApi;
        mWalletMgr  = walletManager;
        mAccountRepository = accountRepository;
        mPrefHelper = prefHelper;

        // set wallet directory
        mWalletMgr.setDir( mPrefHelper.getWalletDirFile() );
        mWalletMgr.openExistingsInDir();

        // set core symbol
        TypeSymbol.setCoreSymbol( mPrefHelper.getCoreSymbolPrecision(), mPrefHelper.getCoreSymbolString());

        mAbiObjHouse = new HashMap<>();
    }


    public void updateCoreSymbol(String symbolStr, int symbolPrecision){
        mPrefHelper.putCoreSymbolInfo( StringUtils.isEmpty(symbolStr) ? Consts.DEFAULT_SYMBOL_STRING : symbolStr
                , symbolPrecision <= 0 ? Consts.DEFAULT_SYMBOL_PRECISION : symbolPrecision );

        TypeSymbol.setCoreSymbol( mPrefHelper.getCoreSymbolPrecision(), mPrefHelper.getCoreSymbolString());
    }

    public EosWalletManager getWalletManager() { return mWalletMgr; }

    public PreferencesHelper getPreferenceHelper() { return mPrefHelper; }


    public void addAccountHistory(String... accountNames){
        mAccountRepository.addAll(accountNames);
    }

    public void addAccountHistory(List<String> accountNames){
        mAccountRepository.addAll(accountNames);
    }

    public void deleteAccountHistory( String accountName ) {
        mAccountRepository.delete( accountName );
    }

    public List<String> searchAccount( String nameStarts){
        return mAccountRepository.searchName( nameStarts );
    }

    public void pushAbiObject(String key, EosAbiMain abi){
        mAbiObjHouse.put(key , abi );
    }

    public EosAbiMain popAbiObject( String key) {
        return mAbiObjHouse.remove( key );
    }

    public void clearAbiObjects(){
        mAbiObjHouse.clear();
    }

    public Observable<EosChainInfo> getChainInfo(){

        return mNodeosApi.readInfo("get_info");
    }

    public Observable<String> getTable( String accountName, String code, String table
             ,int indexPos, String keyType, String encodeType,String lowerBound, String upperBound, int limit ){

        return mNodeosApi.getTable(
                new GetTableRequest(accountName, code, table, indexPos , keyType, encodeType, lowerBound, upperBound, limit))
                .map( tableResult -> Utils.prettyPrintJson(tableResult));
    }

    public Observable<EosPrivateKey[]> createKey(int count ) {
        return Observable.fromCallable( () -> {
            EosPrivateKey[] retKeys = new EosPrivateKey[ count ];
            for ( int i = 0; i < count; i++) {
                retKeys[i] = new EosPrivateKey();
            }

            return retKeys;
        } );
    }



    private SignedTransaction createTransaction(String contract, String actionName, String dataAsHex,
                                                String[] permissions, EosChainInfo chainInfo ){
        currentBlockInfo = chainInfo;
        Action action = new Action(contract, actionName);
        action.setAuthorization(permissions);
        action.setData( dataAsHex );

        SignedTransaction txn = new SignedTransaction();
        txn.addAction( action );
        txn.putSignatures( new ArrayList<>());


        if ( null != chainInfo ) {
            txn.setReferenceBlock(chainInfo.getHeadBlockId());
            txn.setExpiration(chainInfo.getTimeAfterHeadBlockTime(Consts.TX_EXPIRATION_IN_MILSEC));
        }

        return txn;
    }

    private SignedTransaction createTransaction(List<Action> actions, EosChainInfo chainInfo ){
        currentBlockInfo = chainInfo;
        SignedTransaction txn = new SignedTransaction();
        txn.setActions( actions );
        txn.putSignatures( new ArrayList<>());


        if ( null != chainInfo ) {
            txn.setReferenceBlock(chainInfo.getHeadBlockId());
            txn.setExpiration(chainInfo.getTimeAfterHeadBlockTime(Consts.TX_EXPIRATION_IN_MILSEC));
        }

        return txn;
    }


    private Observable<PackedTransaction> signAndPackTransaction(SignedTransaction txnBeforeSign) {

        return mNodeosApi.getRequiredKeys( new GetRequiredKeys( txnBeforeSign, mWalletMgr.listPubKeys() ))
                .map( keys -> {
                    final SignedTransaction stxn ;
                    if ( mPrefHelper.shouldSkipSigning() ) {
                        stxn = txnBeforeSign;
                    }
                    else {
                        stxn = mWalletMgr.signTransaction(txnBeforeSign, keys.getKeys(), new TypeChainId(currentBlockInfo.getChain_id()));
                    }

                    return new PackedTransaction(stxn);
                });
    }


    private String[] getActivePermission(String accountName ) {
        return new String[] { accountName + "@active" };
    }


    public Observable<JsonObject> readAccountInfo(String accountName ) {
        return mNodeosApi.getAccountInfo(new AccountInfoRequest(accountName));
    }



    public Observable<JsonObject> getActions(String accountName, int pos, int offset  ) {

        JsonObject gsonObject = new JsonObject();
        gsonObject.addProperty( "account_name", accountName);
        gsonObject.addProperty( "pos", pos);
        gsonObject.addProperty( "offset", offset);

        return mNodeosApi.getActions( gsonObject);
    }

    public Observable<JsonObject> getServants( String accountName ) {

        JsonObject gsonObject = new JsonObject();
        gsonObject.addProperty( NodeosApi.GET_SERVANTS_KEY, accountName);

        return mNodeosApi.getServants( gsonObject);
    }

    EosChainInfo currentBlockInfo;
    void setInfo(EosChainInfo info){
        currentBlockInfo = info;
    }

    public Observable<PushTxnResponse> createAccount(EosNewAccount newAccountData) {
        return getChainInfo()
                .map( info -> createTransaction(Consts.EOSIO_SYSTEM_ACCOUNT, newAccountData.getActionName(), newAccountData.getAsHex()
                        , getActivePermission( newAccountData.getCreatorName() ), info ))
                .flatMap( txn -> signAndPackTransaction( txn))
                .flatMap( packedTxn -> mNodeosApi.pushTransaction( packedTxn ));
    }

    public Observable<Action> createAccountAction(String creator, String newAccount, EosPublicKey ownerKey, EosPublicKey activeKey) {
        EosNewAccount newAccountData = new EosNewAccount(creator, newAccount
                , TypePublicKey.from( ownerKey) , TypePublicKey.from( activeKey) );


        Action action = new Action(Consts.EOSIO_SYSTEM_ACCOUNT, newAccountData.getActionName());
        action.setAuthorization( getActivePermission( newAccountData.getCreatorName() ) );
        action.setData( newAccountData.getAsHex() );

        return Observable.just( action );
    }



    private Observable<Action> getActionAfterBindArgs( String contract, String permissionAccount, String actionName, String args) {
        return mNodeosApi.jsonToBin( new JsonToBinRequest( contract, actionName, args) )
                .map( binResp -> {
                    Action action = new Action(contract, actionName );
                    action.setAuthorization( getActivePermission(permissionAccount));
                    action.setData( binResp.getBinargs());

                    return action;
                });
    }

    public Observable<Action> buyRamInAssetAction(String payer, String receiver, String assetQuantity) {
        JsonObject object = new JsonObject();
        object.addProperty("payer", new TypeAccountName(payer).toString());
        object.addProperty("receiver", new TypeAccountName(receiver).toString());
        object.addProperty("quant", new TypeAsset( assetQuantity ).toString());

        return getActionAfterBindArgs( Consts.EOSIO_SYSTEM_ACCOUNT, payer, "buyram", new Gson().toJson(object));
    }


    public Observable<Action> delegateAction( String from, String receiver, String networkAsset, String cpuAsset, boolean transfer) {
        JsonObject object = new JsonObject();
        object.addProperty("from", new TypeAccountName(from).toString());
        object.addProperty("receiver", new TypeAccountName(receiver).toString());
        object.addProperty("stake_net_quantity", new TypeAsset(networkAsset).toString() );
        object.addProperty("stake_cpu_quantity", new TypeAsset(cpuAsset).toString() );
        object.addProperty( "transfer", transfer);

        return getActionAfterBindArgs( Consts.EOSIO_SYSTEM_ACCOUNT, from, "delegatebw", new Gson().toJson(object) );
    }

    public Observable<JsonObject> transfer( String from, String to, long amount, String memo ) {

        EosTransfer transfer = new EosTransfer(from, to, amount, memo);

        return pushActionRetJson(Consts.EOSIO_TOKEN_CONTRACT, transfer.getActionName(),Utils.prettyPrintJson(transfer) , getActivePermission( from ) ); //transfer.getAsHex()
    }

    public Observable<JsonObject> pushActionRetJson(String contract, String action, String data, String[] permissions) {
        return mNodeosApi.jsonToBin( new JsonToBinRequest( contract, action, data ))
                .flatMap( jsonToBinResp -> getChainInfo()
                                            .map( info -> createTransaction( contract, action, jsonToBinResp.getBinargs(), permissions, info )) )
                .flatMap( this::signAndPackTransaction )
                .flatMap( mNodeosApi::pushTransactionRetJson );
    }

    public Observable<PushTxnResponse> pushAction(String contract, String action, String data, String[] permissions) {
        return mNodeosApi.jsonToBin( new JsonToBinRequest( contract, action, data ))
                .flatMap( jsonToBinResp -> getChainInfo()
                        .map( info -> createTransaction( contract, action, jsonToBinResp.getBinargs(), permissions, info )) )
                .flatMap( this::signAndPackTransaction )
                .flatMap( mNodeosApi::pushTransaction );
    }


    public Observable<PushTxnResponse> pushActions(List<Action> actions ){
        return getChainInfo()
                .map( info -> createTransaction( actions, info ))
                .flatMap( this::signAndPackTransaction )
                .flatMap( mNodeosApi::pushTransaction );
    }

    public Observable<EosAbiMain> getCodeAbi( String contract ) {
        return mNodeosApi.getCode( new GetCodeRequest(contract))
                .filter( codeResp -> codeResp.isValidCode())
                .map( result -> new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                        .create().fromJson(result.getAbi(), EosAbiMain.class) );
    }

    public Observable<EosAbiMain> getAbi( String contract ) {
        return mNodeosApi.getAbi( new GetCodeRequest(contract))
                .map( result -> new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                        .create().fromJson(result.getAbi(), EosAbiMain.class) );
    }

    public Observable<EosAbiMain> getAbiMainFromJson( String jsonStr ) {
        return Observable.just( new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create().fromJson(jsonStr, EosAbiMain.class));
    }

    public Observable<String> getCurrencyBalance(String contract, String account, String symbol){
        return mNodeosApi.getCurrencyBalance( new GetBalanceRequest(contract,account,symbol))
                .map( result -> Utils.prettyPrintJson(result));
    }

    public Observable<String> getCurrencyStats(String contract, String symbol){
        return mNodeosApi.getCurrencyStats( new GetRequestForCurrency(contract, symbol))
                .map( result -> Utils.prettyPrintJson(result));
    }


}
