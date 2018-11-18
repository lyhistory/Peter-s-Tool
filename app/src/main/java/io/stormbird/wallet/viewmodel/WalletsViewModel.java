package io.stormbird.wallet.viewmodel;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.stormbird.wallet.C;
import io.stormbird.wallet.entity.ErrorEnvelope;
import io.stormbird.wallet.entity.NetworkInfo;
import io.stormbird.wallet.entity.Token;
import io.stormbird.wallet.entity.Wallet;
import io.stormbird.wallet.entity.WalletUpdate;
import io.stormbird.wallet.interact.CreateWalletInteract;
import io.stormbird.wallet.interact.DeleteWalletInteract;
import io.stormbird.wallet.interact.ExportWalletInteract;
import io.stormbird.wallet.interact.FetchTokensInteract;
import io.stormbird.wallet.interact.FetchWalletsInteract;
import io.stormbird.wallet.interact.FindDefaultNetworkInteract;
import io.stormbird.wallet.interact.FindDefaultWalletInteract;
import io.stormbird.wallet.interact.SetDefaultWalletInteract;
import io.stormbird.wallet.router.HomeRouter;
import io.stormbird.wallet.router.ImportWalletRouter;

import static io.stormbird.wallet.C.IMPORT_REQUEST_CODE;

public class WalletsViewModel extends BaseViewModel
{
	private final static String TAG = "WVM";

	private final CreateWalletInteract createWalletInteract;
	private final SetDefaultWalletInteract setDefaultWalletInteract;
	private final DeleteWalletInteract deleteWalletInteract;
	private final FetchWalletsInteract fetchWalletsInteract;
	private final FindDefaultWalletInteract findDefaultWalletInteract;
	private final ExportWalletInteract exportWalletInteract;
	private final FetchTokensInteract fetchTokensInteract;
	private final FindDefaultNetworkInteract findDefaultNetworkInteract;

	private final ImportWalletRouter importWalletRouter;
	private final HomeRouter homeRouter;

	private final MutableLiveData<Wallet[]> wallets = new MutableLiveData<>();
	private final MutableLiveData<Wallet> defaultWallet = new MutableLiveData<>();
	private final MutableLiveData<Wallet> createdWallet = new MutableLiveData<>();
	private final MutableLiveData<ErrorEnvelope> createWalletError = new MutableLiveData<>();
	private final MutableLiveData<String> exportedStore = new MutableLiveData<>();
	private final MutableLiveData<ErrorEnvelope> exportWalletError = new MutableLiveData<>();
	private final MutableLiveData<ErrorEnvelope> deleteWalletError = new MutableLiveData<>();
	private final MutableLiveData<Map<String, Wallet>> updateBalance = new MutableLiveData<>();
	private final MutableLiveData<Map<String, String>> namedWallets = new MutableLiveData<>();
	private final MutableLiveData<Long> lastENSScanBlock = new MutableLiveData<>();

	private NetworkInfo currentNetwork;
	private Map<String, Wallet> walletBalances = new HashMap<>();

	WalletsViewModel(
			CreateWalletInteract createWalletInteract,
			SetDefaultWalletInteract setDefaultWalletInteract,
			DeleteWalletInteract deleteWalletInteract,
			FetchWalletsInteract fetchWalletsInteract,
			FindDefaultWalletInteract findDefaultWalletInteract,
			ExportWalletInteract exportWalletInteract,
			ImportWalletRouter importWalletRouter,
			HomeRouter homeRouter,
			FetchTokensInteract fetchTokensInteract,
			FindDefaultNetworkInteract findDefaultNetworkInteract)
	{
		this.createWalletInteract = createWalletInteract;
		this.setDefaultWalletInteract = setDefaultWalletInteract;
		this.deleteWalletInteract = deleteWalletInteract;
		this.fetchWalletsInteract = fetchWalletsInteract;
		this.findDefaultWalletInteract = findDefaultWalletInteract;
		this.importWalletRouter = importWalletRouter;
		this.exportWalletInteract = exportWalletInteract;
		this.homeRouter = homeRouter;
		this.fetchTokensInteract = fetchTokensInteract;
		this.findDefaultNetworkInteract = findDefaultNetworkInteract;

		fetchWallets();
	}

	public LiveData<Wallet[]> wallets()
	{
		return wallets;
	}

	public LiveData<Map<String, String>> namedWallets()
	{
		return namedWallets;
	}

	public LiveData<Wallet> defaultWallet()
	{
		return defaultWallet;
	}

	public LiveData<Wallet> createdWallet()
	{
		return createdWallet;
	}

	public LiveData<ErrorEnvelope> createWalletError()
	{
		return createWalletError;
	}

	public LiveData<String> exportedStore()
	{
		return exportedStore;
	}

	public LiveData<ErrorEnvelope> exportWalletError()
	{
		return exportWalletError;
	}

	public LiveData<ErrorEnvelope> deleteWalletError()
	{
		return deleteWalletError;
	}

	public LiveData<Map<String, Wallet>> updateBalance()
	{
		return updateBalance;
	}

	public LiveData<Long> lastENSScanBlock() { return lastENSScanBlock; }

	public void setDefaultWallet(Wallet wallet)
	{
		disposable = setDefaultWalletInteract
				.set(wallet)
				.subscribe(() -> onDefaultWalletChanged(wallet), this::onError);
	}

	public void deleteWallet(Wallet wallet)
	{
		disposable = deleteWalletInteract
				.delete(wallet)
				.subscribe(this::onFetchWallets, this::onDeleteWalletError);
	}

