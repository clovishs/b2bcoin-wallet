package com.b2beyond.wallet.b2bcoin.view.controller;

import com.b2beyond.wallet.b2bcoin.controler.CoinRpcController;
import com.b2beyond.wallet.b2bcoin.controler.DaemonController;
import com.b2beyond.wallet.b2bcoin.controler.WalletRpcController;
import com.b2beyond.wallet.b2bcoin.daemon.WalletDaemon;
import com.b2beyond.wallet.b2bcoin.rpc.TransactionItemsRpcPoller;
import com.b2beyond.wallet.b2bcoin.rpc.UnconfirmedTransactionHashesRpcPoller;
import com.b2beyond.wallet.rpc.JsonRpcExecutor;
import com.b2beyond.wallet.rpc.NoParamsRpcPoller;
import com.b2beyond.wallet.rpc.RpcPoller;
import com.b2beyond.wallet.rpc.model.*;
import com.b2beyond.wallet.rpc.model.coin.BlockWrapper;
import com.b2beyond.wallet.rpc.exception.KnownJsonRpcException;
import com.b2beyond.wallet.b2bcoin.util.B2BUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;


public class ActionController {

    private Logger LOGGER = Logger.getLogger(this.getClass());

    private DaemonController controller;
    private CoinRpcController coinRpcController;
    private WalletRpcController walletRpcController;
    private PoolMiningController miningController;
    private SoloMiningController soloMiningController;

    private List<RpcPoller> walletRpcPollers;


    public ActionController(final DaemonController controller, WalletRpcController walletRpcController, CoinRpcController coinRpcController) {
        this.controller = controller;
        this.coinRpcController = coinRpcController;
        this.walletRpcController = walletRpcController;
    }

    public void setMiningController(PoolMiningController miningController) {
        this.miningController = miningController;
    }

    public void setSoloMiningController(SoloMiningController soloMiningController) {
        this.soloMiningController = soloMiningController;
    }

    public void stopBackgroundProcessesBeforeReset() {
        coinRpcController.stop();
        walletRpcController.stop();
    }

    public void startBackgroundProcessesAfterReset() {
        coinRpcController.restart();
        walletRpcController.restart();
    }

    public Address createAddress() {
        try {
            return walletRpcController.getCreateAddressExecutor().execute(JsonRpcExecutor.EMPTY_PARAMS);
        } catch (KnownJsonRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Address importAddress(AddressInput input) {
        try {
            return walletRpcController.getCreateAddressExecutor().execute(input.getParams());
        } catch (KnownJsonRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BlockWrapper getBlockWrapper(String hash) {
        try {
            return coinRpcController.getBlockWrapperExecutor().execute("\"params\": {\"hash\": \"" + hash + "\"}");
        } catch (KnownJsonRpcException e) {
            restartCoinDaemon();
        }
        return null;
    }

    public void exit() {
        LOGGER.info("ActionController.exit was called");
        // Save the wallet
        try {
            walletRpcController.getSaveExecutor().execute(JsonRpcExecutor.EMPTY_PARAMS);
        } catch (KnownJsonRpcException e) {
            e.printStackTrace();
        }
        LOGGER.info("ActionController.exit was called");
        miningController.stopMining();
        soloMiningController.stopMining();
        coinRpcController.stop();
        walletRpcController.stop();
        controller.stop();
    }

    public void restartCoinDaemon() {
        controller.restartDaemon();
    }

//    public void startCoinDaemon() {
//        controller.startDaemon();
//    }

    public void stopCoinDaemon() {
        controller.stopDaemon();
    }

    public CoinRpcController getCoinRpcController() {
        return coinRpcController;
    }

    public WalletRpcController getWalletRpcController() {
        return walletRpcController;
    }

    public void startWallet(List<RpcPoller> walletRpcPollers) {
        controller.startWallet();

        while (!controller.isWalletStarted()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Add pollers if wallet rpc port is available
        for (RpcPoller poller : walletRpcPollers) {
            getCoinRpcController().addPollers(poller);
        }
    }

    public void resetWallet() {
        try {
            //controller.stopDaemon();
            walletRpcController.getResetExecutor().execute(JsonRpcExecutor.EMPTY_PARAMS);

            for (RpcPoller poller : walletRpcPollers) {
                if (poller instanceof TransactionItemsRpcPoller) {
                    ((TransactionItemsRpcPoller) poller).reset();
                }
            }

            //controller.startDaemon();
        } catch (KnownJsonRpcException e) {
            e.printStackTrace();
        }
    }

    public void resetBlockChain() {
        LOGGER.info("Delete block chain");
        LOGGER.debug("Command : " + B2BUtil.getDeleteBlockChainHomeCommand());

        stopCoinDaemon();

//        ProcessBuilder pb = new ProcessBuilder(B2BUtil.getDeleteBlockChainHomeCommand());

        Process process;
        try {
//            process = pb.start();
            process = Runtime.getRuntime().exec(B2BUtil.getDeleteBlockChainHomeCommand());
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            LOGGER.info("Windows sleep : 5 seconds");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopCoinDaemon();
    }

    public SpendKeys getSpendKeys(String address) {
        try {
            return walletRpcController.getSpendKeysExecutor().execute("\"params\": {\"address\": \"" + address + "\"}");
        } catch (KnownJsonRpcException e) {
            e.printStackTrace();
        }


        return null;
    }

    public ViewSecretKey getPublicKey(String address) {
        try {
            return walletRpcController.getViewSecretKeyExecutor().execute("\"params\": {\"address\": \"" + address + "\"}");
        } catch (KnownJsonRpcException e) {
            e.printStackTrace();
        }


        return null;
    }

    public AddressBalance getBalance(String address) {
        LOGGER.info("Get address balance : " + address);

        try {
            return this.walletRpcController.getBalanceExecutor().execute("\"params\": {" +
                    "\"address\": \"" + address + "\"" +
                    "}");
        } catch (KnownJsonRpcException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Success deleteAddress(String address) {
        try {
            return this.walletRpcController.getDeleteAddressExecutor().execute("\"params\": {" +
                "\"address\": \"" + address + "\"" +
            "}");
        } catch (KnownJsonRpcException e) {
            e.printStackTrace();
            return null;
        }
    }

}
