package com.sis.cine.mapper;

import com.sis.cine.dto.PeliculaDTO;
import com.sis.cine.model.Pelicula;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PeliculaMapper {
    PeliculaMapper INSTANCE = Mappers.getMapper(PeliculaMapper.class);

    PeliculaDTO peliculaToPeliculaDTO(Pelicula pelicula);
    List<PeliculaDTO> peliculasToPeliculaDTOs(List<Pelicula> peliculas);
}