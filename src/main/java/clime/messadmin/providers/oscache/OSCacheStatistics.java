/**
 *
 */
package clime.messadmin.providers.oscache;

import java.util.Iterator;

import javax.servlet.ServletContext;

import clime.messadmin.i18n.I18NSupport;
import clime.messadmin.model.Server;
import clime.messadmin.providers.spi.ApplicationDataProvider;
import clime.messadmin.utils.Integers;
import clime.messadmin.utils.StringUtils;

/**
 * Display OSCache statistics
 * Only display statistics for caches that have a configured {@code SimpleStatisticListenerImpl} in {@code oscache.properties}'s {@code cache.event.listeners}.
 *
 * @author C&eacute;drik LIME
 */
public class OSCacheStatistics implements ApplicationDataProvider {
	private static final String BUNDLE_NAME = OSCacheStatistics.class.getName();
	private static final SingleOSCacheStatisticsTable tableBuilder = new SingleOSCacheStatisticsTable();

	/**
	 *
	 */
	public OSCacheStatistics() {
		super();
	}

	/** {@inheritDoc} */
	public String getApplicationDataTitle(ServletContext context) {
		final ClassLoader cl = Server.getInstance().getApplication(context).getApplicationInfo().getClassLoader();
		int nCaches = StatisticListenerImpl.ALL_STATISTIC_LISTENERS.size();
		return I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "title", new Object[] {//$NON-NLS-1$
				Integers.valueOf(nCaches)
		});
	}

	/** {@inheritDoc} */
	public int getPriority() {
		return 0;
	}

	/** {@inheritDoc} */
	public String getXHTMLApplicationData(ServletContext context) {
		final ClassLoader cl = Server.getInstance().getApplication(context).getApplicationInfo().getClassLoader();
		StringBuffer result = new StringBuffer(512);
		synchronized (StatisticListenerImpl.ALL_STATISTIC_LISTENERS) {
			Iterator allStatsIter = StatisticListenerImpl.ALL_STATISTIC_LISTENERS.keySet().iterator();
			while (allStatsIter.hasNext()) {
				StatisticListenerImpl statistics = (StatisticListenerImpl) allStatsIter.next();
				if (result.length() > 0) {// 2nd+ Cache
					result.append("\n<hr/>\n");
				}
				result.append("<h3>");
				result.append(StringUtils.escapeXml(statistics.cache.toString()));
				result.append("</h3>\n");
				// Display the current statistics
				tableBuilder.getXHTMLApplicationData(result, context, statistics);
			}
		}
		return result.toString();
	}

}
