package de.mine.java8stuff;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateApi {

	public static void main(String[] args) {
		
		// LOCALDATE
		
		LocalDate localDateNow = LocalDate.now();
		LocalDate localDateNowLondon = LocalDate.now(ZoneId.of("UTC+0"));
		LocalDate localDate = LocalDate.of(2016, 2, 16);

		int fieldYear = localDate.get(ChronoField.YEAR);
		/*
		    NanoOfSecond
			NanoOfDay
			MicroOfSecond
			MicroOfDay
			MilliOfSecond
			MilliOfDay
			SecondOfMinute
			SecondOfDay
			MinuteOfHour
			MinuteOfDay
			HourOfAmPm
			ClockHourOfAmPm
			HourOfDay
			ClockHourOfDay
			AmPmOfDay
			DayOfWeek
			AlignedDayOfWeekInMonth
			AlignedDayOfWeekInYear
			DayOfMonth
			DayOfYear
			EpochDay
			AlignedWeekOfMonth
			AlignedWeekOfYear
			MonthOfYear
			ProlepticMonth
			YearOfEra
			Year
			Era
			InstantSeconds
			OffsetSeconds
		 */
		
		int year = localDate.getYear();
		Month month = localDate.getMonth();
		int day = localDate.getDayOfMonth();
		DayOfWeek dayOfWeek = localDate.getDayOfWeek();
		int lengthMonth = localDate.lengthOfMonth();
		boolean isLeapYear = localDate.isLeapYear();
		
		
		
		// LOCALTIME
		LocalTime time = LocalTime.of(14, 55, 54, 11 ); // hour, minute, second, milli
		LocalTime time2 = LocalTime.of(14, 55, 54 ); // hour, minute, second
		LocalTime time3 = LocalTime.of(14, 55 ); // hour, minute
		int hourTime = time.getHour();
		int minuteTime = time.getMinute();
		
		LocalTime parsedTime;
		parsedTime = LocalTime.parse("13:45:20");
		parsedTime = LocalTime.parse("13h 45m 20s", DateTimeFormatter.ofPattern("HH'h' mm'm' ss's'"));
		
		
		
		// COMBINE DATE and TIME
		
		// 1415-07-6T6:55:20
		LocalDate dateOne = LocalDate.of(1415, Month.JULY, 6);
		LocalTime timeOne = LocalTime.of(6, 55, 20);
		LocalDateTime dt1 = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45, 20);
		LocalDateTime dt2 = LocalDateTime.of(dateOne, timeOne);
		LocalDateTime dt3 = dateOne.atTime(13, 45, 20);
		LocalDateTime dt4 = dateOne.atTime(timeOne);
		LocalDateTime dt5 = timeOne.atDate(dateOne);
		
		/*
		 * EXCEPTION -
		 * Can not just go and convert Date to Date, using useful defaults of Time = 0....
		 * WHY OR WHY CAN JAVA USE DEFAULTS INSTEAD OF EXCEPTIONS?
		 */
//		LocalDateTime.from(dateOne);
		LocalDateTime localDateTimeOfGuss = LocalDateTime.of(dateOne, LocalTime.MIDNIGHT); // works instead
		LocalDateTime localDateTimeOfGuss9 = dateOne.atStartOfDay();
		localDateTimeOfGuss.plus(1, ChronoUnit.SECONDS);
		localDateTimeOfGuss.plus(1, ChronoUnit.DAYS);
		
		// INSTANT - to be used by machines
		Instant instant = Instant.ofEpochSecond(1454523011);
		// nanosecond adjustment - 1sec=1.000.000.000nanosec
		Instant.ofEpochSecond(0); 					// 1.January.1970 UTC
		Instant.ofEpochSecond(2, 1000_000_000); 	// 2sec + 1sec
		Instant.ofEpochSecond(3, -1000_000_000); 	// 4sec - 1sec
		
		
		// Instant -> LocalDate, LocalTime
		LocalDate dateOfInstant = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
		LocalTime timeOfInstant = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime();
		
		
		
		// (PERIODS, DURATIONS ) alias TemporalAmount
		
		/*
		 * Duration is CRAP, because based on Seconds / Nanos.
		 * - you only can compute a duration on "time based units" - something what can handle seconds like LocalTime. 
		 *   The compiler still allows computing Duration between LocalDate, or getting YEARS from Duration,
		 *   which results in an Exception.
		 *   WHY OR WHY CAN'T I JUST CONVERT SECONDS IN ANY UNIT WITHOUT EXCEPTIONS, AS EXPECTED?!
		 */
		// Duration only works with Seconds-supporting types. Using LocaDate - throws an exception
		// Duration durationSinceGuss = Duration.between(dateOne, LocalDate.now());
		Duration durationSinceGuss1 = Duration.between(LocalDateTime.of(dateOne, LocalTime.MIDNIGHT),LocalDateTime.now());
		Duration durationSinceGuss2 = Duration.between(timeOne,LocalTime.now());
//		Duration durationSinceGuss3 = Duration.between(dateOne,LocalDate.now());   		// exception. It trys to convert LocalDate to Seconds, which is not allowed 
		Duration durationSinceGuss4 = Duration.between(LocalDateTime.now(), LocalDateTime.of(dateOne, LocalTime.MIDNIGHT));	// exception
		
		System.out.println("Duration  "+durationSinceGuss1.get(ChronoUnit.SECONDS));
		System.out.println("Duration  "+durationSinceGuss1.get(ChronoUnit.NANOS));
//		System.out.println("Duration  "+durationSinceGuss1.get(ChronoUnit.DAYS));  		// exception
//		System.out.println("Duration  "+durationSinceGuss1.get(ChronoUnit.YEARS)); 		// exception
		
		/*
		 *  ERROR - 
		 *  Java Epoch starts in 1970. The first amount of seconds was retrieved from year 1415!
		 *  ofEpochSecond() needs seconds SINCE 1970. Seconds since 1415 is too much!
		 */
//		LocalDateTime l = LocalDateTime.ofEpochSecond(durationSinceGuss1.get(ChronoUnit.SECONDS), 0,ZoneOffset.UTC);
		long yearsSinceGuss4 = durationSinceGuss1.get(ChronoUnit.SECONDS) / 60 / 60 / 24 / 365;

		/*
		 * ERROR - 
		 * LocalDate.now().plus(durationSinceGuss1) causes an Exception. 
		 * Seconds are not Supported for LocalDate.plus()!!!
		 * WHY OR WHY CAN'T JAVA DO WHAT COMPILER ALLOWS ME TO DO?
		 */
//		long yearsSinceGuss6 = ChronoUnit.YEARS.between(LocalDate.now(), LocalDate.now().plus(durationSinceGuss1) );
		
		/*
		 * ERROR - 
		 * Still an exception! 
		 * Even on explicitly converting duration to seconds. 
		 * Everything like above. Seconds are just not allowed. Have to convert them manually first e.g. to Days?!
		 * WHY OR WHY CAN'T YOU CONVERT SECONDS TO DAYS OR SOMETHING AUTOMATICALLY, JAVA?
		 */
//		long yearsSinceGuss6 = ChronoUnit.YEARS.between(LocalDate.now(), LocalDate.now().plus(durationSinceGuss1.getSeconds(), ChronoUnit.SECONDS) );
		
		long yearsSinceGuss5 = ChronoUnit.YEARS.between(
				LocalDate.now(), 
				LocalDateTime.now().plus(durationSinceGuss1) 
		);
		
		// via "until" you can compute the years between NOW and Past date without any duration
		long yearsSinceGuss = dateOne.until(LocalDate.now(), ChronoUnit.YEARS);
		
		Period periodBetweenGuss = Period.between(dateOne, LocalDate.now());
		long yearsSinceGuss7 = periodBetweenGuss.getYears();
		
		
		// TEMPORALADJUSTER
		/*
		 * Do complex computations on Dates 
		 */
		LocalDate lastDayInMonth = dateOne.with(TemporalAdjusters.lastDayOfMonth());
		LocalDate dateNextSunay = dateOne.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
		
		
		// FORMATTER
		
		LocalDate parsedDate;
		parsedDate = LocalDate.parse("2014-03-18");
		parsedDate = LocalDate.parse("2014-03-18", DateTimeFormatter.ISO_DATE);
		parsedDate = LocalDate.parse("18/03/2014", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		parsedDate = LocalDate.parse("18.03.2014", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		
		parsedDate = LocalDate.parse("18 März 2014", DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.GERMAN));
		
		DateTimeFormatter germanFormatter = new DateTimeFormatterBuilder()
				.appendText(ChronoField.DAY_OF_MONTH)   // 6
				.appendLiteral(".")
				.appendValue(ChronoField.MONTH_OF_YEAR) // 7
				.appendLiteral("( alias ")
				.appendText(ChronoField.MONTH_OF_YEAR) // July
				.appendLiteral(")")
				.appendLiteral(".")
				.appendValue(ChronoField.YEAR)			// 1941
				.parseCaseInsensitive()
				.toFormatter(Locale.GERMAN);
		System.out.println(germanFormatter.format(dateOne));
		
		
		// TIME ZONES
		
		ZoneId romeZone = ZoneId.of("Europe/Rome");
		ZoneId berlinZone = ZoneId.of("Europe/Berlin");
		ZoneId myZone = TimeZone.getDefault().toZoneId();
		
		ZonedDateTime zonedDateTimeBerlin1 = dateOne.atStartOfDay(berlinZone);
		ZonedDateTime zonedDateTimeBerlin2 = localDateTimeOfGuss.atZone(berlinZone);
		
		
		System.out.println("done");
		
	}

}
