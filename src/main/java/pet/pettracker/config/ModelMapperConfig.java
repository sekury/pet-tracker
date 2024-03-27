package pet.pettracker.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pet.pettracker.model.dto.CatTrackerDto;
import pet.pettracker.model.dto.DogTrackerDto;
import pet.pettracker.model.entity.Cat;
import pet.pettracker.model.entity.Dog;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        var mapper = new ModelMapper();

        mapper.getConfiguration()
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        // prevent entity id changes
        mapper.typeMap(CatTrackerDto.class, Cat.class)
                .addMappings(mapping -> mapping.skip(Cat::setId));
        mapper.typeMap(DogTrackerDto.class, Dog.class)
                .addMappings(mapping -> mapping.skip(Dog::setId));

        return mapper;
    }
}
