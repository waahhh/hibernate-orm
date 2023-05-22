/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.model.internal;

import java.util.Locale;
import java.util.Map;

import org.hibernate.AnnotationException;
import org.hibernate.MappingException;
import org.hibernate.annotations.Fetch;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.SecondPass;
import org.hibernate.mapping.FetchProfile;
import org.hibernate.mapping.PersistentClass;

import static org.hibernate.internal.util.StringHelper.qualify;
import static org.hibernate.mapping.MetadataSource.ANNOTATIONS;

/**
 * @author Gavin King
 */
public class FetchSecondPass implements SecondPass {
	private final Fetch fetch;
	private final PropertyHolder propertyHolder;
	private final String propertyName;
	private final MetadataBuildingContext buildingContext;

	public FetchSecondPass(Fetch fetch, PropertyHolder propertyHolder, String propertyName, MetadataBuildingContext buildingContext) {
		this.fetch = fetch;
		this.propertyHolder = propertyHolder;
		this.propertyName = propertyName;
		this.buildingContext = buildingContext;
	}

	@Override
	public void doSecondPass(Map<String, PersistentClass> persistentClasses) throws MappingException {

		//TODO: handle propertyHolder.getPath() !!!!

		// throws MappingException in case the property does not exist
		buildingContext.getMetadataCollector()
				.getEntityBinding( propertyHolder.getEntityName() )
				.getProperty( propertyName );

		FetchProfile profile = buildingContext.getMetadataCollector().getFetchProfile( fetch.profile() );
		if ( profile == null ) {
			throw new AnnotationException( "Property '" + qualify( propertyHolder.getPath(), propertyName )
					+ "' refers to an unknown fetch profile named '" + fetch.profile() + "'" );
		}
		else if ( profile.getSource() == ANNOTATIONS ) {
			profile.addFetch(
					propertyHolder.getEntityName(),
					propertyName,
					fetch.value().toString().toLowerCase(Locale.ROOT)
			);
		}
		// otherwise, it's a fetch profile defined in XML, and it overrides
		// the annotations, so we simply ignore this annotation completely
	}
}
