package io.stormbird.token.management.contracts.generated;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.6.0.
 */
public class TicketPro extends Contract {
    private static final String BINARY = "608060408190526000805461ffff19168155600655600d80547f737061776e50617373546f2875696e743235362c75696e743235365b5d2c75699092527f6e74382c627974657333322c627974657333322c61646472657373290000000060a05263ffffffff199091166370a082311767ffffffff00000000191667696ecc550000000017604060020a63ffffffff0219166bedd5ede6000000000000000017606060020a63ffffffff0219166f4bd13cdd000000000000000000000000179055348015620000cd57600080fd5b5060405162001c9838038062001c988339810160409081528151602080840151838501516060860151608087015160a088015160c089015160e08a01516101008b015160048054600160a060020a03808b16600160a060020a03199283161790925560058054838b1692169190911790558616600090815260018a529a909a20988b018051909b979a969995989488019793840196928401959184019492909301926200017d928c0190620001fb565b5084516200019390600a9060208801906200024b565b508351620001a990600b9060208701906200024b565b508251620001bf9060099060208601906200024b565b508151620001d590600c9060208501906200024b565b508051620001eb9060079060208401906200024b565b50505050505050505050620002dd565b82805482825590600052602060002090810192821562000239579160200282015b82811115620002395782518255916020019190600101906200021c565b5062000247929150620002bd565b5090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200028e57805160ff191683800117855562000239565b82800160010185558215620002395791820182811115620002395782518255916020019190600101906200021c565b620002da91905b80821115620002475760008155600101620002c4565b90565b6119ab80620002ed6000396000f30060806040526004361061010e5763ffffffff60e060020a60003504166301ffc9a7811461012057806306fdde031461015657806315070401146101e05780631bb75082146101f557806322f8dea51461020d578063313ce5671461022757806340c1b7ad1461025257806344c9af281461026a5780634bd13cdd146102825780634f452b9a146102f9578063696ecc551461030e57806370a082311461036a57806372c5cb63146103db578063758e53aa146104025780639dfd8b021461041a578063a6fb475f14610432578063bb6e7de91461049d578063c9116b69146104b2578063cf0b41a3146104c7578063db0ec9681461051c578063edd5ede61461057f578063f0258e0c146105f6575b34801561011a57600080fd5b50600080fd5b34801561012c57600080fd5b50610142600160e060020a031960043516610627565b604080519115158252519081900360200190f35b34801561016257600080fd5b5061016b6106dd565b6040805160208082528351818301528351919283929083019185019080838360005b838110156101a557818101518382015260200161018d565b50505050905090810190601f1680156101d25780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156101ec57600080fd5b5061016b61076b565b34801561020157600080fd5b50610142600435610801565b34801561021957600080fd5b5061022560043561084c565b005b34801561023357600080fd5b5061023c610873565b6040805160ff9092168252519081900360200190f35b34801561025e57600080fd5b5061016b600435610878565b34801561027657600080fd5b5061016b600435610910565b34801561028e57600080fd5b50604080516020600460248035828101358481028087018601909752808652610225968435963696604495919490910192918291850190849080828437509497505050833560ff16945050506020820135916040810135915060600135600160a060020a0316610971565b34801561030557600080fd5b50610142610ad1565b604080516020600460248035828101358481028087018601909752808652610225968435963696604495919490910192918291850190849080828437509497505050833560ff1694505050602082013591604001359050610ad6565b34801561037657600080fd5b5061038b600160a060020a0360043516610d38565b60408051602080825283518183015283519192839290830191858101910280838360005b838110156103c75781810151838201526020016103af565b505050509050019250505060405180910390f35b3480156103e757600080fd5b506103f0610da3565b60408051918252519081900360200190f35b34801561040e57600080fd5b50610142600435610da9565b34801561042657600080fd5b5061016b600435610daf565b34801561043e57600080fd5b506040805160206004604435818101358381028086018501909652808552610225958335600160a060020a039081169660248035909216963696956064959294930192829185019084908082843750949750610e109650505050505050565b3480156104a957600080fd5b50610225610f67565b3480156104be57600080fd5b5061038b610f8c565b3480156104d357600080fd5b506040805160206004803580820135838102808601850190965280855261022595369593946024949385019291829185019084908082843750949750610fec9650505050505050565b34801561052857600080fd5b50604080516020600460248035828101358481028087018601909752808652610225968435600160a060020a0316963696604495919490910192918291850190849080828437509497506110699650505050505050565b34801561058b57600080fd5b50604080516020600460248035828101358481028087018601909752808652610225968435963696604495919490910192918291850190849080828437509497505050833560ff16945050506020820135916040810135915060600135600160a060020a0316611184565b34801561060257600080fd5b5061060b6113d5565b60408051600160a060020a039092168252519081900360200190f35b600d54600090600160e060020a031983811660e060020a90920216148061066b5750600d54640100000000900460e060020a02600160e060020a0319908116908316145b806106975750600d5468010000000000000000900460e060020a02600160e060020a0319908116908316145b806106c75750600d546c01000000000000000000000000900460e060020a02600160e060020a0319908116908316145b156106d4575060016106d8565b5060005b919050565b6007805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156107635780601f1061073857610100808354040283529160200191610763565b820191906000526020600020905b81548152906001019060200180831161074657829003601f168201915b505050505081565b600c8054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156107f75780601f106107cc576101008083540402835291602001916107f7565b820191906000526020600020905b8154815290600101906020018083116107da57829003601f168201915b5050505050905090565b6000805b600254811015610841578260028281548110151561081f57fe5b906000526020600020015414156108395760019150610846565b600101610805565b600091505b50919050565b600454600160a060020a0316331461086357600080fd5b506008805460ff19166001179055565b600081565b600a8054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156109045780601f106108d957610100808354040283529160200191610904565b820191906000526020600020905b8154815290600101906020018083116108e757829003601f168201915b50505050509050919050565b60098054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156109045780601f106108d957610100808354040283529160200191610904565b600080600042891180610982575088155b151561098d57600080fd5b61099960008a8a6113d9565b604080516000808252602080830180855285905260ff8c1683850152606083018b9052608083018a9052925193965060019360a08084019493601f19830193908390039091019190865af11580156109f5573d6000803e3d6000fd5b5050604051601f190151600454909350600160a060020a038085169116149050610a1e57600080fd5b60008581526003602052604090205460ff1615610a3a57600080fd5b5060005b8751811015610ac657600160a060020a03841660009081526001602052604090208851899083908110610a6d57fe5b6020908102909101810151825460018101845560009384529190922001558751600290899083908110610a9c57fe5b60209081029091018101518254600181810185556000948552929093209092019190915501610a3e565b505050505050505050565b600190565b60008060008042891180610ae8575088155b1515610af357600080fd5b610afe348a8a611723565b604080516000808252602080830180855285905260ff8c1683850152606083018b9052608083018a9052925193975060019360a08084019493601f19830193908390039091019190865af1158015610b5a573d6000803e3d6000fd5b505050602060405103519250600091505b8751821015610c55578782815181101515610b8257fe5b6020908102909101810151600160a060020a038516600090815260019092526040822080549193509061ffff8416908110610bb957fe5b906000526020600020015414151515610bce57fe5b33600090815260016020526040808220600160a060020a03861683529120805461ffff8416908110610bfc57fe5b60009182526020808320909101548354600181810186559484528284200155600160a060020a03861682529190915260409020805461ffff8316908110610c3f57fe5b6000918252602082200155600190910190610b6b565b604051600160a060020a038416903480156108fc02916000818181858888f19350505050158015610c8a573d6000803e3d6000fd5b506040805160ff89166020808301919091529181018890526060810187905260808082528a51908201528951600160a060020a038616927fec67368df72865aef2c3748b4627cbcc0b539079709e3a6fbcaea909f4c68353928c928c928c928c929091829160a0830191808901910280838360005b83811015610d17578181015183820152602001610cff565b505050509050019550505050505060405180910390a2505050505050505050565b600160a060020a03811660009081526001602090815260409182902080548351818402810184019094528084526060939283018282801561090457602002820191906000526020600020905b815481526020019060010190808311610d845750505050509050919050565b60065490565b50600190565b600b8054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156109045780601f106108d957610100808354040283529160200191610904565b6004546000908190600160a060020a03163314610e2c57600080fd5b600091505b8251821015610f14578282815181101515610e4857fe5b6020908102909101810151600160a060020a0387166000908152600190925260408220805461ffff90921693509083908110610e8057fe5b906000526020600020015414151515610e9557fe5b600160a060020a0384166000908152600160205260408082203383529120805483908110610ebf57fe5b60009182526020808320909101548354600181810186559484528284200155600160a060020a03881682529190915260409020805482908110610efe57fe5b6000918252602082200155600190910190610e31565b83600160a060020a031685600160a060020a03167fc0d84ce5c7ff9ca21adb0f8436ff3f4951b4bb78c4e2fae2b6837958b3946ffd85516040518082815260200191505060405180910390a35050505050565b600454600160a060020a03163314610f7e57600080fd5b600454600160a060020a0316ff5b336000908152600160209081526040918290208054835181840281018401909452808452606093928301828280156107f757602002820191906000526020600020905b815481526020019060010190808311610fcf575050505050905090565b600454600090600160a060020a0316331461100657600080fd5b5060005b815181101561106557600454600160a060020a03166000908152600160205260409020825183908390811061103b57fe5b6020908102909101810151825460018181018555600094855292909320909201919091550161100a565b5050565b6000805b825182101561113e57828281518110151561108457fe5b6020908102909101810151336000908152600190925260408220805461ffff909216935090839081106110b357fe5b9060005260206000200154141515156110c857fe5b600160a060020a03841660009081526001602052604080822033835291208054839081106110f257fe5b60009182526020808320909101548354600181810186559484528284200155338252919091526040902080548290811061112857fe5b600091825260208220015560019091019061106d565b82516040805191825251600160a060020a038616917f69ca02dd4edd7bf0a4abb9ed3b7af3f14778db5d61921c7dc7cd545266326de2919081900360200190a250505050565b6005546000908190819081908190600160a060020a031633146111a657600080fd5b428b11806111b257508a155b15156111bd57600080fd5b6111c960008c8c611723565b604080516000808252602080830180855285905260ff8e1683850152606083018d9052608083018c9052925193985060019360a08084019493601f19830193908390039091019190865af1158015611225573d6000803e3d6000fd5b505050602060405103519350600092505b895183101561132657898381518110151561124d57fe5b6020908102909101810151600160a060020a038616600090815260019092526040822080549194509061ffff851690811061128457fe5b90600052602060002001541415151561129957fe5b600160a060020a0384166000908152600160205260409020805461ffff84169081106112c157fe5b6000918252602080832090910154600160a060020a03808a168452600180845260408086208054928301815586529385200182905587168352912080549192509061ffff841690811061131057fe5b6000918252602082200155600190920191611236565b6040805160ff8b166020808301919091529181018a90526060810189905260808082528c51908201528b51600160a060020a038916927f4490f02c64b562efdc9d14a196182a17381fdb62142db3a2117519102a151ea9928e928e928e928e929091829160a0830191808901910280838360005b838110156113b257818101518382015260200161139a565b505050509050019550505050505060405180910390a25050505050505050505050565b3090565b600060606000806060600086516020026054016040519080825280601f01601f191660200182016040528015611419578160200160208202803883390190505b5094506114246113d5565b9350600092505b602083101561146f5784516008840260020a8a029086908590811061144c57fe5b906020010190600160f860020a031916908160001a90535060019092019161142b565b600092505b60208310156114bb5784516008840260020a89029086906020860190811061149857fe5b906020010190600160f860020a031916908160001a905350600190920191611474565b600092505b60148310156115255784516008840260020a6bffffffffffffffffffffffff196c01000000000000000000000000870216029086906040860190811061150257fe5b906020010190600160f860020a031916908160001a9053506001909201916114c0565b8651604051908082528060200260200182016040528015611550578160200160208202803883390190505b509150600092505b865183101561167157868381518110151561156f57fe5b90602001906020020151828481518110151561158757fe5b60209081029091010152865187908490811061159f57fe5b9060200190602002015160f860020a028584602002605401601f018151811015156115c657fe5b906020010190600160f860020a031916908160001a905350600190505b602081101561166657600887848151811015156115fc57fe5b906020019060200201519060020a9004878481518110151561161a57fe5b9060200190602002018181525060f860020a02858285602002605401601f010381518110151561164657fe5b906020010190600160f860020a031916908160001a9053506001016115e3565b600190920191611558565b600092505b81518310156116ba57818381518110151561168d57fe5b9060200190602002015187848151811015156116a557fe5b60209081029091010152600190920191611676565b846040518082805190602001908083835b602083106116ea5780518252601f1990920191602091820191016116cb565b5181516020939093036101000a600019018019909116921691909117905260405192018290039091209c9b505050505050505050505050565b6000606060008084516002026054016040519080825280601f01601f19166020018201604052801561175f578160200160208202803883390190505b50925061176a6113d5565b9150600090505b60208110156117b25782516008820260020a88029084908390811061179257fe5b906020010190600160f860020a031916908160001a905350600101611771565b5060005b60208110156117fa5782516008820260020a8702908490602084019081106117da57fe5b906020010190600160f860020a031916908160001a9053506001016117b6565b5060005b60148110156118605782516008820260020a6bffffffffffffffffffffffff196c01000000000000000000000000850216029084906040840190811061184057fe5b906020010190600160f860020a031916908160001a9053506001016117fe565b5060005b8451811015611918576008858281518110151561187d57fe5b9060200190602002015161ffff169060020a900460f860020a0283826002026054018151811015156118ab57fe5b906020010190600160f860020a031916908160001a90535084818151811015156118d157fe5b9060200190602002015160f860020a0283826002026054016001018151811015156118f857fe5b906020010190600160f860020a031916908160001a905350600101611864565b826040518082805190602001908083835b602083106119485780518252601f199092019160209182019101611929565b5181516020939093036101000a600019018019909116921691909117905260405192018290039091209a99505050505050505050505600a165627a7a7230582013a2c54c8e46cf9b06b4d571b2044e22e11d6ee89457a71a0e1468de8ac972310029";

