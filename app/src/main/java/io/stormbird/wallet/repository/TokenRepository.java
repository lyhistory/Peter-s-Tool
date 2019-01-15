package io.stormbird.wallet.repository;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;
import io.reactivex.*;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.stormbird.token.entity.BadContract;
import io.stormbird.token.entity.FunctionDefinition;
import io.stormbird.token.tools.TokenDefinition;
import io.stormbird.wallet.entity.*;
import io.stormbird.wallet.service.AssetDefinitionService;
import io.stormbird.wallet.service.TickerService;
import io.stormbird.wallet.service.TokenExplorerClientType;
import okhttp3.OkHttpClient;
import org.web3j.abi.*;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSyncing;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;
import rx.functions.Func1;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static io.stormbird.wallet.C.BURN_ADDRESS;
import static org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction;

public class TokenRepository implements TokenRepositoryType {

    private static final String TAG = "TRT";
    private static final long BALANCE_UPDATE_INTERVAL = DateUtils.MINUTE_IN_MILLIS;
    private final TokenExplorerClientType tokenNetworkService;
    private final WalletRepositoryType walletRepository;
    private final TokenLocalSource localSource;
    private final EthereumNetworkRepositoryType ethereumNetworkRepository;
    private final TransactionLocalSource transactionsLocalCache;
    private final AssetDefinitionService assetDefinitionService;
    private final TickerService tickerService;
    private Web3j web3j;
    private boolean useBackupNode = false;
    private NetworkInfo network;
    private Disposable disposable;

    public TokenRepository(
            EthereumNetworkRepositoryType ethereumNetworkRepository,
            WalletRepositoryType walletRepository,
            TokenExplorerClientType tokenNetworkService,
            TokenLocalSource localSource,
            TransactionLocalSource transactionsLocalCache,
            TickerService tickerService,
            AssetDefinitionService assetDefinitionService) {
        this.ethereumNetworkRepository = ethereumNetworkRepository;
        this.walletRepository = walletRepository;
        this.tokenNetworkService = tokenNetworkService;
        this.localSource = localSource;
        this.transactionsLocalCache = transactionsLocalCache;
        this.tickerService = tickerService;
        this.ethereumNetworkRepository.addOnChangeDefaultNetwork(this::buildWeb3jClient);
        this.assetDefinitionService = assetDefinitionService;
        buildWeb3jClient(ethereumNetworkRepository.getDefaultNetwork());
    }

    private void buildWeb3jClient(NetworkInfo defaultNetwork)
    {
        network = defaultNetwork;
        //Adjust timeout params for node connection - it should timeout quickly and not keep retrying,
        //otherwise it can hold up resources
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();

        HttpService publicNodeService = new HttpService(defaultNetwork.rpcServerUrl, client, false);

        web3j = Web3jFactory.build(publicNodeService);

        //test main node, if it's not working then use backup Infura node. If it's not working then we can't listen on the pool
        disposable = getIsSyncing()
                .subscribeOn(Schedulers.io())
                .subscribe(this::receiveSyncing, this::checkFail);
    }

    private void receiveSyncing(Boolean b)
    {
        //have a valid connection and node is done syncing, no need to use infura
        if (b == null || b)
        {
            useBackupNode = true;
            switchToBackupNode();
        }

        disposable.dispose();
    }

    private void checkFail(Throwable failMsg)
    {
        useBackupNode = true;
        switchToBackupNode();
        disposable.dispose();
    }

    private void switchToBackupNode()
    {
        org.web3j.protocol.http.HttpService publicNodeService = new org.web3j.protocol.http.HttpService(network.backupNodeUrl);
        web3j = Web3jFactory.build(publicNodeService);
    }

    private Single<Boolean> getIsSyncing()
    {
        return Single.fromCallable(() -> {
            EthSyncing status = web3j.ethSyncing()
                    .send();
            return status.isSyncing();
        });
    }

    @Override
    public Observable<Token[]> fetchActiveStoredPlusEth(NetworkInfo network, String walletAddress) {
        Wallet wallet = new Wallet(walletAddress);
        return fetchStoredEnabledTokens(network, wallet) // fetch tokens from cache
                .compose(attachEthereumStored(network, wallet)) //add cached eth balance
                .compose(attachERC721Tokens(network, wallet))
                .toObservable();
    }

    @Override
    public Single<BigInteger> fetchLatestBlockNumber()
    {
        return Single.fromCallable(() -> {
            EthBlockNumber blk = web3j.ethBlockNumber()
                    .send();
            return blk.getBlockNumber();
        });
    }


    private Single<Token[]> fetchERC721Tokens(NetworkInfo network, Wallet wallet)
    {
        return localSource.fetchERC721Tokens(network, wallet);
    }

