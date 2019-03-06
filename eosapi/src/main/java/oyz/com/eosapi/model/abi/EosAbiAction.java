package oyz.com.eosapi.model.abi;

import com.google.gson.annotations.Expose;

/**
 * Action
 */
public class EosAbiAction {
    @Expose
    public String name;

    @Expose
    public String type;

    @Override
    public String toString(){
        return "EosAction: " + name + ", type: "+ type ;
    }
}
