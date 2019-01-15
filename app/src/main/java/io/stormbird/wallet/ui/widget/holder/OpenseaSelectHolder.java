package io.stormbird.wallet.ui.widget.holder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import android.widget.LinearLayout;
import io.stormbird.token.entity.TicketRange;
import io.stormbird.wallet.R;
import io.stormbird.wallet.entity.Token;
import io.stormbird.wallet.entity.opensea.Asset;
import io.stormbird.wallet.ui.widget.OnOpenseaAssetCheckListener;
import io.stormbird.wallet.ui.widget.OnTokenCheckListener;

import java.math.BigInteger;

/**
 * Created by James on 12/11/2018.
 * Stormbird in Singapore
 */
public class OpenseaSelectHolder extends OpenseaHolder
{
    private final AppCompatRadioButton select;
    private OnOpenseaAssetCheckListener onTokenCheckListener;
    private final LinearLayout ticketLayout;

    public OpenseaSelectHolder(int resId, ViewGroup parent, Token token)
    {
        super(resId, parent, token);
        ticketLayout = findViewById(R.id.layout_select_ticket);
        select = findViewById(R.id.radioBox);
    }

    @Override
    public void bind(@Nullable Asset asset, @NonNull Bundle addition)
    {
        super.bind(asset, addition);
        select.setVisibility(View.VISIBLE);

        select.setOnCheckedChangeListener(null); //have to invalidate listener first otherwise we trigger cached listener and create infinite loop
        select.setChecked(asset.isChecked);

        select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    onTokenCheckListener.onAssetCheck(asset);
                }
            }
        });

        ticketLayout.setOnClickListener(v -> {
            select.setChecked(true);
        });
    }

    public void setOnTokenCheckListener(OnOpenseaAssetCheckListener onTokenCheckListener)
    {
        this.onTokenCheckListener = onTokenCheckListener;
    }
}
