package com.agoda.redis.server;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/*
 * Entry Point to the application to perform all client queries .
 */
public class HotelAnalyticsManager {
	private static HotelDataManager dataManager =new HotelDataManager(); ;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static void main(String[] args) {
		   System.out.println("TestCase 1 : price per night for booking id 200123 is : $"+getPricePerNightByBookingID(200123));
		   System.out.println("TestCase 2 : oldest check in date for hotel ID 1042 is : " + getOldestCheckInDateByHotelID(1042));
		   System.out.println("TestCase 3 : Find the five most expensive bookings are  : " );printMostExpensiveBookingIDandPrice();
		   System.out.println("TestCase 4 : Total tax on all booking is : $" + getTotalTaxOnAllBookings());
		   dataManager.closeClientConnections();
	}

	/*
	 * Method to get price per night by booking ID , will accept bookingID as input parameter
	 */
	private static Double getPricePerNightByBookingID(Integer bookingID) {
		LinkedHashMap<String, String> bookingDetails = dataManager.getBookingByID(bookingID);
		String price = bookingDetails.get(PropertiesConstant.PRICE_PER_NIGHT_ATTRIBUTE_BOOKING_SCHEMA);
		return Double.parseDouble(price);
	}

	/*
	 * Method to get Oldest CheckInDate for Hotel, will accept hotel ID as input parameter
	 */
	private static String getOldestCheckInDateByHotelID(Integer hotelID) {
		LinkedList<Integer> hotelBookingList = dataManager.getBookingListIdForHotelbyID(hotelID);
		Date oldesteDate = new Date();
		for (Integer bookingID : hotelBookingList) {
			LinkedHashMap<String, String> bookingDetails = dataManager.getBookingByID(bookingID);
			try {
				Date convertedCurrentDate = dateFormat
						.parse(bookingDetails.get(PropertiesConstant.CHECKIN_DATE_ATTRIBUTE_BOOKING_SCHEMA));
				if (convertedCurrentDate.before(oldesteDate))
					oldesteDate = convertedCurrentDate;
			} catch (ParseException e) {
				System.out.println("Invalid Date format " + e.getMessage());
			}
		}
		return dateFormat.format(oldesteDate);
	}
	
	/*
	 * Method to get Most Expensive Booking ID and its price
	 */
	private static void printMostExpensiveBookingIDandPrice(){
		LinkedHashMap<Integer,Double> map = dataManager.getBookingPricesSortedSetDataByCount(5);
		System.out.println("		Booking ID : "+ "Price");
		for(Map.Entry<Integer,Double> entry : map.entrySet()){
			System.out.println("		"+entry.getKey()+" : $"+ new DecimalFormat("##.00").format(entry.getValue()));
		}
	}

	/*
	 * Method to get Applicable Tax on taxable hotel booking , Default tax rate would be applied.
	 */
	private static double getTotalTaxOnAllBookings(){
		HashSet<Integer> hotelWithTax = dataManager.getHotelChargingTaxSet();
		LinkedList<Integer> taxableBookings = new LinkedList<>();
		for(int hotelID : hotelWithTax){
			taxableBookings.addAll(dataManager.getBookingListIdForHotelbyID(hotelID));
		}
		return  dataManager.getBookingTaxByID(taxableBookings,PropertiesConstant.BOOKING_TAX_RATE);
	}
}
