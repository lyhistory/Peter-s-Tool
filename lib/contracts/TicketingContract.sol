//mainnet: 0xA66A3F08068174e8F005112A8b2c7A507a822335

// ["0x474d542b330000000000000000000000020b5b23d4704d415249524e04050001",
// "0x474d542b330000000000000000000000020b5b23d4704d415249524e04050002",
// "0x474d542b330000000000000000000000010a5b291a70504f4c53454e0f050001", 
// "0x474d542b330000000000000000000000010a5b291a70504f4c53454e0f050002", 
// "0x474d542b330000000000000000000000020b5b2944a052555345475911050001", 
// "0x474d542b330000000000000000000000020b5b2944a052555345475911050002", 
// "0x474d542b33000000000000000000000006055b3fae205735335735343a050001",
// "0x474d542b33000000000000000000000006055b3fae205735335735343a050002", 
// "0x474d542b330000000000000000000000020b5b4a01e04c36314c36323f050001",
// "0x474d542b330000000000000000000000020b5b4a01e04c36314c36323f050002",
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050001",
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050002", 
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050003", 
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050004", 
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050005",
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050006",
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050007",
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050008", 
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050009",
// "0x474d542b33000000000000000000000001075b2282f05255534b53410105000a",
// "0x474d542b33000000000000000000000001075b2282f05255534b53410105000b",
// "0x474d542b33000000000000000000000001075b2282f05255534b53410105000c",
// "0x474d542b33000000000000000000000001075b2282f05255534b53410105000d",
// "0x474d542b33000000000000000000000001075b2282f05255534b53410105000e", 
// "0x474d542b33000000000000000000000001075b2282f05255534b53410105000f", 
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050010", 
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050011", 
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050012", 
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050013", 
// "0x474d542b33000000000000000000000001075b2282f05255534b534101050014"], 
// "FIFA WC2018", 
// "SHANKAI", 
// "0x0D590124d2fAaBbbdFa5561ccBf778914a50BCca", 
// "0xFE6d4bC2De2D0b0E6FE47f08A28Ed52F9d052A02",
// "0x2e558Bc42E2e37aB638daebA5CD1062e5b9923De"

