package com.agoda.redis.server;

/*
 * This class is just to hold the constants of the application , if any of them needs to be modified we can just modify it from here
 * 
 * Though some configuration part we can export to some property config file , but i kept it here because of the application size
 */
public class PropertiesConstant {
	static final String SERVER_HOST_NAME = "localhost";
	static final String SERVER_PORT = "6391";
	static final Integer RETRY_ATTAMPT_COUNT = 3;
	static final Integer TIMEOUT_LIMIT = 10000;
	static final Integer CONNECTION_POOL_SIZE = 500;
	static final Integer RETRY_INTERVAL_VAL = 2000;
	
	static final String PRICE_PER_NIGHT_ATTRIBUTE_BOOKING_SCHEMA = "price_per_night";
	static final String HOTEL_ID_ATTRIBUTE_BOOKING_SCHEMA = "hotel_id";
	static final String CHECKIN_DATE_ATTRIBUTE_BOOKING_SCHEMA = "check_in_date";
	static final String NIGHTS_ATTRIBUTE_BOOKING_SCHEMA = "nights";
	
	
	static final String HOTEL_ID_SET_SCHEMA = "hotel_ids"; //set containing all the hotel IDs known by the system
	static final String HOTEL_CHARGING_TAX_SET_SCHEMA = "hotels_charging_tax"; //a set containing all the hotel IDs from hotel_ids for which we have to pay tax
	static final String HOTEL_BOOKING_LIST_SCHEMA = "hotel_bookings:"; // list of booking IDs for the given hotel ID e.g key hotel_bookings:1077 contains a list of all booking IDs for hotel ID 1077
	static final String BOOKING_HASH_SCHEMA = "booking:"; // each of these is a hash containing the FOUR keys i.e hotel_id, price_per_night, check_in_date, nights
	static final String BOOKING_PRICES_SORTED_SET_SCHEMA = "booking_prices"; //sorted set where the score is the total price of the booking and the members are booking IDs
	
	static final double BOOKING_TAX_RATE = 7 ;
}