	private void findNetwork()
	{
		progress.postValue(true);
		disposable = findDefaultNetworkInteract
				.find()
				.subscribe(this::onDefaultNetwork, this::onError);
	}

	private void onDefaultNetwork(NetworkInfo networkInfo)
	{
		if (currentNetwork == null || networkInfo.chainId != currentNetwork.chainId)
		{
			walletBalances.clear();
			currentNetwork = networkInfo;
		}
		if (walletBalances.size() == 0)
		{
			walletBalances = fetchWalletsInteract.getWalletMap(networkInfo);
		}
		//now load the current wallets
		disposable = fetchWalletsInteract
				.fetch(walletBalances)
				.subscribe(this::onFetchWallets, this::onError);
	}

	private void onFetchWallets(Wallet[] items)
	{
		progress.postValue(false);
		wallets.postValue(items);
		disposable = findDefaultWalletInteract
				.find()
				.subscribe(this::onDefaultWalletChanged, t -> {
				});

		getWalletsBalance(items);
	}

	private void onDefaultWalletChanged(Wallet wallet)
	{
		progress.postValue(false);
		defaultWallet.postValue(wallet);
	}

	public void swipeRefreshWallets(long block)
	{
		if (walletBalances.size() == 0)
		{
			walletBalances = fetchWalletsInteract.getWalletMap(currentNetwork);
		}
		//check for updates
		//check names first
		disposable = fetchWalletsInteract.fetch(walletBalances)
				.flatMap(wallets -> fetchWalletsInteract.scanForNames(wallets, block))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::updateNames, this::onError);
	}

	public void fetchWallets()
	{
		progress.postValue(true);
		findNetwork();
	}

	public void newWallet()
	{
		progress.setValue(true);
		createWalletInteract
				.create()
				.subscribe(account -> {
					fetchWallets();
					createdWallet.postValue(account);
				}, this::onCreateWalletError);
	}

	public void exportWallet(Wallet wallet, String storePassword)
	{
		exportWalletInteract
				.export(wallet, storePassword)
				.subscribe(exportedStore::postValue, this::onExportWalletError);
	}

	/**
	 * Sequentially updates the wallet balances on the current network
	 *
	 * @param wallets
	 */
	private void getWalletsBalance(Wallet[] wallets)
	{
		disposable = fetchWalletList(wallets)
				.flatMapIterable(wallet -> wallet) //iterate through each wallet
				.map(this::addWalletToMap)
				.flatMap(wallet -> fetchTokensInteract.fetchEth(currentNetwork, wallet)) //fetch wallet balance
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::updateList, this::onError, this::updateBalances);
	}

	private Wallet addWalletToMap(Wallet wallet)
	{
		if (!walletBalances.containsKey(wallet.address)) walletBalances.put(wallet.address, wallet);
		return wallet;
	}

	private void updateBalances()
	{
		updateBalance.postValue(walletBalances);

		//store
		if (currentNetwork.isMainNetwork)
		{
			Wallet[] walletsFromUpdate = walletBalances.values().toArray(new Wallet[0]);
			storeWallets(walletsFromUpdate);
		}
	}

	private void updateNames(WalletUpdate update)
	{
		//update names for wallets
		//got names?
		for (Wallet w : update.wallets.values())
		{
			if (walletBalances.containsKey(w.address))
			{
				walletBalances.get(w.address).ENSname = w.ENSname;
			}
		}

		Wallet[] walletsFromFetch = walletBalances.values().toArray(new Wallet[0]);

		if (update.wallets.size() > 0)
		{
			wallets.postValue(walletsFromFetch);
		}

		lastENSScanBlock.postValue(update.lastBlock);

		progress.postValue(false);

		storeWallets(walletsFromFetch);
	}

	private void storeWallets(Wallet[] wallets)
	{
		//write wallets to DB
		disposable = fetchWalletsInteract.storeWallets(wallets, currentNetwork.isMainNetwork)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::onStored, this::onError);
	}

	private void onStored(Integer count)
	{
		Log.d(TAG, "Stored " + count + " Wallets");
	}

	private void updateList(Token token)
	{
		if (walletBalances.containsKey(token.getAddress()))
		{
			walletBalances.get(token.getAddress()).setWalletBalance(token.balance);
		}

		updateBalance.postValue(walletBalances);
	}

	private Observable<List<Wallet>> fetchWalletList(Wallet[] wallets)
	{
		return Observable.fromCallable(() -> new ArrayList<>(Arrays.asList(wallets)));
	}

	private void onExportWalletError(Throwable throwable)
	{
		//Crashlytics.logException(throwable);
		exportWalletError.postValue(
				new ErrorEnvelope(C.ErrorCode.UNKNOWN, TextUtils.isEmpty(throwable.getLocalizedMessage())
						? throwable.getMessage() : throwable.getLocalizedMessage()));
	}

	private void onDeleteWalletError(Throwable throwable)
	{
		//Crashlytics.logException(throwable);
		deleteWalletError.postValue(
				new ErrorEnvelope(C.ErrorCode.UNKNOWN, TextUtils.isEmpty(throwable.getLocalizedMessage())
						? throwable.getMessage() : throwable.getLocalizedMessage()));
	}

	private void onCreateWalletError(Throwable throwable)
	{
		//Crashlytics.logException(throwable);
		progress.postValue(false);
		createWalletError.postValue(new ErrorEnvelope(C.ErrorCode.UNKNOWN, throwable.getMessage()));
	}

	public void importWallet(Activity activity)
	{
		importWalletRouter.openForResult(activity, IMPORT_REQUEST_CODE);
	}

	public void showHome(Context context)
	{
		homeRouter.open(context, true);
	}
}
