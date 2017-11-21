package com.b2beyond.wallet.b2bcoin.view.controller;

import com.b2beyond.wallet.b2bcoin.daemon.rpc.JsonRpcExecutor;
import com.b2beyond.wallet.b2bcoin.daemon.rpc.model.Address;
import com.b2beyond.wallet.b2bcoin.daemon.rpc.model.Payment;
import com.b2beyond.wallet.b2bcoin.daemon.rpc.model.PaymentInput;
import com.b2beyond.wallet.b2bcoin.daemon.rpc.model.exception.KnownJsonRpcException;
import org.apache.log4j.Logger;


public class PaymentController {

    private Logger LOGGER = Logger.getLogger(this.getClass());

    private JsonRpcExecutor<Payment> paymentExecutor;


    public PaymentController(JsonRpcExecutor<Payment> paymentExecutor) {
        this.paymentExecutor = paymentExecutor;
    }

    public Payment makePayment(PaymentInput input) throws KnownJsonRpcException {
        LOGGER.info("Create payment");
        this.paymentExecutor.setReadTimeout(300000);
        try {
            return this.paymentExecutor.execute(input.getParams());
        } catch (KnownJsonRpcException e) {
            LOGGER.error("Creating payment failed : " + e.getError().getMessage());
            throw e;
        }
    }

}
