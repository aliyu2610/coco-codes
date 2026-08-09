package com.fooddelivery.restaurantservice.service;

import com.fooddelivery.restaurantservice.domain.Restaurant;
import com.fooddelivery.restaurantservice.repository.RestaurantRepository;
import com.fooddelivery.restaurantservice.web.CreateRestaurantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RestaurantServiceTest {

    private RestaurantRepository repository;
    private RestaurantService    service;

    @BeforeEach
    void setUp() {
        repository = mock(RestaurantRepository.class);
        service    = new RestaurantService(repository);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void create_persistsAndReturnsResponse() {
        var response = service.create(new CreateRestaurantRequest("Burger Place", 20));
        verify(repository).save(any(Restaurant.class));
        assertThat(response.name()).isEqualTo("Burger Place");
        assertThat(response.avgPrepTimeMinutes()).isEqualTo(20);
        assertThat(response.isOpen()).isTrue();
    }

    @Test
    void get_returnsRestaurantWhenFound() {
        var restaurant = new Restaurant("r1", "Pizza Co", 18);
        when(repository.findById("r1")).thenReturn(Optional.of(restaurant));

        var response = service.get("r1");

        verify(repository).findById("r1");
        assertThat(response.name()).isEqualTo("Pizza Co");
    }

    @Test
    void get_throwsWhenRestaurantNotFound() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get("missing"))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
    }
}
