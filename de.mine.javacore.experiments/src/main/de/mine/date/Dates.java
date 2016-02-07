package de.mine.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;



public class Dates {

	public static void main(String[] args) {
		/*
		 *  This one returns the date in milliseconds.
		 *  
		 *  ACHTUNG: 
		 *  the date is returned in UTC alias. Greenwich Time.
		 *  Important, when comparing the date with something
		 */
		long dateCurrentMillis = System.currentTimeMillis();
		
		/*
		 *   ACHTUNG:
		 *   The Date class does not store TimeZones.
		 *   But the debugger will convert the Date into the local TimeZone, when debugging.
		 *   The most tools will use Date as if it would be a UTC time. So it is good to handle the time internally in UTC Format. 
		 */
		Date dateCurrent = new Date(dateCurrentMillis);
		System.out.println("DateCurrent: "+dateCurrent);
		
		
		/*
		 * TimeZone Object is capable to store data about timeZones.
		 * It Is used by DateFormat classes.
		 */
		TimeZone localZone 		= TimeZone.getDefault(); // +2h
		TimeZone timbuktuZone 		= TimeZone.getTimeZone("Africa/Kinshasa"); // +1h
		TimeZone utcZone		= TimeZone.getTimeZone("UTC"); // 0
		
		
		/*
		 * To convert the Time use the object SimpleDateFormat.
		 * It understands 
		 */
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
		System.out.println("No Timezone: "+simpleDateFormat.format(dateCurrent));
		
		//+2
		simpleDateFormat.setTimeZone(localZone);
		System.out.println("Local: "+simpleDateFormat.format(dateCurrent));
		
		//+1
		simpleDateFormat.setTimeZone(timbuktuZone);
		System.out.println("Timbuktu: "+simpleDateFormat.format(dateCurrent));
		
		//+0
		simpleDateFormat.setTimeZone(utcZone);
		System.out.println("UTC: "+simpleDateFormat.format(dateCurrent));
		
		
		
		/*
		 * Operations on Dates are done using Calender. 
		 * GregorianCalender is the right calender.
		 */
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTimeInMillis(System.currentTimeMillis());
		gregorianCalendar.add(Calendar.HOUR, 1); // add an hour
		gregorianCalendar.add(Calendar.MINUTE, 1); // add a minute
		gregorianCalendar.add(Calendar.SECOND, 1); // add a minute
		
		
		Date modifiedDate = gregorianCalendar.getTime();
		System.out.println("Modified Date: " + modifiedDate);
		
		
		
		/*
		 * Supposed I receive a Date in GMT+0 and I have to convert it to GMT+2 timeZone.
		 * Use TimeZone offsets to do so.
		 */
		gregorianCalendar.set(2014, 11, 31, 23,55,55); //0 is January. 12 is January too.
		TimeZone gmtp1Zone = TimeZone.getTimeZone("Etc/GMT+1");
		
		// ACHTUNG: 
		// offset is given relative to the local TimeZone, not relative to UTC.
		// Etc/GMT+1        gives an offset of -1  Convert from UTC: utc --1 
		// Etc/GMT-1        gives an offset of  1  Convert from UTC: utc - 1
		// "Europe/Berlin"  gives an offset of  1. 

		// the paramter of getOffset is important, to differentiate between Summer and Winter time
		int offsetM3 = TimeZone.getTimeZone("Etc/GMT-3").getOffset(System.currentTimeMillis()) /(1000*60*60) ; // imagine i'm in -3 TimeZone
		System.out.println("Offset GMT -3: "+offsetM3 +"h"); // offset +3

		int offsetP3 = TimeZone.getTimeZone("Etc/GMT+3").getOffset(System.currentTimeMillis()) /(1000*60*60); // imagine i'm in -3 TimeZone
		System.out.println("Offset GMT +3: "+offsetP3 +"h"); // offset -3
		
		
		int offsetGmtp1Winter  = gmtp1Zone.getOffset(0); 				//winterTime
		int offsetGmtp1Summer  = gmtp1Zone.getOffset(0,2014,8,1,1,1); 	//summerTime
		int offsetGmtp1Current = TimeZone.getDefault().getOffset(System.currentTimeMillis()); //currentTime Summer or Winter
		System.out.println("Offset GMT+1: "+offsetGmtp1Current/(1000*60*60) + "h");
		
		

		gregorianCalendar.add(Calendar.MILLISECOND, offsetGmtp1Current);
		Date inCurrentTimeZone = gregorianCalendar.getTime();
		System.out.println("23:55 UTC Time in current Timezone: "+ inCurrentTimeZone + ". Used offset of "+offsetGmtp1Current/(1000*60*60)+ "h");
		
		// say all the timezones
//		listTimeZones();
	}

	
	private static void listTimeZones(){
		for(String timeZone : TimeZone.getAvailableIDs()){
			System.out.println(timeZone);
		}
	}
}
