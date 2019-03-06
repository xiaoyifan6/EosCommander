package oyz.com.eosapi.model.abi;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * EosAbiStruct
 */

public class EosAbiStruct {

    @Expose
    public String name;

    @Expose
    public String base;

    @Expose
    public List<EosAbiField> fields;

    @Override
    public String toString() {
        return "Struct name: " + name + ", base: " + base ;
    }
}
