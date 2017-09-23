package com.b2beyond.wallet.b2bcoin.view.controller;

import com.b2beyond.wallet.b2bcoin.daemon.rpc.JsonRpcExecutor;
import com.b2beyond.wallet.b2bcoin.daemon.rpc.model.Address;
import com.b2beyond.wallet.b2bcoin.daemon.rpc.model.AddressBalance;
import org.apache.log4j.Logger;


public class AddressesController {

    private Logger LOGGER = Logger.getLogger(this.getClass());

    private JsonRpcExecutor<Address> createAddressExecutor;
    private JsonRpcExecutor<AddressBalance> balanceExecutor;


    public AddressesController(JsonRpcExecutor<Address> createAddressExecutor, JsonRpcExecutor<AddressBalance> balanceExecutor) {
        this.createAddressExecutor = createAddressExecutor;
        this.balanceExecutor = balanceExecutor;
    }

    public Address createAddress() {
        LOGGER.info("Create address");
        this.createAddressExecutor.setParams("\"params\": {}");
        return this.createAddressExecutor.execute();
    }

    public AddressBalance getBalance(String address) {
        LOGGER.info("Get address balance : " + address);
        this.balanceExecutor.setParams("\"params\": {" +
                "\"address\": \"" + address + "\"" +
                "}");

        return this.balanceExecutor.execute();
    }

}