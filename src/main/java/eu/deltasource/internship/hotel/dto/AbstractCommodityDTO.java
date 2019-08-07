package eu.deltasource.internship.hotel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.deltasource.internship.hotel.domain.commodity.Bed;
import eu.deltasource.internship.hotel.domain.commodity.Shower;
import eu.deltasource.internship.hotel.domain.commodity.Toilet;

/**
 * Transfer object for commodities
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
	@JsonSubTypes.Type(value = Bed.class, name = "Bed"),

	@JsonSubTypes.Type(value = Toilet.class, name = "Toilet"),
	@JsonSubTypes.Type(value = Shower.class, name = "Shower")})
public class AbstractCommodityDTO {

	public AbstractCommodityDTO() {
	}
}