    @Override
    public Observable<Token[]> fetchActiveStored(String walletAddress) {
        NetworkInfo network = ethereumNetworkRepository.getDefaultNetwork();
        Wallet wallet = new Wallet(walletAddress);
        return fetchStoredEnabledTokens(network, wallet) // fetch tokens from cache
                .toObservable();
    }

    private SingleTransformer<Token[], Token[]> attachERC721Tokens(NetworkInfo network, Wallet wallet)
    {
        return upstream -> Single.zip(
                upstream, fetchERC721Tokens(network, wallet),
                (tokens, ERC721Tokens) ->
                {
                    List<Token> result = new ArrayList<>();
                    result.addAll(Arrays.asList(ERC721Tokens));
                    result.addAll(Arrays.asList(tokens));
                    return result.toArray(new Token[0]);
                });
    }

    private SingleTransformer<Token[], Token[]> attachEthereumStored(NetworkInfo network, Wallet wallet)
    {
        return upstream -> Single.zip(
                upstream, attachCachedEth(network, wallet),
                (tokens, ethToken) ->
                {
                    List<Token> result = new ArrayList<>();
                    result.add(ethToken);
                    result.addAll(Arrays.asList(tokens));
                    return result.toArray(new Token[0]);
                });
    }

    private Single<Token> attachCachedEth(NetworkInfo network, Wallet wallet)
    {
        //get stored eth balance
        return Single.fromCallable(() -> {
            Token eth = localSource.getTokenBalance(network, wallet, wallet.address);
            if (eth == null)
            {
                TokenInfo info = new TokenInfo(wallet.address, network.name, network.symbol, 18, true);
                BigDecimal balance = BigDecimal.ZERO;
                eth = new Token(info, balance, System.currentTimeMillis());
                eth.setTokenNetwork(network.chainId);
                eth.setTokenWallet(wallet.address);
            }
            Log.d(TAG, "ETH(BAL): " + eth.balance);
            eth.setIsEthereum();
            return eth;
        });
    }

    @Override
    public Observable<Token> fetchActiveSingle(String walletAddress, Token token)
    {
        NetworkInfo network = ethereumNetworkRepository.getDefaultNetwork();
        Wallet wallet = new Wallet(walletAddress);
        return Single.merge(
                fetchCachedToken(network, wallet, token.getAddress()),
                updateBalance(network, wallet, token)) // Looking for new tokens
                .toObservable();
    }

    @Override
    public Observable<Token> fetchCachedSingleToken(String walletAddress, String tokenAddress)
    {
        NetworkInfo network = ethereumNetworkRepository.getDefaultNetwork();
        Wallet wallet = new Wallet(walletAddress);
        return fetchCachedToken(network, wallet, tokenAddress)
                .toObservable();
    }

    /**
     * Just updates the balance of a token
     *
     * @param walletAddress
     * @param token
     * @return
     */
    @Override
    public Observable<Token> fetchActiveTokenBalance(String walletAddress, Token token)
    {
        NetworkInfo network = ethereumNetworkRepository.getDefaultNetwork();
        Wallet wallet = new Wallet(walletAddress);
        return updateBalance(network, wallet, token)
                .observeOn(Schedulers.newThread())
                .toObservable();
    }

    @Override
    public Observable<Token> fetchActiveTokenBalance(Token token, NetworkInfo network, Wallet wallet)
    {
        return updateBalance(network, wallet, token)
                .observeOn(Schedulers.newThread())
                .toObservable();
    }

    private Observable<Token> fetchBalance(String walletAddress, Token token)
    {
        NetworkInfo network = ethereumNetworkRepository.getDefaultNetwork();
        Wallet wallet = new Wallet(walletAddress);
        return updateBalance(network, wallet, token)
                .observeOn(Schedulers.newThread())
                .toObservable();
    }

    @Override
    public Observable<Token[]> fetchAll(String walletAddress) {
        NetworkInfo network = ethereumNetworkRepository.getDefaultNetwork();
        Wallet wallet = new Wallet(walletAddress);
        return localSource.fetchAllTokens(network, wallet).toObservable();
    }

    private SingleTransformer<Token[], Token[]> attachTicker(NetworkInfo network, Wallet wallet) {
        return upstream -> upstream.flatMap(tokens ->
                Single.zip(
                        Single.just(tokens),
                        getTickers(network, wallet, tokens),
                        (data, tokenTickers) -> {
                            for (Token token : data) {
                                for (TokenTicker ticker : tokenTickers) {
                                    if (token.tokenInfo.address.equals(ticker.contract)) {
                                        token.ticker = ticker;
                                    }
                                }
                            }
                            return data;
                        }));
    }

