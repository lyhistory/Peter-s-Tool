package io.stormbird.wallet.service;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.stormbird.wallet.entity.*;

public interface TransactionsNetworkClientType {
    Observable<Transaction[]> fetchLastTransactions(NetworkInfo networkInfo, Wallet wallet, long lastBlock, String userAddress);
    Single<WalletUpdate> scanENSTransactionsForWalletNames(Wallet[] wallets, long lastBlock);

    Single<ContractType> checkConstructorArgs(NetworkInfo networkInfo, String address);
}
