/**
 *
 */
package clime.messadmin.providers.oscache;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;

import clime.messadmin.i18n.I18NSupport;
import clime.messadmin.providers.spi.BaseTabularDataProvider;
import clime.messadmin.utils.StringUtils;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.persistence.PersistenceListener;
import com.opensymphony.oscache.plugins.diskpersistence.AbstractDiskPersistenceListener;

/**
 * Display OSCache statistics
 *
 * @author C&eacute;drik LIME
 */
class SingleOSCacheStatisticsTable extends BaseTabularDataProvider {
	private static final String BUNDLE_NAME = OSCacheStatistics.class.getName();

	/**
	 *
	 */
	public SingleOSCacheStatisticsTable() {
		super();
	}

	/** {@inheritDoc} */
	public StringBuffer getXHTMLApplicationData(StringBuffer buffer, ServletContext context, StatisticListenerImpl statistics) {
		String[] labels = getApplicationTabularDataLabels(context);
		Object[][] values = getApplicationTabularData(context, statistics);
		String tableId = StringUtils.escapeXml("extraApplicationAttributesTable-"+getClass().getName()+'-'+statistics.hashCode()+'-'+statistics.cache.hashCode());
		buildXHTML(buffer, labels, values, tableId, getTableCaption(context, statistics));
		return buffer;
	}

	protected String getTableCaption(ServletContext context, StatisticListenerImpl statistics) {
		//FIXME add ajax links to: clearStatistics, flush/removeAll
		List<String> argsTableCaption = new ArrayList<String>();
		PersistenceListener persistenceListener = statistics.cache.getPersistenceListener();
		if (persistenceListener instanceof AbstractDiskPersistenceListener) {
			argsTableCaption.add(StringUtils.escapeXml(persistenceListener.getClass().getName() + ' ' + ((AbstractDiskPersistenceListener)persistenceListener).getCachePath()));
		} else {
			argsTableCaption.add(persistenceListener == null ? "" : StringUtils.escapeXml(persistenceListener.toString()));
		}
		String caption = I18NSupport.getLocalizedMessage(BUNDLE_NAME, I18NSupport.getAdminLocale(), I18NSupport.getClassLoader(context),
				"caption", argsTableCaption.toArray());//$NON-NLS-1$
		return caption;
	}

	public String[] getApplicationTabularDataLabels(ServletContext context) {
		final ClassLoader cl = I18NSupport.getClassLoader(context);
		return new String[] {
				I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "label.name"),//$NON-NLS-1
				I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "label.value"),//$NON-NLS-1
				I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "label.details")//$NON-NLS-1
		};
	}

	public Object[][] getApplicationTabularData(ServletContext context, StatisticListenerImpl statistics) {
		final ClassLoader cl = I18NSupport.getClassLoader(context);
		NumberFormat numberFormatter = NumberFormat.getNumberInstance(I18NSupport.getAdminLocale());
		NumberFormat percentFormatter = NumberFormat.getPercentInstance(I18NSupport.getAdminLocale());
		Cache cache = statistics.cache;
		List<Object> data = new LinkedList<Object>();

		// Start by adding some of the cache properties

		//data.add(new Object[] {"Guid", ehCache.getGuid(), null});

		data.add(new Object[] {I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "ObjectCount"),//$NON-NLS-1
				numberFormatter.format(cache.getSize()),
				I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "ObjectCount.details",//$NON-NLS-1$
						Long.valueOf(cache.getCapacity())
				)
		});

		data.add(new Object[] {I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "CacheHits"),//$NON-NLS-1
				numberFormatter.format(statistics.getHitCountSum() + statistics.getHitCount()),
				percentFormatter.format(OSCacheHelper.getCacheHitPercentage(statistics))
		});
		data.add(new Object[] {I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "CacheStaleHits"),//$NON-NLS-1
				numberFormatter.format(statistics.getStaleHitCountSum() + statistics.getStaleHitCount()),
				percentFormatter.format(OSCacheHelper.getCacheStaleHitPercentage(statistics))
		});
		data.add(new Object[] {I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "CacheMisses"),//$NON-NLS-1
				numberFormatter.format(statistics.getMissCountSum() + statistics.getMissCount()),
				percentFormatter.format(OSCacheHelper.getCacheMissPercentage(statistics))
		});
		data.add(new Object[] {I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "FlushCount"),//$NON-NLS-1
				numberFormatter.format(statistics.getFlushCount()), null
		});
		data.add(new Object[] {I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "EntriesAdded"),//$NON-NLS-1
				numberFormatter.format(statistics.getEntriesAdded()), null
		});
		data.add(new Object[] {I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "EntriesRemoved"),//$NON-NLS-1
				numberFormatter.format(statistics.getEntriesRemoved()), null
		});
		data.add(new Object[] {I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "EntriesUpdated"),//$NON-NLS-1
				numberFormatter.format(statistics.getEntriesUpdated()), null
		});

		Object[][] result = data.toArray(new Object[data.size()][]);
		return result;
	}
}
