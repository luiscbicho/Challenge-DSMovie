package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {

    @InjectMocks
    private ScoreService service;

    @Mock
    private ScoreRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private MovieRepository movieRepository;

    private Long existingId, nonExistingId;
    private UserEntity user;
    private MovieEntity movie;
    private MovieDTO movieDTO;
    private ScoreEntity score;
    private ScoreDTO scoreDTO;

    @BeforeEach
    public void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        user = UserFactory.createUserEntity();
        movie = MovieFactory.createMovieEntity();
        movieDTO = MovieFactory.createMovieDTO();
        score = ScoreFactory.createScoreEntity();
        scoreDTO = ScoreFactory.createScoreDTO();

        Mockito.when(repository.saveAndFlush(any())).thenReturn(score);

    }

    @Test
    public void saveScoreShouldReturnMovieDTO() {

        Mockito.when(userService.authenticated()).thenReturn(user);
        Mockito.when(movieRepository.findById(scoreDTO.getMovieId())).thenReturn(Optional.of(movie));
        Mockito.when(movieRepository.save(any())).thenReturn(movie);

        MovieDTO result = service.saveScore(scoreDTO);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), movieDTO.getId());

    }

    @Test
    public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {

        Mockito.when(userService.authenticated()).thenReturn(user);
        Mockito.when(movieRepository.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.saveScore(scoreDTO));


    }
}
