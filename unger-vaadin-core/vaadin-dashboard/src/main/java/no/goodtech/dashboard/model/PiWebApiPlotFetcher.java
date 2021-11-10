package no.goodtech.dashboard.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.goodtech.vaadin.utils.DateUtil;
import no.goodtech.vaadin.utils.PiWebApiUtils;
import no.goodtech.vaadin.utils.SSLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import javax.management.timer.Timer;
import java.io.IOException;
import java.util.*;

/**
 * Uses PI WEB API to fetch plot data from PI or Rockwell Historian.
 * Uses HTTPS and JSON.
 */
public class PiWebApiPlotFetcher extends AbstractFetcher {

	private final String baseUrl, tagWebId;
	private final double intervalsPerSecond;
	private final RestTemplate template;
	private static final Logger LOGGER = LoggerFactory.getLogger(PiWebApiPlotFetcher.class);

	/**
	 * Create fetcher
	 * @param id ID if the fetcher. You refer to this when configuring the charts
	 * @param fullFetch TODO: ?
	 * @param cacheRetentionInterval max age of cache in seconds
	 * @param baseUrl URL for the web api, e.g. https://172.19.11.123/piwebapi/
	 * @param tagWebId unique ID for the tag you like to plot
	 * @param intervals how many points will be retrieved for each query. If null, 100 will be used
	 * @see "https://techsupport.osisoft.com/Documentation/PI-Web-API/help/controllers/dataserver/actions/getpoints.html"
	 */
	public PiWebApiPlotFetcher(String id, boolean fullFetch, long cacheRetentionInterval, String baseUrl, String tagWebId, Integer intervals) {
		super(id, fullFetch, cacheRetentionInterval);
		
		if (baseUrl != null && !baseUrl.endsWith("/"))
			this.baseUrl = baseUrl + "/";
		else
			this.baseUrl = baseUrl;
		
		this.tagWebId = tagWebId;
		
		intervalsPerSecond = intervals.doubleValue() / Long.valueOf(cacheRetentionInterval).doubleValue();

		template = new RestTemplate();
		
		SSLUtil.turnOffSslChecking();
		SSLUtil.turnOffHostNameChecking(template);
	}
	
	public List<SampleDTO> fetchNewSamplePoints(Date startTime) {
		List<SampleDTO> values = new ArrayList<SampleDTO>();
		Map<String, String> urlVariables = new HashMap<String, String>();
		urlVariables.put("tagWebId", tagWebId);
		urlVariables.put("startTime", PiWebApiUtils.PI_QUERY_DATE_FORMAT.format(DateUtil.toGmt(startTime)));
		urlVariables.put("intervals", String.valueOf(computeIntervals(startTime)));
		String url = this.baseUrl + "streams/{tagWebId}/plot?startTime={startTime}&intervals={intervals}";
		final String result = template.getForObject(url, String.class, urlVariables);
		if (LOGGER.isDebugEnabled()) { 
			final String expandedUrl = new UriTemplate(url).expand(urlVariables).toString();
			LOGGER.debug("Call to {}, result: {}", expandedUrl, result);
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode resultNode = mapper.readTree(result);
			final JsonNode itemsNode = resultNode.get("Items");
			final Iterator<JsonNode> elements = itemsNode.elements();
			while (elements.hasNext()) {
				JsonNode itemNode = elements.next();
				final SampleDTO value = createValue(itemNode);
				if (value != null)
					values.add(value);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new ArrayList<>(); //TODO
	}
	
	int computeIntervals(Date startTime) {
		long start = startTime.getTime();
		long end = new Date().getTime();
		long periodInSeconds = (end - start) / Timer.ONE_SECOND;
		final Double intervalsForPeriod = new Double(periodInSeconds * intervalsPerSecond);
		if (intervalsForPeriod < 1)
			return 1;
		if (intervalsForPeriod > 30000)
			return 30000;
		return intervalsForPeriod.intValue();
	}

	private SampleDTO createValue(JsonNode itemNode) {
		final JsonNode good = itemNode.get("Good");
		final JsonNode value = itemNode.get("Value");
		final JsonNode timestamp = itemNode.get("Timestamp");
		if (good.booleanValue()) {
			Date date = PiWebApiUtils.parseDate(timestamp.textValue());
			if (date != null && value != null) {
				SampleDTO historicalValue = new SampleDTO(DateUtil.toLocal(date), value.doubleValue()); 
				return historicalValue;			
			}			
		} else {
			LOGGER.debug("Ignored not good value at timestamp '{}'", timestamp);
		}
		return null;
	}

	double getIntervalsPerSecond() {
		return intervalsPerSecond;
	}
	
	

}
