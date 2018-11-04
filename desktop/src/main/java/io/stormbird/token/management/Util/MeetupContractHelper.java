package io.stormbird.token.management.Util;

import io.stormbird.token.management.contracts.generated.TicketPro;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.List;

public class MeetupContractHelper {
    private Web3j web3j;
    private Credentials credentials;
    private ContractGasProvider contractGasProvider;
    private TicketPro contract;
    public static enum RedeemStatus{
        Redeemed,
        NotRedeemed,
        Unknown
    }
    public MeetupContractHelper(String contractAddress){
        web3j = Web3j.build(new HttpService
                //("https://ropsten.infura.io/v3/3305734c1fad4ad1ab11fd0e9a74059e"));
                ("http://127.0.0.1:8545"));
        credentials =
                Credentials.create("1ff130c55c96dcc7e5ef1faff70c90c9533d18ea320afd0b7b997bf4a87913a2");
        contractGasProvider = new DefaultGasProvider();
        contract = TicketPro.load(contractAddress,web3j,credentials,contractGasProvider);
    }
    public RedeemStatus checkSpawnableTokenRedeemStatus(BigInteger tokenID){
        try {
            boolean result=contract.spawned(tokenID).send();
            if(result){
                return RedeemStatus.Redeemed;
            }else{
                return RedeemStatus.NotRedeemed;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RedeemStatus.Unknown;
        }
    }

    public void redeemSpawnableToken(BigInteger expiry, List<BigInteger> tickets, byte v, byte[] r, byte[] s, String recipient){
        try {
            byte[] _v = new byte[] {0, v};
            BigInteger biV = new BigInteger(_v);

            TransactionReceipt transactionReceipt =  contract.spawnPassTo(expiry,tickets,biV,r,s,recipient).send();
            boolean spawned = contract.spawned(tickets.get(0)).send();
            String getTheContractAddress =  contract.getTheContractAddress().send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
