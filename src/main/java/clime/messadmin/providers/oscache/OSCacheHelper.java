/**
 *
 */
package clime.messadmin.providers.oscache;


/**
 * @author C&eacute;drik LIME
 */
public abstract class OSCacheHelper {

	private OSCacheHelper() {
		throw new AssertionError();
	}

	private static double getPercentage(long number, long total) {
		if (total == 0) {
			return 0.0;
		} else {
			return number / (double)total;
		}
	}

	/**
	 * Returns the percentage of cache accesses that found a requested item in the cache (not stale).
	 *
	 * @return the percentage of successful hits
	 */
	public static double getCacheHitPercentage(StatisticListenerImpl statistics) {
		long hits = statistics.getHitCountSum() + statistics.getHitCount();
		long staleHits = statistics.getStaleHitCountSum() + statistics.getStaleHitCount();
		long misses = statistics.getMissCountSum() + statistics.getMissCount();
		long total = hits + staleHits + misses;
		return getPercentage(hits, total);
	}

	/**
	 * Returns the percentage of cache accesses that found a requested item in the cache (stale).
	 *
	 * @return the percentage of successful hits
	 */
	public static double getCacheStaleHitPercentage(StatisticListenerImpl statistics) {
		long hits = statistics.getHitCountSum() + statistics.getHitCount();
		long staleHits = statistics.getStaleHitCountSum() + statistics.getStaleHitCount();
		long misses = statistics.getMissCountSum() + statistics.getMissCount();
		long total = hits + staleHits + misses;
		return getPercentage(staleHits, total);
	}

	/**
	 * Returns the percentage of cache accesses that did not find a requested element in the cache.
	 *
	 * @return the percentage of accesses that failed to find anything
	 */
	public static double getCacheMissPercentage(StatisticListenerImpl statistics) {
		long hits = statistics.getHitCountSum() + statistics.getHitCount();
		long staleHits = statistics.getStaleHitCountSum() + statistics.getStaleHitCount();
		long misses = statistics.getMissCountSum() + statistics.getMissCount();
		long total = hits + staleHits + misses;
		return getPercentage(misses, total);
	}

}
