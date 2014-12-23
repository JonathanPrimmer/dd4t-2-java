package org.dd4t.providers.rs;

import org.dd4t.core.factories.impl.PropertiesServiceFactory;
import org.dd4t.core.services.PropertiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Singleton instance of the JAX RS client communicating with Tridion Content services. The client defines the root entry
 * WebTargets for each service method. Use the corresponding getter to set the needed parameters and
 * invoke the service.
 * <p/>
 * The URL location is read from a classpath resource file named <b>tridionservice.properties</b>. Example:
 * <code>tridionservice.url=http://my.server.com/tridioncontent/</code>
 * <p/>
 * <p/>
 * Currently, Jersey JAX-RS is used, but other implementations like Resteasy
 * can also be easily switched to. To do this, just change the POM and
 * ensure the correct amount of dependencies are loaded from either the
 * J2EE container or in this POM.
 *
 * @author Mihai Cadariu
 */
public enum JAXRSClient {

	INSTANCE;

	// TODO: change tridionservice.url
	private static final String SERVICE_URL = "tridionservice.url";
	private static final String BINARY_WRAPPER_BY_ID = "providers/binary/getwrapperbyid";
	private static final String BINARY_WRAPPER_BY_URL = "providers/binary/getwrapperbyurl";
	private static final String COMPONENT_BY_ID = "providers/component/getcomponentpresentationbyid";
	private static final String DISCOVER_BY_PUBLICATION_URL = "providers/page/discoverpublicationid";
	private static final String DISCOVER_BY_IMAGES_URL = "providers/binary/discoverpublicationid";
	private static final String PAGE_CHECK_EXISTS = "providers/page/checkpageexists";
	private static final String PAGE_CONTENT_BY_ID = "providers/page/getcontentbyid";
	private static final String PAGE_CONTENT_BY_URL = "providers/page/getcontentbyurl";
	private static final String PAGE_LIST_BY_PUBLICATION = "providers/page/getlistbypublication";
	private static final String RESOLVE_COMPONENT = "providers/link/resolvecomponentbyuri";
	private static final String RESOLVE_COMPONENT_FROM_PAGE = "providers/link/resolvecomponentfrompagebyuri";
	private static final String TAXONOMY_BY_SCHEMA = "providers/taxonomy/gettaxonomybyschema";
	private static final String TAXONOMY_BY_URI = "providers/taxonomy/gettaxonomy";
	private static final String QUERY_COMPONENT_BY_CUSTOMMETA = "providers/query/getcomponentsbycustommeta";
	private static final String QUERY_COMPONENT_BY_SCHEMA = "providers/query/getcomponentsbyschema";
	private static final String QUERY_COMPONENT_BY_SCHEMA_IN_KEYWORD = "providers/query/getcomponentsbyschemainkeyword";

	private final Logger LOG = LoggerFactory.getLogger(JAXRSClient.class);

	private final WebTarget binaryWrapperByIdTarget;
	private final WebTarget binaryWrapperByURLTarget;
	private final WebTarget componentByIdTarget;
	private final WebTarget discoverPublicationByPublicationURLTarget;
	private final WebTarget discoverPublicationByImagesURLTarget;
	private final WebTarget pageCheckExists;
	private final WebTarget pageContentByIdTarget;
	private final WebTarget pageContentByURLTarget;
	private final WebTarget pageListByPublicationTarget;
	private final WebTarget resolveComponentFromPageTarget;
	private final WebTarget resolveComponentTarget;
	private final WebTarget taxonomyBySchemaTarget;
	private final WebTarget taxonomyByURITarget;
	private final WebTarget queryComponentByCustomMeta;
	private final WebTarget queryComponentBySchema;
	private final WebTarget queryComponentsBySchemaInKeyword;


