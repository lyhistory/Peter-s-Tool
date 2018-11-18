package io.stormbird.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.stormbird.wallet.entity.FileData;
import io.stormbird.wallet.entity.NetworkInfo;
import io.stormbird.wallet.entity.Token;
import io.stormbird.wallet.entity.TokenInfo;
import io.stormbird.wallet.entity.Wallet;
import io.stormbird.wallet.interact.AddTokenInteract;
import io.stormbird.wallet.interact.CreateWalletInteract;
import io.stormbird.wallet.interact.FetchWalletsInteract;
import io.stormbird.wallet.interact.ImportWalletInteract;
import io.stormbird.wallet.repository.EthereumNetworkRepositoryType;
import io.stormbird.wallet.repository.LocaleRepositoryType;
import io.stormbird.wallet.repository.PreferenceRepositoryType;

import static io.stormbird.wallet.C.DEFAULT_NETWORK;
import static io.stormbird.wallet.C.DOWNLOAD_READY;
import static io.stormbird.wallet.C.HARD_CODED_KEY;
import static io.stormbird.wallet.C.OVERRIDE_DEFAULT_NETWORK;
import static io.stormbird.wallet.viewmodel.HomeViewModel.ALPHAWALLET_FILE_URL;

public class SplashViewModel extends ViewModel {
    private final FetchWalletsInteract fetchWalletsInteract;
    private final EthereumNetworkRepositoryType networkRepository;
    private final ImportWalletInteract importWalletInteract;
    private final AddTokenInteract addTokenInteract;
    private final CreateWalletInteract createWalletInteract;
    private final PreferenceRepositoryType preferenceRepository;
    private final LocaleRepositoryType localeRepository;

    private MutableLiveData<Wallet[]> wallets = new MutableLiveData<>();
    private MutableLiveData<Wallet> createWallet = new MutableLiveData<>();

    SplashViewModel(FetchWalletsInteract fetchWalletsInteract,
                    EthereumNetworkRepositoryType networkRepository,
                    ImportWalletInteract importWalletInteract,
                    AddTokenInteract addTokenInteract,
                    CreateWalletInteract createWalletInteract,
                    PreferenceRepositoryType preferenceRepository,
                    LocaleRepositoryType localeRepository) {
        this.fetchWalletsInteract = fetchWalletsInteract;
        this.networkRepository = networkRepository;
        this.importWalletInteract = importWalletInteract;
        this.addTokenInteract = addTokenInteract;
        this.createWalletInteract = createWalletInteract;
        this.preferenceRepository = preferenceRepository;
        this.localeRepository = localeRepository;
    }

    public void setLocale(Context context) {
        localeRepository.setDefaultLocale(context, preferenceRepository.getDefaultLocale());
    }

    /**
     * This is slightly confusing because we have to guard against race condition so here is code execution order:
     * 1. check if there is a default network override
     * 2. check if there's a hard coded key - if there is then go to 5, otherwise drop to 3.
     * 3. check if there's a hard coded contract - if so jump to 6 otherwise drop to 4.
     * 4. fetch wallets then jump to FINISH
     * 5. - load hard coded key then jump back to 3. (check hard coded contract).
     * 6. - load hard coded contract then jump back to 4. (load wallets)
     * FINISH - push wallet message so SplashAcitivity now continues with execution of ::onWallets
     */
    public void startOverridesChain() {
        if (OVERRIDE_DEFAULT_NETWORK && !preferenceRepository.getDefaultNetworkSet()) {
            NetworkInfo[] networks = networkRepository.getAvailableNetworkList();
            for (NetworkInfo networkInfo : networks) {
                if (networkInfo.name.equals(DEFAULT_NETWORK)) {
                    networkRepository.setDefaultNetworkInfo(networkInfo);
                    preferenceRepository.setDefaultNetworkSet();
                    break;
                }
            }
        }
        fetchWallets();
    }

    //4. fetch wallets
    private void fetchWallets()
    {
        fetchWalletsInteract
                .fetch(null)
                .subscribe(wallets::postValue, this::onError);
    }

