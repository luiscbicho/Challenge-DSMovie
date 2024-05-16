package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {

    @InjectMocks
    private MovieService service;

    @Mock
    private MovieRepository repository;

    private Long existingMovieId, nonExistingMovieId, dependentMovieId;
    private String title;
    private MovieEntity movie;
    private MovieDTO movieDTO;
    private List<MovieEntity> movies;
    private PageImpl<MovieEntity> page;

    @BeforeEach
    public void setUp() throws Exception {
        existingMovieId = 1L;
        nonExistingMovieId = 2L;
        dependentMovieId = 3L;
        title = "Test Movie";
        movie = MovieFactory.createMovieEntity();
        movieDTO = MovieFactory.createMovieDTO();
        page = new PageImpl<>(List.of(movie));

        Mockito.when(repository.searchByTitle(any(), (Pageable) any())).thenReturn(page);
        Mockito.when(repository.findById(existingMovieId)).thenReturn(Optional.of(movie));
        Mockito.when(repository.findById(nonExistingMovieId)).thenThrow(ResourceNotFoundException.class);
        Mockito.when(repository.save(any())).thenReturn(movie);
        Mockito.when(repository.getReferenceById(existingMovieId)).thenReturn(movie);
        Mockito.when(repository.getReferenceById(nonExistingMovieId)).thenThrow(ResourceNotFoundException.class);
        Mockito.when(repository.existsById(existingMovieId)).thenReturn(true);
        Mockito.when(repository.existsById(dependentMovieId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingMovieId)).thenReturn(false);
        Mockito.doNothing().when(repository).deleteById(existingMovieId);
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).deleteById(nonExistingMovieId);
        Mockito.doThrow(DatabaseException.class).when(repository).deleteById(dependentMovieId);
    }

    @Test
    public void findAllShouldReturnPagedMovieDTO() {

        Pageable pageable = PageRequest.of(0, 12);
        Page<MovieDTO> movieDTOPage = service.findAll(title, pageable);

        Assertions.assertNotNull(movieDTOPage);
        Assertions.assertEquals(movieDTOPage.getSize(), 1);
        Assertions.assertEquals(movieDTOPage.iterator().next().getTitle(), title);

    }

    @Test
    public void findByIdShouldReturnMovieDTOWhenIdExists() {

        MovieDTO result = service.findById(existingMovieId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getTitle(), title);
        Assertions.assertEquals(result.getId(), existingMovieId);


    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingMovieId));

    }

    @Test
    public void insertShouldReturnMovieDTO() {

        MovieDTO result = service.insert(movieDTO);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getTitle(), title);
        Assertions.assertEquals(result.getId(), existingMovieId);

    }

    @Test
    public void updateShouldReturnMovieDTOWhenIdExists() {

        MovieDTO result = service.update(existingMovieId, movieDTO);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getTitle(), title);
        Assertions.assertEquals(result.getId(), existingMovieId);

    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingMovieId, movieDTO));
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> service.delete(existingMovieId));
        verify(repository, Mockito.times(1)).deleteById(existingMovieId);

    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingMovieId));
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependentMovieId));
    }
}
