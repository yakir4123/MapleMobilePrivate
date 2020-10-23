package com.bapplications.maplemobile.utils;


import java.security.SecureRandom;
import java.util.Objects;

public class Randomizer {

	private static SecureRandom random = new SecureRandom();

	public static double nextReal(double from, double to)
	{
		if (from >= to)
			return from;

		return random.nextDouble() * (to - from) + from;
	}

	public static boolean nextBoolean()
	{
		return nextInt(2) == 1;
	}

	public static boolean below(float percent)
	{
		return nextReal(1.0f) < percent;
	}

	public static boolean above(float percent)
	{
		return nextReal(1.0f) > percent;
	}

	public static double nextReal(double to)
	{
		return nextReal(0, to);
	}

	public static int nextInt(int to)
	{
		return nextInt(0, to);
	}

	public static int nextInt(int from, int to) {
		return (int)nextReal(from, to);
	}

	public static <E extends Enum<?>> E nextEnum(Class<E> clazz){
		int e = nextInt(Objects.requireNonNull(clazz.getEnumConstants()).length);
		return clazz.getEnumConstants()[e];
	}

	public static double nextExponential() {
		return nextExponential(1);
	}

	public static double nextExponential(int lambda) {
		return  Math.log(1-random.nextDouble())/(-lambda);
	}
}