    public static final String FUNC_SUPPORTSINTERFACE = "supportsInterface";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_GETSYMBOL = "getSymbol";

    public static final String FUNC_SPAWNED = "spawned";

    public static final String FUNC_SETEXPIRED = "setExpired";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_GETSTREET = "getStreet";

    public static final String FUNC_GETSTATE = "getState";

    public static final String FUNC_SPAWNPASSTO = "spawnPassTo";

    public static final String FUNC_ISSTORMBIRDCONTRACT = "isStormBirdContract";

    public static final String FUNC_TRADE = "trade";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_GETAMOUNTTRANSFERRED = "getAmountTransferred";

    public static final String FUNC_CHECKEXPIRED = "checkExpired";

    public static final String FUNC_GETBUILDING = "getBuilding";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_ENDCONTRACT = "endContract";

    public static final String FUNC_MYBALANCE = "myBalance";

    public static final String FUNC_LOADNEWTICKETS = "loadNewTickets";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_PASSTO = "passTo";

    public static final String FUNC_GETTHECONTRACTADDRESS = "getTheContractAddress";

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFERFROM_EVENT = new Event("TransferFrom", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRADE_EVENT = new Event("Trade", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<DynamicArray<Uint16>>() {}, new TypeReference<Uint8>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}));
    ;

    public static final Event PASSTO_EVENT = new Event("PassTo", 
            Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint16>>() {}, new TypeReference<Uint8>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected TicketPro(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected TicketPro(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected TicketPro(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected TicketPro(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<Boolean> supportsInterface(byte[] interfaceID) {
        final Function function = new Function(FUNC_SUPPORTSINTERFACE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes4(interfaceID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getSymbol() {
        final Function function = new Function(FUNC_GETSYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Boolean> spawned(BigInteger ticket) {
        final Function function = new Function(FUNC_SPAWNED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(ticket)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> setExpired(BigInteger tokenId) {
        final Function function = new Function(
                FUNC_SETEXPIRED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> getStreet(BigInteger tokenId) {
        final Function function = new Function(FUNC_GETSTREET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getState(BigInteger tokenId) {
        final Function function = new Function(FUNC_GETSTATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> spawnPassTo(BigInteger expiry, List<BigInteger> tickets, BigInteger v, byte[] r, byte[] s, String recipient) {
        final Function function = new Function(
                FUNC_SPAWNPASSTO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(expiry), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(tickets, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s), 
                new org.web3j.abi.datatypes.Address(recipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> isStormBirdContract() {
        final Function function = new Function(FUNC_ISSTORMBIRDCONTRACT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> trade(BigInteger expiry, List<BigInteger> ticketIndices, BigInteger v, byte[] r, byte[] s, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_TRADE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(expiry), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint16>(
                        org.web3j.abi.Utils.typeMap(ticketIndices, org.web3j.abi.datatypes.generated.Uint16.class)), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<List> balanceOf(String _owner) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteCall<List>(
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteCall<BigInteger> getAmountTransferred() {
        final Function function = new Function(FUNC_GETAMOUNTTRANSFERRED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> checkExpired(BigInteger tokenId) {
        final Function function = new Function(FUNC_CHECKEXPIRED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> getBuilding(BigInteger tokenId) {
        final Function function = new Function(FUNC_GETBUILDING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, List<BigInteger> ticketIndices) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from), 
                new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint16>(
                        org.web3j.abi.Utils.typeMap(ticketIndices, org.web3j.abi.datatypes.generated.Uint16.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> endContract() {
        final Function function = new Function(
                FUNC_ENDCONTRACT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<List> myBalance() {
        final Function function = new Function(FUNC_MYBALANCE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteCall<List>(
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteCall<TransactionReceipt> loadNewTickets(List<BigInteger> tickets) {
        final Function function = new Function(
                FUNC_LOADNEWTICKETS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(tickets, org.web3j.abi.datatypes.generated.Uint256.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, List<BigInteger> ticketIndices) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint16>(
                        org.web3j.abi.Utils.typeMap(ticketIndices, org.web3j.abi.datatypes.generated.Uint16.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> passTo(BigInteger expiry, List<BigInteger> ticketIndices, BigInteger v, byte[] r, byte[] s, String recipient) {
        final Function function = new Function(
                FUNC_PASSTO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(expiry), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint16>(
                        org.web3j.abi.Utils.typeMap(ticketIndices, org.web3j.abi.datatypes.generated.Uint16.class)), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s), 
                new org.web3j.abi.datatypes.Address(recipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> getTheContractAddress() {
        final Function function = new Function(FUNC_GETTHECONTRACTADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<TicketPro> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, List<BigInteger> tickets, String organiserAddr, String paymasterAddr, String recipientAddr, String streetName, String buildingName, String stateName, String symbolName, String contractName) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(tickets, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.Address(organiserAddr), 
                new org.web3j.abi.datatypes.Address(paymasterAddr), 
                new org.web3j.abi.datatypes.Address(recipientAddr), 
                new org.web3j.abi.datatypes.Utf8String(streetName), 
                new org.web3j.abi.datatypes.Utf8String(buildingName), 
                new org.web3j.abi.datatypes.Utf8String(stateName), 
                new org.web3j.abi.datatypes.Utf8String(symbolName), 
                new org.web3j.abi.datatypes.Utf8String(contractName)));
        return deployRemoteCall(TicketPro.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<TicketPro> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, List<BigInteger> tickets, String organiserAddr, String paymasterAddr, String recipientAddr, String streetName, String buildingName, String stateName, String symbolName, String contractName) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(tickets, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.Address(organiserAddr), 
                new org.web3j.abi.datatypes.Address(paymasterAddr), 
                new org.web3j.abi.datatypes.Address(recipientAddr), 
                new org.web3j.abi.datatypes.Utf8String(streetName), 
                new org.web3j.abi.datatypes.Utf8String(buildingName), 
                new org.web3j.abi.datatypes.Utf8String(stateName), 
                new org.web3j.abi.datatypes.Utf8String(symbolName), 
                new org.web3j.abi.datatypes.Utf8String(contractName)));
        return deployRemoteCall(TicketPro.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<TicketPro> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, List<BigInteger> tickets, String organiserAddr, String paymasterAddr, String recipientAddr, String streetName, String buildingName, String stateName, String symbolName, String contractName) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(tickets, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.Address(organiserAddr), 
                new org.web3j.abi.datatypes.Address(paymasterAddr), 
                new org.web3j.abi.datatypes.Address(recipientAddr), 
                new org.web3j.abi.datatypes.Utf8String(streetName), 
                new org.web3j.abi.datatypes.Utf8String(buildingName), 
                new org.web3j.abi.datatypes.Utf8String(stateName), 
                new org.web3j.abi.datatypes.Utf8String(symbolName), 
                new org.web3j.abi.datatypes.Utf8String(contractName)));
        return deployRemoteCall(TicketPro.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<TicketPro> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, List<BigInteger> tickets, String organiserAddr, String paymasterAddr, String recipientAddr, String streetName, String buildingName, String stateName, String symbolName, String contractName) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(tickets, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.Address(organiserAddr), 
                new org.web3j.abi.datatypes.Address(paymasterAddr), 
                new org.web3j.abi.datatypes.Address(recipientAddr), 
                new org.web3j.abi.datatypes.Utf8String(streetName), 
                new org.web3j.abi.datatypes.Utf8String(buildingName), 
                new org.web3j.abi.datatypes.Utf8String(stateName), 
                new org.web3j.abi.datatypes.Utf8String(symbolName), 
                new org.web3j.abi.datatypes.Utf8String(contractName)));
        return deployRemoteCall(TicketPro.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.count = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse._to = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.count = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventObservable(filter);
    }

    public List<TransferFromEventResponse> getTransferFromEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFERFROM_EVENT, transactionReceipt);
        ArrayList<TransferFromEventResponse> responses = new ArrayList<TransferFromEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferFromEventResponse typedResponse = new TransferFromEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.count = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferFromEventResponse> transferFromEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferFromEventResponse>() {
            @Override
            public TransferFromEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFERFROM_EVENT, log);
                TransferFromEventResponse typedResponse = new TransferFromEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.count = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferFromEventResponse> transferFromEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFERFROM_EVENT));
        return transferFromEventObservable(filter);
    }

    public List<TradeEventResponse> getTradeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRADE_EVENT, transactionReceipt);
        ArrayList<TradeEventResponse> responses = new ArrayList<TradeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TradeEventResponse typedResponse = new TradeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.seller = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.ticketIndices = (List<BigInteger>) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.v = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.r = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.s = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TradeEventResponse> tradeEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TradeEventResponse>() {
            @Override
            public TradeEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRADE_EVENT, log);
                TradeEventResponse typedResponse = new TradeEventResponse();
                typedResponse.log = log;
                typedResponse.seller = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.ticketIndices = (List<BigInteger>) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.v = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.r = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.s = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TradeEventResponse> tradeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRADE_EVENT));
        return tradeEventObservable(filter);
    }

    public List<PassToEventResponse> getPassToEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PASSTO_EVENT, transactionReceipt);
        ArrayList<PassToEventResponse> responses = new ArrayList<PassToEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PassToEventResponse typedResponse = new PassToEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.recipient = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.ticketIndices = (List<BigInteger>) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.v = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.r = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.s = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PassToEventResponse> passToEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, PassToEventResponse>() {
            @Override
            public PassToEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PASSTO_EVENT, log);
                PassToEventResponse typedResponse = new PassToEventResponse();
                typedResponse.log = log;
                typedResponse.recipient = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.ticketIndices = (List<BigInteger>) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.v = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.r = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.s = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<PassToEventResponse> passToEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PASSTO_EVENT));
        return passToEventObservable(filter);
    }

    @Deprecated
    public static TicketPro load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TicketPro(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static TicketPro load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TicketPro(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static TicketPro load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new TicketPro(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static TicketPro load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new TicketPro(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class TransferEventResponse {
        public Log log;

        public String _to;

        public BigInteger count;
    }

    public static class TransferFromEventResponse {
        public Log log;

        public String _from;

        public String _to;

        public BigInteger count;
    }

    public static class TradeEventResponse {
        public Log log;

        public String seller;

        public List<BigInteger> ticketIndices;

        public BigInteger v;

        public byte[] r;

        public byte[] s;
    }

    public static class PassToEventResponse {
        public Log log;

        public String recipient;

        public List<BigInteger> ticketIndices;

        public BigInteger v;

        public byte[] r;

        public byte[] s;
    }
}