    private SingleTransformer<Token[], Token[]> attachTickerStored(NetworkInfo network, Wallet wallet) {
        return upstream -> upstream.flatMap(tokens ->
                Single.zip(
                        Single.just(tokens),
                        getTickersStored(network, wallet, tokens),
                        (data, tokenTickers) -> {
                            for (Token token : data) {
                                for (TokenTicker ticker : tokenTickers) {
                                    if (token.tokenInfo.address.equals(ticker.contract)) {
                                        token.ticker = ticker;
                                    }
                                }
                            }
                            return data;
                        }));
    }

    private Single<TokenTicker[]> getTickersStored(NetworkInfo network, Wallet wallet, Token[] tokens) {
        return localSource.fetchTickers(network, wallet, tokens);
    }

    private Single<TokenTicker[]> getTickers(NetworkInfo network, Wallet wallet, Token[] tokens) {
        return localSource.fetchTickers(network, wallet, tokens)
                .onErrorResumeNext(throwable -> tickerService
                        .fetchTokenTickers(tokens, "USD")
                        .onErrorResumeNext(thr -> Single.just(new TokenTicker[0])))
                .flatMapCompletable(tokenTickers -> localSource.saveTickers(network, wallet, tokenTickers))
                .andThen(localSource
                        .fetchTickers(network, wallet, tokens)
                        .onErrorResumeNext(thr -> Single.just(new TokenTicker[0])));
    }

    @Override
    public Single<Token> addToken(Wallet wallet, TokenInfo tokenInfo, ContractType interfaceSpec)
    {
        TokenFactory tf = new TokenFactory();
        Token newToken = tf.createToken(tokenInfo, interfaceSpec);

        newToken.setTokenWallet(wallet.address);
        newToken.setTokenNetwork(ethereumNetworkRepository.getDefaultNetwork().chainId);
        Log.d(TAG, "Create for store: " + tokenInfo.name);

        return localSource.saveToken(
                    ethereumNetworkRepository.getDefaultNetwork(),
                    wallet,
                    newToken);
    }

    //TODO: This should be called on a per-token basis. User will usually only have one token, so currently it's ok
    @Override
    public Single<Token> callTokenFunctions(Token token, AssetDefinitionService service)
    {
        TokenDefinition definition = service.getAssetDefinition(token.getAddress());
        if (definition == null || definition.functions.size() == 0 || !token.requiresAuxRefresh()) return Single.fromCallable(() -> token); //quick return
        else return Single.fromCallable(() -> {
            token.auxDataRefreshed();
            //need to call the token functions now
            for (String keyName : definition.functions.keySet())
            {
                FunctionDefinition fd = definition.functions.get(keyName);
                //currently all function calls from XML have no params
                String result = null;
                switch (fd.syntax)
                {
                    case Boolean:
                        result = callBoolFunction(fd.method, token.getAddress());
                        break;
                    case IA5String:
                        if (!token.getTokenID(0).equals(BigInteger.valueOf(-1)))
                        {
                            result = callStringFunction(fd.method, token.getAddress(), token.getTokenID(0));
                        }
                        break;
                    default:
                        break;
                }

                token.addAuxDataResult(keyName, result);
            }
            return token;
        });
    }

    private String callStringFunction(String method, String address, BigInteger tokenId)
    {
        String result;
        try
        {
            result = getContractData(address, stringParam(method, tokenId), "");
        }
        catch (Exception e)
        {
            result = null;
        }

        return result;
    }

    private String callBoolFunction(String method, String address)
    {
        String result;
        try
        {
            Boolean res = getContractData(address, boolParam(method), Boolean.TRUE);
            result = res ? "true" : "false";
        }
        catch (Exception e)
        {
            result = "false";
        }

        return result;
    }

    @Override
    public Single<Token[]> addERC721(Wallet wallet, Token[] tokens)
    {
        return localSource.saveERC721Tokens(
                ethereumNetworkRepository.getDefaultNetwork(),
                wallet,
                tokens);
    }

    @Override
    public Single<String> callAddressMethod(String method, byte[] resultHash, String address)
    {
        return Single.fromCallable(() -> {
            org.web3j.abi.datatypes.Function function = addressFunction(method, resultHash);
            Wallet temp = new Wallet(null);
            String responseValue = callMainNetSmartContractFunction(function, address, temp);

            if (responseValue == null) return BURN_ADDRESS;

            List<Type> response = FunctionReturnDecoder.decode(
                    responseValue, function.getOutputParameters());
            if (response.size() == 1) {
                return (String)response.get(0).getValue();
            } else {
                return BURN_ADDRESS;
            }
        });
    }

