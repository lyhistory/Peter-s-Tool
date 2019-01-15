package io.stormbird.wallet.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.stormbird.wallet.entity.ENSCallback;
import io.stormbird.wallet.ui.widget.entity.ENSHandler;
import org.web3j.abi.datatypes.Address;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.stormbird.wallet.C;
import io.stormbird.wallet.R;
import io.stormbird.wallet.entity.Token;
import io.stormbird.wallet.entity.TokenInfo;
import io.stormbird.wallet.entity.Wallet;
import io.stormbird.wallet.router.EthereumInfoRouter;
import io.stormbird.wallet.ui.widget.adapter.AutoCompleteUrlAdapter;
import io.stormbird.wallet.ui.widget.entity.ItemClickListener;
import io.stormbird.wallet.ui.zxing.FullScannerFragment;
import io.stormbird.wallet.ui.zxing.QRScanningActivity;
import io.stormbird.wallet.util.BalanceUtils;
import io.stormbird.wallet.util.KeyboardUtils;
import io.stormbird.wallet.util.QRURLParser;
import io.stormbird.wallet.util.QRUtils;
import io.stormbird.wallet.viewmodel.SendViewModel;
import io.stormbird.wallet.viewmodel.SendViewModelFactory;
import io.stormbird.wallet.widget.AWalletAlertDialog;

import static io.stormbird.wallet.C.Key.WALLET;

public class SendActivity extends BaseActivity implements Runnable, ItemClickListener
{
    private static final String KEY_ADDRESS = "key_address";
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    @Inject
    SendViewModelFactory sendViewModelFactory;
    SendViewModel viewModel;

    // In case we're sending tokens
    private boolean sendingTokens = false;
    private String myAddress;
    private int decimals;
    private String symbol;
    private Wallet wallet;
    private Token token;
    private String contractAddress;
    private double currentEthPrice;

    RelativeLayout ethDetailLayout;
    Button startTransferButton;
    Button copyAddressButton;
    EditText amountEditText;
    AutoCompleteTextView toAddressEditText;
    ImageView qrImageView;
    ImageButton scanQrImageView;
    TextView toAddressError;
    TextView amountError;
    TextView myAddressText;
    TextView amountSymbolText;
    AWalletAlertDialog dialog;

    private ENSHandler ensHandler;
    Handler handler;

    //Token
    TextView balanceEth;
    TextView symbolText;
    TextView arrayBalance;

