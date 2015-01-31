package org.dd4t.providers.impl;

import com.tridion.dcp.ComponentPresentation;
import com.tridion.dcp.ComponentPresentationFactory;
import com.tridion.util.TCMURI;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.providers.BaseBrokerProvider;
import org.dd4t.providers.ComponentPresentationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides access to Dynamic Component Presentations stored in the Content Delivery database. It uses CD API to retrieve
 * raw DCP content from the database. Access to these objects is not cached, and as such must be cached externally.
 * TODO: build in caching
 * TODO: we shouldn't throw exceptions if a DCP is not found?
 * TODO: decompress!
 */
public class BrokerComponentPresentationProvider extends BaseBrokerProvider implements ComponentPresentationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BrokerComponentPresentationProvider.class);

	// default Component Template to use when looking up CP
	private int defaultTemplateId;

	// default Output Format to use when looking up CP
	private String defaultOutputFormat;

	private final Map<Integer, ComponentPresentationFactory> factoryCache = new HashMap<>();

	/**
	 * Retrieves content of a Dynamic Component Presentation by looking up its componentId and publicationId.
	 * A templateId is not provided, so the DCP with the highest linking priority is retrieved.
	 *
	 * <b>Note: This method performs significantly slower than getDynamicComponentPresentation(int, int, int)!
	 * Do provide a templateId!</b>
	 *
	 * @param componentId   int representing the Component item id
	 * @param publicationId int representing the Publication id of the DCP
	 * @return String representing the content of the DCP
	 * @throws ItemNotFoundException if the requested DCP cannot be found
	 */
	@Override
	public String getDynamicComponentPresentation(int componentId, int publicationId) throws ItemNotFoundException, SerializationException {
		return getDynamicComponentPresentation(componentId, 0, publicationId);
	}

	/**
	 * Retrieves content of a Dynamic Component Presentation by looking up its componentId, templateId and publicationId.
	 *
	 * @param componentId   int representing the Component item id
	 * @param templateId    int representing the Component Template item id
	 * @param publicationId int representing the Publication id of the DCP
	 * @return String representing the content of the DCP
	 * @throws ItemNotFoundException if the requested DCP cannot be found
	 */
	@Override
	public String getDynamicComponentPresentation(int componentId, int templateId, int publicationId) throws ItemNotFoundException, SerializationException {
		ComponentPresentationFactory factory = factoryCache.get(publicationId);
		if (factory == null) {
			factory = new ComponentPresentationFactory(publicationId);
			factoryCache.put(publicationId, factory);
		}

		ComponentPresentation result;
		String resultString = null;
		if (templateId != 0) {
			result = factory.getComponentPresentation(componentId, templateId);

			if (result == null) {
				String message = String.format("Component Presentation not found for componentId: %d, templateId: %d and publicationId: %d",
						componentId, templateId, publicationId);
				LOG.info(message);
				throw new ItemNotFoundException(message);
			}

			resultString = result.getContent();
		}

		// option 1: use defaultTemplateId
		if (defaultTemplateId != 0) {
			LOG.debug("DefaultComponentTemplate is specified as " + defaultTemplateId);
			result = factory.getComponentPresentation(componentId, defaultTemplateId);

			if (result == null) {
				String message = String.format("Component Presentation not found for componentId: %d, templateId: %d and publicationId: %d",
						componentId, defaultTemplateId, publicationId);
				LOG.info(message);
				throw new ItemNotFoundException(message);
			}

			resultString = result.getContent();
		}

		// option 2: use defaultOutputFormat
		if (defaultOutputFormat != null && defaultOutputFormat.length() != 0) {
			LOG.debug("DefaultOutputFormat is specified as " + defaultOutputFormat);
			result = factory.getComponentPresentationWithOutputFormat(componentId, defaultOutputFormat);

			if (result == null) {
				String message = String.format("Component Presentation not found for componentId: %d, publicationId: %d and outputFormat: %s",
						componentId, publicationId, defaultOutputFormat);
				LOG.info(message);
				throw new ItemNotFoundException(message);
			}

			resultString = result.getContent();
		}

		// option 3: use priority
		LOG.debug("Find Component Presentation with highest priority");
		result = factory.getComponentPresentationWithHighestPriority(componentId);

		if (result == null) {
			String message = String.format("Component Presentation not found for componentId: %d and publicationId: %d",
					componentId, publicationId);
			LOG.info(message);
			throw new ItemNotFoundException(message);
		}

		if (!StringUtils.isEmpty(resultString)){
			return decodeAndDecompressContent(resultString);
		}
		return  null;
	}

	public String getDefaultOutputFormat() {
		return defaultOutputFormat;
	}

	public void setDefaultOutputFormat(String defaultOutputFormat) {
		this.defaultOutputFormat = defaultOutputFormat;
	}

	public int getDefaultComponentTemplate() {
		return defaultTemplateId;
	}

	public void setDefaultComponentTemplate(String templateURI) throws ParseException {
		this.defaultTemplateId = new TCMURI(templateURI).getItemId();
	}

	public void setDefaultComponentTemplate(int templateId) {
		this.defaultTemplateId = templateId;
	}
}