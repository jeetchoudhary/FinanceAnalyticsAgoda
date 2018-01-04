package com.agoda.redis.server;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;


/*
 * This is Model class and will handle all the interactions of the application with the database
 * All private methods are generic and could return generic return type
 */
public class HotelDataManager {

	private RedissonClient dataClient;

	public HotelDataManager() {
		dataClient = ConnectionFactory.getClientConnection();
	}
	
	/*
	 * Generic method to fetch data from redis set . the set objects could be of any kind i.e Integer,Double,CustomObject etc.
	 */
	private <T> HashSet<T> fetchSetData(String setName) {
		RSet<T> setData = dataClient.getSet(setName);
		HashSet<T> data = new HashSet<T>();
		for (T setElement : setData) {
			data.add(setElement);
		}
		return data;
	}
	
	/*
	 * Generic method to fetch data from redis set in the sorted order . the set objects could be of any kind i.e Integer,Double,CustomObject etc.
	 */
	private <T> TreeSet<T> fetchSortedSetData(String setName) {
		RScoredSortedSet<T> setData = dataClient.getScoredSortedSet(setName);
		TreeSet<T> data = new TreeSet<T>();
		for (T setElement : setData) {
			data.add(setElement);
		}
		return data;
	}
	
	/*
	 * Generic method to get data from redis list . The list objects could be of any kind i.e Integer,Double,CustomObject etc.
	 */
	private <T> LinkedList<T> fetchListData(String listName) {
		RList<T> listData = dataClient.getList(listName);
		LinkedList<T> data = new LinkedList<T>();
		for (T listElement : listData) {
			data.add(listElement);
		}
		return data;
	}

	/*
	 * Generic method to fetch the data from redis hash and return as HapMap . 
	 */
	private <T> LinkedHashMap<T, T> fetchHashData(String keyName) {
		RMap<T, T> mapData = dataClient.getMap(keyName, StringCodec.INSTANCE);
		LinkedHashMap<T, T> data = new LinkedHashMap<>();
		Set<Entry<T, T>> allEntries = mapData.readAllEntrySet();
		for (Entry<T, T> mapElement : allEntries) {
			data.put(mapElement.getKey(), mapElement.getValue());
		}
		return data;
	}

	/*
	 * Method to return set of all hotel ID that are registered to the system
	 */
	public HashSet<Integer> getHotelIDSet() {
		HashSet<Integer> hotelIDSet = fetchSetData(PropertiesConstant.HOTEL_ID_SET_SCHEMA);
		return hotelIDSet;
	}

	/*
	 * Method to return set of all hotel ID ,which are registered to the system and are supposed to be charged tax
	 */
	public HashSet<Integer> getHotelChargingTaxSet() {
		HashSet<Integer> hotelChargingTax = fetchSetData(PropertiesConstant.HOTEL_CHARGING_TAX_SET_SCHEMA);
		return hotelChargingTax;
	}

	/*
	 * Method to return sorted set of booking ID based on total prices of bookings 
	 */
	public TreeSet<Integer> getBookingPricesSortedSet() {
		TreeSet<Integer> bookingPrices = fetchSortedSetData(PropertiesConstant.BOOKING_PRICES_SORTED_SET_SCHEMA);
		return bookingPrices;
	}

	/*
	 * Method to return list of only last "count" num of elements from the sorted set.
	 * Method with parameter 3 will return last HashMap of last three elements in the sorted set (BookingID as key and price as Value of map)
	 */
	public LinkedHashMap<Integer, Double> getBookingPricesSortedSetDataByCount(int count) {
		RScoredSortedSet<Integer> setData = dataClient.getScoredSortedSet(PropertiesConstant.BOOKING_PRICES_SORTED_SET_SCHEMA);
		LinkedHashMap<Integer, Double> data = new LinkedHashMap<Integer, Double>();
		while (count-- > 0) {
			int id = setData.last();
			Double score = setData.getScore(id);
			data.put(id, score);
			setData.pollLast();
		}
		for (Map.Entry<Integer, Double> entry : data.entrySet())
			setData.add(entry.getValue(), entry.getKey());
		return data;
	}

	/*
	 * Method will accept list of all bookings and tax to applied on those bookings
	 * will return sum of tax to be applicable on all the bookings
	 */
	public Double getBookingTaxByID(LinkedList<Integer> taxableBookings,double taxRate) {
		RScoredSortedSet<Integer> setData = dataClient.getScoredSortedSet(PropertiesConstant.BOOKING_PRICES_SORTED_SET_SCHEMA);
		Double totalTax = 0D;
		for (Integer i : taxableBookings) {
			totalTax += Math.round(setData.getScore(i) * taxRate * 100) / 10000;
		}
		return totalTax;
	}

	/*
	 * Method will return list of booking done from hotel , will accept hotelID as parameter
	 */
	public LinkedList<Integer> getBookingListIdForHotelbyID(Integer ID) {
		LinkedList<Integer> bookingListByHotelID = fetchListData(PropertiesConstant.HOTEL_BOOKING_LIST_SCHEMA + ID);
		return bookingListByHotelID;
	}

	/*
	 * Method will return LinkedHashMap of booking details of particular booking , will accept bookinID as parameter 
	 */
	public LinkedHashMap<String, String> getBookingByID(Integer ID) {
		LinkedHashMap<String, String> bookingDetails = fetchHashData(PropertiesConstant.BOOKING_HASH_SCHEMA + ID);
		return bookingDetails;
	}
	
	public Boolean closeClientConnections(){
		return ConnectionFactory.closeClientConnection();
	}

}
