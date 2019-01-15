package io.stormbird.wallet.entity;

/**
 * Created by James on 6/12/2018.
 * Stormbird in Singapore
 */
public enum ContractType
{
    NOT_SET,
    ETHEREUM,
    ERC20,
    ERC721,
    ERC875LEGACY,
    ERC875,
    OTHER,
    CREATION //Placeholder for generic, should be at end of list
};
