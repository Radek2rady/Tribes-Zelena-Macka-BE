package com.greenfoxacademy.zelenamackatribes.resources.unit;

import static org.mockito.ArgumentMatchers.any;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceGenerationChange;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceType;
import com.greenfoxacademy.zelenamackatribes.resources.repositories.ResourceGenerationChangeRepository;
import com.greenfoxacademy.zelenamackatribes.resources.repositories.ResourceRepository;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceServiceImpl;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ResourceServiceTest {

  private ResourceRepository resourceRepository;
  private ResourceGenerationChangeRepository resourceGenerationChangeRepository;
  private ResourceService resourceService;
  private Kingdom fakeKingdom;
  private List<Resource> fakeResources;
  private TimeService timeService;

  @BeforeEach
  public void setup() {
    this.resourceRepository = Mockito.mock(ResourceRepository.class);
    this.resourceGenerationChangeRepository = Mockito.mock(
        ResourceGenerationChangeRepository.class);
    this.timeService = Mockito.mock(TimeService.class);
    this.resourceService = new ResourceServiceImpl(resourceRepository,
        resourceGenerationChangeRepository, timeService);
    fakeResources = Arrays.asList(
        new Resource(1L, ResourceType.GOLD, 50, fakeKingdom, 50, 1L),
        new Resource(2L, ResourceType.FOOD, 40, fakeKingdom, 30, 2L)
    );
    fakeKingdom = new Kingdom(1L, "fakeKingdom", null, new ArrayList<>(),
        fakeResources, new ArrayList<>(), null);
  }

  @Test
  public void getResourcesOk()
      throws InvalidNumberOfResourceObjectsException, ResourceNotFoundException {
    Mockito.when(resourceRepository.findResourcesByKingdom(any())).thenReturn(fakeResources);
    List<Resource> resources = resourceService.getResources(fakeKingdom);
    Assertions.assertEquals(2, resources.size());
  }

  @Test
  public void getResourcesThrowsWithInvalidNumberOfResourceObjects() {
    Mockito.when(resourceRepository.findResourcesByKingdom(any()))
        .thenReturn(Collections.emptyList());

    var exception0 = Assertions.assertThrows(
        InvalidNumberOfResourceObjectsException.class,
        () -> {
          resourceService.getResources(fakeKingdom);
        });
    Assertions.assertEquals("Unexpected number of resources found (wants=2, found=0)",
        exception0.getMessage());

    Mockito.when(resourceRepository.findResourcesByKingdom(any())).thenReturn(
        Collections.singletonList(new Resource(1L, ResourceType.GOLD, 50, fakeKingdom, 50, 1L)));

    var exception1 = Assertions.assertThrows(
        InvalidNumberOfResourceObjectsException.class,
        () -> {
          resourceService.getResources(fakeKingdom);
        });
    Assertions.assertEquals("Unexpected number of resources found (wants=2, found=1)",
        exception1.getMessage());

    Mockito.when(resourceRepository.findResourcesByKingdom(any())).thenReturn(Arrays.asList(
        new Resource(1L, ResourceType.GOLD, 50, fakeKingdom, 50, 1L),
        new Resource(1L, ResourceType.GOLD, 50, fakeKingdom, 50, 1L),
        new Resource(1L, ResourceType.GOLD, 50, fakeKingdom, 50, 1L)));
    var exception3 = Assertions.assertThrows(
        InvalidNumberOfResourceObjectsException.class,
        () -> {
          resourceService.getResources(fakeKingdom);
        });
    Assertions.assertEquals("Unexpected number of resources found (wants=2, found=3)",
        exception3.getMessage());
  }

  @Test
  public void getResourceOk() throws Exception {
    Mockito.when(resourceRepository.findResourcesByKingdom(any())).thenReturn(fakeResources);
    var gold = resourceService.getResource(fakeKingdom, ResourceType.GOLD);
    var food = resourceService.getResource(fakeKingdom, ResourceType.FOOD);
    Assertions.assertEquals(ResourceType.GOLD, gold.getType());
    Assertions.assertEquals(ResourceType.FOOD, food.getType());
    Assertions.assertEquals(50, gold.getAmount());
    Assertions.assertEquals(40, food.getAmount());
  }

  @Test
  public void handlePurchaseShouldReturnNotEnoughResourcesException() {
    Mockito.when(timeService.getTime()).thenReturn(1111111L);
    Mockito.when(resourceRepository.findResourcesByKingdom(any())).thenReturn(fakeResources);

    NotEnoughResourcesException exception1 = Assertions
        .assertThrows(NotEnoughResourcesException.class,
            () -> {
              resourceService.handlePurchase(fakeKingdom, 80, 90);
            });
    Assertions.assertEquals("Don't have enough gold and food", exception1.getMessage());

    NotEnoughResourcesException exception2 = Assertions
        .assertThrows(NotEnoughResourcesException.class,
            () -> {
              resourceService.handlePurchase(fakeKingdom, 10, 90);
            });
    Assertions.assertEquals("Don't have enough food", exception2.getMessage());

    NotEnoughResourcesException exception3 = Assertions
        .assertThrows(NotEnoughResourcesException.class,
            () -> {
              resourceService.handlePurchase(fakeKingdom, 77, 10);
            });
    Assertions.assertEquals("Don't have enough gold", exception3.getMessage());

    InputMismatchException exception4 = Assertions
        .assertThrows(InputMismatchException.class,
            () -> {
              resourceService.handlePurchase(fakeKingdom, -77, 10);
            });
    Assertions.assertEquals("Amount cannot be negative", exception4.getMessage());
  }

  @Test
  public void handlePurchaseShouldReturnResourceNotFoundException() {
    Mockito.when(timeService.getTime()).thenReturn(1111111L);
    Mockito.when(resourceRepository.findResourcesByKingdom(fakeKingdom)).thenReturn(Arrays.asList(
        new Resource(1L, ResourceType.GOLD, 50, fakeKingdom, 50, 1L),
        new Resource(2L, ResourceType.GOLD, 50, fakeKingdom, 50, 1L)));

    Kingdom fakeKingdom2 = new Kingdom(2L, null, null, null, null, null, null);
    Mockito.when(resourceRepository.findResourcesByKingdom(fakeKingdom2)).thenReturn(Arrays.asList(
        new Resource(1L, ResourceType.FOOD, 50, fakeKingdom, 50, 1L),
        new Resource(2L, ResourceType.FOOD, 50, fakeKingdom, 50, 1L)));

    ResourceNotFoundException exception1 = Assertions
        .assertThrows(ResourceNotFoundException.class,
            () -> {
              resourceService.handlePurchase(fakeKingdom, 10, 30);
            });
    Assertions.assertEquals("Food not found", exception1.getMessage());

    ResourceNotFoundException exception2 = Assertions
        .assertThrows(ResourceNotFoundException.class,
            () -> {
              resourceService.handlePurchase(fakeKingdom2, 10, 30);
            });
    Assertions.assertEquals("Gold not found", exception2.getMessage());
  }

  @Test
  public void resourceIncomeChangeIsApplied() throws Exception {
    var food = fakeResources.stream()
        .filter(r -> r.getType().equals(ResourceType.FOOD)).findFirst().get();
    Mockito.when(resourceRepository.findResourcesByKingdom(any()))
        .thenReturn(fakeResources);
    Mockito.when(resourceGenerationChangeRepository.findAllByResourceInAndChangeAtIsLessThanEqual(
        any(), any())).thenReturn(Arrays.asList(
          new ResourceGenerationChange(1L, ResourceType.FOOD, 10, food, 1L),
          new ResourceGenerationChange(1L, ResourceType.FOOD, -5, food, 1L)));
    Assertions.assertEquals(35,
        resourceService.getResource(fakeKingdom, ResourceType.FOOD).getGeneration());
  }

  @Test
  public void resourceIncomeApplied() throws Exception {
    final var food = fakeResources.stream()
        .filter(r -> r.getType().equals(ResourceType.FOOD)).findFirst().get();
    Mockito.when(resourceRepository.findAll()).thenReturn(fakeResources);
    Mockito.when(
        resourceGenerationChangeRepository.findAllByResourceInAndChangeAtIsLessThanEqual(any(),
            any())).thenReturn(Collections.emptyList());
    resourceService.applyResourceGeneration();
    Assertions.assertEquals(70, food.getAmount());
  }
}