    @Override
    public Completable setEnable(Wallet wallet, Token token, boolean isEnabled) {
        NetworkInfo network = ethereumNetworkRepository.getDefaultNetwork();
        return Completable.fromAction(() -> localSource.setEnable(network, wallet, token, isEnabled));
    }

    @Override
    public Completable setBurnList(Wallet wallet, Token token, List<Integer> burnList) {
        NetworkInfo network = ethereumNetworkRepository.getDefaultNetwork();
        return Completable.fromAction(() -> localSource.updateTokenBurn(network, wallet, token, burnList));
    }

    @Override
    public Observable<TokenInfo> update(String contractAddr) {
        return setupTokensFromLocal(contractAddr).toObservable();
    }

    @Override
    public void terminateToken(Token token, Wallet wallet, NetworkInfo network)
    {
        localSource.setTokenTerminated(network, wallet, token);
    }

    @Override
    public Single<TokenInfo[]> update(String[] address)
    {
        return setupTokensFromLocal(address);
    }

    private Single<Token[]> fetchFromNetworkSource(@NonNull NetworkInfo network, @NonNull Wallet wallet) {
        return Single.fromCallable(() -> {
            try {
                return network.isMainNetwork
                        ? tokenNetworkService.fetch(wallet.address).blockingFirst()
                        : new TokenInfo[0];
            } catch (Throwable th) {
                // Ignore all errors, it's not important source.
                return new TokenInfo[0];
            }
        })
        .map(this::mapToTokens);
    }

    private Single<Token[]> extractFromTransactions(NetworkInfo network, Wallet wallet) {
        return transactionsLocalCache.fetchTransaction(network, wallet)
                .flatMap(transactions -> {
                    List<Token> result = new ArrayList<>();
                    for (Transaction transaction : transactions) {
                        if (transaction.operations == null || transaction.operations.length == 0) {
                            continue;
                        }
                        TransactionOperation operation = transaction.operations[0];
                        result.add(new Token(new TokenInfo(
                                operation.contract.address,
                                operation.contract.name,
                                operation.contract.symbol,
                                operation.contract.decimals,
                                true), null, 0));
                    }
                    return Single.just(result.toArray(new Token[result.size()]));
                });
    }

    private Completable updateTokens(NetworkInfo network, Wallet wallet) {
        return Single.zip(
                fetchFromNetworkSource(network, wallet),
                extractFromTransactions(network, wallet),
                localSource.fetchAllTokens(network, wallet),
                (fromNetTokens, fromTrxTokens, cachedTokens) -> {
                    final Set<String> oldTokensIndex = new HashSet<>();
                    final List<Token> zip = new ArrayList<>();
                    zip.addAll(Arrays.asList(fromNetTokens));
                    zip.addAll(Arrays.asList(fromTrxTokens));
                    final List<Token> newTokens = new ArrayList<>();
                    for (Token cachedToken : cachedTokens) {
                        oldTokensIndex.add(cachedToken.tokenInfo.address);
                    }
                    for (int i = zip.size() - 1; i > -1; i--) {
                        if (!oldTokensIndex.contains(zip.get(i).tokenInfo.address)) {
                            newTokens.add(zip.get(i));
                        }
                    }
                    return newTokens.toArray(new Token[newTokens.size()]);
                })
                .flatMapCompletable(tokens -> localSource.saveTokens(network, wallet, tokens));
    }

    /**
     * Obtain live balance of token from Ethereum blockchain and cache into Realm
     *
     * @param network
     * @param wallet
     * @param token
     * @return
     */
    private Single<Token> updateBalance(NetworkInfo network, Wallet wallet, final Token token) {
        if (token.isEthereum())
        {
            return attachEth(network, wallet);
        }
        else
        return Single.fromCallable(() -> {
            TokenFactory tFactory = new TokenFactory();
            try
            {
                List<BigInteger> balanceArray = null;
                List<Integer> burnArray = null;
                BigDecimal balance = null;
                TokenInfo tInfo = token.tokenInfo;
                switch (token.getInterfaceSpec())
                {
                    case ERC875:
                    case ERC875LEGACY:
                        balanceArray = wrappedCheckBalanceArray(wallet, tInfo, token);
                        burnArray = token.getBurnList();
                        break;
                    case ERC721:
                        break;
                    case ERC20:
                    case ETHEREUM:
                        balance = wrappedCheckUintBalance(wallet, token.tokenInfo, token);
                        break;
                    default:
                        break;
                }

                Token updated = tFactory.createToken(tInfo, balance, balanceArray, burnArray, System.currentTimeMillis(), token.getInterfaceSpec());
                updated.patchAuxData(token); //perform any updates we need here
                localSource.updateTokenBalance(network, wallet, updated);
                updated.setTokenWallet(wallet.address);
                updated.setTokenNetwork(network.chainId);
                return updated;
            }
            catch (BadContract e)
            {
                return token;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return token;
            }
        });
    }

