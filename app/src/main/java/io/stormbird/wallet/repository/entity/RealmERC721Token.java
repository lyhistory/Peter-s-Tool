package io.stormbird.wallet.repository.entity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.stormbird.wallet.entity.opensea.Asset;

/**
 * Created by James on 22/10/2018.
 * Stormbird in Singapore
 */
public class RealmERC721Token extends RealmObject
{
    @PrimaryKey
    private String address;
    private String name;
    private String symbol;
    private long addedTime;
    private long updatedTime;
    private String tokenIdList;
    private String schemaName;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(long addedTime) {
        this.addedTime = addedTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public List<String> getTokenIdList()
    {
        String[] list = tokenIdList.split(",");
        List<String> tokens = new ArrayList<>();
        Collections.addAll(tokens, list);
        return tokens;
    }

    public void setTokenIdList(List<Asset> balance) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Asset asset : balance)
        {
            if (!first) sb.append(",");
            sb.append(asset.getTokenId());
            first = false;
        }

        this.tokenIdList = sb.toString();
    }

    public String getSchemaName()
    {
        return schemaName;
    }

    public void setSchemaName(String schemaName)
    {
        this.schemaName = schemaName;
    }
}
