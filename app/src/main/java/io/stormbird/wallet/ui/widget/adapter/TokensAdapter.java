package io.stormbird.wallet.ui.widget.adapter;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import io.stormbird.wallet.R;
import io.stormbird.wallet.entity.Ticket;
import io.stormbird.wallet.entity.Token;
import io.stormbird.wallet.service.AssetDefinitionService;
import io.stormbird.wallet.ui.widget.OnTokenClickListener;
import io.stormbird.wallet.ui.widget.entity.SortedItem;
import io.stormbird.wallet.ui.widget.entity.TokenSortedItem;
import io.stormbird.wallet.ui.widget.entity.TotalBalanceSortedItem;
import io.stormbird.wallet.ui.widget.holder.BinderViewHolder;
import io.stormbird.wallet.ui.widget.holder.TokenHolder;
import io.stormbird.wallet.ui.widget.holder.TotalBalanceHolder;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TokensAdapter extends RecyclerView.Adapter<BinderViewHolder> {
    private static final String TAG = "TKNADAPTER";
    public static final int FILTER_ALL = 0;
    public static final int FILTER_CURRENCY = 1;
    public static final int FILTER_ASSETS = 2;

    private int filterType;
    private Context context;
    private boolean needsRefresh;
    protected final AssetDefinitionService assetService;

    protected final OnTokenClickListener onTokenClickListener;
    protected final SortedList<SortedItem> items = new SortedList<>(SortedItem.class, new SortedList.Callback<SortedItem>() {
        @Override
        public int compare(SortedItem o1, SortedItem o2) {
            return o1.compare(o2);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(SortedItem oldItem, SortedItem newItem) {
            return oldItem.areContentsTheSame(newItem);
        }

        @Override
        public boolean areItemsTheSame(SortedItem item1, SortedItem item2) {
            return item1.areItemsTheSame(item2);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }
    });

    protected TotalBalanceSortedItem total = new TotalBalanceSortedItem(null);

    public TokensAdapter(Context context, OnTokenClickListener onTokenClickListener, AssetDefinitionService aService) {
        this.context = context;
        this.onTokenClickListener = onTokenClickListener;
        needsRefresh = true;
        this.assetService = aService;
    }

    public TokensAdapter(OnTokenClickListener onTokenClickListener, AssetDefinitionService aService) {
        this.onTokenClickListener = onTokenClickListener;
        needsRefresh = true;
        this.assetService = aService;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public BinderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BinderViewHolder holder = null;
        switch (viewType) {
            case TokenHolder.VIEW_TYPE: {
                TokenHolder tokenHolder = new TokenHolder(R.layout.item_token, parent, assetService);
                tokenHolder.setOnTokenClickListener(onTokenClickListener);
                holder = tokenHolder;
                setAnimation(holder.itemView);
            }
            break;
            default:
            // NB to save ppl a lot of effort this view doesn't show - item_total_balance has height coded to 1dp.
            case TotalBalanceHolder.VIEW_TYPE: {
                holder = new TotalBalanceHolder(R.layout.item_total_balance, parent);
                setAnimation(holder.itemView);
            }
        }
        return holder;
    }

    private void setAnimation(View viewToAnimate) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
    }

    @Override
    public void onBindViewHolder(BinderViewHolder holder, int position) {
        items.get(position).view = holder;
        holder.bind(items.get(position).value);
    }

    @Override
    public int getItemViewType(int position) {
        int type = items.get(position).viewType;
        return type;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public boolean checkTokens()
    {
        return needsRefresh;
    }

    public void setTokens(Token[] tokens)
    {
        populateTokens(tokens);
    }

    public void updateTokenCheck(Token token)
    {
        switch (filterType)
        {
            case FILTER_ASSETS:
                if (!token.isEthereum())
                {
                    updateToken(token);
                }
                break;
            case FILTER_CURRENCY:
                if (token.isEthereum())
                {
                    updateToken(token);
                }
                break;

            default:
                updateToken(token);
                break;
        }
    }

    /**
     * Update a single item in the recycler view
     *
     * @param token
     */
    private void updateToken(Token token)
    {
        checkLiveToken(token);
        boolean updated = false;
        for (int i = 0; i < items.size(); i++)
        {
            Object si = items.get(i);
            if (si instanceof TokenSortedItem)
            {
                TokenSortedItem tsi = (TokenSortedItem)si;
                Token thisToken = tsi.value;
                if (thisToken.getAddress().equals(token.getAddress()))
                {
                    if (token.hasPositiveBalance() || token.isEthereum() || assetService.hasDefinition(token.getAddress()))
                    {
                        tsi.value = token;
                        notifyItemChanged(i, tsi);
                    }
                    else
                    {
                        items.removeItemAt(i);
                        notifyItemRemoved(i);
                        notifyDataSetChanged();
                    }
                    updated = true;
                    break;
                }
            }
        }

        if (!updated && !token.isBad() && (token.hasPositiveBalance() || assetService.hasDefinition(token.getAddress())))
        {
            needsRefresh = true;
        }
    }

    private void populateTokens(Token[] tokens)
    {
        int itemCount = items.size();
        if (needsRefresh)
        {
            items.clear();
        }
        items.beginBatchedUpdates();

        items.add(total);

        for (Token token : tokens)
        {
            if (token != null &&  //Add token to display list if it's the base currency, or if it has balance
                    (token.isEthereum() ||
                            (!token.isTerminated() && !token.isBad() &&
                                    (token.hasPositiveBalance() || assetService.hasDefinition(token.getAddress())))))
            {
                Log.d(TAG,"ADDING: " + token.getFullName());
                checkLiveToken(token);
                switch (filterType)
                {
                    case FILTER_ALL:
                        items.add(new TokenSortedItem(token, calculateWeight(token)));
                        break;
                    case FILTER_ASSETS:
                        if (!token.isEthereum())
                        {
                            items.add(new TokenSortedItem(token, calculateWeight(token)));
                        }
                        break;
                    case FILTER_CURRENCY:
                        if (token.isEthereum())
                        {
                            items.add(new TokenSortedItem(token, calculateWeight(token)));
                        }
                        break;

                    default:
                        items.add(new TokenSortedItem(token, calculateWeight(token)));
                        break;
                }
            }
        }
        items.endBatchedUpdates();

        if (itemCount != items.size())
        {
            if (items.size() < itemCount)
            {
                needsRefresh = true;
            }
        }

        if (needsRefresh)
        {
            needsRefresh = false;
            notifyDataSetChanged();
        }
    }

    private int calculateWeight(Token token)
    {
        int weight = 0;
        String tokenName = token.getFullName();
        if(token.isEthereum()) return 1;
        if(token.isBad()) return Integer.MAX_VALUE;

        int i = 4;
        int pos = 0;

        while (i >= 0 && pos < tokenName.length())
        {
            char c = tokenName.charAt(pos++);
            //Character.isIdeographic()
            int w = tokeniseCharacter(c);
            if (w > 0)
            {
                int component = (int)Math.pow(26, i)*w;
                weight += component;
                i--;
            }
        }

        String address = Numeric.cleanHexPrefix(token.getAddress());
        for (i = 0; i < address.length() && i < 2; i++)
        {
            char c = address.charAt(i);
            int w = c - '0';
            weight += w;
        }

        if (weight < 2) weight = 2;

        return weight;
    }

    private int tokeniseCharacter(char c)
    {
        int w = Character.toLowerCase(c) - 'a' + 1;
        if (w > 'z')
        {
            //could be ideographic, in which case we may want to display this first
            //just use a modulus
            w = w % 10;
        }
        else if (w < 0)
        {
            //must be a number
            w = 1 + (c - '0');
        }
        else
        {
            w += 10;
        }

        return w;
    }

    public void setTotal(BigDecimal totalInCurrency) {
        total = new TotalBalanceSortedItem(totalInCurrency);
        //see if we need an update
        items.beginBatchedUpdates();
        for (int i = 0; i < items.size(); i++)
        {
            Object si = items.get(i);
            if (si instanceof TotalBalanceSortedItem)
            {
                items.remove((TotalBalanceSortedItem)si);
                items.add(total);
                notifyItemChanged(i);
                break;
            }
        }
        items.endBatchedUpdates();
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public void clear()
    {
        items.beginBatchedUpdates();
        items.clear();
        items.endBatchedUpdates();
        Log.d(TAG, "Cleared");
        notifyDataSetChanged();
        needsRefresh = true;
    }

    private void checkLiveToken(Token t)
    {
        t.checkIsMatchedInXML(assetService);
    }

    public void setClear()
    {
        needsRefresh = true;
    }

    public void onRemoveTokens(List<String> tokens)
    {
        List<Integer> removeList = new ArrayList<>();

        for (int i = 0; i < items.size(); i++)
        {
            Object si = items.get(i);
            if (si instanceof TokenSortedItem)
            {
                TokenSortedItem tsi = (TokenSortedItem)si;
                Token thisToken = tsi.value;
                if (tokens.contains(thisToken.getAddress()))
                {
                    removeList.add(i);
                }
            }
        }

        items.beginBatchedUpdates();
        if (!removeList.isEmpty())
        {
            //remove in reverse order so indices stay the same
            for (int i = removeList.size() - 1; i >= 0; i--)
            {
                int index = removeList.get(i);
                TokenSortedItem tsi = (TokenSortedItem)items.get(index);
                items.remove(tsi);
                notifyItemChanged(index);
            }
        }
        items.endBatchedUpdates();
    }
}
