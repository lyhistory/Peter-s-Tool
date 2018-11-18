package io.stormbird.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import org.web3j.abi.datatypes.Bool;
import org.web3j.crypto.Hash;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
    private final MutableLiveData<Boolean> ensFail = new MutableLiveData<>();

    private final ConfirmationRouter confirmationRouter;
    private final FetchGasSettingsInteract fetchGasSettingsInteract;
    private final MyAddressRouter myAddressRouter;
    private final FetchTokensInteract fetchTokensInteract;

    public SendViewModel(ConfirmationRouter confirmationRouter,
                         FetchGasSettingsInteract fetchGasSettingsInteract,
                         MyAddressRouter myAddressRouter,
                         FetchTokensInteract fetchTokensInteract) {
        this.confirmationRouter = confirmationRouter;
        this.fetchGasSettingsInteract = fetchGasSettingsInteract;
        this.myAddressRouter = myAddressRouter;
        this.fetchTokensInteract = fetchTokensInteract;
    }

    public LiveData<Double> ethPriceReading() { return ethPrice; }
    public LiveData<String> ensResolve() { return ensResolve; }
    public LiveData<Boolean> ensFail() { return ensFail; }

    public void openConfirmation(Context context, String to, BigInteger amount, String contractAddress, int decimals, String symbol, boolean sendingTokens) {
        confirmationRouter.open(context, to, amount, contractAddress, decimals, symbol, sendingTokens);
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
        if (name == null || name.length() < 2 || name.charAt(0) != '@') return;
        disposable = checkENSAddressFunc(name.substring(1))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::gotHash, this::onError);
    }

    private void gotHash(byte[] resultHash)
    {
        disposable = fetchTokensInteract.callAddressMethod("owner", resultHash, ENSCONTRACT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::gotAddress, this::onError);
    }

    private Single<byte[]> checkENSAddressFunc(final String name)
    {
        return Single.fromCallable(() -> {
            //split name
            String[] components = name.split("\\.");

            byte[] resultHash = new byte[32];
            Arrays.fill(resultHash, (byte)0);

            for (int i = (components.length - 1); i >= 0; i--)
            {
                String nameComponent = components[i];
                resultHash = hashJoin(resultHash, nameComponent.getBytes());
            }

            return resultHash;
        });
    }

    private void gotAddress(String returnedAddress)
    {
        BigInteger test = Numeric.toBigInt(returnedAddress);
        if (!test.equals(BigInteger.ZERO))
        {
            //post the response back
            ensResolve.postValue(returnedAddress);
        }
        else
        {
            ensFail.postValue(true);
        }
    }

    public static byte[] hashJoin(byte[] lastHash, byte[] input)
    {
        byte[] joined = new byte[lastHash.length*2];

        byte[] inputHash = Hash.sha3(input);
        System.arraycopy(lastHash, 0, joined, 0, lastHash.length);
        System.arraycopy(inputHash, 0, joined, lastHash.length, inputHash.length);
        byte[] resultHash = Hash.sha3(joined);
        return resultHash;
    }
}
