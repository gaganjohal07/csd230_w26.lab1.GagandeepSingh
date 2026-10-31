
package csd230.lab1.controllers;

import csd230.lab1.entities.DiscMagEntity;
import csd230.lab1.exceptions.ResourceNotFoundException;
import csd230.lab1.repositories.DiscMagEntityRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rest/discmags")
@Tag(name = "DiscMags", description = "REST API for managing disc magazines")
public class DiscMagRestController {

    private final DiscMagEntityRepository discMagRepository;

    public DiscMagRestController(DiscMagEntityRepository discMagRepository) {
        this.discMagRepository = discMagRepository;
    }

    @GetMapping
    @Operation(summary = "Get all disc magazines")
    @ApiResponse(responseCode = "200", description = "Disc magazines retrieved successfully")
    public List<DiscMagEntity> getAllDiscMags() {
        return discMagRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get disc magazine by ID")
    @ApiResponse(responseCode = "200", description = "Disc magazine found")
    @ApiResponse(responseCode = "404", description = "Disc magazine not found")
    public DiscMagEntity getDiscMagById(@PathVariable Long id) {

        return discMagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Disc magazine not found with ID: " + id));
    }

    @PostMapping
    @Operation(summary = "Create a new disc magazine")
    @ApiResponse(responseCode = "201", description = "Disc magazine created successfully")
    public ResponseEntity<DiscMagEntity> createDiscMag(
            @RequestBody DiscMagEntity discMag) {

        discMag.setId(null);
        DiscMagEntity savedDiscMag = discMagRepository.save(discMag);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedDiscMag);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing disc magazine")
    @ApiResponse(responseCode = "200", description = "Disc magazine updated successfully")
    @ApiResponse(responseCode = "404", description = "Disc magazine not found")
    public DiscMagEntity updateDiscMag(
            @PathVariable Long id,
            @RequestBody DiscMagEntity updatedDiscMag) {

        DiscMagEntity discMag = discMagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Disc magazine not found with ID: " + id));

        discMag.setTitle(updatedDiscMag.getTitle());
        discMag.setPrice(updatedDiscMag.getPrice());
        discMag.setCopies(updatedDiscMag.getCopies());
        discMag.setOrderQty(updatedDiscMag.getOrderQty());
        discMag.setCurrentIssue(updatedDiscMag.getCurrentIssue());
        discMag.setHasDisc(updatedDiscMag.getHasDisc());

        return discMagRepository.save(discMag);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a disc magazine")
    @ApiResponse(responseCode = "204", description = "Disc magazine deleted successfully")
    @ApiResponse(responseCode = "404", description = "Disc magazine not found")
    public ResponseEntity<Void> deleteDiscMag(@PathVariable Long id) {

        DiscMagEntity discMag = discMagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Disc magazine not found with ID: " + id));

        discMagRepository.delete(discMag);

        return ResponseEntity.noContent().build();
    }
}