    TextView priceUSD;
    LinearLayout priceUSDLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send);
        toolbar();
        setTitle("");

        viewModel = ViewModelProviders.of(this, sendViewModelFactory)
                .get(SendViewModel.class);

        handler = new Handler();

        contractAddress = getIntent().getStringExtra(C.EXTRA_CONTRACT_ADDRESS); //contract address
        decimals = getIntent().getIntExtra(C.EXTRA_DECIMALS, C.ETHER_DECIMALS);
        symbol = getIntent().getStringExtra(C.EXTRA_SYMBOL);
        symbol = symbol == null ? C.ETH_SYMBOL : symbol;
        sendingTokens = getIntent().getBooleanExtra(C.EXTRA_SENDING_TOKENS, false);
        wallet = getIntent().getParcelableExtra(WALLET);
        token = getIntent().getParcelableExtra(C.EXTRA_TOKEN_ID);
        myAddress = wallet.address;

        setupTokenContent();

        initViews();
        setupAddressEditField();

        if (token.addressMatches(myAddress))
        {
            viewModel.startEthereumTicker();
            viewModel.ethPriceReading().observe(this, this::onNewEthPrice);
        }
        else
        {
            //currently we don't evaluate ERC20 token value. TODO: Should we?
            priceUSDLayout.setVisibility(View.GONE);
        }
    }

    private void setupAddressEditField()
    {
        AutoCompleteUrlAdapter adapterUrl = new AutoCompleteUrlAdapter(getApplicationContext(), C.ENS_HISTORY);
        adapterUrl.setListener(this);
        ENSCallback ensCallback = new ENSCallback()
        {
            @Override
            public void ENSComplete()
            {
                onStartTransfer();
            }

            @Override
            public void ENSCheck(String name)
            {
                viewModel.checkENSAddress(name);
            }
        };
        ensHandler = new ENSHandler(this, handler, adapterUrl, this, ensCallback);
        viewModel.ensResolve().observe(this, ensHandler::onENSSuccess);
        viewModel.ensFail().observe(this, ensHandler::hideENS);
    }

    private void onNewEthPrice(Double ethPrice)
    {
        currentEthPrice = ethPrice;
        //just got a new eth price
        //recalculate the equivalent USD price
        updateUSDValue();
    }

    private void updateUSDValue()
    {
        String amount = amountEditText.getText().toString();
        if (amount.length() == 0) amount = "0"; //this is to reset the USD equivalent if you delete a large eth value
        if (isValidAmount(amount))
        {
            String usdEquivStr = "$" + getUsdString(Double.valueOf(amount) * currentEthPrice);
            priceUSD.setText(usdEquivStr);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            qrImageView = findViewById(R.id.qr_image);
            qrImageView.setImageBitmap(QRUtils.createQRImage(this, myAddress, qrImageView.getWidth()));
            qrImageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        }
    }

    private void initViews()
    {
        amountSymbolText = findViewById(R.id.edit_amount_symbol);
        amountSymbolText.setText(token.tokenInfo.symbol);
        toAddressError = findViewById(R.id.to_address_error);
        amountError = findViewById(R.id.amount_error);
        myAddressText = findViewById(R.id.address);
        myAddressText.setText(myAddress);
        ethDetailLayout = findViewById(R.id.layout_eth_detail);
        priceUSD = findViewById(R.id.textImportPriceUSD);
        priceUSDLayout = findViewById(R.id.layout_usd_price);

        startTransferButton = findViewById(R.id.button_start_transfer);
        startTransferButton.setOnClickListener(v -> onStartTransfer());

        copyAddressButton = findViewById(R.id.copy_action);
        copyAddressButton.setOnClickListener(v -> copyAddress());

        scanQrImageView = findViewById(R.id.img_scan_qr);
        scanQrImageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, QRScanningActivity.class);
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
//            Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
//            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        });

        amountEditText = findViewById(R.id.edit_amount);
        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                amountError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //update USD price
                updateUSDValue();
            }
        });

        toAddressEditText = findViewById(R.id.edit_to_address);
    }

    private void copyAddress()
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(KEY_ADDRESS, wallet.address);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
    }

    private void onStartTransfer() {
        boolean isValid = true;

        dismissKeyboard();
        amountError.setVisibility(View.GONE);
        final String amount = amountEditText.getText().toString();
        if (!isValidAmount(amount) || !isBalanceEnough(amount)) {
            amountError.setVisibility(View.VISIBLE);
            amountError.setText(R.string.error_invalid_amount);
            isValid = false;
        }

        toAddressError.setVisibility(View.GONE);
        String to = ensHandler.getAddressFromEditView();
        if (to == null) return;

        if (isValid)
        {
            BigInteger amountInSubunits = BalanceUtils.baseToSubunit(amountEditText.getText().toString(), decimals);
            viewModel.openConfirmation(this, to, amountInSubunits, contractAddress, decimals, symbol, sendingTokens, ensHandler.getEnsName());
        }
    }

    private void onBack()
    {
        if (ethDetailLayout.getVisibility() == View.VISIBLE)
        {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_qr, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBack();
                break;
            }
            case R.id.action_info: {
                new EthereumInfoRouter().open(this);
                break;
            }
            case R.id.action_qr:
                viewModel.showContractInfo(this, contractAddress);
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == FullScannerFragment.SUCCESS) {
                if (data != null) {
                    String barcode = data.getParcelableExtra(FullScannerFragment.BarcodeObject);
                    if (barcode == null) barcode = data.getStringExtra(FullScannerFragment.BarcodeObject);

                    //if barcode is still null, ensure we don't GPF
                    if (barcode == null)
                    {
                        Toast.makeText(this, R.string.toast_qr_code_no_address, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    QRURLParser parser = QRURLParser.getInstance();
                    String extracted_address = parser.extractAddressFromQrString(barcode);
                    if (extracted_address == null) {
                        dialog = new AWalletAlertDialog(this);
                        dialog.setIcon(AWalletAlertDialog.ERROR);
                        dialog.setTitle(R.string.toast_qr_code_no_address);
                        dialog.setButtonText(R.string.dialog_ok);
                        dialog.setButtonListener(v -> dialog.dismiss());
                        dialog.show();
                        return;
                    }
                    toAddressEditText.setText(extracted_address);
                }
            } else {
                Log.e("SEND", String.format(getString(R.string.barcode_error_format),
                        "Code: " + String.valueOf(resultCode)
                        ));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    boolean isValidAmount(String eth) {
        try {
            String wei = BalanceUtils.EthToWei(eth);
            return wei != null;
        } catch (Exception e) {
            return false;
        }
    }

    boolean isBalanceEnough(String eth) {
        try {
            BigDecimal amount = new BigDecimal(BalanceUtils.EthToWei(eth));
            BigDecimal balance = new BigDecimal(BalanceUtils.EthToWei(balanceEth.getText().toString()));
            return (balance.subtract(amount).compareTo(BigDecimal.ZERO) == 0 || balance.subtract(amount).compareTo(BigDecimal.ZERO) > 0);
        } catch (Exception e) {
            return false;
        }
    }

    private void dismissKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void setupTokenContent()
    {
        balanceEth = findViewById(R.id.balance_eth);
        arrayBalance = findViewById(R.id.balanceArray);
        symbolText = findViewById(R.id.symbol);

        symbolText.setText(TextUtils.isEmpty(token.tokenInfo.name)
                ? token.tokenInfo.symbol.toUpperCase()
                : getString(R.string.token_name, token.tokenInfo.name, token.tokenInfo.symbol.toUpperCase()));

        TokenInfo tokenInfo = token.tokenInfo;
        BigDecimal decimalDivisor = new BigDecimal(Math.pow(10, tokenInfo.decimals));
        BigDecimal ethBalance = tokenInfo.decimals > 0
                ? token.balance.divide(decimalDivisor) : token.balance;
        ethBalance = ethBalance.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros();
        String value = ethBalance.compareTo(BigDecimal.ZERO) == 0 ? "0" : ethBalance.toPlainString();
        balanceEth.setText(value);

        balanceEth.setVisibility(View.VISIBLE);
        arrayBalance.setVisibility(View.GONE);

        if (viewModel.hasIFrame(token.getAddress()))
        {
            addTokenPage();
        }
    }

    private void addTokenPage()
    {
        LinearLayout viewWrapper = findViewById(R.id.layout_iframe);
        try
        {
            WebView iFrame = findViewById(R.id.iframe);
            String tokenData = viewModel.getTokenData(token.getAddress());
            iFrame.loadData(tokenData, "text/html", "UTF-8");
            viewWrapper.setVisibility(View.VISIBLE);
        }
        catch (Exception e)
        {
            viewWrapper.setVisibility(View.GONE);
        }
    }

    public static String getUsdString(double usdPrice)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(usdPrice);
    }

    @Override
    public void run()
    {
        ensHandler.checkENS();
    }

    @Override
    public void onItemClick(String url)
    {
        ensHandler.handleHistoryItemClick(url);
    }
}