    /**
     * Checks the balance of a token returning Uint256 value, eg Ethereum, ERC20
     * If there was a network error the balance is taken from the previously recorded value
     * @param wallet
     * @param tokenInfo
     * @param token
     * @return
     */
    private BigDecimal wrappedCheckUintBalance(Wallet wallet, TokenInfo tokenInfo, Token token) throws Exception
    {
        BigDecimal balance = getBalance(wallet, tokenInfo);
        if (balance.compareTo(BigDecimal.valueOf(-1)) == 0)
        {
            balance = token.balance;
        }
        return balance;
    }

    /**
     * Checks the balance array for an ERC875 token.
     * The balance function returns a value of -1 in the first entry if there was a comms error.
     * In the event of a comms error, just use the previously obtained balance of the token
     * @param wallet
     * @param tInfo
     * @param token
     * @return
     */
    private List<BigInteger> wrappedCheckBalanceArray(Wallet wallet, TokenInfo tInfo, Token token) throws Exception
    {
        List<BigInteger> balance = getBalanceArray(wallet, tInfo);
        if (balance.size() > 0
                && (balance.get(0).compareTo(BigInteger.valueOf(-1)) == 0))
        {
            //comms error, use previous token balance
            balance = token.getArrayBalance();
        }

        return balance;
    }

    private ObservableTransformer<Token, Token> updateBalance(NetworkInfo network, Wallet wallet) {
        return upstream -> upstream.map(token -> {
            Token newToken = null;
            TokenFactory tFactory = new TokenFactory();
            long now = System.currentTimeMillis();
            long minUpdateBalanceTime = now - BALANCE_UPDATE_INTERVAL;
            BigDecimal balance = null;
            List<BigInteger> balanceArray = null;
            List<Integer> burnArray = null;
            TokenInfo tInfo = token.tokenInfo;
            if (token.balance == null || token.updateBlancaTime < minUpdateBalanceTime) {
                try {
                    switch (token.getInterfaceSpec())
                    {
                        case ERC875:
                        case ERC875LEGACY:
                            balanceArray = wrappedCheckBalanceArray(wallet, token.tokenInfo, token);
                            burnArray = token.getBurnList();
                            break;
                        case ERC721:
                            break;
                        case ERC20:
                        case ETHEREUM:
                            balance = wrappedCheckUintBalance(wallet, token.tokenInfo, token);
                            break;
                        default:
                            break;
                    }

                    newToken = tFactory.createToken(tInfo, balance, balanceArray, burnArray, now, token.getInterfaceSpec());
                    localSource.updateTokenBalance(network, wallet, token);
                    newToken.patchAuxData(token);
                } catch (Throwable th) { /* Quietly */ }
            }
            return newToken;
        });
    }

    private TokenInfo changeToERC20Token(Token token)
    {
        return new TokenInfo(token.tokenInfo.address,
                                        token.tokenInfo.name,
                                        token.tokenInfo.symbol,
                                        token.tokenInfo.decimals,
                                        true, false);
    }

    private TokenInfo changeToERC875Token(Token token)
    {
        return new TokenInfo(token.tokenInfo.address, token.tokenInfo.name, token.tokenInfo.symbol, token.tokenInfo.decimals, true, true);
    }

    private SingleTransformer<Token[], Token[]> attachEthereum(NetworkInfo network, Wallet wallet)
    {
        return upstream -> Single.zip(
                upstream, attachEth(network, wallet),
                (tokens, ethToken) ->
                {
                    List<Token> result = new ArrayList<>();
                    result.add(ethToken);
                    result.addAll(Arrays.asList(tokens));
                    return result.toArray(new Token[result.size()]);
                });
    }

    @Override
    public rx.Subscription memPoolListener(SubscribeWrapper subscriber)
    {
        if (!useBackupNode)
        {
            return web3j.pendingTransactionObservable()
                    .subscribeOn(rx.schedulers.Schedulers.newThread())
                    .subscribe(subscriber.scanReturn, subscriber.onError);
        }
        else
        {
            return null;
        }
    }

    private Single<Token[]> fetchStoredEnabledTokens(NetworkInfo network, Wallet wallet) {
        return localSource
                .fetchEnabledTokensWithBalance(network, wallet);
    }

    private Single<Token> fetchCachedToken(NetworkInfo network, Wallet wallet, String address) {
        return localSource
                .fetchEnabledToken(network, wallet, address);
    }

