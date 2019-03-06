package oyz.com.eosapi.model.abi;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * EosAbiTable 表
 */

public class EosAbiTable {

    @Expose
    public String name;

    @Expose
    public String type;

    @Expose
    public String index_type;

    @Expose
    public List<String> key_names;

    @Expose
    public List<String> key_types;
}
