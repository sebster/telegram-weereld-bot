package com.sebster.weereld.hobbes.people;

import static com.sebster.commons.functions.Functions.unwrapOptional;
import static com.sebster.repository.api.properties.Property.property;

import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.sebster.repository.api.properties.Property;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "nick1", "nick2" }))
@IdClass(PartnerId.class)
@Getter
@EqualsAndHashCode(of = { "nick1", "nick2" })
@ToString
public class Partner {

	@Id
	private String nick1;

	@Id
	private String nick2;

	@Column
	private LocalDate date;

	public Optional<LocalDate> getDate() {
		return Optional.ofNullable(date);
	}

	public static final Property<Partner, LocalDate> DATE =
			property(Partner.class, "date", unwrapOptional(Partner::getDate));

}
