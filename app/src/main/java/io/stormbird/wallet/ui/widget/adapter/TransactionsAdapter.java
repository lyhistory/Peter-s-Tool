package io.stormbird.wallet.ui.widget.adapter;

import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import io.stormbird.wallet.R;
import io.stormbird.wallet.entity.NetworkInfo;
import io.stormbird.wallet.entity.Transaction;
import io.stormbird.wallet.entity.TransactionMeta;
import io.stormbird.wallet.entity.Wallet;
import io.stormbird.wallet.interact.FetchTransactionsInteract;
import io.stormbird.wallet.service.TokensService;
import io.stormbird.wallet.ui.widget.OnTransactionClickListener;
import io.stormbird.wallet.ui.widget.entity.DateSortedItem;
import io.stormbird.wallet.ui.widget.entity.SortedItem;
import io.stormbird.wallet.ui.widget.entity.TimestampSortedItem;
import io.stormbird.wallet.ui.widget.entity.TransactionSortedItem;
import io.stormbird.wallet.ui.widget.holder.BinderViewHolder;
import io.stormbird.wallet.ui.widget.holder.TransactionDateHolder;
import io.stormbird.wallet.ui.widget.holder.TransactionHolder;

import java.util.HashMap;
import java.util.Map;

public class TransactionsAdapter extends RecyclerView.Adapter<BinderViewHolder> {

    private final SortedList<SortedItem> items = new SortedList<>(SortedItem.class, new SortedList.Callback<SortedItem>() {
        @Override
        public int compare(SortedItem left, SortedItem right)
        {
            return left.compare(right);
        }

        @Override
        public boolean areContentsTheSame(SortedItem oldItem, SortedItem newItem) {
            return oldItem.areContentsTheSame(newItem);
        }

        @Override
        public boolean areItemsTheSame(SortedItem left, SortedItem right) {
            return left.areItemsTheSame(right);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
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

    private final OnTransactionClickListener onTransactionClickListener;

    private Wallet wallet;
    private NetworkInfo network;
    private Map<String, TransactionSortedItem> checkMap = new HashMap<>();
    private final TokensService tokensService;
    private final FetchTransactionsInteract fetchTransactionsInteract;

    public TransactionsAdapter(OnTransactionClickListener onTransactionClickListener, TokensService service,
                               FetchTransactionsInteract fetchTransactionsInteract) {
        this.onTransactionClickListener = onTransactionClickListener;
        this.fetchTransactionsInteract = fetchTransactionsInteract;
        tokensService = service;
        setHasStableIds(true);
    }

    @Override
    public BinderViewHolder<?> onCreateViewHolder(ViewGroup parent, int viewType) {
        BinderViewHolder holder = null;
        switch (viewType) {
            case TransactionHolder.VIEW_TYPE: {
                TransactionHolder transactionHolder
                        = new TransactionHolder(R.layout.item_transaction, parent, tokensService, fetchTransactionsInteract);
                transactionHolder.setOnTransactionClickListener(onTransactionClickListener);
                holder = transactionHolder;
            } break;
            case TransactionDateHolder.VIEW_TYPE: {
                holder = new TransactionDateHolder(R.layout.item_transactions_date_head, parent);
            }
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(BinderViewHolder holder, int position) {
        Bundle addition = new Bundle();
        addition.putString(TransactionHolder.DEFAULT_ADDRESS_ADDITIONAL, wallet.address);
        addition.putString(TransactionHolder.DEFAULT_SYMBOL_ADDITIONAL, network.symbol);
        holder.bind(items.get(position).value, addition);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).viewType;
    }

    public void setDefaultWallet(Wallet wallet) {
        this.wallet = wallet;
        notifyDataSetChanged();
    }

    public void setDefaultNetwork(NetworkInfo network) {
        this.network = network;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateTransactions(Transaction[] transactions)
    {
        items.beginBatchedUpdates();

        for (Transaction transaction : transactions)
        {
            TransactionMeta data = new TransactionMeta(transaction.hash, transaction.timeStamp);
            TransactionSortedItem sortedItem = new TransactionSortedItem(
                    TransactionHolder.VIEW_TYPE, data, TimestampSortedItem.DESC);
            items.add(sortedItem);
            items.add(DateSortedItem.round(transaction.timeStamp));
        }

        items.endBatchedUpdates();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }
}
