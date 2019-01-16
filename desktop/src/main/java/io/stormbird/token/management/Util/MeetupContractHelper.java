package io.stormbird.token.management.Util;

import io.stormbird.token.management.contracts.generated.Meetup;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class MeetupContractHelper {
    private Web3j web3j;
    private Credentials credentials;
    private ContractGasProvider contractGasProvider;
    private Meetup contract;
    public static enum RedeemStatus{
        Redeemed,
        NotRedeemed,
        Unknown
    }
    public static boolean connected=false;
    public String contractOwner;
    public boolean isConnected;
    private static boolean pingEndPoint(String host, int port, int timeout){
        try(Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(host,port),timeout);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public MeetupContractHelper(String contractAddress,String networkid,String privateKey,String ownerAddress){
        String host,port,url;
        switch (networkid){
            case "1":
                host="https://mainnet.infura.io/v3/3305734c1fad4ad1ab11fd0e9a74059e";
                port="80";
                break;
            case "3":
                host="https://ropsten.infura.io/v3/3305734c1fad4ad1ab11fd0e9a74059e";
                port="80";
                break;
            default:
                host="http://127.0.0.1";
                port="8545";
                break;
        }
        if(port.equals("80")){
            url=host;
        }else{
            url=host+":"+port;
        }
//        new Thread(()->{
//            connected = pingEndPoint(host, Integer.valueOf(port),3);
//        }).start();
        try {
            web3j = Web3j.build(new HttpService(url));
            contractGasProvider = new DefaultGasProvider();
            if (privateKey == null || privateKey.isEmpty()) {
                TransactionManager transactionManager = new ClientTransactionManager(web3j, null);
                contract = Meetup.load(contractAddress, web3j, transactionManager, contractGasProvider);
            } else {
                credentials = Credentials.create(privateKey);
                contract = Meetup.load(contractAddress, web3j, credentials, contractGasProvider);
            }
            isConnected=true;
        }catch (Exception ex){
            isConnected=false;
        }
        try {
            contractOwner = contract.organiser().send();
        }catch (Exception ex){
            isConnected=false;
        }

    }
    public RedeemStatus checkSpawnableTokenRedeemStatus(BigInteger tokenID){
        try {
            boolean result=contract.spawned(tokenID).send();
            if(result){
                return RedeemStatus.Redeemed;
            }else{
                return RedeemStatus.NotRedeemed;
            }
        }catch (ConnectException e){
            connected = false;
            e.printStackTrace();
            return RedeemStatus.Unknown;
        }catch (Exception e) {
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
            String getTheContractAddress =  contract.getThisContractAddress().send();
        }catch (ConnectException e){
            connected = false;
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