    private Single<Token> attachEth(NetworkInfo network, Wallet wallet) {
        return getEthBalance(wallet) //use local balance fetch, uses less resources
                .map(balance -> {
                    Log.d(TAG, "ETH: " + balance.toPlainString());
                    if (balance.equals(BigDecimal.valueOf(-1)))
                    {
                        //network error - retrieve from cache
                        Token b = localSource.getTokenBalance(network, wallet, wallet.address);
                        if (b != null) balance = b.balance;
                        else balance = BigDecimal.ZERO;
                    }
                    TokenInfo info = new TokenInfo(wallet.address, network.name, network.symbol, 18, true);
                    Token eth = new Token(info, balance, System.currentTimeMillis());
                    eth.setIsEthereum();
                    eth.setTokenNetwork(network.chainId);
                    eth.setTokenWallet(wallet.address);
                    //store token and balance
                    localSource.updateTokenBalance(network, wallet, eth);
                    return eth;
                })
                .flatMap(token -> ethereumNetworkRepository.getTicker()
                        .map(ticker -> {
                            token.ticker = new TokenTicker("", "", ticker.price_usd, ticker.percentChange24h, null);
                            return token;
                        }).onErrorResumeNext(throwable -> Single.just(token)));
    }

    /**
     * Either returns the live eth balance or cached if network is unavilable
     * We can derive the time when the balance was fetched from the Token info
     *
     * @param network
     * @param wallet
     * @return
     */
    @Override
    public Single<Token> getEthBalance(NetworkInfo network, Wallet wallet) {
        return attachEth(network, wallet);
    }

    @Override
    public Single<Ticker> getEthTicker()
    {
        return ethereumNetworkRepository.getTicker();
    }

    private BigDecimal getBalance(Wallet wallet, TokenInfo tokenInfo) throws Exception {
        Function function = balanceOf(wallet.address);
        String responseValue = callSmartContractFunction(function, tokenInfo.address, wallet);

        if (responseValue == null) return BigDecimal.valueOf(-1); //early return for network error

        List<Type> response = FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        if (response.size() == 1) {
            return new BigDecimal(((Uint256) response.get(0)).getValue());
        } else {
            return BigDecimal.ZERO;
        }
    }

