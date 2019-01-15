package io.stormbird.wallet.repository;

import android.text.format.DateUtils;

import io.stormbird.wallet.entity.NetworkInfo;
import io.stormbird.wallet.entity.Transaction;
import io.stormbird.wallet.entity.Wallet;

import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

public class TransactionMemoryCache implements TransactionLocalSource {

	private static final long MAX_TIME_OUT = DateUtils.MINUTE_IN_MILLIS;
	private final Map<String, CacheUnit> cache = new java.util.concurrent.ConcurrentHashMap<>();

	@Override
	public Single<Transaction[]> fetchTransaction(NetworkInfo networkInfo, Wallet wallet) {
		return Single.fromCallable(() -> {
			CacheUnit unit = cache.get(createKey(networkInfo, wallet));
			Transaction[] transactions = null;
			if (unit != null) {
				if (System.currentTimeMillis() - unit.create > MAX_TIME_OUT) {
					cache.remove(createKey(networkInfo, wallet));
				} else {
					transactions = unit.transactions;
				}

			}
			return transactions;
		});
	}

	@Override
	public Transaction fetchTransaction(NetworkInfo networkInfo, Wallet wallet, String hash)
	{
		return null;
	}

	private String createKey(NetworkInfo networkInfo, Wallet wallet) {
        return networkInfo.name + wallet.address;
    }

    @Override
	public Completable putTransactions(NetworkInfo networkInfo, Wallet wallet, Transaction[] transactions) {
		return Completable.fromAction(() -> cache.put(createKey(networkInfo, wallet),
                new CacheUnit(wallet.address, System.currentTimeMillis(), transactions)));
	}

    @Override
    public Single<Transaction> findLast(NetworkInfo networkInfo, Wallet wallet) {
	    return Single.fromCallable(() -> {
            CacheUnit cacheUnit = cache.get(createKey(networkInfo, wallet));
            return cacheUnit != null && cacheUnit.transactions != null && cacheUnit.transactions.length > 0
                    ? cacheUnit.transactions[0]
                    : null;
        });
    }

	@Override
	public Single<Transaction[]> putAndReturnTransactions(NetworkInfo networkInfo, Wallet wallet, Transaction[] txList)
	{
		return Single.fromCallable(() -> {
								   	cache.put(createKey(networkInfo, wallet),
												 new CacheUnit(wallet.address, System.currentTimeMillis(), txList));
									   return txList;
								   });
	}

	private static class CacheUnit {
		final String accountAddress;
		final long create;
		final Transaction[] transactions;

		private CacheUnit(String accountAddress, long create, Transaction[] transactions) {
			this.accountAddress = accountAddress;
			this.create = create;
			this.transactions = transactions;
		}
	}
}
