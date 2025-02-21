package dev.nokee.commons.gradle.provider;

import org.gradle.api.Transformer;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.file.FileSystemLocationProperty;
import org.gradle.api.provider.Provider;

final class LocationOnlyTransformer<LocationType extends FileSystemLocation, InputType> implements Transformer<Provider<LocationType>, InputType> {
	private final Transformer<FileSystemLocationProperty<LocationType>, InputType> mapper;

	public LocationOnlyTransformer(Transformer<FileSystemLocationProperty<LocationType>, InputType> mapper) {
		this.mapper = mapper;
	}

	@Override
	public Provider<LocationType> transform(InputType t) {
		return mapper.transform(t).getLocationOnly();
	}
}
