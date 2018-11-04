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
import org.web3j.abi.datatypes.DynamicBytes;
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
    private static final String BINARY = "608060408190526000805461ffff19168155600655600d80547f737061776e50617373546f2875696e743235362c75696e743235365b5d2c75699092527f6e74382c627974657333322c627974657333322c61646472657373290000000060a05263ffffffff199091166370a082311767ffffffff00000000191667696ecc550000000017604060020a63ffffffff0219166bedd5ede6000000000000000017606060020a63ffffffff0219166f4bd13cdd000000000000000000000000179055348015620000cd57600080fd5b50604051620020ef380380620020ef8339810160409081528151602080840151838501516060860151608087015160a088015160c089015160e08a01516101008b015160048054600160a060020a03808b16600160a060020a03199283161790925560058054838b1692169190911790558616600090815260018a529a909a20988b018051909b979a969995989488019793840196928401959184019492909301926200017d928c0190620001fb565b5084516200019390600a9060208801906200024b565b508351620001a990600b9060208701906200024b565b508251620001bf9060099060208601906200024b565b508151620001d590600c9060208501906200024b565b508051620001eb9060079060208401906200024b565b50505050505050505050620002dd565b82805482825590600052602060002090810192821562000239579160200282015b82811115620002395782518255916020019190600101906200021c565b5062000247929150620002bd565b5090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200028e57805160ff191683800117855562000239565b82800160010185558215620002395791820182811115620002395782518255916020019190600101906200021c565b620002da91905b80821115620002475760008155600101620002c4565b90565b611e0280620002ed6000396000f30060806040526004361061013a5763ffffffff60e060020a60003504166301ffc9a7811461014c57806306fdde0314610182578063150704011461020c5780631bb750821461022157806322f8dea51461023957806329c908fc14610253578063313ce567146102e657806340c1b7ad1461031157806344c9af28146103295780634bd13cdd146103415780634f452b9a146103b8578063696ecc55146103cd57806370a082311461042957806372c5cb631461049a578063758e53aa146104c157806379af7bce146104d95780639dfd8b0214610537578063a6fb475f1461054f578063a7fc9dcb146105ba578063bb6e7de9146105d2578063c299611c146105e7578063c9116b691461065e578063cf0b41a314610673578063db0ec968146106c8578063edd5ede61461072b578063f0258e0c146107a2575b34801561014657600080fd5b50600080fd5b34801561015857600080fd5b5061016e600160e060020a0319600435166107b7565b604080519115158252519081900360200190f35b34801561018e57600080fd5b5061019761086d565b6040805160208082528351818301528351919283929083019185019080838360005b838110156101d15781810151838201526020016101b9565b50505050905090810190601f1680156101fe5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561021857600080fd5b506101976108fb565b34801561022d57600080fd5b5061016e600435610991565b34801561024557600080fd5b506102516004356109dc565b005b34801561025f57600080fd5b506040805160206004602480358281013584810280870186019097528086526102ca968435963696604495919490910192918291850190849080828437509497505050833560ff16945050506020820135916040810135915060600135600160a060020a0316610a03565b60408051600160a060020a039092168252519081900360200190f35b3480156102f257600080fd5b506102fb610a88565b6040805160ff9092168252519081900360200190f35b34801561031d57600080fd5b50610197600435610a8d565b34801561033557600080fd5b50610197600435610b25565b34801561034d57600080fd5b50604080516020600460248035828101358481028087018601909752808652610251968435963696604495919490910192918291850190849080828437509497505050833560ff16945050506020820135916040810135915060600135600160a060020a0316610b86565b3480156103c457600080fd5b5061016e610ce6565b604080516020600460248035828101358481028087018601909752808652610251968435963696604495919490910192918291850190849080828437509497505050833560ff1694505050602082013591604001359050610ceb565b34801561043557600080fd5b5061044a600160a060020a0360043516610f4d565b60408051602080825283518183015283519192839290830191858101910280838360005b8381101561048657818101518382015260200161046e565b505050509050019250505060405180910390f35b3480156104a657600080fd5b506104af610fb8565b60408051918252519081900360200190f35b3480156104cd57600080fd5b5061016e600435610fbe565b3480156104e557600080fd5b506040805160206004604435818101358381028086018501909652808552610197958335956024803596369695606495939492019291829185019084908082843750949750610fc49650505050505050565b34801561054357600080fd5b50610197600435611003565b34801561055b57600080fd5b506040805160206004604435818101358381028086018501909652808552610251958335600160a060020a0390811696602480359092169636969560649592949301928291850190849080828437509497506110649650505050505050565b3480156105c657600080fd5b506104af6004356111bb565b3480156105de57600080fd5b506102516111de565b3480156105f357600080fd5b506040805160206004602480358281013584810280870186019097528086526104af968435963696604495919490910192918291850190849080828437509497505050833560ff16945050506020820135916040810135915060600135600160a060020a0316611203565b34801561066a57600080fd5b5061044a6113e3565b34801561067f57600080fd5b5060408051602060048035808201358381028086018501909652808552610251953695939460249493850192918291850190849080828437509497506114439650505050505050565b3480156106d457600080fd5b50604080516020600460248035828101358481028087018601909752808652610251968435600160a060020a0316963696604495919490910192918291850190849080828437509497506114c09650505050505050565b34801561073757600080fd5b50604080516020600460248035828101358481028087018601909752808652610251968435963696604495919490910192918291850190849080828437509497505050833560ff16945050506020820135916040810135915060600135600160a060020a03166115db565b3480156107ae57600080fd5b506102ca61182c565b600d54600090600160e060020a031983811660e060020a9092021614806107fb5750600d54640100000000900460e060020a02600160e060020a0319908116908316145b806108275750600d5468010000000000000000900460e060020a02600160e060020a0319908116908316145b806108575750600d546c01000000000000000000000000900460e060020a02600160e060020a0319908116908316145b1561086457506001610868565b5060005b919050565b6007805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156108f35780601f106108c8576101008083540402835291602001916108f3565b820191906000526020600020905b8154815290600101906020018083116108d657829003601f168201915b505050505081565b600c8054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156109875780601f1061095c57610100808354040283529160200191610987565b820191906000526020600020905b81548152906001019060200180831161096a57829003601f168201915b5050505050905090565b6000805b6002548110156109d157826002828154811015156109af57fe5b906000526020600020015414156109c957600191506109d6565b600101610995565b600091505b50919050565b600454600160a060020a031633146109f357600080fd5b506008805460ff19166001179055565b6000806000610a1460008a8a611830565b604080516000808252602080830180855285905260ff8c1683850152606083018b9052608083018a9052925193955060019360a08084019493601f19830193908390039091019190865af1158015610a70573d6000803e3d6000fd5b5050604051601f1901519a9950505050505050505050565b600081565b600a8054604080516020601f6002600019610100600188161502019095169490940493840181900481028201810190925282815260609390929091830182828015610b195780601f10610aee57610100808354040283529160200191610b19565b820191906000526020600020905b815481529060010190602001808311610afc57829003601f168201915b50505050509050919050565b60098054604080516020601f6002600019610100600188161502019095169490940493840181900481028201810190925282815260609390929091830182828015610b195780601f10610aee57610100808354040283529160200191610b19565b600080600042891180610b97575088155b1515610ba257600080fd5b610bae60008a8a611830565b604080516000808252602080830180855285905260ff8c1683850152606083018b9052608083018a9052925193965060019360a08084019493601f19830193908390039091019190865af1158015610c0a573d6000803e3d6000fd5b5050604051601f190151600454909350600160a060020a038085169116149050610c3357600080fd5b60008581526003602052604090205460ff1615610c4f57600080fd5b5060005b8751811015610cdb57600160a060020a03841660009081526001602052604090208851899083908110610c8257fe5b6020908102909101810151825460018101845560009384529190922001558751600290899083908110610cb157fe5b60209081029091018101518254600181810185556000948552929093209092019190915501610c53565b505050505050505050565b600190565b60008060008042891180610cfd575088155b1515610d0857600080fd5b610d13348a8a611b7a565b604080516000808252602080830180855285905260ff8c1683850152606083018b9052608083018a9052925193975060019360a08084019493601f19830193908390039091019190865af1158015610d6f573d6000803e3d6000fd5b505050602060405103519250600091505b8751821015610e6a578782815181101515610d9757fe5b6020908102909101810151600160a060020a038516600090815260019092526040822080549193509061ffff8416908110610dce57fe5b906000526020600020015414151515610de357fe5b33600090815260016020526040808220600160a060020a03861683529120805461ffff8416908110610e1157fe5b60009182526020808320909101548354600181810186559484528284200155600160a060020a03861682529190915260409020805461ffff8316908110610e5457fe5b6000918252602082200155600190910190610d80565b604051600160a060020a038416903480156108fc02916000818181858888f19350505050158015610e9f573d6000803e3d6000fd5b506040805160ff89166020808301919091529181018890526060810187905260808082528a51908201528951600160a060020a038616927fec67368df72865aef2c3748b4627cbcc0b539079709e3a6fbcaea909f4c68353928c928c928c928c929091829160a0830191808901910280838360005b83811015610f2c578181015183820152602001610f14565b505050509050019550505050505060405180910390a2505050505050505050565b600160a060020a038116600090815260016020908152604091829020805483518184028101840190945280845260609392830182828015610b1957602002820191906000526020600020905b815481526020019060010190808311610f995750505050509050919050565b60065490565b50600190565b60608082516020026040519080825280601f01601f191660200182016040528015610ff9578160200160208202803883390190505b5095945050505050565b600b8054604080516020601f6002600019610100600188161502019095169490940493840181900481028201810190925282815260609390929091830182828015610b195780601f10610aee57610100808354040283529160200191610b19565b6004546000908190600160a060020a0316331461108057600080fd5b600091505b825182101561116857828281518110151561109c57fe5b6020908102909101810151600160a060020a0387166000908152600190925260408220805461ffff909216935090839081106110d457fe5b9060005260206000200154141515156110e957fe5b600160a060020a038416600090815260016020526040808220338352912080548390811061111357fe5b60009182526020808320909101548354600181810186559484528284200155600160a060020a0388168252919091526040902080548290811061115257fe5b6000918252602082200155600190910190611085565b83600160a060020a031685600160a060020a03167fc0d84ce5c7ff9ca21adb0f8436ff3f4951b4bb78c4e2fae2b6837958b3946ffd85516040518082815260200191505060405180910390a35050505050565b60006002828154811015156111cc57fe5b90600052602060002001549050919050565b600454600160a060020a031633146111f557600080fd5b600454600160a060020a0316ff5b600060608060008089516020026040519080825280601f01601f19166020018201604052801561123d578160200160208202803883390190505b509350895160405190808252806020026020018201604052801561126b578160200160208202803883390190505b509250600091505b895182101561138657898281518110151561128a57fe5b9060200190602002015183838151811015156112a257fe5b6020908102909101015289518a90839081106112ba57fe5b9060200190602002015160f860020a028483602002601f018151811015156112de57fe5b906020010190600160f860020a031916908160001a905350600190505b602081101561137b5760088a8381518110151561131457fe5b906020019060200201519060020a90048a8381518110151561133257fe5b9060200190602002018181525060f860020a02848284602002601f010381518110151561135b57fe5b906020010190600160f860020a031916908160001a9053506001016112fb565b600190910190611273565b82995060028a600081518110151561139a57fe5b602090810290910181015182546001810184556000938452918320909101556002805490919081106113c857fe5b90600052602060002001549450505050509695505050505050565b3360009081526001602090815260409182902080548351818402810184019094528084526060939283018282801561098757602002820191906000526020600020905b815481526020019060010190808311611426575050505050905090565b600454600090600160a060020a0316331461145d57600080fd5b5060005b81518110156114bc57600454600160a060020a03166000908152600160205260409020825183908390811061149257fe5b60209081029091018101518254600181810185556000948552929093209092019190915501611461565b5050565b6000805b82518210156115955782828151811015156114db57fe5b6020908102909101810151336000908152600190925260408220805461ffff9092169350908390811061150a57fe5b90600052602060002001541415151561151f57fe5b600160a060020a038416600090815260016020526040808220338352912080548390811061154957fe5b60009182526020808320909101548354600181810186559484528284200155338252919091526040902080548290811061157f57fe5b60009182526020822001556001909101906114c4565b82516040805191825251600160a060020a038616917f69ca02dd4edd7bf0a4abb9ed3b7af3f14778db5d61921c7dc7cd545266326de2919081900360200190a250505050565b6005546000908190819081908190600160a060020a031633146115fd57600080fd5b428b118061160957508a155b151561161457600080fd5b61162060008c8c611b7a565b604080516000808252602080830180855285905260ff8e1683850152606083018d9052608083018c9052925193985060019360a08084019493601f19830193908390039091019190865af115801561167c573d6000803e3d6000fd5b505050602060405103519350600092505b895183101561177d5789838151811015156116a457fe5b6020908102909101810151600160a060020a038616600090815260019092526040822080549194509061ffff85169081106116db57fe5b9060005260206000200154141515156116f057fe5b600160a060020a0384166000908152600160205260409020805461ffff841690811061171857fe5b6000918252602080832090910154600160a060020a03808a168452600180845260408086208054928301815586529385200182905587168352912080549192509061ffff841690811061176757fe5b600091825260208220015560019092019161168d565b6040805160ff8b166020808301919091529181018a90526060810189905260808082528c51908201528b51600160a060020a038916927f4490f02c64b562efdc9d14a196182a17381fdb62142db3a2117519102a151ea9928e928e928e928e929091829160a0830191808901910280838360005b838110156118095781810151838201526020016117f1565b505050509050019550505050505060405180910390a25050505050505050505050565b3090565b600060606000806060600086516020026054016040519080825280601f01601f191660200182016040528015611870578160200160208202803883390190505b50945061187b61182c565b9350600092505b60208310156118c65784516008840260020a8a02908690859081106118a357fe5b906020010190600160f860020a031916908160001a905350600190920191611882565b600092505b60208310156119125784516008840260020a8902908690602086019081106118ef57fe5b906020010190600160f860020a031916908160001a9053506001909201916118cb565b600092505b601483101561197c5784516008840260020a6bffffffffffffffffffffffff196c01000000000000000000000000870216029086906040860190811061195957fe5b906020010190600160f860020a031916908160001a905350600190920191611917565b86516040519080825280602002602001820160405280156119a7578160200160208202803883390190505b509150600092505b8651831015611ac85786838151811015156119c657fe5b9060200190602002015182848151811015156119de57fe5b6020908102909101015286518790849081106119f657fe5b9060200190602002015160f860020a028584602002605401601f01815181101515611a1d57fe5b906020010190600160f860020a031916908160001a905350600190505b6020811015611abd5760088784815181101515611a5357fe5b906020019060200201519060020a90048784815181101515611a7157fe5b9060200190602002018181525060f860020a02858285602002605401601f0103815181101515611a9d57fe5b906020010190600160f860020a031916908160001a905350600101611a3a565b6001909201916119af565b600092505b8151831015611b11578183815181101515611ae457fe5b906020019060200201518784815181101515611afc57fe5b60209081029091010152600190920191611acd565b846040518082805190602001908083835b60208310611b415780518252601f199092019160209182019101611b22565b5181516020939093036101000a600019018019909116921691909117905260405192018290039091209c9b505050505050505050505050565b6000606060008084516002026054016040519080825280601f01601f191660200182016040528015611bb6578160200160208202803883390190505b509250611bc161182c565b9150600090505b6020811015611c095782516008820260020a880290849083908110611be957fe5b906020010190600160f860020a031916908160001a905350600101611bc8565b5060005b6020811015611c515782516008820260020a870290849060208401908110611c3157fe5b906020010190600160f860020a031916908160001a905350600101611c0d565b5060005b6014811015611cb75782516008820260020a6bffffffffffffffffffffffff196c010000000000000000000000008502160290849060408401908110611c9757fe5b906020010190600160f860020a031916908160001a905350600101611c55565b5060005b8451811015611d6f5760088582815181101515611cd457fe5b9060200190602002015161ffff169060020a900460f860020a028382600202605401815181101515611d0257fe5b906020010190600160f860020a031916908160001a9053508481815181101515611d2857fe5b9060200190602002015160f860020a028382600202605401600101815181101515611d4f57fe5b906020010190600160f860020a031916908160001a905350600101611cbb565b826040518082805190602001908083835b60208310611d9f5780518252601f199092019160209182019101611d80565b5181516020939093036101000a600019018019909116921691909117905260405192018290039091209a99505050505050505050505600a165627a7a72305820c46679b20786464d551dd6ea8ff7d838b1e1134fbc922082e6096fd94bee2ddf0029";

    public static final String FUNC_SUPPORTSINTERFACE = "supportsInterface";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_GETSYMBOL = "getSymbol";

    public static final String FUNC_SPAWNED = "spawned";

    public static final String FUNC_SETEXPIRED = "setExpired";

    public static final String FUNC_TEST = "test";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_GETSTREET = "getStreet";

    public static final String FUNC_GETSTATE = "getState";

    public static final String FUNC_SPAWNPASSTO = "spawnPassTo";

    public static final String FUNC_ISSTORMBIRDCONTRACT = "isStormBirdContract";

    public static final String FUNC_TRADE = "trade";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_GETAMOUNTTRANSFERRED = "getAmountTransferred";

    public static final String FUNC_CHECKEXPIRED = "checkExpired";

    public static final String FUNC_TESTBYTE = "testbyte";

    public static final String FUNC_GETBUILDING = "getBuilding";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_GETSPAWNEDTICKETSBYINDEX = "getSpawnedTicketsByIndex";

    public static final String FUNC_ENDCONTRACT = "endContract";

    public static final String FUNC_TEST2 = "test2";

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

    public RemoteCall<String> test(BigInteger expiry, List<BigInteger> tickets, BigInteger v, byte[] r, byte[] s, String recipient) {
        final Function function = new Function(FUNC_TEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(expiry), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(tickets, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s), 
                new org.web3j.abi.datatypes.Address(recipient)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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

    public RemoteCall<byte[]> testbyte(BigInteger value, BigInteger expiry, List<BigInteger> tickets) {
        final Function function = new Function(FUNC_TESTBYTE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(value), 
                new org.web3j.abi.datatypes.generated.Uint256(expiry), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(tickets, org.web3j.abi.datatypes.generated.Uint256.class))), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
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

    public RemoteCall<BigInteger> getSpawnedTicketsByIndex(BigInteger index) {
        final Function function = new Function(FUNC_GETSPAWNEDTICKETSBYINDEX, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> endContract() {
        final Function function = new Function(
                FUNC_ENDCONTRACT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> test2(BigInteger expiry, List<BigInteger> tickets, BigInteger v, byte[] r, byte[] s, String recipient) {
        final Function function = new Function(
                FUNC_TEST2, 
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
