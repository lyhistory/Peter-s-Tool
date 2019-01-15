package io.stormbird.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import io.stormbird.token.tools.TokenDefinition;
import io.stormbird.wallet.interact.ENSInteract;
import io.stormbird.wallet.service.AssetDefinitionService;
import org.web3j.abi.datatypes.Bool;
import org.web3j.crypto.Hash;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.stormbird.token.tools.Numeric;
import io.stormbird.wallet.entity.Ticker;
import io.stormbird.wallet.entity.Transaction;
import io.stormbird.wallet.entity.Wallet;
import io.stormbird.wallet.interact.FetchGasSettingsInteract;
import io.stormbird.wallet.interact.FetchTokensInteract;
import io.stormbird.wallet.repository.TokenRepository;
import io.stormbird.wallet.router.ConfirmationRouter;
import io.stormbird.wallet.router.MyAddressRouter;

import static io.stormbird.wallet.C.ENSCONTRACT;

public class SendViewModel extends BaseViewModel {
    private static final long CHECK_ETHPRICE_INTERVAL = 10;
    private final MutableLiveData<Wallet> defaultWallet = new MutableLiveData<>();
    private final MutableLiveData<Transaction> transaction = new MutableLiveData<>();
    private final MutableLiveData<Double> ethPrice = new MutableLiveData<>();
    private final MutableLiveData<String> ensResolve = new MutableLiveData<>();
    private final MutableLiveData<String> ensFail = new MutableLiveData<>();

    private final ConfirmationRouter confirmationRouter;
    private final FetchGasSettingsInteract fetchGasSettingsInteract;
    private final MyAddressRouter myAddressRouter;
    private final FetchTokensInteract fetchTokensInteract;
    private final ENSInteract ensInteract;
    private final AssetDefinitionService assetDefinitionService;

    public SendViewModel(ConfirmationRouter confirmationRouter,
                         FetchGasSettingsInteract fetchGasSettingsInteract,
                         MyAddressRouter myAddressRouter,
                         FetchTokensInteract fetchTokensInteract,
                         ENSInteract ensInteract,
                         AssetDefinitionService assetDefinitionService) {
        this.confirmationRouter = confirmationRouter;
        this.fetchGasSettingsInteract = fetchGasSettingsInteract;
        this.myAddressRouter = myAddressRouter;
        this.fetchTokensInteract = fetchTokensInteract;
        this.ensInteract = ensInteract;
        this.assetDefinitionService = assetDefinitionService;
    }

    public LiveData<Double> ethPriceReading() { return ethPrice; }
    public LiveData<String> ensResolve() { return ensResolve; }
    public LiveData<String> ensFail() { return ensFail; }

    public void openConfirmation(Context context, String to, BigInteger amount, String contractAddress, int decimals, String symbol, boolean sendingTokens, String ensDetails) {
        confirmationRouter.open(context, to, amount, contractAddress, decimals, symbol, sendingTokens, ensDetails);
    }

    public void showMyAddress(Context context, Wallet wallet) {
        myAddressRouter.open(context, wallet);
    }

    public void showContractInfo(Context ctx, String contractAddress)
    {
        myAddressRouter.open(ctx, contractAddress);
    }

    public void startEthereumTicker()
    {
        disposable = Observable.interval(0, CHECK_ETHPRICE_INTERVAL, TimeUnit.SECONDS)
                .doOnNext(l -> fetchTokensInteract
                        .getEthereumTicker()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onTicker, this::onError)).subscribe();
    }

    private void onTicker(Ticker ticker)
    {
        if (ticker != null && ticker.price_usd != null)
        {
            ethPrice.postValue(Double.valueOf(ticker.price_usd));
        }
    }

    public void checkENSAddress(String name)
    {
        if (name == null || name.length() < 1) return;
        disposable = ensInteract.checkENSAddress (name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ensResolve::postValue, throwable -> ensFail.postValue(""));
    }

    public boolean hasIFrame(String address)
    {
        return assetDefinitionService.hasIFrame(address);
    }

    public String getTokenData(String address)
    {
        return assetDefinitionService.getIntroductionCode(address);
    }
}