    //5. add hardcoded key then always perform check hard coded contrats
    private void addHardKey(String key) {
        importWalletInteract
                .importPrivateKey(key)
                .subscribe(this::keyAdded, this::onKeyError);
    }

    private void keyAdded(Wallet wallet)
    {
        //success
        System.out.println("Imported wallet at addr: " + wallet.address);

        //continue chain
        fetchWallets();
    }

    private void fetchWallets(Token token)
    {
        fetchWallets();
    }

    //on wallet error ensure execution still continues and splash screen terminates
    private void onError(Throwable throwable) {
        wallets.postValue(new Wallet[0]);
    }

    //on key error ensure contract check continues
    private void onKeyError(Throwable throwable) {
        fetchWallets();
    }

    //on contract error ensure we still call wallet fetch
    private void onContractError(Throwable throwable) {
        fetchWallets();
    }

    public LiveData<Wallet[]> wallets() {
        return wallets;
    }
    public LiveData<Wallet> createWallet() {
        return createWallet;
    }

    private TokenInfo getTokenInfo(String address, String symbol, int decimals, String name, boolean isStormBird)
    {
        TokenInfo tokenInfo = new TokenInfo(address, name, symbol, decimals, true, isStormBird);
        return tokenInfo;
    }

    public void createNewWallet()
    {
        //create a new wallet for the user
        createWalletInteract
                .create()
                .subscribe(account -> {
                    fetchWallets();
                    createWallet.postValue(account);
                }, this::onError);
    }

    public void checkVersionUpdate(Context ctx, long updateTime)
    {
        if (!isPlayStoreInstalled(ctx))
        {
            //check the current install version string against the current version on the alphawallet page
            //current version number as string
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
            int asks = pref.getInt("update_asks", 0);
            if (updateTime == 0 || asks == 2) // if user cancels update twice stop asking them until the next release
            {
                pref.edit().putInt("update_asks", 0).apply();
                pref.edit().putLong("install_time", System.currentTimeMillis()).apply();
            }
            else
            {
                checkWebsiteAPKFileData(updateTime, ctx);
            }
        }
    }

    private boolean isPlayStoreInstalled(Context ctx)
    {
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));

        // The package name of the app that has installed your app
        final String installer = ctx.getPackageManager().getInstallerPackageName(ctx.getPackageName());

        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
    }

    private String stripFilename(String name)
    {
        int index = name.lastIndexOf(".apk");
        if (index > 0)
        {
            name = name.substring(0, index);
        }
        index = name.lastIndexOf("-");
        if (index > 0)
        {
            name = name.substring(index+1);
        }
        return name;
    }

    private void checkWebsiteAPKFileData(long currentInstallDate, final Context baseContext)
    {
        Disposable d = getFileDataFromURL(ALPHAWALLET_FILE_URL).toObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> onUpdate(result, currentInstallDate, baseContext), this::onError);
    }

    private Single<FileData> getFileDataFromURL(final String location)
    {
        return Single.fromCallable(() -> {
            HttpURLConnection connection = null;
            String stepLocation = location;
            FileData fileData = new FileData();
            for (;;) //crawl through the URL linkage until we get the base filename
            {
                URL url = new URL(stepLocation);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(2000);
                connection.setInstanceFollowRedirects(false);
                String redirectLocation = connection.getHeaderField("Location");
                if (redirectLocation == null)
                {
                    fileData.fileDate = connection.getLastModified();
                    fileData.fileName = stepLocation.substring(stepLocation.lastIndexOf('/') + 1, stepLocation.length());
                    break;
                }
                stepLocation = redirectLocation;
                connection.disconnect();
            }
            connection.disconnect();
            return fileData;
        });
    }

    private void onUpdate(FileData data, long currentInstallDate, Context baseContext)
    {
        //if needs update can we spring open a dialogue box from here?
        if (data.fileDate > currentInstallDate)
        {
            String newVersion = stripFilename(data.fileName);
            Intent intent = new Intent(DOWNLOAD_READY);
            intent.putExtra("Version", newVersion);
            baseContext.sendBroadcast(intent);
        }
    }
}