    private Single<BigDecimal> getEthBalance(Wallet wallet)
    {
        return Single.fromCallable(() -> {
            try {
                return new BigDecimal(web3j.ethGetBalance(wallet.address, DefaultBlockParameterName.PENDING)
                        .send()
                        .getBalance());
            }
            catch (IOException e)
            {
                return BigDecimal.valueOf(-1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return BigDecimal.valueOf(-1);
            }
        }).subscribeOn(Schedulers.io());
    }

    private List<BigInteger> getBalanceArray(Wallet wallet, TokenInfo tokenInfo) throws Exception {
        List<BigInteger> result = new ArrayList<>();
        result.add(BigInteger.valueOf(-1));
        try
        {
            org.web3j.abi.datatypes.Function function = balanceOfArray(wallet.address);
            List<Uint256> indices = callSmartContractFunctionArray(function, tokenInfo.address, wallet);
            if (indices != null)
            {
                result.clear();
                for (Uint256 val : indices)
                {
                    result.add(val.getValue());
                }
            }
        }
        catch (StringIndexOutOfBoundsException e)
        {
            //contract call error
            result.clear();
            result.add(BigInteger.valueOf(-1));
        }
        return result;
    }

    public rx.Observable<TransferFromEventResponse> burnListenerObservable(String contractAddr)
    {
        if (!useBackupNode)
        {
            final Event event = new Event("TransferFrom",
                                          Arrays.<TypeReference<?>>asList(new TypeReference<Address>()
                                          {
                                          }, new TypeReference<Address>()
                                          {
                                          }),
                                          Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint16>>()
                                          {
                                          }));
            EthFilter filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.PENDING, contractAddr);
            filter.addSingleTopic(EventEncoder.encode(event));
            return web3j.ethLogObservable(filter).map(new Func1<org.web3j.protocol.core.methods.response.Log, TransferFromEventResponse>()
            {
                @Override
                public TransferFromEventResponse call(org.web3j.protocol.core.methods.response.Log log)
                {
                    EventValues eventValues = extractEventParameters(event, log);
                    TransferFromEventResponse typedResponse = new TransferFromEventResponse();
                    typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                    typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                    typedResponse._indices = (List<Uint16>) eventValues.getNonIndexedValues().get(0).getValue();
                    return typedResponse;
                }
            });
        }
        else
        {
            return rx.Observable.fromCallable(() -> {
                TransferFromEventResponse event = new TransferFromEventResponse();
                event._from = "";
                event._to = "";
                event._indices = null;
                return event;
            });
        }
    }

    private <T> T getContractData(String address, org.web3j.abi.datatypes.Function function, T type) throws Exception
    {
        Wallet temp = new Wallet(null);
        String responseValue = callSmartContractFunction(function, address, temp);

        if (responseValue == null)
        {
            if (type instanceof Boolean)
            {
                return (T)Boolean.FALSE;
            }
            else
            {
                return null;
            }
        }

        List<Type> response = FunctionReturnDecoder.decode(
                responseValue, function.getOutputParameters());
        if (response.size() == 1)
        {
            return (T) response.get(0).getValue();
        }
        else
        {
            return null;
        }
    }

    private String getName(String address) throws Exception {
        org.web3j.abi.datatypes.Function function = nameOf();
        Wallet temp = new Wallet(null);
        String responseValue = callSmartContractFunction(function, address, temp);

        if (responseValue == null) return null;

        List<Type> response = FunctionReturnDecoder.decode(
                responseValue, function.getOutputParameters());
        if (response.size() == 1) {
            String name = (String)response.get(0).getValue();
            if (assetDefinitionService.getNetworkId(address) > 0)
            {
                //does name already contain the token type
                String tokenTypeName = assetDefinitionService.getAssetDefinition(address).getTokenName();
                if (name != null && !name.contains(tokenTypeName)) name = name + " " + tokenTypeName;
            }
            return name;
        } else {
            return null;
        }
    }

    private int getDecimals(String address) throws Exception {
        org.web3j.abi.datatypes.Function function = decimalsOf();
        Wallet temp = new Wallet(null);
        String responseValue = callSmartContractFunction(function, address, temp);
        if (responseValue == null) return 18;

        List<Type> response = FunctionReturnDecoder.decode(
                responseValue, function.getOutputParameters());
        if (response.size() == 1) {
            return ((Uint8) response.get(0)).getValue().intValue();
        } else {
            return 18;
        }
    }

    private static org.web3j.abi.datatypes.Function balanceOf(String owner) {
        return new org.web3j.abi.datatypes.Function(
                "balanceOf",
                Collections.singletonList(new Address(owner)),
                Collections.singletonList(new TypeReference<Uint256>() {}));
    }

    private static org.web3j.abi.datatypes.Function balanceOfArray(String owner) {
        return new org.web3j.abi.datatypes.Function(
                "balanceOf",
                Collections.singletonList(new Address(owner)),
                Collections.singletonList(new TypeReference<DynamicArray<Uint256>>() {}));
    }

    private static org.web3j.abi.datatypes.Function nameOf() {
        return new Function("name",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    }

    private static org.web3j.abi.datatypes.Function stringParam(String param) {
        return new Function(param,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    }

    private static org.web3j.abi.datatypes.Function boolParam(String param) {
        return new Function(param,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
    }

    private static org.web3j.abi.datatypes.Function stringParam(String param, BigInteger value) {
        return new Function(param,
                            Arrays.asList(new Uint256(value)),
                            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    }

    private static org.web3j.abi.datatypes.Function intParam(String param) {
        return new Function(param,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint>() {}));
    }

    private static org.web3j.abi.datatypes.Function symbolOf() {
        return new Function("symbol",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    }

    private static org.web3j.abi.datatypes.Function decimalsOf() {
        return new Function("decimals",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
    }

    private org.web3j.abi.datatypes.Function addressFunction(String method, byte[] resultHash)
    {
        return new org.web3j.abi.datatypes.Function(
                method,
                Collections.singletonList(new org.web3j.abi.datatypes.generated.Bytes32(resultHash)),
                Collections.singletonList(new TypeReference<Address>() {}));
    }

    private List callSmartContractFunctionArray(
            org.web3j.abi.datatypes.Function function, String contractAddress, Wallet wallet)
    {
        try
        {
            String encodedFunction = FunctionEncoder.encode(function);
            org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
                    org.web3j.protocol.core.methods.request.Transaction
                            .createEthCallTransaction(wallet.address, contractAddress, encodedFunction),
                    DefaultBlockParameterName.LATEST).send();

            String value = ethCall.getValue();
            List<Type> values = FunctionReturnDecoder.decode(value, function.getOutputParameters());
            if (values.isEmpty())
                return null;

            Type T = values.get(0);
            Object o = T.getValue();
            return (List) o;
        }
        catch (IOException e) //this call is expected to be interrupted when user switches network or wallet
        {
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private String callSmartContractFunction(
            Function function, String contractAddress, Wallet wallet) throws Exception {
        String encodedFunction = FunctionEncoder.encode(function);

        try
        {
            org.web3j.protocol.core.methods.request.Transaction transaction
                    = createEthCallTransaction(wallet.address, contractAddress, encodedFunction);
            EthCall response = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();

            return response.getValue();
        }
        catch (IOException e) //this call is expected to be interrupted when user switches network or wallet
        {
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Call smart contract function on mainnet contract. This would be used for things like ENS lookup
     * Currently because it's tied to a mainnet contract address there's no circumstance it would work
     * outside of mainnet. Users may be confused if their namespace doesn't work, even if they're currently
     * using testnet.
     *
     * @param function
     * @param contractAddress
     * @param wallet
     * @return
     * @throws Exception
     */
    private String callMainNetSmartContractFunction(
            Function function, String contractAddress, Wallet wallet) throws Exception {
        String encodedFunction = FunctionEncoder.encode(function);

        try
        {
            Web3j mainNet;
            if (network.isMainNetwork)
            {
                mainNet = web3j;
            }
            else
            {
                mainNet = Web3jFactory.build(new org.web3j.protocol.http.HttpService(ethereumNetworkRepository.getAvailableNetworkList()[0].rpcServerUrl));
            }
            org.web3j.protocol.core.methods.request.Transaction transaction
                    = createEthCallTransaction(wallet.address, contractAddress, encodedFunction);
            EthCall response = mainNet.ethCall(transaction, DefaultBlockParameterName.LATEST).send();

            return response.getValue();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] createTokenTransferData(String to, BigInteger tokenAmount) {
        List<Type> params = Arrays.asList(new Address(to), new Uint256(tokenAmount));
        List<TypeReference<?>> returnTypes = Collections.singletonList(new TypeReference<Bool>() {});
        Function function = new Function("transfer", params, returnTypes);
        String encodedFunction = FunctionEncoder.encode(function);
        return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction));
    }

    public static byte[] createTicketTransferData(String to, String indexListStr, Token token) {
        List ticketIndicies = Observable.fromArray(indexListStr.split(","))
                .map(s -> new BigInteger(s.trim())).toList().blockingGet();
        Function function = ((Ticket)token).getTransferFunction(to, ticketIndicies);

        String encodedFunction = FunctionEncoder.encode(function);
        return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction));
    }

    public static byte[] createERC721TransferFunction(String to, Token token, String tokenId)
    {
        Function function = token.getTransferFunction(to, tokenId);
        String encodedFunction = FunctionEncoder.encode(function);
        return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction));
    }

    public static byte[] createTrade(Token token, BigInteger expiry, List<BigInteger> ticketIndices, int v, byte[] r, byte[] s)
    {
        Function function = ((Ticket)token).getTradeFunction(expiry, ticketIndices, v, r, s);
        String encodedFunction = FunctionEncoder.encode(function);
        return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction));
    }

    private Token[] mapToTokens(TokenInfo[] items) {
        int len = items.length;
        Token[] tokens = new Token[len];
        for (int i = 0; i < len; i++) {
            tokens[i] = new Token(items[i], null, 0);
        }
        return tokens;
    }

    private Single<TokenInfo> setupTokensFromLocal(String address)
    {
        return Single.fromCallable(() -> {
            try
            {
                long now = System.currentTimeMillis();
                Boolean isStormbird = getContractData(address, boolParam("isStormBirdContract"), Boolean.TRUE);
                if (isStormbird == null) isStormbird = false;
                TokenInfo result = new TokenInfo(
                        address,
                        getName(address),
                        getContractData(address, stringParam("symbol"), ""),
                        getDecimals(address),
                        true,
                        isStormbird);

                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        });
    }

    private Single<TokenInfo[]> setupTokensFromLocal(String[] addresses)
    {
        return Single.fromCallable(() -> {
            List<TokenInfo> tokenList = new ArrayList<>();
            try
            {
                for (String address : addresses)
                {
                    long now = System.currentTimeMillis();
                    String name = getName(address);
                    Boolean isStormbird = getContractData(address, boolParam("isStormBirdContract"), Boolean.TRUE);
                    if (isStormbird == null) isStormbird = false;
                    TokenInfo result = new TokenInfo(
                            address,
                            name,
                            getContractData(address, stringParam("symbol"), ""),
                            getDecimals(address),
                            true,
                            isStormbird);

                    tokenList.add(result);
                }
                return tokenList.toArray(new TokenInfo[tokenList.size()]);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return tokenList.toArray(new TokenInfo[tokenList.size()]);
            }
        });
    }

    protected EventValues extractEventParameters(
            Event event, org.web3j.protocol.core.methods.response.Log log) {

        List<String> topics = log.getTopics();
        String encodedEventSignature = EventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return null;
        }

        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(), event.getNonIndexedParameters());

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }
}
