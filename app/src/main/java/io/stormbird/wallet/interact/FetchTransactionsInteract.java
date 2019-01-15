package io.stormbird.wallet.interact;

import io.reactivex.ObservableSource;
import io.stormbird.wallet.entity.*;
import io.stormbird.wallet.repository.TransactionRepositoryType;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.stormbird.wallet.service.TokensService;

public class FetchTransactionsInteract {

    private final TransactionRepositoryType transactionRepository;

    public FetchTransactionsInteract(TransactionRepositoryType transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Observable<Transaction[]> fetchCached(NetworkInfo network, Wallet wallet) {
        return transactionRepository
                .fetchCachedTransactions(network, wallet)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Transaction[]> fetchNetworkTransactions(Wallet wallet, long lastBlock, String userAddress) {
        return transactionRepository
                .fetchNetworkTransaction(wallet, lastBlock, userAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Transaction[]> storeTransactions(NetworkInfo networkInfo, Wallet wallet, Transaction[] txList)
    {
        return transactionRepository.storeTransactions(networkInfo, wallet, txList);
    }

    public Single<ContractType> queryInterfaceSpec(TokenInfo tokenInfo)
    {
        return transactionRepository.queryInterfaceSpec(tokenInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public Transaction fetchCached(String walletAddress, String hash)
    {
        return transactionRepository.fetchCachedTransaction(walletAddress, hash);
    }

    public Observable<TokenInfo> queryInterfaceSpecForService(TokenInfo tokenInfo)
    {
        return queryInterfaceSpec(tokenInfo).toObservable()
                .map(spec -> addSpecToService(tokenInfo, spec));
    }

    private TokenInfo addSpecToService(TokenInfo info, ContractType contractType)
    {
        TokensService.setInterfaceSpec(info.address, contractType);
        return info;
    }
}
