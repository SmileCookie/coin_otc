package com.wallet.eth.api;

import java.math.BigDecimal;

import com.wallet.eth.bean.EtherBlock;
import com.wallet.eth.bean.EtherTransaction;
import com.wallet.eth.bean.EtherTransactionReceipt;

/**
 * Ethereum Wallet JSON PRC API
 * 
 * https://ethereum.gitbooks.io/frontier-guide/content/rpc.html
 * 
 * @author guosj
 */
public interface EtherWalletApi {
	
	/**
	 * Returns true if client is actively listening for network connections.
	 * @return
	 */
	//{"jsonrpc":"2.0","method":"net_listening","params":[],"id":67}
	public boolean net_listening();
	
	/**
	 * Returns a list of addresses owned by client.
	 * @return
	 */
	//{"jsonrpc":"2.0","method":"eth_accounts","params":[],"id":1}
	public String[] eth_accounts();
	
	/**
	 * Returns the number of most recent block.
	 * @return
	 */
	//{"jsonrpc":"2.0","method":"eth_blockNumber","params":[],"id":83}
	public long eth_blockNumber();
	
	/**
	 * Returns the balance of the account of given address.
	 * @param address
	 * @param quantityOrTag - integer block number, or the string "latest", "earliest" or "pending"
	 * @return
	 */
	//{"jsonrpc":"2.0","method":"eth_getBalance","params":["0x407d73d8a49eeb85d32cf465507dd71d507100c1", "latest"],"id":1}
	public BigDecimal eth_getBalance(String address, String quantityOrTag);
	
	/**
	 * Returns the number of transactions sent from an address.
	 * @param address
	 * @param quantityOrTag - integer block number, or the string "latest", "earliest" or "pending"
	 * @return
	 */
	//{"jsonrpc":"2.0","method":"eth_getTransactionCount","params":["0x407d73d8a49eeb85d32cf465507dd71d507100c1","latest"],"id":1}
	public long eth_getTransactionCount(String address, String quantityOrTag);
	
	/**
	 * Creates new message call transaction or a contract creation, if the data field contains code.
	 * @param fromAddress
	 * @param toAddress
	 * @param amount
	 * @return  32 Bytes - the transaction hash, or the zero hash if the transaction is not yet available.
	 */
	public String eth_sendTransaction(String fromAddress, String toAddress, BigDecimal amount);
	
	/**
	 * Creates new message call transaction or a contract creation, if the data field contains code.
	 * @param fromAddress
	 * @param toAddress
	 * @param hexString
	 * @return  32 Bytes - the transaction hash, or the zero hash if the transaction is not yet available.
	 */
	public String eth_sendTransaction(String fromAddress, String toAddress, String hexString);
	
	/**
	 * Returns information about a block by hash.
	 * @param hash - 32 Bytes - Hash of a block.
	 * @param flag - If true it returns the full transaction objects, if false only the hashes of the transactions.
	 */
	public EtherBlock eth_getBlockByHash(String hash, boolean flag); 
	
	/**
	 * Returns information about a block by block number.
	 * @param quantityOrTag - - integer of a block number, or the string "earliest", "latest" or "pending".
	 * @param flag -  If true it returns the full transaction objects, if false only the hashes of the transactions.
	 */
	public EtherBlock eth_getBlockByNumber(String quantityOrTag, boolean flag);
	
	/**
	 * Returns the information about a transaction requested by transaction hash.
	 * @param hash - 32 Bytes - hash of a transaction
	 */
	public EtherTransaction eth_getTransactionByHash(String hash);
	
	/**
	 * Returns information about a transaction by block hash and transaction index position.
	 * @param hash - 32 Bytes - hash of a transaction
	 * @param index - integer of the transaction index position.
	 */
	public EtherTransaction eth_getTransactionByBlockHashAndIndex(String hash, long index);
	
	/**
	 * Returns information about a transaction by block number and transaction index position.
	 * @param quantityOrTag - - a block number, or the string "earliest", "latest" or "pending".
	 * @param index - integer of the transaction index position.
	 */
	public EtherTransaction eth_getTransactionByBlockNumberAndIndex(String quantityOrTag, long index);
	
	/**
	 * Returns the receipt of a transaction by transaction hash.
	 * Note That the receipt is not available for pending transactions.
	 * @param hash - 32 Bytes - hash of a transaction
	 */
	public EtherTransactionReceipt eth_getTransactionReceipt(String hash);
	
	/**
	 * Creates a filter in the node, to notify when a new block arrives. 
	 * To check if the state has changed, call eth_getFilterChanges.
	 * @return	a filter id. "0x1"
	 */
	public String eth_newBlockFilter();
	
	/**
	 * Creates a filter in the node, to notify when new pending transactions arrive. 
	 * To check if the state has changed, call eth_getFilterChanges.
	 * @return	a filter id. "0x1"
	 */
	public String eth_newPendingTransactionFilter();
	
	/**
	 * Uninstalls a filter with given id. Should always be called when watch is no longer needed. 
	 * Additonally Filters timeout when they aren't requested with eth_getFilterChanges for a period of time.
	 * @param filter
	 * @return
	 */
	public boolean eth_uninstallFilter(String filter);
	
	/**
	 * Polling method for a filter, which returns an array of logs which occurred since last poll.
	 * 1、如果filter为eth_newBlockFilter，则返回[blockHash1, blockHash2]
	 * 2、如果filter为eth_newPendingTransactionFilter，则返回[transactionHash1, transactionHash2]
	 * @param filter
	 */
	public String[] eth_getFilterChanges(String filter);
}