	/**
	 * Private constructor reads the Service URL location from properties file, initializes the JAX RS client, and
	 * creates the WebTargets for each service method.
	 */
	private JAXRSClient () {
		PropertiesServiceFactory factory = PropertiesServiceFactory.getInstance();
		PropertiesService service = factory.getPropertiesService();

		String serviceUrlProperty = service.getProperty(SERVICE_URL);
		if (!serviceUrlProperty.endsWith("/")) {
			serviceUrlProperty += "/";
		}
		LOG.debug("Using service.url=" + serviceUrlProperty);

		final Client client = ClientBuilder.newClient();
		WebTarget baseTarget = client.target(serviceUrlProperty);

		binaryWrapperByIdTarget = baseTarget.path(BINARY_WRAPPER_BY_ID);
		binaryWrapperByURLTarget = baseTarget.path(BINARY_WRAPPER_BY_URL);
		pageCheckExists = baseTarget.path(PAGE_CHECK_EXISTS);
		pageContentByIdTarget = baseTarget.path(PAGE_CONTENT_BY_ID);
		pageContentByURLTarget = baseTarget.path(PAGE_CONTENT_BY_URL);
		pageListByPublicationTarget = baseTarget.path(PAGE_LIST_BY_PUBLICATION);
		resolveComponentTarget = baseTarget.path(RESOLVE_COMPONENT);
		resolveComponentFromPageTarget = baseTarget.path(RESOLVE_COMPONENT_FROM_PAGE);
		taxonomyByURITarget = baseTarget.path(TAXONOMY_BY_URI);
		taxonomyBySchemaTarget = baseTarget.path(TAXONOMY_BY_SCHEMA);
		componentByIdTarget = baseTarget.path(COMPONENT_BY_ID);
		discoverPublicationByPublicationURLTarget = baseTarget.path(DISCOVER_BY_PUBLICATION_URL);
		discoverPublicationByImagesURLTarget = baseTarget.path(DISCOVER_BY_IMAGES_URL);
		queryComponentByCustomMeta = baseTarget.path(QUERY_COMPONENT_BY_CUSTOMMETA);
		queryComponentBySchema = baseTarget.path(QUERY_COMPONENT_BY_SCHEMA);
		queryComponentsBySchemaInKeyword = baseTarget.path(QUERY_COMPONENT_BY_SCHEMA_IN_KEYWORD);

	}

	/**
	 * Returns the jaxrs client for querying components by Custom Metadata Based on Schema in a keyword
	 *
	 * @return WebTarget
	 */
	public WebTarget getQueryComponentsBySchemaInKeyword () {
		return queryComponentsBySchemaInKeyword;
	}


	/**
	 * Returns the jaxrs client for querying components by Custom Metadata
	 *
	 * @return WebTarget
	 */
	public WebTarget getQueryComponentByCustomMetaRequest () {
		return queryComponentByCustomMeta;
	}

	/**
	 * Returns the jaxrs client for querying components by Schema
	 *
	 * @return WebTarget
	 */
	public WebTarget getQueryComponentBySchema () {
		return queryComponentBySchema;
	}


	/**
	 * Returns the WebTarget for reading BinaryWrapper by TCMURI
	 *
	 * @return WebTarget
	 */
	public WebTarget getBinaryWrapperByIdTarget () {
		return binaryWrapperByIdTarget;
	}

	/**
	 * Returns the WebTarget for reading BinaryWrapper by URL
	 *
	 * @return WebTarget
	 */
	public WebTarget getBinaryWrapperByURLTarget () {
		return binaryWrapperByURLTarget;
	}

	/**
	 * Returns the WebTarget for reading Page model by TCMURI
	 *
	 * @return WebTarget
	 */
	public WebTarget getPageContentByIdTarget () {
		return pageContentByIdTarget;
	}

	/**
	 * Returns the WebTarget for reading Page model by URL
	 *
	 * @return WebTarget
	 */
	public WebTarget getPageContentByURLTarget () {
		return pageContentByURLTarget;
	}

	/**
	 * Returns the WebTarget for reading Page URL list by Publication
	 *
	 * @return WebTarget
	 */
	public WebTarget getPageListByPublicationTarget () {
		return pageListByPublicationTarget;
	}

	/**
	 * Returns the WebTarget for checking a Page exists by URL
	 *
	 * @return WebTarget
	 */
	public WebTarget getPageCheckExists () {
		return pageCheckExists;
	}

	/**
	 * Returns the WebTarget for resolving stand-alone Component links
	 *
	 * @return WebTarget
	 */
	public WebTarget getResolveComponentTarget () {
		return resolveComponentTarget;
	}

	/**
	 * Returns the WebTarget for resolving Component links appearing on a Page
	 *
	 * @return WebTarget
	 */
	public WebTarget getResolveComponentFromPageTarget () {
		return resolveComponentFromPageTarget;
	}

	/**
	 * Returns the WebTarget for retrieving Taxonomies
	 *
	 * @return WebTarget
	 */
	public WebTarget getTaxonomyByURITarget () {
		return taxonomyByURITarget;
	}

	/**
	 * Returns the WebTarget for retrieving Taxonomies and filter related items by Schema
	 *
	 * @return WebTarget
	 */
	public WebTarget getTaxonomyBySchemaTarget () {
		return taxonomyBySchemaTarget;
	}

	/**
	 * Returns the WebTarget for retrieving Dynamic Component Presentations
	 *
	 * @return WebTarget
	 */
	public WebTarget getComponentByIdTarget () {
		return componentByIdTarget;
	}

	/**
	 * Returns the WebTarget for identifying Publication ids by Publication URL
	 *
	 * @return WebTarget
	 */
	public WebTarget getDiscoverPublicationByImagesURLTarget () {
		return discoverPublicationByImagesURLTarget;
	}

	/**
	 * Returns the WebTarget for identifying Publication ids by Publication URL
	 *
	 * @return WebTarget
	 */
	public WebTarget getDiscoverPublicationByPublicationURLTarget () {
		return discoverPublicationByPublicationURLTarget;
	}
}