pragma solidity ^0.4.17;
contract TicketPro
{
    mapping(address => bytes32[]) inventory;
    uint16 ticketIndex = 0; //to track mapping in tickets
    address organiser;
    address paymaster;
    uint numOfTransfers = 0;
    string public name;
    string public symbol;
    uint8 public constant decimals = 0; //no decimals as tickets cannot be split

    event Transfer(address indexed _to, uint16[] _indices);
    event TransferFrom(address indexed _from, address indexed _to, uint16[] _indices);
    event Trade(address indexed seller, uint16[] ticketIndices, uint8 v, bytes32 r, bytes32 s);
    event PassTo(uint16[] ticketIndices, uint8 v, bytes32 r, bytes32 s, address indexed recipient);

    modifier organiserOnly()
    {
        if(msg.sender != organiser) revert();
        else _;
    }
    
    modifier payMasterOnly()
    {
        if(msg.sender != paymaster) revert();
        else _;
    }
    
    function() public { revert(); } //should not send any ether directly

     
    constructor (
        bytes32[] tickets,
        string nameOfContract,
        string symbolForContract,
        address organiserAddr,
        address paymasterAddr,
        address recipientAddr) public
    {
        name = nameOfContract;
        symbol = symbolForContract;
        organiser = organiserAddr;
        paymaster = paymasterAddr;
        inventory[recipientAddr] = tickets;
    }

    function getDecimals() public pure returns(uint)
    {
        return decimals;
    }

    // example: 0, [3, 4], 27, "0x9CAF1C785074F5948310CD1AA44CE2EFDA0AB19C308307610D7BA2C74604AE98", "0x23D8D97AB44A2389043ECB3C1FB29C40EC702282DB6EE1D2B2204F8954E4B451"
    // price is encoded in the server and the msg.value is added to the message digest,
    // if the message digest is thus invalid then either the price or something else in the message is invalid
    function trade(uint256 expiry,
                   uint16[] ticketIndices,
                   uint8 v,
                   bytes32 r,
                   bytes32 s) public payable
    {
        //checks expiry timestamp,
        //if fake timestamp is added then message verification will fail
        require(expiry > block.timestamp || expiry == 0);

        bytes32 message = encodeMessage(msg.value, expiry, ticketIndices);
        address seller = ecrecover(message, v, r, s);

        for(uint i = 0; i < ticketIndices.length; i++)
        { // transfer each individual tickets in the ask order
            uint16 index = ticketIndices[i];
            assert(inventory[seller][index] != bytes32(0)); // 0 means ticket gone.
            inventory[msg.sender].push(inventory[seller][index]);
            // 0 means ticket gone.
            delete inventory[seller][index];
        }
        seller.transfer(msg.value);
        
        emit Trade(seller, ticketIndices, v, r, s);
    }
    
    function loadNewTickets(bytes32[] tickets) public organiserOnly 
    {
        for(uint i = 0; i < tickets.length; i++) 
        {
            inventory[organiser].push(tickets[i]);    
        }
    }
    
    function passTo(uint256 expiry,
                    uint16[] ticketIndices,
                    uint8 v,
                    bytes32 r,
                    bytes32 s,
                    address recipient) public payMasterOnly
    {
        require(expiry > block.timestamp || expiry == 0);
        bytes32 message = encodeMessage(0, expiry, ticketIndices);
        address giver = ecrecover(message, v, r, s);
        for(uint i = 0; i < ticketIndices.length; i++)
        {
            uint16 index = ticketIndices[i];
            //needs to use revert as all changes should be reversed
            //if the user doesnt't hold all the tickets 
            assert(inventory[giver][index] != bytes32(0));
            bytes32 ticket = inventory[giver][index];
            inventory[recipient].push(ticket);
            delete inventory[giver][index];
        }
        
        emit PassTo(ticketIndices, v, r, s, recipient);
    }

    //must also sign in the contractAddress
    function encodeMessage(uint value, uint expiry, uint16[] ticketIndices)
        internal view returns (bytes32)
    {
        bytes memory message = new bytes(84 + ticketIndices.length * 2);
        address contractAddress = getContractAddress();
        for (uint i = 0; i < 32; i++)
        {   // convert bytes32 to bytes[32]
            // this adds the price to the message
            message[i] = byte(bytes32(value << (8 * i)));
        }

        for (i = 0; i < 32; i++)
        {
            message[i + 32] = byte(bytes32(expiry << (8 * i)));
        }

        for(i = 0; i < 20; i++)
        {
            message[64 + i] = byte(bytes20(bytes20(contractAddress) << (8 * i)));
        }

        for (i = 0; i < ticketIndices.length; i++)
        {
            // convert int[] to bytes
            message[84 + i * 2 ] = byte(ticketIndices[i] >> 8);
            message[84 + i * 2 + 1] = byte(ticketIndices[i]);
        }

        return keccak256(message);
    }

    function name() public view returns(string)
    {
        return name;
    }

    function symbol() public view returns(string)
    {
        return symbol;
    }

    function getAmountTransferred() public view returns (uint)
    {
        return numOfTransfers;
    }

    function balanceOf(address _owner) public view returns (bytes32[])
    {
        return inventory[_owner];
    }

    function myBalance() public view returns(bytes32[]){
        return inventory[msg.sender];
    }

    function transfer(address _to, uint16[] ticketIndices) public 
    {
        for(uint i = 0; i < ticketIndices.length; i++)
        {
            uint index = uint(ticketIndices[i]);
            assert(inventory[msg.sender][index] != bytes32(0));
            //pushes each element with ordering
            inventory[_to].push(inventory[msg.sender][index]);
            delete inventory[msg.sender][index];
        }
        emit Transfer(_to, ticketIndices);
    }

    function transferFrom(address _from, address _to, uint16[] ticketIndices)
        organiserOnly public
    {
        for(uint i = 0; i < ticketIndices.length; i++)
        {
            uint index = uint(ticketIndices[i]);
            assert(inventory[_from][index] != bytes32(0));
            //pushes each element with ordering
            inventory[_to].push(inventory[msg.sender][index]);
            delete inventory[_from][index];
        }
        
        emit TransferFrom(_from, _to, ticketIndices);
    }

    function endContract() public organiserOnly
    {
        selfdestruct(organiser);
    }

    function isStormBirdContract() public pure returns (bool) 
    {
        return true; 
    }

    function getContractAddress() public view returns(address)
    {
        return this;
    }

}
