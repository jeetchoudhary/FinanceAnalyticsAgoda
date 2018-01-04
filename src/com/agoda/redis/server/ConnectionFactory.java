package com.agoda.redis.server;

import javax.naming.OperationNotSupportedException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/*
 * This class will be used to create and manage Application/Client connections to redis server 
 * from the application.
 * making this as singleton as only one connection is needed for my use case
 * 
 * only implementing required method now , could be extensible based on the use case
 */
public class ConnectionFactory {

	private static volatile RedissonClient redissonClient = null;

	/*
	 * Making the constructor to control the number of instances created
	 */
	private ConnectionFactory() throws OperationNotSupportedException{
		//throw new OperationNotSupportedException("this ");
	}
	
	/*
	 * if the client is not created yet or client is closed not to take any more request 
	 * getClientConnection() will create a new client and return the reference of that client.
	 * 
	 * This  method is thread safe and multiple threads requesting the connection will get only one client connection.
	 * we can change this to COUNTING SEMAPHORE to control numbers of connections , which will also be thread safe.
	 */
	public static RedissonClient getClientConnection() {
		if (redissonClient == null) {
			synchronized (ConnectionFactory.class) {
				if (redissonClient == null) {
					Config config = new Config();
					config.useSingleServer().setAddress(PropertiesConstant.SERVER_HOST_NAME + ":" + PropertiesConstant.SERVER_PORT);
					config.useSingleServer().setRetryAttempts(PropertiesConstant.RETRY_ATTAMPT_COUNT);
					config.useSingleServer().setTimeout(PropertiesConstant.TIMEOUT_LIMIT);
					config.useSingleServer().setConnectionPoolSize(PropertiesConstant.CONNECTION_POOL_SIZE);
					config.useSingleServer().setRetryInterval(PropertiesConstant.RETRY_INTERVAL_VAL);
					try{
						redissonClient = Redisson.create(config);
					}catch(Exception e){
						System.out.println("Not able to connecte to database , please check your connection to db ");
						System.out.println("if your db is not on localhost please configure it in PropertiesConstant.SERVER_HOST_NAME to correct db , and port no should be 6391 ");
						System.out.println("Exiting Application ....");
						System.exit(0);
					}
				}
			}
		}
		return redissonClient;
	}
/*
 * This method will close the client connection.
 * this can be used to free resource when we don't use it anymore. closeClientConnection
 * we can also user this method to control shutdown behavior of the redis client 
 * i.e SAVE ALL DATA TO THE FILE SYSTEM BEFORE CLOSING THE CONNECTION.
 */
	public static Boolean closeClientConnection(RedissonClient redissonClient) {
		if (redissonClient == null) {
			return Boolean.FALSE;
		} else {
			synchronized (ConnectionFactory.class) {
				if (redissonClient != null) {
					redissonClient.shutdown();
					redissonClient = null;
				}
			}
		}
		return Boolean.TRUE;
	}

	/*
	 * This method will close the default client connection instance.
	 * this can be used to free resource when we don't use it anymore. closeClientConnection we can also user this method to control shutdown behavior of the redis client 
	 * i.e SAVE ALL DATA TO THE FILE SYSTEM BEFORE CLOSING THE CONNECTION.
	 */
	public static Boolean closeClientConnection() {
		return closeClientConnection(redissonClient);
	}
}
